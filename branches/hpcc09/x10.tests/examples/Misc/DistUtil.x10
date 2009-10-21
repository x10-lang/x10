/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

public class DistUtil {

	/**
	 * Return an array of P regions, which together block divide the 1-D region R.
	 */

	public static def block(R: Region(1), P:Int):ValRail[Region(1)](P) = {
		assert P >=0;
		val low = R.min()(0), high = R.max()(0), count = high-low+1;
		val baseSize = count/P, extra = count - baseSize*P;
		ValRail.make[Region(1)](P, (i:Nat):Region(1) => {
		  val start = low+i*baseSize+ (i < extra? i:extra);
		  start..start+baseSize+(i < extra?0:-1)
		  })
	}
    /*	public static def main(Rail[String]):Void  = {
		val  R = block(1..22, 4);	
        for(var i:Int=0; i < R.length; i++)
          x10.io.Console.OUT.println("i=" + i + " " + R(i));	
		
	  }*/
  
}
