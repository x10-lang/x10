import x10.io.Console;
public class Fib extends Object {
	def this() {}
	
	def fibo(n:Int):Int = {
		var x:Int=0, y:Int=1;
		for (var i:Int=2; i <= n; i++) {
			val t = x+y;
			x=y;
			y=t;
		}
		return y;
	}
	def fib(n:Int):Int = n < 2 ? n : fib(n-1)+fib(n-2);
	
	def fact(n:long):long = n < 2 ? 1 : n*fact(n-1);
	
	public static def main(args:Array[String](1)) {
		if (args.size == 0) {
			Console.OUT.println("Sorry. Run fib <n:int>");
			return;
		}
		val n = Int.parseInt(args(0));
		val f = new Fib();
		
		for (var i:Int=2; i <= n; i++) {
			val fib = f.fib(i);
			val fibo = f.fibo(i);
			Console.OUT.print("fib(" + i + ")= " + fib );
			Console.OUT.println(fib == fibo ? "(ok)" : " fibo = " + fibo);
		}
	}
	
	
}
