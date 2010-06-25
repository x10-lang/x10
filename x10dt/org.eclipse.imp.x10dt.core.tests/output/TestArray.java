
abstract public class TestArray
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<TestArray>_RTT = new x10.rtt.RuntimeType<TestArray>(
/* base class */TestArray.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
final x10.
      io.
      StringWriter
      os;
    
//#line 24
final x10.
      io.
      Printer
      out;
    
//#line 25
final java.lang.String
      testName;
    
    
//#line 27
TestArray() {
        
//#line 27
super();
        
//#line 25
this.testName = x10.core.Ref.typeName(this);
        
//#line 28
java.lang.System.setProperty("line.separator","\n");
        
//#line 29
final x10.
          io.
          StringWriter os_ =
          ((x10.
          io.
          StringWriter)(new x10.
          io.
          StringWriter()));
        
//#line 30
this.os = os_;
        
//#line 31
this.out = new x10.
          io.
          Printer(os_);
    }
    
    
//#line 34
abstract java.lang.String
                  expected(
                  );
    
    
//#line 36
boolean
                  status(
                  ){
        
//#line 37
final java.lang.String got =
          os.result();
        
//#line 38
if ((got).equals(this.expected())) {
            
//#line 39
return true;
        } else {
            
//#line 41
x10.
              io.
              Console.OUT.println((("=== got:\n") + (got)));
            
//#line 42
x10.
              io.
              Console.OUT.println((("=== expected:\n") + (this.expected())));
            
//#line 43
x10.
              io.
              Console.OUT.println("=== ");
            
//#line 44
return false;
        }
    }
    
    
//#line 52
abstract static class R
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<TestArray.
      R>_RTT = new x10.rtt.RuntimeType<TestArray.
      R>(
    /* base class */TestArray.
      R.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
        
//#line 54
R(final TestArray out$,
                      final x10.
                        io.
                        Printer o,
                      final java.lang.String test) {
            
//#line 54
super();
            
//#line 21
this.out$ = out$;
            
//#line 55
java.lang.String r;
            
//#line 56
try {{
                
//#line 57
r = this.run();
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            final java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 59
r = e.getMessage();
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final java.lang.Throwable e) {
                
//#line 59
r = e.getMessage();
            }
            
//#line 61
o.println(((((test) + (" "))) + (r)));
        }
        
        
//#line 64
abstract java.lang.String
                      run(
                      );
    
    }
    
    
//#line 68
static class Grid
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<TestArray.
      Grid>_RTT = new x10.rtt.RuntimeType<TestArray.
      Grid>(
    /* base class */TestArray.
      Grid.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 70
x10.core.Rail<java.lang.Object>
          os;
        
        
//#line 72
void
                      set(
                      final int i0,
                      final double vue){
            
//#line 73
((Object[])os.value)[i0] = x10.
              util.
              Box.<java.lang.Double>$implicit_convert(x10.rtt.Types.DOUBLE,
                                                      (double)(vue));
        }
        
        
//#line 76
void
                      set(
                      final int i0,
                      final int i1,
                      final double vue){
            
//#line 77
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i0]),null)/* } */) {
                
//#line 77
((Object[])os.value)[i0] = new TestArray.
                  Grid(this.
                         out$);
            }
            
//#line 78
final TestArray.
              Grid grid =
              ((TestArray.
              Grid)(new x10.core.fun.Fun_0_1<TestArray.
              Grid, TestArray.
              Grid>() {public final TestArray.
              Grid apply$G(final TestArray.
              Grid __desugarer__var__268__) { return apply(__desugarer__var__268__);}
            public final TestArray.
              Grid apply(final TestArray.
              Grid __desugarer__var__268__) { {
                
//#line 78
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__268__,null))/* } */ &&
                                  !x10.core.Ref.at(__desugarer__var__268__, x10.
                                  lang.
                                  Runtime.here().id)) {
                    
//#line 78
throw new java.lang.ClassCastException("TestArray.Grid{self.home==here}");
                }
                
//#line 78
return __desugarer__var__268__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestArray.Grid._RTT;if (i ==1) return TestArray.Grid._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final TestArray.
                      Grid cast(TestArray.
                      Grid self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = TestArray.Grid._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((TestArray.
                      Grid) ((java.lang.Object)((Object[])os.value)[i0])))/* } */)));
            
//#line 79
grid.set((int)(i1),
                                 (double)(vue));
        }
        
        
//#line 82
void
                      set(
                      final int i0,
                      final int i1,
                      final int i2,
                      final double vue){
            
//#line 83
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i0]),null)/* } */) {
                
//#line 83
((Object[])os.value)[i0] = new TestArray.
                  Grid(this.
                         out$);
            }
            
