public class ForEach1 {
    public static void main(String[] args) {
        ForEach1 fe1= new ForEach1();
//        foreach (point p: [0:10]) {
//            System.out.println(p);
//        }
        foreach (point p[i]: [0:10]) {
            System.out.println(p + ": " + i);
        }
        foreach (point p1[i1,j1]: [0:9,0:9]) {
            System.out.println(p1 + ": " + i1 + ": " + j1);
        }
    }
}