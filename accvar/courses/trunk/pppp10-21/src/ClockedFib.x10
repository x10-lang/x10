public class ClockedFib {
    public static def main(s:Array[String](1)) {
        val N:Int = s.size > 0 ? Int.parseInt(s(0)) : 1000;
        finish async {
            val x = new Clocked[Int](1);
            val y = new Clocked[Int](1);
            async clocked(x.clock, y.clock) {
                while (y() < N) {
                    x() = x() + y();
                    x.next();
                    y.next();
                }
                x() = (Int.MAX_VALUE);
            }
            async clocked(x.clock, y.clock) {
                while (x() < N) {
                    y() = x();
                    Console.OUT.print(" " +  x());
                    x.next();
                    y.next();
                }
                y() = (Int.MAX_VALUE);
                Console.OUT.println();
            }
        }
    }
}