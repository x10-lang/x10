/**
 * Atomic return test
 */
public class AtomicReturn {
	var a: int = 0;
	const N: int = 100;

	def update1(): int = {
		atomic {
			a++;
			return a;
		}
	}

	def update3(): int = {
		atomic {
			return a++;
		}
	}

	public def run(): boolean = {
		update1();
		update3();
		x10.io.Console.OUT.println(a);
		return a == 2;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicReturn().run();
	}
}
