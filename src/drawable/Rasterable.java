/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drawable;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
//import java.util.Random;

import worldConstruction.TiledID;

/**
 *
 * @author Xels
 */
public class Rasterable {
  
  protected LinkedList<BufferedImage> layers;
    protected BufferedImage shape;
    protected AffineTransform drawInfo;
    protected Pnt2D pos;
    protected double radRot;
    protected double naturalScale;
    protected double maxScale;
    protected double minScale;

    //used for smooth blending when appearing on the screen
    //see GUIPanel for how it is used
    private long lastTimeDrawn;
    private long lastTimeUpdated;
    protected TiledID id;
    protected Rectangle2D.Double bounds;
    protected long seed;
    
    public HashMap<String,String> properties;

    public Rasterable(double x, double y, long s){
      pos = new Pnt2D(x,y);
      seed=s;
    }
    
    public Rasterable(BufferedImage[] biChoices, double x, double y, double preScale, long s, double mS, TiledID pid, int rx, int ry){
        lastTimeUpdated=System.currentTimeMillis();
        //just to ensure the GUI think it was offscreen for some time
        lastTimeDrawn=lastTimeUpdated-5000;
      pos = new Pnt2D(x,y);

        radRot=0;
        //so that stars of 256size have a min size of 1-2 pixel at 1000 zoom
        naturalScale=Math.max(preScale,0.000006f);
        maxScale=mS;
        seed=s;
        
        layers = new LinkedList<>();
        
        id=new TiledID(pid.toString());
        id.addPosInTile(rx, ry);

        properties = new HashMap<>();
        properties.put("Kind", "Generic");

    }
    
    protected void buildBounds(){
      BufferedImage reference = layers.getFirst();
      bounds = new Rectangle2D.Double(-reference.getWidth()/2, -reference.getHeight()/2, reference.getWidth(), reference.getHeight());
      minScale = 1.0/(reference.getWidth());
    }
    
    public Rectangle getLayerBounds(){
      Rectangle r = new Rectangle();
      r.width=layers.getFirst().getWidth();
      r.height=layers.getFirst().getHeight();
      return r;
    }
    
    public Rectangle2D.Double getScreenBounds(Pnt2D f, double zFactor){
      Rectangle2D.Double retu = new Rectangle2D.Double(bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());

      //calculate the size on the screen, and derive the game-space size out of it
      double correctionFactor=Math.min(naturalScale*zFactor, maxScale)/zFactor;
      
      retu.x *= correctionFactor;
      retu.y *= correctionFactor;
      retu.width*= correctionFactor;
      retu.height*= correctionFactor;
      
      retu.x+=pos.x;
      retu.y+=pos.y;
      return retu;
    }
    
    public long getLastTimeDrawn(){
      return lastTimeDrawn;
    }
    
    public void setLastTimeDrawn(long t){
      lastTimeDrawn=t;
    }
    
    public long getLastTimeUpdated(){
      return lastTimeUpdated;
    }
    
    public void setLastTimeUpdated(long t){
      lastTimeUpdated=t;
    }
    
    public double getMaxScale(){
      return maxScale;
    }
    
    public double getMinScale(){
      return minScale;
    }

    public double getnaturalScale(){
        return naturalScale;
    }

    public void rotate(double rad){
        radRot+=rad;
    }

    public void resetRotation(){
        radRot=0;
    }

    public double getRad(){
        return radRot;
    }
    
    @SuppressWarnings("unchecked")
    public LinkedList<BufferedImage> getLayers(){
      return (LinkedList<BufferedImage>)layers.clone();
    }

    public AffineTransform getAffine(){
        return drawInfo;
    }

    public void setAffine(AffineTransform f){
        drawInfo=f;
    }

    public void resetAffine(){
        BufferedImage reference = layers.getFirst();
        drawInfo = new AffineTransform();
        drawInfo.translate(-reference.getWidth()/2, -reference.getHeight()/2);
    }

    public void translateXY(double x, double y){
        pos.x+=x;
        pos.y+=y;
    }

    public void setPos(double x, double y){
        pos.x=x;
        pos.y=y;
    }

    public boolean useable(){
        return layers.size()>0;
    }

    public Pnt2D getDPoint(){
        return new Pnt2D(pos.x,pos.y);
    }

    public Pnt2D getPosHandle(){
        return pos;
    }
    
    public TiledID getID(){
      return id;
    }
    
    public long getSeed(){
      return seed;
    }

}
