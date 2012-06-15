package x10.lang;


@x10.core.X10Generated public class MultipleExceptions extends x10.lang.Exception implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MultipleExceptions.class);
    
    public static final x10.rtt.RuntimeType<MultipleExceptions> $RTT = x10.rtt.NamedType.<MultipleExceptions> make(
    "x10.lang.MultipleExceptions", /* base class */MultipleExceptions.class
    , /* parents */ new x10.rtt.Type[] {x10.lang.Exception.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MultipleExceptions $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MultipleExceptions.class + " calling"); } 
        x10.lang.Exception.$_deserialize_body($_obj, $deserializer);
        x10.array.Array exceptions = (x10.array.Array) $deserializer.readRef();
        $_obj.exceptions = exceptions;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MultipleExceptions $_obj = new MultipleExceptions((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
        } else {
        $serializer.write(this.exceptions);
        }
        
    }
    
    // constructor just for allocation
    public MultipleExceptions(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public x10.array.Array<x10.core.X10Throwable> exceptions;
        
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final public x10.array.Array<x10.core.X10Throwable>
                                                                                                           exceptions(
                                                                                                           ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.array.Array<x10.core.X10Throwable> t55345 =
              ((x10.array.Array)(exceptions));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return t55345;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public MultipleExceptions(final x10.util.Stack<x10.core.X10Throwable> stack, __0$1x10$lang$Throwable$2 $dummy) {super();
                                                                                                                                                                                                                              {
                                                                                                                                                                                                                                 
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
this.exceptions = ((x10.array.Array)(((x10.array.Array<x10.core.X10Throwable>)
                                                                                                                                                                                                                                                                                                                                                                         ((x10.util.ArrayList<x10.core.X10Throwable>)stack).toArray())));
                                                                                                                                                                                                                                 
                                                                                                                                                                                                                             }}
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public MultipleExceptions() {super();
                                                                                                                                           {
                                                                                                                                              
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
this.exceptions = null;
                                                                                                                                              
                                                                                                                                          }}
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public MultipleExceptions(final x10.core.X10Throwable t) {super();
                                                                                                                                                                        {
                                                                                                                                                                           
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
this.exceptions = ((x10.array.Array)(new x10.array.Array<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init(((int)(1)),
                                                                                                                                                                                                                                                                                                                                                                                                                         ((x10.core.X10Throwable)(t)), (x10.array.Array.__1x10$array$Array$$T) null)));
                                                                                                                                                                           
                                                                                                                                                                       }}
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public void
                                                                                                           printStackTrace(
                                                                                                           ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.array.Array<x10.core.X10Throwable> t55347 =
              ((x10.array.Array)(exceptions));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterable<x10.core.X10Throwable> t55348 =
              ((x10.lang.Iterable<x10.core.X10Throwable>)
                ((x10.array.Array<x10.core.X10Throwable>)t55347).values());
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterator<x10.core.X10Throwable> t55340 =
              ((x10.lang.Iterator<x10.core.X10Throwable>)
                ((x10.lang.Iterable<x10.core.X10Throwable>)t55348).iterator());
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55349 =
                  ((x10.lang.Iterator<x10.core.X10Throwable>)t55340).hasNext$O();
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (!(t55349)) {
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
break;
                }
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.core.X10Throwable t55370 =
                  ((x10.lang.Iterator<x10.core.X10Throwable>)t55340).next$G();
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
t55370.printStackTrace();
            }
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public void
                                                                                                           printStackTrace(
                                                                                                           final x10.io.Printer p){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.array.Array<x10.core.X10Throwable> t55351 =
              ((x10.array.Array)(exceptions));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterable<x10.core.X10Throwable> t55352 =
              ((x10.lang.Iterable<x10.core.X10Throwable>)
                ((x10.array.Array<x10.core.X10Throwable>)t55351).values());
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterator<x10.core.X10Throwable> t55342 =
              ((x10.lang.Iterator<x10.core.X10Throwable>)
                ((x10.lang.Iterable<x10.core.X10Throwable>)t55352).iterator());
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55353 =
                  ((x10.lang.Iterator<x10.core.X10Throwable>)t55342).hasNext$O();
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (!(t55353)) {
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
break;
                }
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.core.X10Throwable t55371 =
                  ((x10.lang.Iterator<x10.core.X10Throwable>)t55342).next$G();
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
t55371.printStackTrace(((x10.io.Printer)(p)));
            }
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public java.lang.String
                                                                                                           toString(
                                                                                                           ){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
java.lang.String me =
              super.toString();
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.array.Array<x10.core.X10Throwable> t55354 =
              ((x10.array.Array)(exceptions));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final int t55355 =
              ((x10.array.Array<x10.core.X10Throwable>)t55354).
                size;
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55363 =
              ((t55355) > (((int)(0))));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (t55363) {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.array.Array<x10.core.X10Throwable> t55357 =
                  ((x10.array.Array)(exceptions));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterable<x10.core.X10Throwable> t55358 =
                  ((x10.lang.Iterable<x10.core.X10Throwable>)
                    ((x10.array.Array<x10.core.X10Throwable>)t55357).values());
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.Iterator<x10.core.X10Throwable> e55344 =
                  ((x10.lang.Iterator<x10.core.X10Throwable>)
                    ((x10.lang.Iterable<x10.core.X10Throwable>)t55358).iterator());
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
for (;
                                                                                                                      true;
                                                                                                                      ) {
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55362 =
                      ((x10.lang.Iterator<x10.core.X10Throwable>)e55344).hasNext$O();
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (!(t55362)) {
                        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
break;
                    }
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.core.X10Throwable e55372 =
                      ((x10.core.X10Throwable)(((x10.lang.Iterator<x10.core.X10Throwable>)e55344).next$G()));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final java.lang.String t55373 =
                      me;
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final java.lang.String t55374 =
                      (("\n\t") + (e55372));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final java.lang.String t55375 =
                      ((t55373) + (t55374));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
me = t55375;
                }
            }
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final java.lang.String t55364 =
              me;
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return t55364;
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public static x10.lang.MultipleExceptions
                                                                                                           make__0$1x10$lang$Throwable$2(
                                                                                                           final x10.util.Stack<x10.core.X10Throwable> stack){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
boolean t55365 =
              ((null) == (stack));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (!(t55365)) {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
t55365 = ((x10.util.ArrayList<x10.core.X10Throwable>)stack).isEmpty$O();
            }
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55366 =
              t55365;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (t55366) {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return null;
            }
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.MultipleExceptions t55367 =
              ((x10.lang.MultipleExceptions)(new x10.lang.MultipleExceptions(((x10.util.Stack<x10.core.X10Throwable>)(stack)), (x10.lang.MultipleExceptions.__0$1x10$lang$Throwable$2) null)));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return t55367;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
public static x10.lang.MultipleExceptions
                                                                                                           make(
                                                                                                           final x10.core.X10Throwable t){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final boolean t55368 =
              ((null) == (t));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
if (t55368) {
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return null;
            }
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final x10.lang.MultipleExceptions t55369 =
              ((x10.lang.MultipleExceptions)(new x10.lang.MultipleExceptions(((x10.core.X10Throwable)(t)))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return t55369;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
final public x10.lang.MultipleExceptions
                                                                                                           x10$lang$MultipleExceptions$$x10$lang$MultipleExceptions$this(
                                                                                                           ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/MultipleExceptions.x10"
return x10.lang.MultipleExceptions.this;
        }
        
        public java.lang.String
          x10$lang$Exception$toString$S$O(
          ){
            return super.toString();
        }
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Throwable$2 {}
        
        }
        