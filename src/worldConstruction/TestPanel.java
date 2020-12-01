package worldConstruction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TestPanel extends javax.swing.JPanel{

  private static final long serialVersionUID = 1L;
  BufferedImage bi;

  @SuppressWarnings("deprecation")
  public TestPanel(){
    CloudsNoise cd = new CloudsNoise(1000000000, 2);
    ValueRamp vr = new ValueRamp();
    vr.addHandle(0.25,0.2);
    vr.addHandle(0.75,0.9);
    cd.setValueRamp(vr);
    //bi = cd.getNoiseBW2D(1024, 720, 100, 1);
    bi = cd.getDistortedNoiseBW2D(400,200, 30, 1, 1.2);
  }
  
  @Override
  public void paintComponent(Graphics g){
    AffineTransform af = new AffineTransform();
    af.scale(5, 5);
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(bi,af,this);
    


    g2.setColor(Color.red);
  }
}
