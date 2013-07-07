/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.interop;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NoReturn;

@NativeRep("c++", "#error Undefined Java", "#error Undefined Java", null)
public class Java {
    private def this() { } // no instances
    
    // Java arrays (special)
    // TODO: reject unsigned types for element type
    @NativeRep("java", "#T[]", null, "x10.rtt.Types.getRTT(#T[].class)")
    public static final class array[T](
        @Native("java", "(#this).length")
        length:Int
    ) {
        @Native("java", "(#T[])#T$rtt.makeArray(#d0)")
        public native def this(d0:Int):array[T]{self.length==d0};
        @Native("java", "(#this)[#i]")
        public final native operator this(i:Int):T;
        @Native("java", "(#this)[#i] = #v")
        public final native operator this(i:Int) = (v:T):T;
    }
    @Native("java", "new java.lang.Object()")
    public static native def newObject():Any{self!=null};
    @Native("java", "(#T[])#T$rtt.makeArray(#d0)")
    public static native def newArray[T](d0:Int):array[T]{self.length==d0};
    @Native("java", "(#T[][])#T$rtt.makeArray(#d0,#d1)")
    public static native def newArray[T](d0:Int, d1:Int):array[array[T]{self.length==d1}]{self.length==d0};
    @Native("java", "(#T[][][])#T$rtt.makeArray(#d0,#d1,#d2)")
    public static native def newArray[T](d0:Int, d1:Int, d2:Int):array[array[array[T]{self.length==d2}]{self.length==d1}]{self.length==d0};
    @Native("java", "(#T[][][][])#T$rtt.makeArray(#d0,#d1,#d2,#d3)")
    public static native def newArray[T](d0:Int, d1:Int, d2:Int, d3:Int):array[array[array[array[T]{self.length==d3}]{self.length==d2}]{self.length==d1}]{self.length==d0};

    // To byte array
    @Native("java", "new byte[] { (byte)(#v?1:0) }")
    public static native def toBytes(v:Boolean):array[Byte]{self.length==1};
    @Native("java", "new byte[] { (byte)(#v>>>8), (byte)(#v) }")
    public static native def toBytes(v:Char):array[Byte]{self.length==2};
    @Native("java", "new byte[] { #v }")
    public static native def toBytes(v:Byte):array[Byte]{self.length==1};
    @Native("java", "new byte[] { (byte)(#v>>>8), (byte)(#v) }")
    public static native def toBytes(v:Short):array[Byte]{self.length==2};
    @Native("java", "new byte[] { (byte)(#v>>>24), (byte)(#v>>>16), (byte)(#v>>>8), (byte)(#v) }")
    public static native def toBytes(v:Int):array[Byte]{self.length==4};
    @Native("java", "new byte[] { (byte)(#v>>>56), (byte)(#v>>>48), (byte)(#v>>>40), (byte)(#v>>>32), (byte)(#v>>>24), (byte)(#v>>>16), (byte)(#v>>>8), (byte)(#v) }")
    public static native def toBytes(v:Long):array[Byte]{self.length==8};
    public static def toBytes(v:Float):array[Byte]{self.length==4} = toBytes(v.toIntBits());
    public static def toBytes(v:Double):array[Byte]{self.length==8} = toBytes(v.toLongBits());
    @Native("java", "#v.getBytes()")
    public static native def toBytes(v:String):array[Byte]{self!=null};

    // From byte array
    @Native("java", "(#a[0]!=0)")
    public static native def toBoolean(a:array[Byte]):Boolean;
    @Native("java", "((char)( ((#a[0]&0xFF)<<8) | ((#a[1]&0xFF)<<0) ))")
    public static native def toChar(a:array[Byte]):Char;
    @Native("java", "(#a[0])")
    public static native def toByte(a:array[Byte]):Byte;
    @Native("java", "((short)( ((#a[0]&0xFF)<<8) | ((#a[1]&0xFF)<<0) ))")
    public static native def toShort(a:array[Byte]):Short;
    @Native("java", "(((#a[0]&0xFF)<<24)+((#a[1]&0xFF)<<16)+((#a[2]&0xFF)<<8)+((#a[3]&0xFF)<<0))")
    public static native def toInt(a:array[Byte]):Int;
    @Native("java", "(((#a[0]&0xFFL)<<56)+((#a[1]&0xFFL)<<48)+((#a[2]&0xFFL)<<40)+((#a[3]&0xFFL)<<32)+((#a[4]&0xFFL)<<24)+((#a[5]&0xFFL)<<16)+((#a[6]&0xFFL)<<8)+((#a[7]&0xFFL)<<0))")
    public static native def toLong(a:array[Byte]):Long;
    public static def toFloat(a:array[Byte]):Float = Float.fromIntBits(toInt(a));
    public static def toDouble(a:array[Byte]):Double = Double.fromLongBits(toLong(a));
    @Native("java", "new java.lang.String(#a)")
    public static native def toString(a:array[Byte]):String{self!=null};

