package view;

import gui.AdvancedComposite;
import gui.GUIAction;
import gui.UIButton;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;

import drawable.Rasterable;
import drawable.DrawablePath;
import drawable.DPTileBoundPolygon;
import drawable.Pnt2D;
import drawable.Vec2D;

import worldConstruction.TileManager;
import worldConstruction.TiledID;

public class UniView implements View{
  
  protected long seed;
  protected Point lastKnownMouseXY;
    private long ts;
    private long lastUpdate;
    protected Pnt2D focus;
    protected double zFactor;
    private int generationCount;
    private int allTilesCount;
    protected DPTileBoundPolygon playField;
    protected final float blendInMS=1700;
    //private BlendComposite blCmp;
    private AdvancedComposite advCmp;
    //2.000.000.000 total diameter
    protected final long MAX;
    protected final double minZF;
    protected double maxZF;
    private boolean debug;
    private boolean eyeCandy;
    //private final int mode;

    protected Point selectionS;
    protected Point selectionE;
    
    //used to display the selection box while dragging
    protected boolean selected=false;
    
    private boolean calibrateFlag=false;
    
    protected LinkedList<DrawablePath> pListT;
    protected LinkedList<Rasterable> iListT;
    protected LinkedList<Rasterable> sListT;
    protected LinkedList<DrawablePath> cListT;
    
    protected TileManager tm;
    protected UIButton butt;
    protected UIButton up;
    
    public UniView(long s, long mx, double zF, int xr, int yr, int density, BufferedImage[] imgs){
      MAX=mx;
      minZF=zF;
      maxZF=5000;
      debug=false;
      eyeCandy=false;
      focus = new Pnt2D(0,0);
      zFactor=zF;
      generationCount=0;
      allTilesCount=0;
        ts=System.currentTimeMillis();
        seed=s;
        selectionS=new Point(0,0);
        selectionE=new Point(0,0);
      //blCmp = BlendComposite.Multiply;
      advCmp = new AdvancedComposite(AdvancedComposite.MODE_MULTIPLY,1);
        lastKnownMouseXY= new Point(0,0);
            
        iListT = new LinkedList<>();
      sListT = new LinkedList<>();
      
      pListT = new LinkedList<>();
      cListT = new LinkedList<>();

      tm = new TileManager(seed,zFactor,focus.getCopy(), MAX, 800, 600, density, imgs);     

        buildBoundsPoly(); 
        
        butt = new UIButton(150,100,50,20,"Enter",null);
        up = new UIButton(500,25,20,20,"Up",null);
    }
    
    @Override
  public void calibrate(double zF, Pnt2D d){
      focus=d.getCopy();
      zFactor=zF;
      tm.setPosZ(focus.x, focus.y, zF);
      calibrateFlag=true;
    }
    
    private void buildBoundsPoly(){
        Polygon p = new Polygon();     
        p.addPoint((int)-MAX, (int)-MAX);
        p.addPoint((int)-MAX, (int)MAX);
        p.addPoint((int)MAX, (int)MAX);
        p.addPoint((int)MAX, (int)-MAX);
        playField = new DPTileBoundPolygon(p,new Pnt2D(0,0),new Color(0,1,1,1f),"Playfield");
    }
      
    @Override
  public void drawScreen(Graphics2D g2, JPanel gp){
      int xRes=gp.getWidth();
      int yRes=gp.getHeight();
      
      double diff = System.currentTimeMillis()-ts;
        ts=System.currentTimeMillis(); 
      tm.resize(xRes, yRes);

      //little hack to unsure the position after zooming is really updated
      lastUpdate+=diff;
      if(lastUpdate>1000){
        lastUpdate=0;
        tm.setPosZ(focus.x, focus.y, zFactor);
      }

        //somehow, it works only on the second drawing cycle
      if(calibrateFlag){
        if(sListT.size()>0){
          calibrateFlag=false;
        }else{
          tm.setSelection(new Rectangle((xRes/2)-5,(yRes/2)-5,10,10));
        }  
      }
        
      LinkedList<DrawablePath> pathList = prepareDrawing(g2,xRes,yRes);

        drawPaths(g2,xRes,yRes,pathList);
        drawImages(g2,xRes,yRes, gp);
        
        if(eyeCandy && zFactor<0.02){      
          drawPostPro(g2,xRes,yRes);
        } 
        
        drawUI(g2,xRes,yRes,diff);
    }
    
