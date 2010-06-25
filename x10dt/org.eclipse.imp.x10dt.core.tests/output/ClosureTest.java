
abstract class ClosureTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClosureTest>_RTT = new x10.rtt.RuntimeType<ClosureTest>(
/* base class */ClosureTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
boolean
      result;
    
    
//#line 25
void
                  check(
                  final java.lang.String test,
                  final int actual,
                  final int expected){
        
//#line 26
boolean result =
          ((int) actual) ==
        ((int) expected);
        
//#line 28
if ((!(((boolean)(result))))) {
            
//#line 29
this.pr(((((((((test) + (" fails: expected "))) + (expected))) + (", got "))) + (actual)));
            
//#line 30
this.result = false;
        } else {
            
//#line 32
this.pr(((((test) + (" succeeds: got "))) + (actual)));
        }
    }
    
    
//#line 35
void
                  check(
                  final java.lang.String test,
                  final java.lang.String actual,
                  final java.lang.String expected){
        
//#line 36
boolean result =
          (actual).equals(expected);
        
//#line 38
if ((!(((boolean)(result))))) {
            
//#line 39
this.pr(((((((((test) + (" fails: expected "))) + (expected))) + (", got "))) + (actual)));
            
//#line 40
this.result = false;
        } else {
            
//#line 42
this.pr(((((test) + (" succeeds: got "))) + (actual)));
        }
    }
    
    
//#line 45
void
                  check(
                  final java.lang.String test,
                  final java.lang.Object actual,
                  final java.lang.Object expected){
        
//#line 46
boolean result =
          ((Object)actual).equals(expected);
        
//#line 48
if ((!(((boolean)(result))))) {
            
//#line 49
this.pr(((((((((test) + (" fails: expected "))) + (expected))) + (", got "))) + (actual)));
            
//#line 50
this.result = false;
        } else {
            
//#line 52
this.pr(((((test) + (" succeeds: got "))) + (actual)));
        }
    }
    
    
//#line 55
void
                  pr(
                  final java.lang.String s){
        
//#line 56
x10.
          io.
          Console.OUT.println(s);
    }
    
    public ClosureTest() {
        super();
        
//#line 23
this.result = true;
    }

}
