public class List(n : int{self >= 0}) {
       var head : Object = null;
   var tail : List(n - 1) = null;

   def this() : List(0) { property(0); }
   def this(head : Object, tail : List) : List(tail.n + 1) {
       property(tail.n + 1);
       this.head = head;
       this.tail = tail;
   }

   def append(arg : List) : List(arg.n + n) {
        return n == 0 ? arg : new List(head, tail.append(arg));
   }
}