package au.edu.anu.mm;


public class TestPeriodicFmm3d
extends au.edu.anu.chem.mm.TestElectrostatic
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, TestPeriodicFmm3d.class);
    
    public static final x10.rtt.RuntimeType<TestPeriodicFmm3d> $RTT = new x10.rtt.NamedType<TestPeriodicFmm3d>(
    "au.edu.anu.mm.TestPeriodicFmm3d", /* base class */TestPeriodicFmm3d.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.chem.mm.TestElectrostatic.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(TestPeriodicFmm3d $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.chem.mm.TestElectrostatic.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        TestPeriodicFmm3d $_obj = new TestPeriodicFmm3d((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        
    }
    
    // constructor just for allocation
    public TestPeriodicFmm3d(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                getOrderedLock(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24853 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t24854 =
              x10.util.concurrent.OrderedLock.getLock((int)(t24853));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
return t24854;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                getStaticOrderedLock(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24855 =
              au.edu.anu.mm.TestPeriodicFmm3d.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t24856 =
              x10.util.concurrent.OrderedLock.getLock((int)(t24855));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
return t24856;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public double
                                                                                                                sizeOfCentralCluster$O(
                                                                                                                ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
return 80.0;
        }
        
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public static class $Main extends x10.runtime.impl.java.Runtime {
        private static final long serialVersionUID = 1L;
        public static void main(java.lang.String[] args) {
        // start native runtime
        new $Main().start(args);
        }
        
        // called by native runtime inside main x10 thread
        public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        // call the original app-main method
        TestPeriodicFmm3d.main(args);
        }
        }
        
        // the original app-main method
        public static void main(final x10.array.Array<java.lang.String> args) {
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
int numAtoms =
               0;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
double density =
              60.0;
            
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
int numTerms =
              10;
            
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
int numShells =
              10;
            
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
boolean verbose =
              false;
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24857 =
              ((x10.array.Array<java.lang.String>)args).
                size;
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24934 =
              ((t24857) > (((int)(0))));
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24934) {
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
java.lang.String ret20036 =
                   null;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret20037: {
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24868 =
                  ((x10.array.Array<java.lang.String>)args).
                    rail;
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24868) {
                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24858 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                       raw));
                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24859 =
                      ((java.lang.String)(((java.lang.String[])t24858.value)[0]));
                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret20036 = t24859;
                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret20037;
                } else {
                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t24860 =
                      ((x10.array.Region)(((x10.array.Array<java.lang.String>)args).
                                            region));
                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24861 =
                      t24860.contains$O((int)(0));
                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24862 =
                      !(t24861);
                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24862) {
                        
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0));
                    }
                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24865 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                       raw));
                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this22234 =
                      ((x10.array.RectLayout)(((x10.array.Array<java.lang.String>)args).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret22235 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2486324985 =
                      this22234.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset2223324986 =
                      ((0) - (((int)(t2486324985))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2486424987 =
                      offset2223324986;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret22235 = t2486424987;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t24866 =
                      ret22235;
                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24867 =
                      ((java.lang.String[])t24865.value)[t24866];
                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret20036 = t24867;
                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret20037;
                }}
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24869 =
                  ret20036;
                
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24870 =
                  java.lang.Integer.parseInt(t24869);
                
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
numAtoms = t24870;
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24871 =
                  ((x10.array.Array<java.lang.String>)args).
                    size;
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24932 =
                  ((t24871) > (((int)(1))));
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24932) {
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
java.lang.String ret22238 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret22239: {
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24882 =
                      ((x10.array.Array<java.lang.String>)args).
                        rail;
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24882) {
                        
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24872 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                           raw));
                        
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24873 =
                          ((java.lang.String)(((java.lang.String[])t24872.value)[1]));
                        
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22238 = t24873;
                        
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22239;
                    } else {
                        
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t24874 =
                          ((x10.array.Region)(((x10.array.Array<java.lang.String>)args).
                                                region));
                        
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24875 =
                          t24874.contains$O((int)(1));
                        
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24876 =
                          !(t24875);
                        
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24876) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(1));
                        }
                        
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24879 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                           raw));
                        
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this22242 =
                          ((x10.array.RectLayout)(((x10.array.Array<java.lang.String>)args).
                                                    layout));
                        
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret22243 =
                           0;
                        
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2487724988 =
                          this22242.
                            min0;
                        
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset2224124989 =
                          ((1) - (((int)(t2487724988))));
                        
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2487824990 =
                          offset2224124989;
                        
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret22243 = t2487824990;
                        
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t24880 =
                          ret22243;
                        
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24881 =
                          ((java.lang.String[])t24879.value)[t24880];
                        
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22238 = t24881;
                        
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22239;
                    }}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24883 =
                      ret22238;
                    
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24884 =
                      java.lang.Double.parseDouble(t24883);
                    
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
density = t24884;
                    
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24885 =
                      ((x10.array.Array<java.lang.String>)args).
                        size;
                    
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24931 =
                      ((t24885) > (((int)(2))));
                    
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24931) {
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
java.lang.String ret22261 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret22262: {
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24896 =
                          ((x10.array.Array<java.lang.String>)args).
                            rail;
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24896) {
                            
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24886 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                               raw));
                            
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24887 =
                              ((java.lang.String)(((java.lang.String[])t24886.value)[2]));
                            
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22261 = t24887;
                            
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22262;
                        } else {
                            
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t24888 =
                              ((x10.array.Region)(((x10.array.Array<java.lang.String>)args).
                                                    region));
                            
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24889 =
                              t24888.contains$O((int)(2));
                            
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24890 =
                              !(t24889);
                            
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24890) {
                                
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(2));
                            }
                            
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24893 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                               raw));
                            
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this22265 =
                              ((x10.array.RectLayout)(((x10.array.Array<java.lang.String>)args).
                                                        layout));
                            
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret22266 =
                               0;
                            
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2489124991 =
                              this22265.
                                min0;
                            
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset2226424992 =
                              ((2) - (((int)(t2489124991))));
                            
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2489224993 =
                              offset2226424992;
                            
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret22266 = t2489224993;
                            
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t24894 =
                              ret22266;
                            
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24895 =
                              ((java.lang.String[])t24893.value)[t24894];
                            
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22261 = t24895;
                            
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22262;
                        }}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24897 =
                          ret22261;
                        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24898 =
                          java.lang.Integer.parseInt(t24897);
                        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
