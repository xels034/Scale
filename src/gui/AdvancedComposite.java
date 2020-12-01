package gui;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AdvancedComposite implements Composite{

  public static int MODE_MIX=0;
  public static int MODE_ADD=1;
  public static int MODE_MULTIPLY=2;
  public static int MODE_SCREEN=3;

  private int flag;
  private float alpha;
  
  private Blender bMix,bAdd,bMult,bScr;
  
  public AdvancedComposite(){
    flag=0;
    alpha=1f;
    initializeBlenders();
  }
  
  public AdvancedComposite(int f, float a){
    flag=f;
    alpha=a;
    initializeBlenders();
  }
  
  public AdvancedComposite derive(float a){
    return new AdvancedComposite(flag, a);
  }
  
  public void setAlpha(float a){
    alpha=a;
  }
  
  public void setMode(int m){
    flag=m;
  }
  
  private void initializeBlenders(){
    bMix = new Blender() {

      @Override
      public int[] blend(int[] bottom, int[]top) {
        int[] erg = new int[4];
        for(byte i=0;i<4;i++){
          erg[i]= (bottom[i]+top[i])/2;
          erg[i] = (int)(top[i] + (erg[i]-top[i])*alpha);
        }
        return erg;
      }
    };
    
    bAdd = new Blender() {
      @Override
      public int[] blend(int[] bottom, int[]top) {
        int[] erg = new int[4];
        for(byte i=0;i<4;i++){
          erg[i]= Math.min(255, bottom[i]+top[i]);
          erg[i] = (int)(top[i] + (erg[i]-top[i])*alpha);
        }
        return erg;
      }
    };
    
    bMult = new Blender() {
      float sB,sT;
      @Override
      public int[] blend(int[] bottom, int[]top) {
        int[] erg = new int[4];
        for(byte i=0;i<4;i++){
          sB = bottom[i]/255.0f;
          sT = top[i]/255.0f;
          erg[i]=(int)((sB*sT)*255);
          erg[i] = (int)(top[i] + (erg[i]-top[i])*alpha);
          //erg[i]=(bottom[i]*top[i])>>8;
        }
        return erg;
      }
    };
    
    bScr = new Blender() {
      float sB,sT;
      @Override
      public int[] blend(int[] bottom, int[]top) {
        int[] erg = new int[4];
        for(byte i=0;i<4;i++){
          sB = 1-(bottom[i]/255.0f);
          sT = 1-(top[i]/255.0f);
          erg[i]=(int)((1-(sB*sT))*255);
          erg[i] = (int)(top[i] + (erg[i]-top[i])*alpha);
        }
        return erg;
      }
    };
  }
  
  @Override
  public CompositeContext createContext(ColorModel arg0, ColorModel arg1,
      RenderingHints arg2) {
      return new AdvancedCompositeContext();
  }
  
  private class Blender{
    public int[] blend(int[] bottom, int[] top){
      return new int[0];
    }
  }
  

  private class AdvancedCompositeContext implements CompositeContext{
    
    private Blender bl;
    public AdvancedCompositeContext(){
      switch(flag){
      case 0:
        bl=bMix;
        break;
      case 1:
        bl=bAdd;
        break;
      case 2:
        bl=bMult;
        break;
      case 3:
        bl=bScr;
        break;
      }  
    }

    @Override
    public void compose(Raster arg0, Raster arg1, WritableRaster arg2) {
      int w = Math.min(arg0.getWidth(), arg1.getWidth());
      int h = Math.min(arg0.getHeight(), arg2.getHeight());
      
      int[] bottomCol = new int[4];
      int[] topCol = new int[4];
      
      for(int y=0;y<h;y++){
        for(int x=0;x<w;x++){
          arg0.getPixel(x, y, bottomCol);
          arg1.getPixel(x, y, topCol);
          arg2.setPixel(x, y, bl.blend(bottomCol, topCol));
        }
      }
    }
    
    @Override
    public void dispose() {
      bl=null;
    }  
  }
}
