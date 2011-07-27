package x10.hadoop;

public abstract class Mapper[KEYIN, VALUEIN, KEYOUT, VALUEOUT] extends org.apache.hadoop.mapreduce.Mapper {

    public static class Context[KEY, VALUE] {
	val context:org.apache.hadoop.mapreduce.Mapper.Context;
	public def this(context:org.apache.hadoop.mapreduce.Mapper.Context) {
	    this.context = context;
	}

	public def write(key:KEY, value:VALUE) {
	    context.write(key,value);
	}
    }

    abstract public def map(KEYIN, VALUEIN, Context[KEYOUT, VALUEOUT]) : void;

    /** this method overrides the required java method */
    public def map(keyin:Any, valuein:Any, context:org.apache.hadoop.mapreduce.Mapper.Context) { // type erasure
	map(keyin as KEYIN, valuein as VALUEIN, new Context[KEYOUT, VALUEOUT](context));
    }
}
