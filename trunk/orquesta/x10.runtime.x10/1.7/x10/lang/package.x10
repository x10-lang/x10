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

//     type true = boolean{self==true};
//     type false = boolean{self==false};

     type nat(v: nat) = nat{self == v};
     type nat(r: Region{rank==1}) = nat{[self] in r};
     type nat(r: Range[nat]) = nat{self in r};
    
     type int(v: int) = int{self == v};
     type int(r: Region{rank==1}) = int{[self] in r};
     type int(r: Range[int]) = int{self in r};

//     type zero=nat(0);
//     type one=nat(1);
//     type two=nat(2);
//     type three=nat(3);
//     type four=nat(4);
//     type five=nat(5);
//     type six=nat(6);
//     type seven=nat(7);
//     type eight=nat(8);
//     type nine=nat(9);

     type int(v: int) = int{self==v};
     type int(r: Region{rank==1}) = int{r.low <= self, self <= r.high};
    
//     type negone=int(-1);
//     type negtwo=int(-2);
//     type negthree=int(-3);
//     type negfour=int(-4);
//     type negfive=int(-5);
//     type negsix=int(-6);
//     type negseven=int(-7);
//     type negeight=int(-8);
//     type negnine=int(-9);

     type Indexable[T](x : Region) =Indexable{IndexableT==T,self.base==x};
     type Settable[T](x : Region) = Settable{SettableT==T,self.base==x};

     type ValRail[T](x: nat)= ValRail{Base==T,self.length==x};
     type Rail[T](x: nat)= Rail{Base==T,self.length==x};

     type NativeValRail[T](x: nat)= NativeValRail{NativeRailT==T,self.length==x};
     type NativeRail[T](x: nat)= NativeRail{NativeRailT==T,self.length==x};

     type Point = ValRail[int];
     type Point(n: nat) = Point{self.length==n};
     type Point(r: Region) = Point{self in r};

     type point = Point;
     type place = Place;
     