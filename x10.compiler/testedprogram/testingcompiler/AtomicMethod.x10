public class AtomicMethod {
	
	var v4:Int = 0;
	
	var v10:int;
	
	static val v13:Int = 0;
	
	var v14:AtomicMethod = new AtomicMethod();
	
	public atomic def atomicmethod(am:AtomicMethod, ii:Int) : void {
		var localInt:Int = 0;
		var i:Int = 0; 
	}
	
	 public def atomicSectionMethod() {
	 	var i:Int = 0;
	 	atomic(i, this) {i = i + 1;}
	 }
	 
	public def atomicSectionWithArg(ii:Array[Int]) {
		var i : Int = 0;
		atomic(ii) {i ++;}
	}
	
	public def testmethod() {
		async {
			this.atomicmethod();
		}
	}
	
	public def testAtomicMethod(v:AtomicMethod,  intv:Int) {
		var tmp:Int = 0;
		finish {
		  async {
			atomic(v, v14) {v14 = null; tmp = tmp + 1; }
		  }
		}
	}
	
	public def testAtomicMethod2() {
		var v:AtomicMethod = new AtomicMethod();
		async {
			atomic(v) {v14 = null;}
		}
		async {
			atomic(v14) {v10 = 0;}
		}
	}
	
	public def testAtomicMethod(v3:Int, v11:int, v7:AtomicMethod, v8:Array[Int](1)) {
		var v1:Int = 0;
		var v2:Int = 2;
		var v6:Int = 3;
		
		//atomic(v1, v2, v4, v7, v13, v14)//(v1, v2, v4, v5, v3, v11)
		atomic(v8)
		{var i:Int = 10;}
	}
	
	 public def seeLinkedVar() {
	 	var v6:linked AtomicMethod = new linked AtomicMethod();
        v6.staticLinkedVar(v6);
        v = w.a
	 }
	 
	 public def staticLinkedVar(a:AtomicMethod) {
	 	var v7:linked AtomicMethod = new linked AtomicMethod();
	 }
}
