import harness.x10Test;

public class XTENLANG_3143 extends x10Test {

    public static interface I {
        public def m[T]() {String <: T} : void;
    }
    public static interface C extends I {
        public def m[U]() {String <: U} : void;
    }

    public def run() : Boolean = true;

    public static def main(args:Rail[String]) {
        new XTENLANG_3143().execute();
    }
}

