/*
 * Created on Oct 3, 2004
 *
 */
package x10.runtime;

/**
 * @author Christoph von Praun
 *
 * A collective is used to record the membership and participation  
 * of activities in synchronization operations. For example, in a barrier
 * a number of activities participate that are recorded in a Collective.
 */
public interface Collective {
	
	/**
	 * Adds a party to the colelctive.
	 */
	void addParty();
	
	/**
	 * Announces that one of the parties has left the collective.
	 */
	void removeParty();
	
	/**
	 * Dissolves the collective once and for all. This method must only
	 * be called once. 
	 */
	void invalidate();
	
	/**
	 * @return true if this collective has not been dissolved yet.
	 */
	boolean isValid();
}

