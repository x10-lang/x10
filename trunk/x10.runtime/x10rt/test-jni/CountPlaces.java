import x10.x10rt.X10RT;

public class CountPlaces {
  public static void main(String[] args) throws InterruptedException {

    X10RT.barrier();

    if (X10RT.here() == X10RT.getNode(X10RT.numNodes()-1)) {
      System.out.println(X10RT.here()+" is about to sleep for 1 second...");
      Thread.sleep(1000);
      System.out.println("Hello world: I am the tardy node "+X10RT.here());
    } else {
      System.out.println("Hello world: There are " +X10RT.numNodes()+" Nodes and I am "+X10RT.here());
    }

    X10RT.barrier();

    System.out.println("Exiting from "+X10RT.here());
  }
}
