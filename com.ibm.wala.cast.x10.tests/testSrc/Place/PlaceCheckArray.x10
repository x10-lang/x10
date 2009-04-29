/**
 * @author bdlucas
 */
public class PlaceCheckArray {


    val a = Array.make[int](Dist.makeUnique());

    public def run01(): boolean {
        try {
            (future (Place.places(1)) a(0)).force();
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("01 fails");
        return false;
    }

    public def run02(): boolean {
        try {
            a(1);
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("02 fails");
        return false;
    }

    public def run(): boolean {
        a(0);
        (future (Place.places(1)) a(1)).force();
        return run01() && run02();
    }

    public static def main(var args: Rail[String]): void = {
        new PlaceCheckArray().run();
    }
}
