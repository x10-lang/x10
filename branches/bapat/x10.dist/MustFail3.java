
class MustFail3
extends x10.core.Ref
{
public static class /* Join: { */RTT/* } */ extends x10.types.RuntimeType<MustFail3> {
public static final /* Join: { */RTT/* } */ it = new /* Join: { */RTT/* } */();
    
    
    public RTT() {super(MustFail3.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof MustFail3)) return false;
        return true;
    }
    public java.util.List<x10.types.Type<?>> getTypeParameters() {
    return null;
    }
}

    
    
//#line 5
void
                 intTest(
                 final x10.
                   lang.
                   Array<java.lang.Integer> a){
        
//#line 6
(a).set(42,
                           42);
    }
    
    
//#line 3
public MustFail3() {
        
//#line 3
super();
    }
}
