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

#include <x10aux/config.h>
#include <x10aux/init_dispatcher.h>

#include <x10/lang/Runtime.h>
#include <x10/lang/Closure.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

InitDispatcher *InitDispatcher::it;

void
InitDispatcher::runInitializers() {
    if (NULL != it) {
        it->runInitializers_();
    }
}

class x10aux::InitClosure : public x10::lang::Closure
{
    protected:
        Initializer init;

    public:

        static x10::lang::VoidFun_0_0::itable<InitClosure> _itable;
        static x10aux::itable_entry _itables[2];

        virtual x10aux::itable_entry* _getITables() { return _itables; }

        // closure body
        void __apply () {
            init();
        }

        InitClosure(Initializer i) : init(i) { }

        virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0) {
            return false; // FIXME: should we be able to compare function types structurally?
        }

        const x10aux::RuntimeType *_type() const {return x10aux::getRTT<x10::lang::VoidFun_0_0>();}

        ref<x10::lang::String> toString() {
            return x10::lang::String::Lit("x10aux::InitClosure ("__FILELINE__")");
        }

        virtual x10aux::serialization_id_t _get_serialization_id() {
            assert(false); // We should never be serializing this closure
            return 0;
        }

        virtual void _serialize_body(x10aux::serialization_buffer &) {
            assert(false); // We should never be serializing this closure
        }
};

x10::lang::VoidFun_0_0::itable<InitClosure> InitClosure::_itable(&InitClosure::equals, &InitClosure::hashCode,
                                                                 &InitClosure::__apply,                                                                  
                                                                 &InitClosure::toString, &InitClosure::typeName);

x10aux::itable_entry InitClosure::_itables[2] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &_itable),
    x10aux::itable_entry(NULL, NULL)
};


void
InitDispatcher::runInitializers_() {
    _S_("There are "<<initc<<" initializers to run");
    for (int i=0 ; i<initc ; ++i) {
        x10aux::ref<x10::lang::VoidFun_0_0> init_closure =
            x10aux::ref<InitClosure>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(InitClosure)))
                                     InitClosure(initv[i]));
        x10::lang::Runtime::runAsync(init_closure);
    }
    initc = -1;
    _S_("====================== STATIC INITIALIZATION DONE ======================");
}

void *
InitDispatcher::addInitializer(Initializer init) {
    if (NULL == it) {
        it = new (system_alloc<InitDispatcher>()) InitDispatcher();
    }
    it->addInitializer_(init);
    return (void*)0x7777; // we call addInitializer(...) to initialise the global vars
}

void
InitDispatcher::addInitializer_(Initializer init) {
    if (initc<0) {
        fprintf(stderr,"Adding initializer too late!");
        abort();
    }
    if (initsz<=(size_t)initc) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = initsz+1;
        // do not use GC
        initv = (Initializer*)::realloc(initv, newsz*sizeof(Initializer));
        initsz = newsz;
    }
    initv[initc++] = init;
}



// vim:tabstop=4:shiftwidth=4:expandtab
