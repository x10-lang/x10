class ParRandomAccess1
extends Benchmark
{public static final x10.rtt.RuntimeType<ParRandomAccess1>_RTT = new x10.rtt.RuntimeType<ParRandomAccess1>(
/* base class */ParRandomAccess1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final static int
      PARALLELISM =
      2;
    
//#line 19
final static int
      logLocalTableSize =
      16;
    
    
//#line 21
double
                  expected(
                  ){
        
//#line 21
return 0.0;
    }
    
    
//#line 22
double
                  operations(
                  ){
        
//#line 22
return ((((double)(1.0))) * (((double)(((double)(int)(((int)(ParRandomAccess1.numUpdates))))))));
    }
    
    
//#line 28
final static int
      localTableSize =
      ((((int)(1))) << (((int)(ParRandomAccess1.logLocalTableSize))));
    
//#line 29
final static int
      tableSize =
      ((((int)(ParRandomAccess1.PARALLELISM))) * (((int)(ParRandomAccess1.localTableSize))));
    
//#line 30
final static int
      numUpdates =
      ((((int)(4))) * (((int)(ParRandomAccess1.tableSize))));
    
//#line 31
final static int
      placeMask =
      ((((int)(ParRandomAccess1.PARALLELISM))) - (((int)(1))));
    
//#line 33
final static long
      POLY =
      7L;
    
//#line 34
final static long
      PERIOD =
      1317624576693539401L;
    
//#line 36
final static class LocalTable
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ParRandomAccess1.
      LocalTable>_RTT = new x10.rtt.RuntimeType<ParRandomAccess1.
      LocalTable>(
    /* base class */ParRandomAccess1.
      LocalTable.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 12
final private ParRandomAccess1
          out$;
        
//#line 38
final x10.core.Rail<java.lang.Long>
          a;
        
//#line 39
final int
          mask;
        
        
//#line 41
LocalTable(final ParRandomAccess1 out$,
                               final int size) {
            
//#line 41
super();
            
//#line 12
this.out$ = out$;
            
//#line 42
this.mask = ((((int)(size))) - (((int)(1))));
            
//#line 43
this.a = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Long> apply(int length) {long[] array = new long[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((long)(((int)(i))));}return new x10.core.Rail<java.lang.Long>(x10.rtt.Types.LONG, size, array);}}.apply(size))));
        }
        
        
//#line 46
final void
                      update(
                      final long ran){
            
//#line 48
final int index =
              ((int)(long)(((long)(((((long)(ran))) & (((long)(((long)(((int)(mask))))))))))));
            
//#line 49
((long[])a.value)[index] = ((((long)(((long[])a.value)[index]))) ^ (((long)(ran))));
        }
    
    }
    
    
//#line 53
final x10.core.ValRail<ParRandomAccess1.
      LocalTable>
      tables;
    
    
//#line 56
final static long
                  HPCCStarts(
                  long n){
        
//#line 57
int i;
        
//#line 57
int j;
        
//#line 58
final x10.core.Rail<java.lang.Long> m2 =
          ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Long>makeVarRail(x10.rtt.Types.LONG, ((int)(64)))));
        
//#line 59
while (((((long)(n))) < (((long)(((long)(((int)(0)))))))))
            
//#line 59
n += ParRandomAccess1.PERIOD;
        
//#line 60
while (((((long)(n))) > (((long)(ParRandomAccess1.PERIOD)))))
            
//#line 60
n -= ParRandomAccess1.PERIOD;
        
//#line 61
if (((long) n) ==
                        ((long) ((long)(((int)(0)))))) {
            
//#line 61
return 1L;
        }
        
//#line 62
long temp =
          ((long)(((int)(1))));
        
//#line 63
for (
//#line 63
i = 0;
                         ((((int)(i))) < (((int)(64))));
                         
//#line 63
i += 1) {
            
//#line 64
((long[])m2.value)[i] = temp;
            
//#line 65
temp = ((((long)((((((long)(temp))) << (((int)(1)))))))) ^ (((long)((((((long)(temp))) < (((long)(((long)(((int)(0))))))))
                                                                                               ? ParRandomAccess1.POLY
                                                                                               : 0L)))));
            
//#line 66
temp = ((((long)((((((long)(temp))) << (((int)(1)))))))) ^ (((long)((((((long)(temp))) < (((long)(((long)(((int)(0))))))))
                                                                                               ? ParRandomAccess1.POLY
                                                                                               : 0L)))));
        }
        
