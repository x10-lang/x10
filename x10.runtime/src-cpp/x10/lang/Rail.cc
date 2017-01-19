/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

#include <x10/lang/Rail.h>
#include <x10/lang/NegativeArraySizeException.h>

x10aux::RuntimeType x10::lang::Rail<void>::rtt;

using namespace x10aux;

namespace x10 {
    namespace lang {

        const serialization_id_t Rail_copy_to_serialization_id =
            NetworkDispatcher::addPutFunctions(Rail_put_notifier,
                                               Rail_put_notifier);

        const serialization_id_t Rail_uncounted_copy_to_serialization_id =
            NetworkDispatcher::addPutFunctions(Rail_uncounted_put_notifier,
                                               Rail_uncounted_put_notifier);

        const serialization_id_t Rail_copy_from_serialization_id =
            NetworkDispatcher::addGetFunctions(Rail_get_notifier,
                                               Rail_get_notifier);

        const serialization_id_t Rail_uncounted_copy_from_serialization_id =
            NetworkDispatcher::addGetFunctions(Rail_uncounted_get_notifier,
                                               Rail_uncounted_get_notifier);

        void Rail_copyToBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place dstPlace, bool overlap, VoidFun_0_0* notif) {
            if (dstPlace->FMGL(id) == x10aux::here) {
                if (overlap) {
                    // potentially overlapping, use memmove
                    memmove(dstAddr, srcAddr, numBytes);
                } else {
                    memcpy(dstAddr, srcAddr, numBytes);
                }                
                if (NULL != notif) {
                    VoidFun_0_0::__apply(notif);
                }
            } else {
                x10aux::place dst_place = dstPlace->FMGL(id);
                x10aux::serialization_buffer buf;
                x10::xrx::FinishState* fs = x10::xrx::Runtime::activity()->finishState();
                if (NULL == notif) {
                    fs->notifySubActivitySpawn(Place::_make(parent(dst_place))); // must be remote; don't need separate notfyRCC
                    buf.write(fs);
                    buf.write(x10aux::here);
                    x10aux::send_put(dst_place, Rail_copy_to_serialization_id, buf, srcAddr, dstAddr, numBytes);
                } else {
                    // Needed to prevent the worker that calls stopFinish from trying to
                    // help and improperly scheduling an activity from an unrelated finish.
                    fs->notifyRemoteContinuationCreated();
                    buf.write(notif);
                    x10aux::send_put(dst_place, Rail_uncounted_copy_to_serialization_id, buf, srcAddr, dstAddr, numBytes);
                }
            }
        }

        void Rail_copyFromBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place srcPlace, bool overlap, VoidFun_0_0* notif) {
            if (srcPlace->FMGL(id) == x10aux::here) {
                Rail_copyBody(srcAddr, dstAddr, numBytes, overlap);
                if (NULL != notif) {
                    VoidFun_0_0::__apply(notif);
                }
            } else {
                x10aux::place src_place = srcPlace->FMGL(id);
                x10aux::serialization_buffer buf;
                x10::xrx::FinishState* fs = x10::xrx::Runtime::activity()->finishState();
                if (NULL == notif) {
                    fs->notifySubActivitySpawn(Place::_make(x10aux::here));
                    // Needed to prevent the worker that calls stopFinish from trying to
                    // help and improperly scheduling an activity from an unrelated finish.
                    fs->notifyRemoteContinuationCreated();
                    x10_int getId = x10::xrx::GetRegistry::registerGet(srcPlace, fs, NULL);
                    buf.write(getId);
                    x10aux::send_get(src_place, Rail_copy_from_serialization_id, buf, srcAddr, dstAddr, numBytes);
                } else {
                    // Needed to prevent the worker that calls stopFinish from trying to
                    // help and improperly scheduling an activity from an unrelated finish.
                    fs->notifyRemoteContinuationCreated();
                    x10_int getId = x10::xrx::GetRegistry::registerGet(srcPlace, fs, NULL);
                    buf.write(getId);
                    x10aux::send_get(src_place, Rail_uncounted_copy_from_serialization_id, buf, srcAddr, dstAddr, numBytes);
                }
            }
        }

        void Rail_copyBody(void *srcAddr, void *dstAddr, x10_int numBytes, bool overlap) {
            if (numBytes > 0) {
                if (overlap) {
                    // potentially overlapping, use memmove
                    memmove(dstAddr, srcAddr, numBytes);
                } else {
                    memcpy(dstAddr, srcAddr, numBytes);
                }
            }
        }
        
        void Rail_get_notifier(deserialization_buffer &buf, x10_int) {
            x10_int id = buf.read<x10_int>();
            x10::xrx::GetRegistry__GetHandle *gh = x10::xrx::GetRegistry::completeGet(id);
            x10::xrx::FinishState* fs = gh->FMGL(finishState);
            // Perform the actions of both notifyActivityCreation and
            // notifyActivityTermination in a single non-blocking action.
            // This notifier is often running on an @Immediate worker thread.
            fs->notifyActivityCreatedAndTerminated(Place::_make(x10aux::here));
        }

        void Rail_uncounted_get_notifier(deserialization_buffer &buf, x10_int) {
            x10_int id = buf.read<x10_int>();
            x10::xrx::GetRegistry__GetHandle *gh = x10::xrx::GetRegistry::completeGet(id);
            VoidFun_0_0* notif = gh->FMGL(notifier);
            VoidFun_0_0::__apply(notif);
        }

        void Rail_put_notifier(deserialization_buffer &buf, x10_int) {
            x10::xrx::FinishState* fs = buf.read<x10::xrx::FinishState*>();
            place src = buf.read<place>();
            // Perform the actions of both notifyActivityCreation and
            // notifyActivityTermination in a single non-blocking action.
            // This notifier is often running on an @Immediate worker thread.
            fs->notifyActivityCreatedAndTerminated(Place::_make(src));
        }

        void Rail_uncounted_put_notifier(deserialization_buffer &buf, x10_int) {
            VoidFun_0_0* notif = buf.read<VoidFun_0_0*>();
            VoidFun_0_0::__apply(notif);
        }
        
        void rail_copyRaw(void *srcAddr, void *dstAddr, x10_long numBytes, bool overlap) {
            if (overlap) {
                // potentially overlapping, use memmove
                memmove(dstAddr, srcAddr, (size_t)numBytes);
            } else {
                memcpy(dstAddr, srcAddr, (size_t)numBytes);
            }
        }

        void throwArrayIndexOutOfBoundsException(x10_long index, x10_long length) {
            char *msg = alloc_printf("Index %lld out of range (length is %lld)", (long long)index, (long long)length);
            throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
        }

        void throwNegativeArraySizeException() {
            throwException(x10::lang::NegativeArraySizeException::_make());
        }            

        void failAllocNoPointers(const char* msg) {
            x10aux::throwException(x10::lang::IllegalArgumentException::_make(x10aux::makeStringLit(msg)));
        }
    }
}
