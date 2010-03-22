import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;

public class Histogram {

    public static def main(args:Rail[String]!) {
    val c = Clock.make();
    val op = Float.+;
    val b :Rail[float @ Clocked[float] (c,op)]! = Rail.make[float](5);
	b(0) = 1;
    
    }
}
