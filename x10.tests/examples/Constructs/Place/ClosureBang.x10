class ClosureBang {
	class C {
		incomplete def n():Void;
	}
	def m(x:()=> GlobalRef[C]{self.home==here}) {
		at (here.next()) {
			x()(); // shd be ok.
		}
	}
}