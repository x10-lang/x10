import java.lang.Integer;

public class ClockLinearSearch{


	public static void main(String args [])
	{
	  	final int MAX = 1000;
		final int [] a = new int [MAX] ;
		final int key = -1;
		for (int i = 0; i < MAX; i++)
			a[i] = i;
		final clock c = clock.factory.clock();
		async clocked(c){
			for (int j = 0; j < MAX/2; j++)
				{
					if (a[j] == key)
						System.out.println(j);
						c.doNext();			
				}	
		}

			for (int j = MAX/2; j < MAX; j++)
				{
					if (a[j] == key)
						System.out.println(j);
					c.doNext();			
				}	
	
		
		
	}
	
}

