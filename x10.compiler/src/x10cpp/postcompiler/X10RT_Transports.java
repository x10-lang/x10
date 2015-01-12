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

package x10cpp.postcompiler;

/**
 * An enumeration describing the key properties
 * of the x10rt implementations supported by X10.
 * Primarily intended for use by X10DT to encapsulate
 * the mapping of human consumable names/information to property files.
 */
public enum X10RT_Transports {
    SOCKETS("Sockets", "x10rt_sockets.properties", 
            "Connects places using TCP/IP sockets as the network transport. Uses ssh to connect spawn places on multiple hosts"),
    STANDALONE("Standalone", "x10rt_standalone.properties", "Connects places using shared memory.  Only supports a single host."),
    PAMI("PAMI", "x10rt_pami.properties", "Uses IBM PAMI as the network transport and IBM POE for process creation."),
    MPI("MPI", "x10rt_mpi.properties", "Uses MPI as the network transport and mpirun for process creation.");
    
    private final String displayName;
    private final String propFileName;
    private final String description;
    
    /**
     * Human readable name for display in UIs
     */
    String displayName() { return displayName; }

    /**
     * Name of the x10rt property file that would be found
     * in x10.dist/etc if this transport was supported by a 
     * particular x10 distibution.
     */
    String propertyFileName() { return propFileName; }
    
    /**
     * A brief description of the transport suitable for display in a UI as "help text"
     */
    String description() { return description; }
    
    X10RT_Transports(String dn, String pfn, String desc) {
        displayName = dn;
        propFileName = pfn;
        description = desc;
    }
}
