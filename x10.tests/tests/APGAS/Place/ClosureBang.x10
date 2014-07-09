class ClosureBang {
	class C {
		def n():void { throw new Exception(); }
	}
	def m(x:()=> GlobalRef[C]{self.home==here}) {
		at (Place.places().next(here)) {
			x()(); // shd be ok.
		}
	}
}
