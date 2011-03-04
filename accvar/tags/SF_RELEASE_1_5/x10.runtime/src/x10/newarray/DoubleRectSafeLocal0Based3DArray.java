/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.newarray;

/**
 * @author donawa
 *
 * TODO: Move this to x10.array.sharedmemory.specialized
 */
final public class DoubleRectSafeLocal0Based3DArray {

    // i is most slowly varying index.
    public final int iMax, jMax, kMax; // inclusive bound
    protected int size;
    protected double[] m;
    
    public interface FunDoubleInt3 {
        double eval(int i, int j, int k);
    }
    public interface FunDoubleInt {
        double eval(int i);
    }
    public interface FunDoubleInt3Double {
        double eval(int i, int j, int k, double d);
    }
    public interface FunDoubleIntDouble {
        double eval(int u, double d);
    }
    public interface FunDoubleDouble2 {
        double eval(double x, double y);
    }
    public DoubleRectSafeLocal0Based3DArray(int i, int j, int k) {
        iMax = i-1;
        jMax = j-1;
        kMax = k-1;
        size = i*j*k;
        m = new double[size];
    }
    public DoubleRectSafeLocal0Based3DArray(int i, int j, int k, FunDoubleInt3 f) {
        iMax = i-1;
        jMax = j-1;
        kMax = k-1;
        size = i*j*k;
        m = new double[size];
        for (int u=0; u < size; u++)
            m[u] = f.eval(i(u), j(u), k(u));
        /*
         for (int x=0; x < iMax; x++)
         for (int y=0; y < yMax; y++)
         for (int z=0; z < zMax; z++)
         m[u(i,j,k)] = f(i,j,k);*/
    }
    public DoubleRectSafeLocal0Based3DArray(int i, int j, int k, FunDoubleInt f) {
        iMax = i-1;
        jMax = j-1;
        kMax = k-1;
        size = i*j*k;
        m = new double[size];
        for (int u=0; u < size; u++)
            m[u] = f.eval(u);
        /*
         for (int x=0; x < iMax; x++)
         for (int y=0; y < yMax; y++)
         for (int z=0; z < zMax; z++)
         m[u(i,j,k)] = f(i,j,k);*/
    }
    
    private int u(int i, int j, int k) { return k+kMax*(j + i*jMax);}
    private int i(int u) { return u /(jMax*kMax);}
    private int j(int u) { return (u /kMax)% jMax;}
    private int k(int u) { return (u % kMax);}
    
    public double get(int i, int j, int k) { return m[u(i,j,k)];}
    public double getOrd(int u) { return m[u];}
    
    public void set(int i, int j, int k, double v) { m[u(i,j,k)]=v;}
    public void plusSet(int i, int j, int k, double v) { m[u(i,j,k)] +=v;}
    public void timesSet(int i, int j, int k, double v) { m[u(i,j,k)] *=v;}
    public void setOrd(int u, double v) { m[u]=v;}
    public void plusSetOrd(int u, double v) { m[u] +=v;}
    public void timesSetOrd(int u, double v) { m[u] *=v;}
    
    public void allUp(FunDoubleInt3Double f) {
        for (int u=0; u < size; u++)
            m[u] = f.eval(i(u), j(u), k(u), m[u]);  
    }
    public void allUpOrd(FunDoubleIntDouble f) {
        for (int u=0; u < size; u++)
            m[u] = f.eval(u, m[u]);
}
    public void allDown(FunDoubleInt3Double f) {
        for (int u=size-1; u >= 0; u--)
            m[u] = f.eval(i(u), j(u), k(u), m[u]);  
    }
    public void liftUp(DoubleRectSafeLocal0Based3DArray other, FunDoubleDouble2 f) {
        assert other.iMax==iMax && other.jMax==jMax && other.iMax==iMax;
        for (int u=0; u < size; u++)
            m[u] = f.eval(m[u], other.getOrd(u));
    }
    public void liftDown(DoubleRectSafeLocal0Based3DArray other, FunDoubleDouble2 f) {
        assert other.iMax==iMax && other.jMax==jMax && other.iMax==iMax;
        for (int u=size-1; u >= 0; u--)
            m[u] = f.eval(m[u], other.getOrd(u));
    }
    public void reduce(FunDoubleDouble2 f, double unit) {
        double result = unit;
        for (int u=0; u < size; u++)
            result = f.eval(m[u], result);
    }
    public DoubleRectSafeLocal0Based3DArray scan(FunDoubleDouble2 f, double unit) {
        double  temp = unit;
        DoubleRectSafeLocal0Based3DArray result = 
            new  DoubleRectSafeLocal0Based3DArray(iMax, jMax, kMax);
        for (int u=0; u < size; u++)
            result.setOrd(u, temp = f.eval(m[u], temp));
        return result;
    }
}

