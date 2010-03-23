
public class ClockSieve {
	public static class Stream(clock c) {
		int value;
		boolean closed=false;
		public Stream() {
			property(clock.factory.clock());
		}
		
		public int get() /* clocked(c)*/ {
			if (closed) throw new RuntimeException();
			c.doNext(); // ensure that the writer has written
			int  v=value;
			c.doNext(); // signal you have read.
			return v;
		}
		public void put(int v) /*clocked(c)*/ {
			if (closed) throw new RuntimeException();
			c.doNext(); // ensure readers have read.
			value=v;
			c.doNext(); // signal that writer has written.
		}
		public void close() /*clocked(c)*/ {
			closed=true;
		}
		public int val() /*clocked(c)*/{
			return value;
		}
		public boolean closed() /*clocked(c)*/ {
			return closed;
		}
	}
	static Stream source(final int start, final int limit){
		final Stream out = new Stream();
		async clocked(out.c) {
			int v=start;
			while (v < limit) { out.put(v); v++;}
			out.close();
		}
		return out;
	}
	static void print(final String prefix, final Stream in)
/*clcoked(in.c)*/{
		async clocked(in.c) {
			in.c.doNext();
			while (! in.closed()) 
				System.err.println(prefix + in.get());
		}
	}
	static Stream filter(final int prime, final Stream in)
/*clocked(in.c)*/ {
		final Stream out = new Stream();
		async clocked(in.c, out.c) {
			//in.c.next();
			while (! in.closed()) {
				int p = in.get();
				if (p % prime != 0) 
					out.put(p);
			}
			out.close();
		}
		return out;
	}
	static Stream sieve(Stream in) {
		Stream out = new Stream();
		sieve(in, out);
		return out;
	}
	static void sieve(final Stream in, final Stream primes)
/*clocked(in.c,primes.c)*/ {
		async clocked (in.c, primes.c) {
			in.c.doNext();
			if (in.closed()) {
				primes.close();
			} else {
				final int v = in.get();
				primes.put(v);
				sieve(filter(v,in), primes);
			}
		}
	}
	
	
	public static void main(String[] s) {
	
		int n = 500;
		if (s.length >0) n = java.lang.Integer.parseInt(s[0]);
		final int N=n;
	    async {
		System.out.println("N=" + N);
		finish async // this async s necessary
			//print("Prime: ",
			  sieve(source(2,N));  //);
	}
 }
}
