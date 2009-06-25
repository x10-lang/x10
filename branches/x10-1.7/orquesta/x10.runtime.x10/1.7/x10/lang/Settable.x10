package x10.lang;
/**
   The compiler accepts the syntax f(p)=e for f a variable of type S
   if S implements Settable[T]{self.base==R} and it can establish
   that p is in the range R, and e is of type T.

   TODO: Figure out how to support various op= operators in Settable.

 */

public interface Settable[Index,Domain,SettableT](base: Domain){Domain <: Contains[Index]} {
    def set(p: Index{self in base}, v: SettableT): void;
}
