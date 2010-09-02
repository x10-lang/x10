// GlobalRef.x10

/**
    GlobalRef wraps any object in a global ref. Its main use is to
    provide a global reference to a local object.
*/
package x10.lang;
public global class GlobalRef[T]  extends GlobalObject
    implements (){here==this.home}=>T {
   transient val t:T;
   public def this(t:T) {
     this.t=t;
   }
   public def apply(){here==this.home}=t;
}
