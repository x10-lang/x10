public class Fib  {
    int value;
    Fib(int v) { value = v;}
    void compute() {
	final Fib f1 = new Fib(value-1), f2= new Fib(value-2);
	finish {
	    async f1.compute();
	    async f2.compute();
	}
	value = f1.value+f2.value;
    }
    ...
}



