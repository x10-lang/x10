/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.distribution;
import x10.lang.point;
import x10.lang.CharReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Char Arrays are currently not implemented.
 */
public abstract class CharArray extends CharReferenceArray {
    public CharArray(distribution d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final char c_;

        public Assign(char c) {
            c_ = c;
        }

        public char apply(char i) {
            return c_;
        }
    }

    protected void assign(CharArray rhs) {
        assert rhs instanceof CharArray;

        CharArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(CharArray res, Operator.Pointwise op, CharArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
		
		CharArray arg_t =  arg;
		CharArray res_t = res;
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			char arg1 = get(p);
			char arg2 = arg_t.get(p);
			char val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(CharArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			char arg1 = get(p);
			char val = op.apply(p, arg1);
			if (res != null)
			    res.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			char arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(CharArray res, Operator.Scan op) {
	    assert res.distribution.equals(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			char arg1 = get(p);
			res.set(op.apply(arg1), p);
		}
	}
	
	  public void scan( CharArray res, pointwiseOp op ) {
        assert res == null || res instanceof CharArray;
        assert res.distribution.equals(distribution);

        CharArray res_t = (res == null) ? null : (CharArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            char val = op.apply(p);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
	
	public void circshift (int[] args) {
		throw new RuntimeException("TODO");
	}
	
    /**
     * Generic flat access.
     */
    public abstract char set(char v, point pos);

    public abstract char set(char v, int d0);

    public abstract char set(char v, int d0, int d1);

    public abstract char set(char v, int d0, int d1, int d2);

    public abstract char set(char v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract char get(point pos);

    public abstract char get(int d0);

    public abstract char get(int d0, int d1);

    public abstract char get(int d0, int d1, int d2);

    public abstract char get(int d0, int d1, int d2, int d3);
    public abstract char get(int[] p);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Character.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public char apply(point p, char arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setChar(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, char[][] a) {
        System.out.print(prefix + "{");
        for (int i = 0; i < a.length; ++i) {
            System.out.print("{");
            for (int j = 0; j < a[i].length; ++ j) {
                System.out.print(a[i][j]);
                if (j < a[i].length - 1)
                    System.out.print(", ");
            }
            System.out.print("}");
            if (i < a.length - 1)
                System.out.print(", ");
        }
        System.out.println("}");
    }

}
