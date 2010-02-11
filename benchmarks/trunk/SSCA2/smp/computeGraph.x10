class computeGraph implements ((graphSDG,graph)=>void) {

  public def apply (SDGdata:graphSDG, G:graph) {
   foreach((tid):[0..nthrewds-1]) {
     val n = N;
     val m = M;

     if (tid==0) {

        endV = Rail.make[VERT_T](m);
        degree = Rail.make[LONG_T](n);
        numEdges = Rail.make[LONG_T](n+1);
        pos = Rail.make[LONG_T](m);
        w = Rail.make[WEIGHT_T](m);
        pSums = Rail.make[LONG_T](nthredds);
     }

     /* #pragma omp barrier */

     val chunkSize = n /nthreads;
    
     for ((i):[0..m-1]) {
      async {
         u: LONG_T = SDGdata.startVertex(i);
         atomic pos(i) = degree(u)++;
      }
     }

     /* #pragma omp barrier */

     prefix_sums (degree, numEdges, pSums,n );

     chunkSize = n/nthreads;
     /* #pragma omp barrier */
   
     for ((i):[0..m]) {
       val u = SDGdata.startVertex(i);
       val j = numEdges(u) + pos(i);
       endV(j) = SDGdata.endVertex(i);
       w(j) = SDGdata.weight(i);
     }
     if (tid==0) time = omp_get_wtime() - time; 
   }

   for ((i):[0..SDGdata.m]) {
     x10.io.Console.OUT.println("[ " + SDGdata.startVertex(i) + " " + SDGdata.endVertex(i) + " " SDGdata.weight(i)); 
   }

   for((i):[0..G.n]) {
    x10.io.Console.OUT.println("[ " + G.numEdges(i) + "] " );
   }

   for ((i): [0..G.n]) {
      for ((j): [G.numEdges(i)..G.numEdges(i+1)) {
        x10.io.Console.OUT.println("[ " + i + " " + G.endV(i) +  " " + G.weight(i) + "] " );
      }
   }
}
