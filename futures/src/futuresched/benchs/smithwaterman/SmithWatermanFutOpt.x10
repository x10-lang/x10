package futuresched.benchs.smithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;


public class SmithWatermanFutOpt {

  @x10.compiler.Native("c++", "(x10aux::atomic_ops::compareAndSet_ptr((volatile void**)#A->raw+#p, #oldv, #newv) == #oldv)")
  static native def CAS(A:Rail[IntFuture], p:Long, oldv:IntFuture, newv:IntFuture):Boolean;

  static def CAS(A:Array_2[IntFuture], i:Long, j:Long, oldv:IntFuture, newv:IntFuture) = CAS(A.raw(), j + (i * A.numElems_2), oldv, newv);
	
  val eFutures: Array_2[IntFuture];
  val fFutures: Array_2[IntFuture];
  val mFutures: Array_2[IntFuture];

  public def this(i: Int, j: Int) {
    eFutures = new Array_2[IntFuture](i+1, j+1);
    fFutures = new Array_2[IntFuture](i+1, j+1);
    mFutures = new Array_2[IntFuture](i+1, j+1);
  }

  // --------------------------------------------------------
  // E function
  private def eFut(i: Int, j: Int): IntFuture {
    val ef = eFutures(i, j);
    if (ef != null)
      return ef;
    val nef = new IntFuture();
    if (CAS(eFutures, i, j, null, nef)) {
      async {
        val deps = eDeps(i, j);
        FTask.asyncAnd(
         deps,
         ()=>{ nef.set(eFun(i, j)); }
        );
//        nef.asyncSet(
//          deps,
//          ()=>{ (eFun(i, j)) }
//        );
      }
      return nef;
    } else
      return eFutures(i, j);
  }

  private def eDeps(i: Int, j: Int): ArrayList[IntFuture] {
    val a = new ArrayList[IntFuture]();
    for (var k: Int = 0; k < i; k++)
      a.add(mFut(k, j));
    return a;
  }

  private def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = mFut(k, j).get();
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def eVal(i: Int, j: Int): Int {
    return eFut(i, j).get();
  }

  // --------------------------------------------------------
  // F function
  private def fFut(i: Int, j: Int): IntFuture {
    val ef = fFutures(i, j);
    if (ef != null)
      return ef;
    val nef = new IntFuture();
    if (CAS(fFutures, i, j, null, nef)) {
      async {
        val deps = fDeps(i, j);
        FTask.asyncAnd(
         deps,
         ()=>{ nef.set(fFun(i, j)); }
        );
//        nef.asyncSet(
//          deps,
//          ()=>{(fFun(i,j))}
//        );
      }
      return nef;
    } else
      return fFutures(i, j);
  }
  
  private def fDeps(i: Int, j: Int): ArrayList[IntFuture] {
    val a = new ArrayList[IntFuture]();
    for (var k: Int = 0; k < j; k++)
      a.add(mFut(i, k));
    return a;
  }
  
  private def fFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = mFut(i, k).get();
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def fVal(i: Int, j: Int): Int {
    return fFut(i, j).get();
  }

  // --------------------------------------------------------
  // M function
  public def mFut(i: Int, j: Int): IntFuture {
    val ef = mFutures(i, j);
    if (ef != null)
      return ef;
    val nef = new IntFuture();
    if (CAS(mFutures, i, j, null, nef)) {
      async {
        val deps = mDeps(i, j);
        FTask.asyncAnd(
         deps,
         ()=>{ nef.set(mFun(i, j)); }
        );
//        nef.asyncSet(
//          deps,
//          ()=>{(mFun(i,j))}
//        );
      }
      return nef;
    } else
      return mFutures(i, j);
  }

  private def mDeps(i: Int, j: Int): ArrayList[IntFuture] {
    val a = new ArrayList[IntFuture]();
    if (i > 0 && j > 0)
      a.add(mFut(i-1, j-1));
    a.add(eFut(i, j));
    a.add(fFut(i, j));
    return a;
  }
  
  private def mFun(i: Int, j: Int): Int {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = mFut(i-1, j-1).get();
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eFut(i, j).get();
    val fv = fFut(i, j).get();
    
    return Math.max(Math.max(v, ev), fv);
  }
  
  public def mVal(i: Int, j: Int): Int {
    return mFut(i, j).get();
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

  public static def m(i: Int, j: Int): Int {
    val s = new SmithWatermanFutOpt(i, j);
    return s.mVal(i, j);
  }

}

