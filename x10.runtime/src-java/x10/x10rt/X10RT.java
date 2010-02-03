package x10.x10rt;

public class X10RT {
    private enum State { UNINITIALIZED, BOOTED, TEARING_DOWN, TORN_DOWN };

    private static State state = State.UNINITIALIZED;
    private static Node[] nodes;
    private static Node here;
    private static int numProgressThreads;

    static final boolean REPORT_UNCAUGHT_USER_EXCEPTIONS = true;

    /**
     * Initialize the X10RT runtime.  This method must be called before any other
     * methods on this class or on any other X10RT related class can be successfully
     * invoked.
     *
     * @param nProgressThreads The number of progress threads to create to process
     *                           incoming messages.
     * @throws IllegalArgumentException if numProgressThreads is not positive
     */
    static {
      assert state.compareTo(State.TEARING_DOWN) < 0 : "X10RT is shutting down";
      assert state != State.BOOTED;

      // TODO: Need to make this dependent on command line argument/system property
      //       Eventually could be mpi, pgas_sockets, pgas_lapi, etc.
      System.loadLibrary("x10rt_mpi");

      initializeImpl();

      ActiveMessage.initializeMessageHandlers();

      nodes = new Node[numNodesImpl()];
      for (int i=0; i<nodes.length; i++) {
        nodes[i] = new Node(i);
      }
      here = nodes[hereImpl()];

      // Add a shutdown hook to automatically teardown X10RT as part of JVM teardown
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
        public void run() {
          synchronized(X10RT.class) {
            state = State.TEARING_DOWN;
            finishImpl();
            state = State.TORN_DOWN;
            System.err.flush();
            System.out.flush();
          }
        }}));

      // Create threads dedicated to poll/drain the message queue.
      Thread progressThread = new Thread(new Runnable(){
          public void run() {
              while (true) {
                  try {
                      ActiveMessage.processQueue();
                  } catch (Throwable e) {
                      if (REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                          e.printStackTrace();
                      }
                  }
                  Thread.yield();
              }
          }}, "X10RT Progress Thread");
      progressThread.setDaemon(true);
      progressThread.start();

      state = State.BOOTED;
    }

    /**
     * This is a blocking call.
     * All nodes must participate, and nobody returns from the call
     * until every node has entered.
     */
    public static void barrier() {
      assert state.compareTo(State.BOOTED) >= 0;
      barrierImpl();
    }

    /**
     * This is a blocking call.
     * Returns when all outstanding operations on this Node complete.
     */
    public static void fence() {
      assert state.compareTo(State.BOOTED) >= 0;
      fenceImpl();
    }

    /**
     * This is a non-blocking call.
     * Checks network for incoming messages and returns.
     */
    public static void poll() {
        assert state.compareTo(State.BOOTED) >= 0;
        if(0 == numProgressThreads) ActiveMessage.processQueue();
        pollImpl();
    }

    /**
     * Get the Node object that represents the Node where this process is executing.
     * @return the Node object that represents the Node where this process is executing.
     */
    public static Node here() {
      assert state.compareTo(State.BOOTED) >= 0;
      return here;
    }

    /**
     * Return the number of nodes in the computation.
     * @return the number of nodes in the computation.
     */
    public static int numNodes() {
      assert state.compareTo(State.BOOTED) >= 0;
      return nodes.length;
    }

    /**
     * Get the Node object that represents Node nodeId
     * @param nodeId the numeric id for the desired node.
     * @return the Node object that represents nodeId
     * @throws IllegalArgumentException if nodeId is not valid.
     */
    public static Node getNode(int nodeId) throws IllegalArgumentException {
      assert state.compareTo(State.BOOTED) >= 0;

      try {
        return nodes[nodeId];
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Invalid node id "+nodeId);
      }
    }

    static boolean isBooted() {
      return state.compareTo(State.BOOTED) >= 0;
    }

    /*
     * Native methods exported from x10rt.
     */
    private static native int initializeImpl();

    private static native int hereImpl();

    private static native int finishImpl();

    private static native int numNodesImpl();

    private static native void barrierImpl();

    private static native void fenceImpl();

    private static native void pollImpl();
}
