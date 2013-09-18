package futuresched.core;

import x10.util.concurrent.AtomicReference;
import x10.util.concurrent.AtomicDouble;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;

// SFuture is a simpler version of Future.
// This implementation is more efficient based on the following assumption on the usage.
// The two methods add and set are never called concurrently.
//    i.e. When tasks are being added to the future for later notification, 
//    the value of the future is not concurrently set.

public final class SDoubleFuture implements SNotifier {
  
  // The holder for the value of the future.
  var data: AtomicDouble;
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
    this.data = new AtomicDouble(NotSet);
    this.head = new AtomicReference[Node[FTask, Any]]();
  }

  public def this(v: Double) {
    super();
    this.data = new AtomicDouble(v);
    this.head = new AtomicReference[Node[FTask, Any]]();
  }


  public def add(task: FTask, obj: Any) {
    addTask(task, obj);
  }
    
  public def set(v: Double) {
     // Set once
//    if (data.compareAndSet(NotSet, v))
//      notifyTasks();
//    else
//      throw new Exception("Future is already set.");
     // Set multiple times
     data.set(v);
     notifyTasks(v);

  }

  public def cas(v1: Double, v2: Double): Boolean {
     // Set once
//    if (data.compareAndSet(NotSet, v))
//      notifyTasks();
//    else
//      throw new Exception("Future is already set.");

     // Set multiple times
     val done = data.compareAndSet(v1, v2);
     if (done)
       notifyTasks(v2);
     return done;
  }

  private def notifyTask(fTask: FTask, v: Any, obj: Any) {
    fTask.inform(v, obj);
  }

  public def asyncSet(fun: ()=>Double) {
    async set(fun());
  }

  public def asyncSet(futures: ArrayList[SDoubleFuture], fun: ()=>Double) {
    FTask.newAnd(
      futures,
      ()=>{ set(fun()); }
    );
  }
  public def asyncSet(futures: ArrayList[SNotifier], fun: ()=>Double) {
    FTask.newAnd(
      futures, 
      ()=>{ set(fun()); }
    );
  }

  public def get(): Double {
    val d = data.get();
    if (d != NotSet)
      return d;
    //throw new Exception("Future is not ready yet.");
//    Console.OUT.println("Blocking on get().");
    finish {
      register(()=>{});
    }
//    Console.OUT.println("get() released.");
    return data.get();
  }

  public def register(block: ()=>void): void {
    FTask.newAnd(this, block);
  }

  public def register(block: (Double)=>void): void {
    val newBlock = () => {
      val v = get();
      block(v);
    };
    FTask.newAnd(this, newBlock);
  }

  //public def registerDeferred(block: T=>void): void {
  //}

}

