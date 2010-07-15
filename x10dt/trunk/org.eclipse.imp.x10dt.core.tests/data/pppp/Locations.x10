public interface Locations[T] {
    def read[T](x:Set[T]):Effects;
    def write[T](x:Set[T]):Effects;
    def touch[T](x:Set[T]):Effects;
    def and(x:Effects):Effects;
}
