/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.runtime;

/**
 * @author tardieu 
 */
interface FinishState {

	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
    def notifySubActivitySpawn(place:Place):Void;

    /** 
	 * An activity created under this finish has terminated.
	 */
    def notifySubActivityTermination():Void;
    
	/** 
	 * Push an exception onto the stack.
	 */
    def pushException(t:Throwable):Void;
    
    def rid(): RID;

    def incr():Void;
}
