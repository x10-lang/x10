/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
//OPTIONS: -WORK_STEALING=true

/*
 * A method implements an interface, and the method contains concurrent
 */
public class OverrideMethod3 {

	def getA(flag:double){
	    val a:AOverrideMethod3;
        if(flag > 0.5){
    	   a = new AAOverrideMethod3();
        }
        else{
    	   a = new AOverrideMethod3();
        }
        return a;
	}
	
	
	public def run() {
		val random = new x10.util.Random();
		
		var passed:boolean = true;
		
	    val flag:double = random.nextDouble();
	    val a = getA(flag);
	    
	    val r:int = a.set(1);
	    if(flag > 0.5){
			passed &= (r == 1);
	    }
	    else{
			passed &= (r == -1);
	    }
		Console.OUT.println("r  = " + r);		
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new OverrideMethod3().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}

}

class AOverrideMethod3{
    public def set(v:int):int{
	    return -1;
    }
}

class AAOverrideMethod3 extends AOverrideMethod3{
    public def set(v:int):int{
        val value:int;
	    finish{
		    async value = v;
	    }
	    return value;
    }
}

