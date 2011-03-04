/**
    A clocked final variable with window size one. A clocked final
    variable represents a sequence of values, one for each phase of
    the underlying clock. For such a variable o, the value in the
    current phase can be read by invoking o(). The value in the next
    phase can be set by invoking o() = v. The activity can signal that
    it is ready to move by invoking next(). This is a blocking call
    --- on return, the object has moved to the next phase, and now
    calls to o() will return the value written in the last phase. If
    no value was written in the last phase, the value of the last
    phase will be returned.
    
    <p> An async may read and write multiple such variables.

    <p> Only asyncs registered on o.clock should operate on
    o. Otherwise the detgerminacy guarantees do not hold.
    
    @author vj
 */
public class Clocked[T](clock:Clock) implements ()=>T{
    var a:Array[T]{rail};
    val name:String; // for documentation

    /** Construct a Clocked[T] with initial value x,and name "".
        Construct a new clock for the returned object.
     */
    public def this(x:T) {
        this(x,Clock.make(), "");
    }

    /** Construct a Clocked[T] with initial value x, clock c, and name "".
     */
    public def this(x:T, c:Clock) {
      this(x, c, "");
    }

    /** Construct a Clocked[T] with initial value x, clock c, and name s.
     */
    public def this(x:T, c:Clock, s:String) {
        property(c);
        this.a= new Array[T] [x, x]; 
        this.name=s;
    }
    /**
       Throw an exception unless the current activity is clcoekd on
       this.clock.  Move to the next phase of the clock shifting the
       value from a(1) to a(0) before doing so.
     */
    public def next() {
        clock.next(); // This is necessary to avoid a read/write race on a(0).
        a(0)=a(1);
        clock.next(); // This is necessary to avoid a read/write race on a(1).
    }

    /**
       Return the current value of the Clocked object
    */
    public def apply() = a(0);

    /** Assign the next value.
     */
    public operator this() = (x:T) { a(1)=x; }
    public def toString() =name;
}