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

#include <stdio.h>

#include <apgas/Task.h>
#include <apgas/Pool.h>

#include <x10aux/bootstrap.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/Runtime.h>

// Wrap a task so it looks like an X10 closure...
class TaskWrapper : public x10::lang::Closure {
private:
    apgas::Task *myTask;

public:    
    static x10::lang::VoidFun_0_0::itable<TaskWrapper> _itable;
    static x10aux::itable_entry _itables[2];
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    void __apply() {
        myTask->execute();
    }
    
    TaskWrapper(apgas::Task* t) : myTask(t) {}
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }

    x10aux::serialization_id_t _get_serialization_id() {
        assert(false);
        return 0;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        assert(false);
    }
};


x10::lang::VoidFun_0_0::itable<TaskWrapper>TaskWrapper::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &TaskWrapper::__apply, &TaskWrapper::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry TaskWrapper::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &TaskWrapper::_itable),x10aux::itable_entry(NULL, NULL)};



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
        char* args = {"APGAS_LIB"};
        hack = this;
        x10aux::real_x10_main(1, &args, &dummy_main);
        hack = NULL;
    }
        
    void Pool::runAsync(Task* task) {
        task->setPool(this);
        TaskWrapper* tw = new (Pool::alloc<TaskWrapper>()) TaskWrapper(task);
        x10::lang::Runtime::runAsync(reinterpret_cast<x10::lang::VoidFun_0_0*>(tw));
    }
        
    void Pool::runFinish(Task* task) {
        task->setPool(this);
        TaskWrapper* tw = new (Pool::alloc<TaskWrapper>()) TaskWrapper(task);
        x10::lang::Runtime::runFinish(reinterpret_cast<x10::lang::VoidFun_0_0*>(tw));
        Pool::dealloc(tw);
    }
}
