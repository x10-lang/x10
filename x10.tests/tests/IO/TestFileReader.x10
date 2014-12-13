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

import harness.x10Test;

import x10.io.File;
import x10.io.FileReader;

/**
 * Tests for x10.io.FileReader, including manipulating and query of offset.
 */
public class TestFileReader extends x10Test {

    public def testOffset() {
        // alphabet.text contains one line with 26 letters
        val fileName = pathCombine(["tests", "IO"], "alphabet.txt");
        val file = new File(fileName);
        val totalSize = file.size();
        chk(totalSize > 0);

        val reader = new FileReader(file);
        chk(reader.offset() == 0);

        reader.skip(10);
        chk(reader.offset() == 10);

        val text = reader.readLine();
        chk(text.length() == 16n);

        chk(reader.offset() == 27); // past the end of line

        return true;
    }


    public def run() {
        var success:Boolean = true;
        success &= testOffset();
        return success;
    }

    public static def main(var args: Rail[String]) {
        new TestFileReader().execute();
    }
}
