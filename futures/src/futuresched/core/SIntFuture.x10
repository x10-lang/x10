package futuresched.core;

import x10.util.concurrent.AtomicReference;
import x10.util.concurrent.AtomicInteger;
import x10.util.ArrayList;

// SFuture is a simpler version of Future.
// This implementation is more efficient based on the following assumption on the usage.
// The two methods add and set are never called concurrently.
//    i.e. When tasks are being added to the future for later notification, 
//    the value of the future is not concurrently set.

public final class SIntFuture implements SNotifier {
  
  // The holder for the value of the future.
  var data: AtomicInteger;
  val NotSet = Int.MIN_VALUE;

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
    this.data = new AtomicInteger(NotSet);
    this.head = new AtomicReference[Node[FTask]]();
  }

  public def add(task: FTask) {
    addTask(task);
  }
    
  public def set(v: Int) {
    if (data.compareAndSet(NotSet, v))
      notifyTasks();
    else
      throw new Exception("Future is already set.");
  }

  private def notifyTask(fTask: FTask) {
    fTask.inform(this);
  }

  public def asyncSet(fun: ()=>Int) {
    async set(fun());
  }

  public def asyncSet(futures: ArrayList[SIntFuture], fun: ()=>Int) {
    FTask.sAsyncWait(
      futures,
      ()=>{ set(fun()); }
    );
  }
  public def asyncSet(futures: ArrayList[SNotifier], fun: ()=>Int) {
    FTask.sAsyncWait(
      futures, 
      ()=>{ set(fun()); }
    );
  }

  public def get(): Int {
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
    FTask.sAsyncWait(this, block);
  }

  public def register(block: (Int)=>void): void {
    val newBlock = () => {
      val v = get();
      block(v);
    };
    FTask.sAsyncWait(this, newBlock);
  }

  //public def registerDeferred(block: T=>void): void {
  //}

}

