
class SeqRandomAccess1
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqRandomAccess1>_RTT = new x10.rtt.RuntimeType<SeqRandomAccess1>(
/* base class */SeqRandomAccess1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
final static int
      PARALLELISM =
      2;
    
//#line 21
final static int
      logLocalTableSize =
      16;
    
    
//#line 23
double
                  expected(
                  ){
        
//#line 23
return 0.0;
    }
    
    
//#line 24
double
                  operations(
                  ){
        
//#line 24
return ((((double)(1.0))) * (((double)(((double)(int)(((int)(SeqRandomAccess1.numUpdates))))))));
    }
    
    
//#line 30
final static int
      localTableSize =
      ((((int)(1))) << (((int)(SeqRandomAccess1.logLocalTableSize))));
    
//#line 31
final static int
      tableSize =
      ((((int)(SeqRandomAccess1.PARALLELISM))) * (((int)(SeqRandomAccess1.localTableSize))));
    
//#line 32
final static int
      numUpdates =
      ((((int)(4))) * (((int)(SeqRandomAccess1.tableSize))));
    
//#line 33
final static int
      placeMask =
      ((((int)(SeqRandomAccess1.PARALLELISM))) - (((int)(1))));
    
//#line 35
final static long
      POLY =
      7L;
    
//#line 36
final static long
      PERIOD =
      1317624576693539401L;
    
//#line 38
final static class LocalTable
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<SeqRandomAccess1.
      LocalTable>_RTT = new x10.rtt.RuntimeType<SeqRandomAccess1.
      LocalTable>(
    /* base class */SeqRandomAccess1.
      LocalTable.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 14
final private SeqRandomAccess1
          out$;
        
//#line 40
final x10.core.Rail<java.lang.Long>
          a;
        
//#line 41
final int
          mask;
        
        
//#line 43
LocalTable(final SeqRandomAccess1 out$,
                               final int size) {
            
//#line 43
super();
            
//#line 14
this.out$ = out$;
            
//#line 44
this.mask = ((((int)(size))) - (((int)(1))));
            
//#line 45
this.a = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Long> apply(int length) {long[] array = new long[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((long)(((int)(i))));}return new x10.core.Rail<java.lang.Long>(x10.rtt.Types.LONG, size, array);}}.apply(size))));
        }
        
        
//#line 48
final void
                      update(
                      final long ran){
            
//#line 50
final int index =
              ((int)(long)(((long)(((((long)(ran))) & (((long)(((long)(((int)(mask))))))))))));
            
//#line 51
((long[])a.value)[index] = ((((long)(((long[])a.value)[index]))) ^ (((long)(ran))));
        }
    
    }
    
    
//#line 55
final x10.core.ValRail<SeqRandomAccess1.
      LocalTable>
      tables;
    
    
//#line 58
final static long
                  HPCCStarts(
                  long n){
        
//#line 59
int i;
        
//#line 59
int j;
        
//#line 60
final x10.core.Rail<java.lang.Long> m2 =
          ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Long>makeVarRail(x10.rtt.Types.LONG, ((int)(64)))));
        
//#line 61
while (((((long)(n))) < (((long)(((long)(((int)(0)))))))))
            
//#line 61
n += SeqRandomAccess1.PERIOD;
        
//#line 62
while (((((long)(n))) > (((long)(SeqRandomAccess1.PERIOD)))))
            
//#line 62
n -= SeqRandomAccess1.PERIOD;
        
//#line 63
if (((long) n) ==
                        ((long) ((long)(((int)(0)))))) {
            
//#line 63
return 1L;
        }
        
//#line 64
long temp =
          ((long)(((int)(1))));
        
//#line 65
for (
//#line 65
i = 0;
                         ((((int)(i))) < (((int)(64))));
                         
//#line 65
i += 1) {
            
//#line 66
((long[])m2.value)[i] = temp;
            
//#line 67
temp = ((((long)((((((long)(temp))) << (((int)(1)))))))) ^ (((long)((((((long)(temp))) < (((long)(((long)(((int)(0))))))))
                                                                                               ? SeqRandomAccess1.POLY
                                                                                               : 0L)))));
            
//#line 68
temp = ((((long)((((((long)(temp))) << (((int)(1)))))))) ^ (((long)((((((long)(temp))) < (((long)(((long)(((int)(0))))))))
                                                                                               ? SeqRandomAccess1.POLY
                                                                                               : 0L)))));
        }
        
