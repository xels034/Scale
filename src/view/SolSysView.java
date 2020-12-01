package view;

import gui.GUIAction;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;

import drawable.CBStar;
import drawable.Rasterable;
import drawable.DrawablePath;

import worldConstruction.StaticsManager;
import worldConstruction.TileManager;
import worldConstruction.TiledID;
import worldConstruction.ValueRamp;

public class SolSysView extends UniView{

  protected StaticsManager sm;
  
  private LinkedList<Rasterable> iListS;
  private LinkedList<DrawablePath> pListS;
  private LinkedList<Rasterable> sListS;
  private LinkedList<DrawablePath> cListS;
  
  private int xRes,yRes;
  
  public SolSysView(long s, long mx, double zF, int xr, int yr, int density, BufferedImage[][] imgs, ValueRamp vr, CBStar cbs) {
    super(s, mx, zF, xr, yr, density, imgs[7]);// 7 is asteroids
    maxZF=1;
    
    iListS = new LinkedList<>();
    pListS= new LinkedList<>();
    sListS = new LinkedList<>();
    cListS = new LinkedList<>();
    
    tm = new TileManager(seed,zF,focus.getCopy(), mx, 800, 600, density, imgs[7], vr, cbs);
    
    sm = new StaticsManager(seed, imgs, cbs.getID());
    
    up.setVisible(true);
    butt.setVisible(false);
  }

  @Override
  public void drawScreen(Graphics2D g2, JPanel jp) {
    xRes=jp.getWidth();
    yRes=jp.getHeight();
    super.drawScreen(g2, jp);
  }

  @SuppressWarnings("hiding")
    @Override
  protected LinkedList<DrawablePath> prepareDrawing(Graphics2D g2, int xRes, int yRes){
    
    LinkedList<Rasterable> tmpImgs = tm.getAllDisplayItems();
        if(tmpImgs!=null){
          iListT = tmpImgs;
          tmpImgs=null;
        }
        
        tmpImgs = sm.getAllDisplayItems();
        if(tmpImgs!=null){
          iListS = tmpImgs;
        }

        LinkedList<DrawablePath> tmpPth = tm.getTilePaths();
        if(tmpPth!=null){
          pListT = tmpPth;
          tmpPth=null;
        }
        
        tmpPth = sm.getAllStaticPaths();
        if(tmpPth!=null){
          pListS=tmpPth;
          tmpPth=null;
        }

        LinkedList<Rasterable>tmpSel = sm.getSelectedItems();
        if(tmpSel!=null){
          sListS = tmpSel;
          tmpSel=null;
        }
        
    tmpSel=tm.getSelectedItems();
    if(tmpSel!=null){
      sListT = tmpSel;
      tmpSel=null;
    }
        
        tmpPth = tm.getAllSelectionCursors();
        if(tmpPth!=null){
          cListT=tmpPth;
          tmpPth=null;
        }
        
        tmpPth = sm.getAllSelectionCursors();
        if(tmpPth!=null){
          cListS=tmpPth;
        }
        
        LinkedList<DrawablePath> pathList = new LinkedList<>();
        
        pathList.addAll(pListT);
        pathList.addAll(pListS);
        
        pathList.addAll(cListT);
        pathList.addAll(cListS);
        
        pathTransponate(xRes, yRes, focus, zFactor, pathList);
        imgTransponate(xRes, yRes, focus, zFactor, iListT); 
        imgTransponate(xRes, yRes, focus, zFactor, iListS);

        g2.setBackground(Color.getHSBColor(0, 0, 0));
        g2.clearRect(0, 0, xRes, yRes);     
        g2.setColor(new Color(0,1,1,0f)); 

    return pathList;
  }
  
  @SuppressWarnings("hiding")
    @Override
    protected void drawScale(Graphics2D g2, int xRes, int yRes){
    
  }
  
  @SuppressWarnings("hiding")
    @Override
  protected void drawImages(Graphics2D g2, int xRes, int yRes, JPanel gp){
    AlphaComposite ac = (AlphaComposite)g2.getComposite();
      Composite co = g2.getComposite();

      g2.setComposite(co);

      LinkedList<Rasterable> bodies = new LinkedList<>();
      
      bodies.addAll(iListT);
      bodies.addAll(iListS);

      
        for(Rasterable cb:bodies){
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
              g2.drawImage(img, cb.getAffine(), gp);
            }
            //g2.drawImage(cb.getBIHandle(), cb.getAffine(),gp);

          }catch (IllegalArgumentException x){
            //May occur for indefinitely small scaled images ... well ... tough luck i guess
          }
            cb.resetAffine();
        } 
        g2.setComposite(co); 
  }
  
  @Override
    protected void drawInfo(Graphics2D g2){
      LinkedList<Rasterable> sL = new LinkedList<>();
    sL.addAll(sListT);
    sL.addAll(sListS);
    if(sL.size()>0){
        g2.setFont(g2.getFont().deriveFont(10.5f));
          g2.setColor(new Color(0,1,1,0.15f));
          g2.fillRoundRect(10, 30, 260, 130, 10, 10);
          g2.setColor(new Color(0,1,1,1f));
          g2.drawRoundRect(10, 30, 260, 130, 10, 10);
        
        if(sL.size()==1){
          Rasterable b = sL.get(0);
          g2.drawString(b.properties.get("Kind")+" "+b.getID().toString()+" (G: "+TiledID.getGeneration(b.getID().toString())+")",20,50);
          g2.drawLine(20, 60, 260, 60);
          g2.drawString("Diameter: "+b.properties.get("Size")+b.properties.get("SUnit"),20,90);
          //g2.drawString("Age: "+b.properties.get("Age")+"byr", 20, 110);
          g2.drawString("Type: "+b.properties.get("Type"), 20, 130);
          g2.drawString("Distance: "+b.properties.get("SDistance")+b.properties.get("SDUnit"), 20, 150);
          g2.drawString("Seed: "+b.getSeed(), 20, 170);

        }else{
          g2.drawString("Multiple selections: "+sL.size()+" items",20,50);
        }
      }
    up.draw(g2);
    }
  
  @Override
  protected Rectangle buildSelectionRect(){
    Rectangle selRect = super.buildSelectionRect();
    sm.setSelection(selRect, focus, zFactor, xRes, yRes);
    return selRect;
  }
  
    @SuppressWarnings({ "deprecation", "static-access" })
  @Override
    public void mousePressed(java.awt.event.MouseEvent evt) {
        //so that dragging doesn't skip half a screen when last...XY=0
        lastKnownMouseXY=evt.getPoint();
        if(evt.getModifiers()==MouseEvent.BUTTON1_MASK){
          if(up.bounds.contains(evt.getX(),evt.getY()) && up.isVisible()){
            up.pressed=true;
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
          if(up.bounds.contains(evt.getX(),evt.getY()) && up.isVisible() && up.pressed){
            //cb=null;
            act = GUIAction.ENTER_GALAXY_FROM_SOL;
            up.pressed=false;
          }
        }
      
      Entry<GUIAction, Object> e = new AbstractMap.SimpleEntry<>(act, cb);
      return e;
    }
}
