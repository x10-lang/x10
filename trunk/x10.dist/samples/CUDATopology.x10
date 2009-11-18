import x10.io.Console;

public class CUDATopology {
    public static def main (args : Rail[String]) {
        for (p in Place.places) {
            at (p) {
                Console.OUT.println("Dumping places at place: "+p);
                for (p2 in Place.places) {
                    Console.OUT.println("Place: "+p2);
                    Console.OUT.println("  Parent: "+p2.parent());
                    Console.OUT.println("  NumChildren: "+p2.numChildren());
                    if (p2.isCUDA()) Console.OUT.println("  Is a CUDA place");
                    if (p2.isHost()) Console.OUT.println("  Is a Host place");
                    if (p2.isSPE()) Console.OUT.println("  Is a SPE place");
                    val children = p2.children();
                    for (c in children) {
                        Console.OUT.println("  Child "+c.childIndex()+": "+c);
                        Console.OUT.println("    Parent: "+c.parent());
                        Console.OUT.println("    NumChildren: "+c.numChildren());
                        if (c.isCUDA()) Console.OUT.println("    Is a CUDA place");
                        if (c.isHost()) Console.OUT.println("    Is a Host place");
                        if (c.isSPE()) Console.OUT.println("    Is a SPE place");
                    }
                }
                Console.OUT.println();
            }
        }
    }
}

// vim: shiftwidth=8:tabstop=8:expandtab

