package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import worldConstruction.TiledID;

public class CBGalaxy extends Rasterable{

  public CBGalaxy(BufferedImage[] biChoices, double x, double y, double preScale, long s, double mS, TiledID pid, int rx, int ry) {
    super(biChoices, x, y, preScale, s, mS, pid, rx, ry);
    
    Random r = new Random();
        r.setSeed(seed);
        double d = r.nextDouble();
        d*=20;
        d+=15;
        properties.put("Kind", "Galaxy");
        properties.put("Size",(long)d+"");
        properties.put("SUnit", "kpc");
        d = r.nextDouble();
        d*=15;
        d++;
        properties.put("Age", (long)d+"");//in billion years, 10^9
        d=r.nextDouble();
        d*=biChoices.length;
        String t="";
        switch((int)d){
        case 0:
          t="Uniform";
          break;
        case 1:
          t="Barred-spiral";
          break;
        case 2:
          t="Spiral";
          break;
        case 3:
          t="Halo";
          break;
        case 4:
          t="Irregular";
          break;
        }
        
        properties.put("Type", t);
        layers.add(biChoices[(int)d]);

        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);

        
        buildBounds();
        minScale*=1; //bc the star tex is half empty
  }
}
