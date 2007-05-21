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
		public String toString() {
			return "MainFrame(" + PC + ", " + x+")";
		}
	}
	Object lock;
	public Main(Object l) {
		super();
		lock = l;
		parent = null;
		joinCount=0;
		status = READY;
		frame = new MainFrame();
	}
	public static final int LABEL_1=1, LABEL_2=2, LABEL_3=3;
	@Override
	protected void compute(Worker w, Frame frame) {
		MainFrame f = (MainFrame) frame;
	
		if (f.PC!=LABEL_1 && f.PC!=LABEL_2) {
			f.PC=LABEL_1;
			// spawning
			int x = spawnTask(w);
			Closure c = w.popFrameCheck();
			if (c !=null) {
				if (w.lastFrame())
				((Main) c).result = x;
				return;
			}
			f.x=x;
		}
		
		if (f.PC <= LABEL_2) {
			f.PC=LABEL_3;
			if (sync()) {
				return;
			}
		}
		result=f.x;
		synchronized (lock) {
			lock.notifyAll();
		}
		setupReturn();
		return;
	}
	@Override
	public final void executeAsInlet() {}
	abstract public int spawnTask(Worker ws);
}
