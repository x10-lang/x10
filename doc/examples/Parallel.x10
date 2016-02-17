class Parallel {

    public static operator for[T](c: Iterable[T], body: (T)=>void) {
        for(x in c) {
            async { body(x); }
        }
    }

    public static def main(Rail[String]) {
        val cpt = new Cell[Long](0);
        Parallel.for(i:Long in 1..10) {
            atomic { cpt() = cpt() + i; }
        }
        Parallel.operator for(1..10, (i:Long)=> {
		atomic { cpt() = cpt() + i; }
	    });
        Console.OUT.println(cpt());
    }
}
