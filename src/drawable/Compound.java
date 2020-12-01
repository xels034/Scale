package drawable;

import java.util.HashMap;

public class Compound {

  HashMap<String, Double> components;
  
  public Compound(){
    components = new HashMap<>();
  }
  
  public Compound(HashMap<String, Double> hm){
    components=hm;
  }
  
  public void addComponent(String s, double d){
    components.put(s, d);
  }
  
  public HashMap<String, Double> getComponents(){
    return components;
  }
}
