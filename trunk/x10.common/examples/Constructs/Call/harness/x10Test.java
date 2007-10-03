package harness;

import java.util.Random;
import x10.lang.*;

abstract public class x10Test
extends x10.
  lang.
  Object
{
    
    
//#line 22
abstract protected boolean
                  run(
                  );
    
    
//#line 24
public void
                  executeAsync(
                  ) {
        
//#line 25
final boolean[] b =
          new boolean[] { false };
        
//#line 26
java.
          lang.
          Thread timer =
          startTimeoutTimer();
        
//#line 27
try {
            
//#line 28
/* template:finish { */
            {
            	x10.lang.Runtime.getCurrentActivity().startFinish();
            	try {
            		{
                
//#line 28
/* template:Async { */(x10.lang.Runtime.asPlace(harness.
                                                                              x10Test.this.
                                                                              location)).runAsync
                	(new x10.runtime.Activity() {
                		public void runX10Task() {
                			{
                    
//#line 28
b[0] =
                      harness.
                        x10Test.this.
                        run();
                }
                		}
                	});/* } */
            }
            	} catch (Throwable tmp0) {
            		x10.lang.Runtime.getCurrentActivity().pushException(tmp0);
            	} finally {
            		x10.lang.Runtime.getCurrentActivity().stopFinish();
            	}
            }
            /* } */
            
        }
        
//#line 29
catch (java.
                             lang.
                             Throwable e) {
            
//#line 30
/* template:place-check { */((java.lang.Throwable) x10.lang.Runtime.hereCheck(e))/* } */.printStackTrace();
        }
        
//#line 32
/* template:place-check { */((java.lang.Thread) x10.lang.Runtime.hereCheck(timer))/* } */.interrupt();
        
//#line 33
reportResult(
                      b[0]);
    }
    
    
//#line 36
public void
                  execute(
                  ) {
        
//#line 37
boolean b =
          false;
        
//#line 38
java.
          lang.
          Thread timer =
          startTimeoutTimer();
        
//#line 39
try {
            
//#line 40
/* template:finish { */
            {
            	x10.lang.Runtime.getCurrentActivity().startFinish();
            	try {
            		{
                
//#line 40
b =
                  this.
                    run();
            }
            	} catch (Throwable tmp1) {
            		x10.lang.Runtime.getCurrentActivity().pushException(tmp1);
            	} finally {
            		x10.lang.Runtime.getCurrentActivity().stopFinish();
            	}
            }
            /* } */
            
        }
        
//#line 41
catch (java.
                             lang.
                             Throwable e) {
            
//#line 42
/* template:place-check { */((java.lang.Throwable) x10.lang.Runtime.hereCheck(e))/* } */.printStackTrace();
        }
        
//#line 44
/* template:place-check { */((java.lang.Thread) x10.lang.Runtime.hereCheck(timer))/* } */.interrupt();
        
//#line 45
reportResult(
                      b);
    }
    
    
//#line 48
final public static String
      PREFIX =
      "++++++ ";
    
    
//#line 49
public static void
                  success(
                  ) {
        
//#line 50
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println(PREFIX +
                                                                                                                         "Test succeeded.");
        
//#line 51
x10.
                      lang.
                      Runtime.
                      setExitCode(
                      0);
    }
    
    
//#line 53
public static void
                  failure(
                  ) {
        
//#line 54
/* template:place-check { */((java.io.PrintStream) x10.lang.Runtime.hereCheck(java.
                                                                                                    lang.
                                                                                                    System.
                                                                                                    out))/* } */.println(PREFIX +
                                                                                                                         "Test failed.");
        
//#line 55
x10.
                      lang.
                      Runtime.
                      setExitCode(
                      1);
    }
    
    
//#line 57
protected static void
                  reportResult(
                  boolean b) {
        
//#line 58
if (b) {
            
//#line 58
success();
        } else {
            
//#line 58
failure();
        }
    }
    
    
//#line 64
public static void
                  chk(
                  boolean b) {
        
//#line 65
if (!b) {
            
//#line 65
throw new java.
              lang.
              Error(
              );
        }
    }
    
    
//#line 72
public static void
                  chk(
                  boolean b,
                  java.
                    lang.
                    String s) {
        
//#line 73
if (!b) {
            
//#line 73
throw new java.
              lang.
              Error(
              s);
        }
    }
    
    
//#line 76
private java.
      util.
      Random
      myRand =
      new java.
      util.
      Random(
      1L);
    
    
//#line 81
protected int
                  ranInt(
                  int lb,
                  int ub) {
        
//#line 82
return lb +
          /* template:place-check { */((java.util.Random) x10.lang.Runtime.hereCheck(myRand))/* } */.nextInt(ub -
                                                                                                               lb +
                                                                                                               1);
    }
    
    
//#line 90
private static java.
                  lang.
                  Thread
                  startTimeoutTimer(
                  ) {
        
//#line 91
final int seconds =
          /* template:place-check { */((java.lang.Integer) x10.lang.Runtime.hereCheck(java.
                                                                                        lang.
                                                                                        Integer.
                                                                                        getInteger(
                                                                                        "x10test.timeout",
                                                                                        300)))/* } */.intValue();
        
//#line 93
java.
          lang.
          Thread timer =
          new java.
          lang.
          Thread(
          new java.
            lang.
            Runnable(
            ) {
              
              
//#line 94
public void
                            run(
                            ) {
                  
//#line 95
if (x10.
                                    lang.
                                    Runtime.
                                    sleep(
                                    seconds *
                                      1000)) {
                      
//#line 96
x10.
                                    lang.
                                    Runtime.
                                    exit(
                                    128);
                  }
              }
          });
        
//#line 100
/* template:place-check { */((java.lang.Thread) x10.lang.Runtime.hereCheck(timer))/* } */.start();
        
//#line 101
return timer;
    }
    
    
//#line 16
public x10Test() {
        
//#line 16
super();
    }
    
    final public static java.
      lang.
      String
      jlc$CompilerVersion$x10 =
      "1.0.0";
    final public static long
      jlc$SourceLastModified$x10 =
      1166074885000L;
    final public static java.
      lang.
      String
      jlc$ClassType$x10 =
      ("H4sIAAAAAAAAAL0aC4xcVfXOzP4/3Z39tNvSz7ZsoaV2t43aFIvK7jJtF6bd" +
       "ZXfbwhIc3ry5s/vY\nN++9vndnOlu0Yoy0QlCQQsAAiqmpmmqMNUgUlaSgIs" +
       "FojRA/NAIiEqSGGAMhpXjOve8782Z21com\n895959577vmfc+/dE2+QWssk" +
       "GwxdnZtWddZPi6y/uHlTP5szqNV/3eZNY5Jp0cywKlnWJMBSMrnr\n2jtWH0" +
       "rfGiUrp0g8q1A1Y41oClMkVTlIM1OkUbGuUiw2QdkUaVWsUY2OqZJM+XedYo" +
       "1LisoHYYMD\neVubdQdQ2QZCwxkwQdUsbzcpFlA1aJrSHEfvfPDOZsWaoqY+" +
       "JFlIyCLfF+/uMkzdoCZTqJVQpbRu\nSgzHtZpUUoHDvIVEJkmzpGk6k5iiax" +
       "Yj7cmbpYI0kGeKOpAEvrYlSbuM4hj0Ru0nh0gkSRZx+IhW\nkExF0hgjlyUd" +
       "wQ6AYAdAsANcsAMyojYHhvlLUjTEGs/QrKLRzJhLpI22MUMNQR4CoklSkwE6" +
       "GFk7\nH/bUXskExG2aLqbvdejqrTQTpIlahkkNuq03XDOWJE1GKVk1JijN6f" +
       "VEaNMY9yAoEFXJMLLGW1Ys\nN0FzQI8iJ4oyNVCSuLCp6wyJQEQ1sIwFqreX" +
       "WYTTwCKlHGXUFIQUTdLrmq8w2zKbveWHTR/svuHJ\ngzHSNkXaFG0C9SaD9B" +
       "mwP0VacjSXBnyDmQzaQ1yjaDAmt2iu4CnSYSnTmsTyJgWztHS1gAM7rDyK\n" +
       "BNd0gEnSIqSfl5luOqKqE25if9VmVWkaLGtxqTi2IxxE0AQGQc0sCN+V9ayi" +
       "gQBXlc5weey7BgbA\n1HqQzIzuLlWjoahIh7BhVdKmByaYqWjTMLRWz8MqjC" +
       "yriBS1YUjyrDRNU4z0lI4bE10wqpELAqcw\n0l06jGMCLS0r0ZJPP6N1Le/e" +
       "PvZWb5TTnKGyivQ3waSVJZPGaZaaVJOpmPh2vv/oyPX55VFCYHB3\nyWAxZn" +
       "DtD/Yk//bTVWLMRSFjRtM3Q6hJyee2LF9xevAvjTEko8HQLQWVH+Cch4Exu2" +
       "db0YDg2Yvx\n0kCTM92AafZdPbbDGfbay3+6uOGpMwcFAYtdAhBXvzPoifGf" +
       "XX/rt+jrUdIwQupkXc3ntBHSSLXM\nsN2uh3YSAoSAjmazFmUjpEbloDqdf4" +
       "P0sorKXacR2obEZni7aBBCIvZvmpDG9fBuh9+rjAwN7AGC\nrYHCzQMHdHPW" +
       "Ao1TDAn9sp7L6RpECClnqKBIgKlKemBGMjVq8c9JavF0wYAzXKfjQCQCElle" +
       "6o8Y\nR3bqaoaaKfn4y7/8ZOKazx+JuvZpU8hIm42630ZNIhGOb0lQZKiyDI" +
       "biv39vW/sXNlqPRkkMk0Qu\nl2dSWqXgg5Kq6gdoJsW4ScZ95s+tDky2JQ3W" +
       "C46QUgGRCCQGKZhkTamVet48Ai0JTO/0ofd+fTZ1\n4CTqEw2gC7EL0kA/s4" +
       "K2lvUTN15905E1MRx0oAZFD0MHKifakIVS8tKnN7zyJn2rKYrRz5+XbP+G\n" +
       "uAsxSfOFS3RFXyAGg+ubn6OUfPb2XSefe+aFdZ4HMtJXFhjKZ6Jjl4oMEphM" +
       "MxAqPfTHlrbF9pG9\nd0c5iRAhGSQ9DD4rS9cIOPg2J1giL/Uggaxu5iQVu1" +
       "wJsBlTP+BBuB2246NbmCQqqIRAHmff/mzd\npucfb34q6g/Jbb5kD7WAcPC4" +
       "p99Jk2KN8ML9Y/fc+8bhG7hybe0yUmfk06oiFzkhF0XAmDpDgk1/\nT9fR+9" +
       "Y/+LxjPZ0edl7IoPEUP3N6xQM/lx6CQAQebkFdxR2Y8JWIswA+N/H2Zl8nfl" +
       "9aBuUA4hML\neio2ekLdTgxdIRZCtyh16e2Y0Bz95tK3/OvUw029giWc08uX" +
       "jQJ76ytbfABHSt7yh1MHD7fqO8BG\noMyDmgel5dR8dlEENV+39+ErO0O8Iy" +
       "R7lKx48Cd7Hn77WXaGm4BnlTj7kmI5z1A+eXO3PleI1333\nK7koqZ+CglDn" +
       "HWyvpOZdmodtYJK0BvqD+Vgkn22u1y0v9QjfsqX+4IVPaDM7CLQIF+BjOkEL" +
       "tfBD\nU9hESOzr9nsOOxfjY0kxAqZbv4H/9SJkCLS2qorWcP2HXv/Vmz/ePP" +
       "mNqG1w6/hqG4QrxBhQqWgS\nT+IbwS8sXm4VoTU2ntg+cp1jeO3c9FEK/aIq" +
       "4ZYJxhduvh8NsHQZ/JKE1GXs98f9LBEDG7v5hISf\nNuTVMJWChJUfqcvNwc" +
       "4jE6SHuyKC9VxFeorlmKOMNEhpLOdlnk822m5KPDftdbgxyYpKxRAv5A5f\n" +
       "92bLbdKTN4qKoSOY/hJaPvfq3Cl66RV3vhiSRBuZbmxUaYGqnveWrraLF4mO" +
       "VbXtW/Xi9i3HP1Xq\nvk1A52WVDSGIJCVPPdDa8+LQb/4ZxfI/JFfVi+DtOu" +
       "eqqkSl5HjhomtjM8oveO6znaOsAA5O2hZ0\niZLsKBxjbcCK1sJvMZjFUfvN" +
       "fFYk0ke5orF9PQgadkQMXJdmqgfkeaP1NCMxM69VD5ZjppKDaFew\nC99H93" +
       "z57is3qTmeUEOEXbZnLN1WVdhKlUbMkmXvXnnsrydfHu+K+jYlF5ftC/xzxM" +
       "aE89lmoOWv\nrrYCH/3khtUnDo2fSTvB5Ubw2bSuq1TSyuQn7HW/wQcWDO72" +
       "c4ZRYVwmoHv8rQF9PgTvHtD9/fPr\nPuL37P9W37cw0kKLVIbt16A1p8kOC/" +
       "ydZ6SmoCuZ6px+TnB65D/gtB8Ccx7efcDxI+8Tp7eD5mxO\nA9SXEy24WTAz" +
       "HyKkZrnIa7FvLtBlN+Jj5H/k6D7gyMrLsDO3LixHW4GjL8L7w/Be9X5y9DXg" +
       "KCsp\nKh5tXFCOtgEnr8L7cnh/aYEczVbhKGLvovwOH07knDE/298BJzSpoZ" +
       "tsnFp5lV1Y3ofg9Wl4Xwnv\nrfPzHuPaBM+3pCx930TwOOQdeWb2wnJ+NZRi" +
       "oHmyEz5fWyjn+HiiCt9RezuxEL4rFpfzC+SZ/4dA\nJkAg5+B9Lby/Pb9AOH" +
       "mzCxKDky9iisaqp4uXRLp4xQhCw3l7ZQGm80eonU1JG9HYghAuWFhZQhov\n" +
       "hfcUIfXHFxgzxi9EFDzLSBxqSJNNKjmq5/nLDDGlyRmTSiHJGTYT9fb2GTf1" +
       "PWUn0uIUVV5z+qZ1\np4z407xuc086m6E+y+ZV1b+d87XrDJNmFU5ns9jcCX" +
       "mfg0XtvTt+vmsYRRL4ixjwV6WKF0fV/d41\nREo+tjzxyG1X/GhLlDTCJhYH" +
       "KBajGhshDXR/YVjPa3jPYuCJ3oi1k5p0isT515huWUpandsNfEyR\nWn7kD3" +
       "Uo75ukZo6RdfPeV+C4lIy7XKxWLUY6fecxOyVrZpdk4CE3XgnAthiZxssHxD" +
       "IxB2TCIpfM\nc7Uhxm0rGhFPRCChSypX3t4sUJ/y23XyVXfNRMnHgMjtg8mJ" +
       "xELuYZIKbvDrdyd2pEZ3J4DsCGp/\n955k0m7HPHDt5M7xhDtmcnyP045N7h" +
       "u1m51AVmpk92RifPvgcCK1PTm4Y4KR1VVYd64WunFmcnR4\nMJkaTg5OTIi5" +
       "iLY2SRZj567ErqHEeEkvHja0i97JnaNXBeBLET45OpZKJvYmwtDWTCXGHcpb" +
       "YXes\nFBQ2x/cTiKEpSbokPPgaNagp9jD+vibe54d0SFg0D+sZ73yRkRXlZ5" +
       "VeP/Ddwyc59z6+qUhXXZLE\n7Q3GoLsaIxuqyLPkiglWWGZjcA8wB/2ER0BQ" +
       "i+0R/BBosJStRek5Vj5nCUIro+zE7gr4ZAgN5fgQ\nWgUfdofja5JVXZ71Q+" +
       "J4DWkq6Twqzd/RntHzabUc+1IBr7x8txhQQkAVD8Nzc58OaiiEHUb653FI\n" +
       "HD0J6ctSJabzG9KsqkusjJweDq5MbhfvDxdXZzaPt4WDIeYen6YaNRW5DN8y" +
       "u6PyivEZ6PDCNcSV\nhYRVDNM8rC6C3TUt4h2Jn55WwFRurACsTEeHolXge6" +
       "mqa9McOqbDoAOKRUcN/9RF7oCAXSK0il3q\nPDjo5pCiSWZgwcVOl7teIFg4" +
       "vXtK57UZzuHH5oCZi4QVgCBiP6TGpOKMDw+6TDpdYv4tJiRJqB8C\nq1kzsN" +
       "UotzAOrmJhvL+CQxYQHFBk0f5niEAcAKA4Y/ag/OJh7TxFAWatlNyU2NRwzz" +
       "UPnOAnPrGC\nONS9slg5Y7rTRTLff1Tb9ZHHLr5TnBh5Z9SiWndvV3hxNSTi" +
       "47O95q2/e/CdszBFFBJ5vP4QtWuk\nhVeUwXlQhtJpana89NVjb33m8NYo3p" +
       "rY80x/8bY7j9f8t524d0Xz0T/fwY87eQXgIZ7jzTYBCtaa\n9djGYz/3ct4t" +
       "PvmutWwTE5hdy2fjYyeusJQPZqQBA6jMskbIlMaQKaHLYe/yEAStHgJGaiF6" +
       "Y2ys\n0yQ8bGOkWdO1NIZ0+9y9FDUjTRbdn4fSTxFKF+vAthkz6Yypa3jvEr" +
       "JsfQjdQZKBWl6Y4rc4dOh6\nT/ydxx+WZfiBb0Y6hvWcoajU7N2B0RH/ZceW" +
       "FlbmaHdct+PC53hH4DbiQhxkRdYB232ZfC43x0/s\n+nDMEXer2e1S4a8iDL" +
       "e/w+t3cnxYp5uwDa+31e3FZOtAuzyol2l5HfuB6l4ZzHy/1/9x8vz5+5uj\n" +
       "4iLVrXOR4wj30Eizs2Snu6SXKw3DMMIYGXGyjNMVd7ucVBM6z80MAbyL3H4e" +
       "mkPAGJ/d4W0uXERm\n5GJDORdePA6jZK8TWkPQimDqkXjet5PgIuOhraNSL9" +
       "+5XY6PfaE70U/g4yZ8vMMRnatEQTES3OuV\nW7VJVnk3WsO6qsIsrK/7EjmD" +
       "8Yvmg48t+f4Vxx8+4139lNFbvtflZG3hvTfMI4J/A5vopOtgKAAA\n");
}
