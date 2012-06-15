package x10.util;


@x10.core.X10Generated final public class GrowableIndexedMemoryChunk<$T> extends x10.core.Ref implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, GrowableIndexedMemoryChunk.class);
    
    public static final x10.rtt.RuntimeType<GrowableIndexedMemoryChunk> $RTT = x10.rtt.NamedType.<GrowableIndexedMemoryChunk> make(
    "x10.util.GrowableIndexedMemoryChunk", /* base class */GrowableIndexedMemoryChunk.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new GrowableIndexedMemoryChunk($T, $$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($T);
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $T = (x10.rtt.Type) ois.readObject();
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(GrowableIndexedMemoryChunk $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + GrowableIndexedMemoryChunk.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$T = $T;
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        GrowableIndexedMemoryChunk $_obj = new GrowableIndexedMemoryChunk((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
        $$serialdata = serialize(); 
        $serializer.write($$serialdata);
        $serializer.write($T);
        
    }
    
    // constructor just for allocation
    public GrowableIndexedMemoryChunk(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.GrowableIndexedMemoryChunk.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final GrowableIndexedMemoryChunk $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.core.IndexedMemoryChunk<$T> imc;
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
/**
    * Elements 0..length-1 have valid entries of type T.
    * Elements length..imc.length-1 may not be valid 
    * values of type T.  
    * It is an invariant of this class, that such elements
    * will never be accessed.
    */
        public int length;
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
// creation method for java code (1-phase java constructor)
        public GrowableIndexedMemoryChunk(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                                     $init();}
        
        // constructor for non-virtual call
        final public x10.util.GrowableIndexedMemoryChunk<$T> x10$util$GrowableIndexedMemoryChunk$$init$S() { {
                                                                                                                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.$init(((int)(0)));
                                                                                                                }
                                                                                                                return this;
                                                                                                                }
        
        // constructor
        public x10.util.GrowableIndexedMemoryChunk<$T> $init(){return x10$util$GrowableIndexedMemoryChunk$$init$S();}
        
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
// creation method for java code (1-phase java constructor)
        public GrowableIndexedMemoryChunk(final x10.rtt.Type $T,
                                          final int cap){this((java.lang.System[]) null, $T);
                                                             $init(cap);}
        
        // constructor for non-virtual call
        final public x10.util.GrowableIndexedMemoryChunk<$T> x10$util$GrowableIndexedMemoryChunk$$init$S(final int cap) { {
                                                                                                                                 
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"

                                                                                                                                 
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"

                                                                                                                                 
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.__fieldInitializers57999();
                                                                                                                                 
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58360 =
                                                                                                                                   ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(cap)), true)));
                                                                                                                                 
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.imc = ((x10.core.IndexedMemoryChunk)(t58360));
                                                                                                                                 
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = 0;
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public x10.util.GrowableIndexedMemoryChunk<$T> $init(final int cap){return x10$util$GrowableIndexedMemoryChunk$$init$S(cap);}
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
// creation method for java code (1-phase java constructor)
        public GrowableIndexedMemoryChunk(final x10.rtt.Type $T,
                                          final x10.io.SerialData sd){this((java.lang.System[]) null, $T);
                                                                          $init(sd);}
        
        // constructor for non-virtual call
        final public x10.util.GrowableIndexedMemoryChunk<$T> x10$util$GrowableIndexedMemoryChunk$$init$S(final x10.io.SerialData sd) {x10$util$GrowableIndexedMemoryChunk$init_for_reflection(sd);
                                                                                                                                          
                                                                                                                                          return this;
                                                                                                                                          }
        public void x10$util$GrowableIndexedMemoryChunk$init_for_reflection(x10.io.SerialData sd) {
             {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"

                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"

                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.__fieldInitializers57999();
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.Object t58361 =
                  ((java.lang.Object)(sd.
                                        data));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> data =
                  ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),t58361))));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58362 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(data))).length);
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58363 =
                  x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t58362)), false);
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.imc = ((x10.core.IndexedMemoryChunk)(t58363));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58364 =
                  ((x10.core.IndexedMemoryChunk)(imc));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58365 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(data))).length);
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(data,((int)(0)),t58364,((int)(0)),((int)(t58365)));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58366 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(data))).length);
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = t58366;
            }}
            
        // constructor
        public x10.util.GrowableIndexedMemoryChunk<$T> $init(final x10.io.SerialData sd){return x10$util$GrowableIndexedMemoryChunk$$init$S(sd);}
        
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.io.SerialData
                                                                                                                   serialize(
                                                                                                                   ){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58367 =
              length;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> data =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t58367)), false);
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58368 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58369 =
              length;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58368,((int)(0)),data,((int)(0)),((int)(t58369)));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.io.SerialData t58370 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(data)),
                                                                                          ((x10.io.SerialData)(null)))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58370;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                   add__0x10$util$GrowableIndexedMemoryChunk$$T(
                                                                                                                   final $T v){
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58371 =
              length;
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58372 =
              ((t58371) + (((int)(1))));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58373 =
              this.capacity$O();
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58376 =
              ((t58372) > (((int)(t58373))));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58376) {
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58374 =
                  length;
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58375 =
                  ((t58374) + (((int)(1))));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.grow((int)(t58375));
            }
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58380 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> x58356 =
              ((x10.util.GrowableIndexedMemoryChunk)(this));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58377 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)x58356).
                length;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58378 =
              ((t58377) + (((int)(1))));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58379 =
              x58356.length = t58378;
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58381 =
              ((t58379) - (((int)(1))));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58380))).$set(((int)(t58381)), v);
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                   insert__1$1x10$util$GrowableIndexedMemoryChunk$$T$2(
                                                                                                                   final int p,
                                                                                                                   final x10.core.IndexedMemoryChunk items){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int addLen =
              ((((x10.core.IndexedMemoryChunk<$T>)(items))).length);
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58382 =
              length;
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int newLen =
              ((t58382) + (((int)(addLen))));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58383 =
              length;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int movLen =
              ((t58383) - (((int)(p))));
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
boolean t58384 =
              true;
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58384) {
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
t58384 = ((movLen) < (((int)(0))));
            }
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58386 =
              t58384;
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58386) {
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58385 =
                  length;
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.util.GrowableIndexedMemoryChunk.illegalGap((int)(p),
                                                                                                                                                                        (int)(t58385));
            }
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58387 =
              this.capacity$O();
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58388 =
              ((newLen) > (((int)(t58387))));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58388) {
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.grow((int)(newLen));
            }
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58392 =
              ((movLen) > (((int)(0))));
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58392) {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58389 =
                  ((x10.core.IndexedMemoryChunk)(imc));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58390 =
                  ((x10.core.IndexedMemoryChunk)(imc));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58391 =
                  ((p) + (((int)(addLen))));
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58389,((int)(p)),t58390,((int)(t58391)),((int)(movLen)));
            }
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58393 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58394 =
              ((((x10.core.IndexedMemoryChunk<$T>)(items))).length);
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(items,((int)(0)),t58393,((int)(p)),((int)(t58394)));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = newLen;
        }
        
        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public $T
                                                                                                                    $apply$G(
                                                                                                                    final int idx){
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
boolean t58396 =
              true;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58396) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58395 =
                  length;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
