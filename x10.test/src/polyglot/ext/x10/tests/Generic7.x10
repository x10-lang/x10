public class Generic7 {
	static class List<T> {
		T data;
        <T>List next;
	}

	public static void main(String[] v) {
        <int>List l=new <int>List();
		l.next.data=(float)5;
	}
}

