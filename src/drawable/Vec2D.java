package drawable;

import java.awt.Color;
import java.lang.ref.WeakReference;


public class Vec2D{
  private Pnt2D start;
  private Pnt2D end;
  private Color col;
  
  private boolean forDeletion;
  
  public WeakReference<Vec2D> next;
  public WeakReference<Vec2D> previous;
  
  public Vec2D(Pnt2D s, Pnt2D e, Color c){
    start=s;
    end=e;
    col=c;
    forDeletion=false;    
  }
  
  public static boolean intersectsFromRight(Vec2D intersector, Vec2D intersected){
    // a x b -> signum
    
    //(a2*b3) - (a3*b2) -> x
    //-(a1*b3 - a3*b1)  -> y
    //(a1*b2) - (a2*b1) -> z
    
    double ax = intersector.end.x-intersector.start.x;
    double ay = intersector.end.y-intersector.start.y;
    
    double bx = intersected.end.x-intersected.start.x;
    double by = intersected.end.y-intersected.start.y;
    
    double z = ax*by - ay*bx;
    
    System.out.println(z);
    
    if(z<0)return false;
    else return true;
    
    //also, smallest angle is bullshit
    //it goes always along 2nd part of line2
  }
  
  private static double[] getUV(Vec2D v1, Vec2D v2){
    //uses parametric representation: A +x*(B-A)
    double ax = v1.start.x;
    double ay = v1.start.y;//origin
    double bx = v1.end.x-v1.start.x;//vector
    double by = v1.end.y-v1.start.y;
    
    double cx = v2.start.x;
    double cy = v2.start.y;
    double dx = v2.end.x-v2.start.x;
    double dy = v2.end.y-v2.start.y;
    
    // a + u*b = c + v*d
    // u,v=?
    double u,v;
    
    u =  (-cx*dy+ax*dy+(cy-ay)*dx)/(by*dx-bx*dy);
    v =  -(bx*(ay-cy)+by*cx-ax*by)/(by*dx-bx*dy);
    double[] arr = {u,v};
    return arr;
  }
  
  public static boolean isPointOnLine(Pnt2D p, Vec2D v){
    double ax = v.start.x;
    double bx = v.end.x-v.start.x;
    double cx = p.x;
    double x = (cx-ax)/bx;
    
    double ay = v.start.y;
    double by = v.end.y-v.start.y;
    double cy = p.y;
    double y = (cy-ay)/by;
    
    if(x>=0 && x<=1 && x==y){
      return true;
    }else{
      return false;
    }
  }
  
  public static boolean isIntersecting(Vec2D v1, Vec2D v2){
    //treats parallel as not intersecting
    double[] uv=getUV(v1,v2);
    
    if(uv[0]>=0 && uv[0]<=1 && uv[1]>=0 && uv[1]<=1){
      return true;
    }else{
      return false;
    }
  }
  
  public static Pnt2D getIntersection(Vec2D v1, Vec2D v2){
    //treats parallel as not intersecting
    double ax = v1.start.x;
    double ay = v1.start.y;//origin
    double bx = v1.end.x-v1.start.x;//vector
    double by = v1.end.y-v1.start.y;
    
    double[] uv = getUV(v1,v2);

    if(uv[0]>=0 && uv[0]<=1 && uv[1]>=0 && uv[1]<=1){
      return new Pnt2D(ax+uv[0]*bx,ay+uv[0]*by);
    }else{
      return null;
    }
  }
  
  public Pnt2D[] getPoints(){
    Pnt2D[] a = {start.getCopy(),end.getCopy()};
    return a;
  }
  
  public Color getColor(){
    return col;
  }
  
  public boolean isDeletionMarked(){
    return forDeletion;
  }
  
  public void markDeletion(boolean b){
    forDeletion=b;
  }
  
  public double getAngleDegree(){
    double xLen = end.x-start.x;
    double yLen = end.y-start.y;
    //because the y coordinate in mathematics goes the other way
    yLen*=-1;
    return ((Math.atan2(yLen, xLen)* (180 / Math.PI))+360)%360;
  }
  
  public double getLength(){
    double xLen = end.x-start.x;
    double yLen = end.y-start.y;
    return Math.hypot(xLen, yLen);
  }
  
}
