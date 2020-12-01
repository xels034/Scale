package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import worldConstruction.TiledID;

public class CBBlackHole extends Rasterable{

  public CBBlackHole(BufferedImage[] biChoices, double x, double y, double preScale, long s, double mS, TiledID pid, int rx, int ry) {
    super(biChoices, x, y, preScale, s, mS, pid, rx, ry);
    
    Random r = new Random();
        r.setSeed(seed);
        double d = r.nextDouble();
        d*=10;
        properties.put("Kind", "Supermassive Black Hole");
        properties.put("Size",(long)d+"");
        properties.put("SUnit", "AU");
        d = r.nextDouble();
        d*=15;
        d++;
        properties.put("Age", (long)d+"");//in billion years, 10^9

        properties.put("Type", "Supermassive");
        
        d=r.nextDouble();
        d*=biChoices.length;
        layers.add(biChoices[(int)d]);

        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
        buildBounds();
  }
}
