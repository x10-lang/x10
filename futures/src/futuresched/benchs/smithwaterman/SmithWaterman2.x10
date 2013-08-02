package futuresched.benchs.swithwaterman;

import x10.util.Pair;

public class SmithWaterman2 {

  var eFutures: ConcurrentHashMap[Pair[Int, Int], SFuture[Int]];
  var fFutures: ConcurrentHashMap[Pair[Int, Int], SFuture[Int]];
  var mFutures: ConcurrentHashMap[Pair[Int, Int], SFuture[Int]];
  
  // --------------------------------------------------------
  // E function
  val fire = new SFuture[Boolean]();
  
  public def eDep(i, j): SFuture[Int] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Int] = eFutures.get(index);
    if (ef != null)
      return ef;
    ef = new SFuture[Int]();
    eFutures.put(index, ef);
    async {
      val deps = new ArrayList[SFuture[Int]]();
      for (var k: Int = 0; k < i; k++)
        deps.add(mDep(k, j));
      val fTask = FTask.newFTask(deps, ()=>{ ef.set(eFun(i,j)) });
      if (deps.size() == 0)
        fire.register(()=> { fTask.now() });
    }
    return ef;
  }
  
  public def eVal(i, j): SFuture[Int] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Int] = eFutures.get(index);
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
  public def fDeps(i: Int, j: Int): ArrayList[SFuture[Int]] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Int] = eFutures.get(index);
    if (ef != null)
      return ef;
    ef = new SFuture[Int]();
    eFutures.put(index, ef);
    async {
      //val deps = eDeps(i, j);
      val deps = new ArrayList[SFuture[Int]]();
      for (var k: Int = 0; k < j; k++) {
        deps.add(mVal(i, k));
      val fTask = new FTask(deps, ()=>{ ef.set(eFun(i,j)) });
      if (deps.size() == 0)
        fire.register(()=> { fTask.now() });
    }
    return ef;
  }

  public def fVal(i: Int, j: Int): SFuture[Int] {
    val index = new Pair[Int, Int](i, j);
    var ff: SFuture[Int] = fFutures.get(index);
    return ff;
  }
  
  public def eFun(i: Int, j: Int): Int {
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
  
  public def mDeps(i: Int, j: Int): ArrayList[SFuture[Int]] {
    val index = new Pair[Int, Int](i, j);
    var ef: SFuture[Int] = eFutures.get(index);
    if (ef != null)
      return ef;
    ef = new SFuture[Int]();
    eFutures.put(index, ef);
    async {
      //val deps = eDeps(i, j);
      val deps = new ArrayList[SFuture[Int]]();
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

  public def mVal(i, j): SFuture[Int] {
    val index = new Pair[Int, Int](i, j);
    var mf: SFuture[Int] = mFutures.get(index);
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
  
  public def m(i, j): Int {
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
  
  public def s(i: Int): Int {
    // The function s can be computed in constant time.
    return i+2;
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
  
  public static def m(i: Int, j: Int): Int {
    val s = new SmithWaterman2();
    s.init(i, j);
    s.backward(i, j);
    s.forward();
    return s.m(i, j);
  }   
}