//#line 84
final TestArray.
              Grid grid =
              ((TestArray.
              Grid)(new x10.core.fun.Fun_0_1<TestArray.
              Grid, TestArray.
              Grid>() {public final TestArray.
              Grid apply$G(final TestArray.
              Grid __desugarer__var__269__) { return apply(__desugarer__var__269__);}
            public final TestArray.
              Grid apply(final TestArray.
              Grid __desugarer__var__269__) { {
                
//#line 84
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__269__,null))/* } */ &&
                                  !x10.core.Ref.at(__desugarer__var__269__, x10.
                                  lang.
                                  Runtime.here().id)) {
                    
//#line 84
throw new java.lang.ClassCastException("TestArray.Grid{self.home==here}");
                }
                
//#line 84
return __desugarer__var__269__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestArray.Grid._RTT;if (i ==1) return TestArray.Grid._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final TestArray.
                      Grid cast(TestArray.
                      Grid self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = TestArray.Grid._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((TestArray.
                      Grid) ((java.lang.Object)((Object[])os.value)[i0])))/* } */)));
            
//#line 85
grid.set((int)(i1),
                                 (int)(i2),
                                 (double)(vue));
        }
        
        
//#line 88
void
                      pr(
                      final int rank){
            
//#line 89
int min =
              os.
                length;
            
//#line 90
int max =
              0;
            
//#line 91
for (
//#line 91
int i =
                               0;
                             ((((int)(i))) < (((int)(os.
                                                       length))));
                             
//#line 91
i += 1) {
                
//#line 92
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i]),null))/* } */) {
                    
//#line 93
if (((((int)(i))) < (((int)(min))))) {
                        
//#line 93
min = i;
                    } else {
                        
//#line 94
if (((((int)(i))) > (((int)(max))))) {
                            
//#line 94
max = i;
                        }
                    }
                }
            }
            
//#line 97
for (
//#line 97
int i =
                               0;
                             ((((int)(i))) < (((int)(os.
                                                       length))));
                             
//#line 97
i += 1) {
                
//#line 98
java.lang.Object o =
                  ((java.lang.Object)((Object[])os.value)[i]);
                
//#line 99
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(o,null)/* } */) {
                    
//#line 100
if (((int) rank) ==
                                     ((int) 1)) {
                        
//#line 101
this.
                                       out$.
                                       out.print(".");
                    } else {
                        
//#line 102
if (((int) rank) ==
                                         ((int) 2)) {
                            
//#line 103
if (((((int)(min))) <= (((int)(i)))) &&
                                             ((((int)(i))) <= (((int)(max))))) {
                                
//#line 104
this.
                                               out$.
                                               out.print((((("    ") + (i))) + ("\n")));
                            }
                        }
                    }
                } else {
                    
//#line 106
if (TestArray.Grid._RTT.instanceof$(o)) {
                        
//#line 107
if (((int) rank) ==
                                         ((int) 2)) {
                            
//#line 108
this.
                                           out$.
                                           out.print((((("    ") + (i))) + ("  ")));
                        } else {
                            
//#line 109
if (((((int)(rank))) >= (((int)(3))))) {
                                
//#line 110
this.
                                               out$.
                                               out.print("    ");
                                
//#line 111
for (
//#line 111
int j =
                                                    0;
                                                  ((((int)(j))) < (((int)(rank))));
                                                  
//#line 111
j += 1) {
                                    
//#line 112
this.
                                                   out$.
                                                   out.print("-");
                                }
                                
//#line 113
this.
                                               out$.
                                               out.print(((((" ") + (i))) + ("\n")));
                            }
                        }
                        
//#line 115
(new x10.core.fun.Fun_0_1<TestArray.
                                        Grid, TestArray.
                                        Grid>() {public final TestArray.
                                        Grid apply$G(final TestArray.
                                        Grid __desugarer__var__270__) { return apply(__desugarer__var__270__);}
                                      public final TestArray.
                                        Grid apply(final TestArray.
                                        Grid __desugarer__var__270__) { {
                                          
//#line 115
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__270__,null))/* } */ &&
                                                             !x10.core.Ref.at(__desugarer__var__270__, x10.
                                                             lang.
                                                             Runtime.here().id)) {
                                              
//#line 115
throw new java.lang.ClassCastException("TestArray.Grid{self.home==here}");
                                          }
                                          
//#line 115
return __desugarer__var__270__;
                                      }}
                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestArray.Grid._RTT;if (i ==1) return TestArray.Grid._RTT;return null;
                                      }
                                      }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                                                  final TestArray.
                                                Grid cast(TestArray.
                                                Grid self) {
                                                      if (self==null) return null;
                                                      x10.rtt.Type rtt = TestArray.Grid._RTT;
                                                      if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                                                      boolean sat = true;
                                                      if (! sat) throw new java.lang.ClassCastException();
                                                      return self;
                                                  }
                                              }.cast((TestArray.
                                                Grid) o))/* } */)).pr((int)(((((int)(rank))) - (((int)(1))))));
                    } else {
                        
//#line 117
final double d =
                          ((java.lang.Double)(((/* template:cast_deptype { */(new java.lang.Object() {
                                                    final x10.
                                                  util.
                                                  Box cast(x10.
                                                  util.
                                                  Box self) {
                                                        if (self==null) return null;
                                                        x10.rtt.Type rtt = new x10.rtt.ParameterizedType(x10.util.Box._RTT, x10.rtt.Types.DOUBLE);
                                                        if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                                                        boolean sat = true;
                                                        if (! sat) throw new java.lang.ClassCastException();
                                                        return self;
                                                    }
                                                }.cast((x10.
                                                  util.
                                                  Box) o))/* } */)).
                                                value));
                        
//#line 118
this.
                                       out$.
                                       out.print((((((int)(double)(((double)(d)))))) + ("")));
                    }
                }
                
