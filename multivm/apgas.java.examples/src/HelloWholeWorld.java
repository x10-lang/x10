import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;


public class HelloWholeWorld {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("I am here");
        Pool p = new Pool(new Task() {
            public void body() {
                for (int i=0; i<Pool.numPlaces(); i++) {
                    Pool.runAsync(i, new Task() {
                        public void body() {
                            System.out.println("Hello from place "+Pool.here());
                        }
                    });
                }
              }
        });
        p.start();
        System.out.println("I am there");

    }

}
