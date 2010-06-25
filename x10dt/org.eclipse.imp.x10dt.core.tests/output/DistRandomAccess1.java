class DistRandomAccess1
extends Benchmark
{public static final x10.rtt.RuntimeType<DistRandomAccess1>_RTT = new x10.rtt.RuntimeType<DistRandomAccess1>(
/* base class */DistRandomAccess1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final static int
      PARALLELISM =
      x10.
      lang.
      Math.min((int)(2),
               (int)(x10.runtime.impl.java.Runtime.MAX_PLACES));
    
//#line 19
final static int
      logLocalTableSize =
      12;
    
    
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
return ((((double)(1.0))) * (((double)(((double)(int)(((int)(DistRandomAccess1.numUpdates))))))));
    }
    
    
//#line 28
final static int
      localTableSize =
      ((((int)(1))) << (((int)(DistRandomAccess1.logLocalTableSize))));
    
//#line 29
final static int
      tableSize =
      ((((int)(DistRandomAccess1.PARALLELISM))) * (((int)(DistRandomAccess1.localTableSize))));
    
//#line 30
final static int
      numUpdates =
      ((((int)(4))) * (((int)(DistRandomAccess1.tableSize))));
    
//#line 31
final static int
      placeMask =
      ((((int)(DistRandomAccess1.PARALLELISM))) - (((int)(1))));
    
//#line 33
final static long
      POLY =
      7L;
    
//#line 34
final static long
      PERIOD =
      1317624576693539401L;
    
//#line 38
final static class LocalTable
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<DistRandomAccess1.
      LocalTable>_RTT = new x10.rtt.RuntimeType<DistRandomAccess1.
      LocalTable>(
    /* base class */DistRandomAccess1.
      LocalTable.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 12
final private DistRandomAccess1
          out$;
        
//#line 40
final x10.core.Rail<java.lang.Long>
          a;
        
//#line 41
final int
          mask;
        
        
//#line 43
LocalTable(final DistRandomAccess1 out$,
                               final int size) {
            
//#line 43
super();
            
//#line 12
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
final x10.core.ValRail<DistRandomAccess1.
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
n += DistRandomAccess1.PERIOD;
        
//#line 62
while (((((long)(n))) > (((long)(DistRandomAccess1.PERIOD)))))
            
//#line 62
n -= DistRandomAccess1.PERIOD;
        
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
                                                                                               ? DistRandomAccess1.POLY
                                                                                               : 0L)))));
            
//#line 68
temp = ((((long)((((((long)(temp))) << (((int)(1)))))))) ^ (((long)((((((long)(temp))) < (((long)(((long)(((int)(0))))))))
                                                                                               ? DistRandomAccess1.POLY
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
                                                                                                 ? DistRandomAccess1.POLY
                                                                                                 : 0)))));
            }
        }
        
//#line 80
return ran;
    }
    
    
