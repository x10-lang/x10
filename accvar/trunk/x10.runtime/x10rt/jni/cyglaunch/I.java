import x10.x10rt.X10RT;

class I {
   public static void main(String[] args) {
      new I().tryIt();
      //System.err.println("Invoked tryIt()");
      System.out.println("some.property="+System.getProperty("some.property"));
      System.out.println("some.other.property="+System.getProperty("some.other.property"));
   }
   void tryIt() {
      int n = X10RT.numPlaces();
      int i = X10RT.here();
      System.out.println("There are "+n+" Nodes and I am Node "+i);
   }
} 
