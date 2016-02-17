class SignalingFinish {
    private var terminated : Boolean = false;
    public operator finish(body: ()=>void) {
        finish {
            body();
        }
        atomic { terminated = true; }
    }
    public def join() {
        when (terminated) {}
    }


    public static def main(Rail[String]) {
	val t = new SignalingFinish();
	async {
	    t.join();
	    Console.OUT.println("after");
	}
	t.finish {
	    Console.OUT.println("before");
	}
    }

}