numTerms = t24898;
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24899 =
                          ((x10.array.Array<java.lang.String>)args).
                            size;
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24930 =
                          ((t24899) > (((int)(3))));
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24930) {
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
java.lang.String ret22269 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret22270: {
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24910 =
                              ((x10.array.Array<java.lang.String>)args).
                                rail;
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24910) {
                                
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24900 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                                   raw));
                                
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24901 =
                                  ((java.lang.String)(((java.lang.String[])t24900.value)[3]));
                                
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22269 = t24901;
                                
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22270;
                            } else {
                                
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t24902 =
                                  ((x10.array.Region)(((x10.array.Array<java.lang.String>)args).
                                                        region));
                                
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24903 =
                                  t24902.contains$O((int)(3));
                                
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24904 =
                                  !(t24903);
                                
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24904) {
                                    
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(3));
                                }
                                
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24907 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                                   raw));
                                
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this22273 =
                                  ((x10.array.RectLayout)(((x10.array.Array<java.lang.String>)args).
                                                            layout));
                                
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret22274 =
                                   0;
                                
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2490524994 =
                                  this22273.
                                    min0;
                                
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset2227224995 =
                                  ((3) - (((int)(t2490524994))));
                                
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2490624996 =
                                  offset2227224995;
                                
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret22274 = t2490624996;
                                
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t24908 =
                                  ret22274;
                                
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24909 =
                                  ((java.lang.String[])t24907.value)[t24908];
                                
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22269 = t24909;
                                
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22270;
                            }}
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24911 =
                              ret22269;
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24912 =
                              java.lang.Integer.parseInt(t24911);
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
numShells = t24912;
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24913 =
                              numShells;
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24914 =
                              ((t24913) < (((int)(1))));
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24914) {
                                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
numShells = 1;
                            }
                            
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24915 =
                              ((x10.array.Array<java.lang.String>)args).
                                size;
                            
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24929 =
                              ((t24915) > (((int)(4))));
                            
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24929) {
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
java.lang.String ret22277 =
                                   null;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret22278: {
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24926 =
                                  ((x10.array.Array<java.lang.String>)args).
                                    rail;
//#line 411 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24926) {
                                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24916 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                                       raw));
                                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24917 =
                                      ((java.lang.String)(((java.lang.String[])t24916.value)[4]));
                                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22277 = t24917;
                                    
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22278;
                                } else {
                                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t24918 =
                                      ((x10.array.Region)(((x10.array.Array<java.lang.String>)args).
                                                            region));
                                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24919 =
                                      t24918.contains$O((int)(4));
                                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t24920 =
                                      !(t24919);
                                    
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t24920) {
                                        
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(4));
                                    }
                                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<java.lang.String> t24923 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<java.lang.String>)args).
                                                                       raw));
                                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this22281 =
                                      ((x10.array.RectLayout)(((x10.array.Array<java.lang.String>)args).
                                                                layout));
                                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret22282 =
                                       0;
                                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2492124997 =
                                      this22281.
                                        min0;
                                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset2228024998 =
                                      ((4) - (((int)(t2492124997))));
                                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t2492224999 =
                                      offset2228024998;
                                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret22282 = t2492224999;
                                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t24924 =
                                      ret22282;
                                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24925 =
                                      ((java.lang.String[])t24923.value)[t24924];
                                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret22277 = t24925;
                                    
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret22278;
                                }}
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final java.lang.String t24927 =
                                  ret22277;
                                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24928 =
                                  (t24927).equals("-verbose");
                                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (t24928) {
                                    
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
verbose = true;
                                }
                            }
                        }
                    }
                }
            } else {
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.io.Printer t24933 =
                  ((x10.io.Printer)(x10.io.Console.ERR));
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
t24933.println(((java.lang.String)("usage: TestPeriodicFmm3d numAtoms [density] [numTerms] [numShells] [-verbose]")));
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
return;
            }
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.mm.TestPeriodicFmm3d alloc17341 =
              ((au.edu.anu.mm.TestPeriodicFmm3d)(new au.edu.anu.mm.TestPeriodicFmm3d((java.lang.System[]) null)));
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t2493525000 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
alloc17341.$init(t2493525000);
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24936 =
              numAtoms;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24937 =
              density;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24938 =
              numTerms;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24939 =
              numShells;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final boolean t24940 =
              verbose;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
