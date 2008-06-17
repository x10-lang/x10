package x10.lang;

public value class Exception extends Throwable {
    val s: String;
    
    public def this() = this("");
    public def this(s: String) = { this.s = s; }
    
    public def getMessage() = s;
}
