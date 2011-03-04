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

package x10.compiler.ws.codegen;

/*
 * @author: Haichuan
 * 
 * Used to config the optimization approaches in work-stealing code gen.
 * Example, whether to generate pc field for some frames
 * 
 * Note all config are static final currently.
 */

public class WSOptimizeConfig {

    /*
     * If it is 1, optimize pc field for finish and async frame 
     */
    static final public int OPT_PC_FIELD = 1;
}
