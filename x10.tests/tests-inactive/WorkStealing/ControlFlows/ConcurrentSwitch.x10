/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
//OPTIONS: -WORK_STEALING=true

/*
 * Test switch with concurrent method calls;
 */
public class ConcurrentSwitch {
    
    public def foo(i:int):int{
        val result:int;
        finish {
            async result = i;
        }
        return result;
    }

    public def switchTest(i:int):int{
        var r : int = 0;
            
        switch(i){
        case 0:
            r += foo(0);
        case 1:
            r += foo(1);
            break;
        case 2:
            r += foo(2);
        case 3:
            r += foo(3);
            break;
        default:
            r += foo(4);
        }
        return r;
    }
    
    public def run():boolean {
        var passed:boolean = true;
        var r : int = 0;
        r = switchTest(0);
        if( r==1 ) { Console.OUT.println("ConcurrentSwitch: 0 passed" );}
        passed &= (r==1);
        
        r = switchTest(1);
        if( r == 1) { Console.OUT.println("ConcurrentSwitch: 1 passed" );}
        passed &= (r==1);
        
        r = switchTest(2);
        if( r == 5) { Console.OUT.println("ConcurrentSwitch: 2 passed" );}
        passed &= (r==5);
        
        r = switchTest(3);
        if( r == 3) { Console.OUT.println("ConcurrentSwitch: 3 passed" );}
        passed &= (r==3);

        r = switchTest(4);
        if( r == 4) { Console.OUT.println("ConcurrentSwitch: 4 passed" );}
        passed &= (r==4);
        
        return passed;
    }
    
    public static def main(Rail[String]) {
        val r = new ConcurrentSwitch().run();
        if(r){
            x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
    
}