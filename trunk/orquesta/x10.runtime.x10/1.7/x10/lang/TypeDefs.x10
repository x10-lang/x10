package x10.lang;

abstract class TypeDefs {
    static type Indexable[T](x : region) =Indexable[T]{self.base==x};
    static type Settable[T](x : region) = Settable[T]{self.base==x};
    static type point(x : region) = point{self in x};
    static type nat = int{self >= 0};
    static type nat(high: int) = nat{self <= high};
    static type int(high: int) = int{self <= high};
    static type int(low: int, high: int) = int{low <= self, self <= high};

    static type ValRail[T](x: nat)= ValRail[T]{self.length==x};
    static type Rail[T](x: nat)= Rail[T]{self.length==x};

    static type NativeValRail[T](x: nat)= NativeValRail[T]{self.length==x};
    static type NativeRail[T](x: nat)= NativeRail[T]{self.length==x};

    static type point = ValRail[int];
    static type point(n: nat) = point{self.length==n);
    static type point(r: region) = point{r.contains(self)};
}
