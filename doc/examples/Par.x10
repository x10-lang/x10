class Par {
    private static class Continue extends Exception {}

    public static operator continue () {
	throw new Continue();
    }

    public static operator for[T](c: Iterable[T], body:(T)=>void) {
	finish {
	    for(x in c) async {
		    try {
			body(x);
		    } catch (Continue) {}
		}
	}
    }

    public static def main(Rail[String]) {
    	val cpt = new Cell[Long](0);
	Par.for(i:Long in 1..10) {
	    if (i%2 == 0) { Par.continue; }
	    atomic { cpt() = cpt() + 1; }
	}
	Console.OUT.println(cpt());
    }
}
