/*
 * Created on March 2nd, 2005
 *
 */
package x10.array;

import java.util.Iterator;

import x10.compilergenerated.Parameter1;
import x10.lang.distribution;
import x10.lang.point;


/**
 * Generic arrays.
 * 
 * @author Christian Grothoff
 */
public abstract class GenericArray extends x10.lang.GenericReferenceArray {

    public static class Assign extends Operator.Scan {
        private final Parameter1 c_;

        public Assign(Parameter1 c) {
            c_ = c;
        }

        public Parameter1 apply(Parameter1 i) {
            return c_;
        }
    }

    public GenericArray(distribution d) {
        super(d);
    }

    /**
     * Constructor that implements constant promotion (Chap. 10.4.2)
     * 
     * @param d
     *            Distribution of the array
     * @param c
     *            constant used to intialize all values of the array
     */
    protected GenericArray(distribution d, Parameter1 c) {
        super(d);
        // initialize all array alements
        scan(this, new Assign(c));
    }

    protected void assign(GenericArray rhs) {
        assert rhs instanceof GenericArray;
        assert rhs.distribution.equals(distribution);

        GenericArray rhs_t = (GenericArray) rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

    /*
     * Generic implementation - an array with fixed, known number of dimensions
     * can of course do without the Iterator.
     */
    public void pointwise(GenericArray res, Operator.Pointwise op, GenericArray arg) {
        assert res == null || res.distribution.equals(distribution);
        assert arg != null;
        assert arg.distribution.equals(distribution);

        GenericArray res_t = (GenericArray) res;
        GenericArray arg_t = (GenericArray) arg;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            Parameter1 arg1 = get(p);
            Parameter1 arg2 = arg_t.get(p);
            Parameter1 val = op.apply(p, arg1, arg2);
            if (res_t != null)
                res_t.set(val, p);
        }
    }

    public void pointwise(GenericArray res, Operator.Pointwise op) {
        assert res == null || res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            Parameter1 arg1 = get(p);
            Parameter1 val = op.apply(p, arg1);
            if (res != null)
                res.set(val, p);
        }
    }

    /* operations can be performed in any order */
    public void reduction(Operator.Reduction op) {
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            Parameter1 arg1 = get(p);
            op.apply(arg1);
        }
    }

    /* operations are performed in canonical order */
    public void scan( GenericArray res, Operator.Scan op ) {
        assert res == null || res instanceof GenericArray;
        assert res.distribution.equals(distribution);

        GenericArray res_t = (res == null) ? null : (GenericArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            Parameter1 arg1 = get(p);
            Parameter1 val = op.apply(arg1);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
    public void scan( GenericArray res, GenericArray.pointwiseOp op ) {
        assert res == null || res instanceof GenericArray;
        assert res.distribution.equals(distribution);

        GenericArray res_t = (res == null) ? null : (GenericArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            Parameter1 val = op.apply(p);
            if (res_t != null)
                res_t.set(val, p);
        }
    }
    
    

    public void circshift(Parameter1[] args) {
        throw new RuntimeException("TODO");
    }

    /**
     * Generic flat access.
     */
    public abstract Parameter1 set(Parameter1 v, point pos);

    public abstract Parameter1 set(Parameter1 v, int d0);

    public abstract Parameter1 set(Parameter1 v, int d0, int d1);

    public abstract Parameter1 set(Parameter1 v, int d0, int d1, int d2);

    public abstract Parameter1 set(Parameter1 v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access. the leas significant dimension is pos[0].
     */
    public abstract Parameter1 get(point pos);

    public abstract Parameter1 get(int d0);

    public abstract Parameter1 get(int d0, int d1);

    public abstract Parameter1 get(int d0, int d1, int d2);

    public abstract Parameter1 get(int d0, int d1, int d2, int d3);

    public Object toJava() {        

        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) 
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        
        final Object ret = java.lang.reflect.Array.newInstance(Integer.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public Parameter1 apply(point p, Parameter1 arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.set(handle, p.get(i), arg);
                return arg;
            }
        });
		return ret;
	}
    
    /* for debugging */
    public static void printArray(String prefix, int[][] a) {
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
} // end of GenericArray
