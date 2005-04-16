/**
 * @author kemal 4/2005
 *
 * Testing behavior of multiple clock barriers with resume
 *
 * It is required in this test case
 * for A1 to pass its next prematurely and 
 * read x==2, while A2 and A3 read x==3.
 *
 * Possible parallel execution script:
 *
 * <code>
    A0: spawns A1 (registers A1 with a first);
    A0: spawns A2 (registers A2 with a,b first)
    A0: spawns A3 (registers A3 with b first)
    A0: terminates, no longer registered with a or b
    A3: start await operation until A1 advances
    A1: x++ (x==1 now)
    A1: a.resume()
    A1: next (wait until A1,A2 have both resumed a)
    A2: x++ (x==2 now)
    A2: a.resume();
    A2: b.resume(); 
    A2: next (wait until A2,A3 have both resumed b and A1,A2 have both resumed a)
    A1: next unblocks; // (since A2 resumed a) 
    A1: read x (x==2)             
    A1: set advanced_A1 flag to true
    A3: return from await operation (A1 has advanced) 
    A3: x++ (x==3 now)
    A3: b.resume()
    A3: next (wait until A2,A3 have both resumed b)
    A3: next unblocks immediately
    A3: read x (x==3)
    A2: next unblocks
    A2: read x (x==3)
 * </code>
 *
 */

class ClockTest15WithResume
{
  int x = 0; // global counter
  boolean advanced_A1=false; // signals that A1 executed next and read x==2
  public boolean run ()
  {
    finish
    /*A0*/
    {
      final clock a = clock.factory.clock ();
      final clock b = clock.factory.clock ();
      /*A1 */ async (here) clocked (a)
      {
	atomic x++;
	next;
	int tmp;
	atomic tmp = x;
	System.out.println("A1 advanced, x="+tmp);
	atomic advanced_A1=true;
	chk (tmp == 2);
	next;
      }
      /*A2 */ async (here) clocked (a, b)
      {
	atomic x++;
	next;
	int tmp;
	atomic tmp = x;
	System.out.println("A2 advanced, x="+tmp);
	chk (tmp == 3);
	next;
      }
      /*A3 */ async (here) clocked (b)
      {
	await(advanced_A1);
	atomic x++;
	next;
	int tmp;
	atomic tmp = x;
	System.out.println("A3 advanced, x="+tmp);
	chk (tmp == 3);
	next;
      }
    } /* end A0 */
    return true;
  }

  static void chk (boolean b)
  {
    if (!b)
      throw new Error ();
  }

  static void delay (int millis)
  {
    try {
      java.lang.Thread.sleep (millis);
    } catch (InterruptedException e) {
    }
  }

  public static void main (String args[])
  {
    final BoxedBoolean b=new BoxedBoolean();
    try { 
	finish b.val= (new ClockTest15WithResume()).run();
    } catch (Throwable e) {
	e.printStackTrace();
	b.val=false;
    }
    System.out.println ("++++++ " + (b.val ? "Test succeeded." : "Test failed."));
    System.exit (b.val ? 0 : 1);
  }
}

class BoxedBoolean {
	public boolean val;
}
