public class ClockProducerConsumer {

  static void read(int [] buf) {
    // Read from input stream into buf
	buf [0] = 0 ;
  }
  
  static void write(int [] buf) {
    // Write buf to output stream
  }

  public static void main(String [] args)
  {
      final int[]  buffer = new int [100];
      final clock c = clock.factory.clock();
	 
     /* Consumer Task */
      async clocked (c) {
      int [] temp = new int [100];
      for (int i = 0; i < 10; i++) {
        c.next();       // Wait for producer to write data
        System.arraycopy (buffer, 0, temp, 0, 100); // Copy buffer into temp
        c.next();       // Signal that data has been read
        write(temp);   // Process data
      }
    }

      /* Producer Task */
      for (int i = 0; i <10; i++) {
        read(buffer);    // Get new data
        c.next();      // Signal that data has been written
        c.next();      // Wait for consumer to read
      }
  }


   void error(clock c, boolean deregister) {
      if (deregister)
        c.drop(); // Drop the clock
      else
        c.resume();  // Skip ahead
   }
}



