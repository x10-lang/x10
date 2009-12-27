package x10.lib.stream;
import x10.io.Printer;

public class PrintSink[T] {
    val p:Printer;
    var ix:Sink[T];

    def this(p:Printer, ix:Sink[T]) {
	this.p = p;
	this.ix=ix;
    }
    def run() {
	async 
	    try {
		while(true) 
		    p.println(ix.get());
	    } catch (StreamClosedException) {
	    }
    }
    public static def make[T](ix:Sink[T])=make(Console.OUT, ix);
    public static def make[T](p:Printer, ix:Sink[T]) {
	val t = new PrintSink[T](p, ix);
	t.run();
	return t;
    }
      
}