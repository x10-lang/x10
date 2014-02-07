/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;


// Members of the class x10.lang._ are imported automatically.
public class _ {
    public static type boolean = Boolean;
    public static type byte = Byte;
    public static type short = Short;
    public static type char = Char;
    public static type int = Int;
    public static type long = Long;
    public static type float = Float;
    public static type double = Double;

    public static type boolean(b:Boolean) = Boolean{self==b};
    public static type byte(b:Byte) = Byte{self==b};
    public static type short(b:Short) = Short{self==b};
    public static type char(b:Char) = Char{self==b};
    public static type int(b:Int) = Int{self==b};
    public static type long(b:Long) = Long{self==b};
    public static type float(b:Float) = Float{self==b};
    public static type double(b:Double) = Double{self==b};
    public static type string(s:String) = String{self==s};


    public static type signed = Int;
    public static type unsigned = UInt;

    public static type ubyte = UByte;
    public static type uint8 = UByte;
    public static type nat8 = UByte;

    public static type ushort = UShort;
    public static type uint16 = UShort;
    public static type nat16 = UShort;

    public static type uint = UInt;
    public static type uint32 = UInt;
    public static type nat32 = UInt;

    public static type ulong = ULong;
    public static type uint64 = ULong;
    public static type nat64 = ULong;
 
    public static type int8 = byte;
    public static type int16 = short;
    public static type int32 = int;
    public static type int64 = long;


    public static type Console = x10.io.Console;
}
