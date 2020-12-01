package drawable;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import worldConstruction.TiledID;
import worldConstruction.ValueRamp;

public class CBPlanet extends Rasterable{

  protected Rasterable sun;
  protected Compound atmoComp;
  protected Compound crustComp;
  protected Compound coreComp;
  
  private static final double SLOPE = 0.0000001; // 2 null is grad dazu
  protected static final int THRESH_LAVA=800;
  protected static final int THRESH_SOLID=150;
  protected static final int THRESH_GAS_SIZE=80000;
  protected static final int THRESH_HOT_SIZE=180000;
  private static final int MIN_SIZE=2000;
  private static final int MAX_SIZE=200000;
  protected static final String[] solidMatTypes = {"Rock","Metal","Mineral","Precious Metal"};
  protected static final String[] gasMatTypes = {"Oxygen","Helium","Hydrogen"};
  
  public CBPlanet(BufferedImage[][] biChoices, double preScale, long s, double mS, int relPos, /*double dist*/Pnt2D pos, Rasterable par) {
    super(biChoices[0], pos.x, pos.y, preScale, s, mS, par.getID(), 0, 0);
    
    TiledID pid = par.getID();
    
    //this.pos = pos;
    double dist = Math.hypot(pos.x, pos.y);
    
    id = new TiledID(pid.toString());
    id.addTrabant(relPos);
    sun=par;
    
    atmoComp = new Compound();
    crustComp = new Compound();
    coreComp = new Compound();
    
    Random r = new Random();
    r.setSeed(s);
    
    ValueRamp sizeRamp = new ValueRamp();
    ValueRamp gaussRamp = new ValueRamp();
    ValueRamp gasGRamp = new ValueRamp();
    
    sizeRamp.changeEnds(0, 0.01);
    
    sizeRamp.addHandle(0.1, 0.3);
    sizeRamp.addHandle(0.25,0.5);
    sizeRamp.addHandle(0.45,1);
    sizeRamp.addHandle(0.5, 1.2);
    sizeRamp.addHandle(0.55,1);
    sizeRamp.addHandle(0.75,0.3);
    sizeRamp.addHandle(0.85,0.15);
    
    gaussRamp.addHandle(0.25, 0.4);
    gaussRamp.addHandle(0.75, 0.6);
    
    gasGRamp.addHandle(0.85, 0);
    gasGRamp.addHandle(0.925, 0.2);
    
    //----
    //----
    
    properties.put("Kind", "Planet");
    properties.put("SDistance",dist+"");
    properties.put("SDUnit", "km");
    
    int sunTemp=Integer.parseInt(sun.properties.get("Temperature"));
    double temperature = fTemp(dist,sunTemp);
    

    
    properties.put("Temperature", temperature+"");
    properties.put("TUnit", "k");
    
    double relativeSizeMedian = sizeRamp.getValue(dist/getRawSize(sunTemp));
    double deviation = gaussRamp.getValue(r.nextDouble())*0.2;
    int maxSize = (int)Math.min(MAX_SIZE, Double.parseDouble(sun.properties.get("Size"))*20000+MIN_SIZE);
    int size = (int)((deviation+relativeSizeMedian)*maxSize)+MIN_SIZE;
    if(size<0)size*=-1;
    properties.put("Size", size+"");
    properties.put("SUnit", "km");
    
    String type;
    boolean atmo=false;

    if(size>THRESH_HOT_SIZE){
      type="Hot Gasgiant";
      atmo=true;
    }else if(size>THRESH_GAS_SIZE){
      type="Gasgiant";
      atmo=true;
    }else{
      if(temperature>THRESH_LAVA*1.5){
        type="Molten";
      }else if(temperature>THRESH_LAVA){
        if(r.nextDouble()<0.65){
          type="Molten";
        }else{
          type="Crust";
        }
      }else if(temperature<THRESH_SOLID){
        if(r.nextDouble()<0.65){
          type="Solid";
        }else{
          type="Crust";
        }
      }else{
        type="Crust";
      }
    }
    
    properties.put("Type", type);
    
    if(!atmo){
      double atmoThresh = deviation+relativeSizeMedian;
      if(r.nextDouble()<atmoThresh+0.15){
        atmo=true;
      }
    }
    
    properties.put("Atmosphere", atmo+"");

    boolean fluid=false;
    double fluidChance = fFluid(dist,sunTemp);
    if(r.nextDouble()<fluidChance && (type.equals("Crust")|| type.equals("Solid"))){
      fluid=true;
    }
    
    properties.put("Fuild", fluid+"");
    
    String t;
    if(temperature>273.15){
      t="Fluid";
    }else{
      t="Solid";
    }
    
    properties.put("FState", t);
    
    boolean magnet=false;
    
    if(type.equals("Crust") || type.equals("Molten")){
      if(r.nextDouble()<0.75){
        magnet=true;
      }
    }
    
    properties.put("Magnetfield", magnet+"");
    
    boolean rings=false;
    
    if(r.nextDouble()<0.15){
      rings=true;
    }
    
    properties.put("Ring", rings+"");
    
    boolean livable=false;
    
    if(fluid && magnet && atmo){
      if(temperature > 223.15 && temperature < 373.15)
      livable=true;
    }
    
    properties.put("Livable", livable+"");
    
    //moonCount=size  15 monde -> maxSize/15  size dadurch
    
    int moonThresh = MAX_SIZE/15;
    int moonCount=size/moonThresh;
    
    properties.put("Moons", moonCount+"");
    
    String[] matPointers = new String[2];
    
    if(atmo){
      matPointers[0]=fillCompound(atmoComp, gasMatTypes, r);
    }
    if(!type.equals("Gasgiant") && !type.equals("Hot Gasgiant")){
      matPointers[1]=fillCompound(crustComp, solidMatTypes, r);
      properties.put("Main Component", matPointers[1]);
      fillCompound(coreComp, solidMatTypes, r);
    }else{
      properties.put("Main Component", matPointers[0]);
    }
    
    int aggPointer=0;
    int smPointer=0;
    int atmPointer=-1;
    
    String prop = properties.get("Type");
    if(prop.equals("Molten")){
      aggPointer=0;
    }else if(prop.equals("Crust") || prop.equals("Solid")){
      aggPointer=1;
    }else{
      aggPointer=2;
    }
    
    if(aggPointer==0){
      smPointer=0;
    }else if(aggPointer==1){
      if(matPointers[1].equals("Rock")){
        smPointer=0;
      }else if(matPointers[1].equals("Metal")){
        smPointer=1;
      }else if(matPointers[1].equals("Mineral")){
        smPointer=2;
      }else if(matPointers[1].equals("Precious Metal")){
        smPointer=3;
      }
    }else{
      if(matPointers[0].equals("Oxygen")){
        smPointer=0;
        atmPointer=0;
      }else if(matPointers[0].equals("Helium")){
        smPointer=1;
        atmPointer=1;
      }else if(matPointers[0].equals("Hydrogen")){
        smPointer=2;
        atmPointer=2;
      }
    }
    
    if(aggPointer!=2){
      if(properties.get("Atmosphere").equals("true")){
        if(matPointers[0].equals("Oxygen")){
          atmPointer=0;
        }else if(matPointers[0].equals("Helium")){
          atmPointer=1;
        }else if(matPointers[0].equals("Hydrogen")){
          atmPointer=2;
        }
      }
    }
    
    layers.addLast(biChoices[aggPointer][smPointer]);
    
    
    
    if(atmPointer>=0){
      layers.addLast(biChoices[3][atmPointer]);
    }
    layers.addLast(biChoices[4][0]);

      BufferedImage reference = layers.getFirst();
      bounds = new Rectangle2D.Double(-reference.getWidth()/2, -reference.getHeight()/2, reference.getWidth(), reference.getHeight());
    
    //TODO layer dependent affines
        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
    naturalScale*=size;
    naturalScale/=layers.getFirst().getWidth();
    
    buildBounds();

    // fSpeed: sqrt((Gm)/R)   G=gravityConst, R distance to center, m Mass
  }
  