//#line 83
final void
                  randomAccessUpdate(
                  final x10.core.ValRail<DistRandomAccess1.
                    LocalTable> tables){
        
//#line 84
try {{
            
//#line 84
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 84
for (
//#line 84
int p =
                                   0;
                                 ((((int)(p))) < (((int)(DistRandomAccess1.PARALLELISM))));
                                 
//#line 84
p += 1) {
                    
//#line 85
final int valp =
                      p;
                    
//#line 86
x10.
                      lang.
                      Runtime.runAsync(((x10.
                                         lang.
                                         Place)((Object[])x10.
                                         lang.
                                         Place.places.value)[p]),
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 87
long ran =
                                             DistRandomAccess1.HPCCStarts((long)(((long)(((int)(((((int)(valp))) * (((int)((((((int)(DistRandomAccess1.numUpdates))) / (((int)(DistRandomAccess1.PARALLELISM)))))))))))))));
                                           
//#line 88
for (
//#line 88
long i =
                                                              ((long)(((int)(0))));
                                                            ((((long)(i))) < (((long)(((long)(((int)(((((int)(DistRandomAccess1.numUpdates))) / (((int)(DistRandomAccess1.PARALLELISM))))))))))));
                                                            
//#line 88
i += 1L) {
                                               
//#line 89
final int placeId =
                                                 ((int)(long)(((long)((((((long)((((((long)(ran))) >> (((int)(DistRandomAccess1.logLocalTableSize)))))))) & (((long)(((long)(((int)(DistRandomAccess1.placeMask)))))))))))));
                                               
//#line 90
final long valran =
                                                 ran;
                                               
//#line 91
final DistRandomAccess1.
                                                 LocalTable table =
                                                 ((DistRandomAccess1.
                                                 LocalTable)((Object[])tables.value)[placeId]);
                                               
//#line 92
x10.
                                                 lang.
                                                 Runtime.runAsync(((x10.
                                                                    lang.
                                                                    Place)((Object[])x10.
                                                                    lang.
                                                                    Place.places.value)[placeId]),
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 93
(new x10.core.fun.Fun_0_1<DistRandomAccess1.
                                                                                     LocalTable, DistRandomAccess1.
                                                                                     LocalTable>() {public final DistRandomAccess1.
                                                                                     LocalTable apply$G(final DistRandomAccess1.
                                                                                     LocalTable __desugarer__var__34__) { return apply(__desugarer__var__34__);}
                                                                                   public final DistRandomAccess1.
                                                                                     LocalTable apply(final DistRandomAccess1.
                                                                                     LocalTable __desugarer__var__34__) { {
                                                                                       
//#line 93
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__34__,null))/* } */ &&
                                                                                                         !x10.core.Ref.at(__desugarer__var__34__, x10.
                                                                                                         lang.
                                                                                                         Runtime.here().id)) {
                                                                                           
//#line 93
throw new java.lang.ClassCastException("DistRandomAccess1.LocalTable{self.home==here}");
                                                                                       }
                                                                                       
//#line 93
return __desugarer__var__34__;
                                                                                   }}
                                                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return DistRandomAccess1.LocalTable._RTT;if (i ==1) return DistRandomAccess1.LocalTable._RTT;return null;
                                                                                   }
                                                                                   }.apply(((DistRandomAccess1.
                                                                                             LocalTable)
                                                                                             table))).update((long)(valran));
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                               
//#line 94
ran = ((((long)((((((long)(ran))) << (((int)(1)))))))) ^ (((long)((((((long)(ran))) < (((long)(0L))))
                                                                                                                                ? DistRandomAccess1.POLY
                                                                                                                                : 0L)))));
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__35__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 84
x10.
              lang.
              Runtime.pushException(__desugarer__var__35__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__35__) {
            
//#line 84
x10.
              lang.
              Runtime.pushException(__desugarer__var__35__);
        }finally {{
             
//#line 84
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 100
boolean
      first;
    
    
//#line 102
public double
                   once(
                   ){
        
//#line 105
this.randomAccessUpdate(tables);
        
//#line 109
if (first) {
            
//#line 110
this.randomAccessUpdate(tables);
            
//#line 111
final x10.core.Rail<java.lang.Integer> errors =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
            
//#line 112
for (
//#line 112
int p =
                                0;
                              ((((int)(p))) < (((int)(DistRandomAccess1.PARALLELISM))));
                              
//#line 112
p += 1) {
                
//#line 113
final DistRandomAccess1.
                  LocalTable table =
                  ((DistRandomAccess1.
                  LocalTable)((Object[])tables.value)[p]);
                
//#line 114
try {{
                    
//#line 114
x10.
                      lang.
                      Runtime.startFinish();
                    {
                        
//#line 114
x10.
                          lang.
                          Runtime.runAsync(((x10.
                                             lang.
                                             Place)((Object[])x10.
                                             lang.
                                             Place.places.value)[p]),
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 115
final DistRandomAccess1.
                                                 LocalTable lt =
                                                 ((DistRandomAccess1.
                                                 LocalTable)(new x10.core.fun.Fun_0_1<DistRandomAccess1.
                                                 LocalTable, DistRandomAccess1.
                                                 LocalTable>() {public final DistRandomAccess1.
                                                 LocalTable apply$G(final DistRandomAccess1.
                                                 LocalTable __desugarer__var__36__) { return apply(__desugarer__var__36__);}
                                               public final DistRandomAccess1.
                                                 LocalTable apply(final DistRandomAccess1.
                                                 LocalTable __desugarer__var__36__) { {
                                                   
//#line 115
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__36__,null))/* } */ &&
                                                                      !x10.core.Ref.at(__desugarer__var__36__, x10.
                                                                      lang.
                                                                      Runtime.here().id)) {
                                                       
//#line 115
throw new java.lang.ClassCastException("DistRandomAccess1.LocalTable{self.home==here}");
                                                   }
                                                   
//#line 115
return __desugarer__var__36__;
                                               }}
                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return DistRandomAccess1.LocalTable._RTT;if (i ==1) return DistRandomAccess1.LocalTable._RTT;return null;
                                               }
                                               }.apply(((DistRandomAccess1.
                                                         LocalTable)
                                                         table))));
                                               
//#line 116
for (
//#line 116
int j =
                                                                   0;
                                                                 ((((int)(j))) < (((int)(lt.
                                                                                           a.
                                                                                           length))));
                                                                 
//#line 116
j += 1) {
                                                   
//#line 117
if (((long) ((long[])lt.
                                                                                       a.value)[j]) !=
                                                                    ((long) ((long)(((int)(j)))))) {
                                                       
//#line 118
try {{
                                                           
//#line 118
x10.
                                                             lang.
                                                             Runtime.startFinish();
                                                           {
                                                               
//#line 118
x10.
                                                                 lang.
                                                                 Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(errors)),
                                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                      
//#line 119
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                                                                      public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                                                                                          
//#line 119
return ((int[])x.value)[y0] = ((((int)(((int[])x.value)[y0]))) + (((int)(z))));
                                                                                      }}
                                                                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                      }
                                                                                      }.apply(errors,
                                                                                              0,
                                                                                              1);
                                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                  });
                                                           }
                                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                       if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                       java.lang.Throwable __desugarer__var__37__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                       {
                                                           
//#line 118
x10.
                                                             lang.
                                                             Runtime.pushException(__desugarer__var__37__);
                                                       }
                                                       }
                                                       throw __$generated_wrappedex$__;
                                                       }catch (java.lang.Throwable __desugarer__var__37__) {
                                                           
//#line 118
x10.
                                                             lang.
                                                             Runtime.pushException(__desugarer__var__37__);
                                                       }finally {{
                                                            
//#line 118
x10.
                                                              lang.
                                                              Runtime.stopFinish();
                                                        }}
                                                       }
                                                   }
                                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                               });
                        }
                    }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                    if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                    java.lang.Throwable __desugarer__var__38__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                    {
                        
//#line 114
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__38__);
                    }
                    }
                    throw __$generated_wrappedex$__;
                    }catch (java.lang.Throwable __desugarer__var__38__) {
                        
//#line 114
x10.
                          lang.
                          Runtime.pushException(__desugarer__var__38__);
                    }finally {{
                         
//#line 114
x10.
                           lang.
                           Runtime.stopFinish();
                     }}
                }
                
