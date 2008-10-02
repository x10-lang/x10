/*
 *
 * (C) Copyright IBM Corporation 2007-2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.xws;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The worker for Cilk-style work stealing. 
 * Instances of this worker are created by Pool. 
 * 
 * API note: Code written by users of the work-stealing API is not intended
 * to subclass this class. Such code can invoke only the public methods of this
 * class. Such code must live outside the x10.runtime.cws package.
 * 
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.Worker")
public class Worker {


  /**
   * Push a frame onto the stack of the current
   * closure (the closure at the bottom of the deque).
   * Called by client code in the body of a procedure
   * which has a spawn async before the first spawn.
   * @param frame -- the frame to be pushed.
   */
  @Native("java", "#0.pushFrame(#1)")
  public native def pushFrame(frame:Frame):void;

  /**
   * Push a frame onto the stack of the current
   * closure (the closure at the bottom of the deque)
   * for execution in the next phase.
   * Called by client code in the body of a procedure
   * which has a spawn async next before the first spawn.
   * TODO: I suspect this comment isn't quite right -- dave.
   * @param frame -- the frame to be pushed.
   */
  @Native("java", "#0.pushFrameNext(#1)")
  public native def pushFrameNext(frame:Frame):void;

  /**
   * Pop the last frame from the stack.
   *
   */
  @Native("java", "#0.popFrame()")
  public native def popFrame():void;


  /**
   * Method to be called by user code after every method invocation that may have
   * pushed a frame on the frame stack. Detects whether the worker has been mugged. 
   * If so, throws a StealAbort. This will cause the worker's stack to unwind all the way
   * to the scheduler, which catches and discards this exception.
   * <p>
   * If the worker has not been mugged, this method does nothing.
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal()")
  public native def abortOnSteal():void throws StealAbort;


  
  /**
   * Method to be called by user code after every method invocation that may have
   * pushed a frame on the frame stack, provided that the method returns an int value.
   * This value should be passed into this call. If the worker has been mugged, this
   * value will be squirrelled away in the promoted closure for the victim, 
   * and from there it will make its way to the stolen frame. 
   * Also, a StealAbort will be thrown. This will cause the Java stack to unwind all the way
   * to the scheduler, which catches and discards this exception.
   * 
   * @param result The result to be passed into the promoted closure if a steal has occured.
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal(#1)")
  public native def abortOnSteal(result:int):void throws StealAbort;

	
  /**
   * @see abortOnSteal(int result)
   * @param result
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal(#1)")
  public native def abortOnSteal(result:double):void throws StealAbort;

  /**
   * @see abortOnSteal(int result)
   * @param result
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal(#1)")
  public native def abortOnSteal(result:float):void throws StealAbort;

  /**
   * @see abortOnSteal(int result)
   * @param result
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal(#1)")
  public native def abortOnSteal(result:long):void throws StealAbort;

  /**
   * @see abortOnSteal(int result)
   * @param result
   * @throws StealAbort
   */
  @Native("java", "#0.abortOnSteal(#1)")
  public native def abortOnSteal(result:Object):void throws StealAbort;
}


