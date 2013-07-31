package futuresched.benchs.swithwaterman;

import x10.util.Pair;

public class SmithWaterman1 {

  var eFutures: ConcurrentHashMap[Pair[Int, Int], Future[Int]]();
  var fFutures: ConcurrentHashMap[Pair[Int, Int], Future[Int]]();
  var mFutures: ConcurrentHashMap[Pair[Int, Int], Future[Int]]();
  
  // --------------------------------------------------------
  // E function
  private def eVal(i, j): Future[Int] {
    val index = new Pair[Int, Int](i, j);
    var ef: Future[Int] = eFutures.get(index);
    if (ef == null) {
      ef = new Future[Int]();
      eFutures.put(index, ef);
      async {
        val deps = eDeps(i, j);
        ef.asyncSet(deps, ()=>{eFun(i,j)});
      }
    }
    return ef;
  }

  private def eDeps(i: Int, j: Int): ArrayList[Future[Int]] {
    val a = new ArrayList[Future[Int]]();
    for (var k: Int = 0; k < i; k++)
      a.add(mVal(k, j));
    return a;
  }

  private def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = mVal(k, j).get();
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def e(i, j): Int {
    return eVal(i, j).get();
  }

  // --------------------------------------------------------
  // F function
  private def fVal(i, j): Future[Int] {
    val index = new Pair[Int, Int](i, j);
    var ff: Future[Int] = fFutures.get(index);
    if (ff == null) {
      ff = new Future[Int]();
      fFutures.put(index, ff);
      async {
        val deps = mDeps(i, j);
        ff.asyncSet(, ()=>{fFun(i,j)});
      }
    }
    return ff;
  }
  
  private def fDeps(i: Int, j: Int): ArrayList[Future[Int]] {
    val a = new ArrayList[Future[Int]]();
    for (var k: Int = 0; k < j; k++)
      a.add(mVal(i, k));
    return a;
  }
  
  private def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = mVal(i, k).get();
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def f(i, j): Int {
    return fVal(i, j).get();
  }

  // --------------------------------------------------------
  // M function
  public def mVal(i, j): SFuture[Int] {
    val index = new Pair[Int, Int](i, j);
    var mf: SFuture[Int] = mFutures.get(index);
    if (mf == null) {
      mf = new SFuture[Int]();
      mFutures.put(index, mf);
      async {
        val deps = mDeps(i, j);
        mf.asyncSet(deps, ()=>{mFun(i,j)});
      }
    }
    return mf;
  }
  
  private def mDeps(i: Int, j: Int): ArrayList[Future[Int]] {
    val a = new ArrayList[Future[Int]]();
    if (i > 0 && j > 0)
      a.add(mVal(i, j));
    a.add(eVal(i, j));
    a.add(fVal(i, j));
    return a;
  }
  
  private def mFun(i: Int, j: Int): Int {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = mVal(i-1, j-1).get();
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eVal(i, j).get();
    val fv = fVal(i, j).get();
    
    return Math.Max(Math.max(v, ev), fv);
  }
  
  public def m(i, j): Int {
    return mVal(i, j).get();
  }
  
  // --------------------------------------------------------
  
  public def gmma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1;
  }
  
  public def s(i: Int): Int {
    // The function s can be computed in constant time.
    return i+2;
  }

  public static def m(i: Int, j: Int): Int {
    val s = new SmithWaterman();
    return s.m(i, j);
  }
}



