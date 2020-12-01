package worldConstruction;

public class TiledID {

  private String id;
  public boolean isContainer;
  private static int cLen=4;
  public static final int MODE_UNIVERSE=0;
  public static final int MODE_GALAXY=1;
  public static final int MODE_STELLAR=2;
  
  //  relative position 
  //  in the supertile,
  //    up to g=8        same for galaxy mode
  //          v                 v
  //  0005|0013:F80A6CB2:030|008 || 0008|0001:FA0763CD:531|001 || 0:1:4
  //      ^            ^                      ^
  //  Position of  relative position          Systemic position. first describes sun(s)
  //  g=0 tile    (0-1000) in tile          2nd position are planets, 3rd moons
  
  //  05|13:008AD:067|999 also possible
  
  //  0005|0013:F80A6CB2:030|008 || 0008|0001:FA0763CD:531|001 || s008|1234:fa67ddc8:1234|4321
  
  
  // % marks datainput
  // d stands for decimal
  // x for hexadecimal
  // e.g. %04d means 4 decimal places with leading zeroes
  
  public TiledID(int x, int y){
    //new root tile
    id = String.format("%0"+cLen+"d|%0"+cLen+"d", x,y);
    isContainer=true;
  }
  
  public void addCenterMass(int x){
    id += " || "+x;
    isContainer=false;
  }
  
  public void addTrabant(int x){
    id += ":"+x;
    isContainer=false;
  }

  public TiledID(TiledID d, int relPos){
    //new subtile
    
    String s = d.toString();
    
    if(s.charAt(s.length()-1-4)=='|'){
      id=s+":";
    }else if(!d.isContainer){
      id=s.substring(0,s.length()-(cLen*2+2));
    }else{
      id=s;
    }

    id +=String.format("%x", relPos);
    isContainer=true;
  }
  
  public TiledID(TiledID inherit, int x, int y){
    id = inherit+" || "+new TiledID(x,y);
    isContainer=true;
  }
  
  public TiledID(TiledID inherit, int x, int y, boolean flag){
    id = inherit+" || s"+new TiledID(x,y);
    isContainer=true;
  }
  
  public TiledID (String id){
    //copy id
    this.id=id;
    isContainer=true;
  }
  
  public void addPosInTile(int x, int y){
    //new object in tile
    if(isContainer){
      id+=String.format(":%04d|%04d", x,y);
      isContainer=false;
    }
  }
  
  @Override
  public String toString(){
    return id;
  }
  
  public static int getMode(String d){
    if(d.split("\\|\\|").length==2){
      return 1;
    }if(d.split("\\|\\|").length==3){
      return 2;
    }else{
      return 0;
    }
  }
  
  public static int getGeneration(String d){
    //to escape the | character, you need not only \, but \\! srsly, dafuq?
    
    String[] gens = d.split("\\|\\|");
    if(gens.length==3){
      //for sol sys, generations are vastly different
      return gens[gens.length-3].split(":").length;
    }else{
      String[] buff = gens[gens.length-1].split(":");
      if(buff.length==3){
        return buff[buff.length-2].length();
      }else{
        return 0;
      }
    }
  }
}
