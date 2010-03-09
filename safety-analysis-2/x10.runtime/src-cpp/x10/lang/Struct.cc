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

#include <x10/lang/Struct.h>

using namespace x10aux;
using namespace x10::lang;

void Struct_methods::_instance_init(Struct& this_) {
    _I_("Doing initialisation for class: x10::lang::Struct");
}

void Struct_methods::_constructor(Struct& this_) {
}

x10_boolean Struct::_struct_equals(Struct that) {
    return true;
}

void Struct::_serialize(Struct this_, serialization_buffer& buf) {
}

void Struct::_deserialize_body(deserialization_buffer& buf) {
}

x10_int Struct::hashCode() {
    return 0;
}

RuntimeType Struct::rtt;
void Struct::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    rtt.initStageTwo("x10.lang.Struct", 0, NULL, 0, NULL, NULL);
}

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
