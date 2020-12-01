package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import worldConstruction.TiledID;

public class CBStar extends Rasterable{

  public CBStar(BufferedImage[] biChoices, double x, double y, double preScale, long s, double mS, TiledID pid, int rx, int ry) {
    super(biChoices, x, y, preScale, s, mS, pid, rx, ry);
    
    
    Random r = new Random();
        r.setSeed(seed);
        properties.put("Kind", "Star");
        
        double d = r.nextDouble();
        d*=15;
        d++;
        properties.put("Age", (long)d+"");//in billion years, 10^9
        
        d=r.nextDouble();
        double sr = r.nextDouble();

        d*=biChoices.length;
        String t="";
        switch((int)d){
        case 0:
          t="Class M";
          sr*=0.2;
          sr+=0.5;
          break;
        case 1:
          t="Class K";
          sr*=0.2;
          sr+=0.7;
          break;
        case 2:
          t="Class F";
          sr*=0.25;
          sr+=1.15;
          break;
        case 3:
          t="Class B";
          sr*=4.8;
          sr+=1.8;
          break;
        case 4:
          t="Class O";
          sr*=10;
          sr+=6.6;
          break;
        }
        
        properties.put("Size",String.format("%.3f", sr).replace(',', '.'));//in solar radii
        properties.put("SUnit", "R");
        
        properties.put("Type", t);
        layers.add(biChoices[(int)d]);
        
        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
        buildBounds();
        minScale*=3; //because the star tex is so damn small
  }



}
