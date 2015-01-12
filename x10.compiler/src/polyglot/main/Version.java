/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.main;

/** This class encapsulates the version of the compiler. */
public abstract class Version {
    /** 
     * The name of the language.  Files produced by different languages
     * are not compatible.
     */
    public abstract String name();

    /** 
     * Marks major changes in the output format of the files produced by the
     * compiler. Files produced be different major versions are considered
     * incompatible and will not be used as source of class information.
     */
    public abstract int major();
    
    /** 
     * Indicates a change in the compiler that does not affect the output
     * format.  Source files will be prefered over class files build by
     * compilers with different minor versions, but if no source file is
     * available, then the class file will be used.
     */
    public abstract int minor();

    /**
     * Denote minor changes and bugfixes to the compiler. Class files compiled
     * with versions of the compiler that only differ in patchlevel (from the
     * current instantiation) will always be preferred over source files
     * (unless the source files have newer modification dates).
     */
    public abstract int patch_level();

    public String toString() {
	return "" + major() + "." + minor() + "." + patch_level();
    }
}
