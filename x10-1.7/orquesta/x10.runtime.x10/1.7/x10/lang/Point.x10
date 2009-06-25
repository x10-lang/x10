package x10.lang;

public value class Point implements NativeValRail{NativeRailT==int}, Comparable[point], Arithmetic[point] /* , LeftArithmetic[int,point] */ {
        property rank() = length;

        /**
         * Return true iff the point is on the upper boundary of the i'th
         * dimension.
         */
        abstract public def onUpperBoundary(i: nat, r: region): boolean;

        /**
         * Return true iff the point is on the lower boundary of the i'th
         * dimension.
         */
        abstract public def onLowerBoundary(i: nat, r: region): boolean;

        private incomplete static def toPoint(rank: nat, c: int): point{length==rank};
        
        private static def allZero(d: nat) = toPoint(d, 0 as int);
        
        public incomplete def add(p: point{self.length==this.length}): point{self.length==this.length};
        public incomplete def sub(p: point{self.length==this.length}): point{self.length==this.length};
        public incomplete def mul(p: point{self.length==this.length}): point{self.length==this.length};
        public incomplete def div(p: point{self.length==this.length}): point{self.length==this.length};
        public incomplete def neg(): point{self.length==this.length};
        
        public def add(c: int) = add(toPoint(length, c));
        public def sub(c: int) = add(-c);
        public def mul(c: int) = mul(toPoint(length, c));
        public def div(c: int) = div(toPoint(length, c));
        public def invadd(c: int) = add(c);
        public def invsub(c: int) = add(-c).neg();
        public def invmul(c: int) = mul(c);
        public def invdiv(c: int) = toPoint(length, c).div(this);
}