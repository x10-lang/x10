/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method arg can have a deptype and it is propagated into the body.
 *
 * @author vj
 */
public class MethodArgDepTypes2 extends x10Test {
	public static def arraycopy(val a_dest: Array[double], 
	                            val a_src: Array[double]{rank==a_dest.rank}): void = {	
    	  val R: Region{rank==a_dest.rank} = a_src.region&& a_dest.region; 
    	  finish foreach (val p: Point{rank==a_dest.rank} in R) {
    	  //finish for( point p : R){	  
    	    a_dest(p)= (future(a_src.dist(p)) {a_src(p)}).force();
    	  }	  
    	  //for( point p : R) a_dest[p]=a_src[p]; //implicit syntax
	}
  
	public def m(val a: Array[double]): void = {
		var result: Array[double] = a;
		val buffDest: Array[double] = a;
		
		val buffSrc =  buffDest;
		arraycopy(buffDest, buffSrc);
	
	}
	public def run()=true;
	
	public static def main(var args: Rail[String]): void = {
		new MethodArgDepTypes2().execute();
	}
}
