/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array.sharedmemory;

import x10.array.Operator;
import junit.framework.TestCase;

/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestIntArray extends TestCase {
    
    public void testIntArray_reduce() {
        Place_c[] places = {new Place_c(), new Place_c()}; 
        int[] dims = {3,3};
        Region_c r = new Region_c(dims);
        Distribution_c d = Distribution_c.makeConstant(r, places[0]);
        IntArray_c ia = new IntArray_c(d, 12);
        Operator.Reduction red = new Operator.Reduction() {
           private int acc_;
           public void apply(int i) {
               acc_ += i;
           }
           public int getIntResult() {
               return acc_;
           }
        };
        ia.reduction(red);
        int result = red.getIntResult();
        System.out.println("Result is " + result + "; should be " + 192);
        assertTrue(result == 192);
    }
   
    private static String fill_(int width) {
        char[] buf = new char[width];
        java.util.Arrays.fill(buf, ' ');
        return new String(buf);
    }

    /** pretty-print an array into a string as <code>#(...)</code> */
    private static String print_(Object obj, int offset) {
        offset++;
        String ret = "(";
        for (int ii = 0; ii < java.lang.reflect.Array.getLength(obj); ii++) {
            Object elt = java.lang.reflect.Array.get(obj, ii);
            String elt_str = (elt == null ? "<null>" : (elt.getClass()
                    .isArray() ? print_(elt, offset) : String.valueOf(elt)));
            if (ii == 0)
                ret += elt_str;
            else if ((elt_str.indexOf('\n') > 0)
                    || ((elt_str.length() + ret.length()
                            - ret.lastIndexOf('\n') > 78) && (offset >= 0)))
                ret += "\n" + fill_(offset) + elt_str;
            else
                ret += " " + elt_str;
        }
        return ret + ")";
    }
}
