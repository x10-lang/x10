
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

/**
 * <p>
 * The state needed to run a task typically can be broken down into
 * state that is common across all tasks (e.g. parameters), and
 * state specific to the task. So we introduce the concept of a 
 * <code>Runner</code>. A Runner is associated with a particular
 * kind of task and typically contains the state that is common
 * across all tasks.
 *
 *<p>
 * Running a task may cause other tasks to be created. 
 * These tasks are pushed onto the given stack.
 * @author vj
 */

import x10.util.Stack;

/**
 * A user program hat intends to use <code>GlobalRunner</code> must 
 * provide an implementation of <code>TaskFrame</code>. The <code>TaskFrame[T]</code>
 * is parametrized on the task type <code>T</code>. This is the type of data that is stored
 * on the stacks used for global load balancing. 
 * <p> To use global load balancing, user code must first create an instance of <code>GlobalRunner</code>.
 * The constructor for <code>GlobalRunner</code> takes various parameters that govern the behavior of 
 * global load balancing. It also takes a nullary function that returns an instance of the user supplied
 * <code>TaskFrame[T]</code>. This function will be invoked once at each place at which <code>GlobalRunner</code>
 * is intended to run. 
 * 
 * <p> Typically, the TaskFrame istance
 * A TaskFrame provides additional parameters that may be necessary to
 * execute a task. Typically, there is one TaskFrame instance per place.
 */

public abstract class  TaskFrame[A, B] {
    /**
     * Run this task in the given task frame.  Implementations of
     * this method will use the stack to create additional tasks,
     * if necessary.
     */
    abstract public def runTask(t:A, stack:Stack[A]):Void offers B;
    
    /**
     * Run this task as a root task. Implementations of this method
     * will use the stack to create additional tasks, if necessary.
     */
    abstract public def runRootTask(t:A, stack:Stack[A]):Void offers B;
}