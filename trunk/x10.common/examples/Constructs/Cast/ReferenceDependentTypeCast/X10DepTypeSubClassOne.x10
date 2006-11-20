import harness.x10Test;

public class X10DepTypeSubClassOne(int a) extends X10DepTypeClassOne {

	public X10DepTypeSubClassOne(:self.a==q)(int a,int q) {
	    super(a);
	    this.a=q;
	}
}