    public static void pathTransponate(int xRes, int yRes, Pnt2D focus, double zFactor, LinkedList<DrawablePath> pathL){
        for(DrawablePath dP:pathL){
            dP.translate(-focus.x+dP.getPos().x, -focus.y+dP.getPos().y);
            dP.scale(zFactor);
            dP.translate(xRes/2, yRes/2);    
        }
    }

    public static void imgTransponate(int xRes, int yRes, Pnt2D focus, double zFactor, LinkedList<Rasterable> list){
      //describes image trans/rot/scal
        AffineTransform tmpF;
       
        for(Rasterable bi:list){
            tmpF=bi.getAffine();
            
            double maxScale=bi.getnaturalScale()*zFactor;
            maxScale=Math.min(maxScale, bi.getMaxScale());
            double corrFactor=((bi.getnaturalScale()*zFactor)/maxScale)-1;
            
            double w = bi.getLayerBounds().getWidth();
            double h = bi.getLayerBounds().getHeight();
            double n = bi.getnaturalScale();
            
            //IMPORTANT: Action happens ALWAYS around original 0,0 of image
            //ALL operations happen in reverse order! Read comments bottom to top
            //ALSO: BImgObj's BI is already CENTERED around it's position!
            
            tmpF.translate(w*0.5*zFactor, h*0.5*zFactor);
            tmpF.translate(-w*zFactor*n,
                     -h*zFactor*n);

            tmpF.translate((-w/2 +
                            bi.getDPoint().x-focus.x/*-(xRes/2)*/) *
                            (zFactor)+
                            (xRes/2)+w/2,

                           (-h/2 +
                            bi.getDPoint().y-focus.y/*-(yRes/2)*/) *
                            (zFactor) +
                            (yRes/2)+h/2);

            tmpF.translate((w/2)*(n*zFactor),
                           (h/2)*(n*zFactor));
            
            //trial and error until i got it. Correction for additional scale by getNaturalScale()
            //for objects whom images should be scaled instead of the original image size

            double scaleScale = Math.max(maxScale, bi.getMinScale());
            tmpF.scale(scaleScale, scaleScale);
            //minScale ensures a approximate 1px width onscreen, so translating-correction
            //can be neglected
            
            tmpF.translate((w/2)*corrFactor, (h/2)*corrFactor);
      
            //(2): get the scaled image. with optional maxScale (e.g. stars should stay small.
            //     this is ensured be the code ABOVE the scaling/correction translation. Use
            //     double.POSITIVE_INFINITY if you want objects to scale always.
            
            tmpF.rotate(bi.getRad(), w/2,
                        h/2);
            //(1): rotating around center (otherwise it'd be the 0,0 corner
            //BEGIN READING TRANSITIONS HERE
        }
    }
    
    protected LinkedList<DrawablePath> prepareDrawing(Graphics2D g2, int xRes, int yRes){

        LinkedList<Rasterable> tmpImgs = tm.getAllDisplayItems();
        if(tmpImgs!=null){
          iListT = tmpImgs;
        }

        LinkedList<DrawablePath> tmpPth = tm.getTilePaths();
        if(tmpPth!=null){
          pListT = tmpPth;
        }
        tmpPth=null;
        
        LinkedList<Rasterable> tmpSel = tm.getSelectedItems();
        if(tmpSel!=null){
          sListT = tmpSel;
        }
        
        tmpPth = tm.getAllSelectionCursors();
        if(tmpPth!=null){
          cListT=tmpPth;
        }
        
        LinkedList<DrawablePath> pathList = new LinkedList<>();
        
        pathList.addAll(pListT);
        pathList.addAll(cListT);
        
        pathList.add(playField);
        pathTransponate(xRes, yRes, focus, zFactor, pListT);
        //pathList.remove(playField);
        
        imgTransponate(xRes, yRes, focus, zFactor, iListT);
        
        
        g2.setBackground(Color.getHSBColor(0, 0, 0));
        g2.clearRect(0, 0, xRes, yRes);     
        g2.setColor(new Color(0,1,1,0f)); 
        
        return pathList;
    }

