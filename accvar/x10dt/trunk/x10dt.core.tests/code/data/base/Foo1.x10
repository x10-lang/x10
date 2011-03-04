public class Foo1 {
	var i: Int;
	var myBar:    Bar;

	def this(bar: Bar) { 
		myBar    = bar;
	}
	
	def foo(){
		myBar.hitMe(this);
	}
}