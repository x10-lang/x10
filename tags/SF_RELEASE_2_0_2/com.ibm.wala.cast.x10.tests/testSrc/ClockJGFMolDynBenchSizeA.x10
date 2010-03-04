/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/



/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/





import java.util.Hashtable;
import java.text.NumberFormat;

/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


/**
  * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0)
  *
  * @author Vivek Sarkar (vsarkar@us.ibm.com)
  *
  * Porting issues identified:
  * 1) Extend x10.lang.Object
  */
  
  safe class JGFTimer extends x10.lang.Object  {

  public String name; 
  public String opname; 
  public double time; 
  public double opcount; 
  public long calls; 
  public int size = -1;
  
  private long start_time;
  private boolean on; 

  public JGFTimer(String name, String opname){
    this.name = name;
    this.opname = opname;
    reset(); 
  }

  public JGFTimer(String name, String opname, int size){
    this.name = name;
    this.opname = opname;
    this.size = size;
    reset();
  }

  public JGFTimer(String name){
    this(name,""); 
  }



  public  void start(){
    if (on) System.out.println("Warning timer " + name + " was already turned on");
    on = true; 
    start_time = System.currentTimeMillis();
  }


  public  void stop(){
    time += (double) (System.currentTimeMillis()-start_time) / 1000.;
    if (!on) System.out.println("Warning timer " + name + " wasn't turned on");
    calls++;
    on = false;  
  }

  public  void addops(double count){
    opcount += count;
  } 

  public  void reset(){
    time = 0.0; 
    calls = 0; 
    opcount = 0; 
    on = false;
  }

  public double perf(){
    return opcount / time; 
  }

  public void longprint(){
      System.out.println("Timer            Calls         Time(s)       Performance("+opname+"/s)");   
     System.out.println(name + "           " + calls +    "           "  +  time + "        " + this.perf());
  }

  public void print(){
    if (opname.equals("")) {
      System.out.println(name + "   " + time + " (s)");
    }
    else {

      switch(size) {
      case 0:
      System.out.println(name + ":SizeA" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      case 1:
      System.out.println(name + ":SizeB" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      case 2:
      System.out.println(name + ":SizeC" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      default:
      System.out.println(name + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      }

    }
  }


  public void printperf(){

     String name;
     name = this.name; 

     // pad name to 40 characters
     while ( name.length() < 40 ) name = name + " "; 
     
     System.out.println(name + "\t" + (float)this.perf() + "\t"
			+ " ("+opname+"/s)");  
  }

}





  safe class JGFInstrumentor extends x10.lang.Object {

  private static final Hashtable timers;
  private static final Hashtable data; 

  static {
    timers = new Hashtable();
    data = new Hashtable(); 
  }

  public static atomic void addTimer (String name){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			 " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name));
    }
  }
    
  public static atomic  void addTimer(String name, String opname){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name + 
			 " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name,opname));
    }
    
  }

  public static atomic void addTimer (String name, String opname, int size){

    if (timers.containsKey(name)) {
      System.out.println("JGFInstrumentor.addTimer: warning -  timer " + name +
                         " already exists");
    }
    else {
      timers.put(name, new JGFTimer(name,opname,size));
    }

  }

  public static atomic void startTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).start();
    }
    else {
      System.out.println("JGFInstrumentor.startTimer: failed -  timer " + name + 
			 " does not exist");
    }

  }

  public static atomic void stopTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).stop();
    }
    else {
      System.out.println("JGFInstrumentor.stopTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }

  public static atomic void addOpsToTimer(String name, double count){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).addops(count);
    }
    else {
      System.out.println("JGFInstrumentor.addOpsToTimer: failed -  timer " + name + 
			 " does not exist");
    }
  }  

  public static atomic double readTimer(String name){
    double time; 
    if (timers.containsKey(name)) {
      time = ((JGFTimer) timers.get(name)).time;
    }
    else {
      System.out.println("JGFInstrumentor.readTimer: failed -  timer " + name + 
			 " does not exist");
       time = 0.0; 
    }
    return time; 
  }  

  public static atomic void resetTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).reset();
    }
    else {
      System.out.println("JGFInstrumentor.resetTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void printTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).print();
    }
    else {
      System.out.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void printperfTimer(String name){
    if (timers.containsKey(name)) {
      ((JGFTimer) timers.get(name)).printperf();
    }
    else {
      System.out.println("JGFInstrumentor.printTimer: failed -  timer " + name +
 			 " does not exist");
    }
  }
  
  public static atomic void storeData(String name, x10.lang.Object obj){
    data.put(name,obj); 
  }

  public static atomic void retrieveData(String name, x10.lang.Object obj){
      obj = (x10.lang.Object) data.get(name); 
  }
  
  public static atomic safe void printHeader(int section, int size) {
  	printHeader(section, size, 1);
  }

  public static atomic safe void printHeader(int section, int size,int nthreads) {

    String header, base;

    header = "";
    base = "Java Grande Forum Thread Benchmark Suite - Version 1.0 - Section ";

    switch (section) {
    case 1:
      header = base + "1";
      break;
    case 2:
      switch (size) {
      case 0:
        header = base + "2 - Size A";
        break;
      case 1:
        header = base + "2 - Size B";
        break;
      case 2:
        header = base + "2 - Size C";
        break;
      }
      break;
    case 3:
      switch (size) {
      case 0:
        header = base + "3 - Size A";
        break;
      case 1:
        header = base + "3 - Size B";
        break;
      }
      break;
    }

    System.out.println(header);

    if (nthreads == 1) {
      System.out.println("Executing on " + nthreads + " thread");
    }
    else {
      System.out.println("Executing on " + nthreads + " threads");
    }

    System.out.println("");

  }

}



/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
 
    interface JGFSection3 {
  public void JGFsetsize(int size);
  public void JGFinitialise();
  public void JGFapplication();
  public void JGFvalidate();
  public void JGFtidyup();  
  public void JGFrun(int size); 
}
 
 
   class Random {

	public int iseed;
	public double v1, v2;

	public Random(int iseed, double v1, double v2) {
		this.iseed = iseed;
		this.v1 = v1;
		this.v2 = v2;
	}

	public double update() {
		double rand;
		double scale = 4.656612875e-10;

		int is1, is2, iss2;
		int imult = 16807;
		int imod = 2147483647;

		if (iseed <= 0) { iseed = 1; }

		is2 = iseed % 32768;
		is1 = (iseed-is2)/32768;
		iss2 = is2 * imult;
		is2 = iss2 % 32768;
		is1 = (is1*imult+(iss2-is2)/32768) % (65536);

		iseed = (is1*32768+is2) % imod;

		rand = scale * iseed;

		return rand;
	}

	public double seed() {
		double s, u1, u2, r;
		s = 1.0;
		do {
			u1 = update();
			u2 = update();

			v1 = 2.0 * u1 - 1.0;
			v2 = 2.0 * u2 - 1.0;
			s = v1*v1 + v2*v2;
		} while (s >= 1.0);

		r = Math.sqrt(-2.0*Math.log(s)/s);

		return r;
	}
}

 
 
 
   class Particle {

	public double xcoord, ycoord, zcoord;
	public double xvelocity, yvelocity, zvelocity;
	public double xforce, yforce, zforce;

	public Particle(double xcoord, double ycoord, double zcoord, double xvelocity,
					double yvelocity, double zvelocity, double xforce,
					double yforce, double zforce)
	{
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.zcoord = zcoord;
		this.xvelocity = xvelocity;
		this.yvelocity = yvelocity;
		this.zvelocity = zvelocity;
		this.xforce = xforce;
		this.yforce = yforce;
		this.zforce = zforce;
	}

	public void domove(double side) {
		xcoord = xcoord + xvelocity + xforce;
		ycoord = ycoord + yvelocity + yforce;
		zcoord = zcoord + zvelocity + zforce;

		if (xcoord < 0) { xcoord = xcoord + side; }
		if (xcoord > side) { xcoord = xcoord - side; }
		if (ycoord < 0) { ycoord = ycoord + side; }
		if (ycoord > side) { ycoord = ycoord - side; }
		if (zcoord < 0) { zcoord = zcoord + side; }
		if (zcoord > side) { zcoord = zcoord - side; }

		xvelocity = xvelocity + xforce;
		yvelocity = yvelocity + yforce;
		zvelocity = zvelocity + zforce;

		xforce = 0.0;
		yforce = 0.0;
		zforce = 0.0;
	}

	public void force(double side, double rcoff, int mdsize, int x, md md1) {
		double sideh;
		double rcoffs;

		double xx, yy, zz, xi, yi, zi, fxi, fyi, fzi;
		double rd, rrd, rrd2, rrd3, rrd4, rrd6, rrd7, r148;
		double forcex, forcey, forcez;

		int i;

		sideh = 0.5*side;
		rcoffs = rcoff*rcoff;

		xi = xcoord;
		yi = ycoord;
		zi = zcoord;
		fxi = 0.0;
		fyi = 0.0;
		fzi = 0.0;

		for (i = x + 1; i < mdsize; i++) {
			xx = xi - md1.one[i].xcoord;
			yy = yi - md1.one[i].ycoord;
			zz = zi - md1.one[i].zcoord;

			if (xx < (-sideh)) { xx = xx + side; }
			if (xx > (sideh))  { xx = xx - side; }
			if (yy < (-sideh)) { yy = yy + side; }
			if (yy > (sideh))  { yy = yy - side; }
			if (zz < (-sideh)) { zz = zz + side; }
			if (zz > (sideh))  { zz = zz - side; }

			rd = xx*xx + yy*yy + zz*zz;

			if (rd <= rcoffs) {
				rrd = 1.0/rd;
				rrd2 = rrd*rrd;
				rrd3 = rrd2*rrd;
				rrd4 = rrd2*rrd2;
				rrd6 = rrd2*rrd4;
				rrd7 = rrd6*rrd;
				md1.epot = md1.epot + (rrd6 - rrd3);
				r148 = rrd7 - 0.5*rrd4;
				md1.vir = md1.vir - rd*r148;
				forcex = xx * r148;
				fxi = fxi + forcex;
				md1.one[i].xforce = md1.one[i].xforce - forcex;
				forcey = yy * r148;
				fyi = fyi + forcey;
				md1.one[i].yforce = md1.one[i].yforce - forcey;
				forcez = zz * r148;
				fzi = fzi + forcez;
				md1.one[i].zforce = md1.one[i].zforce - forcez;
				md1.interactions++;
			}
		}

		xforce = xforce + fxi;
		yforce = yforce + fyi;
		zforce = zforce + fzi;
	}

	public double mkekin(double hsq2) {
		double sumt = 0.0;

		xforce = xforce * hsq2;
		yforce = yforce * hsq2;
		zforce = zforce * hsq2;

		xvelocity = xvelocity + xforce;
		yvelocity = yvelocity + yforce;
		zvelocity = zvelocity + zforce;

		sumt = (xvelocity*xvelocity)+(yvelocity*yvelocity)+(zvelocity*zvelocity);
		return sumt;
	}

	public double velavg(double vaverh, double h, md md1) {
		double velt;
		double sq;

		sq = Math.sqrt(xvelocity*xvelocity + yvelocity*yvelocity +
				zvelocity*zvelocity);

		if (sq > vaverh) { md1.count = md1.count + 1.0; }

		velt = sq;
		return velt;
	}

	public void dscal(double sc, int incx) {
		xvelocity = xvelocity * sc;
		yvelocity = yvelocity * sc;
		zvelocity = zvelocity * sc;
	}
}

 
 
 
 
class md extends x10.lang.Object {

	public const int ITERS = 100;
	public const double LENGTH = 50e-10;
	public const double m = 4.0026;
	public const double mu = 1.66056e-27;
	public const double kb = 1.38066e-23;
	public const double TSIM = 50;
	public const double deltat = 5e-16;
	public Particle one [] = new Particle[0];
	public double epot = 0.0;
	public double vir = 0.0;
	public double count = 0.0;
	int size;
	//int datasizes[] = { 8, 13 };
	int datasizes[] = { 4, 13 };

	public int interactions = 0;

	int i, j, k, lg, mdsize, move, mm;

	double l, rcoff, rcoffs, side, sideh, hsq, hsq2, vel;
	double a, r, sum, tscale, sc, ekin, ek, ts, sp;
	double den = 0.83134;
	double tref = 0.722;
	double h = 0.064;
	double vaver, vaverh, rand;
	double etot, temp, pres, rp;
	double u1, u2, v1, v2, s;

	int ijk, npartm, partsize, iseed, tint;
	int irep = 10;
	int istop = 19;
	int iprint = 10;
	int movemx = 50;

	Random randnum;
	int rank;
	int nprocess;

	public void initialise(int rank, int nprocess) {
		this.rank = rank;
		this.nprocess = nprocess;

		/* Parameter determination */
		mm = datasizes[size];
		partsize = mm*mm*mm*4;
		mdsize = partsize;
		one = new Particle [mdsize];
		l = LENGTH;

		side = Math.pow((mdsize/den), 0.3333333);
		rcoff = mm/4.0;

		a = side/mm;
		sideh = side*0.5;
		hsq = h*h;
		hsq2 = hsq*0.5;
		npartm = mdsize - 1;
		rcoffs = rcoff * rcoff;
		tscale = 16.0 / (1.0 * mdsize - 1.0);
		vaver = 1.13 * Math.sqrt(tref / 24.0);
		vaverh = vaver * h;

		/* Particle Generation */
		ijk = 0;
		for (lg = 0; lg <= 1; lg++) {
			for (i = 0; i<mm; i++) {
				for (j = 0; j<mm; j++) {
					for (k = 0; k<mm; k++) {
						one[ijk] = new Particle((i*a+lg*a*0.5), (j*a+lg*a*0.5), (k*a),
								0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
						ijk = ijk + 1;
					}
				}
			}
		}
		for (lg = 1; lg <= 2; lg++) {
			for (i = 0; i<mm; i++) {
				for (j = 0; j<mm; j++) {
					for (k = 0; k<mm; k++) {
						one[ijk] = new Particle((i*a+(2-lg)*a*0.5), (j*a+(lg-1)*a*0.5),
								(k*a+a*0.5), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
						ijk = ijk + 1;
					}
				}
			}
		}

		/* Initialise velocities */
		iseed = 0;
		v1 = 0.0;
		v2 = 0.0;

		randnum = new Random(iseed, v1, v2);

		for (i = 0; i<mdsize; i += 2) {
			r  = randnum.seed();
			one[i].xvelocity = r*randnum.v1;
			one[i+1].xvelocity  = r*randnum.v2;
		}

		for (i = 0; i<mdsize; i += 2) {
			r  = randnum.seed();
			one[i].yvelocity = r*randnum.v1;
			one[i+1].yvelocity  = r*randnum.v2;
		}

		for (i = 0; i<mdsize; i += 2) {
			r  = randnum.seed();
			one[i].zvelocity = r*randnum.v1;
			one[i+1].zvelocity  = r*randnum.v2;
		}

		/* velocity scaling */
		ekin = 0.0;
		sp = 0.0;

		for (i = 0; i<mdsize; i++) {
			sp = sp + one[i].xvelocity;
		}
		sp = sp / mdsize;

		for (i = 0; i<mdsize; i++) {
			one[i].xvelocity = one[i].xvelocity - sp;
			ekin = ekin + one[i].xvelocity*one[i].xvelocity;
		}

		sp = 0.0;
		for (i = 0; i<mdsize; i++) {
			sp = sp + one[i].yvelocity;
		}
		sp = sp / mdsize;

		for (i = 0; i<mdsize; i++) {
			one[i].yvelocity = one[i].yvelocity - sp;
			ekin = ekin + one[i].yvelocity*one[i].yvelocity;
		}

		sp = 0.0;
		for (i = 0; i<mdsize; i++) {
			sp = sp + one[i].zvelocity;
		}
		sp = sp / mdsize;

		for (i = 0; i<mdsize; i++) {
			one[i].zvelocity = one[i].zvelocity - sp;
			ekin = ekin + one[i].zvelocity*one[i].zvelocity;
		}

		ts = tscale * ekin;
		sc = h * Math.sqrt(tref/ts);

		for (i = 0; i<mdsize; i++) {

			one[i].xvelocity = one[i].xvelocity * sc;
			one[i].yvelocity = one[i].yvelocity * sc;
			one[i].zvelocity = one[i].zvelocity * sc;
		}

		/* MD simulation */
	}

	public void runiters(final clock C) {
		int n = 0;
		move = 0;
		for (move = 0; move<movemx; move++) {

			for (i = 0; i<mdsize; i++) {
				one[i].domove(side);        /* move the particles and update velocities */
			}

			epot = 0.0;
			vir = 0.0;

			for (i = 0+rank; i<mdsize; i += nprocess) {
				one[i].force(side, rcoff, mdsize, i, this);  /* compute forces */
			}

			/* global reduction on partial sums of the forces, epot, vir and interactions */
			C.doNext();
			allreduce();
			C.doNext();

			sum = 0.0;

			for (i = 0; i<mdsize; i++) {
				sum = sum + one[i].mkekin(hsq2);    /*scale forces, update velocities */
			}

			ekin = sum/hsq;

			vel = 0.0;
			count = 0.0;

			for (i = 0; i<mdsize; i++) {
				vel = vel + one[i].velavg(vaverh, h, this); /* average velocity */
			}

			vel = vel / h;

			/* tmeperature scale if required */
			if ((move < istop) && (((move+1) % irep) == 0)) {
				sc = Math.sqrt(tref / (tscale*ekin));
				for (i = 0; i<mdsize; i++) {
					one[i].dscal(sc, 1);
				}
				ekin = tref / tscale;
			}

			/* sum to get full potential energy and virial */
			if (((move+1) % iprint) == 0) {
				ek = 24.0*ekin;
				epot = 4.0*epot;
				etot = ek + epot;
				temp = tscale * ekin;
				pres = den * 16.0 * (ekin - vir) / mdsize;
				vel = vel / mdsize;
				rp = (count / mdsize) * 100.0;
			}
		}
		C.doNext();
	}

	void allreduce() {
		// Place holder for now to emulate allreduce. To be optimized

		if (rank != 0)  return;
		final md[.] P = JGFMolDynBench.P;
		final md t = new md();
		t.mdsize = mdsize;
		t.one = new Particle[mdsize];
		for (point [k]: [0:(mdsize-1)]) t.one[k] = new Particle(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		// sum reduction
		for (point [j]: P) {
			for (point [k]: [0:(mdsize-1)]) {
				t.one[k].xforce += P[j].one[k].xforce;
				t.one[k].yforce += P[j].one[k].yforce;
				t.one[k].zforce += P[j].one[k].zforce;
			}
			t.vir += P[j].vir;
			t.epot += P[j].epot;
			t.interactions += P[j].interactions;
		}
		// broadcast
		finish foreach (point [j]: P) {
			for (point [k]: [0:(mdsize-1)]) {
				P[j].one[k].xforce = t.one[k].xforce;
				P[j].one[k].yforce = t.one[k].yforce;
				P[j].one[k].zforce = t.one[k].zforce;
			}
			P[j].vir = t.vir;
			P[j].epot = t.epot;
			P[j].interactions = t.interactions;
		}
	}
}





/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
  class JGFMolDynBench extends md implements JGFSection3 {

	//int size;
	const int NTHREADS = 4;
	public static final dist D = [0:NTHREADS-1]->here;

	public static final md[.] P = new md[D] (point [j]) { return new md(); };

	public JGFMolDynBench() {
	}

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
		finish foreach (point [j]: D) (P[j]).initialise(j, NTHREADS);
	}

	public void JGFapplication() {
		
		JGFInstrumentor.startTimer("Section3:MolDyn:Run");
		finish async {
			final clock C = clock.factory.clock();
			foreach (point [j]: D) clocked(C) {
				 md m = (1>0)? P[j]: new md(); /*Hack FIXME*/
				 m.runiters(C);
			}
		}
		JGFInstrumentor.stopTimer("Section3:MolDyn:Run");
	}

	public void JGFvalidate() {
		finish foreach (point [j]: D) {
			md myNode = P[j];
			// double refval[] = { 1731.4306625334357, 7397.392307839352 };
			double refval[] = { 275.97175611773514, 7397.392307839352 };
			double dev = Math.abs(myNode.ek - refval[size]);
			if (dev > 1.0e-10 ) {
				System.out.println("Validation failed at thread "+j);
				System.out.println("Kinetic Energy = " + myNode.ek + "  " + dev + "  " + refval[size]);
				throw new Error("Validation failed");
			}
		}
	}

	public void JGFtidyup() {
		System.gc();
	}

	public void JGFrun(int size) {
		JGFInstrumentor.addTimer("Section3:MolDyn:Total", "Solutions", size);
		JGFInstrumentor.addTimer("Section3:MolDyn:Run", "Interactions", size);

		JGFsetsize(size);

		JGFInstrumentor.startTimer("Section3:MolDyn:Total");

		JGFinitialise();
		JGFapplication();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.stopTimer("Section3:MolDyn:Total");

		JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Run", (double) interactions);
		JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Total", 1);

		JGFInstrumentor.printTimer("Section3:MolDyn:Run");
		JGFInstrumentor.printTimer("Section3:MolDyn:Total");
	}
}






public class ClockJGFMolDynBenchSizeA{

	public boolean run() {
		JGFInstrumentor.printHeader(3, 0);
		JGFMolDynBench mold = new JGFMolDynBench();
		mold.JGFrun(0);
		return true;
	}

	public static void main(String[] args) {
		async {
		new ClockJGFMolDynBenchSizeA().run();
		}
	}
}

