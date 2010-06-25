
abstract public class TestDist
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<TestDist>_RTT = new x10.rtt.RuntimeType<TestDist>(
/* base class */TestDist.class
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
TestDist() {
        
//#line 27
super();
        
//#line 25
this.testName = x10.core.Ref.typeName(this);
        
//#line 28
java.lang.System.setProperty("line.separator","\n");
        
//#line 30
final x10.
          io.
          StringWriter tmp =
          ((x10.
          io.
          StringWriter)(new x10.
          io.
          StringWriter()));
        
//#line 31
this.os = tmp;
        
//#line 32
this.out = new x10.
          io.
          Printer(tmp);
    }
    
    
//#line 35
abstract java.lang.String
                  expected(
                  );
    
    
//#line 37
boolean
                  status(
                  ){
        
//#line 38
final java.lang.String got =
          os.result();
        
//#line 39
if ((got).equals(this.expected())) {
            
//#line 40
return true;
        } else {
            
//#line 42
x10.
              io.
              Console.OUT.println((("=== got:\n") + (got)));
            
//#line 43
x10.
              io.
              Console.OUT.println((("=== expected:\n") + (this.expected())));
            
//#line 44
x10.
              io.
              Console.OUT.println("=== ");
            
//#line 45
return false;
        }
    }
    
    
//#line 53
abstract static class R
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<TestDist.
      R>_RTT = new x10.rtt.RuntimeType<TestDist.
      R>(
    /* base class */TestDist.
      R.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 54
final java.lang.String
          testName;
        
        
//#line 56
R(final TestDist out$,
                      final java.lang.String test) {
            
//#line 56
super();
            
//#line 21
this.out$ = out$;
            
//#line 57
this.testName = test;
        }
        
        
//#line 60
void
                      runTest(
                      ){
            
//#line 61
java.lang.String r;
            
//#line 62
try {{
                
//#line 63
r = this.run();
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            final java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 65
r = e.getMessage();
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final java.lang.Throwable e) {
                
//#line 65
r = e.getMessage();
            }
            
//#line 67
this.
                          out$.pr(((((testName) + (" "))) + (r)));
        }
        
        
//#line 70
abstract java.lang.String
                      run(
                      );
    
    }
    
    
//#line 74
static class Grid
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<TestDist.
      Grid>_RTT = new x10.rtt.RuntimeType<TestDist.
      Grid>(
    /* base class */TestDist.
      Grid.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 76
x10.core.Rail<java.lang.Object>
          os;
        
        
//#line 78
void
                      set(
                      final int i0,
                      final double vue){
            
//#line 79
((Object[])os.value)[i0] = x10.
              util.
              Box.<java.lang.Double>$implicit_convert(x10.rtt.Types.DOUBLE,
                                                      (double)(vue));
        }
        
        
//#line 82
void
                      set(
                      final int i0,
                      final int i1,
                      final double vue){
            
//#line 83
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i0]),null)/* } */) {
                
//#line 83
((Object[])os.value)[i0] = new TestDist.
                  Grid(this.
                         out$);
            }
            
//#line 84
final TestDist.
              Grid grid =
              ((TestDist.
              Grid)(new x10.core.fun.Fun_0_1<TestDist.
              Grid, TestDist.
              Grid>() {public final TestDist.
              Grid apply$G(final TestDist.
              Grid __desugarer__var__591__) { return apply(__desugarer__var__591__);}
            public final TestDist.
              Grid apply(final TestDist.
              Grid __desugarer__var__591__) { {
                
//#line 84
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__591__,null))/* } */ &&
                                  !x10.core.Ref.at(__desugarer__var__591__, x10.
                                  lang.
                                  Runtime.here().id)) {
                    
//#line 84
throw new java.lang.ClassCastException("TestDist.Grid{self.home==here}");
                }
                
//#line 84
return __desugarer__var__591__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestDist.Grid._RTT;if (i ==1) return TestDist.Grid._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final TestDist.
                      Grid cast(TestDist.
                      Grid self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = TestDist.Grid._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((TestDist.
                      Grid) ((java.lang.Object)((Object[])os.value)[i0])))/* } */)));
            
//#line 85
grid.set((int)(i1),
                                 (double)(vue));
        }
        
        
//#line 88
void
                      set(
                      final int i0,
                      final int i1,
                      final int i2,
                      final double vue){
            
//#line 89
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i0]),null)/* } */) {
                
//#line 89
((Object[])os.value)[i0] = new TestDist.
                  Grid(this.
                         out$);
            }
            
