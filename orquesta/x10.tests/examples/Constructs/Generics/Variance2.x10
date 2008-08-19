/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Region algebra.
 *
 * @author nystrom 8/2008
 */
public class Variance2 extends x10Test {
        class Get[+T] { x: T; def this(y: T) = { x = y } def get(): T = x; }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                val ga = new Get[A](new A());
                val gb = new Get[B](new B());
                val a = ga.get();
                val b = gb.get();

                val gx : Get[A] = ga;
                val x = gx.get();
                val gy : Get[A] = gb; // covariance
                val y = gy.get();

                return gx == ga && gy == gb && x == a && y == b;
        }

	public static def main(var args: Rail[String]): void = {
		new Variance2().execute();
	}
}

    def run() = {
        val ga = new Get[A](new A());
        val gb = new Get[B](new B());
        val a = ga.get();
        val b = gb.get();

        val gx : Get[A] = ga;
        val x = gx.get();
        val gy : Get[A] = gb; // covariance
        val y = gy.get();

        return gx == ga && gy == gb && x == a && y == b;
    }
}

class Variance2_MustFailCompile {
    class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }

    class A { }
    class B extends A { }

    def run() = {
        val ga = new Get[A](new A());
        val gb = new Get[B](new B());
        val a = ga.get();
        val b = gb.get();

        val gx : Get[A] = ga;
        val x = gx.get();
        val gy : Get[A] = gb; // error: Get is invariant, not covariant
        val y = gy.get();

        return gx == ga && gy == gb && x == a && y == b;
    }
}

class Variance3 {
    class Set[-T] { var x: T;
                    def this(y: T) = { x = y }
                    def set(y: T): void = { x = y }  }

    class A { }
    class B extends A { }

    def run() = {
        val a = new A();
        val b = new B();

        val sa = new Set[A](a);
        val sb = new Set[B](b);

        sa.set(b);
        sb.set(b);

        val sx: Set[B] = sa;
        sx.set(b);

        val sy: Set[B] = sb;
        sy.set(b);

        return true;
    }
}

class Variance3_MustFailCompile {
    class Set[T] { var x: T;
                   def this(y: T) = { x = y }
                   def set(y: T): void = { x = y }  }

    class A { }
    class B extends A { }

    def run() = {
        val a = new A();
        val b = new B();

        val sa = new Set[A](a);
        val sb = new Set[B](b);

        sa.set(b);
        sb.set(b);

        val sx: Set[B] = sa; // error: Set is invariant, not contra
        sx.set(b);

        val sy: Set[B] = sb;
        sy.set(b);

        return true;
    }
}

    class IGet[+T] { def get(): T; }        
    class ISet[-T] { def set(T): void; }
    class B[T] { }
    class C[T] extends B[T] { }
    class CInt extends B[int] { }
    class CObj extends B[Object] { }
*/
}
}

class Subclassing1 {
    class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }
    class Get2[T] extends Get[T] { def this(y: T) = { super(y); } }

    class A { }
    class B extends A { }

    def run() = {
        new Get[A](new A());
        new Get[B](new B());
        new Get2[A](new A());
        new Get2[B](new B());
        return true;
    }
}

class Subclassing2 {
    class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }
    class GetB extends Get[B] { def this(y: B) = { super(y); } }

    class A { }
    class B extends A { }

    def run() = {
        new Get[A](new A());
        new Get[B](new B());
        new GetB(new B());
        return true;
    }
}
     
class Subclassing3 {
    class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }
    class GetA extends Get[A] { def this(y: B) = { super(y); } }

    class A { }
    class B extends A { }

    def run() = {
        new Get[A](new A());
        new Get[B](new B());
        new GetA(new A());
        new GetA(new B());
        return true;
    }
}

class Subclassing4 {
    class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }
    class Getint extends Get[int] { def this(y: B) = { super(y); } }

    def run() = {
        new Get[int](0);
        new Get[int](1);
        new Getint(2);
        new Getint(3);
        return true;
    }
}
