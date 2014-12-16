import harness.x10Test;

public class XTENLANG_3144 extends x10Test {

    public static abstract class A {
        //public abstract def m[V3](p:V3) : void;
        public def m[V3](p:V3) { }
    }

    public static class B extends A {
        public def m[V4](p:V4) {
            super.m[V4](p);
        }
    }

    public static class NotCalled extends A {
        public def m[V5](p:V5) {
            m[V5](p);
        }
    }

    public def run() {
        (new B()).m(42);
        return true;
    }

    public static def main(args:Rail[String]) {
        new XTENLANG_3144().execute();
    }
}

