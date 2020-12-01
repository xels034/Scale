package gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;

import javax.imageio.ImageIO;

import worldConstruction.Testfield;

public class ImageManager {

  private URL[][][] addresses;
  private SoftReference<BufferedImage> [][][] cache;
  
  
  @SuppressWarnings("unchecked")
  public ImageManager(){
    addresses = new URL[1][15][5];
    
    addresses[0][0][0]=Testfield.class.getResource("/uniform_density.png");
    addresses[0][0][1]=Testfield.class.getResource("/barred_density.png");
    addresses[0][0][2]=Testfield.class.getResource("/spiral_density.png");
    addresses[0][0][3]=Testfield.class.getResource("/halo_density.png");
    addresses[0][0][4]=Testfield.class.getResource("/irregular_density.png");
    
    addresses[0][1][0]=Testfield.class.getResource("/uniform_img.png");
    addresses[0][1][1]=Testfield.class.getResource("/barred_img.png");
    addresses[0][1][2]=Testfield.class.getResource("/spiral_img.png");
    addresses[0][1][3]=Testfield.class.getResource("/halo_img.png");
    addresses[0][1][4]=Testfield.class.getResource("/irregular_img.png");
    
    addresses[0][2][0]=Testfield.class.getResource("/star_r.png");
    addresses[0][2][1]=Testfield.class.getResource("/star_o.png");
    addresses[0][2][2]=Testfield.class.getResource("/star_w.png");
    addresses[0][2][3]=Testfield.class.getResource("/star_b.png");
    addresses[0][2][4]=Testfield.class.getResource("/star_u.png");
    
    addresses[0][3][0]=Testfield.class.getResource("black_hole.png");
    
    addresses[0][4][0]=Testfield.class.getResource("/sun_r.png");
    addresses[0][4][1]=Testfield.class.getResource("/sun_o.png");
    addresses[0][4][2]=Testfield.class.getResource("/sun_w.png");
    addresses[0][4][3]=Testfield.class.getResource("/sun_b.png");
    addresses[0][4][4]=Testfield.class.getResource("/sun_u.png");
    
    addresses[0][5][0]=Testfield.class.getResource("/sur_Molt.png");
    
    addresses[0][6][0]=Testfield.class.getResource("/sur_Rock.png");
    addresses[0][6][1]=Testfield.class.getResource("/sur_Metal.png");
    addresses[0][6][2]=Testfield.class.getResource("/sur_Mineral.png");
    addresses[0][6][3]=Testfield.class.getResource("/sur_Prec.png");
    
    addresses[0][7][0]=Testfield.class.getResource("/gas_Oxy.png");
    addresses[0][7][1]=Testfield.class.getResource("/gas_Hel.png");
    addresses[0][7][2]=Testfield.class.getResource("/gas_Hydro.png");
    //---
    addresses[0][8][0]=Testfield.class.getResource("/fBlue_low.png");
    addresses[0][8][1]=Testfield.class.getResource("/fBlue_mid.png");
    addresses[0][8][2]=Testfield.class.getResource("/fBlue_high.png");
    
    addresses[0][9][0]=Testfield.class.getResource("/fGreen_low.png");
    addresses[0][9][1]=Testfield.class.getResource("/fGreen_mid.png");
    addresses[0][9][2]=Testfield.class.getResource("/fGreen_high.png");
    
    addresses[0][10][0]=Testfield.class.getResource("/fRed_low.png");
    addresses[0][10][1]=Testfield.class.getResource("/fRed_mid.png");
    addresses[0][10][2]=Testfield.class.getResource("/fRed_high.png");
    //---
    addresses[0][11][0]=Testfield.class.getResource("/atmo_Oxy.png");
    addresses[0][11][1]=Testfield.class.getResource("/atmo_Hel.png");
    addresses[0][11][2]=Testfield.class.getResource("/atmo_Hydro.png");
    
    addresses[0][12][0]=Testfield.class.getResource("/ring_Rock.png");
    addresses[0][12][1]=Testfield.class.getResource("/ring_Metal.png");
    addresses[0][12][2]=Testfield.class.getResource("/ring_Mineral.png");
    addresses[0][12][3]=Testfield.class.getResource("/ring_Prec.png");
    addresses[0][12][4]=Testfield.class.getResource("/ring_Molten.png");
    
    addresses[0][13][0]=Testfield.class.getResource("/asteroid_Rock.png");
    addresses[0][13][1]=Testfield.class.getResource("/asteroid_Metal.png");
    addresses[0][13][2]=Testfield.class.getResource("/asteroid_Mineral.png");
    addresses[0][13][3]=Testfield.class.getResource("/asteroid_Prec.png");
    
    addresses[0][14][0]=Testfield.class.getResource("/shadow.png");
    
    cache = new SoftReference[addresses.length][addresses[0].length][addresses[0][0].length];
  }
  
  @SuppressWarnings("unchecked")
  public ImageManager(URL[][][] adr){
    addresses=adr;
    cache = new SoftReference[adr.length][adr[0].length][adr[0][0].length];
  }
  
  public BufferedImage get(int x, int y, int z){
    if(cache[x][y][z]!=null){
      return cache[x][y][z].get();
    }else{
      BufferedImage ret = null;
      try{
        ret = ImageIO.read(addresses[x][y][z]);
        cache[x][y][z] = new SoftReference<>(ret);
      }catch (IOException ex){
        System.err.println(ex.getStackTrace());
      }
      return ret;
    }
  }
}
