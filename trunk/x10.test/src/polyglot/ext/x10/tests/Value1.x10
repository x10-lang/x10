public value Value1 {
	int x;
	public Value1() {
		this.x=0;
	}
	public Value1(int x) {
		this.x=x;
	}
	public static void main(String[] v) {
		Value1 x=new Value1(5);
		Value1 y=new Value1(5);
		System.out.println(x==y);
	}
}

