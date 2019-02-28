package futuresched.core;

import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;
import x10.util.concurrent.AtomicInteger;


public final class IntFuture implements Notifier {
// If T is both a reference type and has a zero then this zero is the null.

  // The holder for the value of the future.
  var data: AtomicInteger;
  val NotSet = Int.MIN_VALUE;
  // ---------------------------------------------
  // The set of tasks.
  // Improvement spot:
  // We can try alternative implementations
  // of this set such as a small Hashset.  
  static class Node { //[T2]{T2 isref} {
    //var element: T2;
    var task: FTask;
    public var obj: Any;
    public var next: Node; //[T2];
    var flag: AtomicInteger;

    static val TENTATIVE = 1n;
    static val STABLE = 2n;
    static val REMOVED = 3n;

    //def this(element: T2, next: Node[T2]) {
    def this(task: FTask, obj: Any, next: Node) {
      //this.element = element;
      this.task = task;
      this.obj = obj;
      this.next = next;
      this.flag = new AtomicInteger();
      this.flag.set(TENTATIVE);
    }

    def this(task: FTask, obj: Any) {
      //this.element = element;
      this.task = task;
      this.obj = obj;
      this.flag = new AtomicInteger();
      this.flag.set(TENTATIVE);
    }

  }
  //var head: AtomicReference[Node[TentTask]];
  var head: AtomicReference[Node];
  private def addTask(task: FTask, obj: Any): Node {
    var done: Boolean = false;
    val node = new Node(task, obj);
    while (!done) {
      val currentHead = head.get();
      //val node = new Node[TentTask](task, currentHead);
      node.next = currentHead;
      done = head.compareAndSet(currentHead, node);
    }
    return node;
  }
  private def notifyTasks(v: Any) {
    var node: Node = head.get();
    while (node != null) {
      //val task = node.task;
      notifyTask(node, v, node.obj);
      node = node.next;
    }      
  }
  // ---------------------------------------------

  public def this() {
    super();
    this.data = new AtomicInteger(NotSet);
    //this.data.set(null);
    //this.head = new AtomicReference[Node[TentTask]]();
    this.head = new AtomicReference[Node]();
    //this.head.set(null);
  }

  public def this(v: Int) {
    super();
    this.data = new AtomicInteger(v);
    //this.data.set(v);
    //this.data.set(null);
    //this.head = new AtomicReference[Node[TentTask]]();
    this.head = new AtomicReference[Node]();
    //this.head.set(null);
  }

//  static class TentTask {
//    var task: FTask;
    // Improvement spot:
    // Is it safe to use a basic variable?
//    var flag: AtomicInteger;
//
//    static val TENTATIVE = 1;
//    static val STABLE = 2;
//    static val REMOVED = 3;
//
//    def this(task: FTask) {
//      this.task = task;
//      flag = new AtomicInteger();
//      flag.set(TENTATIVE);
//    }
//  }

  // Adds the task to the set of tasks if the future
  // is not set. Returns true if added.
  public def addIfNotSet(task: FTask, obj: Any): Boolean {
    if (data.get() != NotSet)
      return false;
    val node = addTask(task, obj);
    if (data.get() != NotSet) {
      // We know that data.compareAndSet() is linearized between the
      // two data.get(). Thus, the value is just set.
      // The future is ready.
      // We remove the tentative task and return not added.
      node.flag.set(Node.REMOVED);
      return false;
    } else {
      // We know that there has been no data.compareAndSet() before
      // the second data.get(). 
      // Thus, data.compareAndSet() is after second data.get(). 
      // addTask() is before data.get().
      // data.compareAndSet() is before notifyTasks().
      // Thus, addTask() if before notifyTasks().
      // Thus, this task is properly added.
      node.flag.set(Node.STABLE);
      return true;
    }
  }
    
  public def set(v: Int) {
     // Set once
//    if (data.compareAndSet(NotSet, v))
//      notifyTasks();
//    else
//      throw new Exception("Future is already set.");
     // Set multiple times
     data.set(v);
     notifyTasks(v);
  }

  public def cas(v1: Int, v2: Int): Boolean {
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

  private def notifyTask(node: Node, v: Any, obj: Any) {
    val flag = node.flag;
    var state: Int = flag.get();
    while (state == Node.TENTATIVE)
      state = flag.get(); // Spin for a short time
    if (state == Node.STABLE) {
      val fTask = node.task;
      fTask.inform(v, obj);
    }
  }

  //  @Inline def asyncSet
  public def asyncSet(fun: ()=>Int) {
    async set(fun());
  }

  public def asyncSet(futures: ArrayList[IntFuture], fun: ()=>Int) {
    FTask.asyncAnd(
      futures,
      ()=>{ set(fun()); }
    );
//    // To not make another closure.
//    FTask.asyncAndSet(
//      futures, fun, this
//    );
  }

  public def asyncSet(futures: ArrayList[Notifier], fun: ()=>Int) {
    FTask.asyncAnd(
      futures,
      ()=>{ set(fun()); }
    );
  }

  public def get(): Int {
    val d = data.get();
    if (d != NotSet)
      return d;
//    throw new Exception("Future is not ready yet.");
    finish {
      register(()=>{});
    }
    return data.get();
  }

  public def register(block: ()=>void): void {
    FTask.asyncAnd(this, block);
  }

  // To launch free async for register.
  public def register(fun: (Int)=>void) {
    val newBlock = () => {
      val v = get();
      fun(v);
    };
    FTask.asyncAnd(this, newBlock);
  }

  //public def registerDeferred(block: T=>void): void {
  //}

}

