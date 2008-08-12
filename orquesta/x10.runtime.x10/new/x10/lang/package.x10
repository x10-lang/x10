package x10.lang;

//public type Box[T]{T <: Ref} = T;
//public type Box[T]{T <: Value} = x10.lang.Boxed[T];

public type boolean = Boolean;
public type byte = Byte;
public type short = Short;
public type char = Char;
public type int = Int;
public type long = Long;
public type float = Float;
public type double = Double;
    
public type ubyte = UByte;
public type ushort = UShort;
public type uint = UInt;
public type ulong = ULong;
     
public type nat = uint;
    
public type int8 = byte;
public type int16 = short;
public type int32 = int;
public type int64 = long;

public type nat8 = ubyte;
public type nat16 = ushort;
public type nat32 = uint;
public type nat64 = ulong;

public type point = Point;
public type place = Place;
public type region = Region;
public type dist = Dist;

public type ValArray[T] = Array[T];
public type ValRail[T] = Rail[T];
