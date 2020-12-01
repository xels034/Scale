package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

import worldConstruction.TiledID;

public class CBRing extends Rasterable{

  CBPlanet parent;
  protected static final String[] solidMatTypes = {"Rock","Metal","Mineral","Precious Metal"};
  Compound comp;
  
  
  public CBRing(BufferedImage[] biChoices, double preScale, double mS, CBPlanet par) {
    super(biChoices, par.getPosHandle().x, 0, preScale, par.seed, mS, par.getID(), 0, 0);

    pos = new Pnt2D(par.getPosHandle().x,par.getPosHandle().y);
    id = new TiledID(par.getID().toString()+"r");
    parent=par;
    comp = new Compound();  
    Random r = new Random();
    r.setSeed(par.seed);

    properties.put("Kind", "Planetary Ring");
    properties.put("Type", "default");
    
    properties.put("SDistance",par.properties.get("SDistance"));
    properties.put("SDUnit", "km");
    
    double diameter = r.nextDouble()+2;
    diameter*=Double.parseDouble(par.properties.get("Size"));
    
    properties.put("Size", diameter+"");
    properties.put("SUnit", "km");
    
    String c = par.properties.get("Main Component");
    
    comp = new Compound(par.getCrustComp());
    
    int bic = 0;
    
    if(par.properties.get("Type").equals("Molten")){
      bic=4;
    }else if(c.equals("Metal")){
      bic=1;
    }else if(c.equals("Mineral")){
      bic=2;
    }else if(c.equals("Precious Metal")){
      bic=3;
    }

    layers.add(biChoices[bic]);
    
        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
    naturalScale*=diameter;
    naturalScale/=layers.getFirst().getWidth();
    
    buildBounds();
  }

  public HashMap<String, Double> getComp(){
    return comp.getComponents();
  }
}
