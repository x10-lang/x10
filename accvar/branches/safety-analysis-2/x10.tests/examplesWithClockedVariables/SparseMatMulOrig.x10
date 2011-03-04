import x10.util.Random;


	public class SparseMatMulOrig{
	
	public var nthreads: int;
	public var ytotal: double = 0.0D;

	private var size: int;
	const RANDOM_SEED = 10101010;
	private const datasizes_M: ValRail[int] = [ 100, 100000, 500000 ];
	private const datasizes_N: ValRail[int] = [ 100, 100000, 500000 ];
	private const datasizes_nz: ValRail[int] = [ 500, 500000, 2500000 ];
	private const SPARSE_NUM_ITER: int = 200;

	val R: Random! = new Random(RANDOM_SEED);

	var x: Rail[double]!;
	var val: Rail[double]!;
	var col: Rail[int]!;
	var row: Rail[int]!;
	var lowsum: Rail[int]!;
	var highsum: Rail[int]!;

	var y: Rail[double]!;

	public def this() = {
		this.nthreads = 8;
	}

	public def JGFsetsize(var size: int): void = {
		this.size = size;
	}

	public def JGFinitialise(): void = {
		val ds_N: int = datasizes_N(size);
		val ds_M: int = datasizes_M(size);
		val ds_nz: int = datasizes_nz(size);
		val r_nz: Region = [0..ds_nz-1];
		val r_nthreads: Region = [0..nthreads-1];
	
		

		val xin: Rail[double]! = init(Rail.make[double](ds_N), R) as Rail[double]!;
		x = Rail.make[double](xin.length, (i:int)=> { return xin(i); });
		//x = doubleRail.factory.doubleValueRail(xin); // value Rail.
		y = Rail.make[double](ds_M);      // distributed -- cvp

		var ilow: Rail[int]! = Rail.make[int](nthreads);
		var iup: Rail[int]! = Rail.make[int](nthreads);
		var sum: Rail[int]! = Rail.make[int](nthreads+1);
		val rowt: Rail[int]! = Rail.make[int](ds_nz);
		val colt: Rail[int]! = Rail.make[int](ds_nz);
		val valt: Rail[double]! = Rail.make[double](ds_nz);
		var sect: int = (ds_M + nthreads-1)/nthreads;

		var rowin: Rail[int]! = Rail.make[int](ds_nz);
		var colin: Rail[int]! = Rail.make[int](ds_nz);
		var valin: Rail[double]! = Rail.make[double](ds_nz);
		val lowsumin: Rail[int]! = Rail.make[int](nthreads);
		val highsumin: Rail[int]! = Rail.make[int](nthreads);

		for (val (i): Point in [0..ds_nz-1]) {
			rowin(i) = Math.abs(R.nextInt()) % ds_M;
			colin(i) = Math.abs(R.nextInt()) % ds_N;
			valin(i) = R.nextDouble();
		}

		// reorder Rails for parallel decomposition

		for (val (i): Point in r_nthreads) {
			ilow(i) = i*sect;
			iup(i) = ((i+1)*sect)-1;
			if (iup(i) > ds_M) iup(i) = ds_M;
		}

		for (val (i): Point in r_nz) for (val Point (j): Point in r_nthreads) if ((rowin(i) >= ilow(j)) && (rowin(i) <= iup(j)))
					sum(j+1)++;

		for (val (j): Point in r_nthreads) for (val Point (i): Point in [0..j]) {
				lowsumin(j) +=  sum(j-i);
				highsumin(j) +=  sum(j-i);
			}

		for (val (i): Point in r_nz) for (val Point (j): Point in r_nthreads) if ((rowin(i) >= ilow(j)) && (rowin(i) <= iup(j))) {
					val hsj = highsumin(j);
					rowt(hsj) = rowin(i);
					colt(hsj) = colin(i);
					valt(hsj) = valin(i);
					highsumin(j)++;
				}

		row = Rail.make[int](rowt.length, (i: int) => { return rowt(i); });
		col = Rail.make[int](colt.length, (i: int) => { return colt(i); });
		val = Rail.make[double](valt.length, (i: int): double => { return valt(i); });
		lowsum = Rail.make[int](lowsumin.length, (i: int) => { return lowsumin(i); });
		highsum = Rail.make[int](highsumin.length, (i: int) => { return highsumin(i); });
	
	}


	public def JGFkernel(): void = {
		test();
	}

	public def JGFvalidate(): void = {
		//double refval[] = { 75.02484945753453, 150.0130719633895, 749.5245870753752 };
	     val refval: ValRail[double] = [0.16484213206275583, 150.57514642658575, 749.5245870753752 ];
		var dev: double = Math.abs(ytotal - refval(size));
		if (dev > 1.0e-10) {
			Console.OUT.println("Validation failed");
			Console.OUT.println("ytotal = " + ytotal + "  refval =" + refval(size) + "  " + size);
			throw new Error("Validation failed");
		}
	}

	public def JGFtidyup(): void = {
		//System.gc();
	}

	public def JGFrun(var size: int): void = {
		//JGFInstrumentor.addTimer("Section2:SparseMatMulOrigt:Kernel", "Iterations", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		//JGFInstrumentor.addOpsToTimer("Section2:SparseMatMulOrigt:Kernel", (double) (SPARSE_NUM_ITER));

		//JGFInstrumentor.printTimer("Section2:SparseMatMulOrigt:Kernel");
	}

	private def init(var a: Rail[double]!, var R: Random!): Rail[double] = {
		for ((i) in 0..a.length-1) a(i) = R.nextDouble() * 1e-6;
		return a;
	}
	
	public  def test(): void = {
		val nz: int = val.length;
		
		//y, val, row, col, x, SPARSE_NUM_ITER, nthreads, lowsum, highsum

		//JGFInstrumentor.startTimer("Section2:SparseMatMulOrigt:Kernel");

		finish for (val (id): Point in [0..nthreads-1]) async 
			for (val (reps): Point in [0..SPARSE_NUM_ITER-1])
				 for ((i): Point in lowsum(id)..highsum(id)-1) {
				     val r = row(i);
				     val c = col(i);
				 	 atomic y(r) += x(c)*val(i);
				 	}

		//JGFInstrumentor.stopTimer("Section2:SparseMatMulOrigt:Kernel");
		for (val (i): Point in 0..nz-1) {
		    val r = row(i);
			ytotal += y(r);
		}
	}
	
	 public static def main(args:Rail[String]!)= {
		
		new SparseMatMulOrig (). JGFrun(0);
		
	}
	
}
	
	

