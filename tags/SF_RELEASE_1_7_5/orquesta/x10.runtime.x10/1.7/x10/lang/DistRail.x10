package x10.lang;

public class DistRail[T] {
    public incomplete static def make[T](unique: Dist{unique}, f: (Point) => T): DistRail[T];
}