//#line 70
for (
//#line 70
i = 62;
                         ((((int)(i))) >= (((int)(0))));
                         
//#line 70
i -= 1) {
            
//#line 70
if (((long) ((((((long)((((((long)(n))) >> (((int)(i)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                            ((long) ((long)(((int)(0)))))) {
                
//#line 70
break;
            }
        }
        
//#line 71
long ran =
          ((long)(((int)(2))));
        
//#line 72
while (((((int)(i))) > (((int)(0))))) {
            
//#line 73
temp = ((long)(((int)(0))));
            
//#line 74
for (
//#line 74
j = 0;
                             ((((int)(j))) < (((int)(64))));
                             
//#line 74
j += 1) {
                
//#line 74
if (((long) ((((((long)((((((long)(ran))) >> (((int)(j)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                                ((long) ((long)(((int)(0)))))) {
                    
//#line 74
temp ^= ((long[])m2.value)[j];
                }
            }
            
//#line 75
ran = temp;
            
//#line 76
i -= 1;
            
//#line 77
if (((long) ((((((long)((((((long)(n))) >> (((int)(i)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                            ((long) ((long)(((int)(0)))))) {
                
//#line 78
ran = ((((long)((((((long)(ran))) << (((int)(1)))))))) ^ (((long)((((((long)(ran))) < (((long)(((long)(((int)(0))))))))
                                                                                                 ? SeqRandomAccess1.POLY
                                                                                                 : 0)))));
            }
        }
        
//#line 80
return ran;
    }
    
    
//#line 83
final void
                  randomAccessUpdate(
                  final x10.core.ValRail<SeqRandomAccess1.
                    LocalTable> tables){
        
//#line 84
for (
//#line 84
int p =
                           0;
                         ((((int)(p))) < (((int)(SeqRandomAccess1.PARALLELISM))));
                         
//#line 84
p += 1) {
            
//#line 85
long ran =
              SeqRandomAccess1.HPCCStarts((long)(((long)(((int)(((((int)(p))) * (((int)((((((int)(SeqRandomAccess1.numUpdates))) / (((int)(SeqRandomAccess1.PARALLELISM)))))))))))))));
            
//#line 86
for (
//#line 86
long i =
                               ((long)(((int)(0))));
                             ((((long)(i))) < (((long)(((long)(((int)(((((int)(SeqRandomAccess1.numUpdates))) / (((int)(SeqRandomAccess1.PARALLELISM))))))))))));
                             
//#line 86
i += 1L) {
                
//#line 87
final int placeId =
                  ((int)(long)(((long)((((((long)((((((long)(ran))) >> (((int)(SeqRandomAccess1.logLocalTableSize)))))))) & (((long)(((long)(((int)(SeqRandomAccess1.placeMask)))))))))))));
                
//#line 88
(new x10.core.fun.Fun_0_1<SeqRandomAccess1.
                               LocalTable, SeqRandomAccess1.
                               LocalTable>() {public final SeqRandomAccess1.
                               LocalTable apply$G(final SeqRandomAccess1.
                               LocalTable __desugarer__var__57__) { return apply(__desugarer__var__57__);}
                             public final SeqRandomAccess1.
                               LocalTable apply(final SeqRandomAccess1.
                               LocalTable __desugarer__var__57__) { {
                                 
//#line 88
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__57__,null))/* } */ &&
                                                   !x10.core.Ref.at(__desugarer__var__57__, x10.
                                                   lang.
                                                   Runtime.here().id)) {
                                     
//#line 88
throw new java.lang.ClassCastException("SeqRandomAccess1.LocalTable{self.home==here}");
                                 }
                                 
//#line 88
return __desugarer__var__57__;
                             }}
                             public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return SeqRandomAccess1.LocalTable._RTT;if (i ==1) return SeqRandomAccess1.LocalTable._RTT;return null;
                             }
                             }.apply(((SeqRandomAccess1.
                                       LocalTable)
                                       ((SeqRandomAccess1.
                                       LocalTable)((Object[])tables.value)[placeId])))).update((long)(ran));
                
//#line 89
ran = ((((long)((((((long)(ran))) << (((int)(1)))))))) ^ (((long)((((((long)(ran))) < (((long)(0L))))
                                                                                                 ? SeqRandomAccess1.POLY
                                                                                                 : 0L)))));
            }
        }
    }
    
    
//#line 94
boolean
      first;
    
    
//#line 96
public double
                  once(
                  ){
        
//#line 99
this.randomAccessUpdate(tables);
        
//#line 103
if (first) {
            
//#line 104
this.randomAccessUpdate(tables);
            
//#line 105
int errors =
              0;
            
//#line 106
for (
//#line 106
int p =
                                0;
                              ((((int)(p))) < (((int)(SeqRandomAccess1.PARALLELISM))));
                              
//#line 106
p += 1) {
                
//#line 107
final SeqRandomAccess1.
                  LocalTable table =
                  ((SeqRandomAccess1.
                  LocalTable)(new x10.core.fun.Fun_0_1<SeqRandomAccess1.
                  LocalTable, SeqRandomAccess1.
                  LocalTable>() {public final SeqRandomAccess1.
                  LocalTable apply$G(final SeqRandomAccess1.
                  LocalTable __desugarer__var__58__) { return apply(__desugarer__var__58__);}
                public final SeqRandomAccess1.
                  LocalTable apply(final SeqRandomAccess1.
                  LocalTable __desugarer__var__58__) { {
                    
//#line 107
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__58__,null))/* } */ &&
                                       !x10.core.Ref.at(__desugarer__var__58__, x10.
                                       lang.
                                       Runtime.here().id)) {
                        
//#line 107
throw new java.lang.ClassCastException("SeqRandomAccess1.LocalTable{self.home==here}");
                    }
                    
//#line 107
return __desugarer__var__58__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return SeqRandomAccess1.LocalTable._RTT;if (i ==1) return SeqRandomAccess1.LocalTable._RTT;return null;
                }
                }.apply(((SeqRandomAccess1.
                          LocalTable)
                          ((SeqRandomAccess1.
                          LocalTable)((Object[])tables.value)[p])))));
                
//#line 108
for (
//#line 108
int j =
                                    0;
                                  ((((int)(j))) < (((int)(table.
                                                            a.
                                                            length))));
                                  
//#line 108
j += 1) {
                    
//#line 109
if (((long) ((long[])table.
                                                        a.value)[j]) !=
                                     ((long) ((long)(((int)(j)))))) {
                        
//#line 110
errors += 1;
                    }
                }
            }
            
//#line 112
this.first = false;
            
//#line 113
x10.
              io.
              Console.OUT.printf("%d error(s); allowed %d\n",
                                 (int)(errors),
                                 (int)(((((int)(SeqRandomAccess1.tableSize))) / (((int)(100))))));
            
//#line 114
return ((double)(int)(((int)((((((int)(((((int)(errors))) * (((int)(100))))))) / (((int)(SeqRandomAccess1.tableSize)))))))));
        } else {
            
//#line 116
return 0.0;
        }
    }
    
    
//#line 124
/* template:Main { */
    public static class Main extends x10.runtime.impl.java.Runtime {
    	public static void main(java.lang.String[] args) {
    		// start native runtime
    		new Main().start(args);
    	}
    
    	// called by native runtime inside main x10 thread
    	public void main(final x10.core.Rail<java.lang.String> args) {
    		try {
    
    			// start xrx
    			x10.lang.Runtime.start(
    				// static init activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    					}
    				},
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							SeqRandomAccess1.main(args);
    						} catch (java.lang.RuntimeException e) {
    							throw e;
    						} catch (java.lang.Error e) {
    							throw e;
    						} catch (java.lang.Throwable t) {
    			 		   		throw new x10.lang.MultipleExceptions(t);
    			 		   	}
    					}
    				});
    
    		} catch (java.lang.Throwable t) {
    			t.printStackTrace();
    		}
    	}
    }
    
    // the original app-main method
    public static void main(final x10.core.Rail<java.lang.String> id$4831)  {
        
//#line 125
new SeqRandomAccess1().execute();
    }/* } */
    
    public SeqRandomAccess1() {
        super();
        
//#line 55
this.tables = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<SeqRandomAccess1.
          LocalTable> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = new SeqRandomAccess1.
          LocalTable(SeqRandomAccess1.this,
                     SeqRandomAccess1.localTableSize);}return new x10.core.ValRail<SeqRandomAccess1.
          LocalTable>(SeqRandomAccess1.LocalTable._RTT, SeqRandomAccess1.PARALLELISM, array);}}.apply(SeqRandomAccess1.PARALLELISM))));
        
//#line 94
this.first = true;
    }

}
