package x10.array;

import x10.lang.DoubleReferenceArray;
import x10.lang.Indexable;
import x10.lang.dist;
import x10.lang.doubleArray;
import x10.lang.point;
import x10.lang.region;
import x10.lang.doubleArray.binaryOp;
import x10.lang.doubleArray.unaryOp;

public class DoubleProxyArray extends DoubleReferenceArray {

	private final DoubleReferenceArray realArray_;
	
	public DoubleProxyArray(dist d, DoubleReferenceArray real_array) {
		super(d);
		realArray_ = real_array;
	}
	
	public double set(double v, point p) {
		return realArray_.set(v, p);
	}

	public double set(double v, int p) {
		return realArray_.set(v, p);
	}

	public double set(double v, int p, int q) {
		return realArray_.set(v, p, q);
	}

	public double set(double v, int p, int q, int r) {
		return realArray_.set(v, p, q, r);
	}

	public double set(double v, int p, int q, int r, int s) {
		return realArray_.set(v, p, q, r, s);
	}

	public double get(point p) {
		return realArray_.get(p);
	}

	public double get(int p) {
		return realArray_.get(p);
	}

	public double get(int p, int q) {
		return realArray_.get(p, q);
	}

	public double get(int p, int q, int r) {
		return realArray_.get(p, q, r);
	}

	public double get(int p, int q, int r, int s) {
		return realArray_.get(p, q, r, s);
	}

	public double get(int[] p) {
		return realArray_.get(p);
	}

	public double reduce(binaryOp fun, double unit) {
		return realArray_.reduce(fun, unit);
	}

	public DoubleReferenceArray scan(binaryOp fun, double unit) {
		return realArray_.scan(fun, unit);
	}

	public DoubleReferenceArray restriction(region R) {
		return realArray_.restriction(R);
	}

	public DoubleReferenceArray union(doubleArray other) {
		return realArray_.union(other);
	}

	public DoubleReferenceArray overlay(doubleArray other) {
		return realArray_.overlay(other);
	}

	public void update(doubleArray other) {
		realArray_.update(other);
	}

	public DoubleReferenceArray lift(binaryOp fun, doubleArray a) {
		return realArray_.lift(fun, a);
	}

	public DoubleReferenceArray lift(unaryOp fun) {
		return realArray_.lift(fun);
	}

	public doubleArray toValueArray() {
		return realArray_.toValueArray();
	}

	public boolean isValue() {
		return realArray_.isValue();
	}

	public boolean valueEquals(Indexable other) {
		return realArray_.valueEquals(other);
	}

	public long getUnsafeAddress() {
		return realArray_.getUnsafeAddress();
	}

	public long getUnsafeDescriptor() {
		return realArray_.getUnsafeDescriptor();
	}
	
	public DoubleReferenceArray restrictShallow (region r) {
		return realArray_.restrictShallow(r);
	}	
}
