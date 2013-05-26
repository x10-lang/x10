import x10.compiler.*;
import x10.util.*;
import harness.x10Test;

public class Congruent extends x10Test { 
    val probsize:int;

    public def this(ps:int) {
        probsize = ps;
    }

    public def run () {
        val elements = probsize * 1024/8;
        val imcplh = PlaceLocalHandle.make(PlaceGroup.WORLD, ()=>new Box(IndexedMemoryChunk.allocateZeroed[Long](elements, 8, true)));
        Console.OUT.println("Construction complete.");
        val str0 = imcplh()().toString();
        for (p in Place.places()) {
            val str = at (p) imcplh().toString();
            if (!str.equals(str0)) {
                Console.ERR.println("IMCs were not congruent at "+here+":");
                Console.ERR.println(str);
                Console.ERR.println(str0);
                return false;
            }
        }
        Console.OUT.println("Verified congruence.");

        // do some remote ops
        finish for (p in Place.places()) async at (p) {
            val imc = imcplh()();
            for (i in 0..(elements-1)) {
                val oracle = Math.sqrt(i as Double) as Long;
                imc.getCongruentSibling(p.next()).remoteAdd(i, oracle);
            }
        }
        Console.OUT.println("Remote ops complete.");

        // verify
        val errs = new Cell[Int](0);
        finish for (p in Place.places()) async at (p) {
            var errors:Int = 0;
            val imc = imcplh()();
            for (i in 0..(elements-1)) {
                val oracle = Math.sqrt(i as Double) as Long;
                if (imc(i) != oracle) {
                    Console.ERR.println(here+": imc("+i+")=="+imc(i)+" (should be "+oracle+")");
                    errors++;
                }
            }
            val errors_ = errors;
            at (Place.FIRST_PLACE) atomic errs() += errors_;
        }
        if (errs()>0) {
            Console.ERR.println(errs()+" errors.");
            return false;
        } else {
            Console.OUT.println("Verification complete.");
        }
        return true;
    }
    public static def main(args:Rail[String]) {
        var kBytes:int = 4;
        if (args.size>0) {
            kBytes = Int.parseInt(args(0));
        } 
        new Congruent(kBytes*1024/(Place.MAX_PLACES as int)).execute();
    }
}
