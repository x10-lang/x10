import harness.x10Test;

public class ArrayStaticPlusEqual extends x10Test {

    static val v: Rail[int] = Rail.makeVar[int](2, (x:nat)=>0);

    public def run() {
        for ((i) in 0..1) v(i) += 5;
        for ((i) in 0..1) chk(v(i) == 5);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayStaticPlusEqual().execute();
    }
}
