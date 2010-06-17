public class GenericClass[T]{T <: Coord} {
    public def doStuff(t: T) {
        val x = t.x;
        val y = t.y;
        val z = t.z;
        doStuff2(t);
    }
    private def doStuff2(c: Coord) {
    }
}
