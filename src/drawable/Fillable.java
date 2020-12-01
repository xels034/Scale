package drawable;

import java.awt.Color;
import java.awt.Polygon;
import java.util.LinkedList;


public interface Fillable {

  public Color getFillColor();
  
  public Polygon getPolygon();
  
  //1 true, 0 unknown, -1 false
  public int isCCW();
  
  public LinkedList<Vec2D> getPlot();
  
  public boolean intersects(Vec2D v);
  
  public boolean intersects(Fillable f);
  
  public boolean contains(Pnt2D p);
  
  public boolean contains(Fillable f);
  
  public LinkedList<Pnt2D> getVertices();
}
