package drawable;

import java.awt.Color;
import java.awt.Polygon;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class DPAreaPolygon extends DPTileBoundPolygon implements Fillable{

  public LinkedList<DPAreaPolygon> children;
  private int ccw;
  private LinkedList<Vec2D> shape;
  //a vector from top left to bottom right describes the rectangular bounds
  //used to cast a ray thats always outside of the polygon
  private Vec2D boundry;
  
  private void constructBounds(){
    shape = new LinkedList<>();
    double minX=0;
    double minY=0;
    double maxX=0;
    double maxY=0;
    
    Pnt2D f,l;
    for(int i=0;i<nodes.size()-1;i++){
      f=nodes.get(i);
      l=nodes.get(i+1);
      
      minX=Math.min(minX, f.x);
      minX=Math.min(minX, l.x);
      
      minY=Math.min(minY, f.y);
      minY=Math.min(minY, l.y);
      
      maxX = Math.max(maxX, f.x);
      maxX = Math.max(maxX, l.x);
      
      maxY = Math.max(maxY, f.y);
      maxY = Math.max(maxY, l.y);
      
      shape.addLast(new Vec2D(f,l,col));
    }
    shape.addLast(new Vec2D(nodes.getLast(),nodes.getFirst(),col));
    boundry = new Vec2D(new Pnt2D(minX,minY),new Pnt2D(maxX,maxY),col);
  }
  
  public DPAreaPolygon(LinkedList<Pnt2D> pll, LinkedList<DPAreaPolygon> all, Pnt2D c, String n){
    super(pll,c,all.getFirst().getBaseColor(),n);
    ccw=0;
    children=all;
    constructBounds();
  }
  
  public DPAreaPolygon(LinkedList<Pnt2D> ll, Pnt2D c, Color cl, String n){
    super(ll, c, cl, n);
    ccw=0;
    children = new LinkedList<>();
    //non-merged polys consist only of themselfes
    children.add(this);
    constructBounds();
  }
  
  public DPAreaPolygon(Polygon p, Pnt2D c, Color cl, String n){
    super(p,c,cl,n);
    ccw=0;
    children = new LinkedList<>();
    //non-merged polys consist only of themselfes
    children.add(this);
    constructBounds();
  }
  
  public static DPAreaPolygon[] merge (DPAreaPolygon p1, DPAreaPolygon p2){
    if(p1.contains(p2)){
      p1.children.add(p2);
      DPAreaPolygon[] arr = {p1};
      return arr;
    }else if(p2.contains(p1)){
      p2.children.add(p1);
      DPAreaPolygon[] arr = {p2};
      return arr;
    }else if(p1.intersects(p2)){
      LinkedList<Vec2D[]> crossings = new LinkedList<>();
      LinkedList<Vec2D> p1Shape = p1.getPlot();
      LinkedList<Vec2D> p2Shape = p2.getPlot();
      for(Vec2D v1: p1Shape){
        for(Vec2D v2: p2Shape){
          if(Vec2D.isIntersecting(v1, v2)){
            Vec2D[] arr = {v1,v2};
            crossings.add(arr);
          }
        }
      }
      
      System.out.println(crossings.size());
      //for(Vec2D[] xr: crossings){
        //TODO
        //TODO do it do it :D
        //find the "first" one with Vec2D.intersectsFromRight
        //center point -> c->l2.e is ALWAYS the line you go
        //work with copys, lines may be used elsewhere
        //if line.len=0:
        //previous.next=this.next;
        //this.next.previous=this.previous;
      //}

    }else{
      
    }
    return null;
  }
  
  @Override
  public boolean intersects(Fillable f){
    for(Vec2D v: f.getPlot()){
      if(intersects(v)){
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean intersects(Vec2D v){
    for(Vec2D v2d: shape){
      if(Vec2D.isIntersecting(v2d, v)){
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean contains(Pnt2D p){
    int c=0;
    Pnt2D origin = boundry.getPoints()[0];
    //ensures that the origin is always outside of the polygon
    origin.x-=1;
    origin.y-=1;
    
    //the idea is, whenever a ray cast from outside of the polygon
    //to the point in question, the number is always odd if it is inside
    //and even if it is not
    
    Vec2D ray = new Vec2D(origin,p,col);
    
    for(Vec2D v: shape){
      if(Vec2D.isPointOnLine(p, v)){
        //it is also inside if its exactly on a line
        return true;
      }else if(Vec2D.isIntersecting(ray, v)){
        c++;
      }
    }
    return c%2==1;
  }
  
  @Override
  public boolean contains(Fillable f){
    LinkedList<Pnt2D> ll = f.getVertices();
    for(Pnt2D p: ll){
      if(!contains(p)){
        return false;
      }
    }
    return true;
  }
  
  @Override
  public Polygon getPolygon(){
    Pnt2D[] arr = transNodes.toArray(new Pnt2D[0]);
    Polygon p = new Polygon();
    for(int i=0;i<arr.length;i++){
      p.addPoint((int)arr[i].x, (int)arr[i].y);
    }
    return p;
  }
  
  @Override
  public Color getFillColor(){
    return new Color(col.getRed(),
             col.getGreen(),
             col.getBlue(),
             col.getAlpha()/3);
  }
  
  @Override
  public int isCCW(){
    return ccw;
  }
  
  public Color getBaseColor(){
    return col;
  }
  
  @Override
  public LinkedList<Vec2D> getPlot(){
    //used for merging two polygons
    //its similar to a DrawablePath, only in game-space
    //and the Vec2Ds are linked to each other in a ring
    //also checking if the structure is CW oder CCW
    LinkedList<Vec2D> ll = new LinkedList<>();
    Pnt2D[] arr = nodes.toArray(new Pnt2D[0]);
    
    Vec2D last=null;
    Vec2D act;
    
    int fails=0;
    
    for(int i=0;i<arr.length-1;i++){
      act = new Vec2D(arr[i],arr[i+1],col);
      if(last!=null){
        if(last.getAngleDegree()>act.getAngleDegree()){
          fails++;
        }
        last.next=new WeakReference<>(act);
        act.previous = new WeakReference<>(last);
      }
      ll.add(act);
      last=act;
    }
    act = new Vec2D(arr[arr.length-1],arr[0],col);
    if(last.getAngleDegree()>act.getAngleDegree()){
      fails++;
    }
    last.next=new WeakReference<>(act);
    act.previous = new WeakReference<>(last);
    ll.add(act);
    act.next=new WeakReference<>(ll.getFirst());
    act.previous = new WeakReference<>(last);
    
    if(fails==1){
      //smallest polygon is a triangle.
      //in a CCW triangle, all prev. Vects angles are smaller that their followers
      //except when it jumps from 0 to 360.
      //so, the check in a CCW fails once
      ccw=1;
    }else{
      ccw=-1;
    }
    
    return ll;
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<DPAreaPolygon> getChildren(){
    return (LinkedList<DPAreaPolygon>)children.clone();
  }
}
