#include <x10aux/throw.h>

#include <x10/util/IndexedMemoryChunk.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/String.h>


x10aux::RuntimeType x10::util::IndexedMemoryChunk<void>::rtt;

using namespace x10aux;
using namespace x10::lang;

namespace x10 {
    namespace util {

        const serialization_id_t IMC_copy_to_serialization_id =
            DeserializationDispatcher::addPutFunctions(IMC_buffer_finder,
                                                       IMC_notifier,
                                                       IMC_buffer_finder,
                                                       IMC_notifier);

        const serialization_id_t IMC_uncounted_copy_to_serialization_id =
            DeserializationDispatcher::addPutFunctions(IMC_buffer_finder,
                                                       IMC_uncounted_notifier,
                                                       IMC_buffer_finder,
                                                       IMC_uncounted_notifier);

        const serialization_id_t IMC_copy_from_serialization_id =
            DeserializationDispatcher::addGetFunctions(IMC_buffer_finder,
                                                       IMC_notifier,
                                                       IMC_buffer_finder,
                                                       IMC_notifier);

        const serialization_id_t IMC_uncounted_copy_from_serialization_id =
            DeserializationDispatcher::addGetFunctions(IMC_buffer_finder,
                                                       IMC_uncounted_notifier,
                                                       IMC_buffer_finder,
                                                       IMC_uncounted_notifier);

        
        void IMC_notifyEnclosingFinish(deserialization_buffer& buf) {
            x10::lang::FinishState* fs = buf.read<x10::lang::FinishState*>();
            // olivier says the incr should be just after the notifySubActivitySpawn
            fs->notifyActivityCreation();
            fs->notifyActivityTermination();
        }

        void IMC_serialize_finish_state(place dst, serialization_buffer &buf) {
            // dst is the place where the finish update will occur, i.e. where the notifier runs
            dst = parent(dst);
            x10::lang::FinishState* fs = Runtime::activity()->finishState();
            fs->notifySubActivitySpawn(Place::_make(dst));
            buf.write(fs);
        }

        void IMC_copyToBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place dstPlace, bool overlap, VoidFun_0_0* notif) {
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
                    IMC_serialize_finish_state(dst_place, buf);
                    x10aux::send_put(dst_place, IMC_copy_to_serialization_id, buf, srcAddr, numBytes);
                } else {
                    buf.write(notif);
                    x10aux::send_put(dst_place, IMC_uncounted_copy_to_serialization_id, buf, srcAddr, numBytes);
                }
            }
        }

        void IMC_copyFromBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place srcPlace, bool overlap, VoidFun_0_0* notif) {
            if (srcPlace->FMGL(id) == x10aux::here) {
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
                x10aux::place src_place = srcPlace->FMGL(id);
                x10aux::serialization_buffer buf;
                buf.write((x10_long)(size_t)(srcAddr));
                if (NULL == notif) {
                    IMC_serialize_finish_state(x10aux::here, buf);
                    x10aux::send_get(src_place, IMC_copy_from_serialization_id, buf, dstAddr, numBytes);
                } else {
                    buf.write(notif);
                    x10aux::send_get(src_place, IMC_uncounted_copy_from_serialization_id, buf, dstAddr, numBytes);
                }
            }
        }

        void IMC_copyBody(void *srcAddr, void *dstAddr, x10_int numBytes, bool overlap) {
            if (overlap) {
                // potentially overlapping, use memmove
                memmove(dstAddr, srcAddr, numBytes);
            } else {
                memcpy(dstAddr, srcAddr, numBytes);
            }
        }
        
        void* IMC_buffer_finder(deserialization_buffer& buf, x10_int ) {
            void *dstAddr = (void*)(size_t)buf.read<x10_long>();
            return dstAddr;
        }

        void IMC_notifier(deserialization_buffer &buf, x10_int) {
            buf.read<x10_long>();  // Read and discard data used by IMC_copy_to_buffer_finder
            IMC_notifyEnclosingFinish(buf);
        }

        void IMC_uncounted_notifier(deserialization_buffer &buf, x10_int) {
            buf.read<x10_long>();  // Read and discard data used by IMC_copy_to_buffer_finder
            VoidFun_0_0* notif = buf.read<VoidFun_0_0*>();
            VoidFun_0_0::__apply(notif);
        }

        void checkCongruentArgs (x10_boolean zeroed, x10_boolean containsPtrs) {
            if (!zeroed) 
                throwException(x10::lang::IllegalArgumentException::_make(String::Lit("Congruent memory must be zeroed")));

            if (containsPtrs) 
                throwException(x10::lang::IllegalArgumentException::_make(
                                   String::Lit("Congruent memory is not garbage collected thus cannot contain pointers")));
        }


        void throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) {
            #ifndef NO_EXCEPTIONS
            char *msg = alloc_printf("Index %ld out of range (length is %ld)", (long)index, ((long)length));
            throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
            #endif
        }
    }
}



