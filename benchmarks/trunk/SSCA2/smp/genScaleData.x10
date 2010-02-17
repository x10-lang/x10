package ssca2;

import x10.util.*;

public class genScaleData  {

  public static def compute():  Pair[Double, defs.graphSDG] {

    val GLOBALS = at (defs.container) (defs.container.globals);

    var time: Double;

    val n = GLOBALS.N;
    val m  = GLOBALS.M;

    time = util.get_seconds();

    val memBlock = Rail.make[types.LONG_T](defs.ARRAY_INIT_SIZE*n);
    val SDGedges = Rail.make[GrowableRail[types.LONG_T]](n, (i:Int)=>new GrowableRail[types.LONG_T](0));
     
    (SDGedges(0))(0) = 0;

    val numEdgesPerPhase = m < (1<<18) ? m : (1<<18) as types.LONG_T;

    val numRandVals = numEdgesPerPhase*5*GLOBALS.SCALE;

    util.srand48(GLOBALS.SCALE*34535);
 
    val rv = Rail.make[types.DOUBLE_T](numRandVals);

   var numEdgesAdded: Int = 0;
   while (numEdgesAdded < m) {
     for ((i) in   0..numRandVals-1)  rv(i) = util.drand48();
       val numEdgesToBeAdded = m-numEdgesAdded < numEdgesPerPhase ? m - numEdgesAdded:numEdgesPerPhase;

     var count: Int = 0;

     for ((i) in  0..numEdgesToBeAdded-1) {
       var u: Int = 1;
       var v: Int = 1;
       var step: Int = n/2;
       var av: Double = defs.A;
       var bv: Double = defs.B;
       var cv: Double = defs.C;
       var dv: Double = defs.D;

       x10.io.Console.OUT.println("SCALE " + GLOBALS.SCALE);
       for ((j) in 0..GLOBALS.SCALE-1) {
         val uv = choosePartition(rv(5*(i*GLOBALS.SCALE+j)), u, v, step, av, bv, cv, dv);
         u = uv.u;
         v = uv.v;
         x10.io.Console.OUT.println("u, v" + u + " " + v);
         step = step / 2;
         val abcd = varyParams(rv, 5*(i*GLOBALS.SCALE+j), av, bv, cv, dv);
         av = abcd.av;
         bv = abcd.bv;
         cv = abcd.cv;
         dv = abcd.dv;
       }

       if (u != v) {
         val pos = util.dynArraySortedInsertPos(SDGedges(u-1), v-1);
         //x10.io.Console.OUT.println("position " + pos);
         if (pos != -1) {
           util.dynArraySortedInsert(SDGedges(u-1), v-1, pos);
           x10.io.Console.OUT.println( "u, v " + u + " " + v+ " " +  SDGedges(u-1)(pos));
           count++;
         }
      } 
     }

     numEdgesAdded = numEdgesAdded + count;
     x10.io.Console.OUT.println("numEdgesAdded: " + numEdgesAdded);
   }

   x10.io.Console.OUT.println("done" );

   val srcs = Rail.make[types.VERT_T](m);
   val dests = Rail.make[types.VERT_T](m);

  var count: Int = 0;
   for ((i) in   0..n-1) {
     for ((j) in  0..SDGedges(i).length()-1) {
       srcs(count) = i;
       dests(count) = (SDGedges(i))(j);
     x10.io.Console.OUT.println( "i " + srcs(count) + " " + dests(count) + " "  + SDGedges(i)(j));
       count++;
     }
   }
  
   val  mapping = Rail.make[types.VERT_T](n);
   for((i) in  0..n-1) mapping(i) = i;
   
   util.srand48(GLOBALS.SCALE*32425);

   x10.io.Console.OUT.println("loop 1" + srcs + " " + dests);
   for ((i) in   0..n-1) {
     val j = util.lrand48() % n;
//     x10.io.Console.OUT.println("j " + j );
     val tmpVal = mapping(i);
     mapping(i) = mapping(j);
     mapping(j)= tmpVal;
   }
   x10.io.Console.OUT.println("loop 2");

   for ((i) in   0..m-1) {
     srcs(i) = mapping(srcs(i));
     dests(i) = mapping(dests(i));
   }

  util.srand48(GLOBALS.SCALE*5674828);
   x10.io.Console.OUT.println("loop 3" + srcs + " " + dests);

  for((i) in   0..m-1) {  
    val j = util.lrand48() % m;
    val u = srcs(i);
    val v = dests(i);
    srcs(i) = srcs(j);
    dests(i) = dests(j);
    srcs(j) = u;
    dests(j) = v;
  }

   x10.io.Console.OUT.println("loop 4" + srcs + " " + dests);
  val arv = Rail.make[types.DOUBLE_T](m);
  val w = Rail.make[types.WEIGHT_T](m);

  util.srand48(GLOBALS.SCALE*78956);
  for ((i) in 0..m-1) rv(i) = util.drand48();
   x10.io.Console.OUT.println("loop 5");
  for ((i) in   0..m-1) {
    w(i) = (1 +rv(i) * GLOBALS.MaxIntWeight) as types.WEIGHT_T;
  } 

  time = util.get_seconds() - time;
  return Pair[Double, defs.graphSDG](time, defs.graphSDG(m, n, srcs, dests, w));
} 

   public static struct UV {
    val u : types.LONG_T;
    val v : types.LONG_T;
    public def this (u_ :types.LONG_T, v_:types.LONG_T) {
      u = u_;
      v = v_;
   }
  };

   public static struct ABCD {
    val av : types.DOUBLE_T;
    val bv : types.DOUBLE_T;
    val cv : types.DOUBLE_T;
    val dv : types.DOUBLE_T;
    public def this (av_:types.DOUBLE_T, bv_:types.DOUBLE_T, cv_:types.DOUBLE_T, dv_: types.DOUBLE_T) {
      av = av_;
      bv = bv_;
      cv = cv_;
      dv = dv_;
   }
  };

  public static def choosePartition(p: Double, var u: types.LONG_T, var v:types.LONG_T, step: types.LONG_T, val av:Double, val bv: Double, val cv: Double, val dv:Double) {

    //x10.io.Console.OUT.println("p, av, bv.." + p + " " + av + " " + bv + " " + cv + " " + dv);
    //x10.io.Console.OUT.println("before u, v" + u + ", "  + v);
    if (p <av) {
        /* Do nothing */
    } else if ((p >= av) && (p < av + bv)) {
       v += step;
    } else if ((p >= av + bv) && (p < av + bv+cv)) {
       u += step;
    } else if ((p >= av+bv+cv) && (p < av+bv+cv+dv)) { 
      u += step;
      v += step;
    }
    //x10.io.Console.OUT.println("after u, v" + u + ", "  + v);
     return UV(u, v);
  }


   public static def varyParams(rv: Rail[Double]!, offset:types.INT_T, var av: Double, var bv:Double, var cv:Double, var dv:Double) {

     val v: Double;
     val S: Double;
     v = 0.2;
     av *= 0.9 + v*rv(offset+1);
     bv *= 0.9 + v*rv(offset+2);
     cv *= 0.9 + v*rv(offset+3);
     dv *= 0.9 + v*rv(offset+4);

      S = av + bv + cv + dv;

      av = av / S;
      bv = bv / S;
      cv = cv /S;
      dv = dv / S; 

     return ABCD(av, bv, cv, dv);
  }
};
