import x10.compiler.Foreach;
import x10.array.DenseIterationSpace_2;

class Foreach0 {
    static class Sequential {
	public static operator for (range: LongRange, body: (i:Long)=>void) {
	    Foreach.sequential(range.min, range.max, body);
	}

	public static operator for (space: DenseIterationSpace_2, body: (Point(2))=>void) {
	    Foreach.sequential(space, (i: Long, j: Long) => { body([i, j]); });
	}

	public static operator for (range: LongRange, body: (Point(2))=>void) {
	    Foreach.sequential(range.min, range.max, (min: Long, max: Long) => { body([min, max]); });
	}

	public static class Reduce[T] {
	    // public class NotReady extends CheckedException {}

	    val reduce: (T, T) => T;
	    private var result : Cell[T] = null;

	    public def this(reduce: (T, T) => T){
	    	this.reduce = reduce;
	    }

	    public operator for (range: LongRange, body: (Point(2))=>T) {
		this.result = new Cell(Foreach.sequentialReduce(range.min, range.max,
								(min:Long, max:Long) => { body([min, max]) },
								this.reduce));
	    }

	    public def value () // throws NotReady
	    {
		if (this.result == null) { throw new Exception(); }
		return this.result();
	    }

	}

	static class ReduceId[T] extends Reduce[T] {
	    val identity : T;

	    public def this(reduce: (T, T) => T, identity: T){
		super(reduce);
		this.identity = identity;
	    }

	    public operator for (range: LongRange, body: (i:Long)=>T) {
		this.result = new Cell(Foreach.sequentialReduce(range.min, range.max, body, this.reduce, this.identity));
	    }

	}
    }

    static class Basic {
	public static operator for (range: LongRange, body: (i:Long)=>void) {
	    Foreach.basic(range.min, range.max, body);
	}

	public static operator for (space: DenseIterationSpace_2, body: (Point(2))=>void) {
	    Foreach.basic(space.min0, space.max0,
			  space.min1, space.max1,
			  (i: Long, j: Long) => { body([i, j]); });
	}
    }


    static class Block {
	public static operator for (range: LongRange, body: (i:Long)=>void) {
	    Foreach.block(range.min, range.max, body);
	}

	public static operator for (space: DenseIterationSpace_2, body: (Point(2))=>void) {
	    Foreach.block(space, (i: Long, j: Long) => { body([i, j]); });
	}

	public static operator for (range: LongRange, body: (Point(2))=>void) {
	    Foreach.block(range.min, range.max, (min: Long, max: Long) => { body([min, max]); });
	}

	public static class Reduce[T] {
	    // public class NotReady extends CheckedException {}

	    val reduce: (T, T) => T;
	    private var result : Cell[T] = null;

	    public def this(reduce: (T, T) => T){
	    	this.reduce = reduce;
	    }

	    public operator for (range: LongRange, body: (Point(2))=>T) {
		this.result = new Cell(Foreach.blockReduce(range.min, range.max,
								(min:Long, max:Long) => { body([min, max]) },
								this.reduce));
	    }

	    public def value () // throws NotReady
	    {
		if (this.result == null) { throw new Exception(); }
		return this.result();
	    }

	}

	static class ReduceId[T] extends Reduce[T] {
	    val identity : T;

	    public def this(reduce: (T, T) => T, identity: T){
		super(reduce);
		this.identity = identity;
	    }

	    public operator for (range: LongRange, body: (i:Long)=>T) {
		this.result = new Cell(Foreach.blockReduce(range.min, range.max, body, this.reduce, this.identity));
	    }

	}
    }


}
