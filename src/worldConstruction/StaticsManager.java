package worldConstruction;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import drawable.CBBlackHole;
import drawable.CBMoon;
import drawable.CBPlanet;
import drawable.CBRing;
import drawable.CBSun;
import drawable.Rasterable;
import drawable.DrawablePath;
import drawable.DPOrbitalPolygon;
import drawable.DPSelectionCursor;
import drawable.Pnt2D;

public class StaticsManager {

  private LinkedList<Rasterable> cBodies;
  private LinkedList<Rasterable> selectedBodies;
  
  private LinkedList<DrawablePath> staticPaths;
  private LinkedList<DrawablePath> cursors;
  
  private long seed;
  private Rectangle2D.Double sel;
  
  private TiledID id;
  
  private Pnt2D p;
  private double zF;
  
  private boolean wasSingle;
  private Object selectionFlag;
  
  private ReentrantLock l;
  
  private class Worker extends Thread{
    @Override
    public void run(){
      updateCycle();
    }
  }
  
  private void updateCycle(){
    while(true){
      try{
        synchronized(selectionFlag){
          selectionFlag.wait();
        }
        l.lock();
        updateSelection();
        l.unlock();
      } catch (InterruptedException x){
        System.out.println("Statics Manager interrupted");
      }
    }
  }
  
  public StaticsManager(long s, BufferedImage[] variants, TiledID d){
    sel= new Rectangle2D.Double(0,0,0,0);
    
    cBodies = new LinkedList<>();
    selectedBodies = new LinkedList<>();
    staticPaths = new LinkedList<>();
    cursors = new LinkedList<>();
    
    l = new ReentrantLock();
    selectionFlag=false;
    
    seed=s;
    Random r = new Random();
    
    r.setSeed(seed);
    id=d;  

    cBodies.add(new CBBlackHole(variants,0,0,0.00003,r.nextLong(),Double.POSITIVE_INFINITY,new TiledID(id,0,0),0,0));
    
    wasSingle=false;
    
    Worker w = new Worker();
    w.start();
  }
  
  public StaticsManager(long s, BufferedImage[][] variants, TiledID d){
    sel= new Rectangle2D.Double(0,0,0,0);
    
    cBodies = new LinkedList<>();
    selectedBodies = new LinkedList<>();
    staticPaths = new LinkedList<>();
    cursors = new LinkedList<>();
    
    l = new ReentrantLock();
    selectionFlag=false;
    
    seed=s;
    Random r = new Random();
    
    r.setSeed(seed);
    id=d;
    
    CBSun cbs = new CBSun(variants[5], 0, 0, 1.0, seed, Double.POSITIVE_INFINITY, d);
    
    double temp = Double.parseDouble(cbs.properties.get("Temperature"));
    double sysSize = CBPlanet.getRawSize(temp);
    int erg= Integer.parseInt(cbs.properties.get("Planets"));
    
    LinkedList<CBPlanet> planets = new LinkedList<>();
    LinkedList<CBMoon> moons = new LinkedList<>();
    LinkedList<CBRing> rings = new LinkedList<>();
    
    ValueRamp vr = new ValueRamp();
    vr.changeEnds(1, 0.1);
    vr.addHandle(0.1, 0.35);
    
    for(int i=0;i<erg;i++){
      double bDist = sysSize/erg;
      double dist=bDist+r.nextDouble()*(bDist*0.5)-(bDist*0.25);
      dist+=bDist*i;
      
      DPOrbitalPolygon orb = buildOrbit(dist,r,"Planet "+(i+1));
      orb.setWidth(0.5f);
      orb.setAlphaRamp(vr);
      staticPaths.add(orb);
      LinkedList<Pnt2D> ll = orb.getVertices();
      
      planets.add(new CBPlanet(variants, 1, r.nextLong(), Double.POSITIVE_INFINITY, i, ll.getFirst(), cbs));  
      if(planets.getLast().properties.get("Ring").equals("true")){
        rings.add(new CBRing(variants[6], 1, Double.POSITIVE_INFINITY,planets.getLast()));
      }

      int mErg = Integer.parseInt(planets.getLast().properties.get("Moons"));
      for(int j=0;j<mErg;j++){
        
        double moonDist = Double.parseDouble(planets.getLast().properties.get("SDistance"));
        CBMoon moo = new CBMoon(variants, 1, r.nextLong(), Double.POSITIVE_INFINITY, j, moonDist, planets.getLast());
        
        double pDist = Double.parseDouble(moo.properties.get("PDistance"));
        orb = buildOrbit(pDist,r,"");
        orb.setWidth(0.5f);
        orb.setCenter(planets.getLast().getDPoint());
        orb.setAlphaRamp(vr);
        staticPaths.add(orb);
        
        ll = orb.getVertices();
        
        moo.setPos(ll.getFirst().x+orb.getPos().x, ll.getFirst().y+orb.getPos().y);
        
        moons.add(moo);
      }
    }
    
    cBodies.add(cbs);
    cBodies.addAll(planets);
    cBodies.addAll(moons);
    cBodies.addAll(rings);

    wasSingle=false;
    
    Worker w = new Worker();
    w.start();
  }
  
