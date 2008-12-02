/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class PropertyAssign extends x10Test {
	class Prop(int i, int j) {
		public Prop(int i, int j) {
			property(i,j);
		}
	}
  
   
  
	public boolean run() {
		Prop p = new Prop(1,2);
	
	   return true;
	}
	public static void main(String[] args) {
		new PropertyAssign().execute();
	}
}