	import x10.io.Console;
	public class Fib extends Object {
	    def this() {}
	    
	    def fib(n:int):int 
	    = n < 2 ? n : fib(n-1)+fib(n-2);
	    
	    def fact(n:long):long = n < 2 ? 1 : n*fact(n-1);
	    
	    public static def main(args:Rail[String]) {
	        if (args.length == 0) {
	            Console.OUT.println("Sorry. Run fib <n:int>");
	            return;
	        }
	        val n = Int.parseInt(args(0));
	        
	        Console.OUT.println("factfds(" + n + ")= " 
	                + new Fib().fact(n));
	    }
	    
	    
	}
