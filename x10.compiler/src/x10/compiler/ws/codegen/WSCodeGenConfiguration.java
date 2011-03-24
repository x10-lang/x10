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

import x10.Configuration;

/*
 * @author: Haichuan
 * 
 * Used to config the optimization approaches in work-stealing code gen.
 * Example, whether to generate pc field for some frames
 * 
 * FIXME: still need refactoring a lot
 */

public class WSCodeGenConfiguration {


    //1: not gen pc field for Finish Frame ans async frame
    //Default 1
    public int OPT_PC_FIELD = 1;
    
    //1: not gen try catch block in finish frame and async frame
    public int DISABLE_EXCEPTION_HANDLE = 0;
    
    
    public WSCodeGenConfiguration(Configuration config){
        if(config.WS_DISABLE_EXCEPTION_HANDLE){
            DISABLE_EXCEPTION_HANDLE = 1;
            System.out.println("[WS_INFO] Not Generate fast path's exception handling code");
        }
    }
    
    
}