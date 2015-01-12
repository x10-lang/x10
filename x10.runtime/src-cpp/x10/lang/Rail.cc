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

#include <x10/lang/Rail.h>
#include <x10/lang/NegativeArraySizeException.h>

x10aux::RuntimeType x10::lang::Rail<void>::rtt;

using namespace x10aux;

namespace x10 {
    namespace lang {

        const serialization_id_t Rail_copy_to_serialization_id =
            NetworkDispatcher::addPutFunctions(Rail_buffer_finder,
                                               Rail_notifier,
                                               Rail_buffer_finder,
                                               Rail_notifier);

        const serialization_id_t Rail_uncounted_copy_to_serialization_id =
            NetworkDispatcher::addPutFunctions(Rail_buffer_finder,
                                               Rail_uncounted_notifier,
                                               Rail_buffer_finder,
                                               Rail_uncounted_notifier);

        const serialization_id_t Rail_copy_from_serialization_id =
            NetworkDispatcher::addGetFunctions(Rail_buffer_finder,
                                               Rail_notifier,
                                               Rail_buffer_finder,
                                               Rail_notifier);

        const serialization_id_t Rail_uncounted_copy_from_serialization_id =
            NetworkDispatcher::addGetFunctions(Rail_buffer_finder,
                                               Rail_uncounted_notifier,
                                               Rail_buffer_finder,
                                               Rail_uncounted_notifier);

        
        void Rail_notifyEnclosingFinish(deserialization_buffer& buf) {
            x10::lang::FinishState* fs = buf.read<x10::lang::FinishState*>();
            place src = buf.read<place>();
            // Perform the actions of both notifyActivityCreation and
            // notifyActivityTermination in a single non-blocking action.
            // This notifier is often running on an @Immediate worker thread.
            fs->notifyActivityCreatedAndTerminated(Place::_make(src));
        }

        void Rail_serialize_finish_state(place dst, serialization_buffer &buf) {
            // dst is the place where the finish update will occur, i.e. where the notifier runs
            dst = parent(dst);
            x10::lang::FinishState* fs = Runtime::activity()->finishState();
            fs->notifySubActivitySpawn(Place::_make(dst));
            buf.write(fs);
            buf.write(x10aux::here);
        }

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
                buf.write((x10_long)(size_t)(dstAddr));
                if (NULL == notif) {
                    Rail_serialize_finish_state(dst_place, buf);
                    x10aux::send_put(dst_place, Rail_copy_to_serialization_id, buf, srcAddr, numBytes);
                } else {
                    buf.write(notif);
                    x10aux::send_put(dst_place, Rail_uncounted_copy_to_serialization_id, buf, srcAddr, numBytes);
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
                buf.write((x10_long)(size_t)(srcAddr));
                if (NULL == notif) {
                    Rail_serialize_finish_state(x10aux::here, buf);
                    x10aux::send_get(src_place, Rail_copy_from_serialization_id, buf, dstAddr, numBytes);
                } else {
                    buf.write(notif);
                    x10aux::send_get(src_place, Rail_uncounted_copy_from_serialization_id, buf, dstAddr, numBytes);
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
        
        void* Rail_buffer_finder(deserialization_buffer& buf, x10_int ) {
            void *dstAddr = (void*)(size_t)buf.read<x10_long>();
            return dstAddr;
        }

        void Rail_notifier(deserialization_buffer &buf, x10_int) {
            buf.read<x10_long>();  // Read and discard data used by Rail_copy_to_buffer_finder
            Rail_notifyEnclosingFinish(buf);
        }

        void Rail_uncounted_notifier(deserialization_buffer &buf, x10_int) {
            buf.read<x10_long>();  // Read and discard data used by Rail_copy_to_buffer_finder
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
