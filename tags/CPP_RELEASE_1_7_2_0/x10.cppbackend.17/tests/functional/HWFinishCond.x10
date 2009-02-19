public class HWFinishCond {
	int nplaces;
	public static void main(String[] s) {

		final dist d = dist.factory.unique(place.places);
		final HWFinishCond t = new HWFinishCond();
		t.nplaces = 0;
		if (s.length == 0)
			finish ateach (point p: d) {
				async (here) {
					t.nplaces++;
				}
			}
		// ensure that an activity ran in each place
		System.out.println("Test Succeeded = ? " + (t.nplaces == place.MAX_PLACES));

	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110747
//@@X101X@@TCASE@@HWFinishCond
//@@X101X@@VCODE@@FAIL_BUILD
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWFinishCond.cc
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
