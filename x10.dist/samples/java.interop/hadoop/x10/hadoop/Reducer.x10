package x10.hadoop;

import x10.interop.java.Throws;

public abstract class Reducer[KEYIN, VALUEIN, KEYOUT, VALUEOUT] extends org.apache.hadoop.mapreduce.Reducer {

    public static class Context[KEY, VALUE] {
	val context:org.apache.hadoop.mapreduce.Reducer.Context;
	public def this(context:org.apache.hadoop.mapreduce.Reducer.Context) {
	    this.context = context;
	}

	@Throws[java.io.IOException] @Throws[java.lang.InterruptedException]
	public def write(key:KEY, value:VALUE) {
	    context.write(key,value);
	}
    }

    public static class IterableWrapper[T] implements Iterable[T] {
	public static class IteratorWrapper[T] implements Iterator[T] {
	    val it:java.util.Iterator;
	    public def this(it:java.util.Iterator) {
		this.it = it;
	    }
	    public def hasNext() = it.hasNext();
	    public def next() = it.next() as T;
	}
	val what:java.lang.Iterable;
	public def this(what:java.lang.Iterable) {
	    this.what = what;
	}
	public def iterator() = new IteratorWrapper[T](what.iterator());
    }

    @Throws[java.io.IOException] @Throws[java.lang.InterruptedException]
    abstract public def reduce(KEYIN, Iterable[VALUEIN], Context[KEYOUT, VALUEOUT]) : void;

    /** this method overrides the required java method */
    @Throws[java.io.IOException] @Throws[java.lang.InterruptedException]
    public def reduce(keyin:Any, valuein:java.lang.Iterable, context:org.apache.hadoop.mapreduce.Reducer.Context) { // type erasure
	reduce(keyin as KEYIN, new IterableWrapper[VALUEIN](valuein), new Context[KEYOUT, VALUEOUT](context));
    }
}
