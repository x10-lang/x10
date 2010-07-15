class Mystery {
  static class BoxedInt {
      var v:Int;
      def this (x:Int){v=x;}
      def apply()=v;
      public def toString()=""+v;
  }
    static def run() {
	/*@shared*/ val x=new BoxedInt(1), y= new BoxedInt(1);
	finish async {
	    val c = Clock.make();
	    async clocked(c) 
		while (true) {
		    val r = x();
		    next;
		    y.v=r;
		    Console.OUT.println("y="+ y);
		    next;
		}
	    while(true) {
		val s = x()+y();
		next;
		x.v = s;
		Console.OUT.println("x="+ x);
		next;
	    }
	}
    }
    public static def main(Rail[String]) {
	run();
    }
}