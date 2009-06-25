class  MyInt{
public int a;
};

public class ClockPipeline {

	
	public static void main(String args [])
	{
	
		final clock c = clock.factory.clock();
		final clock d = clock.factory.clock();
		final MyInt n1 = new MyInt();
		final MyInt n2 = new MyInt();
		final MyInt n3 = new MyInt();
		async clocked (c) {
			for (int i = 0; i < 100; i++)
			{
				if (i > 0) c.doNext(); // Wait for my peer(2nd task) to read
				n1.a = i;
				c.doNext(); // Signals that I have written
			}
		}
		
		async clocked (c, d) {
			for (int i = 0; i < 100; i++)
			{
				c.doNext(); // Wait for my peer (1st task) to signal
				if (i > 0) d.doNext(); // Wait for my peer (3rd task) to read
				n2.a = n1.a + 1;
				c.doNext(); // Signal that 1st task that I  have read
				d.doNext();  // Signal the 3rd task that I have written
			}
		
		}
		
		async clocked (d) {
			for (int i = 0; i < 100; i++)
			{
				d.doNext(); // Wait for my peer to signal
				n3.a = n2.a + 1;
				//System.out.println(n3.a);
				d.doNext(); // Signal that I have read
			}
		
		}
		
		c.drop();
		d.drop();
		
		
		
		}
	}
	


