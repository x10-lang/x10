package x10.hadoop;

import x10.interop.java.Throws;

public abstract class Mapper[KEYIN, VALUEIN, KEYOUT, VALUEOUT] extends org.apache.hadoop.mapreduce.Mapper {

    public static class Context[KEY, VALUE] {
	val context:org.apache.hadoop.mapreduce.Mapper.Context;
	public def this(context:org.apache.hadoop.mapreduce.Mapper.Context) {
	    this.context = context;
	}

	@Throws[java.io.IOException] @Throws[java.lang.InterruptedException]
	public def write(key:KEY, value:VALUE) {
	    context.write(key,value);
	}
    }

    @Throws[java.io.IOException] @Throws[java.lang.InterruptedException]
    abstract public def map(KEYIN, VALUEIN, Context[KEYOUT, VALUEOUT]) : void;

    /** this method overrides the required java method */
    @Throws[java.io.IOException] @Throws[java.lang.InterruptedException] 
    public def map(keyin:Any, valuein:Any, context:org.apache.hadoop.mapreduce.Mapper.Context) { // type erasure
	map(keyin as KEYIN, valuein as VALUEIN, new Context[KEYOUT, VALUEOUT](context));
    }
}
