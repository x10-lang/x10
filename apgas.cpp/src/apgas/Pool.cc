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

#include <apgas/Task.h>
#include <apgas/Pool.h>

#include <x10aux/bootstrap.h>
#include <x10/lang/Runtime.h>

namespace apgas {

    static Pool* hack;

    void dummy_main(x10::lang::Rail<x10::lang::String*>* args) {
        hack->_mainTask->execute();
    }
    
    Pool::Pool(Task* mainTask) {
        _mainTask = mainTask;
        _mainTask->setPool(this);
    }

    void Pool::start() {
        // HACK: Whack x10.lang.Activity.DEALLOC_BODY to be false.
        x10::lang::Activity::FMGL(DEALLOC_BODY__get)();
        x10::lang::Activity::FMGL(DEALLOC_BODY) = false;

        char* args = {"APGAS_LIB"};
        hack = this;
        x10aux::real_x10_main(1, &args, &dummy_main);
        hack = NULL;
    }
        
    void Pool::runAsync(Task* task) {
        task->setPool(this);
        x10::lang::Runtime::runAsync(reinterpret_cast<x10::lang::VoidFun_0_0*>(task));
    }
        
    void Pool::runFinish(Task* task) {
        task->setPool(this);
        x10::lang::Runtime::runFinish(reinterpret_cast<x10::lang::VoidFun_0_0*>(task));
    }

    class FinishBlock : public Task {
        int numTasks;
        Task** tasks;
    public:
        FinishBlock(int nt, Task** ts) : numTasks(nt), tasks(ts) {}
        virtual void execute() {
            for (int i=0; i<numTasks; i++) {
                myPool->runAsync(tasks[i]);
            }
        }
    };
    
    void Pool::runFinish(int numTasks, Task* tasks[]) {
        FinishBlock fb(numTasks, tasks);
        runFinish(&fb);
    }        
}
