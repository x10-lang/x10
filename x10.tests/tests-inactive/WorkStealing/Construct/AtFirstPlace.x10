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

import harness.x10Test;

/*
 * At first place. But still should be transformed as at frame
 */

public class AtFirstPlace extends x10Test {
    public def run():boolean {
        
        at (Place.FIRST_PLACE) Console.OUT.println("At first place");
	    return true;
    }

    public static def main(args:Rail[String]) {
        new AtFirstPlace().execute();
    }
}
