package futuresched.benchs.swithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;


public class SmithWaterman2 {

  var eFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];
  var fFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];
  var mFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];

  public def init(i: Int, j: Int) {
    eFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1, j+1);
    fFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1, j+1);
    mFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1, j+1);
    for(var k: Int = 0; k < i+1; k++)
      for(var l: Int = 0; l < i+1; l++) {
        eFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
        fFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
        mFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
      }
  }
  
  // --------------------------------------------------------
  // E function
  val fire = new SFuture[Box[Boolean]]();
  
  public def eDeps(i: Int, j: Int): SFuture[Box[Int]] {
    var efr: AtomicReference[SFuture[Box[Int]]] = eFutures(i, j);
    var ef: SFuture[Box[Int]] = efr.get();
    if (ef != null)
      return ef;
    val nef = new SFuture[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = mDeps(i, j);
        val fTask = FTask.newFTask(deps, ()=>{ nef.set(eFun(i,j)) });
        if (deps.size() == 0)
          fire.register(()=> { fTask.now() });
      }
      return ef;
    }
    return ef;
  }
  
  public def eVal(i: Int, j: Int): SFuture[Box[Int]] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Box[Int]] = eFutures.get(index);
    return ef;
  }
  
  public def eFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < i; k++) {
      val mv = mVal(k, j).get();
      val gv = gamma(i - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }

  public def e(i: Int, j: Int): Int {
    return eVal(i, j).get();
  }

  // --------------------------------------------------------
  // F function
  public def fDeps(i: Int, j: Int): ArrayList[SFuture[Box[Int]]] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Box[Int]] = eFutures.get(index);
    if (ef != null)
      return ef;
    ef = new SFuture[Box[Int]]();
    eFutures.put(index, ef);
    async {
      //val deps = eDeps(i, j);
      val deps = deps.add(mVal(i, k));
      val fTask = new FTask(deps, ()=>{ ef.set(eFun(i,j)) });
      if (deps.size() == 0)
        fire.register(()=> { fTask.now() });
    }
    return ef;
  }

  public def fVal(i: Int, j: Int): SFuture[Box[Int]] {
    val index = new Pair[Int, Int](i, j);
    var ff: SFuture[Box[Int]] = fFutures.get(index);
    return ff;
  }
  
  public def fFun(i: Int, j: Int): Int {
    var maxVal: Int = 0;
    for (var k: Int = 0; k < j; k++) {
      val mv = mVal(i, k).get();
      val gv = gamma(j - k);
      val cv = mv + gv;
      maxVal = Math.max(maxVal, cv);
    }
    return maxVal;
  }
  
  public def f(i: Int, j: Int): Int {
    return fVal(i, j).get();
  }

  // --------------------------------------------------------
  // M function
  
  public def mDeps(i: Int, j: Int): ArrayList[SFuture[Box[Int]]] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Box[Int]] = eFutures.get(index);
    if (ef != null)
      return ef;
    ef = new SFuture[Box[Int]]();
    eFutures.put(index, ef);
    async {
      //val deps = eDeps(i, j);
      val deps = new ArrayList[SFuture[Box[Int]]]();
      if (i > 0 && j > 0)
        deps.add(mVal(i, j));
      deps.add(eVal(i, j));
      deps.add(fVal(i, j));
      val fTask = new FTask(deps, ()=>{ ef.set(eFun(i,j)) });
      if (deps.size() == 0)
        fire.register(()=> { fTask.now() });
    }
    return ef;
  }

  public def mVal(i: Int, j: Int): SFuture[Box[Int]] {
    val index = new Pair[Int, Int](i, j);
    var mf: SFuture[Box[Int]] = mFutures.get(index);
    return mf;
  }
  
  public def mFun(i: Int, j: Int): Int {
    var mv: Int = 0;
    if (i > 0 && j > 0)
      mv = mVal(i-1, j-1).get();
    val sv = s(i, j);
    val v = mv + sv;
    val ev = eVal(i, j).get();
    val fv = fVal(i, j).get();
    
    return Math.Max(Math.max(v, ev), fv);
  }
  
  public def m(i: Int, j: Int): Int {
    return mVal(i, j).get();
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

  public def gmma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1;
  }
  
  public def s(i: Int, j: Int): Int {
    // The function s can be computed in constant time.
    return i+j;
  }

  // --------------------------------------------------------
  public def backward(i: Int, j: Int) {
    finish {
      mDep(i, j);
    }
  }
  
  public def forward() {
    fire.set(true);
  }
  
  // --------------------------------------------------------
  
  public static def futureM(i: Int, j: Int): Int {
    val s = new SmithWaterman2();
    s.init(i, j);
    s.backward(i, j);
    s.forward();
    return s.m(i, j);
  }   
}



