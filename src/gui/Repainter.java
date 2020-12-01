/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 *
 * @author Xels
 */
public class Repainter extends Thread{

    JPanel panel;
    boolean run;
    long ts;
    
    public Repainter(JPanel g){
        panel=g;
        run=true;
      ts=System.currentTimeMillis();
    }

    public void halt(){
        run=false;
    }

    @Override
    public void run(){
        //repaint panel every 20ms
      long diff;
        while(run){
          diff = 20-(System.currentTimeMillis()-ts);
          ts = System.currentTimeMillis();
          if(diff>0){
              try {
              Thread.sleep(diff);
              } catch (InterruptedException ex) {
                  Logger.getLogger(Repainter.class.getName()).log(Level.SEVERE, null, ex);
              }
          }else{
          }
          panel.repaint();
          //System.out.println("fertig meethode");
        }   
    }
}
