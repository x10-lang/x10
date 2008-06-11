package x10.lang;


public class Cell[T] implements NullaryFun[T], Assignable[T] {
    var x:T;
    public def apply():T = x;
    public def set(v:T):void = {
	x=v;
    }
}
