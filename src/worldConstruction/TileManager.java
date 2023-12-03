package worldConstruction;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import drawable.CBPlanet;
import drawable.CBStar;
import drawable.Rasterable;
import drawable.DrawablePath;
import drawable.DPTileBoundPolygon;
import drawable.DPSelectionCursor;
import drawable.Pnt2D;

public class TileManager {

  public double zFactor;
  public Pnt2D focus;

  class ManagerThread extends Thread{
    @Override
    public void run(){
      updateCycle();
    }
  }

  private long seed;
  private int initTileSize; //1,000,000
  private double initNoiseSize=250000; // 250.000
  private int initPointsAmt;
  private int mSD;
  private double actTileSize;
  private int xRes;
  private int yRes;
  private int g;
  private Boolean updateFlag=false;
  private Boolean selectionFlag=false;

  private Rectangle2D.Double sel;

  private Lock defaultLock;
  private Lock selectionLock;

  private ManagerThread mThread;

  private LinkedList<Tile> activeTiles;
  private LinkedList<DPTileBoundPolygon> tilePolygons;
  private LinkedList<Rasterable> displayItems;
  private LinkedList<Rasterable> selectedItems;
  private LinkedList<DrawablePath> selectionCursors;

  private Random rand;
  private BufferedImage[] variants;

  private CloudsNoise cNoise;

  private TiledID pID;
  private int operatingMode;
  private boolean wasSingle;
  private int maxGeneration;

  @SuppressWarnings("hiding")
    private ValueRamp initialize(long s, double z, Pnt2D f, long max, int xRes, int yRes, int amt, BufferedImage[] v){
    defaultLock = new ReentrantLock();
    selectionLock = new ReentrantLock();


    ValueRamp vr = new ValueRamp();
    vr.addHandle(0.25,0.2);
    vr.addHandle(0.75,0.9);

    cNoise = new CloudsNoise(max, CloudsNoise.TYPE_BILINEAR);
    cNoise.setValueRamp(vr);
    mThread = new ManagerThread();
    rand= new Random();

    activeTiles = new LinkedList<>();
    displayItems = new LinkedList<>();
    selectedItems= new LinkedList<>();
    tilePolygons = new LinkedList<>();
    selectionCursors = new LinkedList<>();

    zFactor=z;
    focus=f;
    g=0;
    mSD=550;//Math.min(xRes-50,yRes-50); //exactly screensize seems a bit dodgy to me
    this.xRes=xRes;
    this.yRes=yRes;
    initPointsAmt=amt;

    variants=v;

    seed=s;
    mThread.start();
    wasSingle=false;

    initTileSize = (int)(1000/zFactor);
    initNoiseSize=initTileSize/4.0;

    actTileSize=initTileSize;
    maxGeneration=10;

    return vr;
  }

  public TileManager(long s, double z, Pnt2D f, long max, int xRes, int yRes, int amt, BufferedImage[] v){
    initialize(s,z,f,max,xRes,yRes,amt,v);
    operatingMode=0;
  }

  public TileManager(long s, double z, Pnt2D f, long max, int xRes, int yRes, int amt, BufferedImage[] v, BufferedImage d, TiledID p){
    ValueRamp vr = initialize(s,z,f,max,xRes,yRes,amt,v);
    cNoise = new CloudsNoise(d, max, CloudsNoise.TYPE_BILINEAR);
    cNoise.setValueRamp(vr);
    operatingMode=1;
    pID=p;
  }

  public void setSelection(Rectangle r){
    if(selectionLock.tryLock()){
      sel=new Rectangle2D.Double(r.getX(),r.getY(),r.getWidth(),r.getHeight());
      synchronized(selectionFlag){
        selectionFlag=true;
      }
      synchronized(activeTiles){
        activeTiles.notifyAll();
      }
      selectionLock.unlock();
    }
  }

  public TileManager(long s, double z, Pnt2D f, long max, int xRes, int yRes, int amt, BufferedImage[] v, ValueRamp vr, CBStar cbs){
    ValueRamp interR = initialize(s,z,f,max,xRes,yRes,amt,v);
    double tmp = Double.parseDouble(cbs.properties.get("Size"))*5000;
    long rS = (long)CBPlanet.getRawSize(tmp);
    cNoise = new CloudsNoise(vr, rS, CloudsNoise.TYPE_BILINEAR);
    cNoise.setValueRamp(interR);

    ValueRamp genRamp = new ValueRamp();

    maxGeneration = (int)Math.round(genRamp.getValue(rS/1000000000.0))+6;
    System.out.println(rS+" -> "+maxGeneration);
    operatingMode=2;
    pID=cbs.getID();
  }

