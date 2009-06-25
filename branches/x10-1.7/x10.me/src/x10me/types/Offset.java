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
 * The offset type is used by the runtime system and collector to denote
 * the directed distance between two machine addresses.
 * We use a separate type instead of the Java int type for coding clarity.
 * machine-portability (it can map to 32 bit and 64 bit integral types),
 * and access to unsigned operations (Java does not have unsigned int types).
 * <p>
 * For efficiency and to avoid meta-circularity, the Offset class is intercepted like
 * magic and converted into the base type so no Offset object is created run-time.
 *
 * @see Address Word
 */
public final class Offset {

  private long value;
  /**
   * Constructor.
   * @param value
   * @param zeroExtend <code>true</code> if the value is to be zero extended, 
   *         <code>false</code> for sign extension.
   */
  Offset(int value, boolean zeroExtend) {
    this.value = (zeroExtend) ? ((long)value) & 0x00000000ffffffffL : value;
  }

  Offset(int value) {
    this (value, false);
  }

  Offset(long value) {
    this.value = value;
  }
  
  /* Compensate for some java compilers helpfully defining this synthetically */
  public String toString() {
    return super.toString();
  }

  public boolean equals(Object o) {
    return (o instanceof Offset) && ((Offset) o).value == value;
  }

  public static Offset fromIntSignExtend(int address) {
    return new Offset(address);
  }

  public static Offset fromIntZeroExtend(int address) {
    return new Offset(address, true);
  }

  public static Offset fromLong(long offset) {
    return new Offset(offset);
  }

  public static Offset zero() {
    return new Offset(0);
  }

  public static Offset max() {
    return fromIntSignExtend(-1);
  }

  public int toInt() {
    return (int) value;
  }

  public long toLong() {
    return value;
  }

  public Word toWord() {
    return new Word(value);
  }

  public Offset plus(int byteSize) {
    return new Offset(value + byteSize);
  }

  public Offset plus(Offset off2) {
    return new Offset(value + off2.value);
  }

  public Offset minus(int byteSize) {
    return new Offset(value - byteSize);
  }

  public Offset minus(Offset off2) {
    return new Offset(value - off2.value);
  }

  public boolean EQ(Offset off2) {
    return value == off2.value;
  }

  public boolean NE(Offset off2) {
    return value != off2.value;
  }

  public boolean sLT(Offset off2) {
    return value < off2.value;
  }

  public boolean sLE(Offset off2) {
    return value <= off2.value;
  }

  public boolean sGT(Offset off2) {
    return value > off2.value;
  }

  public boolean sGE(Offset off2) {
    return value >= off2.value;
  }

  public boolean isZero() {
    return EQ(zero());
  }

  public boolean isMax() {
    return EQ(max());
  }
}
