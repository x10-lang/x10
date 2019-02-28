package futuresched.core;

import x10.util.concurrent.AtomicReference;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;

// U Futures are unprotected. The encapsulated data is not atomic.
// It is assumed that set and set or set and get are not called concurrently.
// Get simply returns the value of data.

public final class SUDoubleFuture implements SNotifier {

  // The holder for the value of the future.
  var data: Double;
  val NotSet = Double.MIN_VALUE;

  // ---------------------------------------------
  // The set of tasks.
  // Improvement spot:
  // We can try alternative implementations
  // of this set such as a small Hashset.
  static class Node[T2, T3]{T2 isref} {
    public var elem1: T2;
    public var elem2: T3;
    public var next: Node[T2, T3];

    def this(elem1: T2, elem2: T3, next: Node[T2, T3]) {
      this.elem1 = elem1;
      this.elem2 = elem2;
      this.next = next;
    }
    def this(elem1: T2, elem2: T3) {
      this.elem1 = elem1;
      this.elem2 = elem2;
    }
  }
  var head: AtomicReference[Node[FTask, Any]];
  private def addTask(task: FTask, obj: Any) {
    var done: Boolean = false;
    val node = new Node[FTask, Any](task, obj);
    while (!done) {
      val currentHead: Node[FTask, Any] = head.get();
      node.next = currentHead;
      done = head.compareAndSet(currentHead, node);
    }
  }
  private def notifyTasks(v: Any) {
    var node: Node[FTask, Any] = head.get();
    while (node != null) {
      val task = node.elem1;
      val obj = node.elem2;
      notifyTask(task, v, obj);
      node = node.next;
    }      
  }
  // ---------------------------------------------

  public def this() {
    super();
    this.data = NotSet;
    this.head = new AtomicReference[Node[FTask, Any]]();
  }

  public def this(v: Double) {
    super();
    this.data = v;
    this.head = new AtomicReference[Node[FTask, Any]]();
  }


  public def add(task: FTask, obj: Any) {
    addTask(task, obj);
  }
    
  public def set(v: Double) {
     data = v;
     notifyTasks(v);
  }

  private def notifyTask(fTask: FTask, v: Any, obj: Any) {
    fTask.inform(v, obj);
  }

  public def asyncSet(fun: ()=>Double) {
    async set(fun());
  }

//  public def asyncSet(futures: ArrayList[SUDoubleFuture], fun: ()=>Double) {
//    FTask.newAnd(
//      futures,
//      ()=>{ set(fun()); }
//    );
//  }
//  public def asyncSet(futures: ArrayList[SNotifier], fun: ()=>Double) {
//    FTask.newAnd(
//      futures,
//      ()=>{ set(fun()); }
//    );
//  }

  public def get(): Double {
    return data;
  }

//  public def register(block: ()=>void): void {
//    FTask.newAnd(this, block);
//  }
//
//  public def register(block: (Double)=>void): void {
//    val newBlock = () => {
//      val v = get();
//      block(v);
//    };
//    FTask.newAnd(this, newBlock);
//  }

}

