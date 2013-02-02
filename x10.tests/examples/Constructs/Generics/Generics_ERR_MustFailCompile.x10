struct Struct1 {}
//struct Struct2 extends Struct1 {} // I would prefer it was a semantic error and not a parsing error!
class AClass extends Struct1 {} // ERR: Struct1 cannot be the superclass for AClass; a class must subclass a class.

class Bla[T] extends Exception
	{T<:
		Bla} // ERR
	{
	var x1:
		Bla; // ERR
	var x2:
		Bla[Int,Int]; // ERR: Number of type arguments (2) for Bla is not the same as number of type parameters (1).
	var x3:
		Bla[Bla]; // ERR
	var x4:
		Bla[Bla[Bla]]; // ERR
	var x5:Bla[Bla[Bla[Int]]];
	var x6:Bla[Int]{T<:Bla[Int]};
	var x7:Bla[Int]{T<:
		Bla}; // ERR

	static def m() {}
	static def m2():Bla[Int] {
		Bla.m();
		//Bla[Int].m(); // I would prefer it was a semantic error and not a parsing error!
		val z1 =
			new Bla(); // ERR (ctor type parameters are inferred if they can be!)
		val z2 = // ShouldNotBeERR
			new Bla[Bla](); // ERR
		val z3 =
			new Bla[Int[Int]](); // ERR

		val b1 =
			null instanceof Bla[Int]
			|| null instanceof
				Bla // ERR
			|| null instanceof
				Bla[Bla]; // ERR

		val a1 = null as Bla[Int];
		val a2 =  // ShouldNotBeERR
		    null as
			Bla; // ERR

		val c1 = // ShouldNotBeERR 
		    (x:Bla[Int],
				y:
					Bla, // ERR
				z:Bla[Bla[Int]]):
					Bla // ERR
				=> null;

		return new Bla[Int]();
	}
	static def m3(x:
		Bla, // ERR
		y:Bla[Int]):
			Bla // ERR
		= null;

	def m5[U]() {U<:Bla[T]} {}
	static def m6[U]() {U<:
		Bla} // ERR
		{}


	static type BlaInt = Bla[Int];
	static type Bla2 =
		Bla[Bla]; // ERR
	static type Bla3 =
		Bla; // ERR
	static type Bla4(x:
		Bla // ERR
		) = Int;

	static class S {}
	var s1:S;
	var s2:Bla.S
	    = new Bla.S();  // ctor params are inferred

	var s3:Bla[Int].S;
	//var s4 = new Bla[Int].S(); // I would prefer it was a semantic error and not a parsing error!
	var s5:
		S[T]; // ERR

	var other:Bla[T] = null;

	class Inner {}
	var i1:Inner = other.new Inner();
	var i2:Bla[T].Inner = other.new Inner();
	var i3:Bla[Int].Inner = new Bla[Int]().new Inner();
	var i4:
		Bla.Inner; // ShouldBeErr
	var i5:Bla[T].Inner =
		new Bla(). new Inner(); // ERR (ctor type parameters are inferred if they can be!)
	var i6:
		Inner[T]; // ERR

	class Inner2[U] {}

	var j1:Inner2[Int] = other.new Inner2[Int]();
	var j2:Bla[T].Inner2[T] = other.new Inner2[T]();
	var j3:Bla[Int].Inner2[T] = new Bla[Int]().new Inner2[T](); 
	var j4:
		Inner2; // ERR
	var j5:Bla[T].Inner2[T] =
		other.new Inner2(); // ERR ERR
	var j6:Inner2[T];
	var j7:
		Inner2; // ERR
}
