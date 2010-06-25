public class GenericClass<T extends Coord>
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<GenericClass>_RTT = new x10.rtt.RuntimeType<GenericClass>(
/* base class */GenericClass.class, 
/* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT}
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}

public x10.rtt.Type getParam(int i) {if (i ==0)return T;return null;}

    private final x10.rtt.Type T;
    
    
    
//#line 2
public void
                 doStuff(
                 final T t){
        
//#line 3
final int x =
          new x10.core.fun.Fun_0_1<T, T>() {public final T apply$G(final T __desugarer__var__0__) { {
              
//#line 3
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__0__,null))/* } */ &&
                               !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__0__)),x10.
                                   lang.
                                   Runtime.here())/* } */)) {
                  
//#line 3
throw new java.lang.ClassCastException("T{self.home==here}");
              }
              
//#line 3
return __desugarer__var__0__;
          }}
          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return T;if (i ==1) return T;return null;
          }
          }.apply$G(((T)
                      t)).
            x;
        
//#line 4
final int y =
          new x10.core.fun.Fun_0_1<T, T>() {public final T apply$G(final T __desugarer__var__1__) { {
              
//#line 4
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__1__,null))/* } */ &&
                               !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__1__)),x10.
                                   lang.
                                   Runtime.here())/* } */)) {
                  
//#line 4
throw new java.lang.ClassCastException("T{self.home==here}");
              }
              
//#line 4
return __desugarer__var__1__;
          }}
          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return T;if (i ==1) return T;return null;
          }
          }.apply$G(((T)
                      t)).
            y;
        
//#line 5
final int z =
          new x10.core.fun.Fun_0_1<T, T>() {public final T apply$G(final T __desugarer__var__2__) { {
              
//#line 5
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__2__,null))/* } */ &&
                               !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__2__)),x10.
                                   lang.
                                   Runtime.here())/* } */)) {
                  
//#line 5
throw new java.lang.ClassCastException("T{self.home==here}");
              }
              
//#line 5
return __desugarer__var__2__;
          }}
          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return T;if (i ==1) return T;return null;
          }
          }.apply$G(((T)
                      t)).
            z;
        
//#line 6
this.doStuff2(t);
    }
    
    
//#line 8
private void
                 doStuff2(
                 final Coord c){
        
    }
    
    public GenericClass(final x10.rtt.Type T) {
                                                       super();
                                                   this.T = T;
                                                    {
                                                       
                                                   }}

}
