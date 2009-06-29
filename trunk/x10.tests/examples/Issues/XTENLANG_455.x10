
class XTENLANG_455_Base {
    public def m[T](x:T): void { }
}

public class XTENLANG_455 extends XTENLANG_455_Base {
    public def m(x:String) = "Correct";
    
    public static def main(Rail[String]) {
        val y = new XTENLANG_455();
        val z: String = y.m("Incorrect");
        Console.OUT.println(z);
    }
}


