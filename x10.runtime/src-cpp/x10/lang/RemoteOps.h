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

#ifndef __X10_LANG_REMOTEOPS_H
#define __X10_LANG_REMOTEOPS_H

#include <x10aux/config.h>

#define X10_LANG_RAIL_H_NODEPS
#include <x10/lang/Rail.h>
#undef X10_LANG_RAIL_H_NODEPS

#define X10_LANG_GLOBALREF_H_NODEPS
#include <x10/lang/GlobalRef.h>
#undef X10_LANG_GLOBALREF_H_NODEPS

namespace x10 {
    namespace lang { 

        class RemoteOps {
        public:
            /*
             * Operations on GlobalRef: memory may be congruent or non-congruent
             */
            static void remoteAdd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
                Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
                x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, val);
            }
                
            static void remoteAdd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
                Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
                x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, (x10_ulong)val);
            }                

            static void remoteAnd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
                Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
                x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, val);
            }                

            static void remoteAnd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
                Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
                x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, (x10_ulong)val);
            }
                

            static void remoteOr(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
                Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
                x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, val);
            }                

            static void remoteOr(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
                Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
                x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, (x10_ulong)val);
            }                

            static void remoteXor(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val) {
                Rail<x10_ulong>* remoteRail = (Rail<x10_ulong>*)target->value;
                x10_ulong* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, val);
            }
                
            static void remoteXor(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val) {
                Rail<x10_long>* remoteRail = (Rail<x10_long>*)target->value;
                x10_long* remoteAddr = &(remoteRail->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op(target->location, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, (x10_ulong)val);
            }

            /*
             * Optimized unsafe path operates directly on local addresses.
             * Only sensible with congruent memory (but this is not checked)
             */
            static void remoteAdd(x10_long placeId, Rail<x10_ulong>* target, x10_long idx, x10_ulong val) {
                x10_ulong* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, val);
            }

            static void remoteAdd(x10_long placeId, Rail<x10_long>* target, x10_long idx, x10_ulong val) {
                x10_long* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_ADD, val);
            }

            static void remoteAnd(x10_long placeId, Rail<x10_ulong>* target, x10_long idx, x10_ulong val) {
                x10_ulong* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, val);
            }

            static void remoteAnd(x10_long placeId, Rail<x10_long>* target, x10_long idx, x10_ulong val) {
                x10_long* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_AND, val);
            }

            static void remoteOr(x10_long placeId, Rail<x10_ulong>* target, x10_long idx, x10_ulong val) {
                x10_ulong* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, val);
            }

            static void remoteOr(x10_long placeId, Rail<x10_long>* target, x10_long idx, x10_ulong val) {
                x10_long* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_OR, val);
            }

            static void remoteXor(x10_long placeId, Rail<x10_ulong>* target, x10_long idx, x10_ulong val) {
                x10_ulong* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, val);
            }

            static void remoteXor(x10_long placeId, Rail<x10_long>* target, x10_long idx, x10_ulong val) {
                x10_long* remoteAddr = &(target->raw[idx]);  // Just doing address arithmetic, not actually dereferencing anything.
                ::x10aux::remote_op((x10rt_place)placeId, (x10rt_remote_ptr)remoteAddr, X10RT_OP_XOR, val);
            }

            static void registerForRemoteOps(Rail<x10_long>* rail) {
                ::x10aux::register_mem(&rail->raw[0], sizeof(x10_long)*rail->FMGL(size));
            }

            static void registerForRemoteOps(Rail<x10_ulong>* rail) {
                ::x10aux::register_mem(&rail->raw[0], sizeof(x10_ulong)*rail->FMGL(size));
            }
        };
    }
}

#endif /* __X10_LANG_REMOTEOPS_H */
        
