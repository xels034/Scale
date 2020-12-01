package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

import worldConstruction.TiledID;

public class CBAsteroid extends Rasterable{

  protected static final String[] solidMatTypes = {"Rock","Metal","Mineral","Precious Metal"};
  Compound comp;
  
  public CBAsteroid(BufferedImage[] biChoices, double x, double y,double preScale, long s, double mS, TiledID pid, int rx, int ry) {
    super(biChoices, x, y, preScale, s, mS, pid, rx, ry);
    
    comp = new Compound();  
    Random r = new Random();
    r.setSeed(s);

    properties.put("Kind", "Asteroid");
    properties.put("Type", "default");
    properties.put("SDistance", Math.hypot(x, y)+"");
    properties.put("SDUnit", "km");
    
    double size = r.nextDouble()*250+10;
    
    properties.put("Size", size+"");
    properties.put("SUnit", "km");
    String mat = CBPlanet.fillCompound(comp, solidMatTypes, r);
    
    int imat=0;
    if(mat.equals("Rock")){
      imat=0;
    }else if(mat.equals("Metal")){
      imat=1;
    }else if(mat.equals("Mineral")){
      imat=2;
    }else if(mat.equals("Precious Metal")){
      imat=3;
    }
    
    layers.addLast(biChoices[imat]);
    
        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
    naturalScale*=size;
    naturalScale/=layers.getFirst().getWidth();
    
    buildBounds();
  }


  public HashMap<String, Double> getComp(){
    return comp.getComponents();
  }
}
