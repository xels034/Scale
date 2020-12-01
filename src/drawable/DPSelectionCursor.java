package drawable;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;



public class DPSelectionCursor implements DrawablePath{
  
  private LinkedList<Vec2D> lines;
  private Rasterable center;
  private Color col;
  private Point strPos;

  
  public DPSelectionCursor(Rasterable cen, Color col){
    center=cen;
    this.col=col;
    lines = new LinkedList<>();
  }
  

  @Override
  public LinkedList<Vec2D> getPreparedLines(double zFactor, Pnt2D focus, int xRes, int yRes) {
    double ns = center.getnaturalScale();
    double ms = center.getMaxScale();
    //double imgH=center.getBIHandle().getHeight();
    double imgH=center.getLayerBounds().getHeight();
    double imgW=center.getLayerBounds().getWidth();
    //double imgW=center.getBIHandle().getHeight();
    Pnt2D c = center.getDPoint();
    
    imgH*=Math.min(zFactor*ns, ms);
    imgW*=Math.min(zFactor*ns, ms);
    
    
    c.x*=zFactor;
    c.y*=zFactor;
    
    c.x+=xRes/2;
    c.y+=yRes/2;

    double untenY=c.y+imgH/2;
    double untenX=c.x-imgW/2;
    
    double obenY=c.y-imgH/2;
    double obenX=c.x+imgW/2;
    

    untenY+=5-focus.y*zFactor;
    untenX-=5+focus.x*zFactor;
    
    obenY-=5+focus.y*zFactor;
    obenX+=5-focus.x*zFactor;
    
    strPos = new Point((int)untenX-15, (int)untenY+15);
    
    lines.clear();  
    lines.add(new Vec2D(new Pnt2D(untenX,untenY-5),  new Pnt2D(untenX,untenY),col));
    lines.add(new Vec2D(new Pnt2D(untenX,untenY),   new Pnt2D(untenX+5,untenY),col));
    lines.add(new Vec2D(new Pnt2D(obenX-5,obenY),   new Pnt2D(obenX,obenY),col));
    lines.add(new Vec2D(new Pnt2D(obenX,obenY),   new Pnt2D(obenX,obenY+5),col));
    return lines;
  }
  
  @Override
    public String getName(){
    return center.getID().toString();
  }

  @Override
  public Point getNamePos() {
    return strPos;
  }


  @Override
  public float getFontSize() {
    return 8.5f;
  }
  
  @Override
  public boolean ignoreMetrics(){
    return true;
  }


  @Override
  public Color getFontColor() {
    return col;
  }


  @Override
  public void clearPreparation() {
    strPos=null;
  }


  @Override
  public void translate(double x, double y) {
    //Selectioncursors shouldn't be directly modified
  }


  @Override
  public void scale(double factor) {
    //selectioncursors shouldn't be directly modified
  }  
  
  @Override
  public Pnt2D getPos(){
    return center.getDPoint().getCopy();
  }
  
  @Override
  public float getWidth(){
    return 1f;
  }
}
