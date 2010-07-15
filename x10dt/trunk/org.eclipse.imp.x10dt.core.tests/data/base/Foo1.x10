public class Foo1 {
var i: Int;
def this(bar: Bar!) { bar.hitMe(this); // this should be flagged as an error? }
}
