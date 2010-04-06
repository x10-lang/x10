import x10.compiler.*;

public class UTS {

  @NativeRep ("c++", "UTS__SHA1Rand", "UTS__SHA1Rand", null)
  @NativeCPPInclude ("sha1_rand.hpp")
  @NativeCPPCompilationUnit ("sha1.c")
  public static struct SHA1Rand {
    public def this (seed:int) { }

    public def this (parent:SHA1Rand, spawn_number:int) { }

    @Native ("c++", "UTS__SHA1Rand_methods::apply(#0)")
    public def apply () : int = 0;
  }

  public static def main (args:Rail[String]!) {
    val RootRNG = SHA1Rand (0);
    for ((i) in 0..9) {
      val ChildRNG = SHA1Rand (RootRNG, i);
      Console.OUT.println (i+" "+ChildRNG());
    }
  }
}
