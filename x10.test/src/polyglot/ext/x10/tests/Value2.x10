public value Value2 {
	int x;
	public Value2() {
		this.x=0;
	}
	public Value2(int x) {
		this.x=x;
	}
	public static void main(String[] v) {
		Value1 x=new Value1(5);
		Value1 y=new Value1(6);
		System.out.println(x==y);
	}
}

