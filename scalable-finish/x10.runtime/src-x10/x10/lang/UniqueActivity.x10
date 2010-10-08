/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;
import x10.util.Stack;

public class UniqueActivity extends Activity{
	/**
	* The user-specified code for this activity.
	*/
	private val body:()=>Void;
       
	/**
	* Create activity.
	*/
	def this(body:()=>Void, rootFinish:Runtime.FinishState, safe:Boolean) {
		super(body,rootFinish,safe);
		this.body = body;
	}

	/**
	* Create uncounted activity.
	*/
	def this(body:()=>Void, safe:Boolean) {
   		super(body,safe);
   		this.body = body;
	}
	/**
	* Run activity.
	*/
	def run():Void {
   		try {
       			body();
   		} catch (t:Throwable) {
       			if (null != finishState) {
       				if (here.equals(finishState.home)) {
       					(finishState as Runtime.UniqueRootFinish!).pushException(t);
    				}else {
       					//TODO: should notify root the exception?
    				}
       			} else {
           			Runtime.println("Uncaught exception in uncounted activity");
           			t.printStackTrace();
       			}
   		}
   		if (null != clockPhases) clockPhases.drop();
   		if (null != finishState) {
   			if (here.equals(finishState.home)) {
   				(finishState as Runtime.UniqueRootFinish!).notify2();
			} else {
                                val f = finishState;
				val closure = () => {
                                        Console.OUT.println(finishState!=null);
                                        Console.OUT.println(f!=null);
					(f as Runtime.UniqueRootFinish!).notify2();
				};
                                //Console.OUT.println("before run at");
				Runtime.runAtNative(finishState.home.id, closure);
                                //Console.OUT.println("after run at");
				Runtime.dealloc(closure);
			}
   		} 
   		Runtime.dealloc(body);
   		//Console.OUT.println("activity terminate");
	}
}
