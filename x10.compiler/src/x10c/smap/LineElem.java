/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package x10c.smap;

public class LineElem {
    private int origSrcStart;

    private int javaStart;

    private int incr;

    public LineElem(int origSrcStart, int javaStart, int incr) {
        this.origSrcStart= origSrcStart;
        this.javaStart= javaStart;
        this.incr= incr;
    }

    public int getOrigSrcStart() {
        return origSrcStart;
    }

    public int getJavaStart() {
        return javaStart;
    }

    public int getIncr() {
        return incr;
    }
    
    public String toString() {
    	return getOrigSrcStart() + ":" + getJavaStart() + "," + getIncr();
    }
}
