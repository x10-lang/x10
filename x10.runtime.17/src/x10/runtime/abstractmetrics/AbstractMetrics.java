/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.abstractmetrics;

public interface AbstractMetrics {

	public abstract long getTotalOps();

	public abstract long getCritPathOps();

	public abstract void addLocalOps(long n);
	
	public abstract void addCritPathOps(long n);

	public abstract void maxCritPathOps(long n);

	public abstract long getTotalUnblockedTime();

	public abstract long getCritPathTime();

	public abstract void maxCritPathTime(long t);

	public abstract long getResumeTime();

	public abstract void setResumeTime();

	public abstract void updateIdealTime();

	public abstract long getCurrentTime();

	public abstract void addUnblockedTime(long t);

}