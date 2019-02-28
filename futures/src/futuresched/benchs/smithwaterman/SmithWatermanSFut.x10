package futuresched.benchs.smithwaterman;

import x10.util.Box;
import futuresched.core.*;
import x10.array.Array_2;
import x10.util.ArrayList;
import x10.util.concurrent.AtomicReference;


public class SmithWatermanSFut {

  var eFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];
  var fFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];
  var mFutures: Array_2[AtomicReference[SFuture[Box[Int]]]];

  public def init(i: Int, j: Int) {
    eFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1n, j+1n);
    fFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1n, j+1n);
    mFutures = new Array_2[AtomicReference[SFuture[Box[Int]]]](i+1n, j+1n);
    for(var k: Int = 0n; k < i+1n; k++)
      for(var l: Int = 0n; l < i+1n; l++) {
        eFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
        fFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
        mFutures(k, l) = new AtomicReference[SFuture[Box[Int]]]();
      }
  }

  // --------------------------------------------------------
  // E function
  val fire = new SFuture[Box[Int]]();
  
  public def eFut(val i: Int, val j: Int): SFuture[Box[Int]] {
    var efr: AtomicReference[SFuture[Box[Int]]] = eFutures(i, j);
    var ef: SFuture[Box[Int]] = efr.get();
    if (ef != null)
      return ef;
    val nef = new SFuture[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = eDeps(i, j);
        if (deps.isEmpty())
          deps.add(fire);
        FTask.newAnd(
          deps,
          ()=>{
//               Console.OUT.println("Launching e(" + i + ", " + j +  ")");
             nef.set(new Box[Int](eFun(i,j)));
          }
        );
//        if (deps.isEmpty())
//          FTask.sAsyncWait(fire, ()=>{
//            fTask.now();
//          });
      }
      return nef;
    }
    return ef;
  }

  private def eDeps(i: Int, j: Int): ArrayList[SFuture[Box[Int]]] {
    val a = new ArrayList[SFuture[Box[Int]]]();
    for (var k: Int = 0n; k < i; k++)
      a.add(mFut(k, j));
    return a;
  }

  public def eFun(i: Int, j: Int): Int {
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
  public def fFut(i: Int, j: Int): SFuture[Box[Int]] {
    var efr: AtomicReference[SFuture[Box[Int]]] = fFutures(i, j);
    var ef: SFuture[Box[Int]] = efr.get();
    if (ef != null)
      return ef;
    val nef = new SFuture[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = fDeps(i, j);
        if (deps.isEmpty())
          deps.add(fire);
        FTask.newAnd(
          deps,
          ()=>{
//            Console.OUT.println("Launching f(" + i + ", " + j +  ")");
            nef.set(new Box[Int](fFun(i,j)));
          }
        );
//        if (deps.isEmpty())
//          FTask.sAsyncWait(fire, ()=>{
//            fTask.now();
//          });
      }
      return nef;
    }
    return ef;
  }

  private def fDeps(i: Int, j: Int): ArrayList[SFuture[Box[Int]]] {
    val a = new ArrayList[SFuture[Box[Int]]]();
    for (var k: Int = 0n; k < j; k++)
      a.add(mFut(i, k));
    return a;
  }
  
  public def fFun(i: Int, j: Int): Int {
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
  public def mFut(i: Int, j: Int): SFuture[Box[Int]] {
    var efr: AtomicReference[SFuture[Box[Int]]] = mFutures(i, j);
    var ef: SFuture[Box[Int]] = efr.get();
    if (ef != null)
      return ef;
    val nef = new SFuture[Box[Int]]();
    if (efr.compareAndSet(null, nef)) {
      async {
        val deps = mDeps(i, j);
        if (deps.isEmpty())
          deps.add(fire);
        FTask.newAnd(
          deps,
          ()=>{
//            Console.OUT.println("Launching m(" + i + ", " + j +  ")");
            nef.set(new Box[Int](mFun(i,j)));
          }
        );
//        if (deps.isEmpty())
//          FTask.sAsyncWait(fire, ()=>{
//            fTask.now();
//          });
      }
      return nef;
    }
    return ef;
  }

  private def mDeps(i: Int, j: Int): ArrayList[SFuture[Box[Int]]] {
    val a = new ArrayList[SFuture[Box[Int]]]();
    if (i > 0 && j > 0)
      a.add(mFut(i-1n, j-1n));
    a.add(eFut(i, j));
    a.add(fFut(i, j));
    return a;
  }
  
  public def mFun(i: Int, j: Int): Int {
    var mv: Int = 0n;
    if (i > 0 && j > 0)
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

  public static def gamma(i: Int): Int {
    // The function gamma can be computed in constant time.
    return i+1n;
  }
  
  public static def s(i: Int, j: Int): Int {
    // The function s can be computed in constant time.
    return i+j;
  }

  // --------------------------------------------------------
  public def buildFutureGraph(i: Int, j: Int) {
    finish {
      mFut(i, j);
    }
//    Console.OUT.println("End of backward.");
  }

  public def forward() {
     finish {
        fire.set(new Box[Int](1n));
     }
//    Console.OUT.println("Setting fire.");
  }

  // --------------------------------------------------------

   public static def m(i: Int, j: Int): Int {
      val s = new SmithWatermanSFut();
      s.init(i, j);
      s.buildFutureGraph(i, j);
//    val f = s.mFut(i, j);
      s.forward();

//    return f.fireAndGet(() => {
//      s.forward();
//    })();
      return s.mVal(i, j);
   }
}