  public void setPosZ(double x, double y, double factor){
    if(defaultLock.tryLock()){
      if(focus.x!=x || focus.y!=y || zFactor!=factor){
        focus.x=x;
        focus.y=y;
        zFactor=factor;
        synchronized(activeTiles){
          activeTiles.notifyAll();
        }
        synchronized(updateFlag){
          updateFlag=true;
        }
      }
      defaultLock.unlock();
    }
  }

  public void resize(int x, int y){
    if(defaultLock.tryLock()){
      if(xRes!=x ||yRes!=y){
        xRes=x;
        yRes=y;
        synchronized(activeTiles){
          activeTiles.notifyAll();
        }
        synchronized(updateFlag){
          updateFlag=true;
        }
      }
      defaultLock.unlock();
    }
  }

  public void updateCycle(){
    while(true){
      try{
        //methods are self-explanatory
        if(updateFlag){
          synchronized(updateFlag){
            updateFlag=false;
          }
          defaultLock.lock();
          checkGen();
          addTilesAfterMovement();
          removeUnneededTiles();
          updateDisplayTiles();
          constructTilePolygons();
          defaultLock.unlock();
        }

        if(selectionFlag){
          synchronized(selectionFlag){
            selectionFlag=false;
          }
          selectionLock.lock();
          updateSelectionList();
          selectionLock.unlock();
        }

        if(!updateFlag && !selectionFlag){
          synchronized(activeTiles){
            activeTiles.wait();
          }
        }
      } catch (InterruptedException exc){
        System.out.println("Manager interrupted in update method");
      }
    }
  }

  private void checkGen(){
    //see if displayedTiles should be split or merged, threshold is minScreenDimension
    //g < 10 to ensure a really zoomed in feeling leaving out the last gen
    //System.out.println(g+" at: zF: "+zFactor+"   and actTS: "+actTileSize+"    should mean: "+actTileSize*zFactor+"   MAX:"+MAX);
    while(actTileSize*zFactor > mSD && g < maxGeneration){
      g++;
      actTileSize/=4;
    }
    while((actTileSize*4)*zFactor < mSD){
      g--;
      actTileSize*=4;
    }
  }

