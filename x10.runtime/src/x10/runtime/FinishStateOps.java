/**
 * 
 */
package x10.runtime;

import java.util.Stack;

/**
 * @author xinb
 *
 */
public interface FinishStateOps {
	//called remotedly from child activity
	public void notifySubActivityTermination();
    public void notifySubActivityTermination(Throwable t);
    public void notifySubActivitySpawn();
    
    //called locally only?
    public Stack exceptions();
    public void waitForFinish();
    public void pushException( Throwable t);
    public void notifySubActivitySpawnAtChild(boolean fromRoot);
    public boolean notShadow();
}
