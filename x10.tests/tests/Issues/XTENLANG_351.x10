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

import harness.x10Test;

/**
 * Unicode characters in comments, such as
 * "Мама мыла раму"
 * and
 * "Ευχαριστώ"
 * or even
 * "façade"
 * caused the lexer to report an error at the end of the file (2/2011).
 */
public class XTENLANG_351 extends x10Test {
    public def run(): boolean = true;

    public static def main(Rail[String]) {
        new XTENLANG_351().execute();
    }
}
