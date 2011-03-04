package STL;
import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NativeCPPInclude;

/**
 * A wrapper around the std::priority_queue. This will have to do for now.
 */
//@NativeRep("c++", "PriorityQueue<#1, #2>", "PriorityQueue<#1, #2>", null)
//@NativeCPPInclude("PriorityQueue.h")
public final struct PriorityQueue[T, Compare] {
  // Constructor #1 -> no argument default constructor
  public def this () { }

  // Constructor #2 -> copy constructor
  public def this (x: PriorityQueue[T,Compare]) {}

  // Constructor #3 -> constructor that takes in compare argument
  public def this (c: Compare) {}

  /////////////////////////////////////////////////////////////////////
  // operations
  /////////////////////////////////////////////////////////////////////

  // bool empty() const
  //@Native("c++", "(#0)->empty()")
  public def empty():Boolean = false;

  // size_type size() const
  //@Native("c++", "(#0)->size()")
  public def size():Int = 0;

  // const value_type& top() const
  //@Native("c++", "(#0)->top()")
  public def top():T { throw new NullPointerException(); }

  // void push (const value_type&)
  //@Native("c++", "(#0)->push(#1)")
  public def push(val element:T) : void {  }

  // void pop ()
  //@Native("c++", "(#0)->pop()")
  public def pop() : void { }
}
