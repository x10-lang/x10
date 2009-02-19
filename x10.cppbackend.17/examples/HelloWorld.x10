public class HelloWorld {
	public static void main(String[] args) {
		System.out.println("Hello World");
		for (int i = 0; i < args.length; i++)
			System.out.println("Arg "+i+": "+args[i]);
		String prefix = "3 = ";
		double three = 3;
		System.out.println(prefix+three);
	}
}
