package futuresched.benchs.bfs;

import x10.array.Array_1;

public class SeqBFS {

   var g: SeqGraph;
   var r: SeqNode;

   public static class LNode[T] {
      var element: T;
      var next: LNode[T];

      public def this(element: T, next: LNode[T]) {
         this.element = element;
         this.next = next;
      }
   }

   var head1: LNode[SeqNode] = null;
   var head2: LNode[SeqNode] = null;
   var visited: Array_1[Boolean];

   public def this(g: SeqGraph, r: SeqNode) {
      this.g = g;
      this.r = r;
   }

   public def add(n: SeqNode) {
      head2 = new LNode[SeqNode](n, head2);
   }

   public def isEmpty(): Boolean {
      return head2 == null;
   }

   public def nextPhase() {
      head1 = head2;
      head2 = null;
      var lNode: LNode[SeqNode] = head1;
      while (lNode != null) {
         val node = lNode.element;
         visit(node);
         lNode = lNode.next;
      }
   }

   public def visit(node: SeqNode) {
      visited(node.no) = true;
//      Console.OUT.println("Visiting: " + node.no);
      val iter = node.neighbors.iterator();
      while (iter.hasNext()) {
         val node2 = iter.next();
         if (!visited(node2.no)) {
            add(node2);
            if (node2.parent == null) {
               node2.parent = node;
//               Console.OUT.println("\tSetting parent: " + node2.no);
            }
         }
      }
   }

   public def bfs() {
      r.parent = r;
      val nodes = g.nodes;
      val s = nodes.size() as Int;
      visited = new Array_1[Boolean](s);
      for (var i: Int = 0; i < s; i++)
         visited(i) = false;

      add(r);
      while (!isEmpty())
         nextPhase();
   }

   public static def bfs(g: SeqGraph, r: SeqNode) {
      val b = new SeqBFS(g, r);
      b.bfs();
   }

}




