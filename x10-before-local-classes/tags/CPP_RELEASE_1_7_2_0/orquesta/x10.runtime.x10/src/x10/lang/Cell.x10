package x10.lang;

import x10.lang.annotations.*;

@Cell.T
public class Cell {
	public static  interface T extends Parameter(:x==1) {}
	
	Object@T x;
	Object@T get() { return x; }
	void set(Object@T x) { this.x = x; }
	Cell(Object@T init) {
		x=init;
	}
	public static void main(String[] t) {
		Cell@Instance((int) 0) x = new Cell(0);
	}
}