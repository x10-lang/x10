/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;;

/**
*  Implementing a 5-point stencil operation using foreach loop
* @author Tong
  11/29/2006
  @author vj 09/2008 In 1.7 this version is hte same as StencilForeach2D. 
  Type inference infers all the relevant properties.
*/
public class StencilForeach2D_Dep extends x10Test {
	
        public def run(): boolean = {
        	val R  = [-1..256, -1..256] to Region;
            val r  = [0..255, 0..255] to Region;
        	val north = [0, 1] to Point;
            val south = [0, -1] to Point;
            val west  = [-1, 0] to Point;
            val east = [1, 0] to Point; 
        	val A  =  Array.make[double](R->here, (point)=>0.0D); 
        	val h: double = 0.1;
        	
        	finish foreach (val p: point(2) in r) 
        	   A(p)=(A(p+north)+A(p+south)+A(p+west)+A(p+east)-4*A(p))*h;
        	
	    return true;
	}
	
	public static def main(var args: Rail[String]): void = {
		new StencilForeach2D_Dep().execute();
	}

}
