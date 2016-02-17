class Test {

    public static def testForeach0() {
	/*  Sequential */
	Console.OUT.println("*** Sequential ***");

	Foreach0.Sequential.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach0.Sequential.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Foreach0.Sequential.for([min, max]:Point(2) in 1..10) {
	    var s: Long = 0;
	    for (i in min..max) {
		s = s + i;
	    }
	    Console.OUT.println("sum = "+s);
	}

	val collector1 = new Foreach0.Sequential.ReduceId((a:Long, b:Long)=> a + b, 0);
	collector1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	try {
	    Console.OUT.println(collector1.value());
	} catch (e: Exception) { assert(false); }

	val collector2 = new Foreach0.Sequential.Reduce[Long]((a:Long, b:Long)=> a + b);
	collector2.for([min, max]:Point(2) in 1..10) {
	    var s: Long = 0;
	    for (i in min..max) {
		s = s + i;
	    }
	    s
	}
	try {
	    Console.OUT.println(collector2.value());
	} catch (e: Exception) { assert(false); }


	/* Basic */
	Console.OUT.println("*** Basic ***");

	Foreach0.Basic.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach0.Basic.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}


	/* Block */
	Console.OUT.println("*** Block ***");

	Foreach0.Block.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach0.Block.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	val sumBlock: Cell[Long] = new Cell(0 as Long);
	Foreach0.Block.for([min, max]:Point(2) in 1..10) {
	    var s: Long = 0;
	    for (i in min..max) {
		s = s + i;
	    }
	    atomic { sumBlock() = sumBlock() + s; }
	}
	Console.OUT.println("sum = "+sumBlock());

	val collectorBlock1 = new Foreach0.Block.ReduceId((a:Long, b:Long)=> a + b, 0);
	collectorBlock1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	Console.OUT.println(collectorBlock1.value());

	val collectorBlock2 = new Foreach0.Block.Reduce[Long]((a:Long, b:Long)=> a + b);
	collectorBlock2.for([min, max]:Point(2) in 1..10) {
	    var s: Long = 0;
	    for (i in min..max) {
		s = s + i;
	    }
	    s
	}
	Console.OUT.println(collectorBlock2.value());
    }

    public static def testForeach1() {
	/*  Sequential */
	Console.OUT.println("*** Sequential ***");

	Foreach1.Sequential.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach1.Sequential.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Foreach1.Sequential.slice(1, 10, (min:Long, max:Long) => {
		var s: Long = 0;
		for (i in min..max) {
		    s = s + i;
		}
	    Console.OUT.println("sum = "+s);
	    });

	val collector1 = new Foreach1.Sequential.Reduce((a:Long, b:Long)=> a + b, 0);
	collector1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	try {
	    Console.OUT.println(collector1.value());
	} catch (e: Exception) { assert(false); }

	try {
	    val res1 = Foreach1.Sequential.reduceSlice(1, 10, (min:Long, max:Long)=>{
		    var s: Long = 0;
		    for (i in min..max) {
			s = s + i;
		    }
		    s
		}, (a:Long, b:Long)=> a + b);
	    Console.OUT.println(res1);
	} catch (e: Exception) { assert(false); }


	/* Basic */
	Console.OUT.println("*** Basic ***");

	Foreach1.Basic.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach1.Basic.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}


	/* Block */
	Console.OUT.println("*** Block ***");

	Foreach1.Block.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	}

	Foreach1.Block.for([i, j]:Point(2) in 1..3 * 1..4) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	val sumBlock: Cell[Long] = new Cell(0 as Long);
	Foreach1.Block.slice(1, 10, (min:Long, max:Long)=>{
		var s: Long = 0;
		for (i in min..max) {
		    s = s + i;
		}
		atomic { sumBlock() = sumBlock() + s; }
	    });
	Console.OUT.println("sum = "+sumBlock());

	val collectorBlock1 = new Foreach1.Block.Reduce((a:Long, b:Long)=> a + b, 0);
	collectorBlock1.for(i:Long in 1..10) {
	    Console.OUT.println(i);
	    i
	}
	Console.OUT.println(collectorBlock1.value());

	val res2 = Foreach1.Block.reduceSlice(1, 10, (min:Long, max:Long)=>{
		var s: Long = 0;
		for (i in min..max) {
		    s = s + i;
		}
		s
	    }, (a:Long, b:Long)=> a + b);
	Console.OUT.println(res2);
    }

    public static def main(Rail[String]) {
	Console.OUT.println("* Test Foreach0 *");
	testForeach0();
	Console.OUT.println("* Test Foreach1 *");
	testForeach1();
    }

}