//#line 68
for (
//#line 68
i = 62;
                         ((((int)(i))) >= (((int)(0))));
                         
//#line 68
i -= 1) {
            
//#line 68
if (((long) ((((((long)((((((long)(n))) >> (((int)(i)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                            ((long) ((long)(((int)(0)))))) {
                
//#line 68
break;
            }
        }
        
//#line 69
long ran =
          ((long)(((int)(2))));
        
//#line 70
while (((((int)(i))) > (((int)(0))))) {
            
//#line 71
temp = ((long)(((int)(0))));
            
//#line 72
for (
//#line 72
j = 0;
                             ((((int)(j))) < (((int)(64))));
                             
//#line 72
j += 1) {
                
//#line 72
if (((long) ((((((long)((((((long)(ran))) >> (((int)(j)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                                ((long) ((long)(((int)(0)))))) {
                    
//#line 72
temp ^= ((long[])m2.value)[j];
                }
            }
            
//#line 73
ran = temp;
            
//#line 74
i -= 1;
            
//#line 75
if (((long) ((((((long)((((((long)(n))) >> (((int)(i)))))))) & (((long)(((long)(((int)(1))))))))))) !=
                            ((long) ((long)(((int)(0)))))) {
                
//#line 76
ran = ((((long)((((((long)(ran))) << (((int)(1)))))))) ^ (((long)((((((long)(ran))) < (((long)(((long)(((int)(0))))))))
                                                                                                 ? ParRandomAccess1.POLY
                                                                                                 : 0)))));
            }
        }
        
//#line 78
return ran;
    }
    
    
//#line 81
final void
                  randomAccessUpdate(
                  final x10.core.ValRail<ParRandomAccess1.
                    LocalTable> tables){
        
//#line 82
try {{
            
//#line 82
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 82
for (
//#line 82
int p =
                                   0;
                                 ((((int)(p))) < (((int)(ParRandomAccess1.PARALLELISM))));
                                 
//#line 82
p += 1) {
                    
//#line 83
final int valp =
                      p;
                    
//#line 84
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 85
long ran =
                                             ParRandomAccess1.HPCCStarts((long)(((long)(((int)(((((int)(valp))) * (((int)((((((int)(ParRandomAccess1.numUpdates))) / (((int)(ParRandomAccess1.PARALLELISM)))))))))))))));
                                           
//#line 86
for (
//#line 86
long i =
                                                              ((long)(((int)(0))));
                                                            ((((long)(i))) < (((long)(((long)(((int)(((((int)(ParRandomAccess1.numUpdates))) / (((int)(ParRandomAccess1.PARALLELISM))))))))))));
                                                            
//#line 86
i += 1L) {
                                               
//#line 87
final int placeId =
                                                 ((int)(long)(((long)((((((long)((((((long)(ran))) >> (((int)(ParRandomAccess1.logLocalTableSize)))))))) & (((long)(((long)(((int)(ParRandomAccess1.placeMask)))))))))))));
                                               
//#line 88
final long valran =
                                                 ran;
                                               
//#line 89
final ParRandomAccess1.
                                                 LocalTable table =
                                                 ((ParRandomAccess1.
                                                 LocalTable)(new x10.core.fun.Fun_0_1<ParRandomAccess1.
                                                 LocalTable, ParRandomAccess1.
                                                 LocalTable>() {public final ParRandomAccess1.
                                                 LocalTable apply$G(final ParRandomAccess1.
                                                 LocalTable __desugarer__var__48__) { return apply(__desugarer__var__48__);}
                                               public final ParRandomAccess1.
                                                 LocalTable apply(final ParRandomAccess1.
                                                 LocalTable __desugarer__var__48__) { {
                                                   
//#line 89
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__48__,null))/* } */ &&
                                                                     !x10.core.Ref.at(__desugarer__var__48__, x10.
                                                                     lang.
                                                                     Runtime.here().id)) {
                                                       
//#line 89
throw new java.lang.ClassCastException("ParRandomAccess1.LocalTable{self.home==here}");
                                                   }
                                                   
//#line 89
return __desugarer__var__48__;
                                               }}
                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ParRandomAccess1.LocalTable._RTT;if (i ==1) return ParRandomAccess1.LocalTable._RTT;return null;
                                               }
                                               }.apply(((ParRandomAccess1.
                                                         LocalTable)
                                                         ((ParRandomAccess1.
                                                         LocalTable)((Object[])tables.value)[placeId])))));
                                               
//#line 90
table.update((long)(valran));
                                               
//#line 91
ran = ((((long)((((((long)(ran))) << (((int)(1)))))))) ^ (((long)((((((long)(ran))) < (((long)(0L))))
                                                                                                                                ? ParRandomAccess1.POLY
                                                                                                                                : 0L)))));
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__49__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 82
x10.
              lang.
              Runtime.pushException(__desugarer__var__49__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__49__) {
            
//#line 82
x10.
              lang.
              Runtime.pushException(__desugarer__var__49__);
        }finally {{
             
//#line 82
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 97
boolean
      first;
    
    
//#line 99
public double
                  once(
                  ){
        
//#line 102
this.randomAccessUpdate(tables);
        
//#line 106
if (first) {
            
//#line 107
this.randomAccessUpdate(tables);
            
//#line 108
int errors =
              0;
            
//#line 109
for (
//#line 109
int p =
                                0;
                              ((((int)(p))) < (((int)(ParRandomAccess1.PARALLELISM))));
                              
//#line 109
p += 1) {
                
//#line 110
final ParRandomAccess1.
                  LocalTable table =
                  ((ParRandomAccess1.
                  LocalTable)(new x10.core.fun.Fun_0_1<ParRandomAccess1.
                  LocalTable, ParRandomAccess1.
                  LocalTable>() {public final ParRandomAccess1.
                  LocalTable apply$G(final ParRandomAccess1.
                  LocalTable __desugarer__var__50__) { return apply(__desugarer__var__50__);}
                public final ParRandomAccess1.
                  LocalTable apply(final ParRandomAccess1.
                  LocalTable __desugarer__var__50__) { {
                    
//#line 110
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__50__,null))/* } */ &&
                                       !x10.core.Ref.at(__desugarer__var__50__, x10.
                                       lang.
                                       Runtime.here().id)) {
                        
//#line 110
throw new java.lang.ClassCastException("ParRandomAccess1.LocalTable{self.home==here}");
                    }
                    
//#line 110
return __desugarer__var__50__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ParRandomAccess1.LocalTable._RTT;if (i ==1) return ParRandomAccess1.LocalTable._RTT;return null;
                }
                }.apply(((ParRandomAccess1.
                          LocalTable)
                          ((ParRandomAccess1.
                          LocalTable)((Object[])tables.value)[p])))));
                
//#line 111
for (
//#line 111
int j =
                                    0;
                                  ((((int)(j))) < (((int)(table.
                                                            a.
                                                            length))));
                                  
//#line 111
j += 1) {
                    
//#line 112
if (((long) ((long[])table.
                                                        a.value)[j]) !=
                                     ((long) ((long)(((int)(j)))))) {
                        
//#line 113
errors += 1;
                    }
                }
            }
            
//#line 115
this.first = false;
            
//#line 116
x10.
              io.
              Console.OUT.printf("%d error(s); allowed %d\n",
                                 (int)(errors),
                                 (int)(((((int)(ParRandomAccess1.tableSize))) / (((int)(100))))));
            
//#line 117
return ((double)(int)(((int)((((((int)(((((int)(errors))) * (((int)(100))))))) / (((int)(ParRandomAccess1.tableSize)))))))));
        } else {
            
//#line 119
return 0.0;
        }
    }
    
    
//#line 127
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
    							ParRandomAccess1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$2303)  {
        
//#line 128
new ParRandomAccess1().execute();
    }/* } */
    
    public ParRandomAccess1() {
        super();
        
//#line 53
this.tables = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<ParRandomAccess1.
          LocalTable> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = new ParRandomAccess1.
          LocalTable(ParRandomAccess1.this,
                     ParRandomAccess1.localTableSize);}return new x10.core.ValRail<ParRandomAccess1.
          LocalTable>(ParRandomAccess1.LocalTable._RTT, ParRandomAccess1.PARALLELISM, array);}}.apply(ParRandomAccess1.PARALLELISM))));
        
//#line 97
this.first = true;
    }
    
    }
    