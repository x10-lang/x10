package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.ComparisonOps;
import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.array.Point")
public final value class Point(rank: nat) implements
    Indexable[nat,int],
    ArithmeticOps[Point(this.rank)],
    ComparisonOps[Point(this.rank)]
{
    @Native("java", "(#0).get(#1)")
    public native def apply(i: nat): int;

    @Native("java", "(#0).coordsRail()")
    public native def coords(): ValRail[int];

    //
    // factories
    //

    @Native("java", "x10.array.Point.makeFromValRail(#1)")
    public native static def make(cs: ValRail[int]): Point(cs.length);

    @Native("java", "x10.array.Point.makeFromVarRail(#1)")
    public native static def make(cs: Rail[int]): Point(cs.length);

    @Native("java", "x10.array.Point.makeConstant(#1, #2)")
    public native static def makeConstant(rank: nat, c: int): Point(rank);

    @Native("java", "x10.array.Point.makeZero(#1)")
    public native static def makeZero(rank: nat): Point(rank);

    @Native("java", "x10.array.Point.makeFromVarArgs()")
    public native static def makeRank0(): Point(0);

    @Native("java", "x10.array.Point.makeFromVarArgs(#1)")
    public native static def makeRank1(i: int): Point(1);
    
    @Native("java", "x10.array.Point.makeFromVarArgs(#1, #2)")
    public native static def makeRank2(i: int, j: int): Point(2);
    
    @Native("java", "x10.array.Point.makeFromVarArgs(#1, #2, #3)")
    public native static def makeRank3(i: int, j: int, k: int): Point(3);
    
    @Native("java", "x10.array.Point.makeFromVarArgs(#1, #2, #3, #4)")
    public native static def makeRank4(i: int, j: int, k: int, l: int): Point(4);

    //
    // operations
    //

    @Native("java", "#0")
    native public def $plus(): Point(rank);
    
    @Native("java", "(#0).neg()")
    native public def $minus(): Point(rank);

    @Native("java", "(#0).add(#1)")
    native public def $plus(that: Point(rank)): Point(rank);
    @Native("java", "(#0).sub(#1)")
    native public def $minus(that: Point(rank)): Point(rank);
    @Native("java", "(#0).mul(#1)")
    native public def $times(that: Point(rank)): Point(rank);
    @Native("java", "(#0).div(#1)")
    native public def $over(that: Point(rank)): Point(rank);
    @Native("java", "(#0).mod(#1)")
    native public def $percent(that: Point(rank)): Point(rank);

    @Native("java", "(#0).equals(#1)	")
    native public def $eq(that: Point(rank)): boolean;
    @Native("java", "(! (#0).equals(#1))")
    native public def $ne(that: Point(rank)): boolean;
    
    @Native("java", "(#0).lt(#1)")
    native public def $lt(that: Point(rank)): boolean;
    @Native("java", "(#0).gt(#1)")
    native public def $gt(that: Point(rank)): boolean;
    @Native("java", "(#0).le(#1)")
    native public def $le(that: Point(rank)): boolean;
    @Native("java", "(#0).ge(#1)")
    native public def $ge(that: Point(rank)): boolean;

    @Native("java", "x10.array.Point.makeFromVarRail(#1)")
    public native static def $convert(r: Rail[int]): Point(r.length);
    
    @Native("java", "x10.array.Point.makeFromValRail(#1)")
    public native static def $convert(r: ValRail[int]): Point(r.length);

    @Native("java", "(#0).toString()")
    public native def toString(): String;


    //
    //
    //

    private native def this();
}
