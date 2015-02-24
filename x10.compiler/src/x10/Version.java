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

package x10;

/**
 * Version information for x10 extension
 */
public class Version extends polyglot.main.Version {
    @Override
    public String name() { return "x10c"; }

    @Override
    public int major() { return 2; }
    @Override
    public int minor() { return 5; }
    @Override
    public int patch_level() { return 2; }
}
