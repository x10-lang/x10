package rc7;

final public class Timer {
	public val total:Rail[Long]!;
public val count:Rail[Long]!;

public def this(n:Int) {
	total = Rail.make[Long](n, (Int)=>0L);
	count = Rail.make[Long](n, (Int)=>0L);
}

public def start(id:Int) { total(id) -= System.nanoTime(); }
public def clear(id:Int) { total(id) = 0; }
public def stop(id:Int) { total(id) += System.nanoTime(); count(id)++; }
}
