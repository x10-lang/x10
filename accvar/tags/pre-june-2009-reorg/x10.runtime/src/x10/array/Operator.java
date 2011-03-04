/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 28, 2004
 */
package x10.array;

import x10.compilergenerated.Parameter1;
import x10.lang.point;

/**
 * Base class for all operation classes. Implementations override one of the
 * methods. The default implementation of each operator throws a runtime
 * exception.
 * 
 * @author Christoph von Praun
 * @author Igor Peshansky (added Unary and Binary)
 */
public abstract class Operator {

	private static final String ERR_ = "Operation for requested type not implemented.";

	/**
	 * The operation performed must be functional, i.e. stateless. It is the
	 * user's obligation to make sure that both conditions are met. The argument of the
	 * apply function is the current value at the current position in the array. 
	 */
	public static abstract class Pointwise extends Operator {
		public boolean apply(point p, boolean i) {
			throw new RuntimeException(ERR_);
		}

		public byte apply(point p, byte i) {
			throw new RuntimeException(ERR_);
		}

		public char apply(point p, char i) {
			throw new RuntimeException(ERR_);
		}

		public short apply(point p, short i) {
			throw new RuntimeException(ERR_);
		}

		public int apply(point p, int i) {
			throw new RuntimeException(ERR_);
		}

		public long apply(point p, long i) {
			throw new RuntimeException(ERR_);
		}

		public float apply(point p, float i) {
			throw new RuntimeException(ERR_);
		}

		public double apply(point p, double i) {
			throw new RuntimeException(ERR_);
		}

		public Parameter1 apply(point p, Parameter1 i) {
		    throw new RuntimeException(ERR_);
		}
		
		public boolean apply(point p, boolean i, boolean j) {
			throw new RuntimeException(ERR_);
		}

		public byte apply(point p, byte i, byte j) {
			throw new RuntimeException(ERR_);
		}

		public char apply(point p, char i, char j) {
			throw new RuntimeException(ERR_);
		}

		public short apply(point p, short i, short j) {
			throw new RuntimeException(ERR_);
		}

		public int apply(point p, int i, int j) {
			throw new RuntimeException(ERR_);
		}

		public long apply(point p, long i, long j) {
			throw new RuntimeException(ERR_);
		}

		public float apply(point p, float i, float j) {
			throw new RuntimeException(ERR_);
		}

		public double apply(point p, double i, double j) {
			throw new RuntimeException(ERR_);
		}

        public Parameter1 apply(point p, Parameter1 i, Parameter1 j) {
        	throw new RuntimeException(ERR_);
        }
	}
	
	/**
	 * The operation performed must be fully associative and cumulative. It is
	 * the user's obligation to make sure that both conditions are met.
	 */
	public static abstract class Unary extends Operator {
		public boolean apply(boolean i) {
			throw new RuntimeException(ERR_);
		}

		public byte apply(byte i) {
			throw new RuntimeException(ERR_);
		}

		public char apply(char i) {
			throw new RuntimeException(ERR_);
		}

		public short apply(short i) {
			throw new RuntimeException(ERR_);
		}

		public int apply(int i) {
			throw new RuntimeException(ERR_);
		}

		public long apply(long i) {
			throw new RuntimeException(ERR_);
		}

		public float apply(float i) {
			throw new RuntimeException(ERR_);
		}

		public double apply(double i) {
			throw new RuntimeException(ERR_);
		}

        public Parameter1 apply(Parameter1 o) {
        	throw new RuntimeException(ERR_);
        }
	}
	
	/**
	 * The operation performed must be functional, i.e. stateless. It is the
	 * user's obligation to make sure that both conditions are met. The argument of the
	 * apply function is the current value at the current position in the array. 
	 */
	public static abstract class Binary extends Operator {
		public boolean apply(boolean i, boolean j) {
			throw new RuntimeException(ERR_);
		}

		public byte apply(byte i, byte j) {
			throw new RuntimeException(ERR_);
		}

		public char apply(char i, char j) {
			throw new RuntimeException(ERR_);
		}

		public short apply(short i, short j) {
			throw new RuntimeException(ERR_);
		}

		public int apply(int i, int j) {
			throw new RuntimeException(ERR_);
		}

		public long apply(long i, long j) {
			throw new RuntimeException(ERR_);
		}

		public float apply(float i, float j) {
			throw new RuntimeException(ERR_);
		}

		public double apply(double i, double j) {
			throw new RuntimeException(ERR_);
		}

        public Parameter1 apply(Parameter1 i, Parameter1 j) {
        	throw new RuntimeException(ERR_);
        }
	}
	
	/**
	 * The operation performed must be fully associative and cumulative. It is
	 * the user's obligation to make sure that both conditions are met.
	 */
	public static abstract class Reduction extends Operator {

	    public void apply(boolean i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(byte i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(char i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(short i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(int i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(long i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(float i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(double i) {
			throw new RuntimeException(ERR_);
		}

		public void apply(Parameter1 i) {
			throw new RuntimeException(ERR_);
		}
		
		public boolean getBooleanResult() {
			throw new RuntimeException(ERR_);
		}

		public byte getByteResult() {
			throw new RuntimeException(ERR_);
		}

		public short getShortResult() {
			throw new RuntimeException(ERR_);
		}

		public char getCharResult(){
			throw new RuntimeException(ERR_);
		}

		public int getIntResult() {
			throw new RuntimeException(ERR_);
		}

		public long getLongResult() {
			throw new RuntimeException(ERR_);
		}

		public float getFloatResult() {
			throw new RuntimeException(ERR_);
		}
		
		public double getDoubleResult() {
			throw new RuntimeException(ERR_);
		}
		
		public Parameter1 getGenericResult() {
			throw new RuntimeException(ERR_);
		}
		
		public void reset() {
			throw new RuntimeException(ERR_);
		}
	}
}