package drawable;

import java.util.Objects;

public class Pnt2D implements Comparable<Pnt2D> {
    public double x;
    public double y;

    public Pnt2D(){
        x=0f;
        y=0f;
    }

    public Pnt2D(double x, double y){
        this.x=x;
        this.y=y;
    }

    public void clear(){
        x=0;
        y=0;
    }
    
    public Pnt2D getCopy(){
      return new Pnt2D(x,y);
    }
    
    @Override
    public int compareTo(Pnt2D o){
      //sorts after x then after y
      if(o.x<x)return 1;
      else if(o.x>x)return -1;
      else if(o.y<y)return 1;
      else if(o.y>y)return -1;
      return 0;
    }

  @Override
  public boolean equals(Object arg0) {
    Pnt2D c=(Pnt2D)arg0;
    return (x==c.x && y==c.y);
  }
  
  @Override
  public String toString(){
    return String.format("%.4f:%.4f", x,y);
  }

  @Override
  public int hashCode(){
    return Objects.hash(x,y);
  }
}
