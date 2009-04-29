/**
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class PropertyAssign {
	class Prop(i: int, j: int) {
		public def this(i: int, j: int): Prop = {
			property(i,j);
		}
        }
  
	public def run(): boolean = {
                var p: Prop = new Prop(1,2);
	        return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PropertyAssign().run();
	}
}
