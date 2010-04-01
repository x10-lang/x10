import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;

public class TestRailReference {


    public static def main(args:Rail[String]!) {
    val c = Clock.make();
    val op = Float.+;
    shared var a: Rail[float];
    shared var b: Rail[float] @ Clocked[Float](c, op);
    
    async clocked (c)
		a = b;  
    
    }
}
