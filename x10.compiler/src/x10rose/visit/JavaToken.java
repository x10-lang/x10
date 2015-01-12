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

package x10rose.visit;

/**
 * This is the token class in Java that we will hand as an object to the C++ JNI functions.
 * We also wrap source position information here.
 */
public class JavaToken {
    // We mostly just want the token to carry a string version
    // of the parsed code and source position.
    public String text;   
    public String filename;
    private JavaSourcePositionInformation posInfo;

    public JavaToken(String s, JavaSourcePositionInformation posInfo) {
        this.text = s;
        this.posInfo = posInfo;
    }

    public String getText() {
        return text;
    }
    
    public String getFileName() {
        return filename;
    }

	 public JavaSourcePositionInformation getJavaSourcePositionInformation() {
        return this.posInfo;
    }
}
