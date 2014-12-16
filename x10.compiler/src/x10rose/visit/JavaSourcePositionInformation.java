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

package x10rose.visit;

/**
 * This is the source code position class in Java that we will hand as an object to the C++ JNI functions.
 */
public class JavaSourcePositionInformation {
    // This is the class to communicate the source position to ROSE
    // so that IR nodes built on that side will have a valid source 
    // code position.

    private int line_number_start;
    private int line_number_end;
    private int column_number_start;
    private int column_number_end;

    public JavaSourcePositionInformation(int line) {
        this(line, line, 0, 0);        
    }

     public JavaSourcePositionInformation(int line_start, int line_end) {
         this(line_start, line_end, 0, 0);
     }

     public JavaSourcePositionInformation(int line_start, int line_end, int col_start, int col_end) {
         line_number_start   = line_start;
         line_number_end     = line_end;
         column_number_start = col_start;
         column_number_end   = col_end;
    }

    public int getLineStart() {
        return line_number_start;
    }

    public int getLineEnd() {
        return line_number_end;
    }

    public int getColumnStart() {
        return column_number_start;
    }

    public int getColumnEnd() {
        return column_number_end;
    }
}
