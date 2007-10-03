import harness.x10Test;
import x10.lang.*;

public class Block
extends harness.
  x10Test
{
    
    
//#line 14
public boolean
                  run(
                  ) {
        
//#line 15
x10.
          lang.
          region r =
          x10.
            lang.
            region.
            factory.
            region(
            x10.
              lang.
              region.
              factory.
              region(
              0,
              9));
        
//#line 16
x10.
          lang.
          dist d =
          x10.
            lang.
            dist.
            factory.
            block(
            r);
        
//#line 17
return true;
    }
    
    
//#line 20
/* template:Main { */
    public static class Main extends x10.runtime.Activity {
    	private final String[] form;
    	public Main(String[] args) {
    		super("Main Activity");
    		this.form = args;
    	}
    	public void runX10Task() {
    		main(form);
    	}
    }
    
    // the original app-main method
    public static void main(java.
      lang.
      String[] args) {
    	if (x10.lang.Runtime.runtime == null) {
    		System.err.println("Please use the 'x10' script to invoke X10 programs, or see the generated");
    		System.err.println("Java code for alternate invocation instructions.");
    		System.exit(128);
    	}
    {
        
//#line 21
/* template:place-check { */((Block) x10.lang.Runtime.hereCheck(new Block(
          )))/* } */.execute();
    }
    }
    
    // How to invoke?  Use the following general command:
    // java $(javaArgs) x10.lang.Runtime $(x10Args) ClassName $(x10AppArgs)
    /* } */
    
    
    
//#line 13
public Block() {
        
//#line 13
super();
    }
    
    final public static java.
      lang.
      String
      jlc$CompilerVersion$x10 =
      "1.0.0";
    final public static long
      jlc$SourceLastModified$x10 =
      1191362109000L;
    final public static java.
      lang.
      String
      jlc$ClassType$x10 =
      ("H4sIAAAAAAAAAJ1ZDWwcxRUe3/n//z8/xIkTHAik8SWqFLUNRb0458TJ+Qfb" +
       "MWBEj729OXvx3u6y\nO2efQ4lCkQiF0hIRKkIBETUSFUqlilQUlVatBPRXRW" +
       "2qFrUqCEp/RaGKWgTqD/S9mf27vd0zraXb\nnX1v5r0377158834/FukzjLJ" +
       "TkNXVxdUnQ3TEhsu7dk9zFYNag3fsGf3lGRaNDeiSpY1C7SMTB64\n7r6tx7" +
       "MnYmTzPOnKK1TNWWOawhRJVY7R3DxpUqwDisVmKJsnbYo1qdEpVZIp/65XrG" +
       "lJUXknbHAi\nb2tLbgcq20RoOB1mqJrn7WbFAquSpimtcvHOB2e2KNY8NfX9" +
       "koWGtPu+OLvXMHWDmkyhVkqVsrop\nMezXZlJJhRkWLTQyTVokTdOZxBRdsx" +
       "jpTN8qLUuJIlPURBrmtS9NOmV0R9LrdRs5TmrSpJ3Tx7Rl\nyVQkjTFyddpx" +
       "bAIcmwDHJrhjEzKKNhMj/CUpGkrtytG8otHclGukLbYpRw1hHhJiaVKbAzsY" +
       "2b6W\n9MycZILgDk0Xw+ccuwajRoI3McowqFG344Y642nSbATNqjUhaA7Xc6" +
       "FtY5dHQYeoSo6RbZ5aoW6G\nFsAeRU6VZGqgJ1GxqesMjUBBtaDGgtDbatpx" +
       "GGSkVKCMmsKQkkkG3fQVaVuRs7d/q/mjfTe9cCxO\nOuZJh6LNYNxk8D6D6c" +
       "+T1gItZEFeMpfDfOjSKCaMyTOaB3iedFvKgiaxokkhLS1dXcaO3VYRXYI6\n" +
       "HWKatArvF2Wmm46r6sUysb/q8qq0AJnVH3THKNLBBc2QENTMg/NdXy8pGjhw" +
       "S3CEO8ehI9ABhjaA\nZxZ1V1Wthq4i3SKHVUlbSMwwU9EWoGudXgQtjGyMFI" +
       "rRMCR5SVqgGUbWB/tNCRb0auKOwCGM9AW7\ncUkQpY2BKPniM1nf+p97p94d" +
       "jHGbc1RW0f5mGLQ5MGia5qlJNZmKge8Vh0+P3VjcFCMEOvcFOos+\nye3fPJ" +
       "r+83e3iD6XhfSZzN4KpSYj/3vvpoGLyd83xdGMRkO3FAx+2cx5GZiyOftKBh" +
       "TPQayXBqac\n6RZMc+jw1EGn21/e+O3ljS++ekwY0O8agLKGnU7fm/7+jSee" +
       "om/GSOMYqZd1tVjQxkgT1XIjdrsB\n2mkoEII6mc9blI2RWpWT6nX+Dd7LKy" +
       "pfOk3QNiS2yNslgxBSY/82wOsivNvg9xIjycRRMNhKLN+a\nWNHNJQsiTrEk" +
       "DMt6oaBrUCGkgqFiXjiJDU1JVRP7VV1ewt2CwcRQTfdKTQ04ZFNwOWIZOaSr" +
       "OWpm\n5Cff+PFnUkc+d0/MTU/bQEbquEBSU8OlrCv3E8Yph/X3r0/v6/zCLu" +
       "uZGInjzlAoFJmUVSksPLBJ\nX6G5DON52OXLeZ5qkKetWUhZyP6MCoJE9TDI" +
       "skm2BVPTW8Jj0JIg3y4e/+Bnb2dWLmAQMeq9KF2Y\nBkFZEra1XjVz8+Fb7t" +
       "kWx04rtehv6JqI3l1DFGXkDT/a+YdL9N3mGJY8/2ZkL2ootlCINF+NxPXn\n" +
       "q76QZUNrzygjv33v+IVf/eSVHd6yY2SoohpUjsTVHHQZ7FoyzUF99MSf29AR" +
       "v57MnYpxE6EsMtjp\nsOJsDuooW9X7nAqJc2kAD+R1syCpyHI9wBZNfcWj8O" +
       "zrxEefSEQMUMBAXlzfu6t+98vfbnkx5q/D\nHb4dHgCAWNVdXnxnTYrA4JWH" +
       "px586K2TN/Hg2tFlpN4oZlVFLnFDLquBZOoJqTDD63tPf+mqR192\nsqfHk8" +
       "7RCyZP6c6LA2d+ID0G1QeWtQVgiq9awjURRwE+d/P2Hh8Tv6+soHIC8bkF1y" +
       "c21vsWm+gw\nIMTjYggu31Hcu5yoFrK3v/P8482DYiI4ZtBRZpIt0YnOvf/Y" +
       "my9d+s6e2a/GbIt3cAE7bV9ie5c9\nTeJN0yd/IGoH4bvfyRsutd4tvXCzKL" +
       "Pd5eUjpRULf1p9nl55zf2vh5SeJqYbu1S6TFXPD0Ft43xn\ndRzRcf2W10f3" +
       "PnlH0BExsPPqaD+UC8nI82fa1r++/+d/jyFmClnrDSL5XaizpapRGblr+bLr" +
       "4ovK\nD3ntsJd0BWooH7TP74yK6tLKI7WdG9AD86uzf90QsZPwboffP5DZj4" +
       "919vLD5ycjoxuRxGtm+Dgj\ncbOogYevqnJgMZUC7KjLNkJ45ugjpz61Wy3w" +
       "IhTi4ApwHcSfEZgzCEwCak9tPvfHC29M98Z86O3y\nCgDlHyMQHJ9nh4HZvr" +
       "WaBt77hZ1bzx+ffjXrrKcRRhqyuq5SSavwn8jROYN3vMkw8PVpw4jod7gi\n" +
       "3v0Qw5fg3Qvv+9aOd4zHG8qjxaF2eGxr3C1ye3Q8eX0UTr1x3a53Hv7Nv3r+" +
       "91hWOSyBigmIbc7e\neiLCHQSvPquejg9dij3XP8Stqs1KQntrEPVXgvoyrM" +
       "4d2e66fQB+Q7a7n7LfX/G53SnjnXwjQZAz\nLIC9ERnTBXwscT2qgX/Y0g1j" +
       "7XV3CyO1Bdi7nRTi73kgLutKrnqmMaFmJcyqkrMLgS0lUvZXg/ZV\nqaTijD" +
       "XsnZ8BbWxKnb37muf2xkjTPGnGDrCnUg0gciO9bXlEL2p4QWAgFh2zDgHcgI" +
       "Me/wIEbilZ\ndXWiqKrzpI6fVSGXOG+WmgVGdqx50MZ+GRmRC2YcYIoeH6Y4" +
       "JFmL45KBpzM8y8Ip3Dk1o5SZVTAT\nlFyxxplc9ANwUuO5CDx0RfTK8UZl5G" +
       "3KL3bIBx5YjJFrwcjRZHom9WEuENIK3kw0TKQOZiYnUmj2\nCcjxiaPptN2O" +
       "e+S62UPTKbfP7PRRpx2fvX7SbvaAWZmxidnU9GhyJJUZTScPzjCytcrUnTNx" +
       "H45M\nT44k05mRdHJmRoxFsXemST8yx1Pj+1PTQS4AyE7BnT00eaCMvgHps5" +
       "NTmXRqLhUmtnY+Ne1Y3ibJ\nUHsVJhY9P56mSa+EZWASzr6iDvl5zZJbImxK" +
       "t2StavKInvMwMiMDlXjb48O81/NBzoWFbygK/Wya\ndNkF3ytIjOys4s/A3Q" +
       "ho2GhLcEF4sszwu8C9do85SS0GuDCt9uwqqxyzDqnRInuQHSFPXpTMSnlI\n" +
       "rSIP2eHymmWsMX5KF96fmUq2iEHzMzpzOqD5SukbBD1afZ/oEDCg+q7jj0Et" +
       "hbLDyPAaCxJ7z5qw\nu6sSHMfwai+v6hKrMGc9J0eb28v54e7qyRfxmisZku" +
       "5dCxTOb4pcIW+jzYjW2LUIDK9cQ135MGUV\nyzQvq+2AdmgJz/l+e9pAUmWy" +
       "AjHajm7ghs97g6prC5w6pUOnFcWik4Z/aLvboSwvkVolL3VeHHRz\nv6JJZp" +
       "nCfofl6isrFg73aHBch+Ggkz1laS42rDIKCvZTak0qMxsbtZh0IZD+rQCumV" +
       "IoE9JhLepm\nSIZxcpUM4/yIBbmM5LJAlvw4z6kDQBQ3cx6VH56jwKIDCnDX" +
       "ysjNqd2NDx45c54j8DhfXv5rUCEZ\nbzKidlBXnNjcbzutjX/i2cvvF4ieuc" +
       "cjgaTdGwMOxfaLevnTQfPELx/959swRACLIh7pbSh0TxBN\nxSFYAi190SgX" +
       "NwbIcYGa3b974ty7d578WAwvCGxxph8BThTxGvvu8w8NtJx+7T5+MuVAQegT" +
       "duLz\n8zapDLE3MNIoZXF9yrA2m1y0CmcKw1SWJQatJsPUGbiN5mx4X2tJee" +
       "6HXKlSYh22zwDIy0Pu8xvd\nR/DxGCjC8iuzvBEyqomPwscT7pAv4+NRrpNL" +
       "4BrxcTZEQJsnAHRD7cfQ12sSHp0YadF0LYsbAmDl\nMNGMNAOALwJwVITNQg" +
       "8jrbgPL5q6hv/lClHbEGJ3wOSzJcOB50f4s/cD8fc+/hDU4Qe+IVdH9IKh\n" +
       "qNQcPIi1Ff9TZXioH7OUh3xarFjOiL5O+X8P3F+DaQ/lioXCahJnPySgvGNF" +
       "n2uFH4MYLr/b4zsI\nIYzpbveGx21zubhVO9Rej+rt0xwFf6T6Gi7fN3+t/+" +
       "3C++8/3BITV4kuSkYtd4j1fNJR2eOq9HZa\nfngKmciYs0c5rC6X5WxUoePc" +
       "faVMbrvL54U9hIzV3e3e4dJFXUfy1ytn4VXzMEvmnMIcIlYUTM/E\nY75zCH" +
       "cZf3ZHcXk+fRwf14am2gF8jOLDZ1kHpIZGLQsjOkstVqopPyVWZrRJtnhXqy" +
       "O6qoLNiMyH\nUgWD8WvWY8+u+8Y1Tz7+qndxV2Fr5VGV0/fy52trTP+/65Mr" +
       "eFMfAAA=");
}
