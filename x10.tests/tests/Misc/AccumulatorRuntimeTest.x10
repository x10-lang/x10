import harness.x10Test;

import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;
import x10.lang.annotations.*; // FieldAnnotation MethodAnnotation

// tests for Accumulators
// todo: need to test Runtime.sync()

public class AccumulatorRuntimeTest extends x10Test {
	static struct LongReducer implements Reducible[Long] {
        public def zero():Long = 0;
        public operator this(x:Long,y:Long):Long = x+y;
	}
	static struct LongReducer2 implements Reducible[Long] {
			public def zero():Long = 1;
			public operator this(x:Long,y:Long):Long = x*y;
	}
	public static def main(var args: Rail[String]): void {
		new AccumulatorRuntimeTest().execute();
	}

	public def run(): boolean {
		async {
			when (cell.value!=null) {}
			val x = cell.value;
			//try {x()=2; fail(); } catch (e:IllegalAccAccess) {}
			//try {x<-2; fail(); } catch (e:IllegalAccAccess) {}
		}
		async {
			async {
				when (cell.value!=null) {}
				val x = cell.value;
				//try {x()=2; fail(); } catch (e:IllegalAccAccess) {}
				x<-2;
			}	
			testAccRestrictions();
		}
		testAcc();
		testAcc2();
		testAcc3();
		testAcc4();
		Console.OUT.println("ok");
		return true;
	}
	static val cell:Cell[Accumulator[Long]] = new Cell[Accumulator[Long]](null);
	static def testAccRestrictions() {
		val x = new Accumulator[Long](LongReducer());
		atomic {
			cell.value = x;
		}
		x<-1;
		async x<-2;
		at (Place.places().next(here)) {
			x<-1;
			async x<-2;
		}
		x()=1;
		async {
			//try {x()=2; fail(); } catch (e:IllegalAccAccess) {}
		}
		at (Place.places().next(here)) {
			x()=1;
			async {
				//try {x()=2; fail(); } catch (e:IllegalAccAccess) {}
			}
		}
	}
	static def fail():void { throw new Exception("Failed!"); }
	static def testAcc() {
		val x = new Accumulator[Long](LongReducer());
		val y = new Accumulator[Long](LongReducer2());
		m2(x,y);
	}
	static def m(x:Accumulator[Long]) {
                val w = Place.places();
		//async x.supply(2l);
		x.supply(2l);

		async x.supply(3l);
		at (w.next(here)) x.supply(4l);
		at (w.next(w.next(here))) x.supply(5l);
		async at (w.next(w.next(here))) x.supply(6l);
		async at (w.next(w.next(here))) async x.supply(7l);
		async at (w.next(w.next(here))) { async x.supply(8l); async x.supply(9l); x.supply(10l); }
		val p = here;
		async at (w.next(w.next(here))) async at (p) async x.supply(11l);
		async at (p) async at (p) async x.supply(12l);
	}
	static def m2(x:Accumulator[Long],y:Accumulator[Long]) {
		finish {
			m(x);
			m(y);
		}
		finish {
			m(x);
			m(y);
		}
		Console.OUT.println(x.result());
		Console.OUT.println(y.result());
		assert x.result()==154l;
		assert y.result()==229442532802560000l;
	}
	static def testAcc2() {
		val x = new Accumulator[Long](LongReducer());
		at (Place.places().next(here)) {
			x <- 1;
			assert x()==1l;
		}
		assert x()==1l;
	}
	static def testAcc3() {
		val x = new Accumulator[Long](LongReducer());
		at (Place.places().next(here)) {
		 finish async {
		  x <- 1;
		 }
		 assert x()==1l;
		}
		assert x()==1l;
	}
	static def testAcc4() {
		val acc = new Accumulator[Long](LongReducer());
		at (Place.places().next(here)) {
		 val c = Clock.make();
		 async clocked (c) {
		  acc <- 1;
		  c.advance(); // this updates the clock c, but now it should also update the "acc" (but the "acc" is at a different place)
		 }
		 c.advance();
		}
		assert acc()==1l;
	}
}
