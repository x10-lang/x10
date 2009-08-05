package x10.runtime;

public value IntValue(i:Int) {
	public def this(i:Int) {
		property(i);
	}
	
	public def hashCode():Int = i;
}
