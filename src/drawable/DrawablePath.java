package drawable;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;


public interface DrawablePath {
  
  public LinkedList<Vec2D> getPreparedLines(double zFactor, Pnt2D focus, int xRes, int yRes);
  
  public void clearPreparation();
  
  public String getName();
  
  public Point getNamePos();
  
  public float getFontSize();
  
  public boolean ignoreMetrics();
  
  public Color getFontColor();
  
  public void translate (double x, double y);
  
  public void scale (double factor);
  
  public Pnt2D getPos();
  
  public float getWidth();
  
}