alloc17341.test((int)(t24936),
                                                                                                                                  (double)(t24937),
                                                                                                                                  (int)(t24938),
                                                                                                                                  (int)(t24939),
                                                                                                                                  (boolean)(t24940));
        }
        
        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
public void
                                                                                                                test(
                                                                                                                final int numAtoms,
                                                                                                                final double density,
                                                                                                                final int numTerms,
                                                                                                                final int numShells,
                                                                                                                final boolean verbose){
            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (verbose) {
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24941 =
                  ((double)(int)(((int)(numAtoms))));
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24942 =
                  ((t24941) / (((double)(density))));
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24943 =
                  java.lang.Math.log(((double)(t24942)));
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24944 =
                  java.lang.Math.log(((double)(8.0)));
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24945 =
                  ((t24943) / (((double)(t24944))));
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double t24946 =
                  ((t24945) + (((double)(1.0))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b23645 =
                  ((int)(double)(((double)(t24946))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t24947 =
                  ((2) < (((int)(b23645))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t24948 =
                   0;
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t24947) {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t24948 = b23645;
                } else {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t24948 = 2;
                }
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int numLevels =
                  t24948;
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.io.Printer t24957 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24949 =
                  (("Testing Periodic FMM for ") + ((x10.core.Int.$box(numAtoms))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24950 =
                  ((t24949) + (" atoms, target density = "));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24951 =
                  ((t24950) + ((x10.core.Double.$box(density))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24952 =
                  ((t24951) + (" numTerms = "));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24953 =
                  ((t24952) + ((x10.core.Int.$box(numTerms))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24954 =
                  ((t24953) + (" numShells = "));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24955 =
                  ((t24954) + ((x10.core.Int.$box(numShells))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24956 =
                  ((t24955) + (" numLevels = "));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24958 =
                  ((t24956) + ((x10.core.Int.$box(numLevels))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
t24957.println(((java.lang.String)(t24958)));
            } else {
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.io.Printer t24959 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24960 =
                  (((x10.core.Int.$box(numAtoms))) + (" atoms: "));
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
t24959.print(((java.lang.String)(t24960)));
            }
            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms =
              ((x10.array.DistArray)(this.generateAtoms((int)(numAtoms))));
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d fmm3d =
              ((au.edu.anu.mm.PeriodicFmm3d)(new au.edu.anu.mm.PeriodicFmm3d((java.lang.System[]) null)));
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10x.vector.Point3d alloc1734225002 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t2496125001 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
alloc1734225002.$init(((double)(0.0)),
                                                                                                                                        ((double)(0.0)),
                                                                                                                                        ((double)(0.0)),
                                                                                                                                        t2496125001);
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t2496325004 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
fmm3d.$init(((double)(density)),
                                                                                                                              ((int)(numTerms)),
                                                                                                                              alloc1734225002,
                                                                                                                              ((double)(80.0)),
                                                                                                                              ((int)(numAtoms)),
                                                                                                                              ((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(atoms)),
                                                                                                                              ((int)(numShells)),
                                                                                                                              t2496325004);
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
fmm3d.assignAtomsToBoxes();
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final double energy =
              fmm3d.calculateEnergy$O();
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
if (verbose) {
                
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final x10.io.Printer t24964 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final java.lang.String t24965 =
                  (("energy = ") + ((x10.core.Double.$box(energy))));
                
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
t24964.println(((java.lang.String)(t24965)));
                
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24967 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Tree construction")),
                                                                                                                                   (int)(7),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24967)));
                
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24969 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Prefetch")),
                                                                                                                                   (int)(1),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24969)));
                
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24971 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Direct")),
                                                                                                                                   (int)(2),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24971)));
                
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24973 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Multipole")),
                                                                                                                                   (int)(3),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24973)));
                
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24975 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Combine")),
                                                                                                                                   (int)(4),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24975)));
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24977 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Macroscopic")),
                                                                                                                                   (int)(8),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24977)));
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24979 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Transform")),
                                                                                                                                   (int)(5),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24979)));
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24981 =
                  ((au.edu.anu.util.Timer)(fmm3d.
                                             timer));
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Far field")),
                                                                                                                                   (int)(6),
                                                                                                                                   ((au.edu.anu.util.Timer)(t24981)));
            }
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.util.Timer t24983 =
              ((au.edu.anu.util.Timer)(fmm3d.
                                         timer));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.logTime(((java.lang.String)("Total")),
                                                                                                                               (int)(0),
                                                                                                                               ((au.edu.anu.util.Timer)(t24983)));
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final public au.edu.anu.mm.TestPeriodicFmm3d
                                                                                                                au$edu$anu$mm$TestPeriodicFmm3d$$au$edu$anu$mm$TestPeriodicFmm3d$this(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
return au.edu.anu.mm.TestPeriodicFmm3d.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
// creation method for java code
        public static au.edu.anu.mm.TestPeriodicFmm3d $make(){return new au.edu.anu.mm.TestPeriodicFmm3d((java.lang.System[]) null).$init();}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.TestPeriodicFmm3d au$edu$anu$mm$TestPeriodicFmm3d$$init$S() { {
                                                                                                        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
super.$init();
                                                                                                        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"

                                                                                                        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.mm.TestPeriodicFmm3d this2484725005 =
                                                                                                          this;
                                                                                                        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this2484725005.X10$object_lock_id0 = -1;
                                                                                                    }
                                                                                                    return this;
                                                                                                    }
        
        // constructor
        public au.edu.anu.mm.TestPeriodicFmm3d $init(){return au$edu$anu$mm$TestPeriodicFmm3d$$init$S();}
        
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
// creation method for java code
        public static au.edu.anu.mm.TestPeriodicFmm3d $make(final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.TestPeriodicFmm3d((java.lang.System[]) null).$init(paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.TestPeriodicFmm3d au$edu$anu$mm$TestPeriodicFmm3d$$init$S(final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
super.$init();
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"

                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final au.edu.anu.mm.TestPeriodicFmm3d this2485025006 =
                                                                                                                                                         this;
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this2485025006.X10$object_lock_id0 = -1;
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final int t24984 =
                                                                                                                                                         paramLock.getIndex();
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.X10$object_lock_id0 = ((int)(t24984));
                                                                                                                                                   }
                                                                                                                                                   return this;
                                                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.TestPeriodicFmm3d $init(final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$TestPeriodicFmm3d$$init$S(paramLock);}
        
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
final private void
                                                                                                                __fieldInitializers17081(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers17081$P(
          final au.edu.anu.mm.TestPeriodicFmm3d TestPeriodicFmm3d){
            TestPeriodicFmm3d.__fieldInitializers17081();
        }
    
}
