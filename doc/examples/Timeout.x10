class Timeout {
    public static operator while(ms: Long, body: ()=>void) {
	val deadline = System.currentTimeMillis() + ms;
	while (System.currentTimeMillis() < deadline) {
	    body();
	}
    }

    public static def main(Rail[String]) {
        val cpt = new Cell[Long](0);
        Timeout.while(10) {
            atomic { cpt() = cpt() + 1; }
        }
        Console.OUT.println(cpt());
    }

}
