package ssca2;

class WrapRail[T] implements Indexable[Int,T], Settable[Int,T] {
	public var length: Int;
private val data: Rail[T];

public def this(size: Int) {
	length = 0;
	data= Rail.make[T](size);
}
public def reset() {
	length = 0;
}

public def add(val elem: T) {
	data(length++) = elem;
}

public def apply(i: Int) {
	return data(i);
}

public def set(elem: T, i: Int) {
	data(i) = elem;
	return data(i);
}

};

