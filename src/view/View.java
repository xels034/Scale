package view;

import gui.GUIAction;

import java.awt.Graphics2D;
import java.util.Map.Entry;

import javax.swing.JPanel;

import drawable.Pnt2D;


public interface View {

  public void drawScreen(Graphics2D g2, JPanel jp);
  
  public void calibrate(double zFactor, Pnt2D focus);
  
  public void keyPressed (java.awt.event.KeyEvent evt);
  
  public void mouseDragged(java.awt.event.MouseEvent evt);
  
  public void mousePressed(java.awt.event.MouseEvent evt);
  
  public Entry<GUIAction, Object> mouseReleased(java.awt.event.MouseEvent evt);
  
  public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt);
}
