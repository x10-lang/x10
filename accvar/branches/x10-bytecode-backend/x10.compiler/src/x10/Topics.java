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

package x10;

import polyglot.main.Report;

/**
 * Extension information for x10 extension.
 */
public class Topics {
    public static final String x10 = "x10";

    static {
        Report.topics.add(x10);
    }
}
