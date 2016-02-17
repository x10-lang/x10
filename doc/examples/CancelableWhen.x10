class CancelableWhen {
    private var stop : Boolean = false;

    public operator when(condition: Cell[Boolean], body: ()=> void) {
        when (condition() || stop) {
            if (!stop) { body(); }
        }
    }

    public def cancel() {
        atomic { stop = true; }
    }

    public static def main(Rail[String]) {
	val c = new CancelableWhen();
	val b = new Cell[Boolean](false);
	finish {
	    async {
		c.when(b) { Console.OUT.println("KO"); }
	    }
	    c.cancel();
	}
    }

}
