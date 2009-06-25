package x10.lang;
/**

An implementation of a distributed tree. Non-null branches of the
tree may point to a local or remote object.

The tree is created from the bottom up and it is the responsibility
of the creator to balance the tree across multiple places, if that
is appropriate.

@author vj 06/04/2008
*/

public class Tree[T] {
    var data:T;
    var left, right: Tree[T];
    public def this(_data:T, _left:Tree[T], _right:Tree[T]) = {
	this.data = _data;
	this.left = _left;
	this.right = _right;
    }
  
  /** Reduce the tree using a naive algorithm that reduces sub-trees
   * recursively in parallel. 
   */
   
  public def reduce[V <: Value](z:V,  u:T=>V, f:(V,V)=>V):V {
      if (left == null) {
	  if (right == null) return u(data);
	  if (right.location == here) {
	      return f(u(data), right.reduce(z,u, f));
	  }
	  val rf: future[V] = future (right, ()=> right.reduce(z,f));
	  return f(u(data), rf.force());
      }
      if (right == null) {
	  if (left.location==here) {
	      return f(u(data), left.reduce(z,u,f));
	  }
	  val lf:future[V] = future(left, ()=> left.reduce(z,f));
	  return f(u(data), lf.force());
      }
  	
      val lf:future[V] = future(left, ()=>left.reduce(z,f)), 
	  rf:future[V] = future(right, ()=>right.reduce(z, f));
      return f(u(data), f(lf.force(), rf.force()));
  }
}


