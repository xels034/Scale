package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import worldConstruction.TiledID;
import worldConstruction.ValueRamp;

public class CBSun extends CBStar{

  public CBSun(BufferedImage[] biChoices, double x, double y,double preScale, long s, double mS, TiledID pid) {
    super(biChoices, x, y, preScale, s, mS, pid, 0, 0);
    id = new TiledID(pid.toString());
    id.addCenterMass(0);
    
    int temp = (int)(Double.parseDouble(properties.get("Size"))*5000);
    
    properties.put("Temperature", temp+"");
    properties.put("TUnit", "k");
    properties.put("System Size", CBPlanet.fSize(temp)+"");
    
    ValueRamp pAmt = new ValueRamp();
    pAmt.changeEnds(2, 20);
    pAmt.addHandle(0.3, 15);
    
    //double sysSize = CBPlanet.getRawSize(temp);
    double ix = (temp/5000.0)/15.0;
    
    Random r = new Random();
    r.setSeed(s);
    double pAmtBase= r.nextDouble();
    ix = pAmt.getValue(ix);
    pAmtBase*=0.7*ix;
    pAmtBase-=0.35*ix;
    
    int erg=(int)(ix+pAmtBase+0.5);
    
    properties.put("Planets", erg+"");
    
    
    int bic=0;
    String type = properties.get("Type");
    if(type.equals("Class K")){
      bic=1;
    }else if(type.equals("Class F")){
      bic=2;
    }else if(type.equals("Class B")){
      bic=3;
    }else if(type.equals("Class O")){
      bic=4;
    }

    layers.add(biChoices[bic]);

        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
    naturalScale*=Double.parseDouble(properties.get("Size"))*700000; //to get from sun Radii to kilometers
    naturalScale/=layers.getFirst().getWidth();
    
    buildBounds();
    
    minScale*=3;
  }

}
