
abstract public class TestDist
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<TestDist>_RTT = new x10.rtt.RuntimeType<TestDist>(
/* base class */TestDist.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
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
if (