    protected void drawImages(Graphics2D g2, int xRes, int yRes, JPanel gp){
      AlphaComposite ac = (AlphaComposite)g2.getComposite();
        Composite co = g2.getComposite();
        
        for(Rasterable cb:iListT){
          try{
            //smooth blend-in
            long time = System.currentTimeMillis();
            //when the last time it was drawn is greater than a second, its probably "New" on the screen
            //so set the Updated value to the current time
            if(time-cb.getLastTimeDrawn()>1000){
              cb.setLastTimeUpdated(time);
            }
            //based on the updated value determine if its still blending in (from 0-blendInMS)
            float alpha = (Math.min(time-cb.getLastTimeUpdated(), blendInMS))/blendInMS;
            //set last drawn to now for the next painting cycle
            cb.setLastTimeDrawn(time);
            
            g2.setComposite(ac.derive(alpha));
            for(BufferedImage img:cb.getLayers()){
              g2.drawImage(img, cb.getAffine(),gp);
            }

          }catch (IllegalArgumentException x){
            //May occur for indefinitely small scaled images ... well ... tough luck i guess
          }
            cb.resetAffine();
        } 
        g2.setComposite(co);
    }

    private void drawPostPro(Graphics2D g2, int xRes, int yRes){
      float[] subtractor= {(float)(0.4/(zFactor*1000)),(float)(0.75f/(zFactor*1000)),(float)(1/(zFactor*1000))};
      
        Composite c = g2.getComposite();
        //g2.setComposite(blCmp);
        g2.setComposite(advCmp);
        g2.setColor(new Color(1-subtractor[0],1-subtractor[1],1-subtractor[2],1f));
        g2.fillRect(0, 0, xRes, yRes);
        g2.setComposite(c);
    }

