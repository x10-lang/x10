package futuresched.core;

import x10.util.concurrent.AtomicReference;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;

// SFuture is a simpler version of Future.
// This implementation is more efficient based on the following assumption on the usage.
// The two methods add and set are never called concurrently.
//    i.e. When tasks are being added to the future for later notification, 
//    the value of the future is not concurrently set.

public final class SFuture[T]{T isref, T haszero} {
  
  // The holder for the value of the future.
  var data: AtomicReference[T];
  // ---------------------------------------------
  // The set of tasks.
  // Improvement spot:
  // We can try alternative implementations
  // of this set such as a small Hashset.
  static class Node[T2]{T2 isref} {
    var element: T2;
    var next: Node[T2];

    def this(element: T2, next: Node[T2]) {
      this.element = element;
      this.next = next;
    }
  }
  var head: AtomicReference[Node[FTask]];
  private def addTask(task: FTask) {
    var done: Boolean = false;
    while (!done) {
      val currentHead: Node[FTask] = head.get(); 
      val node = new Node[FTask](task, currentHead);
      done = head.compareAndSet(currentHead, node);
    }
  }
  private def notifyTasks() {
    var node: Node[FTask] = head.get();
    while (node != null) {
      val task = node.element;
      notifyTask(task);
      node = node.next;
    }      
  }
  // ---------------------------------------------

  public def this() {
    super();
    this.data = new AtomicReference[T]();
    //this.data.set(null);
    this.head = new AtomicReference[Node[FTask]]();
    //this.head.set(null);
  }

  public def add(task: FTask) {
    addTask(task);
  }
    
  public def set(v: T) {
    if (data.compareAndSet(null, v))
      notifyTasks();
    else
      throw new Exception("Future is already set.");
  }

  private def notifyTask(fTask: FTask) {
    fTask.inform();
  }

  public def asyncSet(fun: ()=>T) {
    async set(fun());
  }

  public def asyncSet(futures: ArrayList[Future[T]], fun: ()=>T) {
    FTask.asyncWait(
      futures, 
      ()=>{ set(fun()); }
    );
  }

  public def get(): T {
    val d = data.get();
    if (d != null)
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
      FTask.asyncWait(this, block);
    }

    public def register(block: (T)=>void): void {
      val newBlock = () => {
        val v = get();
        block(v);
      };
      FTask.asyncWait(this, newBlock);
    }

    //public def registerDeferred(block: T=>void): void {
    //}

}

