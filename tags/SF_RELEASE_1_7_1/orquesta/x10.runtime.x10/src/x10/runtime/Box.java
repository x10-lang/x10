package x10.runtime;

public class Box extends Ref {
    Object value;
    
    private Box(Object v) {
        assert v != null;
        this.value = v;
    }
    
    public static Ref make(boolean v) { return new Box(v); }
    public static Ref make(byte v) { return new Box(v); }
    public static Ref make(short v) { return new Box(v); }
    public static Ref make(char v) { return new Box(v); }
    public static Ref make(int v) { return new Box(v); }
    public static Ref make(long v) { return new Box(v); }
    public static Ref make(float v) { return new Box(v); }
    public static Ref make(double v) { return new Box(v); }
    
    public static Ref make(Object v) {
        if (v == null)
            return null;
        if (v instanceof Ref)
            return (Ref) v;
        return new Box(v);
    }
    
    public Object value() { return value; }

    public boolean equals(Object o) {
        if (o instanceof Box) {
            return value.equals(((Box) o).value);
        }
        return false;
    }
    
    public int hashCode() {
        return value.hashCode();
    }
    
    public String toString() {
        return value.toString();
    }
}
