/**
 * An implementation of the stack that allows us to set the initial size 
 * of the stack and that holds all the time. This also uses a Rail instead
 * of an ArrayList --- so it should be considerably faster.
 */
public class FixedRailStack[T] {
  private val internalStorage:Rail[T];
  private var size:Int;

  /** Construct a fixed size stack */
  public def this(N:Int) { 
    this.internalStorage = Rail.make[T] (N);
    this.size=0;
  }

  /** Return the size of the stack */
  public def size() = this.size;

  /** Check if the stack is empty */
  public def isEmpty() = this.size==0;
    
  /** Add the element to the top of the stack. */
  public def push(v:T) {
    // check that we are not going to blow the stack.
    assert (this.size < this.internalStorage.length());

    // Add the element and increase the size
    this.internalStorage(this.size) = v;
    ++this.size;
  }
  
  /** Remove and return the top element of the stack. */
  public def pop():T {
    // check that we have something in the stack
    assert (this.size > 0); 

    // Remove the last element and give it out
    return this.internalStorage(--this.size);
  }
  
  /** Return, but do not remove, the top element of the stack. */
  public def peek() {
    // check that we have something in the stack
    assert (this.size > 0); 

    // Remove the last element and give it out
    return this.internalStorage((this.size-1));
  }

  /** Clear everything, but retain the size  */
  public def clear() = this.size=0;

  /** Output the contents of the queue in the order they are stored */
  public def print () {
    Console.OUT.print ("[");
    for (var i:Int=0; i<this.size; ++i) {
      Console.OUT.print (((i==0)?"":",") + this.internalStorage(i));
    }
    Console.OUT.println ("]");
  }

  public static def main (args:Array[String](1)) {
    val myStack = new FixedRailStack[Int] (5);
    
    myStack.push (1);
    myStack.push (2);
    myStack.push (3);
    myStack.push (4);
    myStack.push (5);

    myStack.print ();

    myStack.pop ();
    myStack.pop ();

    myStack.print ();
  }

}