//#line 90
final TestDist.
              Grid grid =
              ((TestDist.
              Grid)(new x10.core.fun.Fun_0_1<TestDist.
              Grid, TestDist.
              Grid>() {public final TestDist.
              Grid apply$G(final TestDist.
              Grid __desugarer__var__592__) { return apply(__desugarer__var__592__);}
            public final TestDist.
              Grid apply(final TestDist.
              Grid __desugarer__var__592__) { {
                
//#line 90
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__592__,null))/* } */ &&
                                  !x10.core.Ref.at(__desugarer__var__592__, x10.
                                  lang.
                                  Runtime.here().id)) {
                    
//#line 90
throw new java.lang.ClassCastException("TestDist.Grid{self.home==here}");
                }
                
//#line 90
return __desugarer__var__592__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestDist.Grid._RTT;if (i ==1) return TestDist.Grid._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final TestDist.
                      Grid cast(TestDist.
                      Grid self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = TestDist.Grid._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((TestDist.
                      Grid) ((java.lang.Object)((Object[])os.value)[i0])))/* } */)));
            
//#line 91
grid.set((int)(i1),
                                 (int)(i2),
                                 (double)(vue));
        }
        
        
//#line 94
void
                      pr(
                      final int rank){
            
//#line 95
int min =
              os.
                length;
            
//#line 96
int max =
              0;
            
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
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(((java.lang.Object)((Object[])os.value)[i]),null))/* } */) {
                    
//#line 99
if (((((int)(i))) < (((int)(min))))) {
                        
//#line 99
min = i;
                    } else {
                        
//#line 100
if (((((int)(i))) > (((int)(max))))) {
                            
//#line 100
max = i;
                        }
                    }
                }
            }
            
//#line 103
for (
//#line 103
int i =
                                0;
                              ((((int)(i))) < (((int)(os.
                                                        length))));
                              
//#line 103
i += 1) {
                
//#line 104
java.lang.Object o =
                  ((java.lang.Object)((Object[])os.value)[i]);
                
//#line 105
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(o,null)/* } */) {
                    
//#line 106
if (((int) rank) ==
                                     ((int) 1)) {
                        
//#line 107
this.
                                       out$.
                                       out.print(".");
                    } else {
                        
//#line 108
if (((int) rank) ==
                                         ((int) 2)) {
                            
//#line 109
if (((((int)(min))) <= (((int)(i)))) &&
                                             ((((int)(i))) <= (((int)(max))))) {
                                
//#line 110
this.
                                               out$.
                                               out.print((((("    ") + (i))) + ("\n")));
                            }
                        }
                    }
                } else {
                    
//#line 112
if (TestDist.Grid._RTT.instanceof$(o)) {
                        
//#line 113
if (((int) rank) ==
                                         ((int) 2)) {
                            
//#line 114
this.
                                           out$.
                                           out.print((((("    ") + (i))) + ("  ")));
                        } else {
                            
//#line 115
if (((((int)(rank))) >= (((int)(3))))) {
                                
//#line 116
this.
                                               out$.
                                               out.print("    ");
                                
//#line 117
for (
//#line 117
int j =
                                                    0;
                                                  ((((int)(j))) < (((int)(rank))));
                                                  
//#line 117
j += 1) {
                                    
//#line 118
this.
                                                   out$.
                                                   out.print("-");
                                }
                                
//#line 119
this.
                                               out$.
                                               out.print(((((" ") + (i))) + ("\n")));
                            }
                        }
                        
//#line 121
(new x10.core.fun.Fun_0_1<TestDist.
                                        Grid, TestDist.
                                        Grid>() {public final TestDist.
                                        Grid apply$G(final TestDist.
                                        Grid __desugarer__var__593__) { return apply(__desugarer__var__593__);}
                                      public final TestDist.
                                        Grid apply(final TestDist.
                                        Grid __desugarer__var__593__) { {
                                          
//#line 121
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__593__,null))/* } */ &&
                                                             !x10.core.Ref.at(__desugarer__var__593__, x10.
                                                             lang.
                                                             Runtime.here().id)) {
                                              
//#line 121
throw new java.lang.ClassCastException("TestDist.Grid{self.home==here}");
                                          }
                                          
//#line 121
return __desugarer__var__593__;
                                      }}
                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return TestDist.Grid._RTT;if (i ==1) return TestDist.Grid._RTT;return null;
                                      }
                                      }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                                                  final TestDist.
                                                Grid cast(TestDist.
                                                Grid self) {
                                                      if (self==null) return null;
                                                      x10.rtt.Type rtt = TestDist.Grid._RTT;
                                                      if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                                                      boolean sat = true;
                                                      if (! sat) throw new java.lang.ClassCastException();
                                                      return self;
                                                  }
                                              }.cast((TestDist.
                                                Grid) o))/* } */)).pr((int)(((((int)(rank))) - (((int)(1))))));
                    } else {
                        
//#line 123
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
                        
//#line 124
this.
                                       out$.
                                       out.print((((((int)(double)(((double)(d)))))) + ("")));
                    }
                }
                
