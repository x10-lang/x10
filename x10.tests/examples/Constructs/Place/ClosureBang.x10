class ClosureBang {
	class C {
		def n():void { throw new Exception(); }
	}
	def m(x:()=> GlobalRef[C]{self.home==here}) {
		at (here.next()) {
			x()(); // shd be ok.
		}
	}
}
