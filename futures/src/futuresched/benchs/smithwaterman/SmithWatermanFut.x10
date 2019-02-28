package futuresched.benchs.smithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;


public class SmithWatermanFut {

  var eFutures: Array_2[AtomicReference[Future[Box[Int]]]];
  var fFutures: Array_2[AtomicReference[Future[Box[Int]]]];
  var mFutures: Array_2[AtomicReference[Future[Box[Int]]]];

  public def init(i: Int, j: Int) {
    eFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1n, j+1n);
    fFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1n, j+1n);
    mFutures = new Array_2[AtomicReference[Future[Box[Int]]]](i+1n, j+1n);
    for(var k: Int = 0n; k < i+1n; k++)
      for(var l: Int = 0n; l < i+1n; l++) {
        eFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
        fFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
        mFutures(k, l) = new AtomicReference[Future[Box[Int]]]();
      }
  }

  // --------------------------------------------------------
  // E function
  private def eFut(i: Int, j: Int): Future[Box[Int]] {
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
          ()=>{ new Box[Int](eFun(i, j)) }
        );
      }
      return nef;
    } else
      return efr.get();
  }

  private def eDeps(i: Int, j: Int): ArrayList[Future[Box[Int]]] {
    val a = new ArrayList[Future[Box[Int]]]();
    for (var k: Int = 0n; k < i; k++)
      a.add(mFut(k, j));
    return a;
  }

  private def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0n;
    for (var k: Int = 0n; k < i; k++) {
      val mv = mFut(k, j).get()();
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def eVal(i: Int, j: Int): Int {
    return eFut(i, j).get()();
  }

  // --------------------------------------------------------
  // F function
  private def fFut(i: Int, j: Int): Future[Box[Int]] {
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
    for (var k: Int = 0n; k < j; k++)
      a.add(mFut(i, k));
    return a;
  }
  
  private def fFun(i: Int, j: Int): Int {
    var maxVal: Int = 0n;
    for (var k: Int = 0n; k < j; k++) {
      val mv = mFut(i, k).get()();
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def fVal(i: Int, j: Int): Int {
    return fFut(i, j).get()();
  }

  // --------------------------------------------------------
  // M function
  public def mFut(i: Int, j: Int): Future[Box[Int]] {
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
    if (i > 0n && j > 0n)
      a.add(mFut(i-1n, j-1n));
    a.add(eFut(i, j));
    a.add(fFut(i, j));
    return a;
  }
  
  private def mFun(i: Int, j: Int): Int {
    var mv: Int = 0n;
    if (i > 0n && j > 0n)
      mv = mFut(i-1n, j-1n).get()();
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eFut(i, j).get()();
    val fv = fFut(i, j).get()();
    
    return Math.max(Math.max(v, ev), fv);
  }
  
  public def mVal(i: Int, j: Int): Int {
    return mFut(i, j).get()();
  }
  
  // --------------------------------------------------------

  public static def seqE(i: Int, j: Int): Int {
    var maxVal: Int = 0n;
    for (var k: Int = 0n; k < i; k++) {
      val mv = seqM(k, j);
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }

  public static def seqF(i: Int, j: Int): Int {
    var maxVal: Int = 0n;
    for (var k: Int = 0n; k < j; k++) {
      val mv = seqM(i, k);
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }

  public static def seqM(i: Int, j: Int): Int {
    var mv: Int = 0n;
    if (i > 0n && j > 0n)
      mv = seqM(i-1n, j-1n);
    val sv = s(i, j);
    val v = mv + sv;
    val ev = seqE(i, j);
    val fv = seqF(i, j);

    return Math.max(Math.max(v, ev), fv);
  }

  public static def gamma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1n;
  }
  
  public static def s(i: Int, j: Int): Int {
    // The function s can be computed in constant time.
    return i+j;
  }

  public static def m(i: Int, j: Int): Int {
    val s = new SmithWatermanFut();
    s.init(i, j);
    return s.mVal(i, j);
  }

}

