package ssca2;

import x10.util.*;

public class genScaleData  {
  public static def compute (): defs.genScaleDataRet {

    val GLOBALS = at (defs.container) (defs.container.globals);

    var time: double;

    val n = GLOBALS.N;
    val m  = GLOBALS.M;

    time = util.get_seconds();

    val memBlock = Rail.make[types.LONG_T](defs.ARRAY_INIT_SIZE*n);
    val SDGedges = Rail.make[GrowableRail[types.LONG_T]](n);

    (SDGedges(0))(0) = 0;

    val numEdgesPerPhase = (1<<18) as types.LONG_T;
    if (m < numEdgesPerPhase) 
      numEdgesPerPhase = m;  

    val numRandVals = numEdgesPerPhase*5*GLOBALS.SCALE;

    srand48(GLOBALS.SCALE*34535);
 
    val rv = Rail.make[types.DOUBLE_T](numRandVals);

   var numEdgesAdded: Int = 0;
   while (numEdgesAdded < m) {
     for ((i) in   0..numRandVals)  rv(i) = drand48();
       val numEdgesToBeAdded = m-numEdgesAdded < numEdgesPerPhase ? m - numEdgesAdded:numEdgesPerPhase;

     var count: Int = 0;

     for ((i) in  0..numEdgesToBeAdded-1) {
       val u = 1;
       val v = 1;
       val step = n/2;
       val av = defs.A;
       val bv = defs.B;
       val cv = defs.C;
       val dv = defs.D;

       for ((j) in 0..GLOBALS.SCALE-1) {
         choosePartition(rv(5*(i*GLOBALS.SCALE+j)), u, v, step, av, bc, cv, dv);
         step = step / 2;
         varyParams(rv, 5*(i*GLOBALS.SCALE+j), av, bv, cv, dv);
       }

       if (u != v) {
         val pos = util.dynArraySortedInsertPos(SDGedges(u-1), v-1);
         if (pos != -1) {
           util.dynArraySortedInsert(SDGedges(u-1), v-1, pos);
           count++;
         }
      } 
  
     }

     numEdgesAdded = numEdgesAdded + count;
     x10.io.Console.OUT.println(numEdgesAdded);
   }

   x10.io.Console.OUT.println("done");

   val srcs = Rail.make[types.VERT_T](m);
   val dests = Rail.make[types.VERT_T](m);

  var count: Int = 0;
   for ((i) in   0..n-1) {
     for ((j) in  0..SDGedges(i).length()-1) {
       srcs(count) = i;
       dests(count) = (SDGedges(i))(j);
       count++;
     }
   }
  val  mapping = Rail.make[types.VERT_T](n);
   for((i) in  0..n-1) mapping(i) = i;
   
   srand48(GLOBALS.SCALE*32425);

   for ((i) in   0..n-1) {
     val j = lrand48() % n;
     val tmpVal = mapping(i);
     mapping(i) = mapping(j);
     mapping(j)= tmpVal;
   }

   for ((i) in   0..m-1) {
     srcs(i) = mapping(srcs(i));
     dests(i) = mapping(dests(i));
   }

  srand48(GLOBALS.SCALE*5674828);

  for((i) in   0..m-1) {  
    val j = lrand48() % m;
    val u = srcs(i);
    val v = dests(i);
    srcs(i) = srcs(j);
    dests(i) = dests(j);
    srcs(j) = u;
    dests(j) = v;
  }

  val arv = Rail.make[types.DOUBLE_T](m);
  val w = Rail.make[types.WEIGHT_T](m);

  srand48(GLOBALS.SCALE*78956);
  for ((i) in 0..m) rv(i) = drand48();
  for ((i) in   0..m-1) {
    w(i) = (1 +rv(i) * GLOBALS.MaxIntWeight) as types.WEIGHT_T;
  } 

  time = util.get_seconds() - time;
  return genScaleDataRet (time, graphSDG(m, n, srcs, dests, w));
} 
};

class chooseParition implements (double, types.LONG_T, types.LONG_T, types.LONG_T, double, double, double, double)=> void {
  public static def apply (p: double, var u: Rail[types.LONG_T], var v: Rail[types.LONG_T], step: types.LONG_T, av:double, bv: double, cv: double, dv:double) {

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
  }
};

class varyParams implements ((double, types.INT_T, Rail[double], Rail[double], Rail[double], Rail[double])=> void) {

   public static def apply(rv: Rail[double], offset:types.INT_T, var av: double, var bv:double, var cv:double, var dv:double): void {

     val v: double;
     val S: double;
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
  }
};
