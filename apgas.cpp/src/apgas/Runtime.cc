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

#include <stdio.h>

#include <apgas/Runtime.h>
#include <apgas/Task.h>

#include <x10aux/alloc.h>
#include <x10aux/bootstrap.h>
#include <x10/xrx/Runtime.h>
#include <x10/xrx/Runtime__Watcher.h>

namespace apgas {

    static Runtime theRuntime;

    Runtime* Runtime::getRuntime() { return &theRuntime; }
    
    void Runtime::start(int argc, char** argv) {
        x10aux::apgas_main(argc, argv);
        if (here() != 0) {
            x10::xrx::Runtime::join();
        }
    }

    void Runtime::terminate() {
        x10::xrx::Runtime::terminateAllJob();
    }
    
    int Runtime::here() {
        return x10::xrx::Runtime::hereInt();
    }

    int Runtime::numPlaces() {
        return x10aux::num_places;
    }
    
    void Runtime::runSync(Task* task) {
        task->setRuntime(this);
        x10::xrx::Runtime__Watcher* xrx_watcher = x10::xrx::Runtime::submit(reinterpret_cast<x10::lang::VoidFun_0_0*>(task));
        xrx_watcher->await();
    }

    void Runtime::runAsync(Task* task) {
        task->setRuntime(this);
        x10::xrx::Runtime::runAsync(reinterpret_cast<x10::lang::VoidFun_0_0*>(task));
    }

    void Runtime::runAsyncAt(int place, RemoteTask* task) {
        x10::xrx::Runtime::runAsync(x10::lang::Place::_make(place),
                                    reinterpret_cast<x10::lang::VoidFun_0_0*>(task),
                                    NULL);
    }
    
    void Runtime::runFinish(Task* task) {
        task->setRuntime(this);
        x10::xrx::Runtime::runFinish(reinterpret_cast<x10::lang::VoidFun_0_0*>(task));
    }

    class FinishBlock : public Task {
        int numTasks;
        Task** tasks;
    public:
        FinishBlock(int nt, Task** ts) : numTasks(nt), tasks(ts) {}
        virtual void execute() {
            for (int i=0; i<numTasks; i++) {
                myRuntime->runAsync(tasks[i]);
            }
        }
    };
    
    void Runtime::runFinish(int numTasks, Task* tasks[]) {
        FinishBlock fb(numTasks, tasks);
        runFinish(&fb);
    }        

    int Runtime::numWorkers() {
        return x10::xrx::Runtime::FMGL(NTHREADS__get)();
    }
        
    void* Runtime::alloc_impl(size_t size) {
        return x10aux::alloc_internal(size, true);
    }
            
    void Runtime::dealloc_impl(void* obj) {
        x10aux::dealloc_internal(obj);
    }

    void* Runtime::realloc_impl(void* src, size_t dsz) {
        return x10aux::realloc_internal(src, dsz);
    }
}
