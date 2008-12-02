/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Creating an instance of a nested class should work.
 *
 * @author vj Wed Jul 30 06:09:38 2008
 */
public class Test1 extends x10Test {
    class Board { 
        void run() {}
    }
    public static void main(String[] args) {
        new TestI().new Board().run();
    }
}


