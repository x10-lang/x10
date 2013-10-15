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

#include <x10/lang/RemoteOps.h>
#include <x10/lang/GlobalRef.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;

void x10::lang::RemoteOps::remoteAdd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
    Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
    x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, val);
}

void x10::lang::RemoteOps::remoteAdd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
    Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
    x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, (x10_ulong)val);
}


void x10::lang::RemoteOps::remoteAnd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
    Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
    x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, val);
}

void x10::lang::RemoteOps::remoteAnd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
    Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
    x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, (x10_ulong)val);
}


void x10::lang::RemoteOps::remoteOr(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
    Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
    x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, val);
}

void x10::lang::RemoteOps::remoteOr(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
    Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
    x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, (x10_ulong)val);
}


void x10::lang::RemoteOps::remoteXor(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
    Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
    x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, val);
}

void x10::lang::RemoteOps::remoteXor(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
    Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
    x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
    x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, (x10_ulong)val);
}

