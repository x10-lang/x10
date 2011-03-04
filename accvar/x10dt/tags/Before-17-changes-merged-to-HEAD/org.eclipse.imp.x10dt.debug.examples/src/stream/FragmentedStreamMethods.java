package stream;

import x10.lang.*;

public class FragmentedStreamMethods
extends x10.
  lang.
  Object
{
    
//#line 10
final public static int
      MEG =
      1024 *
      1024;
    
//#line 11
final public static double
      alpha =
      3.0;
    
//#line 12
final public static int
      NUM_TIMES =
      10;
    
    
//#line 13
/* template:Main { */
    public static class Main extends x10.runtime.Activity {
    	private final String[] form;
    	public Main(String[] args) {
    		super("Main Activity");
    		this.form = args;
    	}
    	public void runX10Task() throws Throwable {
    		main(form);
    	}
    }
    
    // the original app-main method
    public static  void main(/* Join: { */java.
      lang.
      String[] args/* } */)  {
    	if (x10.lang.Runtime.runtime == null) {
    		java.lang.System.err.println("Please use the 'x10' script to invoke X10 programs, or see the generated");
    		java.lang.System.err.println("Java code for alternate invocation instructions.");
    		java.lang.System.exit(128);
    	}
    {
        
//#line 14
final boolean[] verified =
          new boolean[] { true };
        
//#line 15
final double[] times =
          new double[NUM_TIMES];
        
//#line 16
long N0 =
          2 *
          MEG;
        
//#line 17
if (args.
                          length >
                          0) {
            
//#line 17
N0 =
              java.
                lang.
                Long.
                parseLong(
                args[0]);
        }
        
//#line 18
final long N =
          N0 *
          x10.
            lang.
            place.
            MAX_PLACES;
        
//#line 19
final int LocalSize =
          (int)
            N0;
        
//#line 20
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("LocalSize=" +
                                                                                                                         LocalSize);
        
//#line 21
final x10.
          lang.
          region RLocal =
          /* template:place-check { */((x10.lang.region.factory) x10.lang.Runtime.hereCheck(x10.
                                                                                              lang.
                                                                                              region.
                                                                                              factory))/* } */.region(/* template:place-check { */((x10.lang.region.factory) x10.lang.Runtime.hereCheck(x10.
                                                                                                                                                                                                          lang.
                                                                                                                                                                                                          region.
                                                                                                                                                                                                          factory))/* } */.region(0,
                                                                                                                                                                                                                                  LocalSize -
                                                                                                                                                                                                                                    1));
        
//#line 22
/* template:finish { */
        {
        	x10.lang.Runtime.getCurrentActivity().startFinish();
        	try {
        		{
            
//#line 22
/* template:Async { */(x10.lang.Runtime.asPlace(/* template:here { */x10.lang.Runtime.here()/* } */)).runAsync
            	(new x10.runtime.Activity("FragmentedStreamMethods.x10, Line 22") {
            		public void runX10Task() {
            			{
                
//#line 23
final x10.
                  lang.
                  clock clk =
                  /* template:place-check { */((x10.lang.clock.factory) x10.lang.Runtime.hereCheck(x10.
                                                                                                     lang.
                                                                                                     clock.
                                                                                                     factory))/* } */.clock();
                
//#line 25
/* template:ateach { */
                {
                	x10.lang.dist __var400____distCopy = x10.
                                                        lang.
                                                        dist.
                                                        UNIQUE; // make copy to avoid recomputation
                	for (java.util.Iterator __var400____ = __var400____distCopy.iterator();
                		 __var400____.hasNext(); )
                	{
                		final  x10.
                  lang.
                  point __var400__ = (x10.
                  lang.
                  point) __var400____.next();
                		/* Join: { */final int p =
                  /* template:array_get { */((__var400__).get(0))/* } */;/* } */
                		((x10.runtime.Place) __var400____distCopy.get(__var400__)).runAsync
                			(new x10.runtime.Activity(/* template:clock { */x10.lang.Runtime.getCurrentActivity().checkClockUse((x10.runtime.Clock) clk)/* } */) {
                				public void runX10Task() {
                					{
                    
//#line 26
double[] a =
                      new double[LocalSize];
                    
//#line 26
double[] b =
                      new double[LocalSize];
                    
//#line 26
double[] c =
                      new double[LocalSize];
                    
//#line 28
stream.
                                  FragmentedStreamMethods.
                                  vectorInit(
                                  b,
                                  c,
                                  LocalSize);
                    
//#line 30
stream.
                                  FragmentedStreamMethods.
                                  loopAdd(
                                  a,
                                  b,
                                  c,
                                  times);
                    
//#line 32
stream.
                                  FragmentedStreamMethods.
                                  verify(
                                  a,
                                  b,
                                  c,
                                  clk,
                                  verified);
                }
                				}
                			});
                	}
                }
                /* } */
                
            }
            		}
            	});/* } */
        }
        	} catch (Throwable tmp401) {
        		x10.lang.Runtime.getCurrentActivity().pushException(tmp401);
        	} finally {
        		x10.lang.Runtime.getCurrentActivity().stopFinish();
        	}
        }
        /* } */
        
        
//#line 37
double min =
          10000000L;
        
//#line 38
for (
//#line 38
int j =
                           0;
                         j <
                           NUM_TIMES;
                         
//#line 38
j++) {
            
//#line 38
if (times[j] <
                              min) {
                
//#line 38
min =
                  times[j];
            }
        }
        
//#line 39
stream.
                      FragmentedStreamMethods.
                      printStats(
                      N,
                      min,
                      verified[0]);
    }
    }
    
    // How to invoke?  Use the following general command:
    // java $(javaArgs) x10.lang.Runtime $(x10Args) ClassName $(x10AppArgs)
    /* } */
    
    
    
//#line 41
public static double
                  mySecond(
                  ) {
        
//#line 42
return (double)
                             (((double)
                                 ((java.
                                     lang.
                                     System.
                                     nanoTime() /
                                     1000)) *
                                 1.0E-6));
    }
    
    
//#line 45
public static void
                  vectorInit(
                  double[] b,
                  double[] c,
                  int LocalSize) {
        
//#line 46
assert (b.
                              length ==
                              c.
                                length);
        
//#line 47
final int p =
          (/* template:here { */x10.lang.Runtime.here()/* } */).
            id;
        
//#line 48
for (
//#line 48
int i =
                           0;
                         i <
                           b.
                             length;
                         
//#line 48
i++) {
            
//#line 49
b[i] =
              1.5 *
                ((p *
                    LocalSize +
                    i));
            
//#line 50
c[i] =
              2.5 *
                ((p *
                    LocalSize +
                    i));
        }
    }
    
    
//#line 53
public static void
                  loopAdd(
                  double[] a,
                  double[] b,
                  double[] c,
                  double[] times) {
        
//#line 54
final int p =
          (/* template:here { */x10.lang.Runtime.here()/* } */).
            id;
        
//#line 55
for (
//#line 55
int j =
                           0;
                         j <
                           NUM_TIMES;
                         
//#line 55
j++) {
            
//#line 56
if (p ==
                              0) {
                
//#line 56
times[j] =
                  -stream.
                     FragmentedStreamMethods.
                     mySecond();
            }
            
//#line 57
stream.
                          FragmentedStreamMethods.
                          vectorAdd(
                          a,
                          b,
                          c);
            
//#line 58
/* template:next { */
            x10.lang.Runtime.getCurrentActivity().doNext();
            /* } */
            
            
//#line 59
if (p ==
                              0) {
                
//#line 59
times[j] +=
                  stream.
                    FragmentedStreamMethods.
                    mySecond();
            }
        }
    }
    
    
//#line 62
public static void
                  vectorAdd(
                  double[] a,
                  double[] b,
                  double[] c) {
        
//#line 63
for (
//#line 63
int i =
                           0;
                         i <
                           a.
                             length;
                         
//#line 63
i++) {
            
//#line 64
a[i] =
              b[i] +
                alpha *
                c[i];
        }
    }
    
    
//#line 66
public static void
                  verify(
                  double[] a,
                  double[] b,
                  double[] c,
                  x10.
                    lang.
                    clock clk,
                  final boolean[] verified) {
        
//#line 67
for (
//#line 67
int i =
                           0;
                         i <
                           a.
                             length;
                         
//#line 67
i++) {
            
//#line 68
if (a[i] !=
                              b[i] +
                              alpha *
                              c[i]) {
                
//#line 69
/* template:Async { */(x10.lang.Runtime.asPlace(x10.
                                                                              lang.
                                                                              place.
                                                                              FIRST_PLACE)).runAsync
                	(new x10.runtime.Activity("FragmentedStreamMethods.x10, Line 69") {
                		public void runX10Task() {
                			{
                    
//#line 69
verified[0] =
                      false;
                }
                		}
                	});/* } */
            }
        }
    }
    
    
//#line 73
public static void
                  printStats(
                  long N,
                  double time,
                  boolean verified) {
        
//#line 74
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Number of places=" +
                                                                                                                         x10.
                                                                                                                           lang.
                                                                                                                           place.
                                                                                                                           MAX_PLACES);
        
//#line 75
long size =
          (3 *
             8 *
             N /
             MEG);
        
//#line 76
double rate =
          ((3 *
              8 *
              N)) /
          ((1.0E9 *
              time));
        
//#line 77
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Size of arrays: " +
                                                                                                                         size +
                                                                                                                         " MB (total)" +
                                                                                                                         size /
                                                                                                                           x10.
                                                                                                                             lang.
                                                                                                                             place.
                                                                                                                             MAX_PLACES +
                                                                                                                         " MB (per place)");
        
//#line 78
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Min time: " +
                                                                                                                         time +
                                                                                                                         " rate=" +
                                                                                                                         rate +
                                                                                                                         " GB/s");
        
//#line 79
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Result is " +
                                                                                                                         ((verified
                                                                                                                             ? "verified."
                                                                                                                             : "NOT verified.")));
    }
    
    
//#line 9
public FragmentedStreamMethods() {
        
//#line 9
super();
    }
}
