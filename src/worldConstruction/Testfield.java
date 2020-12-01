package worldConstruction;

import java.awt.Color;
import java.util.LinkedList;

import drawable.DPAreaPolygon;
import drawable.Pnt2D;
import drawable.Vec2D;

public class Testfield {

  /*private static String which(double stAngle, double ndAngle){
    int erg = (int)(Math.abs(stAngle - ndAngle)-90)%360;
    System.out.println(erg);
    if(erg>180){
      return "First";
    }else if(erg<180){
      return "Second";
    }else{
      return "Unknown result. error";
    }
  }*/
  
  public static void main(String[] args) {
    
    
    /*Pnt2D a = new Pnt2D(0,0);
    Pnt2D b = new Pnt2D(2,1);
    
    double xDist = b.x-a.x;
    double yDist = b.y-a.y;
    yDist*=-1;
    double erg1 = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;

    
    b = new Pnt2D(-1,-1);
    xDist = b.x-a.x;
    yDist = b.y-a.y;
    yDist*=-1;
    double erg2 = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;

    
    b = new Pnt2D(-1,1);
    xDist = b.x-a.x;
    yDist = b.y-a.y;
    yDist*=-1;
    double erg3 = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;
    
    b = new Pnt2D(1,-1);
    xDist = b.x-a.x;
    yDist = b.y-a.y;
    yDist*=-1;
    double erg4 = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;

    System.out.println(erg1+" vs "+erg2+": "+which(erg1, erg2));
    System.out.println(erg1+" vs "+erg3+": "+which(erg1, erg3));
    System.out.println(erg1+" vs "+erg4+": "+which(erg1, erg4));
    System.out.println(erg2+" vs "+erg3+": "+which(erg2, erg3));
    System.out.println(erg2+" vs "+erg4+": "+which(erg2, erg4));
    
    System.out.println("---");
    
    a = new Pnt2D(0,5);
    b = new Pnt2D(0,-5);
    
    xDist = b.x-a.x;
    yDist = b.y-a.y;
    yDist*=-1;
    double ergA = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;
    
    a = new Pnt2D(2,-2);
    b = new Pnt2D(-2,0);
    
    xDist = b.x-a.x;
    yDist = b.y-a.y;
    yDist*=-1;
    double ergB = ((Math.atan2(yDist, xDist)* (180 / Math.PI))+360)%360;
    
    System.out.println(which(ergA,ergB));
    
    System.out.println("*********");*/
    
    
    Color col = new Color(0,0,0,0);
    
    Pnt2D pa = new Pnt2D(0,-5);
    Pnt2D pb = new Pnt2D(-5,0);
    Pnt2D pc = new Pnt2D(0,5);
    Pnt2D pd = new Pnt2D(5,0);
    
    Pnt2D ce = new Pnt2D(0,0);
    Pnt2D ta1 = new Pnt2D(2,1);
    Pnt2D ta2 = new Pnt2D(-1,-1);
    Pnt2D ta3 = new Pnt2D(1,1);
    
    Vec2D tLine1 = new Vec2D(ce,ta1,col);
    Vec2D tLine2 = new Vec2D(ce,ta2,col);
    Vec2D tLine3 = new Vec2D(ce,ta3,col);
    
    Vec2D la = new Vec2D(pa,pb,col);
    Vec2D lb = new Vec2D(pb,pc,col);
    Vec2D lc = new Vec2D(pc,pd,col);
    Vec2D ld = new Vec2D(pd,pa,col);
    System.out.println("IntersectsFromRight:");
    System.out.println("tLine1 on tLine2: "+Vec2D.intersectsFromRight(tLine1, tLine2));
    System.out.println("tLine1 on tLine3: "+Vec2D.intersectsFromRight(tLine1, tLine3));
    System.out.println();
    System.out.println("la on lb: "+Vec2D.intersectsFromRight(lb, la));
    System.out.println("la on lb: "+Vec2D.intersectsFromRight(lc, lb));
    System.out.println("la on lb: "+Vec2D.intersectsFromRight(ld, lc));
    System.out.println("la on lb: "+Vec2D.intersectsFromRight(la, ld));
    System.out.println("");
    
    System.out.println("pa on la: "+Vec2D.isPointOnLine(pa, la));
    System.out.println("la on la: "+Vec2D.isIntersecting(la, la));
    LinkedList<Pnt2D> ll = new LinkedList<>();
    
    ll.add(pa);
    ll.add(pb);
    ll.add(pc);
    ll.add(pd);
    
    DPAreaPolygon dpap = new DPAreaPolygon(ll,new Pnt2D(0,0),col,"Test");
    
    pa = new Pnt2D(3,-6);
    pb = new Pnt2D(-3,-6);
    pc = new Pnt2D(-3,3);
    pd = new Pnt2D(3,3);
    
    la = new Vec2D(pa,pb,col);
    lb = new Vec2D(pb,pc,col);
    lc = new Vec2D(pc,pd,col);
    ld = new Vec2D(pd,pa,col);
    
    ll = new LinkedList<>();
    
    ll.add(pa);
    ll.add(pb);
    ll.add(pc);
    ll.add(pd);
    
    DPAreaPolygon dpap2 = new DPAreaPolygon(ll, new Pnt2D(0,0),col,"Test2");
    
    System.out.println("dpap and dpap2: "+dpap2.contains(dpap));
    DPAreaPolygon.merge(dpap, dpap2);
    
    
    //System.out.println("Contains 0.001:5 "+dpap.contains(new Pnt2D(1,-5)));
    //System.out.println("Contains itself: "+dpap.contains(dpap));*/
    
    /*BufferedImage[][] textures = new BufferedImage[10][5];
    
    URL[] urls = new URL[22];
    
    urls[0]=Testfield.class.getResource("/sur_Molt.png");
    urls[1]=Testfield.class.getResource("/sur_Rock.png");
    urls[2]=Testfield.class.getResource("/sur_Metal.png");
    urls[3]=Testfield.class.getResource("/sur_Mineral.png");
    urls[4]=Testfield.class.getResource("/sur_Prec.png");
    urls[5]=Testfield.class.getResource("/gas_Oxy.png");
    urls[6]=Testfield.class.getResource("/gas_Hel.png");
    urls[7]=Testfield.class.getResource("/gas_Hydro.png");
    urls[8]=Testfield.class.getResource("/atmo_Oxy.png");
    urls[9]=Testfield.class.getResource("/atmo_Hel.png");
    urls[10]=Testfield.class.getResource("/atmo_Hydro.png");
    urls[11]=Testfield.class.getResource("/shadow.png");
    
    urls[12]=Testfield.class.getResource("/star_r.png");
    urls[13]=Testfield.class.getResource("/star_o.png");
    urls[14]=Testfield.class.getResource("/star_w.png");
    urls[15]=Testfield.class.getResource("/star_b.png");
    urls[16]=Testfield.class.getResource("/star_u.png");
    
    urls[17]=Testfield.class.getResource("/uniform_density.png");
    urls[18]=Testfield.class.getResource("/barred_density.png");
    urls[19]=Testfield.class.getResource("/spiral_density.png");
    urls[20]=Testfield.class.getResource("/halo_density.png");
    urls[21]=Testfield.class.getResource("/irregular_density.png");
    
    /*try {
      textures[0][0] = ImageIO.read(urls[0]); //molten
      textures[1][0] = ImageIO.read(urls[1]); //solid: Rock
      textures[1][1] = ImageIO.read(urls[2]);  //  Metal  
      textures[1][2] = ImageIO.read(urls[3]);  //  Mineral
      textures[1][3] = ImageIO.read(urls[4]);  //  Prec
      textures[2][0] = ImageIO.read(urls[5]);  //Gas: oxy
      textures[2][1] = ImageIO.read(urls[6]);  //  hel
      textures[2][2] = ImageIO.read(urls[7]);  //  hydro
      textures[3][0] = ImageIO.read(urls[8]);  //atmo: oxy
      textures[3][1] = ImageIO.read(urls[9]);  //  hel
      textures[3][2] = ImageIO.read(urls[10]);//  hsydro
      textures[4][0] = ImageIO.read(urls[11]);//shadow
      
      textures[5][0] = ImageIO.read(urls[12]);//stars: red
      textures[5][1] = ImageIO.read(urls[13]);//  orange
      textures[5][2] = ImageIO.read(urls[14]);//white
      textures[5][3] = ImageIO.read(urls[15]);//blue
      textures[5][4] = ImageIO.read(urls[16]);//ultra violet
      
      textures[6][0] = ImageIO.read(urls[1]);  //ring: Rock
      textures[6][1] = ImageIO.read(urls[2]);  //  metal
      textures[6][2] = ImageIO.read(urls[3]);  //  mineral
      textures[6][3] = ImageIO.read(urls[4]);  //  prec

      textures[7][0] = ImageIO.read(urls[1]);  //asteroids: rock
      textures[7][1] = ImageIO.read(urls[2]);  //  metal
      textures[7][2] = ImageIO.read(urls[3]);  //  mineral
      textures[7][3] = ImageIO.read(urls[4]);  //  prec
      
      textures[8][0] = ImageIO.read(urls[17]);//galaxyD: uniform
      textures[8][1] = ImageIO.read(urls[18]);//  barred
      textures[8][2] = ImageIO.read(urls[19]);//  spiral
      textures[8][3] = ImageIO.read(urls[20]);//  halo
      textures[8][4] = ImageIO.read(urls[21]);//  irregular
      
      textures[9][0] = ImageIO.read(urls[17]);//galaxy_i: uniform
      textures[9][1] = ImageIO.read(urls[18]);//  barred
      textures[9][2] = ImageIO.read(urls[19]);//  spiral
      textures[9][3] = ImageIO.read(urls[20]);//  halo
      textures[9][4] = ImageIO.read(urls[21]);//  irregular
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    long ts = System.currentTimeMillis();
    
    //Long l = new Long("4273");
    //Long l = new Long("42514245245273");
    Random r = new Random();
    Long l = r.nextLong();
    CBSun s = new CBSun(textures[5], 0, 0, 1, l, Double.POSITIVE_INFINITY, tid);
    String t = s.properties.get("Type");
    
    double temp = Double.parseDouble(s.properties.get("Temperature"));
    
    double sysSize = CBPlanet.getRawSize(temp);
    
    int erg= Integer.parseInt(s.properties.get("Planets"));
    
    LinkedList<CBPlanet> planets = new LinkedList<CBPlanet>();
    LinkedList<CBMoon> moons = new LinkedList<CBMoon>();
    LinkedList<CBRing> rings = new LinkedList<CBRing>();
    
    System.out.println(temp+":"+t+" -> "+ CBPlanet.fSize(temp)+" -> "+erg+" Planets\n\n\n");
    System.out.println(s.getID());
    for(int i=0;i<erg;i++){
      double bDist = sysSize/erg;
      double dist=bDist+r.nextDouble()*(bDist*0.5)-(bDist*0.25);
      dist+=bDist*i;
      
      planets.add(new CBPlanet(textures, 1, r.nextLong(), Double.POSITIVE_INFINITY, i, new DPoint(dist,0), s));
      
      if(planets.getLast().properties.get("Ring").equals("true")){
        rings.add(new CBRing(textures[6], 1, Double.POSITIVE_INFINITY,planets.getLast()));
      }
      
      
      int mErg = Integer.parseInt(planets.getLast().properties.get("Moons"));
      for(int j=0;j<mErg;j++){
        double d = Double.parseDouble(planets.getLast().properties.get("SDistance"));
        moons.add(new CBMoon(textures, 1, r.nextLong(), Double.POSITIVE_INFINITY, j, d, planets.getLast()));
      }
    }
    

    ts = System.currentTimeMillis()-ts;
    
    for(CBPlanet p:planets){
      System.out.println(p.getID().toString());
      for(String str:p.properties.keySet()){
        System.out.println(str+":  "+p.properties.get(str));
      }
      System.out.println("Atmo Comp:");
      for(String str:p.getAtmoComp().keySet()){
        System.out.println("   "+str+":  "+p.getAtmoComp().get(str));
      }
      System.out.println("Crust Comp:");
      for(String str:p.getCrustComp().keySet()){
        System.out.println("   "+str+":  "+p.getCrustComp().get(str));
      }
      System.out.println("Core Comp:");
      for(String str:p.getCoreComp().keySet()){
        System.out.println("   "+str+":  "+p.getCoreComp().get(str));
      }
      System.out.println("\n---\n---\n\n");
    }
    
    System.out.println(moons.size());
    
    for(CBMoon p:moons){
      System.out.println(p.getID().toString());
      for(String str:p.properties.keySet()){
        System.out.println(str+":  "+p.properties.get(str));
      }
      System.out.println("Atmo Comp:");
      for(String str:p.getAtmoComp().keySet()){
        System.out.println("   "+str+":  "+p.getAtmoComp().get(str));
      }
      System.out.println("Crust Comp:");
      for(String str:p.getCrustComp().keySet()){
        System.out.println("   "+str+":  "+p.getCrustComp().get(str));
      }
      System.out.println("Core Comp:");
      for(String str:p.getCoreComp().keySet()){
        System.out.println("   "+str+":  "+p.getCoreComp().get(str));
      }
      System.out.println("\n---\n---\n\n");
    }
    
    for(CBRing ri:rings){
      System.out.println(ri.getID().toString());
      for(String str:ri.properties.keySet()){
        System.out.println(str+":  "+ri.properties.get(str));
      }
      System.out.println("Comp:");
      for(String str:ri.getComp().keySet()){
        System.out.println("   "+str+":  "+ri.getComp().get(str));
      }
      System.out.println("\n---\n---\n\n");
    }
    
    System.out.println(ts);
    System.out.println(Long.MAX_VALUE);*/
  }

}