//#line 122
this.first = false;
                
//#line 123
x10.
                  io.
                  Console.OUT.printf("%d error(s); allowed %d\n",
                                     (int)(((int[])errors.value)[0]),
                                     (int)(((((int)(DistRandomAccess1.tableSize))) / (((int)(100))))));
                
//#line 124
return ((double)(int)(((int)((((((int)(((((int)(((int[])errors.value)[0]))) * (((int)(100))))))) / (((int)(DistRandomAccess1.tableSize)))))))));
            } else {
                
//#line 126
return 0.0;
            }
        }
        
        
//#line 134
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
        							DistRandomAccess1.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$2031)  {
            
//#line 135
new DistRandomAccess1().execute();
        }/* } */
        
        public DistRandomAccess1() {
            super();
            
//#line 55
this.tables = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<DistRandomAccess1.
              LocalTable> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = x10.
              lang.
              Runtime.<DistRandomAccess1.
              LocalTable>evalAt$G(DistRandomAccess1.LocalTable._RTT,
                                  ((x10.
                                    lang.
                                    Place)((Object[])x10.
                                    lang.
                                    Place.places.value)[p]),
                                  new x10.core.fun.Fun_0_0<DistRandomAccess1.
                                    LocalTable>() {public final DistRandomAccess1.
                                    LocalTable apply$G() { return apply();}
                                  public final DistRandomAccess1.
                                    LocalTable apply() { {
                                      
//#line 56
return new DistRandomAccess1.
                                        LocalTable(DistRandomAccess1.this,
                                                   DistRandomAccess1.localTableSize);
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return DistRandomAccess1.LocalTable._RTT;return null;
                                  }
                                  });}return new x10.core.ValRail<DistRandomAccess1.
              LocalTable>(DistRandomAccess1.LocalTable._RTT, DistRandomAccess1.PARALLELISM, array);}}.apply(DistRandomAccess1.PARALLELISM))));
            
//#line 100
this.first = true;
        }
    
    }
    