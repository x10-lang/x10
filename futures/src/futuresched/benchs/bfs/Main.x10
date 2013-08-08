package futuresched.benchs.bfs;

import futuresched.core.*;

public class Main {

	public static def main(Rail[String]) {
      FTask.init();

      val graph = Graph.random(10, 3);

      SpanTree.spanningTree(graph);


   }

}

