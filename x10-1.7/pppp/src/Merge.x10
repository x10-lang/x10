class Merge {
    static def merge(a:Sink[Int], b:Sink[Int]) {
	val s = new SimpleStreamInp();
	merge(a, b, s.source());
	return s.sink();
    } 
    static  def merge(a:Sink[Int], b:Sink[Int], c:Source[Int]) {
	async 
	    try {
		finish {
		    async while (true) s.put(a.get()); 
		    while(true) s.put(b.get());
		} catch (Exception) {
		} finally {
		    s.close();
		}
	    }
    }
}

