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
    
    @Native("java", "x10.array.Point.makeFromVarRail(#1)")
    public native static def $convert(r: Rail[int]): Point(r.length);
    
    @Native("java", "x10.array.Point.makeFromValRail(#1)")
    public native static def $convert(r: ValRail[int]): Point(r.length);

    @Native("java", "x10.array.Point.makeFromValRail(#1)")
    public native static def make(cs: ValRail[int]): Point(cs.length);
    
    @Native("java", "x10.array.Point.makeFromVarRail(#1)")
    public native static def make(cs: Rail[int]): Point(cs.length);
    
    @Native("java", "x10.array.Point.makeFromVarArgs(#1)")
    public native static def make(x0:nat): Point(1);
    
    @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2)")
    public native static def make(x0:nat, x1:nat): Point(2);
    
     @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2,#3)")
    public native static def make(x0:nat, x1:nat, x2:nat): Point(3);
    
     @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2,#3,#4)")
    public native static def make(x0:nat, x1:nat, x2:nat, x3:nat): Point(4);
    
     @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2,#3,#4,#5)")
    public native static def make(x0:nat, x1:nat, x2:nat, x3:nat, x4:nat): Point(5);
    
      @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2,#3,#4,#5,#6)")
    public native static def make(x0:nat, x1:nat, x2:nat, x3:nat, x4:nat,x5:nat): Point(6);
    
      @Native("java", "x10.array.Point.makeFromVarArgs(#1,#2,#3,#4,#5,#6,#7)")
    public native static def make(x0:nat, x1:nat, x2:nat, x3:nat, x4:nat,x5:nat,x6:nat): Point(7);

    @Native("java", "x10.array.Point.makeConstant(#1, #2)")
    public native static def makeConstant(rank: nat, c: int): Point(rank);

    @Native("java", "x10.array.Point.makeZero(#1)")
    public native static def makeZero(rank: nat): Point(rank);

    //
    // operations
    //

    @Native("java", "#0")
    native public def $plus(): Point{rank==this.rank};
    
    @Native("java", "(#0).neg()")
    native public def $minus(): Point(this.rank);

    @Native("java", "(#0).add(#1)")
    native public def $plus(that: Int): Point{rank==this.rank};
    @Native("java", "(#0).sub(#1)")
    native public def $minus(that: Int): Point(this.rank);
    @Native("java", "(#0).mul(#1)")
    native public def $times(that: Int): Point(this.rank);
    @Native("java", "(#0).div(#1)")
    native public def $over(that: Int): Point(this.rank);
    @Native("java", "(#0).mod(#1)")
    native public def $percent(that: Int): Point(this.rank);

    @Native("java", "(#0).add(#1)")
    native public def inv$plus(that: Int): Point{rank==this.rank};
    @Native("java", "(#0).invsub(#1)")
    native public def inv$minus(that: Int): Point(this.rank);
    @Native("java", "(#0).mul(#1)")
    native public def inv$times(that: Int): Point(this.rank);
    @Native("java", "(#0).invdiv(#1)")
    native public def inv$over(that: Int): Point(this.rank);
    @Native("java", "(#0).invmod(#1)")
    native public def inv$percent(that: Int): Point(this.rank);

    @Native("java", "(#0).add(#1)")
    native public def $plus(that: Point{rank==this.rank}): Point{rank==this.rank};
    @Native("java", "(#0).sub(#1)")
    native public def $minus(that: Point(this.rank)): Point(this.rank);
    @Native("java", "(#0).mul(#1)")
    native public def $times(that: Point(this.rank)): Point(this.rank);
    @Native("java", "(#0).div(#1)")
    native public def $over(that: Point(this.rank)): Point(this.rank);
    @Native("java", "(#0).mod(#1)")
    native public def $percent(that: Point(this.rank)): Point(this.rank);

    @Native("java", "(#0).equals(#1)	")
    native public def $eq(that: Point(this.rank)): boolean;
    @Native("java", "(! (#0).equals(#1))")
    native public def $ne(that: Point(this.rank)): boolean;
    
    @Native("java", "(#0).lt(#1)")
    native public def $lt(that: Point(this.rank)): boolean;
    @Native("java", "(#0).gt(#1)")
    native public def $gt(that: Point(this.rank)): boolean;
    @Native("java", "(#0).le(#1)")
    native public def $le(that: Point(this.rank)): boolean;
    @Native("java", "(#0).ge(#1)")
    native public def $ge(that: Point(this.rank)): boolean;

    

    @Native("java", "(#0).toString()")
    public native def toString(): String;


    //
    //
    //

    private native def this();
}
