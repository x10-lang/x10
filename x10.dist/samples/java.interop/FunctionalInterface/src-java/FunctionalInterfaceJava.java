import java.util.ArrayList;

public class FunctionalInterfaceJava {
    public static void main(String[] args) {

        ArrayList<String> stringsJava = new ArrayList<>();
        stringsJava.add("aaa");
        stringsJava.add("bbb");
        stringsJava.add("ccc");
        int origSizeJava = stringsJava.size();
        stringsJava.removeIf(s -> false);
        int newSizeJava = stringsJava.size();
        if (newSizeJava != origSizeJava) {
            System.out.println("ERROR: something is wrong with Java predicate.");
            System.exit(1);
        }

        ArrayList<String> stringsX10 = new ArrayList<>();
        stringsX10.add("aaa");
        stringsX10.add("bbb");
        stringsX10.add("ccc");
        int origSizeX10 = stringsX10.size();
        stringsX10.removeIf(new PredicateX10());
        int newSizeX10 = stringsX10.size();
        if (newSizeX10 != origSizeX10) {
            System.out.println("ERROR: something is wrong with X10 predicate.");
            System.exit(1);
        }

        String error = FunctionalInterfaceX10.reduce(Long::sum, 0, 0, 10, 55);
        if (error != null) {
            System.out.println(error);
            System.exit(1);
        }

        System.out.println("OK");
    }
}
