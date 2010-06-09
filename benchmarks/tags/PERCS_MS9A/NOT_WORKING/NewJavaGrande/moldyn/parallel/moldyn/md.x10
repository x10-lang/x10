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
package moldyn;

/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
public class md extends x10.lang.Object {

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
  public var epot: double = 0.0;
  public var vir: double = 0.0;
  public var interactions: Int = 0;

  public var count: double = 0.0; // incremented in Particle, but maybe redundant

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
      one(i).xvelocity = r*randnum.v1;
      one(i+1).xvelocity  = r*randnum.v2;
    }

    for (var i: Int = 0; i < mdsize; i += 2) {
      val r  = randnum.seed();
      one(i).yvelocity = r*randnum.v1;
      one(i+1).yvelocity  = r*randnum.v2;
    }

    for (var i: Int = 0; i < mdsize; i += 2) {
      val r  = randnum.seed();
      one(i).zvelocity = r*randnum.v1;
      one(i+1).zvelocity  = r*randnum.v2;
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

      var vel: Double = 0.0;
      count = 0.0;
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

    val P = JGFMolDynBench.P;
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
    finish foreach ((j) in 0..P.length-1) {
      for ((k) in 0..mdsize-1) {
        P(j).one(k).xforce = t.one(k).xforce;
        P(j).one(k).yforce = t.one(k).yforce;
        P(j).one(k).zforce = t.one(k).zforce;
      }
      P(j).vir = t.vir;
      P(j).epot = t.epot;
      P(j).interactions = t.interactions;
    }
  }
}
