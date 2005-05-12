/*
 * Created on Oct 28, 2004
 */
package x10.array;

import java.util.Iterator;
import x10.lang.dist;
import x10.lang.point;
import x10.lang.ByteReferenceArray;

/**
 * @author Christoph von Praun
 * 
 * Byte Arrays are currently not implemented.
 */
public abstract class ByteArray extends ByteReferenceArray {
    public ByteArray(dist d) {
        super(d);
    }
    
    public static class Assign extends Operator.Scan {
        private final byte c_;

        public Assign(byte c) {
            c_ = c;
        }

        public byte apply(byte i) {
            return c_;
        }
    }

    protected void assign(ByteArray rhs) {
        assert rhs instanceof ByteArray;

        ByteArray rhs_t =  rhs;
        for (Iterator it = rhs_t.distribution.region.iterator(); it.hasNext();) {
            point pos = (point) it.next();
            set(rhs_t.get(pos), pos);
        }
    }

	/*
	 * Generic implementation - an array with fixed, known number of dimensions
	 * can of course do without the Iterator.
	 */
	public void pointwise(ByteArray res, Operator.Pointwise op, ByteArray arg) {
	    assert res.distribution.equals(distribution);
        assert arg.distribution.equals(distribution);
		
		ByteArray arg_t =  arg;
		ByteArray res_t = res;
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			byte arg1 = get(p);
			byte arg2 = arg_t.get(p);
			byte val = op.apply(p, arg1, arg2);
			res_t.set(val, p);
		}
	}
	
	public void pointwise(ByteArray res, Operator.Pointwise op) {
	    assert res == null || res.distribution.equals(distribution);
        
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			byte arg1 = get(p);
			byte val = op.apply(p, arg1);
			if (res != null)
			    res.set(val, p);
		}
	}
	
	public void reduction(Operator.Reduction op) {
		for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			byte arg1 = get(p);
			op.apply(arg1);
		}
	}
	
	public void scan(ByteArray res, Operator.Scan op) {
	    assert res.distribution.equals(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			byte arg1 = get(p);
			res.set(op.apply(arg1), p);
		}
	}
	
	  public void scan( ByteArray res, pointwiseOp op ) {
        assert res == null || res instanceof ByteArray;
        assert res.distribution.equals(distribution);

        ByteArray res_t = (res == null) ? null : (ByteArray) res;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            byte val = op.apply(p);
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
    public abstract byte set(byte v, point pos);

    public abstract byte set(byte v, int d0);

    public abstract byte set(byte v, int d0, int d1);

    public abstract byte set(byte v, int d0, int d1, int d2);

    public abstract byte set(byte v, int d0, int d1, int d2, int d3);

    /**
     * Generic flat access.
     */
    public abstract byte get(point pos);

    public abstract byte get(int d0);

    public abstract byte get(int d0, int d1);

    public abstract byte get(int d0, int d1, int d2);

    public abstract byte get(int d0, int d1, int d2, int d3);
    public abstract byte get(int[] p);
    
    public Object toJava() {        
        final int[] dims_tmp = new int[distribution.rank];       
        for (int i = 0; i < distribution.rank; ++i) {
            dims_tmp[i] = distribution.region.rank(i).high() + 1;
        }
        
        final Object ret = java.lang.reflect.Array.newInstance(Byte.TYPE, dims_tmp);
        pointwise(null, new Operator.Pointwise() {
            public byte apply(point p, byte arg) {
                Object handle = ret;
                int i = 0;
                for (; i < dims_tmp.length - 1; ++i) {
                    handle = java.lang.reflect.Array.get(handle, p.get(i));
                }
                java.lang.reflect.Array.setByte(handle, p.get(i), arg);
                return arg;
            }
        });
        return ret;
    }
    
    /* for debugging */
    public static void printArray(String prefix, byte[][] a) {
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
