import x10.io.Console;

import harness.x10Test;

public class XTENLANG_3120 extends x10Test {
    public static class Foo extends java.util.Date { }
    public static class Bar extends java.util.ArrayList { }
    public def run () {
        new Foo();
        new Bar();
        return true;
    }
    public static def main (args : Array[String]) {
        (new XTENLANG_3120()).run();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

