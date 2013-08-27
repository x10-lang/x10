
import x10.util.*;

public class A {
	var num:int = 0;
	var num_1:int = 0;
	var num_2:int = 0;
	
	
	var num_byte:byte = 0;
	var num_short:short = 0;
	var num_int:int = 0;
	var num_long:long = 0;
	var obj:B;
	
	var obj_any:Any;
	
	var obj_C:C[int, long];
	var obj_D:D[C[int, long]];
	var obj_E:E_Struct_Template[int, short];
	var obj_F:F_Struct;
	var arr_byte:Array[byte](1) = new Array[byte](10);
	var arr_short:Array[short](1) = new Array[short](10);
	var arr_int:Array[int](1) = new Array[int](10);
	var arr_long:Array[long](1) = new Array[long](10);
	var arr_obj:Array[B](1) = new Array[B](10);
	
	public def this() {
		num = 1;
	}
	
	public def this(num_arg:int) {
		num = num_arg;
	}
	
	public def this(num_arg_1:int, num_arg_2:int) {
		num_1 = num_arg_1;
		num_2 = num_arg_2;
	}
	
	public def foo_1(arg1:Int) {
		this.num = arg1;
		this.foo_2(2);
	}
	
	public def foo_2(arg1:Int) {
		this.num = arg1;
	}
}