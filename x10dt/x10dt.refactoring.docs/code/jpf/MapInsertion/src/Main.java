/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class Main {

	// @Symbolic("true")
	// public static String k1;
	//
	// @Symbolic("true")
	// public static int v1;
	//
	// @Symbolic("true")
	// public static String k2;
	//
	// @Symbolic("true")
	// public static int v2;

	public static void main(String[] args) {
	}

	// public static void freqAccumShouldBeCommutative() {
	// Freq freq1 = new Freq();
	// Freq freq2 = new Freq();
	//
	// freq1.accum(k1, 2 + v1);
	// freq1.accum(k2, v2);
	//
	// freq2.accum(k2, v2);
	// freq2.accum(k1, v1);
	//
	// assert freq1.equals(freq2);
	// }

	// public static void freqAccumShouldBeCommutative2(String k1, Integer v1,
	// String k2, Integer v2) {
	// Freq freq1 = new Freq();
	// Freq freq2 = new Freq();
	//
	// freq1.accum(k1, 2 + v1);
	// freq1.accum(k2, v2);
	//
	// freq2.accum(k2, v2);
	// freq2.accum(k1, v1);
	//
	// assert freq1.equals(freq2);
	// }

	public static void freqIntegerAccumShouldBeCommutative3(Integer k1,
			Integer v1, Integer k2, Integer v2) {
		FreqInteger freq1 = new FreqInteger();
		FreqInteger freq2 = new FreqInteger();

		freq1.accum(k1, 2 + v1);
		freq1.accum(k2, v2);

		freq2.accum(k2, v2);
		freq2.accum(k1, v1);

		assert freq1.equals(freq2);
	}

}
