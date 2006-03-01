/**
 * 
 */
package x10.runtime;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

/**
 * @author xinb
 *
 */
public interface FinishStateOps /*extends Remote*/ {
	//called remotedly from child activity
	public void notifySubActivityTermination() throws RemoteException;
    public void notifySubActivityTermination(Throwable t) throws RemoteException;
    
    //called locally only?
    public void notifySubActivitySpawn() throws RemoteException;
    public Stack exceptions() throws RemoteException;
    public void waitForFinish() throws RemoteException;
    public void pushException( Throwable t) throws RemoteException;
}
