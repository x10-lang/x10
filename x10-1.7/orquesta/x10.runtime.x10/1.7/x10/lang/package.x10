package x10.lang;

     type boolean = Boolean;
     type byte = Byte;
     type short = Short;
     type char = Char;
     type int = Int;
     type long = Long;
     type float = Float;
     type double = Double;
    
     type ubyte = UByte;
     type ushort = UShort;
     type uint = UInt;
     type ulong = ULong;
     type nat = uint;
     type Nat = uint;
    
     type int8 = byte;
     type int16 = short;
     type int32 = int;
     type int64 = long;

     type nat8 = ubyte;
     type nat16 = ushort;
     type nat32 = uint;
     type nat64 = ulong;

     type Nat(v: nat) = nat{self == v};
     type nat(v: nat) = nat{self == v};
     type nat(r: Region{rank==1}) = nat{[self] in r};
     type nat(r: Range[nat]) = nat{self in r};
    
     type int(v: int) = int{self == v};
     type int(r: Region{rank==1}) = int{[self] in r};
     type int(r: Range[int]) = int{self in r};

     type int(v: int) = int{self==v};
     type int(r: Region{rank==1}) = int{r.low <= self, self <= r.high};

     type Indexable[T](x : Region) =Indexable{IndexableT==T,self.base==x};
     type Settable[T](x : Region) = Settable{SettableT==T,self.base==x};

     type ValRail[T](x: nat)= ValRail{Base==T,self.length==x};
     type Rail[T](x: nat)= Rail{Base==T,self.length==x};

     type NativeValRail[T](x: nat)= NativeValRail{NativeRailT==T,self.length==x};
     type NativeRail[T](x: nat)= NativeRail{NativeRailT==T,self.length==x};

     type Point(n: nat) = Point{self.length==n};
     type Point(r: Region) = Point{self in r};

     type point = Point;
     type place = Place;
     type region = Region;