class ClosureBang {
	class C {
		incomplete def n():Void;
	}
	def m(x:()=> C!) {
		at (here.next()) {
			x().n(); // shd be ok.
		}
	}
}