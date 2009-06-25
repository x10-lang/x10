package x10.lang;

// Members of the class x10.lang._ are imported automatically.
public class _ {
    public static type void = Void;
    public static type boolean = Boolean;
    public static type byte = Byte;
    public static type short = Short;
    public static type char = Char;
    public static type int = Int;
    public static type long = Long;
    public static type float = Float;
    public static type double = Double;

/*
    // UNCOMMENT IF UNSIGNED INTEGERS SUPPORTED
    public static type ubyte = UByte;
    public static type ushort = UShort;
    public static type uint = UInt;
    public static type ulong = ULong;
     
    public static type nat = uint;

    public static type nat8 = ubyte;
    public static type nat16 = ushort;
    public static type nat32 = uint;
    public static type nat64 = ulong;
*/

    // COMMENT OUT IF UNSIGNED INTEGERS SUPPORTED
    public static type nat = int;
 
    public static type int8 = byte;
    public static type int16 = short;
    public static type int32 = int;
    public static type int64 = long;

    public static type point = Point;
    public static type place = Place;
    public static type region = Region;
    public static type dist = Dist;

    public static type ValArray[T] = Array[T];
}