 /**  
 Check that circular dependencies between classes are handled correctly
 during TypeElaboration.
 See CircularField.x10
 
 *@author vj
 *
 */



 public class CF1(int i, int j)  {
  
	 public CF1(int i, int j) {this.i=i; this.j=j;}
	 CircularField(:k==3) f = (CircularField(:k==3)) new CircularField(3);
 
 }
  