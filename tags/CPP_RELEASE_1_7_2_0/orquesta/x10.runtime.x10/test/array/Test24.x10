/**
 * Constant distribution
 *
 * (Was ConstDist)
 */

class Test24 extends TestArray {

    public void run() {
	
	Region/*(:rank==2&&zeroBased)*/ r = r(0,9,0,9);
        pr("r " + r);

	Dist/*(:rank==2&&onePlace==here)*/ d = Dist.makeConstant(r, here);
        pr("d " + d);

        Array_double/*(:rank==2&&onePlace==here&&zeroBased)*/ a =
            Array_double.make(Dist.makeConstant(r, here), Array_double.NO_INIT);
        pr("a " + a);

        Array_double/*(:rank==2&&onePlace==here&&zeroBased)*/ b =
            Array_double.make(Dist.makeConstant(r, here), Array_double.NO_INIT);
        pr("b " + b);
		

    }

}

