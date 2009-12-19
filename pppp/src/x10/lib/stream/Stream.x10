package x10.lib.stream;

public interface Stream[T] extends Source[T], Sink[T] {

    def source():Source[T];
    def sink():Sink[T];

}