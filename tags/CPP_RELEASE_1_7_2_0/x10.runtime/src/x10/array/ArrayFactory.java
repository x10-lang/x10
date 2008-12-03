/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on 12.09.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package x10.array;

import x10.compilergenerated.Parameter1;
import x10.lang.BooleanReferenceArray;
import x10.lang.ByteReferenceArray;
import x10.lang.CharReferenceArray;
import x10.lang.DoubleReferenceArray;
import x10.lang.FloatReferenceArray;
import x10.lang.GenericReferenceArray;
import x10.lang.IntReferenceArray;
import x10.lang.LongReferenceArray;
import x10.lang.ShortReferenceArray;
import x10.lang.booleanArray;
import x10.lang.byteArray;
import x10.lang.charArray;
import x10.lang.dist;
import x10.lang.doubleArray;
import x10.lang.floatArray;
import x10.lang.genericArray;
import x10.lang.intArray;
import x10.lang.longArray;
import x10.lang.shortArray;

abstract public /*value*/ class ArrayFactory {
	/**
	 * Return the unique boolean value array initialized with false
	 * and defined over the distribution [0..k-1]->here.
	 */
	public booleanArray booleanValueArray(/*nat*/ int k) {
		return booleanValueArray(k, false);
	}
	/**
	 * Return the unique boolean value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public booleanArray/*(:rank=1)*/ booleanValueArray(/*nat*/ int k, boolean init) {
		return booleanValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique boolean value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public booleanArray/*(:rank=1)*/ booleanValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return booleanValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique boolean value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ booleanArray/*(D)*/ booleanValueArray(dist D, boolean init) {
		return BooleanArray(D, new booleanArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique boolean value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ booleanArray/*(D)*/ booleanValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return BooleanArray(D, init, true, false, false);
	}

	/**
	 * Return the unique boolean reference array initialized with false
	 * and defined over the distribution [0..k-1]->here.
	 */
	public BooleanReferenceArray BooleanReferenceArray(/*nat*/ int k) {
		return BooleanReferenceArray(k, false);
	}
	/**
	 * Return the unique boolean reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public BooleanReferenceArray/*(:rank=1)*/ BooleanReferenceArray(/*nat*/ int k, boolean init) {
		return BooleanReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique boolean reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public BooleanReferenceArray/*(:rank=1)*/ BooleanReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return BooleanReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique boolean reference array initialized with false
	 * and defined over the distribution D.
	 */
	public BooleanReferenceArray BooleanReferenceArray(dist D) {
		return BooleanReferenceArray(D, false);
	}
	/**
	 * Return the unique boolean reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ BooleanReferenceArray/*(D)*/ BooleanReferenceArray(dist D, boolean init) {
		return BooleanArray(D, new booleanArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique boolean reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ BooleanReferenceArray/*(D)*/ BooleanReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return BooleanArray(D, init, true, true, false);
	}

	/**
	 * Return the unique boolean array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ BooleanReferenceArray/*(D)*/ BooleanArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique byte value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public byteArray byteValueArray(/*nat*/ int k) {
		return byteValueArray(k, (byte) 0);
	}
	/**
	 * Return the unique byte value array initialized with initVal
	 * and defined over the distribution [0..k-1]->here.
	 */
	public byteArray/*(:rank=1)*/ byteValueArray(/*nat*/ int k, byte initVal) {
		return byteValueArray(x10.lang.dist.factory.local(k), initVal);
	}
	/**
	 * Return the unique byte value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public byteArray/*(:rank=1)*/ byteValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return byteValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique byte value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ byteArray/*(D)*/ byteValueArray(dist D, byte init) {
		return ByteArray(D, new byteArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique byte value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ byteArray/*(D)*/ byteValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return ByteArray(D, init, true, false, false);
	}

	/**
	 * Return the unique byte reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ByteReferenceArray ByteReferenceArray(/*nat*/ int k) {
		return ByteReferenceArray(k, (byte) 0);
	}
	/**
	 * Return the unique byte reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ByteReferenceArray/*(:rank=1)*/ ByteReferenceArray(/*nat*/ int k, byte init) {
		return ByteReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique byte reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ByteReferenceArray/*(:rank=1)*/ ByteReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return ByteReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique byte reference array initialized with 0
	 * and defined over the distribution D.
	 */
	public ByteReferenceArray ByteReferenceArray(dist D) {
		return ByteReferenceArray(D, (byte) 0);
	}
	/**
	 * Return the unique byte reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ ByteReferenceArray/*(D)*/ ByteReferenceArray(dist D, byte init) {
		return ByteArray(D, new byteArray.Constant(init), true, true, false);		
	}
	/**
	 * Return the unique byte reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ ByteReferenceArray/*(D)*/ ByteReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return ByteArray(D, init, true, true, false);
	}

	/**
	 * Return the unique byte array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ ByteReferenceArray/*(D)*/ ByteArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique char value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public charArray charValueArray(/*nat*/ int k) {
		return charValueArray(k, (char) 0);
	}
	/**
	 * Return the unique char value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public charArray/*(:rank=1)*/  charValueArray(/*nat*/ int k, char init) {
		return charValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique char value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public charArray/*(:rank=1)*/ charValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return charValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique char value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ charArray/*(D)*/ charValueArray(dist D, char init) {
		return CharArray(D, new charArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique char value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ charArray/*(D)*/ charValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return CharArray(D, init, true, false, false);
	}

	/**
	 * Return the unique char reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public CharReferenceArray CharReferenceArray(/*nat*/ int k) {
		return CharReferenceArray(k, (char)0);
	}
	/**
	 * Return the unique char reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public CharReferenceArray/*(:rank=1)*/  CharReferenceArray(/*nat*/ int k, char init) {
		return CharReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique char reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public CharReferenceArray/*(:rank=1)*/ CharReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return CharReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique char reference array initialized with 0
	 * and defined over the distribution D.
	 */
	public CharReferenceArray CharReferenceArray(dist D) {
		return CharReferenceArray(D, (char) 0);
	}
	/**
	 * Return the unique char reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ CharReferenceArray/*(D)*/ CharReferenceArray(dist D, char init) {
		return CharArray(D, new charArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique char reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ CharReferenceArray/*(D)*/ CharReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return CharArray(D, init, true, true, false);
	}

	/**
	 * Return the unique char array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ CharReferenceArray/*(D)*/ CharArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique int value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 * vj: Note that in this implementation, returns intArray rather than
	 * intValueArray.
	 */
	public intArray intValueArray(/*nat*/ int k) {
		return intValueArray(k, 0);
	}
	/**
	 * Return the unique int value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public intArray/*(:rank=1)*/  intValueArray(/*nat*/ int k, int init) {
		return intValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique int value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public intArray/*(:rank=1)*/ intValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return intValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique int value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ intArray/*(D)*/ intValueArray(dist D, int init) {
		return IntArray(D, new intArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique int value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ intArray/*(D)*/ intValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return IntArray(D, init, true, false, false);
	}
	/**
	 * Return an int value array initialized with the given 1-d 0:n-1 array a.
	 */
	abstract public intArray intValueArray(int[] a);

	/**
	 * Return the unique int reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public IntReferenceArray IntReferenceArray(/*nat*/ int k) {
		return IntReferenceArray(k, 0);
	}
	/**
	 * Return the unique int reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public IntReferenceArray/*(:rank=1)*/  IntReferenceArray(/*nat*/ int k, int init) {
		return IntReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique int reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public IntReferenceArray/*(:rank=1)*/ IntReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return IntReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique int reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ IntReferenceArray/*(D)*/ IntReferenceArray(dist D, int init) {
		return IntArray(D, new intArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique int reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ IntReferenceArray/*(D)*/ IntReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return IntArray(D, init, true, true, false);
	}

	/**
	 * Return the unique int array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ IntReferenceArray/*(D)*/ IntArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique long value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 * vj: Note that in this implementation, returns longArray rather than
	 * longValueArray.
	 * @param k
	 * @return
	 */
	public longArray longValueArray(/*nat*/ int k) {
		return longValueArray(k, 0);
	}
	/**
	 * Return the unique long value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public longArray/*(:rank=1)*/  longValueArray(/*nat*/ int k, long init) {
		return longValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique long value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public longArray/*(:rank=1)*/ longValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return longValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique long value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ longArray/*(D)*/ longValueArray(dist D, long init) {
		return LongArray(D, new longArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique long value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ longArray/*(D)*/ longValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return LongArray(D, init, true, false, false);
	}

	/**
	 * Return the unique long reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public LongReferenceArray LongReferenceArray(/*nat*/ int k) {
		return LongReferenceArray(k, 0);
	}
	/** Return the unique long reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public LongReferenceArray/*(:rank=1)*/ LongReferenceArray(/*nat*/ int k, long init) {
		return LongReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique long reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public LongReferenceArray/*(:rank=1)*/ LongReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return LongReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique long reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ LongReferenceArray/*(D)*/ LongReferenceArray(dist D, long init) {
		return LongArray(D, new longArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique long reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ LongReferenceArray/*(D)*/ LongReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return LongArray(D, init, true, true, false);
	}

	/**
	 * Return the unique long array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ LongReferenceArray/*(D)*/ LongArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique short value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public shortArray shortValueArray(/*nat*/ int k) {
		return shortValueArray(k, (short) 0);
	}
	/**
	 * Return the unique short value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public shortArray/*(:rank=1)*/  shortValueArray(/*nat*/ int k, short initVal) {
		return shortValueArray(x10.lang.dist.factory.local(k), initVal);
	}
	/**
	 * Return the unique short value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public shortArray/*(:rank=1)*/ shortValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return shortValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique short value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ shortArray/*(D)*/ shortValueArray(dist D, short init) {
		return ShortArray(D, new shortArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique short value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ shortArray/*(D)*/ shortValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return ShortArray(D, init, true, false, false);
	}

	/**
	 * Return the unique short reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ShortReferenceArray ShortReferenceArray(/*nat*/ int k) {
		return ShortReferenceArray(k, (short) 0);
	}
	/**
	 * Return the unique short reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ShortReferenceArray/*(:rank=1)*/  ShortReferenceArray(/*nat*/ int k, short init) {
		return ShortReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique short reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public ShortReferenceArray/*(:rank=1)*/ ShortReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return ShortReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique short reference array initialized with 0
	 * and defined over the distribution D.
	 */
	public ShortReferenceArray ShortReferenceArray(dist D) {
		return ShortReferenceArray(D, (short) 0);
	}
	/**
	 * Return the unique short reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ ShortReferenceArray/*(D)*/ ShortReferenceArray(dist D, short init) {
		return ShortArray(D, new shortArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique short reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ ShortReferenceArray/*(D)*/ ShortReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return ShortArray(D, init, true, true, false);
	}

	/**
	 * Return the unique short array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ ShortReferenceArray/*(D)*/ ShortArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique float value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public floatArray floatValueArray(/*nat*/ int k) {
		return floatValueArray(k, 0);
	}
	/**
	 * Return the unique float value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public floatArray/*(:rank=1)*/  floatValueArray(/*nat*/ int k, float init) {
		return floatValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique float value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public floatArray/*(:rank=1)*/ floatValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return floatValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique float value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ floatArray/*(D)*/ floatValueArray(dist D, float init) {
		return FloatArray(D, new floatArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique float value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ floatArray/*(D)*/ floatValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return FloatArray(D, init, true, false, false);
	}
	/**
	 * Return the unique float reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public FloatReferenceArray FloatReferenceArray(/*nat*/ int k) {
		return FloatReferenceArray(k, 0);
	}
	/**
	 * Return the unique float reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public FloatReferenceArray/*(:rank=1)*/ FloatReferenceArray(/*nat*/ int k, float init) {
		return FloatReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique float reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public FloatReferenceArray/*(:rank=1)*/ FloatReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return FloatReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique float reference array initialized with 0
	 * and defined over the distribution D.
	 */
	public FloatReferenceArray FloatReferenceArray(dist D) {
		return FloatReferenceArray(D, 0);
	}
	/**
	 * Return the unique float reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ FloatReferenceArray/*(D)*/ FloatReferenceArray(dist D, float init) {
		return FloatArray(D, new floatArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique float reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ FloatReferenceArray/*(D)*/ FloatReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return FloatArray(D, init, true, true, false);
	}

	/**
	 * Return the unique float array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ FloatReferenceArray/*(D)*/ FloatArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);

	/**
	 * Return the unique double value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public doubleArray doubleValueArray(/*nat*/ int k) {
		return doubleValueArray(k, 0);
	}
	/**
	 * Return the unique double value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public doubleArray/*(:rank=1)*/  doubleValueArray(/*nat*/ int k, double init) {
		return doubleValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique double value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public doubleArray/*(:rank=1)*/ doubleValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return doubleValueArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique double value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ doubleArray/*(D)*/ doubleValueArray(dist D, double init) {
		return DoubleArray(D, new doubleArray.Constant(init), true, false, false);
	}
	/**
	 * Return the unique double value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ doubleArray/*(D)*/ doubleValueArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return DoubleArray(D, init, true, false, false);
	}
	/**
	 * Return a double value array initialized with the given 1-d 0:n-1 array a.
	 */
	abstract public doubleArray doubleValueArray(double[] a);
	/**
	 * Return the unique double reference array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public DoubleReferenceArray DoubleReferenceArray(/*nat*/ int k) {
		return DoubleReferenceArray(k, 0);
	}
	/**
	 * Return the unique double reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public DoubleReferenceArray/*(:rank=1)*/ DoubleReferenceArray(/*nat*/ int k, double init) {
		return DoubleReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique double reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public DoubleReferenceArray/*(:rank=1)*/ DoubleReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return DoubleReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique double reference array initialized with 0
	 * and defined over the distribution D.
	 */
	public DoubleReferenceArray DoubleReferenceArray(dist D) {
		return DoubleReferenceArray(D, 0);
	}
	/**
	 * Return the unique double reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ DoubleReferenceArray/*(D)*/ DoubleReferenceArray(dist D, double init) {
		return DoubleArray(D, new doubleArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique double reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ DoubleReferenceArray/*(D)*/ DoubleReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return DoubleArray(D, init, true, true, false);
	}

	/**
	 * Return the unique double array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe     whether to create a safe array
	 * @param mutable  whether to create a mutable (reference) array
	 */
	abstract public /*(distribution D)*/ DoubleReferenceArray/*(D)*/ DoubleArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);
	abstract public /*(distribution D)*/ DoubleReferenceArray/*(D)*/ DoubleArray3d(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ignored);
	/**
	 * Return the unique generic value array initialized with 0
	 * and defined over the distribution [0..k-1]->here.
	 */
	public genericArray GenericValueArray(/*nat*/ int k) {
		return GenericValueArray(k, (Parameter1) null);
	}
	/**
	 * Return the unique generic value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public genericArray/*(:rank=1)*/ GenericValueArray(/*nat*/ int k, Parameter1 init) {
		return GenericValueArray(x10.lang.dist.factory.local(k), init, false);
	}
	/**
	 * Return the unique generic value array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public genericArray/*(:rank=1)*/ GenericValueArray(/*nat*/ int k, Operator.Pointwise init) {
		return GenericValueArray(x10.lang.dist.factory.local(k), init, false);
	}
	/**
	 * Return the unique generic value array initialized with init
	 * and defined over the distribution D.
	 */
	public
	/*(distribution D)*/ genericArray/*(D)*/ GenericValueArray(dist D, Parameter1 init, boolean refs_to_values) {
		return GenericArray(D, new genericArray.Constant(init), true, false, refs_to_values);
	}
	/**
	 * Return the unique generic value array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ genericArray/*(D)*/ GenericValueArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean refs_to_values) {
		return GenericArray(D, init, true, false, refs_to_values);
	}

	/**
	 * Return the unique generic reference array initialized with null
	 * and defined over the distribution [0..k-1]->here.
	 */
	public GenericReferenceArray GenericReferenceArray(/*nat*/ int k) {
		return GenericReferenceArray(k, (Parameter1) null);
	}
	/**
	 * Return the unique generic reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public GenericReferenceArray/*(:rank=1)*/ GenericReferenceArray(/*nat*/ int k, Parameter1 init) {
		return GenericReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique generic reference array initialized with init
	 * and defined over the distribution [0..k-1]->here.
	 */
	public GenericReferenceArray/*(:rank=1)*/ GenericReferenceArray(/*nat*/ int k, Operator.Pointwise init) {
		return GenericReferenceArray(x10.lang.dist.factory.local(k), init);
	}
	/**
	 * Return the unique generic reference array initialized with null
	 * and defined over the distribution D.
	 */
	public GenericReferenceArray GenericReferenceArray(dist D) {
		return GenericReferenceArray(D, (Parameter1) null);
	}
	/**
	 * Return the unique generic reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ GenericReferenceArray/*(D)*/ GenericReferenceArray(dist D, Parameter1 init) {
		return GenericArray(D, new genericArray.Constant(init), true, true, false);
	}
	/**
	 * Return the unique generic reference array initialized with init
	 * and defined over the distribution D.
	 */
	public /*(distribution D)*/ GenericReferenceArray/*(D)*/ GenericReferenceArray(dist D, Operator.Pointwise/*(D.region)*/ init) {
		return GenericArray(D, init, true, true, false);
	}

	/**
	 * Return the unique boolean array initialized with init and defined
	 * over the distribution D.
	 *
	 * @param safe           whether to create a safe array
	 * @param mutable        whether to create a mutable (reference) array
	 * @param ref_to_values  whether the elements of this array will be values
	 */
	abstract public /*(distribution D)*/ GenericReferenceArray/*(D)*/ GenericArray(dist D, Operator.Pointwise/*(D.region)*/ init, boolean safe, boolean mutable, boolean ref_to_values);
}
