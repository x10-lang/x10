/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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