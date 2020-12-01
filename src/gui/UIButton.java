package gui;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;



public class UIButton implements UIElement{

  public Rectangle bounds;
  public boolean pressed;
  boolean vis;
  String text;
  Point tPos;
  UIElement parent;
  HashSet<UIElement> children;
  
  
  public UIButton(int x, int y, int w, int h, String t, UIElement p){
    bounds = new Rectangle(x,y,w,h);
    pressed=false;
    text=t;
    tPos = new Point(x+3,y+h-3);
    parent=p;
    children = new HashSet<>();
  }

  @Override
  public UIElement getParent() {
    return parent;
  }

  @Override
  public Set<UIElement> getChildren() {
    return children;
  }

  @Override
  public void draw(Graphics2D g2) {
    g2.draw(bounds);
    g2.drawString(text, tPos.x, tPos.y);
    for(UIElement e:children){
      e.draw(g2);
    }
  }

  @Override
  public void addChild(UIElement e) {
    children.add(e);
  }

  @Override
  public boolean isVisible() {
    return vis;
  }

  @Override
  public void setVisible(boolean b) {
    vis=b;  
  }
}
