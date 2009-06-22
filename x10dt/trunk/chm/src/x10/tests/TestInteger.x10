package x10.tests;

value class TestInteger extends x10.lang.Object {
	
	final int val;
	
	TestInteger(int val){ this.val = val; }
	public String toString() { return String.valueOf(this.val); }
}