    private void drawPaths(Graphics2D g2, int xRes, int yRes, LinkedList<DrawablePath> pathList){
        for(DrawablePath dp:pathList){
          g2.setStroke(new BasicStroke(dp.getWidth(),BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
          for(Vec2D l:dp.getPreparedLines(zFactor, focus,xRes, yRes)){
            g2.setColor(l.getColor());
            g2.drawLine((int)l.getPoints()[0].x, (int)l.getPoints()[0].y, (int)l.getPoints()[1].x, (int)l.getPoints()[1].y);
          }
          g2.setStroke(new BasicStroke(1));
          //if there is enough place to display the TiledID
          double w=g2.getFontMetrics().getStringBounds(dp.getName(), g2).getWidth();
          //1M is the init tile size. 1/2.5 would be the max width of the text
          double max=((1000000*zFactor)/Math.pow(4, generationCount)/2.5);
          if(w<max && generationCount!=-1 || dp.ignoreMetrics()){    
            Point nPos = dp.getNamePos();
            g2.setFont(g2.getFont().deriveFont(dp.getFontSize()));
            g2.setColor(dp.getFontColor());
            g2.drawString(dp.getName(), nPos.x, nPos.y);  
          }
          dp.clearPreparation();
        }
    }

    @SuppressWarnings("null")
    private void drawUI(Graphics2D g2, int xRes, int yRes, double diff){
      g2.setColor(new Color(0,1,1,1f));
        g2.drawLine(xRes/2, (yRes/2)-10, xRes/2, (yRes/2)+10);
        g2.drawLine((xRes/2)-10,yRes/2,(xRes/2)+10,yRes/2);
        
        int i = tm.getActiveTileCount();
        Integer gen =tm.getG();
        if(i>-1){
          allTilesCount=i;
        }
        if(gen!=null){
          generationCount=gen;
        }
        
        g2.setFont(g2.getFont().deriveFont(8.5f));
        
        if(debug){
          g2.drawString("Total tiles: "+allTilesCount, xRes-130, 20);
          g2.drawString("Total Img Amount: "+iListT.size(),xRes-130,40);
          g2.drawString("FPS: "+String.format("%.3f",1000/diff), xRes-130, 60);
          g2.drawString("zFactor: "+zFactor,xRes-130,80);
          g2.drawString("Known G: "+generationCount, xRes-130, 100);
        }
        
        //draw the rect for selection. only displayed while dragging the rect acros the screen
        if(selected){
          g2.setColor(new Color(0,1,1,0.05f));
          g2.fillRect(Math.min(selectionS.x,selectionE.x), Math.min(selectionS.y, selectionE.y), Math.abs(selectionE.x-selectionS.x), Math.abs(selectionE.y-selectionS.y));
          g2.setColor(new Color(0,1,1,0.4f));
          g2.drawRect(Math.min(selectionS.x,selectionE.x), Math.min(selectionS.y, selectionE.y), Math.abs(selectionE.x-selectionS.x), Math.abs(selectionE.y-selectionS.y));
        }
        g2.setColor(new Color(0,1,1,1f));
        g2.setFont(g2.getFont().deriveFont(8.5f));
        drawMagnitude(g2,xRes,yRes);
        drawScale(g2,xRes,yRes);
        drawInfo(g2);

        if(generationCount==-1){
          g2.drawString("Loading ...", xRes/2-20, yRes/2+40);
        }
    }
    
    private void drawMagnitude(Graphics2D g2, int xRes, int yRes){
      
      int pos = (int)(Math.log10(zFactor)*25);
      int anchor = yRes-100;
      
      //+4 to get a 8pt text into the middle
      
      //g2.drawString("Magnitude: "+(Math.round(1/zFactor*1000)), 5, anchor+100);
      
      g2.drawString("0.5x", 20, anchor-75-20+4);
      g2.drawString("1x", 20, anchor-75+4);
      g2.drawString("1000x", 20, anchor+4);
      g2.drawString("1.000.000x",20,anchor+75+4);
      
      for(int i=-75;i<=75;i+=25){
        g2.drawLine(16, anchor-i, 18, anchor-i);
      }
      g2.drawLine(16, anchor-75-17, 18, anchor-75-17);

      
      g2.drawLine(18,anchor-75-17,18,anchor+75);
      g2.drawLine(4, anchor-pos, 14,anchor-pos);
    }
    
    protected void drawScale(Graphics2D g2, int xRes, int yRes){
      //g2.setColor(new Color(0,1,0,0.5f));
      //width of the unit (parsec) meassurend in pixel
        double unitLength=zFactor/1000000000;
        //where the scale starts on x axis from left
        int startXL=120;
        //max len meassured from right
        int maxLenR=120;
        
        String[] suffix = {"","K","M","G","T","P"};
        String[] nulls = {"","0","00"};
        String output;
        
        int nullen=0;
        
        while(unitLength<60){
          unitLength*=10;
          nullen++;
        }
        int kays = (int)Math.floor(nullen/3f);
        nullen=nullen%3;
        
        int mod=0;
        
        g2.drawString("Scale",xRes-startXL-10,yRes-50);
        
        g2.drawString("X: "+String.format("%.3f",focus.x), xRes-100, yRes-50);
        g2.drawString("Y: "+String.format("%.3f",focus.y), xRes-100, yRes-30);
        
        g2.drawLine(maxLenR, yRes-20, xRes-startXL, yRes-20);
        for(int i=(xRes-startXL);i>maxLenR;i-=unitLength){
          g2.drawLine(i, yRes-20, i, yRes-30);
          if(mod>0){
            output=mod+nulls[nullen]+suffix[kays]+"pc";
            g2.drawString(output, i, yRes-35);
          }
          mod++;
        }
    }
    
    protected void drawInfo(Graphics2D g2){
      if(sListT.size()>0){
        g2.setFont(g2.getFont().deriveFont(10.5f));
          g2.setColor(new Color(0,1,1,0.15f));
          g2.fillRoundRect(10, 30, 260, 130, 10, 10);
          g2.setColor(new Color(0,1,1,1f));
          g2.drawRoundRect(10, 30, 260, 130, 10, 10);
        
        if(sListT.size()==1){
          Rasterable b = sListT.get(0);
          g2.drawString(b.properties.get("Kind")+" "+b.getID().toString()+" (G: "+TiledID.getGeneration(b.getID().toString())+")",20,50);
          g2.drawLine(20, 60, 260, 60);
          g2.drawString("Diameter: "+b.properties.get("Size")+b.properties.get("SUnit"),20,90);
          g2.drawString("Age: "+b.properties.get("Age")+"byr", 20, 110);
          g2.drawString("Type: "+b.properties.get("Type"), 20, 130);
          g2.drawString("Seed: "+b.getSeed(), 20, 150);
          butt.setVisible(true);
        }else{
          g2.drawString("Multiple selections: "+sListT.size()+" items",20,50);
          butt.setVisible(false);
        }
      }else{
        butt.setVisible(false);
      }
      
      if(butt.isVisible()){
        butt.draw(g2);
      }
    }

    protected Rectangle buildSelectionRect(){
      Rectangle selRect = new Rectangle(Math.min(selectionS.x, selectionE.x),
                       Math.min(selectionS.y, selectionE.y),
                       Math.abs(selectionS.x-selectionE.x),
                       Math.abs(selectionS.y-selectionE.y));
      //if its small enough, it sould be a click
      //however, a click is represented by a rect (you dont have to klick
      //EXACTLY in an item. but in that case its shifted so that the mouse-
      //position is in the center
      if(selRect.getWidth()*selRect.getHeight()<100){
        selRect.width=10;
        selRect.height=10;
        selRect.x=lastKnownMouseXY.x-3;
        selRect.y=lastKnownMouseXY.y-3;
      }
      tm.setSelection(selRect);
      return selRect;
    }

  protected void checkBounds(){
    zFactor=Math.max(zFactor, minZF);
    zFactor=Math.min(zFactor, maxZF);
    
    focus.x=Math.max(focus.x, -MAX);
    focus.y=Math.max(focus.y, -MAX);
    
    focus.x=Math.min(focus.x, MAX);
    focus.y=Math.min(focus.y, MAX);
  }

  @Override
    public void keyPressed (java.awt.event.KeyEvent evt){
        //ONLY works wir keyPressed, NOT keyTyped.
      //must be invoked my the frame, the panel doesn't recognize it
      System.out.println("Key: "+evt.getKeyCode());
        
        switch(evt.getKeyCode()){
        case KeyEvent.VK_BACK_SPACE:
          focus.x=0;
          focus.y=0;
          tm.setPosZ(0, 0, zFactor);
          break;
        case KeyEvent.VK_C:
        zFactor/=1.1;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_V:
        zFactor*=1.1;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_W:
        focus.y-=10/zFactor;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_S:
        focus.y+=10/zFactor;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_A:
        focus.x-=10/zFactor;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_D:
        focus.x+=10/zFactor;
        checkBounds();
        tm.setPosZ(focus.x,focus.y,zFactor);
        break;
        case KeyEvent.VK_B:
        if(debug){
          debug=false;
        }else{
          debug=true;
        }
        break;
        case KeyEvent.VK_R:
        if(eyeCandy){
          eyeCandy=false;
        }else{
          eyeCandy=true;
        }
        }
    }

    @SuppressWarnings({ "deprecation", "static-access" })
  @Override
    public void mouseDragged(java.awt.event.MouseEvent evt) {
        //would use .getButton()==evt.BUTTON3, but getButton() is always 0, dafuq?  
        if(evt.getModifiers()==MouseEvent.BUTTON3_MASK){
            focus.x-=(evt.getX()-lastKnownMouseXY.x)/zFactor;
            focus.y-=(evt.getY()-lastKnownMouseXY.y)/zFactor;
            checkBounds();
          tm.setPosZ(focus.x,focus.y,zFactor);
        }else if(evt.getModifiers()==MouseEvent.BUTTON1_MASK){
          selectionE.x=evt.getX();
          selectionE.y=evt.getY();
        }
        lastKnownMouseXY=evt.getPoint();
    }
    
    @SuppressWarnings({ "deprecation", "static-access" })
  @Override
    public void mousePressed(java.awt.event.MouseEvent evt) {
        //so that dragging doesn't skip half a screen when last...XY=0
        lastKnownMouseXY=evt.getPoint();
        if(evt.getModifiers()==MouseEvent.BUTTON1_MASK){
          if(butt.bounds.contains(evt.getX(),evt.getY()) && butt.isVisible()){
            butt.pressed=true;
          }else{
            selected = true;
            selectionS = new Point(evt.getX(),evt.getY());
            selectionE = new Point(evt.getX(),evt.getY());
          }
        }
    }
    
    @SuppressWarnings({ "deprecation", "static-access" })
  @Override
    public Entry<GUIAction, Object> mouseReleased(java.awt.event.MouseEvent evt){
      Rasterable cb=null;
        GUIAction act = GUIAction.NONE;
      if(evt.getModifiers()==MouseEvent.BUTTON1_MASK){
          if(selected){
            selected=false;
            buildSelectionRect();
          }
          if(butt.bounds.contains(evt.getX(),evt.getY()) && butt.isVisible() && butt.pressed){
            cb=sListT.get(0);
            act = GUIAction.ENTER_GALAXY_FROM_UNIVERSE;
            butt.pressed=false;
          }
        }
      
      Entry<GUIAction, Object> e = new AbstractMap.SimpleEntry<>(act, cb);
      return e;
    }

    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        //zooming 1.1 per rotation, so three rots = 1.1^3
        //-rotation to invert zoom direction
        zFactor*=Math.pow(1.1, -evt.getWheelRotation());
        checkBounds();
      tm.setPosZ(focus.x,focus.y,zFactor);
    }
    
}
