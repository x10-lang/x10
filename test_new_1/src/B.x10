public class B {
	var num_1_B:int = 0;
	var num_2_B:int = 0;
	
	public def this() {}
	
	public def boo_1(arg1:Int) {
		this.num_1_B = arg1;
		this.boo_2(this.num_1_B + 10);
	}
	
	public def boo_2(arg1:Int) {
		this.num_2_B = arg1;
	}
	
	public def toString(): String {
		return "class B ["+num_1_B+"]["+num_2_B+"]";
	}

}