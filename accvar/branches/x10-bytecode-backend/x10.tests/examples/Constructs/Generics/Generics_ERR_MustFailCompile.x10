public class Generics_ERR_MustFailCompile {}

class Bla[T] extends Throwable
	{T<:
		Bla} // ERR - not caught!
	{
	var x1:
		Bla; // ERR
	var x2:
		Bla[Int,Int]; // ERR - reported 2 errors in this line!
	var x3:
		Bla[Bla]; // ERR
	var x4:
		Bla[Bla[Bla]]; // ERR
	var x5:Bla[Bla[Bla[Int]]];
	var x6:Bla[Int]{T<:Bla[Int]};
	var x7:Bla[Int]{T<:
		Bla}; // ERR - not caught!

	static def m() {}
	static def m2():Bla[Int] {
		Bla.m();
		Bla[Int].m(); // ERR - now it is a parsing error!
		val z1 = 
			new Bla(); // ERR - not caught!
		val z2 = 
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
		val a2 = null as 
			Bla; // ERR

		val c1 = (x:Bla[Int],
				y:
					Bla, // ERR - not caught!
				z:Bla[Bla[Int]]):
					Bla // ERR
				=> null;

		return new Bla[Int]();
	}
	static def m3(x:
		Bla, // ERR - not caught!
		y:Bla[Int]):
			Bla // ERR
		= null;
	
	static def m4() throws 
		Bla // ERR
		{}
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
	var s2:Bla.S = new Bla.S();


	// causes a syntax error!
	var s3:
		Bla[Int] // ERR
			.S;

		
	var s4:Bla.S = new 
		Bla[Int] // ERR
			.S();
	var s5:
		S[T]; // ERR

	class Inner {}
	var i1:Inner = new Inner();
	var i2:Bla[T].Inner = new Inner();
	var i3:Bla[Int].Inner = new Bla[Int]().new Inner();
	var i4:
		Bla.Inner; // ERR
	var i5:Bla[T].Inner =
		new Bla(). new Inner(); // ERR
	var i6:
		Inner[T]; // ERR 

	class Inner2[U] {}
	var j1:Inner2[Int] = new Inner2[Int]();
	var j2:Bla[T].Inner2[T] = new Inner2[T]();
	var j3:Bla[Int].Inner2[T] = new Bla[Int]().new Inner2[T]();
	var j4:
		Inner2; // ERR
	var j5:Bla[T].Inner2[T] =
		this.new Inner2(); // ERR
	var j6:Inner2[T];
	var j7:
		Inner2; // ERR
}