t58396 = ((idx) >= (((int)(t58395))));
            }
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58398 =
              t58396;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58398) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58397 =
                  length;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.util.GrowableIndexedMemoryChunk.raiseIndexOutOfBounds((int)(idx),
                                                                                                                                                                                    (int)(t58397));
            }
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58399 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final $T t58400 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t58399))).$apply$G(((int)(idx)))));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58400;
        }
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                    $set__1x10$util$GrowableIndexedMemoryChunk$$T(
                                                                                                                    final int idx,
                                                                                                                    final $T v){
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
boolean t58402 =
              true;
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58402) {
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58401 =
                  length;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
t58402 = ((idx) > (((int)(t58401))));
            }
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58404 =
              t58402;
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58404) {
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58403 =
                  length;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.util.GrowableIndexedMemoryChunk.illegalGap((int)(idx),
                                                                                                                                                                         (int)(t58403));
            }
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58405 =
              length;
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58407 =
              ((int) idx) ==
            ((int) t58405);
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58407) {
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.add__0x10$util$GrowableIndexedMemoryChunk$$T((($T)(v)));
            } else {
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58406 =
                  ((x10.core.IndexedMemoryChunk)(imc));
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58406))).$set(((int)(idx)), v);
            }
        }
        
        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.core.IndexedMemoryChunk<$T>
                                                                                                                    imc(
                                                                                                                    ){
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58408 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58408;
        }
        
        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public int
                                                                                                                    length$O(
                                                                                                                    ){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58409 =
              length;
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58409;
        }
        
        
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public int
                                                                                                                    capacity$O(
                                                                                                                    ){
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58410 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58411 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t58410))).length);
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58411;
        }
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                    removeLast(
                                                                                                                    ){
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58413 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58412 =
              length;
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58414 =
              ((t58412) - (((int)(1))));
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58413))).clear(((int)(t58414)), ((int)(1)));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58415 =
              length;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58416 =
              ((t58415) - (((int)(1))));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = t58416;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58417 =
              length;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58418 =
              ((t58417) + (((int)(1))));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.shrink((int)(t58418));
        }
        
        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                    clear(
                                                                                                                    ){
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58420 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58419 =
              length;
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58421 =
              ((t58419) - (((int)(1))));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58420))).clear(((int)(0)), ((int)(t58421)));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = 0;
        }
        
        
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.core.IndexedMemoryChunk<$T>
                                                                                                                    moveSectionToIndexedMemoryChunk(
                                                                                                                    final int i,
                                                                                                                    final int j){
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58422 =
              ((j) - (((int)(i))));
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int len =
              ((t58422) + (((int)(1))));
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58424 =
              ((len) < (((int)(1))));
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58424) {
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58423 =
                  x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(0)), false);
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58423;
            }
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> tmp =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(len)), false);
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58425 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58425,((int)(i)),tmp,((int)(0)),((int)(len)));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58428 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58429 =
              ((j) + (((int)(1))));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58430 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58426 =
              length;
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58427 =
              ((t58426) - (((int)(j))));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58431 =
              ((t58427) - (((int)(1))));
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58428,((int)(t58429)),t58430,((int)(i)),((int)(t58431)));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58433 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58432 =
              length;
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58434 =
              ((t58432) - (((int)(len))));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58433))).clear(((int)(t58434)), ((int)(len)));
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> x58358 =
              ((x10.util.GrowableIndexedMemoryChunk)(this));
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int y58359 =
              len;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58435 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)x58358).
                length;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58436 =
              ((t58435) - (((int)(y58359))));
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x58358.length = t58436;
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58437 =
              length;
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58438 =
              ((t58437) + (((int)(1))));
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.shrink((int)(t58438));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return tmp;
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.array.Array<$T>
                                                                                                                    moveSectionToArray(
                                                                                                                    final int i,
                                                                                                                    final int j){
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58439 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.moveSectionToIndexedMemoryChunk((int)(i),
                                                                                                                                                                             (int)(j))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.array.Array<$T> t58440 =
              ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(t58439, (x10.array.Array.__0$1x10$array$Array$$T$2) null)));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58440;
        }
        
        
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.core.IndexedMemoryChunk<$T>
                                                                                                                    toIndexedMemoryChunk(
                                                                                                                    ){
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58441 =
              length;
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> ans =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t58441)), false);
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58442 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58443 =
              length;
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58442,((int)(0)),ans,((int)(0)),((int)(t58443)));
            
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return ans;
        }
        
        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public x10.array.Array<$T>
                                                                                                                    toArray(
                                                                                                                    ){
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58444 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.toIndexedMemoryChunk()));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.array.Array<$T> t58445 =
              ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(t58444, (x10.array.Array.__0$1x10$array$Array$$T$2) null)));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return t58445;
        }
        
        
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                    grow(
                                                                                                                    int newCapacity){
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
int oldCapacity =
              this.capacity$O();
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58447 =
              newCapacity;
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58446 =
              oldCapacity;
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58448 =
              ((t58446) * (((int)(2))));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58451 =
              ((t58447) < (((int)(t58448))));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58451) {
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58449 =
                  oldCapacity;
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58450 =
                  ((t58449) * (((int)(2))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
newCapacity = t58450;
            }
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58452 =
              newCapacity;
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58453 =
              ((t58452) < (((int)(8))));
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58453) {
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
newCapacity = 8;
            }
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58454 =
              newCapacity;
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> tmp =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t58454)), false);
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58455 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58456 =
              length;
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58455,((int)(0)),tmp,((int)(0)),((int)(t58456)));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58459 =
              length;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58457 =
              newCapacity;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58458 =
              length;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58460 =
              ((t58457) - (((int)(t58458))));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(tmp))).clear(((int)(t58459)), ((int)(t58460)));
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58461 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58461))).deallocate();
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.imc = ((x10.core.IndexedMemoryChunk)(tmp));
        }
        
        
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
public void
                                                                                                                    shrink(
                                                                                                                    int newCapacity){
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58463 =
              newCapacity;
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58462 =
              this.capacity$O();
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58464 =
              ((t58462) / (((int)(4))));
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
boolean t58466 =
              ((t58463) > (((int)(t58464))));
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (!(t58466)) {
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58465 =
                  newCapacity;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
t58466 = ((t58465) < (((int)(8))));
            }
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final boolean t58467 =
              t58466;
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
if (t58467) {
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return;
            }
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58468 =
              newCapacity;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58469 =
              length;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58470 =
              x10.lang.Math.max$O((int)(t58468),
                                  (int)(t58469));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
newCapacity = t58470;
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58471 =
              newCapacity;
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58472 =
              x10.lang.Math.max$O((int)(t58471),
                                  (int)(8));
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
newCapacity = t58472;
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58473 =
              newCapacity;
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> tmp =
              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t58473)), false);
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58474 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58475 =
              length;
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t58474,((int)(0)),tmp,((int)(0)),((int)(t58475)));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58478 =
              length;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58476 =
              newCapacity;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58477 =
              length;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final int t58479 =
              ((t58476) - (((int)(t58477))));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(tmp))).clear(((int)(t58478)), ((int)(t58479)));
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58480 =
              ((x10.core.IndexedMemoryChunk)(imc));
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t58480))).deallocate();
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.imc = ((x10.core.IndexedMemoryChunk)(tmp));
        }
        
        
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
private static void
                                                                                                                    raiseIndexOutOfBounds(
                                                                                                                    final int idx,
                                                                                                                    final int length){
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58481 =
              (("Index is ") + ((x10.core.Int.$box(idx))));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58482 =
              ((t58481) + ("; length is "));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58483 =
              ((t58482) + ((x10.core.Int.$box(length))));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.lang.ArrayIndexOutOfBoundsException t58484 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t58483)));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
