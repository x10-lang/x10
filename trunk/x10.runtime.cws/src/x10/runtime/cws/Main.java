package x10.runtime.cws;
import static x10.runtime.cws.ClosureStatus.*;


public abstract class Main extends Closure {
	public volatile int result;
	static class MainFrame extends Frame {
		int PC,x;
		public MainFrame() {
			super();
		}
		public Closure makeClosure() {
			return null;
		}
	}
	Object lock;
	public Main(Object l) {
		super();
		lock = l;
		parent = null;
		joinCount=0;
		status = READY;
		frame = null;
	}
	public static final int LABEL_1=1, LABEL_2=2, LABEL_3=3;
	@Override
	protected void compute(Worker w, Frame frame) {
		// get the frame and push it.
		// f must be a FibFrame.
		MainFrame f = (MainFrame) frame;
	
		if (f.PC!=LABEL_1 && f.PC!=LABEL_2) {
			f.PC=LABEL_1;
			// spawning
			int x = spawnTask(w);
			Closure c = w.popFrameCheck();
			if (c != null) {
				((Main) c).result = x;
				return;
			}
			f.x=x;
		}
		
		if (f.PC <= LABEL_2) {
			f.PC=LABEL_3;
			if (w.sync()) 
				return;
		}
		result=f.x;
		lock.notifyAll();
		setupReturn();
		return;
	}

	@Override
	public void executeAsInlet() {}

	abstract public int spawnTask(Worker ws);
}
