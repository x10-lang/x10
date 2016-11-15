/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import x10.interop.Java;
import x10.util.resilient.localstore.Cloneable;

final class UTS implements Cloneable {
  static den = Math.log(4.0 / (1.0 + 4.0));

  static def encoder():MessageDigest {
    try {
      return MessageDigest.getInstance("SHA-1");
    } catch (val e:NoSuchAlgorithmException) {
      Console.ERR.println("Could not initialize a MessageDigest for the \"SHA-1\" algorithm");
      e.printStackTrace();
    }
    return null;
  }

  var hash:Rail[Byte];
  var depth:Rail[Int];
  var lower:Rail[Int];
  var upper:Rail[Int];
  var size:Int;
  var count:Long;
  
  def this() {}

  def this(n:Int) {
    this.hash = new Rail[Byte](n * 20n + 4n); // slack for in-place SHA1 computation
    this.depth = new Rail[Int](n);
    this.lower = new Rail[Int](n);
    this.upper = new Rail[Int](n);
  }

  private def digest(md:MessageDigest, d:Int) throws DigestException {
    if (size >= depth.size) {
      grow();
    }
    ++count;
    val offset = size * 20n;
    md.digest(Java.convert(hash), offset, 20n);
    val v = ((0x7fn & hash(offset + 16n)) << 24n)
    | ((0xffn & hash(offset + 17n)) << 16n)
    | ((0xffn & hash(offset + 18n)) << 8n) | (0xffn & hash(offset + 19n));
    val n =  (Math.log(1.0 - v / 2147483648.0) / den) as Int;
    if (n > 0n) {
      if (d > 1n) {
        depth(size) = d - 1n;
        lower(size) = 0n;
        upper(size++) = n;
      } else {
        count += n;
      }
    }
  }

  def seed(md:MessageDigest, s:Int, d:Int) {
    try {
      for (var i:Int = 0n; i < 16n; ++i) {
        hash(i) = 0 as Byte;
      }
      hash(16n) = (s >> 24n) as Byte;
      hash(17n) = (s >> 16n) as Byte;
      hash(18n) = (s >> 8n) as Byte;
      hash(19n) = s as Byte;
      md.update(Java.convert(hash), 0n, 20n);
      digest(md, d);
    } catch (DigestException) {
    }
  }

  def expand(md:MessageDigest) throws DigestException {
    val top = size - 1n;
    val d = depth(top);
    val l = lower(top);
    val u = upper(top) - 1n;
    if (u == l) {
      size = top;
    } else {
      upper(top) = u;
    }
    val offset = top * 20n;
    hash(offset + 20n) = (u >> 24n) as Byte;
    hash(offset + 21n) = (u >> 16n) as Byte;
    hash(offset + 22n) = (u >> 8n) as Byte;
    hash(offset + 23n) = u as Byte;
    md.update(Java.convert(hash), offset, 24n);
    digest(md, d);
  }

  def run(md:MessageDigest) {
    try {
      while (size > 0n) {
        expand(md);
      }
    } catch (DigestException) {
    }
  }

  def trim():UTS {
    val b:UTS;
    if (size == 0n) {
      b = new UTS();
    } else {
      b = new UTS(size);
      Rail.copy(hash, 0, b.hash, 0, size * 20);
      Rail.copy(depth, 0, b.depth, 0, size as Long);
      Rail.copy(lower, 0, b.lower, 0, size as Long);
      Rail.copy(upper, 0, b.upper, 0, size as Long);
      b.size = size;
    }
    b.count = count;
    return b;
  }

  def split():UTS {
    var s:Int = 0n;
    for (var i:Int = 0n; i < size; ++i) {
      if ((upper(i) - lower(i)) >= 2n) {
        ++s;
      }
    }
    if (s == 0n) {
      return null;
    }
    val b:UTS = new UTS(s);
    for (var i:Int = 0n; i < size; ++i) {
      val p = upper(i) - lower(i);
      if (p >= 2n) {
        Rail.copy(hash, i * 20, b.hash, b.size * 20, 20);
        b.depth(b.size) = depth(i);
        b.upper(b.size) = upper(i);
        upper(i) -= p / 2n;
        b.lower(b.size++) = upper(i);
      }
    }
    return b;
  }

  def merge(b:UTS) {
    val s = size + b.size;
    while (s > depth.size) grow();
    Rail.copy(b.hash, 0, hash, size * 20, b.size * 20);
    Rail.copy(b.depth, 0, depth, size as Long, b.size as Long);
    Rail.copy(b.lower, 0, lower, size as Long, b.size as Long);
    Rail.copy(b.upper, 0, upper, size as Long, b.size as Long);
    size = s;
  }
  
  def grow() {
    val n = depth.size * 2n;
    val h = new Rail[Byte](n * 20n + 4n);
    val d = new Rail[Int](n);
    val l = new Rail[Int](n);
    val u = new Rail[Int](n);
    Rail.copy(hash, 0, h, 0, size * 20);
    Rail.copy(depth, 0, d, 0, size as Long);
    Rail.copy(lower, 0, l, 0, size as Long);
    Rail.copy(upper, 0, u, 0, size as Long);
    hash = h;
    depth = d;
    lower = l;
    upper = u;
  }

  public def clone() {
      return trim();
  }

  static def sub(str:String, start:Int, end:Int):String {
    return str.substring(start, Math.min(end, str.length()));
  }

  public static def main(args:Rail[String]) {
    var depth:Int = 13n;
    if (args.size > 0) depth = Int.parseInt(args(0));

    val md = encoder();

    var bag:UTS = new UTS(64n);

    Console.OUT.println("Warmup...");
    bag.seed(md, 19n, depth - 2n);
    bag.run(md);

    bag = new UTS(64n);

    Console.OUT.println("Starting...");
    var time:Long = -System.nanoTime();

    bag.seed(md, 19n, depth);
    bag.run(md);

    time += System.nanoTime();
    Console.OUT.println("Finished.");

    val count = bag.count;
    Console.OUT.println("Depth: " + depth + ", Performance: " + count + "/"
        + sub("" + time / 1e9, 0n, 6n) + " = "
        + sub("" + (count / (time / 1e3)), 0n, 6n) + "M nodes/s");
  }
}
