/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ZoomFrame.java
 *
 * Created on 21.11.2012, 16:22:53
 */

package gui;

/**
 *
 * @author Xels
 */
public class GUIFrame extends javax.swing.JFrame {

  private static final long serialVersionUID = 7506430291670132407L;
  private GUIP gp;
  
  //private TestPanel gp;
    
    public GUIFrame() {
        initComponents();
        this.setSize(1024, 720);
        
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
      public void run() {
                new GUIFrame().setVisible(true);
            }
        });
    }

    private void initComponents() {
        gp = new gui.GUIP(getWidth(),getHeight());
      //gp = new TestPanel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
      public void keyPressed(java.awt.event.KeyEvent evt) {
              gp.v.keyPressed(evt);
            }
        });
        javax.swing.GroupLayout zoomPanel1Layout = new javax.swing.GroupLayout(gp);
        gp.setLayout(zoomPanel1Layout);
        zoomPanel1Layout.setHorizontalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        zoomPanel1Layout.setVerticalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }
}
