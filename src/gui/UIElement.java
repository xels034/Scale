package gui;

import java.awt.Graphics2D;
import java.util.Set;

public interface UIElement {

  public UIElement getParent();
  
  public Set<UIElement> getChildren();
  
  public void draw(Graphics2D g2);
  
  public void addChild(UIElement e);
  
  public void setVisible(boolean b);
  
  public boolean isVisible();
}
