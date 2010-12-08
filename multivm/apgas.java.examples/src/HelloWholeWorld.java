import com.ibm.apgas.Pool;


public class HelloWholeWorld {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("I am here");
        Pool p = new Pool(new Runnable() {
            public void run() {
                System.out.println("Hello world");
             }
        });
        p.start();
        System.out.println("I am there");

    }

}
