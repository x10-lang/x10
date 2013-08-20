package futuresched.benchs.bfs;

import futuresched.core.*;


public class Main {

	public static def main(Rail[String]) {
      FTask.init();

      val graph = Graph.random(10, 3);
      val s = graph.toString();
      Console.OUT.println("Graph: ");
      Console.OUT.print(s);

      Console.OUT.println("");

      BFS.bfs(graph, graph.nodes(0));
      val p = graph.toStringParents();
      Console.OUT.println("Parents: ");
      Console.OUT.print(p);
   }

}