  private void addTilesAfterMovement(){
    //translate screen coordinates into game coordinates
    final double xMin = focus.x-((xRes/2)/zFactor);
    final double yMin = focus.y-((yRes/2)/zFactor);

    double x = xMin;
    double y = yMin;
    double tmpY=y;

    //plus tileSize or otherwise the most right and bottom rows won't be created
    final double xMax = x+(xRes/zFactor)+actTileSize;
    final double yMax = y+(yRes/zFactor)+actTileSize;

    //get the first pixel, see if a tile coveres it. if not, create
    //look for the next posible uncovered pixel, repead until screen is covered
    while(y < yMax){
      while(x < xMax){
        boolean covered=false;
        Tile container=null;
        int relativeG=g;
        //go up while tile does not cover the point up to g=0, start with g-1
        //if(g==0)
          //calculate which root tile will contain the point, create
        //while actG<g
          //calculate in which tile the point falls, create if necessary, down to actG=G

        //go up while no covering tile is found
        while(!covered && relativeG>0){
          relativeG--;
          //is there a covering tile in this gen?
          for(Tile t:activeTiles){
            if(t.getGen()==relativeG && t.contains(x, y)){
              covered=true;
              container=t;
              break;
            }
          }
        }
        //if there's no root containing the point, create the corresponding root
        if(!covered){
          //get the starting coords for the containing Tile by clamping x
          //to only those vlaues, root tiles could be created
          double tileX=x/initTileSize;
          tileX=Math.floor(tileX);
          tileX*=initTileSize;

          double tileY=y/initTileSize;
          tileY=Math.floor(tileY);
          tileY*=initTileSize;

          int pX = (int)((tileX)/initTileSize);
          int pY = (int)((tileY)/initTileSize);


          //unique seed by concatenating the two coordinates
          String s1 = Math.abs(Math.round(tileX))+""+Math.abs(Math.round(tileY));
          if(s1.length()>19){
            System.out.print(s1);
            s1 = s1.substring(s1.length()-19, s1.length());
            System.out.println(" got cut: "+s1);
          }
          rand.setSeed(Long.parseLong(s1)+seed);

          TiledID tid=null;

          //int shift = (int)Math.ceil(MAX/initTileSize);
          int shift = 1000; //because initT is MAX/1000, shift is always 1000

          switch(operatingMode){
          case 0:
            tid = new TiledID(pX+shift,pY+shift);
            break;
          case 1:
            tid= new TiledID(pID,pX+shift,pY+shift);
            break;
          case 2:
            tid = new TiledID(pID,pX+shift,pY+shift,true);
            break;
          }


          Tile t = new Tile(rand.nextLong(), new Pnt2D(tileX,tileY), initTileSize, null, 0, variants,
              new HashMap<>(), new Point(pX,pY),tid);

          if(!activeTiles.contains(t)){
            activeTiles.add(t);
            container=t;

            cNoise.setOffset(container.getStart());
            Double[][] tileData = cNoise.getPointsDistorted2D(container.getSize(), container.getSize(), initPointsAmt, container.getSeed(),
                                      initNoiseSize, 1, 1.2);
            container.populate(tileData,operatingMode);
          }else{
            container=activeTiles.get(activeTiles.indexOf(t));
            //System.out.println("dafuq? oO");
            //Somehow the root-tile test fails :/
            //brute force testing if there is one already, if yes, take that
            //TODO fix fucking root-detection!
          }
        }
        //go down the tree, calculate the needed tile
        while(relativeG<g){
          //calculate the tile that should contain the point
          double relX=x-container.getStart().x;
          double relY=y-container.getStart().y;

          int pInTX = (int)(relX/(container.getSize()/4));
          int pInTY = (int)(relY/(container.getSize()/4));

          //look if the desired tile is already present
          if(container.getSubTile(pInTX, pInTY)==null){
            //if not, create and populate
            try{
              container=container.createTile(pInTX, pInTY);

              cNoise.setOffset(container.getStart());
              Double[][] tileData = cNoise.getPointsDistorted2D(container.getSize(), container.getSize(), initPointsAmt, container.getSeed(),
                    initNoiseSize/Math.pow(4, relativeG), 1, 1.2);

              container.populate(tileData,operatingMode);
              activeTiles.add(container);
            } catch (TileNotFilledException tnfx){
              System.out.println(tnfx.getMessage());
            }
          }else{
            //else just set the reference
            container=container.getSubTile(pInTX, pInTY);
          }
          relativeG++;
        }
        x+=container.getSize();
        tmpY=container.getSize();
      }
      x=xMin;
      y+=tmpY;
    }
    //AND FINALLY YOU'RE DONE :D
  }

  /*@Deprecated
  @SuppressWarnings("unchecked")
  private void removeDuplicates(){
    //somehow sometimes it happens, that strange duplicateted tiles
    //are generated when operatingmode=2. just delete them.
    LinkedList<Tile> forSaving = new LinkedList<Tile>();
    for(Tile t:activeTiles){
      if(!forSaving.contains(t)) forSaving.add(t);
    }
    activeTiles=(LinkedList<Tile>)forSaving.clone();
  }*/

  private void removeUnneededTiles(){
    //remoceDuplicates();

    //1)get rid of all too deep subtiles (TileG>g, or just children of TileG>=g)
    //2)get rid of all tileG>g tileG=g tiles not in screen proximity
    //3)get rid of all tileG<g tiles not parents of displayed tiles (tileG=g)

    //4)3 can be ensured by 2: If the children is in proximity, so is the parent
    //  so modifying 2) to: get rid of all tiles not in screenProximity where tileG <= g+1
    //               or all children not in screenProximity where tileG < g+1


    //remember: you can't delete items of a list you're iteratung on, so remember which one
    //you want to delete afterwards
    LinkedList<Tile> forCollapse = new LinkedList<>();

    for(Tile t:activeTiles){
      if(t.getGen()>=g){
        forCollapse.addAll(t.getAllocatedChildren());
        t.collapseAllChildren();
      }
      if(t.getGen() < g+1){
        LinkedList<Point> pointsForCollapse = new LinkedList<>();
        //determine if any of the children are eligible for deletion
        for(Tile candidate:t.getAllocatedChildren()){
          //eligible if they are out of screenBounds +- 1 tileSize
          // -(focus+(Res/2)/zFator represents the game coordinates of pixel 0,0 in screenspace

          double xMin = focus.x-((xRes/2)/zFactor)-actTileSize;
          double xMax = focus.x+((xRes/2)/zFactor)+actTileSize;

          double yMin = focus.y-((yRes/2)/zFactor)-actTileSize;
          double yMax = focus.y+((yRes/2)/zFactor)+actTileSize;

          if(candidate.getStart().x+candidate.getSize() < xMin ||
             candidate.getStart().x > xMax ||
             candidate.getStart().y+candidate.getSize() < yMin ||
             candidate.getStart().y > yMax){


            pointsForCollapse.add(candidate.getRelPos());
            forCollapse.add(candidate);
          }
        }
        for(Point p:pointsForCollapse){
          t.collapseChild(p.x, p.y);
        }
      }
    }
    activeTiles.removeAll(forCollapse);
    forCollapse.clear();
  }

