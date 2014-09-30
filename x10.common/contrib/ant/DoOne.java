/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * A variation of the Sequential task from Ant.
 * Execute a sequence of tasks until the first one succeeds.
 */
public class DoOne extends Task implements TaskContainer {

    /** Optional Vector holding the nested tasks */
    private Vector nestedTasks = new Vector();

    /**
     * Add a nested task to Doone.
     * <p>
     * @param nestedTask  Nested task to execute Doone
     * <p>
     */
    public void addTask(Task nestedTask) {
        nestedTasks.addElement(nestedTask);
    }

    /**
     * Execute nestedTasks until one succeeds
     *
     * @throws BuildException if all nested tasks fails.
     */
    public void execute() throws BuildException {
        for (Iterator i = nestedTasks.iterator(); i.hasNext();) {
            Task nestedTask = (Task) i.next();
            try {
                nestedTask.perform();
                return; // Success!
            } catch (BuildException e) {
            }
        }
        throw new BuildException("No task succeeded");
    }
}
