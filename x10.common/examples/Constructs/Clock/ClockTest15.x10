/**
 * @author kemal 4/2005
 *
 * Testing behavior of multiple clock barriers without resume
 *
 * (need language clarification resulting from resume/next semantics).
 *
 * Is 'next' is the same as '(resume my registered clocks); next;'?
 * If yes, the following parallel execution order is legitimate 
 * (but intuitively not what we want).
 * I.e. A1 reads x==2 by passing its next prematurely, 
 * but A2 and A3 read x==3
 *
 * <code>
    A0: spawns A1 (registers A1 with a first);
    A0: spawns A2 (registers A2 with a,b first)
    A0: spawns A3 (registers A3 with b first)
    A0: terminates, no longer registered with a or b
    A3: start delay operation (wait for N milliseconds)
    A1: x++ (x==1 now)
    A1: a.resume() // part of next
    A1: next (wait until A1,A2 have both resumed a)
    A2: x++ (x==2 now)
    A2: a.resume(); // part of next
    A2: b.resume(); // part of next
    A2: next (wait until A2,A3 have both resumed b and A1,A2 have both resumed a)
    A1: next unblocks; // (since A2 resumed a) 
    A1: read x (x==2)                         <=== bug here
    A3: return from delay operation
    A3: x++ (x==3 now)
    A3: b.resume() // part of next 
    A3: next (wait until A2,A3 have both resumed b)
    A3: next unblocks immediately
    A3: read x (x==3)
    A2: next unblocks
    A2: read x (x==3)
 * </code>
 *
 * The behavior above is present in the current x10 
 * implementation as of 4/16/05
 */

class ClockTest15
{
  int x = 0; // global counter
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
	chk (tmp == 3);
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
	delay(5000);
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
	finish b.val= (new ClockTest15 ()).run ();
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
