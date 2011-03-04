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

public class TailActivity extends Activity{
	
	/**
	* The user-specified code for this activity.
	*/
	private val body:()=>Void;
        /**
 		 * place Id that this activity is created, from 0 to Max_Place-1. 
		 * -1 means don't care 
 		 */
    public val parentPlace:Int;
	/**
	* Create activity.
	*/
	def this(body:()=>Void, rootFinish:Runtime.RootFinish, safe:Boolean, parentPlace:Int) {
		super(body,rootFinish,safe,parentPlace);
		(this.finishState as Runtime.RootFinish).notifyTailActivityCreation();
		this.body = body;
        this.parentPlace = parentPlace;
	}

	/**
	* Create clocked activity.
	*/
	def this(body:()=>Void, rootFinish:Runtime.RootFinish, clocks:ValRail[Clock], phases:ValRail[Int], parentPlace:Int) {
		this(body, rootFinish, false,parentPlace);
   		this.clockPhases = Runtime.ClockPhases.make(clocks, phases);
	}

	/**
	* Create uncounted activity.
	*/
	def this(body:()=>Void, safe:Boolean, parentPlace:Int) {
   		super(body,null,safe,parentPlace);
   		this.body = body;
        this.parentPlace = parentPlace;
	}
	/**
	* Run activity.
	*/
	def run():Void {
   		try {
       			body();
   		} catch (t:Throwable) {
       			if (null != finishState) {
           			finishState.pushException(t);
       			} else {
           			Runtime.println("Uncaught exception in uncounted activity");
           			t.printStackTrace();
       			}
   		}
   		if (null != clockPhases) clockPhases.drop();
   		if (null != finishState) {
            val rootFinish = finishState as Runtime.RootFinish;
   			rootFinish.notifyTailActivityTermination(parentPlace);
   		} 
   		Runtime.dealloc(body);
	}
}