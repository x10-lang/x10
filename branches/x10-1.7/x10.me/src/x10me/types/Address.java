/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.types;

/**
 * The {@link Address} type is used by the runtime system and collector to
 * denote machine addresses.  We use a separate type instead of the
 * Java int type for coding clarity,  machine-portability (it can map
 * to 32 bit and 64 bit integral types), and access to unsigned
 * operations (Java does not have unsigned int types).
 * <p>
 * For efficiency and to avoid meta-circularity, the Address class is
 * intercepted like {@link org.jikesrvm.runtime.Magic} and converted into the base type so no
 * Address object is created run-time.
 *
 */
public final class Address {

  private long value;

  /**
   * Constructor.
   * @param value
   * @param zeroExtend <code>true</code> if the value is to be zero extended, 
   *         <code>false</code> for sign extension.
   */
  Address(int value, boolean zeroExtend) {
    this.value = (zeroExtend) ? ((long)value) & 0x00000000ffffffffL : value;
  }

  Address(int value) {
    this (value, false);
  }

  Address(long value) {
    this.value = value;
  }

  /****************************************************************************
   *
   * Special values
   */

  /** Constant zero address */
  private static final Address _zero = new Address(0);

  public static Address objectAsAddress(Object value) {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * Return an {@link Address} instance that reflects the value
   * zero.
   *
   * @return An {@link Address} instance that reflects the value zero.
   */
  public static Address zero() {
    return _zero;
  }

  /**
   * Return <code>true</code> if this instance is zero.
   *
   * @return <code>true</code> if this instance is zero.
   */
  public boolean isZero() {
    return EQ(zero());
  }

  /** Constant zero address */
  private static final Address _max = fromIntSignExtend(-1);

  /**
   * Return an {@link Address} instance that reflects the maximum
   * allowable {@link Address} value.
   *
   * @return An {@link Address} instance that reflects the
   * maximum allowable {@link Address} value.
   */
  public static Address max() {
    return _max;
  }

  /**
   * Return <code>true</code> if this instance is the maximum allowable
   * {@link Address} value.
   *
   * @return <code>true</code> if this instance is the maximum allowable
   * {@link Address} value.
   */
  public boolean isMax() {
    return EQ(max());
  }

  /****************************************************************************
   *
   * Conversions
   */

  /**
   * Fabricate an {@link Address} instance from an integer, after
   * sign extending the integer.
   *
   * @param address the integer from which to create an {@link Address}
   * instance
   * @return An address instance
   */
  public static Address fromIntSignExtend(int address) {
    return new Address(address);
  }

  /**
   * Fabricate an {@link Address} instance from an integer, after
   * zero extending the integer.
   *
   * @param address the integer from which to create an {@link Address}
   * instance
   * @return An address instance
   */
  public static Address fromIntZeroExtend(int address) {
    return new Address(address, true);
  }

  /**
   * Fabricate an {@link Address} instance from a long.
   *
   * @param address The long from which to create an {@link Address}
   * instance
   * @return An address instance
   */
  public static Address fromLong(long address) {
    return new Address(address);
  }

  /**
   * Return an integer that reflects the value of this
   * {@link Address} instance.
   *
   * @return An integer that reflects the value of this
   * {@link Address} instance.
   */
  public int toInt() {
    return (int) value;
  }

  /**
   * Return a <code>long</code> that reflects the value of this
   * {@link Address} instance.
   *
   * @return a <code>long</code> that reflects the value of this
   * {@link Address} instance.
   */
  public long toLong() {
    return value;
  }

  /**
   * Return a <code>Word</code> instance that reflects the value of
   * this {@link Address} instance.
   *
   * @return A <code>Word</code> instance that reflects the value of
   * this {@link Address} instance.
   */
  public Word toWord() {
    return new Word(value);
  }

  /****************************************************************************
   *
   * Arithemtic operators
   */

  /**
   * Add an integer to this {@link Address}, and return the sum.
   *
   * @param  v the value to be added to this {@link Address}
   * @return An {@link Address} instance that reflects the result
   * of the addition.
   */
  public Address plus(int v) {
    return new Address(value + v);
  }

  /**
   * Add an {@link Offset} to this {@link Address}, and
   * return a new {@link Address} which is the sum.
   *
   * @param offset the {@link Offset} to be added to the address
   * @return An {@link Address} instance that reflects the result
   * of the addition.
   */
  public Address plus(Offset offset) {
    return new Address(value + offset.toWord().toAddress().value);
  }

  /**
   * Add an {@link Extent} to this {@link Address}, and
   * return a new {@link Address} which is the sum.
   *
   * @param extent the {@link Extent} to be added to this
   * {@link Address}
   * @return An {@link Address} instance that reflects the result
   * of the addition.
   */
  public Address plus(Extent extent) {
    return new Address(value + extent.toWord().toAddress().value);
  }

  /**
   * Subtract an integer from this {@link Address}, and return
   * the result.
   *
   * @param v The integer to be subtracted from this
   * {@link Address}.
   * @return An {@link Address} instance that reflects the result
   * of the subtraction.
   */
  public Address minus(int v) {
    return new Address(value - v);
  }

  /**
   * Subtract an {@link Offset} from this {@link Address}, and
   * return the result.
   *
   * @param offset the {@link Offset} to be subtracted from this
   * {@link Address}.
   * @return An {@link Address} instance that reflects the result
   * of the subtraction.
   */
  public Address minus(Offset offset) {
    return new Address(value - offset.toWord().toAddress().value);
  }

  /**
   * Subtract an {@link Extent} from this {@link Address}, and
   * return the result.
   *
   * @param extent the {@link Extent} to be subtracted from this
   * {@link Address}.
   * @return An {@link Address} instance that reflects the result
   * of the subtraction.
   */
  public Address minus(Extent extent) {
    return new Address(value - extent.toWord().toAddress().value);
  }

  /**
   * Compute the difference between two {@link Address}es and
   * return the result.
   *
   * @param addr2 the {@link Address} to be subtracted from this
   * {@link Address}.
   * @return An {@link Offset} instance that reflects the result
   * of the subtraction.
   */
  public Offset diff(Address addr2) {
    return new Offset(value - addr2.value);
  }


  /****************************************************************************
   *
   * Boolean operators
   */

  /**
   * Return true if this {@link Address} instance is <i>less
   * than</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>less
   * than</i> <code>addr2</code>.
   */
  public boolean LT(Address addr2) {
    if (value >= 0 && addr2.value >= 0) return value < addr2.value;
    if (value < 0 && addr2.value < 0) return value < addr2.value;
    if (value < 0) return false;
    return true;
  }

  /**
   * Return true if this {@link Address} instance is <i>less
   * than or equal to</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>less
   * than or equal to</i> <code>addr2</code>.
   */
  public boolean LE(Address addr2) {
    return (value == addr2.value) || LT(addr2);
  }

  /**
   * Return true if this {@link Address} instance is <i>greater
   * than</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>greater
   * than</i> <code>addr2</code>.
   */
  public boolean GT(Address addr2) {
    return addr2.LT(this);
  }

  /**
   * Return true if this {@link Address} instance is <i>greater
   * than or equal to</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>greater
   * than or equal to</i> <code>addr2</code>.
   */
  public boolean GE(Address addr2) {
    return addr2.LE(this);
  }

  /**
   * Return true if this {@link Address} instance is <i>equal
   * to</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>equal
   * to</i> <code>addr2</code>.
   */
  public boolean EQ(Address addr2) {
    return value == addr2.value;
  }

  /**
   * Return true if this {@link Address} instance is <i>not equal
   * to</i> <code>addr2</code>.
   *
   * @param addr2 the {@link Address} to be compared to this
   * {@link Address}.
   * @return true if this {@link Address} instance is <i>not
   * equal to</i> <code>addr2</code>.
   */
  public boolean NE(Address addr2) {
    return !EQ(addr2);
  }
}
