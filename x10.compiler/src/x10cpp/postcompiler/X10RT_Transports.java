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

package x10cpp.postcompiler;

/**
 * An enumeration of the x10rt transport implementations 
 * that are supported by NativeX10.
 */
public enum X10RT_Transports {
    SOCKETS("Sockets", "x10rt_sockets.properties"),
    STANDALONE("Standalone", "x10rt_standalone.properties"),
    LAPI("LAPI", "x10rt_pgas_lapi.properties"),
    BGP("Blue Gene/P", "x10rt_pgas_bgp.properties");
    
    /**
     * Human readable name for display in UIs
     */
    String displayName;
    
    /**
     * Name of the x10rt property file associated with this transport
     */
    String propFileName;
    
    X10RT_Transports(String dn, String pfn) {
        displayName = dn;
        propFileName = pfn;
    }
}
