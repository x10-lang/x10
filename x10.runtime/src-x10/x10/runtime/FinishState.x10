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
	 * An activity is spawned under this finish (called by spawner).
	 */
    global def notifySubActivitySpawn(place:Place):Void;

	/** 
	 * An activity is created under this finish (called by spawnee).
	 */
    global def notifyActivityCreation():Void;

    /** 
	 * An activity created under this finish has terminated.
	 * Also called be the activity governing the finish when it completes the finish body.
	 */
    global def notifyActivityTermination():Void;
    
	/** 
	 * Push an exception onto the stack.
	 */
    global def pushException(t:Throwable):Void;
}
