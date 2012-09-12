/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import java.security.MessageDigest;
import x10.interop.Java;
import x10.io.File;

/**
 * An example to illustrate how to use Java Message Digest API from X10.
 * In this example, message digest is generated for each file in parallel on multiple places.
 * Supported algorithms are MD5 and SHA{1,256,384,512}. (default is MD5).
 * Usage: [X10_NPLACES=n] x10 MD [-md5,-sha{1,256,384,512}] file1 file2 ...
 */
public class MD {

    static val BUFFER_SIZE = 8192;
    static def printDigest(fileName:String, md:MessageDigest) {
        val digest = md.digest();
        // avoid boxing byte to Byte
        // for (var i:Int = 0; i < digest.length; ++i) {
        //     Console.OUT.printf("%02x", digest(i));
        // }
        // Console.OUT.println("  " + fileName);
        val sb = new x10.util.StringBuilder();
        for (var i:Int = 0; i < digest.length; ++i) {
            val hex = (digest(i) as UByte).toHexString();
            if (hex.length() == 1) sb.add('0'); // zero padding
            sb.add(hex);
        }
        sb.add("  " + fileName);
        Console.OUT.println(sb.toString());
    }

    static def generateDigest(fileName:String, algo:String) {
        try {
            val md = MessageDigest.getInstance(algo);
            val input = new Array[Byte](BUFFER_SIZE);
            val fr = new File(fileName).openRead();
            var len:Int;
            while ((len = fr.available()) > 0) {
                if (len > input.size) len = input.size;
                fr.read(input, 0, len);
                val jinput = Java.convert(input);
                md.update(jinput, 0, len);
                if (len < input.size) break;
            }
            fr.close();
            printDigest(fileName, md);
        } catch (e:x10.lang.CheckedThrowable) {
            e.printStackTrace();
        }
    }

    static def generateDigestParallel(fileNames:Rail[String], algo:String) {
        finish for (var i:Int = 0; i < fileNames.size; ++i) {
            val fileName = fileNames(i);
            async generateDigest(fileName, algo);
        }
    }

    public static def main(args:Rail[String]) {
        val places = Place.places();
        val numPlaces = places.size();
        var algoTemp:String = "MD5";        
        var filesTemp:Rail[String] = args;
        
        if (args.size == 0) return;
        
        if (args(0).charAt(0) == '-') {
            val args0 = args(0).substring(1);
            var newalgo:Boolean = false;
            if (args0.equalsIgnoreCase("MD5")) {
                algoTemp = "MD5";
                newalgo = true;
            } else if (args0.equalsIgnoreCase("SHA1") || args0.equalsIgnoreCase("SHA-1")) {
                algoTemp = "SHA-1";
                newalgo = true;
            } else if (args0.equalsIgnoreCase("SHA256") || args0.equalsIgnoreCase("SHA-256")) {
                algoTemp = "SHA-256";
                newalgo = true;
            } else if (args0.equalsIgnoreCase("SHA384") || args0.equalsIgnoreCase("SHA-384")) {
                algoTemp = "SHA-384";
                newalgo = true;
            } else if (args0.equalsIgnoreCase("SHA512") || args0.equalsIgnoreCase("SHA-512")) {
                algoTemp = "SHA-512";
                newalgo = true;
            }
            if (newalgo) {
                filesTemp = new Rail[String](args.size - 1);
                Array.copy(args, 1, filesTemp, 0, args.size - 1);
            }
        }
        
        val algo = algoTemp;
        val files = filesTemp;
        
        if (files.size == 0) return;

        if (numPlaces == 0) {
            generateDigestParallel(files, algo);
        } else {
            val filesArray = new Rail[Rail[String]](numPlaces, (i:Int) => new Rail[String]((files.size + numPlaces - 1 - i) / numPlaces));
            // for (var i:Int = 0; i < files.size; ++i) {
            //     filesArray(i % numPlaces)(i / numPlaces) = files(i);
            // }
            var srcIndex:Int = 0;
            for (var i:Int = 0; i < numPlaces; ++i) {
                val files_i = filesArray(i);
                Array.copy(files, srcIndex, files_i, 0, files_i.size);
                srcIndex += files_i.size;
            }
            finish for (var i:Int = 0; i < numPlaces; ++i) {
                val files_i = filesArray(i);
                at (places(i)) async generateDigestParallel(files_i, algo);
            }
        }
    }
}
