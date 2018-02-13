/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2018.
 */

import java.util.ArrayList;

import java.util.function.LongBinaryOperator;
import java.io.Serializable;

public class FunctionalInterfaceJava {
    public static void main(String[] args) {

        ArrayList<String> stringsJava = new ArrayList<>();
        stringsJava.add("aaa");
        stringsJava.add("bbb");
        stringsJava.add("ccc");
        int origSizeJava = stringsJava.size();
        stringsJava.removeIf(s -> false);
        int newSizeJava = stringsJava.size();
        if (newSizeJava != origSizeJava) {
            System.out.println("ERROR: something is wrong with Java predicate.");
            System.exit(1);
        }

        ArrayList<String> stringsX10 = new ArrayList<>();
        stringsX10.add("aaa");
        stringsX10.add("bbb");
        stringsX10.add("ccc");
        int origSizeX10 = stringsX10.size();
        stringsX10.removeIf(new PredicateX10());
        int newSizeX10 = stringsX10.size();
        if (newSizeX10 != origSizeX10) {
            System.out.println("ERROR: something is wrong with X10 predicate.");
            System.exit(1);
        }

        String error1 = FunctionalInterfaceX10.reduce((long left,long right)->left+right, 0, 0, 10, 55);  // Java lambda
        if (error1 != null) {
            System.out.println(error1);
            System.exit(1);
        }

        String error2 = FunctionalInterfaceX10.reduce(Long::sum, 0, 0, 10, 55);  // Java method reference
        if (error2 != null) {
            System.out.println(error2);
            System.exit(1);
        }

        String error1s = FunctionalInterfaceX10.reduceAt((LongBinaryOperator & Serializable)(long left,long right)->left+right, 0, 0, 10, 55);  // Java lambda
        if (error1s != null) {
            System.out.println(error1s);
            System.exit(1);
        }

        String error2s = FunctionalInterfaceX10.reduceAt((LongBinaryOperator & Serializable)Long::sum, 0, 0, 10, 55);  // Java method reference
        if (error2s != null) {
            System.out.println(error2s);
            System.exit(1);
        }

        System.out.println("OK");
    }
}
