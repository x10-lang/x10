public class HWException {
	public static void main(String[] args) {
		try {
			throw new RuntimeException("Hello World");
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}