//#line 127
if (((int) rank) ==
                                 ((int) 1)) {
                    
//#line 128
this.
                                   out$.
                                   out.print(" ");
                }
            }
            
//#line 130
if (((int) rank) ==
                             ((int) 1)) {
                
//#line 131
this.
                               out$.
                               out.print("\n");
            }
        }
        
        public Grid(final TestDist out$) {
            super();
            
//#line 21
this.out$ = out$;
            
//#line 76
this.os = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Object>makeVarRail(x10.rtt.Types.runtimeType(java.lang.Object.class), ((int)(10)))));
        }
    
    }
    
    
    
//#line 135
x10.
                   array.
                   Array<java.lang.Double>
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r){
        
//#line 136
return this.prArray(test,
                                         r,
                                         (boolean)(false));
    }
    
    
//#line 139
x10.
                   array.
                   Array<java.lang.Double>
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r,
                   final boolean bump){
        
//#line 141
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
            
//#line 142
int v =
              1;
            
//#line 143
for (
//#line 143
int i =
                                0;
                              ((((int)(i))) < (((int)(pt.
                                                        rank))));
                              
//#line 143
i += 1) {
                
//#line 144
v *= pt.apply((int)(i));
            }
            
//#line 145
return ((double)(int)(((int)(((((int)(v))) % (((int)(10))))))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 148
final x10.core.fun.Fun_0_1<x10.
          array.
          Point,java.lang.Double> init0 =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
          array.
          Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Point id$79623) { return apply(id$79623);}
        public final double apply(final x10.
          array.
          Point id$79623) { {
            
//#line 148
return 0.0;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 150
final x10.
          array.
          Array<java.lang.Double> a =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  r,
                                  bump
                                    ? init0
                                    : init1)));
        
//#line 151
this.prArray(test,
                                  a,
                                  (boolean)(bump));
        
//#line 153
return new x10.core.fun.Fun_0_1<x10.
          array.
          Array<java.lang.Double>, x10.
          array.
          Array<java.lang.Double>>() {public final x10.
          array.
          Array<java.lang.Double> apply$G(final x10.
          array.
          Array<java.lang.Double> __desugarer__var__594__) { return apply(__desugarer__var__594__);}
        public final x10.
          array.
          Array<java.lang.Double> apply(final x10.
          array.
          Array<java.lang.Double> __desugarer__var__594__) { {
            
//#line 153
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__594__,null))/* } */ &&
                               !(((int) __desugarer__var__594__.rank()) ==
                                 ((int) r.
                                          rank) &&
                                 /* template:equalsequals { */x10.rtt.Equality.equalsequals(((x10.
                                   lang.
                                   Place)(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__594__)))),x10.
                                   lang.
                                   Runtime.here())/* } */)) {
                
//#line 153
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.region.rank==r.rank, s" +
                                                                     "elf.home==here}"));
            }
            
//#line 153
return __desugarer__var__594__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
        }
        }.apply(((x10.
                  array.
                  Array)
                  a));
    }
    
    
//#line 156
void
                   prRegion(
                   final java.lang.String test,
                   final x10.
                     array.
                     Region r){
        
//#line 158
this.pr((((((("--- ") + (testName))) + (": "))) + (test)));
        
//#line 160
new TestDist.
          Anonymous$83(this,
                       r,
                       "rank").runTest();
        
//#line 161
new TestDist.
          Anonymous$84(this,
                       r,
                       "rect").runTest();
        
//#line 162
new TestDist.
          Anonymous$85(this,
                       r,
                       "zeroBased").runTest();
        
//#line 163
new TestDist.
          Anonymous$86(this,
                       r,
                       "rail").runTest();
        
//#line 165
new TestDist.
          Anonymous$87(this,
                       r,
                       "isConvex()").runTest();
        
//#line 166
new TestDist.
          Anonymous$88(this,
                       r,
                       "size()").runTest();
        
//#line 168
this.pr((("region: ") + (r)));
    }
    
    
