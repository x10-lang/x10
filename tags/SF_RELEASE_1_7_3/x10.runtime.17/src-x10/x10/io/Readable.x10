package x10.io;

public interface Readable {
    def read(r: Reader) : Readable throws IOException;
}
