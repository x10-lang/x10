
import x10.compiler.Native;

public class G {
	@Native("c++", "(#this)->func_native()")
	public native def func_1() : void;
	
	@Native("c++", "(#this)->func_native()")
	public native def func_1(x:int) : void;
}