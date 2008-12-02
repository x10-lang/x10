import harness.x10Test;

/**
 * Literal test, for java literals
 */
public class LiteralTest extends x10Test {
	int i1 = -2147483648;
	int i2 = 020000000000;
	int i3 = 0x80000000;
	int i4 = 2147483647;
	int i5 = 0x7fffffff;
	int i6 = 017777777777;
	int i7 = (int) 037777777777L;
	int i8 = (int) 0xffffffffL;
	int i9 = -1;
	long l1 = -9223372036854775808L;
	long l2 = 0x8000000000000000l;
	long l3 = 01000000000000000000000l;
	long l4 = 9223372036854775807L;
	long l5 = 0x7fffffffffffffffL;
	long l6 = 0777777777777777777777L;
	long l7 = -1L;
	long l8 = 01777777777777777777777L;
	long l9 = 0xffffffffffffffffL;
	String foo = "a\r\n";

	// necessary to run this in the TestCompiler harness.
	public LiteralTest() { }

	public boolean run() {
		chk(i1 == i2 && i2 == i3);
		chk(i1 == -i1);
		chk(i4 == i5 && i5 == i6);
		chk(i4+1 == i1 && i6 == i1-1);
		chk(i7 == i8 && i8 == i9);
		chk(i9+1 == (i1-i3));
		chk((i9 << 31) == i1);
		chk(l1 == l2 &&l2 == l3 && l1 == -l1);
		chk(l1 == ((long)i1<<32));
		chk(l4 == l5 && l5 == l6);
		chk(l4+1 == l1 && l4 == l1-1);
		chk(l7 == l8 && l8 == l9 && l9 == i9);
		chk(l9+1 == (l1-l3));
		chk((l9 << 63) == l1);
		chk(foo.charAt(0) == 'a');
		chk(foo.charAt(1) == '\r');
		chk(foo.charAt(2) == '\n');
		chk(foo.equals("a"+"\r"+"\n"));
		return true;
	}

	public static void main(String[] args) {
		new LiteralTest().execute();
	}
}

