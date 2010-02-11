class findSubGraphs_BFS implements (Graph, Rail[Rail[V]], Rail[edge], int)=>double) {
  public def apply(G: graph, extractedSubGraphs:Rail[V], maxIntWtlist: Rail[edge], maxIntWtListSize: int) {
     foreach ((tid): [0..num_threads-1]) {
       val n = G.n;
       val visites = Rail.make[boolean](n);

      for ((i):[0..maxIntWtListsize-1]) {
        val VList = extractedSubGraphs[i];
        for ((k): [0..n-1]) visited(k) = false;
        VList(0).num = maxIntWtList(i).startVertex;
        VList(0).depth=-1;
 
        VList(1).num = maxIntWtList(i).startVertex;
        VList(1).depth=-1;
             
        VList(Vlist(0).num) =  true;
        VList(Vlist(1).num) =  true;
       
        var depth = 1;
        var vectricesVisited = 2;
        var currIndex = 1;
        while((depth < SubGraphPathLength) || (verticesVisisted==n)) {
          depth = VList(currIndex).depth + 1;
          for ((j) : [G.numEdges(VList(currIndex).num)..G.numEdges(VList(currIndex).num+1)]) {
            if (visited(G.endV(j)) == false) {
              visited(G.endV(j)) = 1;
              VList(verticesVisited).num = G.endV(j);
              VList(verticesVisited).depth = depth;
              verticesVisited++; 
            }
          }

          if ((currIndex < verticesVisited - 1)&& (verticesVisited < n)) {
            currIndex++;
            depth = VList(currIndex).depth;
          } else break;
        }

      }
    }
   }
}
