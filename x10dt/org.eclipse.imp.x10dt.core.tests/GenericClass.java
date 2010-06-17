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
if (