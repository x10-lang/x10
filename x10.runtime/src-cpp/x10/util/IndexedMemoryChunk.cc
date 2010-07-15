#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/Runtime.h>


x10aux::RuntimeType x10::util::IndexedMemoryChunk<void>::rtt;

using namespace x10aux;

namespace x10 {
    namespace util {

        const serialization_id_t IMC_copy_to_serialization_id =
            DeserializationDispatcher::addPutFunctions(IMC_copy_to_buffer_finder,
                                                       IMC_copy_to_notifier,
                                                       NULL,
                                                       NULL);

        void* IMC_copy_to_buffer_finder(deserialization_buffer& buf, x10_int ) {
            void *dstAddr = (void*)(size_t)buf.read<x10_long>();
            return dstAddr;
        }

        void IMC_copy_to_notifier(deserialization_buffer &buf, x10_int) {
            buf.read<x10_long>();  // Read and discard data used by IMC_copy_to_buffer_finder
            x10::lang::Rail_notifyEnclosingFinish(buf);
        }

        void IMC_notifyEnclosingFinish(deserialization_buffer& buf) {
            ref<x10::lang::Reference> fs = buf.read<ref<x10::lang::Reference> >();
            ref<x10::lang::Runtime> rt = x10::lang::PlaceLocalHandle_methods<ref<x10::lang::Runtime> >::apply(x10::lang::Runtime::FMGL(runtime));
            // olivier says the incr should be just after the notifySubActivitySpawn
            (fs.operator->()->*(findITable<x10::lang::Runtime__FinishState>(fs->_getITables())->notifyActivityCreation))();
            (fs.operator->()->*(findITable<x10::lang::Runtime__FinishState>(fs->_getITables())->notifyActivityTermination))();
        }

        void IMC_serialize_finish_state(place dst, serialization_buffer &buf) {
            // dst is the place where the finish update will occur, i.e. where the notifier runs
            dst = parent(dst);
            ref<x10::lang::Runtime> rt = x10::lang::PlaceLocalHandle_methods<ref<x10::lang::Runtime> >::apply(x10::lang::Runtime::FMGL(runtime));
            ref<x10::lang::Reference> fs = rt->currentState();
            (fs.operator->()->*(findITable<x10::lang::Runtime__FinishState>(fs->_getITables())->notifySubActivitySpawn))(x10::lang::Place_methods::_make(dst));
            buf.write(fs);
        }
    }
}



