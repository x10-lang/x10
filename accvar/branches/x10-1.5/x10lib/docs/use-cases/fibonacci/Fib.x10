class Fib {
	public static future<int> fib(future<int> f1, future<int> f2) {
		return future{ f1.force() + f2.force() };
	}

	public static future<int> fib(int val) {
		if(val<=1)
			return future{1};
		return add(fib(val-1), fib(val-2));
	}

	public static void main(String[] args) {
		int lo=Integer.parseInt(args[0]);
		int hi=Integer.parseInt(args[1]);

		for(int i=0; i<100; i++) {
			System.out.println(fib(i).force());
		}
	}
}
