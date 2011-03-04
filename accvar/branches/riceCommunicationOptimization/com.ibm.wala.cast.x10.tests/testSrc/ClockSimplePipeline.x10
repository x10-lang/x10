public class ClockSim {

  static void read(int  buf)
  {
  	 // Read from input stream into buf
  }
  
  static void write(int  buf)
  {
  	// Write buf to output stream
  }


  public static void main(String args )
  {

     finish {  
	 
  	   System.out.println("Here");
      final int  buffer = -1 ; // = new int [100];
      final clock c = clock.factory.clock();
      c.drop();

	 
     /* Consumer Task */
      async clocked (c) {
      
      int  temp = -1; //= new int [100];
    
      for (int i = 0; i < 1; i++) {
        c.next();       // Wait for producer to write data
        //System.arraycopy (buffer, 0, temp, 0, 100);
        c.next();       // Signal that data has been read
        write(temp);   // Process data
      }
    }

		
		
		
      /* Producer Task */
      for (int i = 0; i <1; i++) {
        read(buffer);    // Get new data
        c.next();      // Signal that data has been written
        c.next();      // Wait for consumer to read
      }
      
     }

     
  }


    void error(clock c, boolean deregister) {
    if (deregister)
      c.drop(); // Drop the clock
    else
      c.resume();  // Skip ahead
  }
  
  


}



