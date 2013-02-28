import x10.io.Console;
import x10.compiler.*;

import x10.util.Vec;

import harness.x10Test;

public class VectorTest extends x10Test {
    public static def my_assert[T](a:T, b:T, msg:String) {
        if (a!=b) {
            Console.ERR.println(a + " != " + b + " while " + msg);
            return 1;
        }
        return 0;
    }
    public static def modify_in_method_sized[T] (vec:Vec[T]{size==20}, imposter:T) {
        vec(15) = imposter;
    }
    public static def modify_in_method_unsized[T] (vec:Vec[T], imposter:T) {
        vec(15) = imposter;
    }
    public static def doTest[T] (a:T, b:T, imposter:T) { T haszero } {
        var counter:Int = 0;

        var vec_sized1 : Vec[T]{size==20} = Vec.make[T](20);
        var vec_unsized1 : Vec[T] = Vec.make[T](20);

        my_assert(vec_sized1, vec_sized1, "doing self equality 1");
        my_assert(vec_unsized1, vec_unsized1, "doing self equality 2");
        my_assert(vec_sized1, vec_unsized1, "doing equality 1");
        my_assert(vec_unsized1, vec_sized1, "doing equality 2");

        vec_sized1(15) = a;
        vec_unsized1(15) = b;

        counter += my_assert(vec_sized1(15), a, "doing simple assignment");
        counter += my_assert(vec_unsized1(15), b, "doing simple assignment");

        var vec_sized2 : Vec[T]{size==20} = vec_sized1;
        var vec_unsized2 : Vec[T] = vec_unsized1;

        my_assert(vec_sized1, vec_sized2, "doing equality 3");
        my_assert(vec_unsized1, vec_unsized2, "doing equality 4");

        counter += my_assert(vec_sized1(15), a, "checking init");
        counter += my_assert(vec_unsized1(15), b, "checking init");

        vec_sized1(15) = imposter;
        vec_unsized1(15) = imposter;
        counter += my_assert(vec_sized2(15), a, "checking no alias on copy");
        counter += my_assert(vec_unsized2(15), b, "checking no alias on copy");

        vec_sized1 = vec_sized2;
        vec_unsized1 = vec_unsized2;
        counter += my_assert(vec_sized1(15), a, "checking copy whole array");
        counter += my_assert(vec_unsized1(15), b, "checking copy whole array");

        vec_sized2(15) = imposter;
        vec_unsized2(15) = imposter;
        counter += my_assert(vec_sized1(15), a, "checking no alias on copy (2)");
        counter += my_assert(vec_unsized1(15), b, "checking no alias on copy (2)");

        // restore values, we know this works, checked earlier
        vec_sized2 = vec_sized1;
        vec_unsized2 = vec_unsized1;

        vec_sized1(15) = imposter;
        vec_unsized1(15) = imposter;
        vec_sized1 = vec_unsized2 as Vec[T]{size==20};
        vec_unsized1 = vec_sized2;
        counter += my_assert(vec_sized1(15), b, "checking copy whole array with conversions");
        counter += my_assert(vec_unsized1(15), a, "checking copy whole array with conversions");

        vec_sized1 = vec_sized2;
        vec_unsized1 = vec_unsized2;

        modify_in_method_sized(vec_sized1, imposter);
        modify_in_method_unsized(vec_unsized1, imposter);
        counter += my_assert(vec_sized1(15), a, "checking no alias when call method");
        counter += my_assert(vec_unsized1(15), b, "checking no alias when call method");

        modify_in_method_sized(vec_sized1, imposter);
        val tmp1 = vec_sized1;
        tmp1(15) = imposter;
        val tmp2 = vec_unsized1;
        tmp2(15) = imposter;
        counter += my_assert(vec_sized1(15), a, "checking no alias on init");
        counter += my_assert(vec_unsized1(15), b, "checking no alias on init");

        Console.OUT.println("vec_sized1.typeName():"+vec_sized1.typeName());
        Console.OUT.println("vec_sized1:"+vec_sized1);
        Console.OUT.println("vec_unsized1.typeName():"+vec_unsized1.typeName());
        Console.OUT.println("vec_unsized1:"+vec_unsized1);

        return counter;
    }

    public static def doWholeTest () {
        var counter:Int = 0;

        counter += doTest[Double](115, 215, 666);
        counter += doTest[Int](115, 215, 666);
        counter += doTest[Byte](115 as Byte, 215 as Byte, 666 as Byte);
        counter += doTest[Complex](Complex(115,115), Complex(215,215), Complex(666,666));

        return counter;
    }

    public static def main (args:Rail[String]) {
        (new VectorTest()).execute();
    }

    public def run() = doWholeTest() == 0;
}

// vim: shiftwidth=4:tabstop=4:expandtab
