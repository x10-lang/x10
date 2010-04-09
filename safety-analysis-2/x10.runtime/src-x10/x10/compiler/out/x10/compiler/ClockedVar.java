package x10.compiler;

public class ClockedVar<T>
extends x10.core.Ref
{public x10.rtt.Type<?> rtt_x10$compiler$ClockedVar_T() { return this.T; }

public static class RTT<T> extends x10.rtt.RuntimeType<x10.
  compiler.
  ClockedVar<T>> {
public final x10.rtt.Type T;
    
    public RTT(final x10.rtt.Type T) {super(x10.
                                            compiler.
                                            ClockedVar.class);
                                          this.T = T;
                                          }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof x10.
          compiler.
          ClockedVar)) return false;
        if (! this.T.equals(((x10.
          compiler.
          ClockedVar) o).rtt_x10$compiler$ClockedVar_T())) return false;
        return true;
    }
    public java.util.List<x10.rtt.Type<?>> getTypeParameters() {
    return java.util.Arrays.asList(new x10.rtt.Type<?>[] { 
        T
         });
    }
}

    private final x10.rtt.Type T;
    
    
//#line 15
T
      xRead;
    
//#line 16
T
      xWrite;
    
    
//#line 17
public ClockedVar(final x10.rtt.Type T) {
                                                                 
//#line 17
super();
                                                             this.T = T;
                                                              {
                                                                 
                                                             }}
    
    
//#line 18
public ClockedVar(final x10.rtt.Type T,
                                  final T x) {
                                                      
//#line 18
super();
                                                  this.T = T;
                                                   {
                                                      
//#line 18
this.xRead = ((T)(x));
                                                  }}
    
    
//#line 19
public T
                  get(
                  ){
        
//#line 19
return xRead;
    }
    
    
//#line 20
public void
                  set(
                  final T x){
        
//#line 20
this.xWrite = ((T)(x));
    }
    
    
//#line 21
public static <S> x10.core.Rail<x10.
                  compiler.
                  ClockedVar<S>>
                  makeClockedRail(
                  final x10.rtt.Type S,
                  final java.lang.Integer length){
        
//#line 22
return x10.core.RailFactory.<x10.
          compiler.
          ClockedVar<S>>makeVarRail(new x10.compiler.ClockedVar.RTT<S>(S), length, new x10.core.fun.Fun_0_1<java.lang.Integer, x10.
          compiler.
          ClockedVar<S>>() {public final x10.
          compiler.
          ClockedVar<S> apply(final java.lang.Integer id$2) { try {{
            
//#line 22
return new x10.
              compiler.
              ClockedVar<S>(S);
        }}catch (java.lang.RuntimeException ex) {throw ex;}catch (java.lang.Exception ex) {throw new x10.runtime.impl.java.WrappedRuntimeException(ex);}}
        public x10.rtt.Type<?> rtt_x10$lang$Fun_0_1_Z1() { return x10.rtt.Types.INT; }
        public x10.rtt.Type<?> rtt_x10$lang$Fun_0_1_U() { return new x10.compiler.ClockedVar.RTT<S>(S); }
        });
    }
}
