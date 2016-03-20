import x10.util.foreach.*;

class Test {


    public static def testForeach() {
	/*  Sequential */
	Console.OUT.println("*** Sequential ***");

	Sequential.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Sequential.for(i:Long, j:Long in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Sequential.for(slice:LongRange in 1 .. 10) {
	    var s: Long = 0;
	    for (i in slice) {
		s = s + i;
	    }
	    Console.OUT.println("sum = "+s);
	}

	val collector1 = new Sequential.Reducer((a:Long, b:Long)=> a + b, 0);
	collector1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	try {
	    Console.OUT.println(collector1.value());
	} catch (e: Exception) { assert(false); }

	try {
	    val res1 = Sequential.operator for(1..10, (a:Long, b:Long)=> a + b, (slice:LongRange)=>{
		    var s: Long = 0;
		    for (i in slice) {
			s = s + i;
		    }
		    s
		});
	    Console.OUT.println(res1);
	} catch (e: Exception) { assert(false); }


	/* Basic */
	Console.OUT.println("*** Basic ***");

	Basic.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Basic.for(i:Long, j:Long in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}


	/* Block */
	Console.OUT.println("*** Block ***");

	Block.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Block.for(i:Long, j:Long in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	val sumBlock: Cell[Long] = new Cell(0 as Long);
	Block.for (slice:LongRange in 1..10) {
	    var s: Long = 0;
	    for (i in slice) {
		s = s + i;
	    }
	    atomic { sumBlock() = sumBlock() + s; }
	}
	Console.OUT.println("sum = "+sumBlock());

	val collectorBlock1 = new Block.Reducer((a:Long, b:Long)=> a + b, 0);
	collectorBlock1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	Console.OUT.println(collectorBlock1.value());

	val res2 = Block.operator for(1..10, (a:Long, b:Long)=> a + b, (slice:LongRange)=>{
		var s: Long = 0;
		for (i in slice) {
		    s = s + i;
		}
		s
	    });
	Console.OUT.println(res2);
    }

    public static def main(Rail[String]) {
	Console.OUT.println("* Test Foreach *");
	testForeach();
    }

}
