#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/Runtime.h>


x10aux::RuntimeType x10::util::IndexedMemoryChunk<void>::rtt;

using namespace x10aux;
using namespace x10::lang;

namespace x10 {
    namespace util {

        const serialization_id_t IMC_copy_to_serialization_id =
            DeserializationDispatcher::addPutFunctions(IMC_buffer_finder,
                                                       IMC_notifier,
                                                       NULL,
                                                       NULL);

        const serialization_id_t IMC_uncounted_copy_to_serialization_id =
            DeserializationDispatcher::addPutFunctions(IMC_buffer_finder,
                                                       IMC_uncounted_notifier,
                                                       NULL,
                                                       NULL);

        const serialization_id_t IMC_copy_from_serialization_id =
            DeserializationDispatcher::addGetFunctions(IMC_buffer_finder,
                                                       IMC_notifier,
                                                       NULL,
                                                       NULL);

        const serialization_id_t IMC_uncounted_copy_from_serialization_id =
            DeserializationDispatcher::addGetFunctions(IMC_buffer_finder,
                                                       IMC_uncounted_notifier,
                                                       NULL,
                                                       NULL);

        
        void IMC_notifyEnclosingFinish(deserialization_buffer& buf) {
            ref<Reference> fs = buf.read<ref<Reference> >();
            ref<Runtime> rt = PlaceLocalHandle_methods<ref<Runtime> >::apply(Runtime::FMGL(runtime));
            // olivier says the incr should be just after the notifySubActivitySpawn
            (fs.operator->()->*(findITable<Runtime__FinishState>(fs->_getITables())->notifyActivityCreation))();
            (fs.operator->()->*(findITable<Runtime__FinishState>(fs->_getITables())->notifyActivityTermination))();
        }

        void IMC_serialize_finish_state(place dst, serialization_buffer &buf) {
            // dst is the place where the finish update will occur, i.e. where the notifier runs
            dst = parent(dst);
            ref<Runtime> rt = PlaceLocalHandle_methods<ref<Runtime> >::apply(Runtime::FMGL(runtime));
            ref<Reference> fs = rt->currentState();
            (fs.operator->()->*(findITable<Runtime__FinishState>(fs->_getITables())->notifySubActivitySpawn))(Place_methods::_make(dst));
            buf.write(fs);
        }

        void IMC_copyToBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place dstPlace, bool overlap, bool uncounted) {
            if (dstPlace->FMGL(id) == x10aux::here) {
                if (overlap) {
                    // potentially overlapping, use memmove
                    memmove(dstAddr, srcAddr, numBytes);
                } else {
                    memcpy(dstAddr, srcAddr, numBytes);
                }                
            } else {
                x10aux::place dst_place = dstPlace->FMGL(id);
                x10aux::serialization_buffer buf;
                buf.write((x10_long)(size_t)(dstAddr));
                if (uncounted) {
                    x10aux::send_put(dst_place, IMC_uncounted_copy_to_serialization_id, buf, srcAddr, numBytes);
                } else {
                    IMC_serialize_finish_state(dst_place, buf);
                    x10aux::send_put(dst_place, IMC_copy_to_serialization_id, buf, srcAddr, numBytes);
                }
            }
        }

        void IMC_copyFromBody(void *srcAddr, void *dstAddr, x10_int numBytes, Place srcPlace, bool overlap, bool uncounted) {
            if (srcPlace->FMGL(id) == x10aux::here) {
                if (overlap) {
                    // potentially overlapping, use memmove
                    memmove(dstAddr, srcAddr, numBytes);
                } else {
                    memcpy(dstAddr, srcAddr, numBytes);
                }
            } else {
                x10aux::place src_place = srcPlace->FMGL(id);
                x10aux::serialization_buffer buf;
                buf.write((x10_long)(size_t)(srcAddr));
                if (uncounted) {
                    x10aux::send_get(src_place, IMC_uncounted_copy_from_serialization_id, buf, dstAddr, numBytes);
                } else {
                    IMC_serialize_finish_state(x10aux::here, buf);
                    x10aux::send_get(src_place, IMC_copy_from_serialization_id, buf, dstAddr, numBytes);
                }
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
            // do nothing.
            // TODO: would maybe be nice if we could just register a NULL notifier callback.  Does X10RT support this?
        }
    }
}



