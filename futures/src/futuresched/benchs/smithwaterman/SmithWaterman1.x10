package futuresched.benchs.smithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;

public class SmithWaterman1 {

  var eFutures: Array_2[AtomicReference[Future[Box[Int]]]];
  var fFutures: Array_2[AtomicReference[Future[Box[Int]]]];
  var mFutures: Array_2[AtomicReference[Future[Box[Int]]]];

  public def init(i: Int, j: Int) {
    eFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1, j+1);
    fFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1, j+1);
    mFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1, j+1);
    for(var k: Int = 0; k < i+1; k++)
      for(var l: Int = 0; l < i+1; l++) {
        eFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
        fFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
        mFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
      }
  }

  // --------------------------------------------------------
  // E function
  private def eVal(i: Int, j: Int): Future[Box[Int]] {
    var efr: AtomicReference[Future[Box[Int]]] = eFutures(i, j);
    var ef: Future[Box[Int]] = efr.get();
    if (ef != null)
      return ef;
    val nef = new Future[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = eDeps(i, j);
        nef.asyncSet(
          deps,
          ()=>{new Box[Int](eFun(i, j))}
        );
      }
      return nef;
    } else
      return efr.get();
  }

  private def eDeps(i: Int, j: Int): ArrayList[Future[Box[Int]]] {
    val a = new ArrayList[Future[Box[Int]]]();
    for (var k: Int = 0; k < i; k++)
      a.add(mVal(k, j));
    return a;
  }

  private def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = mVal(k, j).get()();
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def e(i: Int, j: Int): Int {
    return eVal(i, j).get()();
  }

  // --------------------------------------------------------
  // F function
  private def fVal(i: Int, j: Int): Future[Box[Int]] {
    var efr: AtomicReference[Future[Box[Int]]] = fFutures(i, j);
    val ef = efr.get();
    if (ef != null)
      return ef;
    val nef = new Future[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = fDeps(i, j);
        nef.asyncSet(
          deps,
          ()=>{new Box[Int](fFun(i,j))}
        );
      }
      return nef;
    } else
      return efr.get();
  }
  
  private def fDeps(i: Int, j: Int): ArrayList[Future[Box[Int]]] {
    val a = new ArrayList[Future[Box[Int]]]();
    for (var k: Int = 0; k < j; k++)
      a.add(mVal(i, k));
    return a;
  }
  
  private def fFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = mVal(i, k).get()();
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def f(i: Int, j: Int): Int {
    return fVal(i, j).get()();
  }

  // --------------------------------------------------------
  // M function
  public def mVal(i: Int, j: Int): Future[Box[Int]] {
    var efr: AtomicReference[Future[Box[Int]]] = mFutures(i, j);
    val ef = efr.get();
    if (ef != null)
      return ef;
    val nef = new Future[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = mDeps(i, j);
        nef.asyncSet(
          deps,
          ()=>{new Box[Int](mFun(i,j))}
        );
      }
      return nef;
    } else
      return efr.get();
  }

  private def mDeps(i: Int, j: Int): ArrayList[Future[Box[Int]]] {
    val a = new ArrayList[Future[Box[Int]]]();
    if (i > 0 && j > 0)
      a.add(mVal(i-1, j-1));
    a.add(eVal(i, j));
    a.add(fVal(i, j));
    return a;
  }
  
  private def mFun(i: Int, j: Int): Int {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = mVal(i-1, j-1).get()();
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eVal(i, j).get()();
    val fv = fVal(i, j).get()();
    
    return Math.max(Math.max(v, ev), fv);
  }
  
  public def m(i: Int, j: Int): Int {
    return mVal(i, j).get()();
  }
  
  // --------------------------------------------------------

  public static def seqE(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = seqM(k, j);
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }

  public static def seqF(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = seqM(i, k);
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }

  public static def seqM(i: Int, j: Int): Int {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = seqM(i-1, j-1);
    val sv = s(i, j);
    val v = mv + sv;
    val ev = seqE(i, j);
    val fv = seqF(i, j);

    return Math.max(Math.max(v, ev), fv);
  }

  public static def gamma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1;
  }
  
  public static def s(i: Int, j: Int): Int {
    // The function s can be computed in constant time.
    return i+j;
  }

  public static def futureM(i: Int, j: Int): Int {
    val s = new SmithWaterman1();
    s.init(i, j);
    return s.m(i, j);
  }

}


