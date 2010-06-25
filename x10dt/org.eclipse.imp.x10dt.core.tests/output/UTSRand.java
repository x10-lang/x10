final class UTSRand
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<UTSRand>_RTT = new x10.rtt.RuntimeType<UTSRand>(
/* base class */UTSRand.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
    
//#line 24
final static long
                  next(
                  final long r,
                  final int i){
        
//#line 25
long seed =
          ((((long)(r))) + (((long)(((long)(((int)(i))))))));
        
//#line 26
seed = ((((long)((((((long)(seed))) ^ (((long)(25214903917L)))))))) & (((long)((((((long)((((((long)(1L))) << (((int)(48)))))))) - (((long)(((long)(((int)(1)))))))))))));
        
//#line 27
for (
//#line 27
int k =
                           0;
                         ((((int)(k))) < (((int)(11))));
                         
//#line 27
k += 1) {
            
//#line 28
seed = ((((long)((((((long)(((((long)(seed))) * (((long)(25214903917L))))))) + (((long)(11L)))))))) & (((long)((((((long)((((((long)(1L))) << (((int)(48)))))))) - (((long)(((long)(((int)(1)))))))))))));
        }
        
//#line 31
final int l0 =
          ((int)(long)(((long)(((long)(((long)((((((long)((((long)(long)(((long)(seed)))))))) >>> (((int)((((((int)(48))) - (((int)(32))))))))))))))))));
        
//#line 33
seed = ((((long)((((((long)(((((long)(seed))) * (((long)(25214903917L))))))) + (((long)(11L)))))))) & (((long)((((((long)((((((long)(1L))) << (((int)(48)))))))) - (((long)(((long)(((int)(1)))))))))))));
        
//#line 36
final int l1 =
          ((int)(long)(((long)(((long)(((long)((((((long)((((long)(long)(((long)(seed)))))))) >>> (((int)((((((int)(48))) - (((int)(32))))))))))))))))));
        
//#line 38
return ((((long)((((((long)((((long)(((int)(l0)))))))) << (((int)(32)))))))) + (((long)(((long)(((int)(l1))))))));
    }
    
    
//#line 41
final static double
      scale =
      ((((double)((((double)(long)(((long)(java.lang.Long.MAX_VALUE)))))))) - (((double)((((double)(long)(((long)(java.lang.Long.MIN_VALUE)))))))));
    
    
//#line 43
final static double
                  number(
                  final long r){
        
//#line 43
return ((((double)((((((double)(((double)(long)(((long)(r))))))) / (((double)(UTSRand.scale)))))))) - (((double)((((((double)(((double)(long)(((long)(java.lang.Long.MIN_VALUE))))))) / (((double)(UTSRand.scale)))))))));
    }
    
    public UTSRand() {
        super();
    }

}
