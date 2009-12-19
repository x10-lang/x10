package x10.lib.stream;

import x10.lib.collections.List;
/**

  <p> A simple implementation of an unbounded single-producer,
  single-consumer stream.

  <p>The producer is expected to repeatedly call put(t) to place elements
  in the stream. (These elements will be retrieved by the consumer in FIFO
  order.) When the producer has completed its task, it may call close() to 
  close the stream. Any attempt to put an element in the stream after closing
  it results in an exception being thrown. Closing an already closed stream
  has no effect.

  <p>Uses linked lists to represent a stream.

  @author vj Nov 2009
*/
public class SimpleStreamImp[T] implements Stream[T] {

    var head: List[T];

    var tail:List[T];

    public def this() {
	val v = new List[T]();
	head = v;
	tail = v;
    }

    public def source() = this;
    public def sink() = this;

   /** Close the stream. 
    */
    public def close() {
	if (tail != null)
	  atomic { 
	    tail.tail=null;
            tail=null;
          }
    }

    /**
      Is the stream closed and empty?
    */
    public def isClosed()  = (tail==null && head.tail==null);

    /**
      Put the given element in the stream, throwing a StreamClosedException
      if the stream is closed.
    */
    public def put(t:T):Void throws StreamClosedException {
	if (tail==null)
	    throw new StreamClosedException();
	val v = new List[T]();
	tail.head = t;
	tail.tail = v;
	atomic tail = v;
    }

    /**
	Return the next element in the stream, throwing a 
        StreamClosedException if the last element in the stream
        has been read and the stream has been discovered to be closed.
    */
    public def get():T throws StreamClosedException {
	await (head != tail || tail==null);
	if (head.tail==null)
	    throw new StreamClosedException();
	val result = head.head;
	head = head.tail;
	return result;
    }
}