//#line 171
void
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Array<java.lang.Double> a){
        
//#line 172
this.prArray(test,
                                  a,
                                  (boolean)(false));
    }
    
    
//#line 175
void
                   prArray(
                   final java.lang.String test,
                   final x10.
                     array.
                     Array<java.lang.Double> a,
                   final boolean bump){
        
//#line 177
final x10.
          array.
          Region r =
          a.
            region;
        
//#line 179
this.prRegion(test,
                                   r);
        
//#line 182
TestDist.
          Grid grid =
          ((TestDist.
          Grid)(new TestDist.
          Grid(this)));
        
//#line 183
final x10.core.Iterator<x10.
          array.
          Region.
          Scanner> it =
          ((x10.core.Iterator)(new x10.core.fun.Fun_0_1<x10.core.Iterator<x10.
          array.
          Region.
          Scanner>, x10.core.Iterator<x10.
          array.
          Region.
          Scanner>>() {public final x10.core.Iterator<x10.
          array.
          Region.
          Scanner> apply$G(final x10.core.Iterator<x10.
          array.
          Region.
          Scanner> __desugarer__var__595__) { return apply(__desugarer__var__595__);}
        public final x10.core.Iterator<x10.
          array.
          Region.
          Scanner> apply(final x10.core.Iterator<x10.
          array.
          Region.
          Scanner> __desugarer__var__595__) { {
            
//#line 183
if (!x10.core.Ref.at(__desugarer__var__595__, x10.
                               lang.
                               Runtime.here().id)) {
                
//#line 183
throw new java.lang.ClassCastException("x10.lang.Iterator[x10.array.Region.Scanner]{self.home==here}");
            }
            
//#line 183
return __desugarer__var__595__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(x10.core.Iterator.class);if (i ==1) return x10.rtt.Types.runtimeType(x10.core.Iterator.class);return null;
        }
        }.apply(((x10.core.Iterator)
                  r.scanners()))));
        
//#line 184
while (it.hasNext()) {
            
//#line 185
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
              Scanner __desugarer__var__596__) { return apply(__desugarer__var__596__);}
            public final x10.
              array.
              Region.
              Scanner apply(final x10.
              array.
              Region.
              Scanner __desugarer__var__596__) { {
                
//#line 185
if (!x10.core.Ref.at(__desugarer__var__596__, x10.
                                   lang.
                                   Runtime.here().id)) {
                    
//#line 185
throw new java.lang.ClassCastException("x10.array.Region.Scanner{self.home==here}");
                }
                
//#line 185
return __desugarer__var__596__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region.Scanner._RTT;if (i ==1) return x10.array.Region.Scanner._RTT;return null;
            }
            }.apply(((x10.
                      array.
                      Region.
                      Scanner)
                      it.next$G()))));
            
//#line 186
this.pr("  poly");
            
//#line 187
if (((int) r.
                                      rank) ==
                             ((int) 0)) {
                
//#line 188
this.pr("ERROR rank==0");
            } else {
                
//#line 189
if (((int) r.
                                          rank) ==
                                 ((int) 1)) {
                    
//#line 190
final x10.
                      array.
                      Array<java.lang.Double> a2 =
                      ((x10.
                      array.
                      Array)(new x10.core.fun.Fun_0_1<x10.
                      array.
                      Array<java.lang.Double>, x10.
                      array.
                      Array<java.lang.Double>>() {public final x10.
                      array.
                      Array<java.lang.Double> apply$G(final x10.
                      array.
                      Array<java.lang.Double> __desugarer__var__597__) { return apply(__desugarer__var__597__);}
                    public final x10.
                      array.
                      Array<java.lang.Double> apply(final x10.
                      array.
                      Array<java.lang.Double> __desugarer__var__597__) { {
                        
//#line 190
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__597__,null))/* } */ &&
                                           !x10.core.Ref.at(__desugarer__var__597__, x10.
                                           lang.
                                           Runtime.here().id)) {
                            
//#line 190
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                                 "n.rank==1}"));
                        }
                        
//#line 190
return __desugarer__var__597__;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                    }
                    }.apply(((x10.
                              array.
                              Array)
                              a))));
                    
