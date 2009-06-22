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
 * The word type is used by the runtime system and collector to denote machine
 * word-sized quantities.
 * We use a separate type instead of the Java int type for coding clarity.
 * machine-portability (it can map to 32 bit and 64 bit integral types),
 * and access to unsigned operations (Java does not have unsigned int types).
 * <p>
 * For efficiency and to avoid meta-circularity, the Word class is intercepted like
 * magic and converted into the base type so no Word object is created run-time.
 *
 * @see Address
 */
public final class Word {
  
  private long value;
  
  Word(int value, boolean zeroExtend) {
    this.value = (zeroExtend) ? ((long)value) & 0x00000000ffffffffL : value;
  }

  Word(int value) {
    this (value, false);
  }

  Word(long value) {
    this.value = value;
  }

  public boolean equals(Object o) {
    return (o instanceof Word) && ((Word) o).value == value;
  }

  public static Word fromIntSignExtend(int val) {
    return new Word(val);
  }

  public static Word fromIntZeroExtend(int val) {
    return new Word(val, true);
  }

  public static Word fromLong(long val) {
    return new Word(val);
  }

  public static Word zero() {
    return new Word(0);
  }

  public static Word one() {
    return new Word(1);
  }

  public static Word max() {
    return fromIntSignExtend(-1);
  }

  public int toInt() {
    return (int) value;
  }

  public long toLong() {
    return value;
  }

  public Address toAddress() {
    return new Address(value);
  }

  public Offset toOffset() {
    return new Offset(value);
  }

  public Extent toExtent() {
    return new Extent(value);
  }

  public Word plus(Word w2) {
    return new Word(value + w2.value);
  }

  public Word plus(Offset w2) {
    return new Word(value + w2.toWord().value);
  }

  public Word plus(Extent w2) {
    return new Word(value + w2.toWord().value);
  }

  public Word minus(Word w2) {
    return new Word(value - w2.value);
  }

  public Word minus(Offset w2) {
    return new Word(value - w2.toWord().value);
  }
  public Word minus(Extent w2) {
    return new Word(value - w2.toWord().value);
  }

  public boolean isZero() {
    return EQ(zero());
  }

  public boolean isMax() {
    return EQ(max());
  }

  public boolean LT(Word addr2) {
    if (value >= 0 && addr2.value >= 0) return value < addr2.value;
    if (value < 0 && addr2.value < 0) return value < addr2.value;
    if (value < 0) return true;
    return false;
  }

  public boolean LE(Word w2) {
    return (value == w2.value) || LT(w2);
  }

  public boolean GT(Word w2) {
    return w2.LT(this);
  }

  public boolean GE(Word w2) {
    return w2.LE(this);
  }

  public boolean EQ(Word w2) {
    return value == w2.value;
  }

  public boolean NE(Word w2) {
    return !EQ(w2);
  }

  public Word and(Word w2) {
    return new Word(value & w2.value);
  }

  public Word or(Word w2) {
    return new Word(value | w2.value);
  }

  public Word not() {
    return new Word(~value);
  }

  public Word xor(Word w2) {
    return new Word(value ^ w2.value);
  }

  public Word lsh(int amt) {
    return new Word(value << amt);
  }

  public Word rshl(int amt) {
    return new Word(value >>> amt);
  }

  public Word rsha(int amt) {
    return new Word(value >> amt);
  }
}
