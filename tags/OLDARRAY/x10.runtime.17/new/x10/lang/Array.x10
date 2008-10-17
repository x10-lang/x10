/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.Native;
import x10.compiler.NativeRep;

// note: use dist.region.rank, not rank -- only the properties in the
// header are in scope in the header.

@NativeRep("java", "x10.array.Array<#1>")
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

 @Native("java", "x10.array.ArrayFactory.makeFromRail(#2, #4)")
    native public static def $convert[T](r: Rail[T]): Array[T]{rank==1,rail};
 
    @Native("java", "x10.array.ArrayFactory.<#2>makeFromValRail(#3, #4)")
    native public static def $convert[T](r: ValRail[T]): Array[T]{rank==1,rail};
    
    @Native("java", "x10.array.ArrayFactory.<#2>makeVarArray(#3, #4, #5)")
    native public static def make[T](d: Dist, init: (Point)=>T)
       : Array[T]{self.dist==d};

    @Native("java", "x10.array.ArrayFactory.<#2>makeVarArray(#3, #4)")
    native public static def make[T](d: Dist): Array[T]{dist==d};
    
    @Native("java", "x10.array.ArrayFactory.makeFromRail(#2, #4)")
    native public static def make[T](r: Rail[T]): Array[T]{rank==1};

    @Native("java", "x10.array.ArrayFactory.<#2>makeFromValRail(#3, #4)")
    native public static def make[T](r: ValRail[T]): Array[T]{rank==1};
    
    @Native("java", "x10.array.ArrayFactory.<#2>makeVarArray(#3,x10.array.RegionFactory.makeRect(0,(#4-1)),#5)")
    native public static def make[T](n: nat, init:(point)=>T):Array[T]{rank==1,rect};
    
     @Native("java", "x10.array.ArrayFactory.<#2> makeVarArray(#3, #4,#5)")
    native public static def makeFromRegion[T](r: Region, init: (Point)=>T)
	   : Array[T]{region==r,onePlace==here};

    @Native("java", "x10.array.ArrayFactory.<#2>makeVarArray(#3, #4,#5,#6)")
    native public static def makeFromRegion[T](r: Region, init: (Point)=>T, value: boolean): Array[T]{region==r,onePlace==here};
    
    @Native("java", "(#0).restriction(#1)")
    public native def restriction(r: Region): Array[T]{region==r};
                     
    @Native("java", "(#0).get$(#1)")
    public native def get(pt: Point(this.rank)): T;
    @Native("java", " (#0).get$(#1)")
    public native def get(i0: int){rank==1}: T;
    @Native("java", "(#0).get$(#1, #2)")
    public native def get(i0: int, i1: int){rank==2}: T;
    @Native("java", " (#0).get$(#1, #2, #3)")
    public native def get(i0: int, i1: int, i2: int){rank==3}: T;   
                 
    @Native("java", "(#0).get$(#1)")
    public native def apply(pt: Point(this.rank)): T;
    @Native("java", " (#0).get$(#1)")
    public native def apply(i0: int){rank==1}: T;
    @Native("java", "(#0).get$(#1, #2)")
    public native def apply(i0: int, i1: int){rank==2}: T;
    @Native("java", " (#0).get$(#1, #2, #3)")
    public native def apply(i0: int, i1: int, i2: int){rank==3}: T;
    
                
    @Native("java", "(#0).set$(#1, #2)")
    public native def set(v:T, pt: Point(this.rank)): void;
    @Native("java", "(#0).set$(#1, #2)")
    public native def set(v:T, i0: int){rank==1}: void;
    
    @Native("java", "(#0).set$(#1, #2, #3)")
    public native def set(v:T, i0: int, i1: int){rank==2}: void;
    
    @Native("java", "(#0).set$(#1, #2, #3, #4)")
    public native def set(v:T, i0: int, i1: int, i2: int){rank==3}: void;
    
    @Native("java", "(#0)")
    native public def $plus(): Array[T]{dist==this.dist};
    @Native("java", "(#0).neg()")
    native public def $minus(): Array[T]{dist==this.dist};

    @Native("java", "(#0).add(#1)")
    native public def $plus(that: Array[T]{dist==this.dist}): Array[T]{dist==this.dist};
    
    @Native("java", "(#0).sub(#1)")
    native public def $minus(that: Array[T]{dist==this.dist}): Array[T]{dist==this.dist};
    
    @Native("java", "(#0).mul(#1)")
    native public def $times(that: Array[T]{dist==this.dist}): Array[T]{dist==this.dist};
    
    @Native("java", "(#0).div(#1)")
    native public def $over(that: Array[T]{dist==this.dist}): Array[T]{dist==this.dist};

    // ----------------- restriction
    @Native("java", "((x10.array.Array)(#0).restriction(#1))")
    native public def $bar(r: Region(this.rank)): Array[T](r);
    
    @Native("java", "((x10.array.Array)(#0).restriction(#1))")
    native public def $bar(d: Dist(this.rank)): Array[T](d.region);
    
    @Native("java", "(#0).restriction(#1)")
    native public def $bar(p: Place): Array[T]{onePlace==here};
    
    // ----------------- union
    @Native("java", "(#0).union(#1)")
    native public def $or(a:Array[T](this.rank)):Array[T](this.rank);
    
    // ----------------- overlay
    @Native("java", "(#0).overlay(#1)")
    native public def overlay(a:Array[T](this.rank)):Array[T](this.rank);
    
    // ----------------- update
    @Native("java", "(#0).update(#1)")
    native public def update(a:Array[T](this.rank)):Array[T](this.rank);
    
  
    // ------------------- scan
   @Native("java","(#0).scan(#1,#2)")
    native public def scan(res:Array[T](this.dist), 
              op:(T)=>T):Void;
   
    @Native("java","(#0).scan(#1,#2)")
    native public def scan(res:Array[T](this.dist), 
              op:(Point(this.rank), T)=>T):Void;
    @Native("java", "(#0).scan(#1,#2)")
    native public def scan(op:(T,T)=>T, unit:T):Array[T](this.dist);
    
    // ------------------- pointwise
    @Native("java", "(#0).pointwise(#1,#2")
    native public 
      def pointwise(res: Array[T](this.dist), 
                    f:(Point(this.rank), T, T)=>T):Void;
    
    // ------------------- reduce, reduction
    @Native("java", "(#0).reduce(#1,#2,#3)")
    native public def reduce(f:(T,T)=>T, unit:T, r:Region(this.rank)):T;
    @Native("java", "(#0).reduce(#1,#2)")
    native public def reduce(f:(T,T)=>T, unit:T):T;
    @Native("java", "(#0).reduce(#1)")
    native public def reduce(f:(T,T)=>T):T;
    
    @Native("java", "(#0).<#1>reduction(#3)")
    native public def reduction[U](op:(T)=>U):U;
    
     // ------------------- lift
    @Native("java", "(#0).lift(#1)")
    native public def lift(op:(T)=>T):Array[T](dist);
    
     @Native("java", "(#0).lift(#1,#2)")
    native public def lift(op:(T,T)=>T, other:Array[T](this.dist))
      :Array[T](this.dist);
    
    @Native("java", "(#0).sum()")
    native public def sum():T;
    
    @Native("java", "(#0).sum(#1)")
    native public def sum(r:Region):T;
    
    @Native("java", "(#0).max()")
    native public def max():T;
    
    @Native("java", "(#0).sum(#1)")
    native public def max(r:Region):T;
    
    
    
    //
    // private/protected
    //

    protected def this(dist: Dist) = {
        property(dist);
    }
}