//#line 191
int min0 =
                      s.min((int)(0));
                    
//#line 192
int max0 =
                      s.max((int)(0));
                    
//#line 193
for (
//#line 193
int i0 =
                                        min0;
                                      ((((int)(i0))) <= (((int)(max0))));
                                      
//#line 193
i0 += 1) {
                        
//#line 194
if (bump) {
                            
//#line 194
a2.set$G((double)(((((double)(a2.apply$G((int)(i0))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                  (int)(i0));
                        }
                        
//#line 195
grid.set((int)(i0),
                                              (double)(java.lang.Double)(a2.apply$G((int)(i0))));
                    }
                } else {
                    
//#line 197
if (((int) r.
                                              rank) ==
                                     ((int) 2)) {
                        
//#line 198
final x10.
                          array.
                          Array<java.lang.Double> a2 =
                          ((x10.
                          array.
                          Array)(new x10.core.fun.Fun_0_1<x10.
                          array.
                          Array<java.lang.Double>, x10.
                          array.
                          Array<java.lang.Double>>() {public final x10.
                          array.
                          Array<java.lang.Double> apply$G(final x10.
                          array.
                          Array<java.lang.Double> __desugarer__var__598__) { return apply(__desugarer__var__598__);}
                        public final x10.
                          array.
                          Array<java.lang.Double> apply(final x10.
                          array.
                          Array<java.lang.Double> __desugarer__var__598__) { {
                            
//#line 198
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__598__,null))/* } */ &&
                                               !x10.core.Ref.at(__desugarer__var__598__, x10.
                                               lang.
                                               Runtime.here().id)) {
                                
//#line 198
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                                     "n.rank==2}"));
                            }
                            
//#line 198
return __desugarer__var__598__;
                        }}
                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                        }
                        }.apply(((x10.
                                  array.
                                  Array)
                                  a))));
                        
//#line 199
int min0 =
                          s.min((int)(0));
                        
//#line 200
int max0 =
                          s.max((int)(0));
                        
//#line 201
for (
//#line 201
int i0 =
                                            min0;
                                          ((((int)(i0))) <= (((int)(max0))));
                                          
//#line 201
i0 += 1) {
                            
//#line 202
s.set((int)(0),
                                               (int)(i0));
                            
//#line 203
int min1 =
                              s.min((int)(1));
                            
//#line 204
int max1 =
                              s.max((int)(1));
                            
//#line 205
for (
//#line 205
int i1 =
                                                min1;
                                              ((((int)(i1))) <= (((int)(max1))));
                                              
//#line 205
i1 += 1) {
                                
//#line 206
if (bump) {
                                    
//#line 206
a2.set$G((double)(((((double)(a2.apply$G((int)(i0),
                                                                                          (int)(i1))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                          (int)(i0),
                                                          (int)(i1));
                                }
                                
//#line 207
grid.set((int)(i0),
                                                      (int)(i1),
                                                      (double)(java.lang.Double)(a2.apply$G((int)(i0),
                                                                                            (int)(i1))));
                            }
                        }
                    } else {
                        
//#line 210
if (((int) r.
                                                  rank) ==
                                         ((int) 3)) {
                            
//#line 211
final x10.
                              array.
                              Array<java.lang.Double> a2 =
                              ((x10.
                              array.
                              Array)(new x10.core.fun.Fun_0_1<x10.
                              array.
                              Array<java.lang.Double>, x10.
                              array.
                              Array<java.lang.Double>>() {public final x10.
                              array.
                              Array<java.lang.Double> apply$G(final x10.
                              array.
                              Array<java.lang.Double> __desugarer__var__599__) { return apply(__desugarer__var__599__);}
                            public final x10.
                              array.
                              Array<java.lang.Double> apply(final x10.
                              array.
                              Array<java.lang.Double> __desugarer__var__599__) { {
                                
//#line 211
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__599__,null))/* } */ &&
                                                   !x10.core.Ref.at(__desugarer__var__599__, x10.
                                                   lang.
                                                   Runtime.here().id)) {
                                    
//#line 211
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                                         "n.rank==3}"));
                                }
                                
//#line 211
return __desugarer__var__599__;
                            }}
                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                            }
                            }.apply(((x10.
                                      array.
                                      Array)
                                      a))));
                            
