package x10.lib.stream;
/** The interface implemented by the sink for a stream.
 */
public interface Sink[T] {

    /**
       Return next element from the stream, waiting until it has one.
       Throw StreamClosedException if the stream is closed.
     */
    def get():T throws StreamClosedException;

    /** Is the stream closed? If so, an attempt to get() will throw
	an exception.
     */
    def isClosed():Boolean;
}