
import x10.util.Box;

public class Hello {
    public static def main(Rail[String]) {
        finish for (p in Place.places()) {
            at (p)
               async
                  Console.OUT.println("Hello World from place "+p.id);
        }

        val b = new Box[Any](2);

        val v: Int = b() as Int;

        Console.OUT.println(v);

    }
}
