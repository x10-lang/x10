public class Event {
	public static val logEvents=false;
	public static def event(s:String) {
		event(logEvents, s);
	}

	public static def event(verbose:Boolean, s:String) {
		if (verbose)
			Console.OUT.println("[Place(" + here.id+"), at " 
					+ System.nanoTime() + "] " + s);
	}
}