package x10.lib.stream;

public class ValRailSource[T] implements Sink[T] {
	var i:Int=0;
	val a:ValRail[T];
	public def this(a:ValRail[T]) {
	    this.a=a;
	}
	public def get() throws StreamClosedException {
	    if (i < a.length)
		return a(i++);
	    throw new StreamClosedException();
	}
	public def isClosed() = i==a.length;
}