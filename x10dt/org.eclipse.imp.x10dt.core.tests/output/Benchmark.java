
abstract class Benchmark
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Benchmark>_RTT = new x10.rtt.RuntimeType<Benchmark>(
/* base class */Benchmark.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 29
abstract double
                  once(
                  );
    
    
//#line 30
abstract double
                  expected(
                  );
    
    
//#line 31
abstract double
                  operations(
                  );
    
    
//#line 33
double
                  now(
                  ){
        
//#line 33
return ((((double)((((double)(long)(((long)(x10.
                                          lang.
                                          System.nanoTime())))))))) * (((double)(1.0E-9))));
    }
    
    
//#line 34
final x10.
      io.
      Printer
      out;
    
//#line 38
final static java.lang.String
      lg =
      "";
    
//#line 40
final static double
      WARMUP =
      30.0;
    
//#line 41
final static double
      TIMING =
      10.0;
    
    
//#line 43
Benchmark() {
        
//#line 43
super();
        
//#line 44
this.out = x10.
          io.
          Console.OUT;
    }
    
    
//#line 47
public boolean
                  run(
                  ){
        
//#line 50
out.println("functional check");
        
//#line 51
final double warmup =
          this.now();
        
//#line 52
final double result =
          this.once();
        
//#line 53
if (((double) result) !=
                        ((double) this.expected())) {
            
//#line 54
out.println((((((("got ") + (result))) + ("; expected "))) + (this.expected())));
            
//#line 55
return false;
        }
        
//#line 59
if (("java").equals("java")) {
            
//#line 60
out.println((((("warmup for >") + (Benchmark.WARMUP))) + ("s")));
            
//#line 61
while (((((double)(((((double)(this.now()))) - (((double)(warmup))))))) < (((double)(Benchmark.WARMUP)))))
                
//#line 62
this.once();
        }
        
//#line 66
out.println((((("timing for >") + (Benchmark.TIMING))) + ("s")));
        
//#line 67
double avg =
          0.0;
        
//#line 68
double min =
          java.lang.Double.POSITIVE_INFINITY;
        
//#line 69
int count =
          0;
        
//#line 70
while (((((double)(avg))) < (((double)(Benchmark.TIMING))))) {
            
//#line 71
final double start =
              this.now();
            
//#line 72
this.once();
            
//#line 73
final double t =
              ((((double)(this.now()))) - (((double)(start))));
            
//#line 74
if (((((double)(t))) < (((double)(min))))) {
                
//#line 75
min = t;
            }
            
//#line 76
avg += t;
            
//#line 77
count += 1;
        }
        
//#line 79
avg /= ((double)(((int)(count))));
        
//#line 82
final double ops =
          ((((double)(this.operations()))) / (((double)(avg))));
        
//#line 83
out.printf("time: %.3f; count: %d; min/time: %.2f\n",
                               (double)(avg),
                               (int)(count),
                               (double)(((((double)(min))) / (((double)(avg))))));
        
//#line 84
if (((((double)(ops))) < (((double)(1000000.0))))) {
            
//#line 84
out.printf("%.3g kop/s\n",
                                   (double)(((((double)(ops))) / (((double)(1000.0))))));
        } else {
            
//#line 85
if (((((double)(ops))) < (((double)(1.0E9))))) {
                
//#line 85
out.printf("%.3g Mop/s\n",
                                       (double)(((((double)(ops))) / (((double)(1000000.0))))));
            } else {
                
//#line 86
out.printf("%.3g Gop/s\n",
                                       (double)(((((double)(ops))) / (((double)(1.0E9))))));
            }
        }
        
//#line 87
out.printf("test=%s lg=x10-%s ops=%g\n",
                               x10.core.Ref.typeName(this),
                               "java",
                               (double)(ops));
        
//#line 90
return true;
    }

}
