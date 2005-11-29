public class For1 {
    public static void main(String[] args) {
        foo1();
        foo2();
        foo3();
    }
    public static void foo1() {
        for (point p: [0:9]) {
            System.out.println(p);
        }
    }
    public static void foo2() {
        for (point p[i]: [0:9]) {
            System.out.println(p + ": " + i);
        }
    }
    public static void foo3() {
        for (point p[i,j]: [0:9,0:9]) {
            System.out.println(p + ": " + i + ": " + j);
        }
    }
}