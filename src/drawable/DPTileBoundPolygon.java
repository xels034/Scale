/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package drawable;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.LinkedList;

/**
 *
 * @author Xels
 */
public class DPTileBoundPolygon implements DrawablePath{

  protected LinkedList<Pnt2D> nodes;
  protected LinkedList<Pnt2D> transNodes;
  protected Pnt2D center;
  protected Color col;
  private String name;
  
  //used for getNamePos()
  private double minX=0;
  private double maxY=0;
  
  private double minXT=0;
  private double maxYT=0;
  
  private float width;
  //private ValueRamp vr;
  
  public DPTileBoundPolygon(){
    nodes= new LinkedList<>();
    transNodes = new LinkedList<>();
    center= new Pnt2D(0,0);
    width=1f;
  }
  
  public DPTileBoundPolygon(LinkedList<Pnt2D> ll, Pnt2D c, Color cl, String n){
    name=n;
    nodes=ll;
    transNodes=new LinkedList<>();
    for(Pnt2D p:nodes){
      transNodes.addLast(p.getCopy());
      
      minX = Math.min(minX, p.x);
      maxY = Math.max(maxY, p.y);
    }
    minXT=minX;
    maxYT=maxY;
    
    center=c.getCopy();
    col=cl;
    width=1f;
  }
  
  public DPTileBoundPolygon(Polygon p, Pnt2D c, Color cl, String n){
    name=n;
    nodes = new LinkedList<>();
    transNodes = new LinkedList<>();
    center=c.getCopy();
    for(int i=0;i<p.npoints;i++){
      nodes.addLast(new Pnt2D(p.xpoints[i],p.ypoints[i]));
      transNodes.addLast(new Pnt2D(p.xpoints[i],p.ypoints[i]));
      
      minX = Math.min(minX, p.xpoints[i]);
      maxY = Math.max(maxY, p.ypoints[i]);
    }
    minXT=minX;
    maxYT=maxY;
    
    col=cl;
    width=1f;
  }
  
  public void setWidth(float f){
    width=f;
  }
  
  public void setCenter(Pnt2D p){
    center=p.getCopy();
  }
  
  @Override
  public float getWidth(){
    return width;
  }
  
  @Override
    public void scale(double factor){
    for(Pnt2D p:transNodes){
      p.x*=factor;
      p.y*=factor;
    }
    
    minXT*=factor;
    maxYT*=factor;
  }
  
  @Override
    public void translate(double x, double y){
    for(Pnt2D p:transNodes){
      p.x+=x;
      p.y+=y;
    }
    
    minXT+=x;
    maxYT+=y;
  }
  
  @Override
  public LinkedList<Vec2D> getPreparedLines(double zFactor, Pnt2D focus, int xRes, int yRes) {
    
    LinkedList<Vec2D> out = new LinkedList<>();
    Object[] arr = transNodes.toArray();
    
    Pnt2D dr1,dr2;
    for(int i=0;i<arr.length-1;i++){
      dr1=(Pnt2D)arr[i];
      dr2=(Pnt2D)arr[i+1];
    

      
      out.add(new Vec2D(dr1,dr2,col));
    }
    dr1=(Pnt2D)arr[arr.length-1];
    dr2=(Pnt2D)arr[0];
    out.add(new Vec2D(dr1,dr2,col));

    //resetTransform();
    
    return out;
  }
  
  @Override
    public void clearPreparation(){
    transNodes.clear();
    for(Pnt2D p:nodes){
      transNodes.add(p.getCopy());
    }
    minXT=minX;
    maxYT=maxY;
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<Pnt2D> getVertices(){
    return (LinkedList<Pnt2D>)nodes.clone();
  }
  
  @Override
    public Pnt2D getPos(){
    return center;
  }
  
  @Override
    public String getName(){
    return name;
  }
  
  @Override
  public Point getNamePos(){
    return new Point((int)minXT+3,(int)maxYT-3);
  }

  @Override
  public float getFontSize() {
    return 8.5f;
  }
  
  @Override
  public boolean ignoreMetrics(){
    return false;
  }

  @Override
  public Color getFontColor() {
    return new Color(0,1,1,0.15f);
  }
}