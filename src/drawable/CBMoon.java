package drawable;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import worldConstruction.TiledID;

public class CBMoon extends CBPlanet{

  private CBPlanet parent;
  
  private static final int MIN_SIZE=250;
  private static final int MAX_SIZE=7500;
  
  public CBMoon(BufferedImage[][] biChoices, double preScale, long s, double mS, int relPos, double dist, CBPlanet par) {
    super(biChoices, preScale, s, mS, relPos, new Pnt2D(dist,0), par.sun);
    
    TiledID pid = par.getID();
    
    properties.clear();
    
    id = new TiledID(pid.toString());
    id.addTrabant(relPos);
    
    this.sun = par.sun;
    
    parent=par;
    atmoComp = new Compound();
    crustComp = new Compound();
    coreComp = new Compound();
    
    Random r = new Random();
    r.setSeed(s);
    
    /*ValueRamp sizeRamp = new ValueRamp();
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
    gasGRamp.addHandle(0.925, 0.2);*/
    
    //----
    //----
    
    properties.put("Kind", "Moon");
    properties.put("SDistance",dist+"");
    properties.put("SDUnit", "km");
    
    int sunTemp=Integer.parseInt(sun.properties.get("Temperature"));
    double temperature = fTemp(dist,sunTemp);
    

    
    properties.put("Temperature", temperature+"");
    properties.put("TUnit", "k");
    
    //TODO: fancy code. but boring results.
    /*double relativeSizeMedian = sizeRamp.getValue(dist/getRawSize(sunTemp));//TODO was fSize
    
    double deviation = gaussRamp.getValue(r.nextDouble())*1;
    int maxSize = (int)Math.min(MAX_SIZE, Double.parseDouble(parent.properties.get("Size"))/15);
    
    
    int size = (int)   ((deviation+relativeSizeMedian)*maxSize)   +MIN_SIZE;
    if(size<0)size*=-1;*/
    
    int size = MIN_SIZE+(int)(r.nextDouble()*Double.parseDouble((parent.properties.get("Size")))/10);
    
    if(size>MAX_SIZE){
      size = MAX_SIZE-(int)(r.nextDouble()*1500)+750;
    }
    
    properties.put("Size", size+"");
    properties.put("SUnit", "km");
    
    
    double pSize = Double.parseDouble(parent.properties.get("Size"));
    double pDist = r.nextDouble()*pSize*3+(pSize*2);
    
    properties.put("PDistance",pDist+"");
    properties.put("PUnit", "km");
    
    pos = new Pnt2D(dist+pDist,0);
    
    String type;
    boolean atmo=false;


    if(temperature>THRESH_LAVA*1.5){
      type="Molten";
    }else if(temperature>THRESH_LAVA){
      if(r.nextDouble()<0.35){
        type="Molten";
      }else{
        type="Crust";
      }
    }else if(temperature<THRESH_SOLID){
      if(r.nextDouble()<0.85){
        type="Solid";
      }else{
        type="Crust";
      }
    }else{
      type="Crust";
    }
    
    
    properties.put("Type", type);
    

    double d = (double)(size)/(double)MAX_SIZE;
    if(r.nextDouble()<d){
      atmo=true;
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
    
    boolean livable=false;
    
    if(fluid && magnet && atmo){
      if(temperature > 223.15 && temperature < 373.15)
      livable=true;
    }
    
    properties.put("Livable", livable+"");
    
    String[] matPointers = new String[2];
    
    if(atmo){
      matPointers[0]=fillCompound(atmoComp, gasMatTypes, r);
    }
    matPointers[1]=fillCompound(coreComp, solidMatTypes, r);

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
    
    layers.addLast(biChoices[aggPointer][smPointer]);//surface
    
    if(atmPointer>=0){
      layers.addLast(biChoices[3][atmPointer]);//atmosphere, if present
    }
    layers.addLast(biChoices[4][0]);//shadow
    
        drawInfo = new AffineTransform();
        drawInfo.translate(-layers.getFirst().getWidth()/2, -layers.getFirst().getHeight()/2);
        
        
    naturalScale=preScale*size;
    naturalScale/=layers.getFirst().getWidth();
    
    buildBounds();
    // fSpeed: sqrt((Gm)/R)   G=gravityConst, R distance to center, m Mass
  }


}
