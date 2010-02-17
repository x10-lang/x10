package ssca2;

import x10.util.*;
class getStartLists  {

  public static def compute(G:defs.graph):  Pair[Double, Rail[defs.edge]] {
    var time: Double;

    val nthreads = util.x10_get_num_threads();
    val p_start = Rail.make[types.LONG_T](nthreads);
    val p_end = Rail.make[types.LONG_T](nthreads);
    val local_max = Rail.make[types.LONG_T](nthreads);
    val m = G.m;
    val n = G.n;
    val tmpListSize = 1000;
    val pList =  Rail.make[defs.edge](tmpListSize);

     /* replace this by max reduction */ 
     time = util.x10_get_wtime();

     finish foreach ((tid) in 0..nthreads-1) {
       var pCount: Int = 0;
       val chunkSize = n/nthreads;
       for((i) in tid*chunkSize..(tid+1)*chunkSize-1) {
         val lo = (G.numEdges as Rail[types.LONG_T]!)(i);
         val hi = (G.numEdges as Rail[types.LONG_T]!)(i+1);
         for((j) in lo..hi-1) {
         if ((G.weight as Rail[types.LONG_T]!)(j) >  local_max(tid)) {
           local_max(tid) = (G.weight as Rail[types.LONG_T]!)(j); 
           pCount = 0;
           pList(pCount) = new defs.edge(i, (G.endV as Rail[types.VERT_T]!)(j), j, local_max(tid));
           pCount++;
         } else if ((G.weight as Rail[types.LONG_T]!)(j) == local_max(tid)){
           pList(pCount) = new defs.edge(i, (G.endV as Rail[types.VERT_T]!)(j), j, local_max(tid));
           pCount++;
         }
       }
     }

       p_end(tid) = pCount;
       p_start(tid)= 0;
     }

      var tmp: Double = local_max(0);
      for((tid) in 1..nthreads-1) {
        if (local_max(tid) > tmp) tmp = local_max(tid);
      }

      val maxWeight = tmp;

      finish foreach((tid) in 0..nthreads-1) {
         if (maxWeight != local_max(tid)) p_end(tid) = 0;
      }

       //scan
      for((i) in 1..nthreads-1) {
        p_end(i) = p_end(i-1) + p_end(i);
        p_start(i) = p_end(i-1);
      }

      val maxIntWtListSize = p_end(nthreads-1);
      val maxIntWtList = Rail.make[defs.edge](maxIntWtListSize);

     x10.io.Console.OUT.println("before loop" );
     finish foreach ((tid) in 0..nthreads-1) {
       for ((j) in p_start(tid)..p_end(tid)-1) {
          val startVertex = pList(j-p_start(tid)).startVertex;
          val endVertex = pList(j-p_start(tid)).endVertex;
          val e = pList(j-p_start(tid)).e;
          val w = pList(j-p_start(tid)).w;
          maxIntWtList(j) = new defs.edge(startVertex, endVertex, e, w);
        }
      }

     return Pair[Double, Rail[defs.edge]](time, maxIntWtList);
   } 
}; 