  public static String fillCompound(Compound c, String[] choices, Random r){
    int amt = (int)Math.min((r.nextDouble()*choices.length)+2,choices.length);
    HashSet<Integer> used = new HashSet<>();
    String s="";
    int idx=0;
    double left=1;
    for(int i=0;i<amt;i++){
      do{
        idx = (int)(r.nextDouble()*choices.length);
      } while(used.contains(idx));
      
      used.add(idx);
      
      double percentage;
      if(used.size()<amt){
        percentage=r.nextDouble()*left;
      }else{
        percentage=left;
      }
      left-=percentage;
      c.addComponent(choices[idx], percentage);
      s=choices[idx];
    }
    return s;
  }

  protected double fTemp(double x, double temp){
    //returns absolute temperature
    return  ((1/(x + 1/SLOPE))*(1/SLOPE))*(temp/5);
  }
  
  public static double fSize(double temp){
    //returns absolute recommended SolSys size
    return Math.pow(10, Math.floor(Math.log10(getRawSize(temp))+1));
  }
  
  public static double fSizeRound(double temp){
    //same as fSize, but rounded inseat of strict upgoing;
    return Math.pow(10, Math.round(Math.log10(getRawSize(temp))));
  }
  
  public static double getRawSize(double temp){
    return (temp-500)/(500*SLOPE);
  }
  
  protected double fFluid (double x, double temp){
    //returns chance of water in 0-1 interval
    return (1500-fTemp(x,temp))/1500;
  }
  
  public HashMap<String, Double> getAtmoComp(){
    return atmoComp.getComponents();
  }
  
  public HashMap<String, Double> getCrustComp(){
    return crustComp.getComponents();
  }
  
  public HashMap<String, Double> getCoreComp(){
    return coreComp.getComponents();
  }
}