    // Java classes
    @Native("java", "#T$rtt.getJavaClass()")
    public static native def javaClass[T]():java.lang.Class;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).set(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Any):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setByte(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Byte):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setShort(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Short):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setInt(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Int):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setLong(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Long):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setFloat(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Float):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setDouble(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Double):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setChar(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Char):void;
    @Native("java", "do { try { #T$rtt.getJavaClass().getDeclaredField(#name).setBoolean(null,#value); } catch (java.lang.Exception e) { java.lang.RuntimeException re = (e instanceof java.lang.RuntimeException) ? ((java.lang.RuntimeException) e) : new x10.lang.WrappedThrowable(e); throw re; } } while (false)")
    public static native def setStaticField[T](name:String,value:Boolean):void;
    @Native("java", "#o.getClass()")
    public static native def getClass(o:Any):java.lang.Class;
    
    // Java exceptions
    @Native("java", "do { throw #e; } while (false)")
    public static native @NoReturn def throwException(e:CheckedThrowable):void;
    
    // Java conversions (primitive)
    @Native("java", "java.lang.Boolean.valueOf(#b)")
    public static native def convert(b:x10.lang.Boolean):java.lang.Boolean{self!=null};
    @Native("java", "#b.booleanValue()")
    public static native def convert(b:java.lang.Boolean):x10.lang.Boolean;
    @Native("java", "java.lang.Byte.valueOf(#y)")
    public static native def convert(y:x10.lang.Byte):java.lang.Byte{self!=null};
    @Native("java", "#y.byteValue()")
    public static native def convert(y:java.lang.Byte):x10.lang.Byte;
    @Native("java", "java.lang.Short.valueOf(#s)")
    public static native def convert(s:x10.lang.Short):java.lang.Short{self!=null};
    @Native("java", "#s.shortValue()")
    public static native def convert(s:java.lang.Short):x10.lang.Short;
    @Native("java", "java.lang.Integer.valueOf(#i)")
    public static native def convert(i:x10.lang.Int):java.lang.Integer{self!=null};
    @Native("java", "#i.intValue()")
    public static native def convert(i:java.lang.Integer):x10.lang.Int;
    @Native("java", "java.lang.Long.valueOf(#l)")
    public static native def convert(l:x10.lang.Long):java.lang.Long{self!=null};
    @Native("java", "#l.longValue()")
    public static native def convert(l:java.lang.Long):x10.lang.Long;
    @Native("java", "java.lang.Float.valueOf(#f)")
    public static native def convert(f:x10.lang.Float):java.lang.Float{self!=null};
    @Native("java", "#f.floatValue()")
    public static native def convert(f:java.lang.Float):x10.lang.Float;
    @Native("java", "java.lang.Double.valueOf(#d)")
    public static native def convert(d:x10.lang.Double):java.lang.Double{self!=null};
    @Native("java", "#d.doubleValue()")
    public static native def convert(d:java.lang.Double):x10.lang.Double;
    @Native("java", "java.lang.Character.valueOf(#c)")
    public static native def convert(c:x10.lang.Char):java.lang.Character{self!=null};
    @Native("java", "#c.charValue()")
    public static native def convert(c:java.lang.Character):x10.lang.Char;
    
    // Java conversions (array)
    @Native("java", "(#T[])#a.raw.getBackingArray()")
    public static native def convert[T](a:x10.regionarray.Array[T](1)):array[T];
    @Native("java", "new x10.regionarray.Array((java.lang.System[]) null, #T$rtt).x10$regionarray$Array$$init$S(new x10.core.Rail(#T$rtt, #a.length, #a), (x10.regionarray.Array.__0$1x10$regionarray$Array$$T$2) null)")
    public static native def convertArray[T](a:array[T]):x10.regionarray.Array[T](1);
    
    // Java conversions (rail)
    @Native("java", "(#T[])#a.getBackingArray()")
    public static native def convert[T](a:x10.lang.Rail[T]):array[T];
    @Native("java", "new x10.core.Rail(#T$rtt, #a.length, #a)")
    public static native def convert[T](a:array[T]):x10.lang.Rail[T];
}
