package gui;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;

import drawable.CBGalaxy;
import drawable.CBPlanet;
import drawable.CBStar;
import drawable.Rasterable;

import view.GalaxyView;
import view.SolSysView;
import view.UniView;
import view.View;

import worldConstruction.ValueRamp;

public class GUIP extends javax.swing.JPanel{

  private static final long serialVersionUID = -2222207880613558008L;
  public View v;
  private BufferedImage[][] textures;
  private int mode;
  private Object[] buffer;
  
  public GUIP(int x, int y){
    Repainter rp = new Repainter(this);
    rp.start();
    mode=0;
    
    buffer = new Object[3];
    textures = new BufferedImage[10][5];
    URL[] urls = new URL[27];
    
    urls[0]=GUIP.class.getResource("/sur_Molt.png");
    urls[1]=GUIP.class.getResource("/sur_Rock.png");
    urls[2]=GUIP.class.getResource("/sur_Metal.png");
    urls[3]=GUIP.class.getResource("/sur_Mineral.png");
    urls[4]=GUIP.class.getResource("/sur_Prec.png");
    urls[5]=GUIP.class.getResource("/gas_Oxy.png");
    urls[6]=GUIP.class.getResource("/gas_Hel.png");
    urls[7]=GUIP.class.getResource("/gas_Hydro.png");
    urls[8]=GUIP.class.getResource("/atmo_Oxy.png");
    urls[9]=GUIP.class.getResource("/atmo_Hel.png");
    urls[10]=GUIP.class.getResource("/atmo_Hydro.png");
    urls[11]=GUIP.class.getResource("/shadow.png");
    
    urls[12]=GUIP.class.getResource("/star_r.png");
    urls[13]=GUIP.class.getResource("/star_o.png");
    urls[14]=GUIP.class.getResource("/star_w.png");
    urls[15]=GUIP.class.getResource("/star_b.png");
    urls[16]=GUIP.class.getResource("/star_u.png");
    
    urls[17]=GUIP.class.getResource("/uniform_density.png");
    urls[18]=GUIP.class.getResource("/barred_density.png");
    urls[19]=GUIP.class.getResource("/spiral_density.png");
    urls[20]=GUIP.class.getResource("/halo_density.png");
    urls[21]=GUIP.class.getResource("/irregular_density.png");
    
    urls[22]=GUIP.class.getResource("/ring_Rock.png");
    urls[23]=GUIP.class.getResource("/ring_Metal.png");
    urls[24]=GUIP.class.getResource("/ring_Mineral.png");
    urls[25]=GUIP.class.getResource("/ring_Prec.png");
    urls[26]=GUIP.class.getResource("/ring_Molten.png");
    
    try {
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
      
      textures[6][0] = ImageIO.read(urls[22]);//ring: Rock
      textures[6][1] = ImageIO.read(urls[23]);//  metal
      textures[6][2] = ImageIO.read(urls[24]);//  mineral
      textures[6][3] = ImageIO.read(urls[25]);//  prec
      textures[6][4] = ImageIO.read(urls[26]);//  molten

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

    Random r = new Random();
    r.setSeed(1);
    Long l=r.nextLong();
    v = new UniView(l, 1000000000, 0.001, 800, 600, 350, textures[5]);
    buffer[0]=l;
    
    initComponents();
  }
  
  @Override
  public void paintComponent(Graphics g){
    Toolkit k = Toolkit.getDefaultToolkit();
    BufferedImage cursorImg=null;
    try {
      cursorImg = ImageIO.read(GUIP.class.getResource("/cursor_c.png"));
    }catch (IOException e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
      System.out.println("ex");
    }
    //int w = k.getBestCursorSize(16, 16).width;
    //System.out.println("Best dim: "+w);
    this.setCursor(k.createCustomCursor(cursorImg, new Point(10,10), "custom"));
    
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
    v.drawScreen(g2, this);
  }
  
    private void initComponents() {
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                v.mouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
      public void mousePressed(java.awt.event.MouseEvent evt) {
                v.mousePressed(evt);
            }
            @Override
      public void mouseReleased(java.awt.event.MouseEvent evt){
              Entry<GUIAction, Object> e=v.mouseReleased(evt);
              
              switch(e.getKey()){
              case ENTER_GALAXY_FROM_UNIVERSE:
                mode++;
                buffer[mode]=e.getValue();
                
                CBGalaxy glxy = (CBGalaxy)e.getValue();
                
                int type;
                int amt=700;
                String t=glxy.properties.get("Type");
                
                if(t.equals("Uniform")){
                  type=0;
                  amt=400;
                }else if(t.equals("Barred-spiral")){
                  type=1;
                }else if(t.equals("Spiral")){
                  type=2;
                }else if(t.equals("Halo")){
                  type=3;
                }else{
                  type=4;
                }
                v = new GalaxyView(glxy.getSeed(), 1000000000, 0.001, 800, 600, amt, /*starIMGs*/textures[5],textures[8][type],glxy.getID());
                
                
                break;
              case ENTER_SOL_FROM_GALAXY:
                mode++;
                buffer[mode]=e.getValue();
                CBStar star = (CBStar)e.getValue();
                ValueRamp vr = constructRamp(star.getSeed());
                
                double temperature = Double.parseDouble(star.properties.get("Size"))*5000;
                int maxDistance = (int)CBPlanet.fSize(temperature);
                int rDistance = (int)CBPlanet.fSizeRound(temperature);
                double zoom = 500.0/rDistance;//half a screen width
                
                v = new SolSysView(star.getSeed(), maxDistance, zoom, 800, 800, 700, textures, vr, star);
                
                
                break;
              case ENTER_GALAXY_FROM_SOL:
                mode--;
                Rasterable seeder = (Rasterable)buffer[mode];
                Rasterable calibrato = (Rasterable)buffer[mode+1];
                
                int samt=700;
                String st=seeder.properties.get("Type");
                
                if(st.equals("Uniform")){
                  type=0;
                  amt=400;
                }else if(st.equals("Barred-spiral")){
                  type=1;
                }else if(st.equals("Spiral")){
                  type=2;
                }else if(st.equals("Halo")){
                  type=3;
                }else{
                  type=4;
                }
                
                v = new GalaxyView(seeder.getSeed(), 1000000000, 0.001, 800, 600, samt, textures[5],textures[8][type],seeder.getID());
                v.calibrate(2500, calibrato.getDPoint());
                
                
                
                break;
              case ENTER_UNIVERSE_FROM_GALAXY:
                mode--;
                Long l= (Long)buffer[mode];
                Rasterable calibrator = (Rasterable)buffer[mode+1];
              v = new UniView(l, 1000000000, 0.001, 800, 600, 350, textures[5]);
              v.calibrate(2500,calibrator.getDPoint());
                break;
              case NONE:
              }
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
      public void mouseDragged(java.awt.event.MouseEvent evt) {
                v.mouseDragged(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
      public void keyPressed(java.awt.event.KeyEvent evt) {
                v.keyPressed(evt);
            }
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }
    
    private ValueRamp constructRamp(long s){
      ValueRamp ret = new ValueRamp();
      ret.changeEnds(0, 0);
      
      Random r = new Random();
      r.setSeed(s);
      if(r.nextDouble()<0.2){
        ret.addHandle(0.5, 0);
        ret.addHandle(0.54, 0.15);
        ret.addHandle(0.55, 1);
        ret.addHandle(0.56, 0.15);
        ret.addHandle(0.6, 0);
        System.out.println("has belt");
      }
      return ret;
    }
  
}