//#line 121
if (((int) rank) ==
                                 ((int) 1)) {
                    
//#line 122
this.
                                   out$.
                                   out.print(" ");
                }
            }
            
//#line 124
if (((int) rank) ==
                             ((int) 1)) {
                
//#line 125
this.
                               out$.
                               out.print("\n");
            }
        }
        
        public Grid(final TestArray out$) {
            super();
            
//#line 21
this.out$ = out$;
            
//#line 70
this.os = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Object>makeVarRail(x10.rtt.Types.runtimeType(java.lang.Object.class), ((int)(10)))));
        }
    
    }
    
    
    
//#line 129
x10.
                   array.
                   DistArray<java.lang.Double>
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r){
        
//#line 130
return this.prArray(test,
                                         r,
                                         (boolean)(false));
    }
    
    
//#line 133
x10.
                   array.
                   DistArray<java.lang.Double>
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r,
                   final boolean bump){
        
//#line 135
final x10.core.fun.Fun_0_1<x10.
          array.
          Point,java.lang.Double> init1 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
          array.
          Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Point pt) { return apply(pt);}
        public final double apply(final x10.
          array.
          Point pt) { {
            
//#line 136
int v =
              1;
            
//#line 137
for (
//#line 137
int i =
                                0;
                              ((((int)(i))) < (((int)(pt.
                                                        rank))));
                              
//#line 137
i += 1) {
                
//#line 138
v *= pt.apply((int)(i));
            }
            
//#line 139
return ((double)(int)(((int)(((((int)(v))) % (((int)(10))))))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 142
final x10.core.fun.Fun_0_1<x10.
          array.
          Point,java.lang.Double> init0 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
          array.
          Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Point id$28007) { return apply(id$28007);}
        public final double apply(final x10.
          array.
          Point id$28007) { {
            
//#line 142
return 0.0;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 144
final x10.
          array.
          DistArray<java.lang.Double> a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                           x10.
                                             array.
                                             Dist.makeConstant(r,
                                                               x10.
                                                                 lang.
                                                                 Runtime.here()),
                                           bump
                                             ? init0
                                             : init1)));
        
//#line 145
this.prArray(test,
                                  a,
                                  (boolean)(bump));
        
//#line 147
return new x10.core.fun.Fun_0_1<x10.
          array.
          DistArray<java.lang.Double>, x10.
          array.
          DistArray<java.lang.Double>>() {public final x10.
          array.
          DistArray<java.lang.Double> apply$G(final x10.
          array.
          DistArray<java.lang.Double> __desugarer__var__271__) { return apply(__desugarer__var__271__);}
        public final x10.
          array.
          DistArray<java.lang.Double> apply(final x10.
          array.
          DistArray<java.lang.Double> __desugarer__var__271__) { {
            
//#line 147
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__271__,null))/* } */ &&
                               !(((int) __desugarer__var__271__.rank()) ==
                                 ((int) r.
                                          rank))) {
                
//#line 147
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                     "r.rank}"));
            }
            
//#line 147
return __desugarer__var__271__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
        }
        }.apply(((x10.
                  array.
                  DistArray)
                  a));
    }
    
    
//#line 150
void
                   prDistributed(
                   final java.lang.String test,
                   final x10.
                     array.
                     DistArray<java.lang.Double> a){
        
//#line 151
x10.core.Rail<x10.
          lang.
          Place> ps =
          ((x10.core.Rail)(x10.core.RailFactory.<x10.
          lang.
          Place>makeRailFromValRail(x10.lang.Place._RTT, a.
                                                           dist.places())));
        
//#line 152
for (
//#line 152
int i =
                            0;
                          ((((int)(i))) < (((int)(ps.
                                                    length))));
                          
//#line 152
i += 1) {
            
//#line 153
final x10.
              lang.
              Place p =
              ((x10.
              lang.
              Place)((Object[])ps.value)[i]);
            
//#line 154
try {{
                
//#line 154
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 155
x10.
                      lang.
                      Runtime.runAsync(p,
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 156
final x10.
                                             array.
                                             DistArray<java.lang.Double> ans1 =
                                             ((x10.
                                             array.
                                             DistArray)(a.$bar(p)));
                                           
//#line 157
x10.
                                             lang.
                                             Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(TestArray.this)),
                                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                               
//#line 157
TestArray.this.prArray(((((((test) + (" at "))) + (p))) + (" (by place)")),
                                                                                                   ans1);
                                                           }}
                                                           });
                                           
