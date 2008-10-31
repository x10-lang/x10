package x10.runtime.xws;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A pool of workers for executing X10 programs.
 * 
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.Pool")
public class Pool {

  public native def this():Pool;
  public native def this(poolSize:int):Pool;

  @Native("java", "#0.getPoolSize()")
  public native def getPoolSize():Int;
	
  @Native("java", "#0.isQuiescent()")
  public native def isQuiescent():boolean;

  @Native("java", "#0.isShutdown()")
  public native def isShutdown():boolean;

  @Native("java", "#0.isTerminated()")
  public native def isTerminated():boolean;

  @Native("java", "#0.shutdown()")
  public native def shutdown():void;

  @Native("java", "#0.shutdownNow()")
  public native def shutdownNow():void;

  @Native("java", "#0.getStealAttempts()")
  public native def getStealAttempts():long;

  @Native("java", "#0.getStealCount()")
  public native def getStealCount():long;

  @Native("java", "#0.submit(#1)")
  public native def submit(j:Job):void;

  @Native("java", "#0.invoke(#1)")
  public native def invoke(j:Job):void;
}
