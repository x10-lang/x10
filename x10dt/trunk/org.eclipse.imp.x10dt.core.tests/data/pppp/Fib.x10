public class Fib {
  var n:int=0;
  def this(n:int) {
     this.n = n;
  }
  def fib() {
     if (n <= 2) {
    	 n= 1;
    	 return;
     }
     val f1 = new Fib(n-1);
     val f2 = new Fib(n-2);
     finish {
    	 async f1.fib();
         f2.fib(); 
     }
     n=f1.n + f2.n;
  }
	
  public static def main(args:Rail[String]) {
    if (args.length < 1) {
       Console.OUT.println("Usage: Fib <n>");
       return;
    }
		
    val n = Int.parseInt(args(0));
    val  f = new Fib(n);
    f.fib();
    Console.OUT.println("fib(" + n + ")= " + f.n);
    }
	
}
