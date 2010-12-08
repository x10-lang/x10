import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;

public class HelloWholeWorld {

    public static void main(String[] args) {
        Pool p = new Pool(new Task() {
            public void body() {
                sayHello();
              }
        });
        p.start();
    }

    static void sayHello() {
        for (int i=0; i<Pool.numPlaces(); i++) {
            Pool.runAsync(i, new Task() {
                public void body() {
                    System.out.println("Hello from place "+Pool.here());
                }
            });
        }
    }
}
