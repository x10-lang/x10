package x10.runtime.xws;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A Job is used to submit work to a pool of workers. The programmer should subclass Job
 * and specify the task to be executed in spawnTask. 
 * 
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.Job")
public abstract class Job extends Closure {

  public native def this(p:Pool):Job;
	
  @Native("java", "#0.executionTime()")
  public native def executionTime():Int;

  @Native("java", "#0.getInt()")
  public native def getInt():int throws Exception;

}