  private DPOrbitalPolygon buildOrbit(double d, Random r, String t){
    LinkedList<Pnt2D> verts = new LinkedList<>();
    
    int resolution=128;
    
    int offset = (int)(r.nextDouble()*resolution);
    
    double rad=0;
    double stepSize = (2*Math.PI)/resolution;
    rad+=(offset*stepSize);
    Pnt2D v;
    
    for(int i=0;i<resolution;i++){
      v = new Pnt2D(d*Math.cos(rad),d*Math.sin(rad));
      verts.add(v);
      rad+=stepSize;
    }
    
    //making a random ellipse. just squash y, translate x by f and rotate random
    //TODO elliptic orbits. ungly. needed at all?
    /*double stretch=r.nextDouble()*0.01;

    
    double minorD = d*(1.0-stretch);
    double f = Math.sqrt(d*d-minorD*minorD);
    double rot = r.nextDouble()*2*Math.PI;
    for(Pnt2D p:verts){
      p.y*=(1.0+stretch);
      p.x+=f;
      
      //rotation by matrix multiplication:
      
      // [cos -sin]   [x]    [x*cos - y*sin]
      // [sin  cos] x [y] -> [x*sin + y*cos]
      
      double x = p.x*Math.cos(rot)-p.y*Math.sin(rot);
      double y = p.x*Math.sin(rot)+p.y*Math.cos(rot);
      p.x=x;
      p.y=y;
    }*/
    return new DPOrbitalPolygon(verts,new Pnt2D(0,0),new Color(0,1f,1f,0.5f),t);
  }
  
  public void setSelection(Rectangle r, Pnt2D focus, double zFactor, int xRes, int yRes){
    if(l.tryLock()){
      synchronized(selectionFlag){
        selectionFlag.notifyAll();
      }
      zF=zFactor;
      sel=new Rectangle2D.Double(r.getX(),r.getY(),r.getWidth(),r.getHeight());  
      wasSingle =(sel.height*sel.width==100);
      sel.x=sel.x/zFactor+focus.x-(xRes/2)/zFactor;
      sel.y=sel.y/zFactor+focus.y-(yRes/2)/zFactor;
      sel.height/=zFactor;
      sel.width/=zFactor;
      
      l.unlock();
    }  
  }
  
  private void updateSelection(){
    Rectangle2D.Double bodyBounds;
    selectedBodies.clear();
    cursors.clear();
    //if the size is 100, it's been adjusted and meant for only 1 selection

    for(Rasterable b:cBodies){
      bodyBounds = b.getScreenBounds(p, zF);
      if(sel.intersects(bodyBounds)){
        if(!(selectedBodies.size()==1 && wasSingle)){
          selectedBodies.add(b);
          cursors.add(new DPSelectionCursor(b,new Color(0,1,1,0.55f)));
        }
      }
    }
  }
  
  public boolean wasSingle(){
    return wasSingle;
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<Rasterable> getSelectedItems(){
    if(l.tryLock()){
      try{
        return (LinkedList<Rasterable>)selectedBodies.clone();
      } finally{
        l.unlock();
      }
    }else{
      return null;
    }
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<Rasterable> getAllDisplayItems(){
    if(l.tryLock()){
      try{
        return (LinkedList<Rasterable>)cBodies.clone();
      }finally{
        l.unlock();
      }
    } else{
      return null;
    }
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<DrawablePath> getAllSelectionCursors(){
    if(l.tryLock()){
      try{
        return (LinkedList<DrawablePath>)cursors.clone();
      }finally{
        l.unlock();
      }
    }else{
      return null;
    }
  }
  
  @SuppressWarnings("unchecked")
  public LinkedList<DrawablePath> getAllStaticPaths(){
    if(l.tryLock()){
      try{
        return (LinkedList<DrawablePath>)staticPaths.clone();
      }finally{
        l.unlock();
      }
    }else{
      return null;
    }
  }
  
}
