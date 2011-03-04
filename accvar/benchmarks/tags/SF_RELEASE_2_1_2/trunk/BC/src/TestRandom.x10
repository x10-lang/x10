import x10.util.Random;

public final class TestRandom {
  public static def main (Array[String](1)) {
    val rng = new Random(2L);

    for (var i:Int=0; i<10; ++i)
      Console.OUT.println (rng.nextDouble());
  }
}
