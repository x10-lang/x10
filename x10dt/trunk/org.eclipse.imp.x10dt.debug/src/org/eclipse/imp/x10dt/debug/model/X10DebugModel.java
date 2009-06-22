package org.eclipse.imp.x10dt.debug.model;

public class X10DebugModel  {
	public static X10DebugTarget fX10DebugTarget; 
	public static IDebugTarget newX10DebugTarget(){
        X10DebugModel.fX10DebugTarget =  new X10DebugTarget();
        return X10DebugModel.fX10DebugTarget;
    }
    public static IDebugTarget newX10DebugTarget(ILaunch launch){
        X10DebugModel.fX10DebugTarget =  new X10DebugTarget(launch);
        return X10DebugModel.fX10DebugTarget;
    }
    
    public static IDebugTarget getX10DebugTarget(){
    	return X10DebugModel.fX10DebugTarget;
    }
}