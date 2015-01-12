/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef APGAS_POOL_H
#define APGAS_POOL_H

#include <x10aux/alloc.h>

namespace apgas {
    class Task;
    class RemoteTask;
    
    class Runtime {
    public:
        /**
         * Get the Runtime object for the current Place.
         */
        static Runtime* getRuntime(void);

        /**
         * Start the Runtime.
         * Must be called before any other methods are called on the Runtime.
         * This method must be called in all Places, but will only return in Place 0.
         */
        void start(int argc, char** argv);

        /**
         * Stop the Runtime at all Places.
         */
        void terminate();

        /**
         * Return the id of the current Place
         */
        int here(void);

        /**
         * Return the total number of Places.
         * Places are densely numbered from 0..numPlaces()-1
         */
        int numPlaces();
        
        /**
         * Spawn a new task to be synchronously executed in the current Place.
         * Will not return until task and all of its spawned children tasks complete.
         */
        void runSync(Task* task);
        
        /**
         * Spawn a new task to be asynchronously executed as a child of the
         * currently executing Task in the current Place.
         * Returns immediately (does not wait for task to execute)
         */
        void runAsync(Task* task);

        /**
         * Schedule the argument Task as an async to be asynchronously executed
         * at the argument Place.
         * Returns immediately (does not wait for task to execute)
         */
        void runAsyncAt(int place, RemoteTask* task);
        
        /**
         * Execute the body of the argument Task as the body of a Finish
         * statement in the current Task.
         * Will not return until task and all of its spawned children tasks complete.
         */
        void runFinish(Task* task);

        /**
         * Execute the argument tasks within a newly created finish scope.
         * A shorthand for a finish block that contains runAsync of all argument tasks.
         * Will not return until task and all of its spawned children tasks complete.
         */
        void runFinish(int numTasks, Task** tasks);

        /**
         * Return the number of worker threads available to execute asyncs
         */
        int numWorkers(void);
        
        template<class T> static inline T* alloc(size_t size = sizeof(T)) { return (T*)alloc_impl(size); }
        template<class T> static inline void dealloc(const T* obj) { dealloc_impl((void*)obj); }
        template<class T> static inline T* realloc(T* src, size_t dsz) { return (T*)realloc_impl(src, dsz); }

    private:
        static void* alloc_impl(size_t size);
        static void dealloc_impl(void* obj);
        static void* realloc_impl(void* src, size_t dsz);
    };
}

#endif /* APGAS_POOL_H */
