public class HWSerialize {
	public static void main(String[] args) {
		final place h = here;
		final point q = [0,1];
		final region(:self.rank==1 && self.rect) r1 = [2:3];
		final region r2 = [[4:5],[6:7]];
		final dist dU = dist.UNIQUE;
		final dist dU1 = dist.UNIQUE | here;
		final dist dl = r2->here;
		final dist de = dl | dist.UNIQUE[1];
		final int value [:self.rank==1 && self.rect] ia = new int value [r1] (point [p]) { return p; };
		final place value [:self.rank==1 && self.rect] ip = new place value [r1] (point [p]) { return dist.UNIQUE[p]; };
//		final place value [:self.rank==1 && self.rect] ip = new place value [r1] (point [p]) { return h; };
		//final dist db = dist.block(r2);
		finish ateach (point p : dist.UNIQUE) {
			if (here.id == 0)
				System.out.println("q=["+q[0]+","+q[1]+"]");
		}
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111431
//@@X101X@@TCASE@@HWSerialize
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWSerialize.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@q=[0,1]
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
