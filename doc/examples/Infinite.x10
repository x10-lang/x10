class Infinite {
    private static class Break extends Exception {}

    public static operator break () {
	throw new Break();
    }

    public static operator while (body:()=>void) {
	try {
	    while(true) {
		body();
	    }
	} catch (Break) {}
    }

    public static def main(Rail[String]) {
	Infinite.while() {
	    Infinite.break;
	}
	Console.OUT.println("OK");
    }

}
