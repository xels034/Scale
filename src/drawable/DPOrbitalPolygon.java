package drawable;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.LinkedList;

import worldConstruction.ValueRamp;

public class DPOrbitalPolygon extends DPTileBoundPolygon{

  private ValueRamp vr;
  
  public DPOrbitalPolygon(){
    super();
    
    vr = new ValueRamp();
    vr.changeEnds(1, 1);
  }
  
  public DPOrbitalPolygon(LinkedList<Pnt2D> ll, Pnt2D c, Color cl, String n){
    super(ll,c,cl,n);
    
    vr = new ValueRamp();
    vr.changeEnds(1, 1);
  }
  
  public DPOrbitalPolygon(Polygon p, Pnt2D c, Color cl, String n){
    super(p,c,cl,n);
    
    vr = new ValueRamp();
    vr.changeEnds(1, 1);
  }
  
  
  public void setAlphaRamp(ValueRamp v){
    vr=v;
  }
  
  @Override
  public LinkedList<Vec2D> getPreparedLines(double zFactor, Pnt2D focus, int xRes, int yRes) {
    double relPos=1;
    double stepSize=1.0/nodes.size();
    double mod=1;
    Color c;
    
    LinkedList<Vec2D> out = new LinkedList<>();
    Object[] arr = transNodes.toArray();
    
    Pnt2D dr1,dr2;
    for(int i=0;i<arr.length-1;i++){
      dr1=(Pnt2D)arr[i];
      dr2=(Pnt2D)arr[i+1];
      mod = vr.getValue(relPos);
      
      c = new Color(col.getRed(),
              col.getGreen(),
              col.getBlue(),
              (int)(col.getAlpha()*mod));
    
      out.add(new Vec2D(dr1,dr2,c));
      relPos-=stepSize;
    }
    dr1=(Pnt2D)arr[arr.length-1];
    dr2=(Pnt2D)arr[0];
    out.add(new Vec2D(dr1,dr2,col));
    
    return out;
  }
  
  @Override
  public Point getNamePos(){
    Pnt2D p = transNodes.getFirst();
    Point ret = new Point((int)(p.x-5),(int)(p.y+5));
    return ret;
  }
  
  @Override
  public boolean ignoreMetrics(){
    return true;
  }
  
  @Override
  public Color getFontColor() {
    return new Color(0,1,1,0.35f);
  }
}
