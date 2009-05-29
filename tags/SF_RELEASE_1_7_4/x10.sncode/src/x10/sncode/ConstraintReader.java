package x10.sncode;

public class ConstraintReader {

    private byte[] bytes;
    private int offset;
    private int end;

    public ConstraintReader(byte[] bytes, int offset, int end) {
        this.bytes = bytes;
        this.offset = offset;
        this.end = end;
    }

}
