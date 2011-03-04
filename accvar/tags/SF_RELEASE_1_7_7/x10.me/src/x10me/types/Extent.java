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
 * The extent type is used by the runtime system and collector to denote the
 * undirected distance between two machine addresses. It is most similar
 * to an unsigned int and as such, comparison are unsigned.
 * <p>
 * For efficiency and to avoid meta-circularity, the class is intercepted like
 * magic and converted into the base type so no objects are created run-time.
 *
 * @see Address Word Offset
 */
public final class Extent {
  private long value;

  Extent(int value, boolean zeroExtend) {
    this.value = (zeroExtend) ? ((long)value) & 0x00000000ffffffffL : value;
  }

  Extent(int value) {
    this (value, false);
  }

  Extent(long value) {
    this.value = value;
  }

  public boolean equals(Object o) {
    return (o instanceof Extent) && ((Extent) o).value == value;
  }

  public static Extent fromIntSignExtend(int address) {
    return new Extent(address);
  }

  public static Extent fromIntZeroExtend(int address) {
    return new Extent(address, true);
  }

  public static Extent fromLong(long offset) {
    return new Extent(offset);
  }

  public static Extent zero() {
    return new Extent(0);
  }

  public static Extent one() {
    return new Extent(1);
  }

  public static Extent max() {
    return fromIntSignExtend(-1);
  }

  public int toInt() {
    return (int)value;
  }

  public long toLong() {
    return value;
  }

  public Word toWord() {
    return new Word(value);
  }

  public Extent plus(int byteSize) {
    return new Extent(value + byteSize);
  }

  public Extent plus(Extent byteSize) {
    return new Extent(value + byteSize.value);
  }

  public Extent minus(int byteSize) {
    return new Extent(value - byteSize);
  }

  public Extent minus(Extent byteSize) {
    return new Extent(value - byteSize.value);
  }

  public boolean LT(Extent extent2) {
    if (value >= 0 && extent2.value >= 0) return value < extent2.value;
    if (value < 0 && extent2.value < 0) return value < extent2.value;
    if (value < 0) return false;
    return true;
  }

  public boolean LE(Extent extent2) {
    return (value == extent2.value) || LT(extent2);
  }

  public boolean GT(Extent extent2) {
    return extent2.LT(this);
  }

  public boolean GE(Extent extent2) {
    return extent2.LE(this);
  }

  public boolean EQ(Extent extent2) {
    return value == extent2.value;
  }

  public boolean NE(Extent extent2) {
    return !EQ(extent2);
  }
}

