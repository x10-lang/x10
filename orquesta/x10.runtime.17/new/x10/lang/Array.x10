package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.Native;
import x10.compiler.NativeRep;

// note: use dist.region.rank, not rank -- only the properties in the
// header are in scope

@NativeRep("java", "x10.array.Array")
public abstract value class Array[T](dist: Dist) implements
    Indexable[Point(dist.region.rank),T],
    Settable[Point(dist.region.rank),T],
    ArithmeticOps[Array[T]]
{
    // region via dist
    property region: Region = dist.region;
    property rank: int = dist.rank;
    property rect: boolean = dist.rect;
    property zeroBased: boolean = dist.zeroBased;
    
    // dist
    property rail: boolean = dist.rail;
    property unique: boolean = dist.unique;
    property constant: boolean = dist.constant;
    property onePlace: Place = dist.onePlace;

    @Native("java", "x10.array.ArrayFactory.make(#1, #2)")
    native public static def make[T](dist: Dist, init: Indexable[nat,T]): Array[T];
    
    @Native("java", "x10.array.ArrayFactory.makeLocal(#1, #2)")
    native public static def make[T](region: Region, init: Indexable[nat,T]): Array[T];
    
    // @Native("java", "x10.array.ArrayFactory.make(#1, #2)")
    // native public static def make[T](region: Region, init: Indexable[nat,T], value: boolean): Array[T];
    
    @Native("java", "x10.array.ArrayFactory.makeFromRail(#1, #2)")
    native public static def make[T](r: Rail[T]): Array[T]{rank==1};
    
    @Native("java", "x10.array.ArrayFactory.makeFromValRail(#1, #2)")
    native public static def make[T](r: ValRail[T]): Array[T]{rank==1};
    
    @Native("java", "(#0).restriction(#1)")
    public native def restriction(r: Region): Array[T];
    
    @Native("java", "(#0).get(#1)")
    public native def apply(pt: Point(this.rank)): T;
    
    @Native("java", "(#0).get(#1)")
    public native def get(pt: Point(this.rank)): T;
    @Native("java", "(#0).get(#1)")
    public native def get(i0: int){rank==1}: T;
    @Native("java", "(#0).get(#1, #2)")
    public native def get(i0: int, i1: int){rank==2}: T;
    @Native("java", "(#0).get(#1, #2, #3)")
    public native def get(i0: int, i1: int, i2: int){rank==3}: T;
                     
    @Native("java", "(#0).set(#1, #2)")
    public native def set(pt: Point(this.rank), v: T): void;
    @Native("java", "(#0).set(#1, #2)")
    public native def set(i0: int, v: T){rank==1}: void;
    @Native("java", "(#0).set(#1, #2, #3)")
    public native def set(i0: int, i1: int, v: T){rank==2}: void;
    @Native("java", "(#0).set(#1, #2, #3, #4)")
    public native def set(i0: int, i1: int, i2: int, v: T){rank==3}: void;
                     
    @Native("java", "(#0)")
    native public def $plus(): Array[T];
    @Native("java", "(#0).neg()")
    native public def $minus(): Array[T];

    @Native("java", "(#0).add(#1)")
    native public def $plus(that: Array[T]): Array[T];
    
    @Native("java", "(#0).sub(#1)")
    native public def $minus(that: Array[T]): Array[T];
    
    @Native("java", "(#0).mul(#1)")
    native public def $times(that: Array[T]): Array[T];
    
    @Native("java", "(#0).div(#1)")
    native public def $over(that: Array[T]): Array[T];

    @Native("java", "(#0).restriction(#1)")
    native public def $bar(r: Region): Array[T];
    
    @Native("java", "(#0).restriction(#1)")
    native public def $bar(p: Place): Array[T];

    @Native("java", "x10.array.ArrayFactory.makeFromRail(#1)")
    native public static def $convert[T](r: Rail[T]): Array[T];
    
    @Native("java", "x10.array.ArrayFactory.makeFromValRail(#1)")
    native public static def $convert[T](r: ValRail[T]): Array[T];    

    //
    // private/protected
    //

    protected def this(dist: Dist) = {
        property(dist);
    }
}
