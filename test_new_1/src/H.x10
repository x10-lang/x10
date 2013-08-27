public class H extends G {
	// USING CASE: In this class, the function func_1 overloads the same function of class G. In class G, this
	// function is defined multiple times, so the X10 compiler emits the using directive.
	// For TM, we don't want to emit this directive for NATIVE methods.
	//
	public def func_1() : void {
		return;
	}
}