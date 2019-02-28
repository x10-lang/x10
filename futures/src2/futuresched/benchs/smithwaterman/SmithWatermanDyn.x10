package futuresched.benchs.smithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;


public class SmithWatermanDyn {

  var eArray: Array_2[Int];
  var fArray: Array_2[Int];
  var mArray: Array_2[Int];

  public def init(i: Int, j: Int) {
    eArray = new Array_2[Int](i+1, j+1);
    fArray = new Array_2[Int](i+1, j+1);
    mArray = new Array_2[Int](i+1, j+1);
  }

  public def setE(i: Int, j: Int) {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = mArray(k, j);
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    eArray(i, j) = maxVal;
  }

  public def setF(i: Int, j: Int) {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = mArray(i, k);
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    fArray(i, j) = maxVal;
  }

  public def setM(i: Int, j: Int) {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = mArray(i-1, j-1);
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eArray(i, j);
    val fv = fArray(i, j);

    mArray(i, j) = Math.max(Math.max(v, ev), fv);
  }


  public def mVal(i: Int, j: Int): Int {
    for (var k: Int = 0; k < i+1; k++)
      for (var l: Int = 0; l < i+1; l++) {
        setE(k, l);
        setF(k, l);
        setM(k, l);
      }
    return mArray(i, j);
  }

  public static def gamma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1;
  }
  
  public static def s(i: Int, j: Int): Int {
    // The function s can be computed in constant time.
    return i+j;
  }

  public static def m(i: Int, j: Int): Int {
    val s = new SmithWatermanDyn();
    s.init(i, j);
    return s.mVal(i, j);
  }

}