//#line 212
int min0 =
                              s.min((int)(0));
                            
//#line 213
int max0 =
                              s.max((int)(0));
                            
//#line 214
for (
//#line 214
int i0 =
                                                min0;
                                              ((((int)(i0))) <= (((int)(max0))));
                                              
//#line 214
i0 += 1) {
                                
//#line 215
s.set((int)(0),
                                                   (int)(i0));
                                
//#line 216
int min1 =
                                  s.min((int)(1));
                                
//#line 217
int max1 =
                                  s.max((int)(1));
                                
//#line 218
for (
//#line 218
int i1 =
                                                    min1;
                                                  ((((int)(i1))) <= (((int)(max1))));
                                                  
//#line 218
i1 += 1) {
                                    
//#line 219
s.set((int)(1),
                                                       (int)(i1));
                                    
//#line 220
int min2 =
                                      s.min((int)(2));
                                    
//#line 221
int max2 =
                                      s.max((int)(2));
                                    
//#line 222
for (
//#line 222
int i2 =
                                                        min2;
                                                      ((((int)(i2))) <= (((int)(max2))));
                                                      
//#line 222
i2 += 1) {
                                        
//#line 223
if (bump) {
                                            
//#line 223
a2.set$G((double)(((((double)(a2.apply$G((int)(i0),
                                                                                                  (int)(i1),
                                                                                                  (int)(i2))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                                  (int)(i0),
                                                                  (int)(i1),
                                                                  (int)(i2));
                                        }
                                        
//#line 224
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
        
//#line 230
grid.pr((int)(r.
                                     rank));
        
//#line 232
this.pr("  iterator");
        
//#line 233
this.prArray1(a,
                                   (boolean)(false));
    }
    
    
//#line 236
void
                   prArray1(
                   final x10.
                     array.
                     Array<java.lang.Double> a,
                   final boolean bump){
        
//#line 238
TestDist.
          Grid grid =
          ((TestDist.
          Grid)(new TestDist.
          Grid(this)));
        
//#line 239
for (
//#line 239
final x10.core.Iterator<x10.
                            array.
                            Point> p79817 =
                            a.
                              region.iterator();
                          p79817.hasNext();
                          ) {
            
//#line 239
final x10.
              array.
              Point p =
              ((x10.
                array.
                Point)
                p79817.next$G());
            
//#line 240
if (((int) p.
                                      rank) ==
                             ((int) 1)) {
                
//#line 241
final x10.
                  array.
                  Array<java.lang.Double> a2 =
                  ((x10.
                  array.
                  Array)(new x10.core.fun.Fun_0_1<x10.
                  array.
                  Array<java.lang.Double>, x10.
                  array.
                  Array<java.lang.Double>>() {public final x10.
                  array.
                  Array<java.lang.Double> apply$G(final x10.
                  array.
                  Array<java.lang.Double> __desugarer__var__600__) { return apply(__desugarer__var__600__);}
                public final x10.
                  array.
                  Array<java.lang.Double> apply(final x10.
                  array.
                  Array<java.lang.Double> __desugarer__var__600__) { {
                    
//#line 241
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__600__,null))/* } */ &&
                                       !x10.core.Ref.at(__desugarer__var__600__, x10.
                                       lang.
                                       Runtime.here().id)) {
                        
//#line 241
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                             "n.rank==1}"));
                    }
                    
//#line 241
return __desugarer__var__600__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                }
                }.apply(((x10.
                          array.
                          Array)
                          a))));
                
//#line 242
if (bump) {
                    
//#line 242
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                          (int)(p.apply((int)(0))));
                }
                
//#line 243
grid.set((int)(p.apply((int)(0))),
                                      (double)(java.lang.Double)(a2.apply$G((int)(p.apply((int)(0))))));
            } else {
                
//#line 244
if (((int) p.
                                          rank) ==
                                 ((int) 2)) {
                    
//#line 245
final x10.
                      array.
                      Array<java.lang.Double> a2 =
                      ((x10.
                      array.
                      Array)(new x10.core.fun.Fun_0_1<x10.
                      array.
                      Array<java.lang.Double>, x10.
                      array.
                      Array<java.lang.Double>>() {public final x10.
                      array.
                      Array<java.lang.Double> apply$G(final x10.
                      array.
                      Array<java.lang.Double> __desugarer__var__601__) { return apply(__desugarer__var__601__);}
                    public final x10.
                      array.
                      Array<java.lang.Double> apply(final x10.
                      array.
                      Array<java.lang.Double> __desugarer__var__601__) { {
                        
//#line 245
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__601__,null))/* } */ &&
                                           !x10.core.Ref.at(__desugarer__var__601__, x10.
                                           lang.
                                           Runtime.here().id)) {
                            
//#line 245
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                                 "n.rank==2}"));
                        }
                        
//#line 245
return __desugarer__var__601__;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                    }
                    }.apply(((x10.
                              array.
                              Array)
                              a))));
                    
//#line 246
if (bump) {
                        
//#line 246
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                              (int)(p.apply((int)(1))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                              (int)(p.apply((int)(0))),
                                              (int)(p.apply((int)(1))));
                    }
                    
//#line 247
grid.set((int)(p.apply((int)(0))),
                                          (int)(p.apply((int)(1))),
                                          (double)(java.lang.Double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                                (int)(p.apply((int)(1))))));
                } else {
                    
//#line 248
if (((int) p.
                                              rank) ==
                                     ((int) 3)) {
                        
//#line 249
final x10.
                          array.
                          Array<java.lang.Double> a2 =
                          ((x10.
                          array.
                          Array)(new x10.core.fun.Fun_0_1<x10.
                          array.
                          Array<java.lang.Double>, x10.
                          array.
                          Array<java.lang.Double>>() {public final x10.
                          array.
                          Array<java.lang.Double> apply$G(final x10.
                          array.
                          Array<java.lang.Double> __desugarer__var__602__) { return apply(__desugarer__var__602__);}
                        public final x10.
                          array.
                          Array<java.lang.Double> apply(final x10.
                          array.
                          Array<java.lang.Double> __desugarer__var__602__) { {
                            
//#line 249
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__602__,null))/* } */ &&
                                               !x10.core.Ref.at(__desugarer__var__602__, x10.
                                               lang.
                                               Runtime.here().id)) {
                                
//#line 249
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                                     "n.rank==3}"));
                            }
                            
//#line 249
return __desugarer__var__602__;
                        }}
                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
                        }
                        }.apply(((x10.
                                  array.
                                  Array)
                                  a))));
                        
