/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package moldyn;

public class Random {

  public var iseed: Int;
  public var v1: Double, v2: Double;

  public def this(iseed: Int, v1: Double, v2: Double) {
    this.iseed = iseed;
    this.v1 = v1;
    this.v2 = v2;
  }

  public def update() {
    val rand: Double;
    val scale = 4.656612875e-10;

    var is1: Int, is2: Int, iss2: Int;
    val imult = 16807;
    val imod = 2147483647;

    if (iseed <= 0) { iseed = 1; }

    is2 = iseed % 32768;
    is1 = (iseed-is2)/32768;
    iss2 = is2 * imult;
    is2 = iss2 % 32768;
    is1 = (is1*imult+(iss2-is2)/32768) % (65536);

    iseed = (is1*32768+is2) % imod;

    rand = scale * iseed;

    return rand;
  }

  public def seed() {
    var s: Double;
    val r: Double;

    s = 1.0;
    do {
      val u1: Double, u2: Double;
      u1 = update();
      u2 = update();

      v1 = 2.0 * u1 - 1.0;
      v2 = 2.0 * u2 - 1.0;
      s = v1*v1 + v2*v2;
    } while (s >= 1.0);

    r = Math.sqrt(-2.0*Math.log(s)/s);

    return r;
  }
}
