public class For1 {
    public static void main(String[] args) {
        foo1();
        foo2();
        foo3();
    }
    public static void foo1() {
        For1 f1 = new For1();
        for (point p: [0:9]) {
            System.out.println(p);
        }
    }
    public static void foo2() {
        for (point p1[i]: [0:9]) {
            System.out.println(p1 + ": " + i);
        }
    }
    public static void foo3() {
        for (point p2[i,j]: [0:9,0:9]) {
            System.out.println(p2 + ": " + i + ": " + j);
        }
    }
}