//#line 250
if (bump) {
                            
//#line 250
a2.set$G((double)(((((double)(a2.apply$G((int)(p.apply((int)(0))),
                                                                                  (int)(p.apply((int)(1))),
                                                                                  (int)(p.apply((int)(2))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                                  (int)(p.apply((int)(0))),
                                                  (int)(p.apply((int)(1))),
                                                  (int)(p.apply((int)(2))));
                        }
                        
//#line 251
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
        
//#line 254
grid.pr((int)(a.rank()));
    }
    
    
//#line 258
void
                   prDist(
                   final java.lang.String test,
                   final x10.
                     array.
                     Dist d){
        
//#line 260
this.pr((((((("--- ") + (test))) + (": "))) + (d)));
        
//#line 262
final x10.core.fun.Fun_0_1<x10.
          array.
          Point,java.lang.Double> init =
          ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<x10.
          array.
          Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Point id$79624) { return apply(id$79624);}
        public final double apply(final x10.
          array.
          Point id$79624) { {
            
//#line 262
return (-(((double)(1.0))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 263
final x10.
          array.
          Array<java.lang.Double> a =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  d.
                                    region,
                                  init)));
        
//#line 265
x10.core.Rail<x10.
          lang.
          Place> ps =
          ((x10.core.Rail)(x10.core.RailFactory.<x10.
          lang.
          Place>makeRailFromValRail(x10.lang.Place._RTT, d.places())));
        
//#line 266
for (
//#line 266
int i =
                            0;
                          ((((int)(i))) < (((int)(ps.
                                                    length))));
                          
//#line 266
i += 1) {
            
//#line 267
final x10.
              array.
              Region r =
              d.get(((x10.
                      lang.
                      Place)((Object[])ps.value)[i]));
            
//#line 268
for (
//#line 268
final x10.core.Iterator<x10.
                                array.
                                Point> p79818 =
                                r.iterator();
                              p79818.hasNext();
                              ) {
                
//#line 268
final x10.
                  array.
                  Point p =
                  ((x10.
                  array.
                  Point)(p79818.next$G()));
                
//#line 269
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
                  Point __desugarer__var__603__) { return apply(__desugarer__var__603__);}
                public final x10.
                  array.
                  Point apply(final x10.
                  array.
                  Point __desugarer__var__603__) { {
                    
//#line 269
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__603__,null))/* } */ &&
                                       !(((int) __desugarer__var__603__.
                                                  rank) ==
                                         ((int) a.
                                                  region.
                                                  rank))) {
                        
//#line 269
throw new java.lang.ClassCastException("x10.array.Point{self.rank==a.region.rank}");
                    }
                    
//#line 269
return __desugarer__var__603__;
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                }
                }.apply(((x10.
                          array.
                          Point)
                          p))));
                
//#line 270
a.set$G((double)(((((double)(((((double)(a.apply$G(q)))) + (((double)(((double)(int)(((int)(((x10.
                                                                                                                           lang.
                                                                                                                           Place)((Object[])ps.value)[i]).
                                                                                                                           id))))))))))) + (((double)(((double)(int)(((int)(1))))))))),
                                     q);
            }
        }
        
//#line 273
this.prArray1(a,
                                   (boolean)(false));
    }
    
    