//#line 158
final x10.
                                             array.
                                             Region r =
                                             ((x10.
                                             array.
                                             Region)(a.
                                                       dist.get(p)));
                                           
//#line 159
final x10.
                                             array.
                                             DistArray<java.lang.Double> ans2 =
                                             ((x10.
                                             array.
                                             DistArray)(a.$bar(r)));
                                           
//#line 160
x10.
                                             lang.
                                             Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(TestArray.this)),
                                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                               
//#line 160
TestArray.this.prArray(((((((test) + (" at "))) + (p))) + (" (by region)")),
                                                                                                   ans2);
                                                           }}
                                                           });
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__272__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 154
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__272__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__272__) {
                
//#line 154
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__272__);
            }finally {{
                 
//#line 154
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }
        }
    
    
//#line 167
void
                   prUnbounded(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r){
        
//#line 168
try {{
            
//#line 169
this.prRegion(test,
                                       r);
            
//#line 170
x10.
              array.
              Region.
              Scanner s =
              ((x10.
                array.
                Region.
                Scanner)
                r.scanners().next$G());
            
//#line 171
x10.core.Iterator<x10.
              array.
              Point> i =
              ((x10.core.Iterator)
                r.iterator());
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof x10.
          array.
          UnboundedRegionException) {
        final x10.
          array.
          UnboundedRegionException e = (x10.
          array.
          UnboundedRegionException) __$generated_wrappedex$__.getCause();
        {
            
//#line 173
this.pr(e.toString());
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final x10.
                  array.
                  UnboundedRegionException e) {
            
//#line 173
this.pr(e.toString());
        }
    }
    
    
//#line 178
void
                   prRegion(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r){
        
//#line 180
this.pr((((((("--- ") + (testName))) + (": "))) + (test)));
        
//#line 182
new TestArray.
          Anonymous$18(this,
                       r,
                       out,
                       "rank");
        
//#line 183
new TestArray.
          Anonymous$19(this,
                       r,
                       out,
                       "rect");
        
//#line 184
new TestArray.
          Anonymous$20(this,
                       r,
                       out,
                       "zeroBased");
        
//#line 185
new TestArray.
          Anonymous$21(this,
                       r,
                       out,
                       "rail");
        
//#line 187
new TestArray.
          Anonymous$22(this,
                       r,
                       out,
                       "isConvex()");
        
//#line 188
new TestArray.
          Anonymous$23(this,
                       r,
                       out,
                       "size()");
        
//#line 190
this.pr((("region: ") + (r)));
    }
    
    
//#line 194
void
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     DistArray<java.lang.Double> a){
        
//#line 195
this.prArray(test,
                                  a,
                                  (boolean)(false));
    }
    
    
//#line 198
void
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     DistArray<java.lang.Double> a,
                   final boolean bump){
        
//#line 200
final x10.
          array.
          Region r =
          a.region();
        
//#line 202
this.prRegion(test,
                                   r);
        
//#line 205
TestArray.
          Grid grid =
          ((TestArray.
          Grid)(new TestArray.
          Grid(this)));
        
//#line 206
x10.core.Iterator<x10.
          array.
          Region.
          Scanner> it =
          ((x10.core.Iterator)(r.scanners()));
        
//#line 207
while (it.hasNext()) {
            
//#line 208
x10.
              array.
              Region.
              Scanner s =
              ((x10.
              array.
              Region.
              Scanner)(new x10.core.fun.Fun_0_1<x10.
              array.
              Region.
              Scanner, x10.
              array.
              Region.
              Scanner>() {public final x10.
              array.
              Region.
              Scanner apply$G(final x10.
              array.
              Region.
              Scanner __desugarer__var__273__) { return apply(__desugarer__var__273__);}
            public final x10.
              array.
              Region.
              Scanner apply(final x10.
              array.
              Region.
              Scanner __desugarer__var__273__) { {
                
//#line 208
if (!x10.core.Ref.at(__desugarer__var__273__, x10.
                                   lang.
                                   Runtime.here().id)) {
                    
//#line 208
throw new java.lang.ClassCastException("x10.array.Region.Scanner{self.home==here}");
                }
                
//#line 208
return __desugarer__var__273__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region.Scanner._RTT;if (i ==1) return x10.array.Region.Scanner._RTT;return null;
            }
            }.apply(((x10.
                      array.
                      Region.
                      Scanner)
                      it.next$G()))));
            
//#line 209
this.pr("  poly");
            
//#line 210
if (((int) r.
                                      rank) ==
                             ((int) 0)) {
                
//#line 211
this.pr("ERROR rank==0");
            } else {
                
//#line 212
if (((int) r.
                                          rank) ==
                                 ((int) 1)) {
                    
//#line 213
final x10.
                      array.
                      DistArray<java.lang.Double> a2 =
                      ((x10.
                      array.
                      DistArray)(new x10.core.fun.Fun_0_1<x10.
                      array.
                      DistArray<java.lang.Double>, x10.
                      array.
                      DistArray<java.lang.Double>>() {public final x10.
                      array.
                      DistArray<java.lang.Double> apply$G(final x10.
                      array.
                      DistArray<java.lang.Double> __desugarer__var__274__) { return apply(__desugarer__var__274__);}
                    public final x10.
                      array.
                      DistArray<java.lang.Double> apply(final x10.
                      array.
                      DistArray<java.lang.Double> __desugarer__var__274__) { {
                        
//#line 213
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__274__,null))/* } */ &&
                                           !(((int) __desugarer__var__274__.rank()) ==
                                             ((int) 1))) {
                            
//#line 213
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                                 "1}"));
                        }
                        
//#line 213
return __desugarer__var__274__;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                    }
                    }.apply(((x10.
                              array.
                              DistArray)
                              a))));
                    
//#line 214
int min0 =
                      s.min((int)(0));
                    
//#line 215
int max0 =
                      s.max((int)(0));
                    
//#line 216
for (
//#line 216
int i0 =
                                        min0;
                                      ((((int)(i0))) <= (((int)(max0))));
                                      
//#line 216
i0 += 1) {
                        
//#line 217
if (bump) {
                            
//#line 217
a2.set$G((double)(((((double)(a2.apply$G((int)(i0))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                  (int)(i0));
                        }
                        
//#line 218
grid.set((int)(i0),
                                              (double)(java.lang.Double)(a2.apply$G((int)(i0))));
                    }
                } else {
                    
//#line 220
if (((int) r.
                                              rank) ==
                                     ((int) 2)) {
                        
//#line 221
final x10.
                          array.
                          DistArray<java.lang.Double> a2 =
                          ((x10.
                          array.
                          DistArray)(new x10.core.fun.Fun_0_1<x10.
                          array.
                          DistArray<java.lang.Double>, x10.
                          array.
                          DistArray<java.lang.Double>>() {public final x10.
                          array.
                          DistArray<java.lang.Double> apply$G(final x10.
                          array.
                          DistArray<java.lang.Double> __desugarer__var__275__) { return apply(__desugarer__var__275__);}
                        public final x10.
                          array.
                          DistArray<java.lang.Double> apply(final x10.
                          array.
                          DistArray<java.lang.Double> __desugarer__var__275__) { {
                            
//#line 221
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__275__,null))/* } */ &&
                                               !(((int) __desugarer__var__275__.rank()) ==
                                                 ((int) 2))) {
                                
//#line 221
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                                     "2}"));
                            }
                            
//#line 221
return __desugarer__var__275__;
                        }}
                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                        }
                        }.apply(((x10.
                                  array.
                                  DistArray)
                                  a))));
                        
//#line 222
int min0 =
                          s.min((int)(0));
                        
//#line 223
int max0 =
                          s.max((int)(0));
                        
//#line 224
for (
//#line 224
int i0 =
                                            min0;
                                          ((((int)(i0))) <= (((int)(max0))));
                                          
//#line 224
i0 += 1) {
                            
//#line 225
s.set((int)(0),
                                               (int)(i0));
                            
//#line 226
int min1 =
                              s.min((int)(1));
                            
//#line 227
int max1 =
                              s.max((int)(1));
                            
//#line 228
for (
//#line 228
int i1 =
                                                min1;
                                              ((((int)(i1))) <= (((int)(max1))));
                                              
//#line 228
i1 += 1) {
                                
//#line 229
if (bump) {
                                    
//#line 229
a2.set$G((double)(((((double)(a2.apply$G((int)(i0),
                                                                                          (int)(i1))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                          (int)(i0),
                                                          (int)(i1));
                                }
                                
//#line 230
grid.set((int)(i0),
                                                      (int)(i1),
                                                      (double)(java.lang.Double)(a2.apply$G((int)(i0),
                                                                                            (int)(i1))));
                            }
                        }
                    } else {
                        
//#line 233
if (((int) r.
                                                  rank) ==
                                         ((int) 3)) {
                            
//#line 234
final x10.
                              array.
                              DistArray<java.lang.Double> a2 =
                              ((x10.
                              array.
                              DistArray)(new x10.core.fun.Fun_0_1<x10.
                              array.
                              DistArray<java.lang.Double>, x10.
                              array.
                              DistArray<java.lang.Double>>() {public final x10.
                              array.
                              DistArray<java.lang.Double> apply$G(final x10.
                              array.
                              DistArray<java.lang.Double> __desugarer__var__276__) { return apply(__desugarer__var__276__);}
                            public final x10.
                              array.
                              DistArray<java.lang.Double> apply(final x10.
                              array.
                              DistArray<java.lang.Double> __desugarer__var__276__) { {
                                
//#line 234
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__276__,null))/* } */ &&
                                                   !(((int) __desugarer__var__276__.rank()) ==
                                                     ((int) 3))) {
                                    
//#line 234
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                                         "3}"));
                                }
                                
//#line 234
return __desugarer__var__276__;
                            }}
                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                            }
                            }.apply(((x10.
                                      array.
                                      DistArray)
                                      a))));
                            
//#line 235
int min0 =
                              s.min((int)(0));
                            
//#line 236
int max0 =
                              s.max((int)(0));
                            
//#line 237
for (
//#line 237
int i0 =
                                                min0;
                                              ((((int)(i0))) <= (((int)(max0))));
                                              
//#line 237
i0 += 1) {
                                
//#line 238
s.set((int)(0),
                                                   (int)(i0));
                                
//#line 239
int min1 =
                                  s.min((int)(1));
                                
//#line 240
int max1 =
                                  s.max((int)(1));
                                
//#line 241
for (
//#line 241
int i1 =
                                                    min1;
                                                  ((((int)(i1))) <= (((int)(max1))));
                                                  
//#line 241
i1 += 1) {
                                    
//#line 242
s.set((int)(1),
                                                       (int)(i1));
                                    
//#line 243
int min2 =
                                      s.min((int)(2));
                                    
//#line 244
int max2 =
                                      s.max((int)(2));
                                    
//#line 245
for (
//#line 245
int i2 =
                                                        min2;
                                                      ((((int)(i2))) <= (((int)(max2))));
                                                      
//#line 245
i2 += 1) {
                                        
//#line 246
if (bump) {
                                            
//#line 246
a2.set$G((double)(((((double)(a2.apply$G((int)(i0),
                                                                                                  (int)(i1),
                                                                                                  (int)(i2))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                                  (int)(i0),
                                                                  (int)(i1),
                                                                  (int)(i2));
                                        }
                                        
//#line 247
grid.set((int)(i0),
                                                              (int)(i1),
                                                              (int)(i2),
                                                              (double)(java.lang.Double)(a2.apply$G((int)(i0),
                                                                                                    (int)(i1),
                                                                                                    (int)(i2))));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
//#line 253
grid.pr((int)(r.
                                     rank));
        
//#line 255
this.pr("  iterator");
        
//#line 256
this.prArray1(a,
                                   (boolean)(false));
    }
    
    
//#line 259
void
                   prArray1(
                   final x10.
                     array.
                     DistArray<java.lang.Double> a,
                   final boolean bump){
        
//#line 261
TestArray.
          Grid grid =
          ((TestArray.
          Grid)(new TestArray.
          Grid(this)));
        
//#line 262
for (
//#line 262
final x10.core.Iterator<x10.
                            array.
                            Point> p28234 =
                            a.region().iterator();
                          p28234.hasNext();
                          ) {
            
//#line 262
final x10.
              array.
              Point p =
              ((x10.
                array.
                Point)
                p28234.next$G());
            
//#line 264
if (((int) p.
                                      rank) ==
                             ((int) 1)) {
                
//#line 265
final x10.
                  array.
                  DistArray<java.lang.Double> a2 =
                  ((x10.
                  array.
                  DistArray)(new x10.core.fun.Fun_0_1<x10.
                  array.
                  DistArray<java.lang.Double>, x10.
                  array.
                  DistArray<java.lang.Double>>() {public final x10.
                  array.
                  DistArray<java.lang.Double> apply$G(final x10.
                  array.
                  DistArray<java.lang.Double> __desugarer__var__277__) { return apply(__desugarer__var__277__);}
                public final x10.
                  array.
                  DistArray<java.lang.Double> apply(final x10.
                  array.
                  DistArray<java.lang.Double> __desugarer__var__277__) { {
                    
//#line 265
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__277__,null))/* } */ &&
                                       !(((int) __desugarer__var__277__.rank()) ==
                                         ((int) 1))) {
                        
//#line 265
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                             "1}"));
                    }
                    
//#line 265
return __desugarer__var__277__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                }
                }.apply(((x10.
                          array.
                          DistArray)
                          a))));
                
//#line 266
if (bump) {
                    
//#line 266
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                          (int)(p.apply((int)(0))));
                }
                
//#line 267
grid.set((int)(p.apply((int)(0))),
                                      (double)(java.lang.Double)(a2.apply$G((int)(p.apply((int)(0))))));
            } else {
                
//#line 268
if (((int) p.
                                          rank) ==
                                 ((int) 2)) {
                    
//#line 269
final x10.
                      array.
                      DistArray<java.lang.Double> a2 =
                      ((x10.
                      array.
                      DistArray)(new x10.core.fun.Fun_0_1<x10.
                      array.
                      DistArray<java.lang.Double>, x10.
                      array.
                      DistArray<java.lang.Double>>() {public final x10.
                      array.
                      DistArray<java.lang.Double> apply$G(final x10.
                      array.
                      DistArray<java.lang.Double> __desugarer__var__278__) { return apply(__desugarer__var__278__);}
                    public final x10.
                      array.
                      DistArray<java.lang.Double> apply(final x10.
                      array.
                      DistArray<java.lang.Double> __desugarer__var__278__) { {
                        
//#line 269
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__278__,null))/* } */ &&
                                           !(((int) __desugarer__var__278__.rank()) ==
                                             ((int) 2))) {
                            
//#line 269
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                                 "2}"));
                        }
                        
//#line 269
return __desugarer__var__278__;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                    }
                    }.apply(((x10.
                              array.
                              DistArray)
                              a))));
                    
//#line 270
if (bump) {
                        
//#line 270
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                              (int)(p.apply((int)(1))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                              (int)(p.apply((int)(0))),
                                              (int)(p.apply((int)(1))));
                    }
                    
//#line 271
grid.set((int)(p.apply((int)(0))),
                                          (int)(p.apply((int)(1))),
                                          (double)(java.lang.Double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                                (int)(p.apply((int)(1))))));
                } else {
                    
//#line 272
if (((int) p.
                                              rank) ==
                                     ((int) 3)) {
                        
//#line 273
final x10.
                          array.
                          DistArray<java.lang.Double> a2 =
                          ((x10.
                          array.
                          DistArray)(new x10.core.fun.Fun_0_1<x10.
                          array.
                          DistArray<java.lang.Double>, x10.
                          array.
                          DistArray<java.lang.Double>>() {public final x10.
                          array.
                          DistArray<java.lang.Double> apply$G(final x10.
                          array.
                          DistArray<java.lang.Double> __desugarer__var__279__) { return apply(__desugarer__var__279__);}
                        public final x10.
                          array.
                          DistArray<java.lang.Double> apply(final x10.
                          array.
                          DistArray<java.lang.Double> __desugarer__var__279__) { {
                            
//#line 273
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__279__,null))/* } */ &&
                                               !(((int) __desugarer__var__279__.rank()) ==
                                                 ((int) 3))) {
                                
//#line 273
throw new java.lang.ClassCastException(("x10.array.DistArray[x10.lang.Double]{self.dist.region.rank==" +
                                                                                     "3}"));
                            }
                            
//#line 273
return __desugarer__var__279__;
                        }}
                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
                        }
                        }.apply(((x10.
                                  array.
                                  DistArray)
                                  a))));
                        
