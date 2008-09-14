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
    
    public static type true = boolean{self==true};
    public static type false = boolean{self==false};
    
    public static type boolean(b:Boolean) = Boolean{self==b};
    public static type byte(b:Byte) = Byte{self==b};
    public static type short(b:Short) = Short{self==b};
    public static type char(b:Char) = Char{self==b};
    public static type int(b:Int) = Int{self==b};
    public static type long(b:Long) = Long{self==b};
    public static type float(b:Float) = Float{self==b};
    public static type double(b:Double) = Double{self==b};
    public static type string(s:String) = String{self==s};
    
    public static type Boolean(b:Boolean) = Boolean{self==b};
    public static type Byte(b:Byte) = Byte{self==b};
    public static type Short(b:Short) = Short{self==b};
    public static type Char(b:Char) = Char{self==b};
    public static type Int(b:Int) = Int{self==b};
    public static type Long(b:Long) = Long{self==b};
    public static type Float(b:Float) = Float{self==b};
    public static type Double(b:Double) = Double{self==b};
    public static type String(s:String) = String{self==s};
   
    public static type Nat = Int;

/*
    // UNCOMMENT IF UNSIGNED INTEGERS SUPPORTED
    public static type ubyte = UByte;
    public static type ushort = UShort;
    public static type uint = UInt;
    public static type ulong = ULong;
     
    public static type Nat = uint;

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
    public static type clock = Clock;
    
    public static type Point(r: Int) = Point{rank==r};
    public static type point(r: Int) = Point{rank==r};
    public static type Region(r:Nat) = Region{rank==r};
    
    public static type Dist(r:Nat)   = Dist{rank==r};
    public static type Dist(r:Region) = Dist{region==r};
    
    public static type Array[T](r:Nat) = Array[T]{rank==r};
    public static type Array[T](r:Region) = Array[T]{region==r};
    public static type Array[T](d:Dist) = Array[T]{dist==d};

  
    public static type ValArray[T](r:Nat) = ValArray[T]{rank==r};
    public static type ValArray[T](r:Region) = ValArray[T]{region==r};
    public static type ValArray[T](d:Dist) = ValArray[T]{dist==d};
    
    public static type ValRail[T](n:Nat) = ValRail[T]{length==n};
}