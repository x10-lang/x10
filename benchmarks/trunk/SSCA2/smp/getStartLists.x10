
class getStartLists implements (Graph)=>retType {
  def apply(G:Graph):retType{
    val ntreads, tid, chunkSize: LONG_T;
    val i, j, n, m, maxWeight : LONG_T;
    val local_max; Rail[LONG_T];
    val maxIntWtListSize, pCount, tmpListSize : LONG_T;
    val maxIntWtList, pList: Rail[edge];   
    val p_start, p_end: Rail[LONG_T];
    val time = 0;

     /* replace this by max reduction */ 
     finish foreach ((tid): [0..nthreads-1]) {
       val pList2: Rail[edge];
       if (tid==0) time = omp_get_wtime();
       val n = G.n;
       val m = G.m;
       if(tid==0) local_max = Rail.Make[LONG_T](nthreads);
       val chunkSize = m / nthreads;
       finish for((i):[tid*chunkSize..(tid+1)*chunkSize-1]) {
         if (G.weight(i) > local_max(tid)) {
           local_max(tid) = G.weight(i);
         }
       }

       if (tid==0) {
         val maxWeight = local_max(0);
         for ((i): [1..nthreads-1]) {
           if (local_max(i) > maxWeight) maxWeight = local_max(i);
         }
       }

       val pList =  GrowableRail.make[edge](10);
       var pCount = 0;
       for((i): [0..m-1]) {
         if (G.weight(i) == maxWeight) {
          pList(pCount).endVertex = G.endV(i);
          pList(pcount).e = i;
          pList(pCount).w = maxWeight;
          pCount++;
         }
       }

       if (tid==0) {
         p_start = Rail.make[LONG_T](nthreads);
         p_end = Rail.make[LONG_T](nthreads);
       }
       p_end(tid) = pCount;
       p_start(tid)= 0;

       /* replace by SCAN */
       if (tid==0) {
        for((i) : [1..nthreads]) {
          p_end(i) = pend(i-1) + p_end(i);
          p_start(i) = p_end(i-1);
        }

        maxIntWtListSize = p_end(nthreads-1);
        maxIntWtList = Rail.make[edge](maxIntWtListSize);
       }

       /* barrier ?? * /

        finish for ((j):[p_start(tid)..p_end(tid)]) {
          maxIntWtList(j).endVertex = pList(j-p_start(tid)).endVertex;
          maxIntWtList(j).e = pList(j-p_start(tid)).e;
          maxIntWtList(j).w = pList(j-p_start(tid)).w;
        }

        finish for ((i): [0..maxIntWtListSize]) {
          async {
            maxIntWtList(j).startVertex = BinarySearchEdgeList(G.numEdges, n, maxIntWtList(i).e);
          }
        }

       if (tid==0) return retType(maxIntWtList, maxIntWtListSize);
     }
   }
}; 
