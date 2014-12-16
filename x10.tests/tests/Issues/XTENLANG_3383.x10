import harness.x10Test;

public class XTENLANG_3383 extends x10Test {
    public def run() {
        val x = new Rail[Complex](1);
        x(0) += Complex.ONE;
        assert(x(0) == Complex.ONE);

        return true;
    }

    public static def main(args:Rail[String]) {
        new XTENLANG_3383().execute();
    }
}

