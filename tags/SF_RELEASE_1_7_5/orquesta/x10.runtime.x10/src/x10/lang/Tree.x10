package x10.lang;
/**

An implementation of a distributed tree. Non-null branches of the
tree may point to a local or remote object.

The tree is created from the bottom up and it is the responsibility
of the creator to balance the tree across multiple places, if that
is appropriate.

@author vj 06/04/2008
*/
@Tree.T
public class Tree {
	 public static interface T extends Parameter(:x==1){
		 Value value();
	 }
	  T data; // the data
	  
	  nullable<Tree@T> left, right; // these nodes may live anywhere
	  public Tree(T _data, nullable<Tree@T> _left, nullable<Tree@T> _right) {
		  this.data = _data;
		  this.left = _left;
		  this.right = _right;
	  }
  
	  public static abstract class BinaryOp {
		  public abstract Value apply(Value x, Value y);
	  }
	  public static  abstract class UnaryOp {
		  public abstract Value apply(Value x);
	  }
  /** Reduce the tree using a naive algorithm that reduces sub-trees
   * recursively in parallel. 
   */
   
  public Value reduce(final T z,  final BinaryOp f)  {
  	if (left == null) {
  		if (right == null) return data.value();
  		if (right.location == here) {
  			return f(data.value(), right.reduce(z,f));
  		}
  		future<Value> rf = future (right) { right.reduce(z,f)};
  		return f(data.value(), rf.force());
  	}
  						
  	if (right == null) {
  		if (left == null) return data.value();
  		if (left.location==here) {
  			return f(data.value(), left.reduce(z,f));
  		}
  		future<Value> lf = future(left) { left.reduce(z,f)};
  		return f(data.value(), lf.force());
  	}
  	
  	future<Value> lf = future(left){left.reduce(z,f)}, 
  	rf = future(right) {right.reduce(z, f)};
  	return f(data.value(), f(lf.force(), rf.force()));
  }
  		

}


