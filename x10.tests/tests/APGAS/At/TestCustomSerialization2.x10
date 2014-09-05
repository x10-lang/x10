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

import harness.x10Test;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.HashMap;

/*
 * Simple test case to check to see if custom serialization
 * protocol is being called by runtime system
 */
public class TestCustomSerialization2 extends x10Test {

    static class CS implements CustomSerialization {
        val x:long;
        val y:long;
        transient var sum:long;
 
         public def serialize(s:Serializer) {
             s.writeAny(x);
             s.writeAny(y);
         }
  
         def this(ds:Deserializer) {
             x = ds.readAny() as long;
             y = ds.readAny() as long;
             sum = x + y;
         }
 
         def this(a:long, b:long) { x = a; y = b; sum = x + y; }
    }

    public def run():boolean {
        val map =  new HashMap[String,CS]();
	map.put("a", new CS(10,20));
	map.put("b", new CS(20,10));
	map.put("c", new CS(25,5));

        val ser = new Serializer();	
        map.serialize(ser);
	val map2 = new HashMap[String,CS](new Deserializer(ser));
        chk(map2.get("a").sum == 30);        
        chk(map2.get("b").sum == 30);        
        chk(map2.get("c").sum == 30);        

        at (Place.places().next(here)) {
            // The custom serialization logic re-establishes that sum = x + y
            // Default serialzation would result in sum having the value of zero.
            chk(map.get("a").sum == 30);        
            chk(map.get("b").sum == 30);        
            chk(map.get("c").sum == 30);        
        }
        return true;
    }

    public static def main(Rail[String]) {
        new TestCustomSerialization2().execute();
    }
}
  
 