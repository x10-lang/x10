/**
 * An implementation of a queue.
 */
public final class FixedRailQueue[T]{T haszero} {
  private val internalStorage:Rail[T]{self!=null};
  private var N:Int;
  private var head:Int;
  private var tail:Int;

  /** Construct a fixed size queue */
  public def this(N:Int) { 
    this.N = N;
    this.internalStorage = new Rail[T](N);
    this.head=0n;
    this.tail=0n;
  }

  /** Check if the queue is empty */
  @x10.compiler.Inline public def isEmpty() = this.head==this.tail;
    
  /** Add the element to the front of the queue. */
  @x10.compiler.Inline public def push(v:T) {
    // Add the element and increase the size
    this.internalStorage(this.tail++) = v;
  }
  
  /** Remove and return one element of the queue if FIFO order. */
  @x10.compiler.Inline public def pop():T {
    // Remove the first element from the queue.
    return this.internalStorage(this.head++);
  }
  
  /** Remove and return one element of the queue in LIFO order. */
  @x10.compiler.Inline public def top():T {
    return this.internalStorage(--this.tail);
  }

  /** Rewind. */
  @x10.compiler.Inline public def rewind() {
    this.head=0n;
  }

  /** Output the contents of the queue in the order they are stored */
  public def print () {
    Console.OUT.print("h = "+head+", t = "+tail+", ");
    Console.OUT.print ("[");
    for (var i:Int=this.head; i<this.tail; ++i) {
      Console.OUT.print (((i==this.head)?"":",") + this.internalStorage(i));
    }
    Console.OUT.println ("]");
  }

  public static def main (args:Rail[String]) {
    val myQueue = new FixedRailQueue[Int] (4n);
    
    myQueue.push (1n);
    myQueue.push (2n);
    myQueue.push (3n);
    myQueue.push (4n);
    myQueue.print ();

    myQueue.pop ();
    myQueue.pop ();
    myQueue.print ();

    myQueue.pop ();
    myQueue.pop ();
    myQueue.print ();

    myQueue.push (5n);
    myQueue.print ();

    myQueue.push (2n);
    myQueue.push (3n);
    myQueue.push (4n);
    myQueue.print ();
  }
}
