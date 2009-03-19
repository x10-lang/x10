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
 * (Unlike Test1, the outer object is created in a separate statement.)
 * @author vj Wed Jul 30 06:09:38 2008
 */
public class Test2 extends x10Test {
    class Board { 
        void run() {}
    }
    public static void main(String[] args) {
	TestI t = new TestI();
        t.new Board().run();
    }
}


