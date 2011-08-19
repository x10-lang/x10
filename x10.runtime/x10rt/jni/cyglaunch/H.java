class H {
   static {
      //System.err.println("Loading library H");
      //System.loadLibrary("x10rt_sockets");
      try {
         //System.err.println("The platform string is: "+System.mapLibraryName("H"));
         System.loadLibrary("H");
      } catch (Throwable t) {
         t.printStackTrace(System.err);
      }
      //System.loadLibrary("jni-pgasrt.dll");
      //System.load("./H.dll");
      //System.err.println("Loaded library H");
   }
   public static void main(String[] args) {
      new H().tryIt();
      //System.err.println("Invoked tryIt()");
      System.out.println("some.property="+System.getProperty("some.property"));
      System.out.println("some.other.property="+System.getProperty("some.other.property"));
   }
   public native void nat();
   void tryIt() {
      nat();
      //com.ibm.pgas.PGASRT.x10_init();
      //x10_init();
      int n = x10_nplaces();
      int i = x10_here();
      System.out.println("There are "+n+" Nodes and I am Node "+i);
   }
   public native void x10_init();
   public native int x10_nplaces();
   public native int x10_here();
} 
