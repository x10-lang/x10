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

#include <apgas/Task.h>

namespace apgas {
    x10::lang::VoidFun_0_0::itable<Task>Task::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &Task::__apply, &Task::toString, &x10::lang::Closure::typeName);
    x10aux::itable_entry Task::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &Task::_itable),x10aux::itable_entry(NULL, NULL)};

}
    
