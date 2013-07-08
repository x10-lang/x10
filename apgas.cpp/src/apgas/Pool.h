/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

#ifndef APGAS_POOL_H
#define APGAS_POOL_H

namespace apgas {
    class Task;

    class Pool {
    private:
        Task* _mainTask;

    public:
        /**
         * Create a pool and specify the main task that the pool should execute.
         * Note that this does not begin executing mainTask; execution does not
         * start until the start method is called on the Pool.
         */
        Pool(Task* mainTask) : _mainTask(mainTask) {} 

        /**
         * Initialize the APGAS runtime and start executing the mainTask.
         * This method will not return until the mainTask and the implicit
         * Finish that wraps it terminate
         */
        void start();
        
        /**
         * Schedule the argument Task as an async to be executed
         * in the current Place.
         */
        void runAsync(Task* task);

        /**
         * Execute the body of the argument Task as the body of a Finish
         * statement. 
         */
        void runFinish(Task* task);
    };
}

#endif /* APGAS_POOL_H */
