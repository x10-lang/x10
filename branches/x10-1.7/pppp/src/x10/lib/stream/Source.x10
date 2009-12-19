package x10.lib.stream;

/** The interface implemented by the source for a stream.
    Can be used to push elements on the stream, and close the stream.
 */
public interface Source[T] {

    /**
       Put this element on the stream. Throws an exception if the
       stream is closed.
       Does not block.
     */
    def put(t:T):Void throws StreamClosedException;

    /** Close this stream. No op if the stream has already been closed.
     */
    def close():Void;

    /** Has this stream already been closed?
     */
    def isClosed():Boolean;


}