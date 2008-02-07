package msat;

public class Lit {
	public static int VAR_UNDEF = -1;
	public static Lit LIT_UNDEF = new Lit(VAR_UNDEF, false),
	LIT_ERROR = new Lit(VAR_UNDEF, true);
	final int x;
	public Lit() { x=2*VAR_UNDEF;}
	public Lit(int var) { this(var, false);}
	public Lit(int var, boolean sign) { 
		x= var+var + (sign?1:0);
		}
	public Lit unsign() {
		Lit q = new Lit(x & ~1);
		return q;
	}
	public static int toInt(Lit p) { return p.x;}
	public static Lit toLit(int i) { return new Lit(i);}
	public int var() { return x >> 1;}
	public Lit opTilde() { return id(true); }
	public Lit id(boolean sign) { 
		Lit q = new Lit(x ^ (sign? 1:0));
		return q;
	}
	
}