throw t58484;
        }
        
        public static void
          raiseIndexOutOfBounds$P(
          final int idx,
          final int length){
            x10.util.GrowableIndexedMemoryChunk.raiseIndexOutOfBounds((int)(idx),
                                                                      (int)(length));
        }
        
        
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
private static void
                                                                                                                    illegalGap(
                                                                                                                    final int idx,
                                                                                                                    final int length){
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58485 =
              (("Insert at ") + ((x10.core.Int.$box(idx))));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58486 =
              ((t58485) + (" would have created gap (length = "));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58487 =
              ((t58486) + ((x10.core.Int.$box(length))));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final java.lang.String t58488 =
              ((t58487) + (")"));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.lang.UnsupportedOperationException t58489 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t58488)));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
throw t58489;
        }
        
        public static void
          illegalGap$P(
          final int idx,
          final int length){
            x10.util.GrowableIndexedMemoryChunk.illegalGap((int)(idx),
                                                           (int)(length));
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final public x10.util.GrowableIndexedMemoryChunk<$T>
                                                                                                                   x10$util$GrowableIndexedMemoryChunk$$x10$util$GrowableIndexedMemoryChunk$this(
                                                                                                                   ){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
return x10.util.GrowableIndexedMemoryChunk.this;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final public void
                                                                                                                   __fieldInitializers57999(
                                                                                                                   ){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
final x10.core.IndexedMemoryChunk<$T> t58490 =
              (x10.core.IndexedMemoryChunk<$T>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.imc = t58490;
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/GrowableIndexedMemoryChunk.x10"
this.length = 0;
        }
    
}