  private void updateDisplayTiles(){
    displayItems.clear();
    for(Tile t:activeTiles){
      if(t.getGen()==g){
        displayItems.addAll(t.getDrawingSet());
      }
    }
  }

  private void updateSelectionList(){
    selectedItems.clear();
    selectionCursors.clear();
    //translate screen-rect into gamespace-rect
    //focus-Res/2+coords
    Rectangle2D.Double tileRect;
    Rectangle2D.Double bodyBounds;
    //if the size is 100, it's been adjusted and meant for only 1 selection
    wasSingle =(sel.height*sel.width==100);

    sel.x=sel.x/zFactor+focus.x-(xRes/2)/zFactor;
    sel.y=sel.y/zFactor+focus.y-(yRes/2)/zFactor;
    sel.height/=zFactor;
    sel.width/=zFactor;

    for(Tile t:activeTiles){
      if(t.getGen()==g){
        tileRect = new Rectangle2D.Double(t.getStart().x,t.getStart().y,t.getSize(),t.getSize());
        if (sel.intersects(tileRect)){
          for(Rasterable b:t.getDrawingSet()){
            bodyBounds = b.getScreenBounds(focus, zFactor);
            if(sel.intersects(bodyBounds)){
              if(!(wasSingle&&selectedItems.size()==1)){
                selectedItems.add(b);
                selectionCursors.add(new DPSelectionCursor(b, new Color(0,1,1,0.55f)));
              }
            }
          }
        }
      }
    }
  }

  public boolean wasSingle(){
    return wasSingle;
  }

  private void constructTilePolygons(){
    Color c = new Color(0,1,1,0.02f);
    tilePolygons.clear();
    for(Tile t:activeTiles){
      if(t.getGen()==g){
        LinkedList<Pnt2D> dl = new LinkedList<>();
          double x1,x2,y1,y2;

          x1 = 0;
          x2 = t.getSize();
          y1 = 0;
          y2 = t.getSize();

          dl.addLast(new Pnt2D(x2,y2));
          dl.addLast(new Pnt2D(x2,y1));
          dl.addLast(new Pnt2D(x1,y1));
          dl.addLast(new Pnt2D(x1,y2));

          /*dl.add(new DPoint(x2,y2));
          dl.add(new DPoint(x1,y2));
          dl.add(new DPoint(x1,y1));
          dl.add(new DPoint(x2,y1));*/

          DPTileBoundPolygon b = new DPTileBoundPolygon(dl,t.getStart(),c,t.getIDStr());
          tilePolygons.add(b);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public LinkedList<DrawablePath> getTilePaths(){
    if(defaultLock.tryLock()){
      try{
        return (LinkedList<DrawablePath>)tilePolygons.clone();
      }finally{
        defaultLock.unlock();
      }
    }else{
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public LinkedList<Rasterable> getSelectedItems(){
    if(selectionLock.tryLock()){
      try{
        return (LinkedList<Rasterable>)selectedItems.clone();
      }finally{
        selectionLock.unlock();
      }
    }else{
      return null;
    }
  }

  public int getActiveTileCount(){
    if(defaultLock.tryLock()){
      try{
        return activeTiles.size();
      }finally{
        defaultLock.unlock();
      }
    }else{
      return -1;
    }
  }

  @SuppressWarnings("unchecked")
  public LinkedList<Rasterable> getAllDisplayItems(){
    if(defaultLock.tryLock()){
      try{
        return (LinkedList<Rasterable>)displayItems.clone();
      } finally{
        defaultLock.unlock();
      }
    }else{
      return null;
    }

  }

  public int getG(){
    if(defaultLock.tryLock()){
      try{
        return g;
      } finally{
        defaultLock.unlock();
      }
    }else{
      return -1;
    }
  }

  @SuppressWarnings("unchecked")
  public LinkedList<DrawablePath> getAllSelectionCursors(){
    if(selectionLock.tryLock()){
      try{
        return (LinkedList<DrawablePath>)selectionCursors.clone();
      }finally{
        selectionLock.unlock();
      }
    }else{
      return null;
    }
  }
}
