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

import x10.lang.Clock;


/*
*
* (C) Copyright IBM Corporation 2006
*
*  This file is part of X10 Test.
*
*/


class Random {

 private var iseed: Int;
 private var v1: double, v2: double;

 public def this(iseed: Int, v1: double, v2: double) {
   this.iseed = iseed;
   this.v1 = v1;
   this.v2 = v2;
 }

 public def getv1(): double {
	 return v1;
 }
 
 public def getv2(): double {
	 return v2;
 }
 
 public def update() {
   val rand: double;
   val scale = 4.656612875e-10;

   var is1: Int, is2: Int, iss2: Int;
   val imult = 16807;
   val imod = 2147483647;

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

 public def seed() {
   var s: double;
   val r: double;

   s = 1.0;
   do {
     val u1: double, u2: double;
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
*                  Original version of this code by                       *
*                         Dieter Heermann                                 *
*                       converted to Java by                              *
*                Lorna Smith  (l.smith@epcc.ed.ac.uk)                     *
*                   (see copyright notice below)                          *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
 class md extends x10.lang.Object {

  public const ITERS: Int = 100;
  public const LENGTH: double = 50e-10;
  public const m: double = 4.0026;
  public const mu: double = 1.66056e-27;
  public const kb: double = 1.38066e-23;
  public const TSIM: double = 50;
  public const deltat: double = 5e-16;

  const den: double = 0.83134;
  const tref: double = 0.722;
  const h: double = 0.064;

  const irep: Int = 10;
  const istop: Int = 19;
  const iprint: Int = 10;
  const movemx: Int = 50;
  
  static val datasizes: ValRail[Int] = [4, 13];

  public val one: Rail[Particle!]!;
  public var epot: double = 0.0; // clock it
  public var vir: double = 0.0; // clock it
  public var interactions: Int = 0; // clock it

 // public var count: double = 0.0; // incremented in Particle, but maybe redundant

  val partsize: Int, mdsize: Int, mm: Int;

  val rcoff: double, rcoffs: double, side: double, sideh: double, a: double;
  val hsq: double, hsq2: double;

  var sc: double;
  val tscale: double;
  var ekin: double, ek: double, ts: double, sp: double;

  val vaver: double, vaverh: double;

  val rank: Int;
  val nprocess: Int;

  public def this(rank: Int, nprocess: Int) {
    this.rank = rank;
    this.nprocess = nprocess;

    val size = 0;

    /* Parameter determination */
    val mmlocal = datasizes(size);
    mm = mmlocal;

    val partsizelocal = mmlocal*mmlocal*mmlocal*4;
    
    partsize = partsizelocal;
    mdsize = partsizelocal;

    one = Rail.make[Particle!](partsizelocal);

    val sidelocal = Math.pow((partsizelocal/den), 0.3333333);
    side = sidelocal;

    val rcofflocal = mmlocal/4.0;
    rcoff = rcofflocal;

    a = sidelocal/mmlocal;
    sideh = sidelocal*0.5;

    val hsqlocal = h*h;
    hsq = hsqlocal;

    hsq2 = hsqlocal*0.5;
    // npartm = partsizelocal - 1;
    rcoffs = rcofflocal * rcofflocal;
    tscale = 16.0 / (1.0 * partsizelocal - 1.0);

    val vaverlocal = 1.13 * Math.sqrt(tref / 24.0);
    vaver = vaverlocal;

    vaverh = vaverlocal * h;
  }

  // the following constructor does not exist in v1.5, and is used to
  // initialize local variable t in allreduce(); t uses only the following
  // fields of md: mdsize, one, vir, epot, interactions
  public def this(mdsize: Int, one: Rail[Particle!]) {
    // dummy initializations for final fields
    rank = -1;
    nprocess = 0;

    mm =0;
    partsize = 0;
    side = 0.0;
    rcoff = 0.0;
    a = 0.0;
    sideh = 0.0;
    hsq = 0.0;
    hsq2 = 0.0;
    rcoffs = 0.0;
    tscale = 0.0;
    vaver = 0.0;
    vaverh = 0.0;
    this.one = one as Rail[Particle!]!;
    this.mdsize = mdsize;
  }

  public def initialise() {
    /* Particle Generation */
    var ijk: Int = 0;
    for ((lg) in 0..1) {
      for ((i) in 0..mm-1) {
        for ((j) in 0..mm-1) {
          for ((k) in 0..mm-1) {
            one(ijk) = new Particle((i*a+lg*a*0.5), (j*a+lg*a*0.5), (k*a), 
                                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            ijk = ijk + 1;
          }
        }
      }
    } 
    for ((lg) in 1..2) {
      for ((i) in 0..mm-1) {
        for ((j) in 0..mm-1) {
          for ((k) in 0..mm-1) {
            one(ijk) = new Particle((i*a+(2-lg)*a*0.5), (j*a+(lg-1)*a*0.5),
                                    (k*a+a*0.5), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            ijk = ijk + 1;
          }
        }
      }
    }

    /* Initialise velocities */
    val iseed = 0;
    val v1 = 0.0;
    val v2 = 0.0;

    val randnum = new Random(iseed, v1, v2);

    for (var i: Int = 0; i < mdsize; i += 2) {
      val r  = randnum.seed();
      one(i).xvelocity = r*randnum.getv1();
      one(i+1).xvelocity  = r*randnum.getv2();
    }

    for (var i: Int = 0; i < mdsize; i += 2) {
      val r  = randnum.seed();
      one(i).yvelocity = r*randnum.getv1();
      one(i+1).yvelocity  = r*randnum.getv2();
    }

    for (var i: Int = 0; i < mdsize; i += 2) {
      val r  = randnum.seed();
      one(i).zvelocity = r*randnum.getv1();
      one(i+1).zvelocity  = r*randnum.getv2();
    }

    /* velocity scaling */
    ekin = 0.0;
    sp = 0.0;

    for ((i) in 0..mdsize-1) {
      sp = sp + one(i).xvelocity;
    }
    sp = sp / mdsize;

    for ((i) in 0..mdsize-1) {
      one(i).xvelocity = one(i).xvelocity - sp;
      ekin = ekin + one(i).xvelocity*one(i).xvelocity;
    }

    sp = 0.0;
    for ((i) in 0..mdsize-1) {
      sp = sp + one(i).yvelocity;
    }
    sp = sp / mdsize;

    for ((i) in 0..mdsize-1) {
      one(i).yvelocity = one(i).yvelocity - sp;
      ekin = ekin + one(i).yvelocity*one(i).yvelocity;
    }

    sp = 0.0;
    for ((i) in 0..mdsize-1) {
      sp = sp + one(i).zvelocity;
    }
    sp = sp / mdsize;

    for ((i) in 0..mdsize-1) {
      one(i).zvelocity = one(i).zvelocity - sp;
      ekin = ekin + one(i).zvelocity*one(i).zvelocity;
    }

    ts = tscale * ekin;
    sc = h * Math.sqrt(tref/ts);

    for ((i) in 0..mdsize-1) {
      one(i).xvelocity = one(i).xvelocity * sc;
      one(i).yvelocity = one(i).yvelocity * sc;
      one(i).zvelocity = one(i).zvelocity * sc;
    }

    /* MD simulation */
  }

  public def runiters(C: Clock) {
    for ((move) in 0..movemx-1) {
      for ((i) in 0..mdsize-1) {
        one(i).domove(side); // move the particles and update velocities
      }

      epot = 0.0;
      vir = 0.0;

      for (var i: Int = 0+rank; i < mdsize; i += nprocess) {
        one(i).force(side, rcoff, mdsize, i, this); // compute forces
      }

      // global reduction on partial sums of the forces, epot, vir and
      // interactions 
      next;
      allreduce();
      next;

      var sum: double = 0.0;
      for ((i) in 0..mdsize-1) {
        sum = sum + one(i).mkekin(hsq2);  // scale forces, update velocities
      }
      ekin = sum/hsq;

      var vel: double = 0.0;
     // count = 0.0;
      for ((i) in 0..mdsize-1) {
        vel = vel + one(i).velavg(vaverh, h, this); /* average velocity */
      }
      vel = vel / h;

      // temperature scale if required
      if ((move < istop) && (((move+1) % irep) == 0)) {
        sc = Math.sqrt(tref / (tscale*ekin));
        for ((i) in 0..mdsize-1) {
          one(i).dscal(sc, 1);
        }
        ekin = tref / tscale;
      }

      // sum to get full potential energy and virial
      if (((move+1) % iprint) == 0) {
        ek = 24.0*ekin;
        epot = 4.0*epot;
        // etot = ek + epot; // UNUSED
        // temp = tscale * ekin; // UNUSED
        // pres = den * 16.0 * (ekin - vir) / mdsize; // UNUSED
        vel = vel / mdsize;
        // rp = (count / mdsize) * 100.0; // UNUSED
      }
    }
    next;
  }

  def allreduce() {
    // Place holder for now to emulate allreduce. To be optimized

    if (rank != 0) return;

    val P = Moldyn.P;
    
    val t = new md(mdsize, Rail.make[Particle!](mdsize));

    for ((k) in 0..mdsize-1) {
      t.one(k) = new Particle(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    // sum reduction
    for ((j) in 0..P.length-1) {
      for ((k) in 0..mdsize-1) {
        t.one(k).xforce += P(j).one(k).xforce;
        t.one(k).yforce += P(j).one(k).yforce;
        t.one(k).zforce += P(j).one(k).zforce;
      }
      t.vir += P(j).vir;
      t.epot += P(j).epot;
      t.interactions += P(j).interactions;
    }

    // broadcast
    finish for ((j) in 0..P.length-1) async {
      for ((k) in 0..mdsize-1) {
      	val Onejk = P(j).one(k);
        Onejk.xforce = t.one(k).xforce;
        Onejk.yforce = t.one(k).yforce;
        Onejk.zforce = t.one(k).zforce;
      }
      P(j).vir = t.vir;
      P(j).epot = t.epot;
      P(j).interactions = t.interactions;
    }
  }
}




class Particle {


 public var xcoord: double, ycoord: double, zcoord: double;
 public var xvelocity: double, yvelocity: double, zvelocity: double;
 public var xforce: double, yforce: double, zforce: double;

 public def this(xcoord: double, ycoord: double, zcoord: double, 
                 xvelocity: double, yvelocity: double, zvelocity: double, 
                 xforce: double, yforce: double, zforce: double) {
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

 public def domove(side: double) {
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

 public def force(side: double, rcoff: double, mdsize: Int, x: Int, md1: md!) {
   var sideh: double;
   var rcoffs: double;

   var xx: double, yy: double, zz: double, xi: double, yi: double, zi: double;
   var fxi: double, fyi: double, fzi: double;
   var rd: double, rrd: double, rrd2: double, rrd3: double, rrd4: double;
   var rrd6: double, rrd7: double, r148: double;
   var forcex: double, forcey: double, forcez: double;

   // var i: Int;

   sideh = 0.5*side;
   rcoffs = rcoff*rcoff;

   xi = xcoord;
   yi = ycoord;
   zi = zcoord;
   fxi = 0.0;
   fyi = 0.0;
   fzi = 0.0;

   // for (i = x + 1; i < mdsize; i++) {
   for ((i) in x+1..mdsize-1) {
     xx = xi - md1.one(i).xcoord;
     yy = yi - md1.one(i).ycoord;
     zz = zi - md1.one(i).zcoord;

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
       val forcexx = forcex;
       at (md1.one(i).xforce) md1.one(i).xforce = md1.one(i).xforce - forcexx;
       forcey = yy * r148;
       fyi = fyi + forcey;
       val forceyy = forcey;
       at (md1.one(i).yforce) md1.one(i).yforce = md1.one(i).yforce - forceyy;
       forcez = zz * r148;
       fzi = fzi + forcez;
       val forcezz = forcez;
       at (md1.one(i).zforce) md1.one(i).zforce = md1.one(i).zforce - forcezz;
       md1.interactions++;
     }
   }

   xforce = xforce + fxi;
   yforce = yforce + fyi;
   zforce = zforce + fzi;
 }

 public def mkekin(hsq2: double) {
   val sumt: double;

   xforce = xforce * hsq2;
   yforce = yforce * hsq2;
   zforce = zforce * hsq2;

   xvelocity = xvelocity + xforce;
   yvelocity = yvelocity + yforce;
   zvelocity = zvelocity + zforce;

   sumt = (xvelocity*xvelocity)+(yvelocity*yvelocity)+(zvelocity*zvelocity);
   return sumt;
 }

 public def velavg(vaverh: double, h: double, md1: md!) {
   val velt: double;
   val sq: double;

   sq = Math.sqrt(xvelocity*xvelocity + yvelocity*yvelocity + 
                  zvelocity*zvelocity);

   //if (sq > vaverh) { md1.count = md1.count + 1.0; }

   velt = sq;
   return velt;
 }

 public def dscal(sc: double, incx: Int) {
   xvelocity = xvelocity * sc;
   yvelocity = yvelocity * sc;
   zvelocity = zvelocity * sc;
 }
}




/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
public class Moldyn {
  public static val NTHREADS: Int = 4;
  val size: Int;

  public static val P = 
    ValRail.make[md!](NTHREADS, (i: Int) => new md(i, NTHREADS));

  public def this(size: Int) {
    this.size = size;
  }

  // not called any more
  public def JGFsetsize(size: Int) {
    // this.size = size; // error: cannot assign to final field size 
  }

  public def JGFinitialise() {
    finish foreach ((j) in 0..P.length-1) (P(j)).initialise();
  }

  public def JGFapplication() {
   // JGFInstrumentor.startTimer("Section3:MolDyn:Run");
    finish async {
      val C: Clock = Clock.make();
      foreach ((j) in 0..P.length-1) clocked(C) P(j).runiters(C);
    }
   // JGFInstrumentor.stopTimer("Section3:MolDyn:Run");
  }

  public def JGFvalidate() {
    finish foreach ((j) in 0..P.length-1) {
      val myNode = P(j);

      val refval: ValRail[double] = [ 275.97175611773514, 7397.392307839352 ];

      val dev: double = Math.abs(myNode.ek - refval(size));

      if (dev > 1.0e-10) {
        Console.OUT.println("Validation failed at thread "+j);
        Console.OUT.println("Kinetic Energy = " + myNode.ek + "  " + 
                            dev + "  " + refval(size));
        throw new Error("Validation failed");
      }
   
    }
    Console.OUT.println("Validated");
  }

  public def JGFtidyup() {
    // System.gc();
  }

  // immutable instance field size is initialized in the constructor, so
  // argument size is not used in the body of JGFrun; it exists because
  // Moldyn implements JGFSection3 which contains JGFrun(size: Int): void
  
  public def JGFrun(size: Int) {
    //JGFInstrumentor.addTimer("Section3:MolDyn:Total", "Solutions", size);
    //JGFInstrumentor.addTimer("Section3:MolDyn:Run", "Interactions", size);

    JGFsetsize(size);

    //JGFInstrumentor.startTimer("Section3:MolDyn:Total");

    JGFinitialise();
    JGFapplication();
    JGFvalidate();
    JGFtidyup();

    //JGFInstrumentor.stopTimer("Section3:MolDyn:Total");

    //val interactions = 0;
    //JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Run", interactions as double);
    //JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Total", 1);

    //JGFInstrumentor.printTimer("Section3:MolDyn:Run");
    //JGFInstrumentor.printTimer("Section3:MolDyn:Total");
  }

	public static def main(args:Rail[String]!)= {
		new Moldyn(0).JGFrun(0);
	}

}