//#line 277
void
                   pr(
                   final java.lang.String s){
        
//#line 278
x10.
          lang.
          Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(this)),
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 278
out.println(s);
                        }}
                        });
    }
    
    
//#line 281
static void
                   xxx(
                   final java.lang.String s){
        
//#line 282
x10.
          io.
          Console.OUT.println((("xxx ") + (s)));
    }
    
    
//#line 286
x10.
                   array.
                   Region
                   r(
                   final int a,
                   final int b,
                   final int c,
                   final int d){
        
//#line 287
return x10.
          array.
          Region.makeRectangular(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { a,c })/* } */),
                                 x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { b,d })/* } */));
    }
    
    
//#line 160
final private static class Anonymous$83
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$83>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$83>(
    /* base class */TestDist.
      Anonymous$83.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 160
java.lang.String
                       run(
                       ){
            
//#line 160
return (("") + (this.
                                           r.
                                           rank));
        }
        
        
//#line 160
private Anonymous$83(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 160
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    
    
//#line 161
final private static class Anonymous$84
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$84>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$84>(
    /* base class */TestDist.
      Anonymous$84.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 161
java.lang.String
                       run(
                       ){
            
//#line 161
return (("") + (this.
                                           r.
                                           rect));
        }
        
        
//#line 161
private Anonymous$84(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 161
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    
    
//#line 162
final private static class Anonymous$85
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$85>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$85>(
    /* base class */TestDist.
      Anonymous$85.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 162
java.lang.String
                       run(
                       ){
            
//#line 162
return (("") + (this.
                                           r.
                                           zeroBased));
        }
        
        
//#line 162
private Anonymous$85(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 162
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    
    
//#line 163
final private static class Anonymous$86
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$86>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$86>(
    /* base class */TestDist.
      Anonymous$86.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 163
java.lang.String
                       run(
                       ){
            
//#line 163
return (("") + (this.
                                           r.rail()));
        }
        
        
//#line 163
private Anonymous$86(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 163
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    
    
//#line 165
final private static class Anonymous$87
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$87>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$87>(
    /* base class */TestDist.
      Anonymous$87.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 165
java.lang.String
                       run(
                       ){
            
//#line 165
return (("") + (this.
                                           r.isConvex()));
        }
        
        
//#line 165
private Anonymous$87(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 165
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    
    
//#line 166
final private static class Anonymous$88
                 extends TestDist.
                   R
                 {public static final x10.rtt.RuntimeType<TestDist.
      Anonymous$88>_RTT = new x10.rtt.RuntimeType<TestDist.
      Anonymous$88>(
    /* base class */TestDist.
      Anonymous$88.class
    , /* parents */ new x10.rtt.Type[] {TestDist.R._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final private TestDist
          out$;
        
//#line 156
final private x10.
          array.
          Region
          r;
        
        
//#line 166
java.lang.String
                       run(
                       ){
            
//#line 166
return (("") + (this.
                                           r.size()));
        }
        
        
//#line 166
private Anonymous$88(final TestDist out$,
                                          final x10.
                                            array.
                                            Region r,
                                          final java.lang.String a1) {
            
//#line 166
super(out$,
                               a1);
            
//#line 21
this.out$ = out$;
            
//#line 156
this.r = r;
        }
    
    }
    

}