//#line 274
if (bump) {
                            
//#line 274
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                                  (int)(p.apply((int)(1))),
                                                                                  (int)(p.apply((int)(2))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                  (int)(p.apply((int)(0))),
                                                  (int)(p.apply((int)(1))),
                                                  (int)(p.apply((int)(2))));
                        }
                        
//#line 275
grid.set((int)(p.apply((int)(0))),
                                              (int)(p.apply((int)(1))),
                                              (int)(p.apply((int)(2))),
                                              (double)(java.lang.Double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                                    (int)(p.apply((int)(1))),
                                                                                    (int)(p.apply((int)(2))))));
                    }
                }
            }
        }
        
//#line 278
grid.pr((int)(a.rank()));
    }
    
    
//#line 282
void
                   prPoint(
                   final java.lang.String test,
                   final x10.
                     array.
                     Point p){
        
//#line 283
int sum =
          0;
        
//#line 284
for (
//#line 284
int i =
                            0;
                          ((((int)(i))) < (((int)(p.
                                                    rank))));
                          
//#line 284
i += 1) {
            
//#line 285
sum += p.apply((int)(i));
        }
        
//#line 286
this.pr(((((((((test) + (" "))) + (p))) + (" sum="))) + (sum)));
    }
    
    
//#line 290
void
                   prDist(
                   final java.lang.String test,
                   final x10.
                     array.
                     Dist d){
        
//#line 292
this.pr((((((("--- ") + (test))) + (": "))) + (d)));
        
//#line 294
final x10.core.fun.Fun_0_1<x10.
          array.
          Point,java.lang.Double> init =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
          array.
          Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Point id$28008) { return apply(id$28008);}
        public final double apply(final x10.
          array.
          Point id$28008) { {
            
//#line 294
return (-(((double)(1.0))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 295
final x10.
          array.
          DistArray<java.lang.Double> a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                           x10.
                                             array.
                                             Dist.makeConstant(d.
                                                                 region,
                                                               x10.
                                                                 lang.
                                                                 Runtime.here()),
                                           init)));
        
//#line 297
x10.core.Rail<x10.
          lang.
          Place> ps =
          ((x10.core.Rail)(x10.core.RailFactory.<x10.
          lang.
          Place>makeRailFromValRail(x10.lang.Place._RTT, d.places())));
        
//#line 298
for (
//#line 298
int i =
                            0;
                          ((((int)(i))) < (((int)(ps.
                                                    length))));
                          
//#line 298
i += 1) {
            
//#line 299
final x10.
              array.
              Region r =
              d.get(((x10.
                      lang.
                      Place)((Object[])ps.value)[i]));
            
//#line 300
for (
//#line 300
final x10.core.Iterator<x10.
                                array.
                                Point> p28235 =
                                r.iterator();
                              p28235.hasNext();
                              ) {
                
//#line 300
final x10.
                  array.
                  Point p =
                  ((x10.
                  array.
                  Point)(p28235.next$G()));
                
//#line 301
final x10.
                  array.
                  Point q =
                  ((x10.
                  array.
                  Point)(new x10.core.fun.Fun_0_1<x10.
                  array.
                  Point, x10.
                  array.
                  Point>() {public final x10.
                  array.
                  Point apply$G(final x10.
                  array.
                  Point __desugarer__var__280__) { return apply(__desugarer__var__280__);}
                public final x10.
                  array.
                  Point apply(final x10.
                  array.
                  Point __desugarer__var__280__) { {
                    
//#line 301
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__280__,null))/* } */ &&
                                       !(((int) __desugarer__var__280__.
                                                  rank) ==
                                         ((int) a.
                                                  dist.
                                                  region.
                                                  rank))) {
                        
//#line 301
throw new java.lang.ClassCastException("x10.array.Point{self.rank==a.dist.region.rank}");
                    }
                    
//#line 301
return __desugarer__var__280__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                }
                }.apply(((x10.
                          array.
                          Point)
                          p))));
                
//#line 302
a.set$G((double)(((((double)(((((double)(a.apply$G(q)))) + (((double)(((double)(int)(((int)(((x10.
                                                                                                                           lang.
                                                                                                                           Place)((Object[])ps.value)[i]).
                                                                                                                           id))))))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                     q);
            }
        }
        
//#line 305
this.prArray1(a,
                                   (boolean)(false));
    }
    
    
//#line 309
void
                   pr(
                   final java.lang.String s){
        
//#line 310
out.println(s);
    }
    
    
//#line 313
static void
                   xxx(
                   final java.lang.String s){
        
//#line 314
x10.
          io.
          Console.OUT.println((("xxx ") + (s)));
    }
    
    
//#line 318
x10.
                   array.
                   Region
                   r(
                   final int a,
                   final int b,
                   final int c,
                   final int d){
        
//#line 319
return x10.
          array.
          Region.makeRectangular(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { a,c })/* } */),
                                 x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { b,d })/* } */));
    }
    
    
//#line 182
final private static class Anonymous$18
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$18>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$18>(
    /* base class */TestArray.
      Anonymous$18.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 182
java.lang.String
                       run(
                       ){
            
//#line 182
return (("") + (this.
                                           r.
                                           rank));
        }
        
        
//#line 182
private Anonymous$18(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 182
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
//#line 183
final private static class Anonymous$19
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$19>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$19>(
    /* base class */TestArray.
      Anonymous$19.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 183
java.lang.String
                       run(
                       ){
            
//#line 183
return (("") + (this.
                                           r.
                                           rect));
        }
        
        
//#line 183
private Anonymous$19(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 183
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
//#line 184
final private static class Anonymous$20
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$20>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$20>(
    /* base class */TestArray.
      Anonymous$20.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 184
java.lang.String
                       run(
                       ){
            
//#line 184
return (("") + (this.
                                           r.
                                           zeroBased));
        }
        
        
//#line 184
private Anonymous$20(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 184
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
//#line 185
final private static class Anonymous$21
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$21>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$21>(
    /* base class */TestArray.
      Anonymous$21.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 185
java.lang.String
                       run(
                       ){
            
//#line 185
return (("") + (this.
                                           r.rail()));
        }
        
        
//#line 185
private Anonymous$21(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 185
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
//#line 187
final private static class Anonymous$22
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$22>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$22>(
    /* base class */TestArray.
      Anonymous$22.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 187
java.lang.String
                       run(
                       ){
            
//#line 187
return (("") + (this.
                                           r.isConvex()));
        }
        
        
//#line 187
private Anonymous$22(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 187
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
//#line 188
final private static class Anonymous$23
                 extends TestArray.
                   R
                 {public static final x10.rtt.RuntimeType<TestArray.
      Anonymous$23>_RTT = new x10.rtt.RuntimeType<TestArray.
      Anonymous$23>(
    /* base class */TestArray.
      Anonymous$23.class
    , /* parents */ new x10.rtt.Type[] {TestArray.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestArray
          out$;
        
//#line 178
final private x10.
          array.
          Region
          r;
        
        
//#line 188
java.lang.String
                       run(
                       ){
            
//#line 188
return (("") + (this.
                                           r.size()));
        }
        
        
//#line 188
private Anonymous$23(final TestArray out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final x10.
                                            io.
                                            Printer a1,
                                          final java.lang.String a2) {
            
//#line 188
super(out$,
                               a1,
                               a2);
            
//#line 21
this.out$ = out$;
            
//#line 178
this.r = r;
        }
    
    }
    
    
    }
    