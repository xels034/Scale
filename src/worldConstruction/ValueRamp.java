package worldConstruction;

import java.util.TreeSet;

import drawable.Pnt2D;

public class ValueRamp {
  TreeSet<Pnt2D> handles;
  
  public ValueRamp(){
    handles = new TreeSet<>();
    handles.add(new Pnt2D(0,0));
    handles.add(new Pnt2D(1,1));
  }
  
  public void changeEnds(double y1, double y2){
    handles.remove(handles.first());
    handles.add(new Pnt2D(0,y1));
    
    handles.remove(handles.last());
    handles.add(new Pnt2D(1,y2));
  }
  
  public void addHandle(double x, double y){
    handles.add(new Pnt2D(x,y));
  }
  
  public double getValue(double x){
    Pnt2D p1 = handles.floor(new Pnt2D(x,0));
    Pnt2D p2 = handles.ceiling(new Pnt2D(x,0));
    //because floor&ceiling both return greater/less AND equals
    //when the sample is exactly on a handle, it'd return both times the
    //same pos if this happens, increment p2 or decrement p1
    if(x<0){
      return handles.first().y;
    }else if(x>1){
      return handles.last().y;
    }else{
      if(p1.equals(p2)){
        if(p2.x<1){
          p2=handles.higher(p1);
        }else{
          p1=handles.lower(p2);
        }
      }
      double gap = p2.x-p1.x;
      double dx = x-p1.x;  
      //System.out.println("g:"+gap+" dx:"+dx+" p1:"+p1.x+" p2:"+p2.x);
      double w = dx/gap;
      double erg = p1.y*(1-w)+p2.y*w;
      return erg;
    }
  }
}