import java.util.function.LongBinaryOperator;

public class FunctionalInterfaceX10 {
    public static def reduce(reducer:LongBinaryOperator, init:Long, from:Long, to:Long, check:Long):String {
        var reduced:Long = init;
        for (var i:Long = from; i <= to; ++i) {
            reduced = reducer.applyAsLong(reduced, i);
        }
        val error = (reduced == check) ? null : "ERROR: something is wrong with X10 long binary operator";
        return error;
    }
    public static def main(Rail[String]):void {
        val error = reduce(new LongBinaryOperatorX10(), 0, 0, 10, 55);
        if (error != null) {
            Console.OUT.println(error);
            System.setExitCode(1n);
            return;
        }
        Console.OUT.println("OK");
    }
}
