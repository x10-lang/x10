package stream;

import x10.lang.*;

public class BuggyFragmentedStreamMethods
extends x10.
  lang.
  Object
{
    
//#line 19
final public static int
      MEG =
      1024 *
      1024;
    
//#line 20
final public static double
      alpha =
      3.0;
    
//#line 21
final public static int
      NUM_TIMES =
      10;
    
    
//#line 22
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
        
//#line 23
final boolean[] verified =
          new boolean[] { true };
        
//#line 24
final double[] times =
          new double[NUM_TIMES];
        
//#line 25
long N0 =
          2 *
          MEG;
        
//#line 26
if (args.
                          length >
                          0) {
            
//#line 26
N0 =
              java.
                lang.
                Long.
                parseLong(
                args[0]);
        }
        
//#line 27
final long N =
          N0 *
          x10.
            lang.
            place.
            MAX_PLACES;
        
//#line 28
final int LocalSize =
          (int)
            N0;
        
//#line 29
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("LocalSize=" +
                                                                                                                         LocalSize);
        
//#line 30
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
        
//#line 31
/* template:finish { */
        {
        	x10.lang.Runtime.getCurrentActivity().startFinish();
        	try {
        		{
            
//#line 31
/* template:Async { */(x10.lang.Runtime.asPlace(/* template:here { */x10.lang.Runtime.here()/* } */)).runAsync
            	(new x10.runtime.Activity() {
            		public void runX10Task() {
            			{
                
//#line 32
final x10.
                  lang.
                  clock clk =
                  /* template:place-check { */((x10.lang.clock.factory) x10.lang.Runtime.hereCheck(x10.
                                                                                                     lang.
                                                                                                     clock.
                                                                                                     factory))/* } */.clock();
                
//#line 34
/* template:ateach { */
                {
                	x10.lang.dist __var0____distCopy = x10.
                                                      lang.
                                                      dist.
                                                      UNIQUE; // make copy to avoid recomputation
                	for (java.util.Iterator __var0____ = __var0____distCopy.iterator();
                		 __var0____.hasNext(); )
                	{
                		final  x10.
                  lang.
                  point __var0__ = (x10.
                  lang.
                  point) __var0____.next();
                		/* Join: { */final int p =
                  /* template:array_get { */((__var0__).get(0))/* } */;/* } */
                		((x10.runtime.Place) __var0____distCopy.get(__var0__)).runAsync
                			(new x10.runtime.Activity(/* template:clock { */x10.lang.Runtime.getCurrentActivity().checkClockUse((x10.runtime.Clock) clk)/* } */) {
                				public void runX10Task() {
                					{
                    
//#line 36
double[] a =
                      new double[LocalSize +
                                   1];
                    
//#line 37
double[] b =
                      new double[LocalSize];
                    
//#line 37
double[] c =
                      new double[LocalSize];
                    
//#line 39
stream.
                                  BuggyFragmentedStreamMethods.
                                  vectorInit(
                                  b,
                                  c,
                                  LocalSize);
                    
//#line 41
stream.
                                  BuggyFragmentedStreamMethods.
                                  loopAdd(
                                  a,
                                  b,
                                  c,
                                  times);
                    
//#line 43
stream.
                                  BuggyFragmentedStreamMethods.
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
        	} catch (Throwable tmp2) {
        		x10.lang.Runtime.getCurrentActivity().pushException(tmp2);
        	} finally {
        		x10.lang.Runtime.getCurrentActivity().stopFinish();
        	}
        }
        /* } */
        
        
//#line 46
double min =
          10000000L;
        
//#line 47
for (
//#line 47
int j =
                           0;
                         j <
                           NUM_TIMES;
                         
//#line 47
j++) {
            
//#line 47
if (times[j] <
                              min) {
                
//#line 47
min =
                  times[j];
            }
        }
        
//#line 48
stream.
                      BuggyFragmentedStreamMethods.
                      printStats(
                      N,
                      min,
                      verified[0]);
    }
    }
    
    // How to invoke?  Use the following general command:
    // java $(javaArgs) x10.lang.Runtime $(x10Args) ClassName $(x10AppArgs)
    /* } */
    
    
    
//#line 50
public static double
                  mySecond(
                  ) {
        
//#line 51
return (double)
                             (((double)
                                 ((java.
                                     lang.
                                     System.
                                     nanoTime() /
                                     1000)) *
                                 1.0E-6));
    }
    
    
//#line 54
public static void
                  vectorInit(
                  double[] b,
                  double[] c,
                  int LocalSize) {
        
//#line 55
assert (b.
                              length ==
                              c.
                                length);
        
//#line 56
final int p =
          (/* template:here { */x10.lang.Runtime.here()/* } */).
            id;
        
//#line 58
for (
//#line 58
int i =
                           0;
                         i <
                           b.
                             length;
                         
//#line 58
i++) {
            
//#line 59
b[i] =
              1.5 *
                ((p *
                    LocalSize +
                    i));
            
//#line 60
c[i] =
              2.5 *
                ((p *
                    LocalSize +
                    i));
        }
    }
    
    
//#line 63
public static void
                  loopAdd(
                  double[] a,
                  double[] b,
                  double[] c,
                  double[] times) {
        
//#line 64
final int p =
          (/* template:here { */x10.lang.Runtime.here()/* } */).
            id;
        
//#line 65
for (
//#line 65
int j =
                           0;
                         j <
                           NUM_TIMES;
                         
//#line 65
j++) {
            
//#line 66
if (p ==
                              0) {
                
//#line 66
times[j] =
                  -stream.
                     BuggyFragmentedStreamMethods.
                     mySecond();
            }
            
//#line 67
stream.
                          BuggyFragmentedStreamMethods.
                          vectorAdd(
                          a,
                          b,
                          c);
            
//#line 68
/* template:next { */
            x10.lang.Runtime.getCurrentActivity().doNext();
            /* } */
            
            
//#line 69
if (p ==
                              0) {
                
//#line 69
times[j] +=
                  stream.
                    BuggyFragmentedStreamMethods.
                    mySecond();
            }
        }
    }
    
    
//#line 72
public static void
                  vectorAdd(
                  double[] a,
                  double[] b,
                  double[] c) {
        
//#line 73
for (
//#line 73
int i =
                           0;
                         i <
                           a.
                             length;
                         
//#line 73
i++) {
            
//#line 74
a[i] =
              b[i] +
                alpha *
                c[i];
        }
    }
    
    
//#line 76
public static void
                  verify(
                  double[] a,
                  double[] b,
                  double[] c,
                  x10.
                    lang.
                    clock clk,
                  final boolean[] verified) {
        
//#line 77
for (
//#line 77
int i =
                           0;
                         i <
                           a.
                             length;
                         
//#line 77
i++) {
            
//#line 78
if (a[i] !=
                              b[i] +
                              alpha *
                              c[i]) {
                
//#line 79
/* template:Async { */(x10.lang.Runtime.asPlace(x10.
                                                                              lang.
                                                                              place.
                                                                              FIRST_PLACE)).runAsync
                	(new x10.runtime.Activity(/* template:clock { */x10.lang.Runtime.getCurrentActivity().checkClockUse((x10.runtime.Clock) clk)/* } */) {
                		public void runX10Task() {
                			{
                    
//#line 79
verified[0] =
                      false;
                }
                		}
                	});/* } */
                
//#line 80
return;
            }
        }
    }
    
    
//#line 85
public static void
                  printStats(
                  long N,
                  double time,
                  boolean verified) {
        
//#line 86
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Number of places=" +
                                                                                                                         x10.
                                                                                                                           lang.
                                                                                                                           place.
                                                                                                                           MAX_PLACES);
        
//#line 87
long size =
          (3 *
             8 *
             N /
             MEG);
        
//#line 88
double rate =
          ((3 *
              8 *
              N)) /
          ((1.0E9 *
              time));
        
//#line 89
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
        
//#line 90
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Min time: " +
                                                                                                                         time +
                                                                                                                         " rate=" +
                                                                                                                         rate +
                                                                                                                         " GB/s");
        
//#line 91
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println("Result is " +
                                                                                                                         ((verified
                                                                                                                             ? "verified."
                                                                                                                             : "NOT verified.")));
    }
    
    
//#line 18
public BuggyFragmentedStreamMethods() {
        
//#line 18
super();
    }
}
