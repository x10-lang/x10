#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>
#include <x10aux/itables.h>

#include <x10/lang/Object.h>

#include <x10/lang/Place.struct_h>
#include <x10/util/Pair.struct_h>

#define X10_LANG_ITERABLE_H_NODEPS
#include <x10/lang/Iterable.h>
#undef X10_LANG_ITERABLE_H_NODEPS
#define X10_LANG_SETTABLE_H_NODEPS
#include <x10/lang/Settable.h>
#undef X10_LANG_SETTABLE_H_NODEPS


#include <stdio.h>

namespace x10 { namespace lang { class VoidFun_0_0; } }
namespace x10 { namespace lang { template<class R> class Fun_0_0; } }
namespace x10 { namespace lang { template<class P1, class R> class Fun_0_1; } }
namespace x10 { namespace lang { template<class T> class ValRail; } }

namespace x10 {

    namespace lang {

        void _initRTTHelper_Rail(x10aux::RuntimeType *location, const x10aux::RuntimeType *element,
                                 const x10aux::RuntimeType *p1, const x10aux::RuntimeType *p2);


        template<class T> class Rail : public Object {
            public:
            RTT_H_DECLS_CLASS;

            typedef x10aux::ref<Rail<T> > R;

            static typename Iterable<T>::template itable<Rail<T> > _itable_iterable;
            static typename Settable<x10_int, T>::template itable<Rail<T> > _itable_settable;
            static x10aux::itable_entry _itables[3];
            virtual x10aux::itable_entry* _getITables() { return _itables; }

            private:

            Rail(const Rail<T>& arr); // disabled

            public:
            // 32 bit array indexes
            const x10_int FMGL(length);

            // The Rail's data.
            // As a locality optimization, we are going to allocate all of the storage for the
            // Rail object and its data array contiguously (ie, in a single allocate call),
            // but to avoid making assumptions about the C++ object model, we will always
            // access it via this pointer instead of using the data[1] "struct hack."
            // This may cost us an extra load instruction (but no extra cache misses).
            // By declaring the pointer const, we should enable the C++ compiler to be reasonably
            // effective at hoisting this extra load out of loop nests.
            T* const _data;
            
            Rail(x10_int length_, T* storage) : FMGL(length)(length_),  _data(storage) { }

            GPUSAFE virtual T set(T v, x10_int index) { 
                return (*this)[index] = v; 
            } 

            GPUSAFE T apply(x10_int index) {
                return operator[](index);
            }   

            GPUSAFE T& operator[](x10_int index) {
                #ifndef NO_BOUNDS_CHECKS
                x10aux::checkRailBounds(index, FMGL(length));
                #endif
                return _data[index];
            }
      
            T* raw() { return _data; }

            #if 0
            virtual x10aux::ref<ValRail<T> > view (void) {
                ValRail<T>* rail = new (x10aux::alloc<ValRail<T> >()) ValRail<T>(FMGL(length),_data);
                rail->x10::lang::Object::_constructor();
                return rail;
            }
            #endif

            virtual x10aux::ref<Iterator<T> > iterator();

            static R make(x10_int length);
            static R make(x10_int length, x10aux::ref<Fun_0_1<x10_int,T> > init);
            static R make(x10aux::ref<ValRail<T> > other);
            static R make(x10_int length, x10_int offset, x10aux::ref<ValRail<T> > other);
            static R make(x10_int length, x10_int offset, x10aux::ref<Rail<T> > other);

            void reset(x10aux::ref<Fun_0_1<x10_int,T> > init);
            void reset(T val);

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            static void _serialize(R this_,
                                   x10aux::serialization_buffer &buf);

            void _serialize_body(x10aux::serialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            template<class S> static x10aux::ref<S> _deserializer(x10aux::deserialization_buffer &buf);

            template<class S> static x10aux::ref<S> _deserialize(x10aux::deserialization_buffer &buf);

            static R makeCUDA(x10::lang::Place p, x10_int length);

            static const x10aux::serialization_id_t _copy_to_serialization_id;

            static void *_copy_to_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_to_notifier(x10aux::deserialization_buffer&, x10_int);

            static void *_copy_to_cuda_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_to_cuda_notifier(x10aux::deserialization_buffer&, x10_int);

            virtual void copyTo (x10_int src_off, R dst, x10_int dst_off,
                                 x10_int len);

            virtual void copyTo (x10_int src_off,
                                 x10::lang::Place dst_place,
                                 x10aux::ref<Fun_0_0<x10::util::Pair<R, x10_int> > > dst_finder,
                                 x10_int len);

            virtual void copyTo (x10_int src_off,
                                 x10::lang::Place dst_place,
                                 x10aux::ref<Fun_0_0<x10::util::Pair<R, x10_int> > > dst_finder,
                                 x10_int len,
                                 x10aux::ref<VoidFun_0_0> notifier);

            static const x10aux::serialization_id_t _copy_from_serialization_id;

            static void *_copy_from_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_from_notifier(x10aux::deserialization_buffer&, x10_int);

            static void *_copy_from_cuda_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_from_cuda_notifier(x10aux::deserialization_buffer&, x10_int);

            virtual void copyFrom (x10_int dst_off, R src, x10_int src_off,
                                   x10_int len);

            virtual void copyFrom (x10_int dst_off, x10::lang::Place src_place,
                                   x10aux::ref<Fun_0_0<x10::util::Pair<R, x10_int> > > src_finder,
                                   x10_int len);

            virtual x10aux::ref<String> toString();
        };

        void Rail_notifyEnclosingFinish(x10aux::deserialization_buffer& buf);

        void Rail_serialize_finish_state(x10aux::place dst_place,
                                         x10aux::serialization_buffer& buf);

        void Rail_serializeAndSendPut(x10::lang::Place dst_place_, x10aux::ref<x10::lang::Reference> df,
                                      x10_ubyte code, x10aux::serialization_id_t _id,
                                      void* data, size_t size);

        void Rail_serializeAndSendGet(x10::lang::Place dst_place_, x10aux::ref<x10::lang::Reference> df,
                                      x10_ubyte code, x10aux::serialization_id_t _id,
                                      void* data, size_t size);

        template<class T> x10aux::RuntimeType Rail<T>::rtt;

        template<> class Rail<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif

#ifndef __X10_LANG_RAIL_H_NODEPS
#define __X10_LANG_RAIL_H_NODEPS
// #include <x10/lang/System.h> // causes cycle
#include <x10/lang/Iterable.h>
#include <x10/lang/Settable.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/Fun_0_0.h>
#include <x10/lang/Rail__RailIterator.h>
#include <x10/lang/ValRail.h>
#include <x10aux/itables.h>
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION

namespace x10 {

    namespace lang {

        template<class T> void Rail<T>::_initRTT() {
            x10::lang::_initRTTHelper_Rail(&rtt, x10aux::getRTT<T>(),
                                           x10aux::getRTT<Settable<x10_int,T> >(),
                                           x10aux::getRTT<Iterable<T> >());
        }

        template <class T> typename Iterable<T>::template itable<Rail<T> > Rail<T>::_itable_iterable(&Rail<T>::at,
                                                                                                     &Rail<T>::at,
                                                                                                     &Rail<T>::equals,
                                                                                                     &Rail<T>::hashCode,
                                                                                                     &Rail<T>::home,
                                                                                                     &Rail<T>::iterator,
                                                                                                     &Rail<T>::toString,
                                                                                                     &Rail<T>::typeName);

        template <class T> typename Settable<x10_int, T>::template itable<Rail<T> > Rail<T>::_itable_settable(&Rail<T>::at,
                                                                                                              &Rail<T>::at,
                                                                                                              &Rail<T>::equals,
                                                                                                              &Rail<T>::hashCode,
                                                                                                              &Rail<T>::home,
                                                                                                              &Rail<T>::set,
                                                                                                              &Rail<T>::toString,
                                                                                                              &Rail<T>::typeName);
        
        template <class T> x10aux::itable_entry x10::lang::Rail<T>::_itables[3] = {
            x10aux::itable_entry(&x10::lang::Iterable<T>::rtt, &x10::lang::Rail<T>::_itable_iterable),
            x10aux::itable_entry(&x10::lang::Settable<x10_int, T>::rtt, &x10::lang::Rail<T>::_itable_iterable),
            x10aux::itable_entry(NULL,  (void*)x10aux::getRTT<Rail<T> >())
        };

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            // Memset both for efficiency and to allow T to be a struct.
            memset(rail->raw(), 0, length * sizeof(T));
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length,
                                                               x10aux::ref<Fun_0_1<x10_int,T> > init ) {
            R rail = x10aux::alloc_rail<T,Rail<T> >(length);
            x10aux::ref<x10::lang::Reference> initAsRef = init;
            typename Fun_0_1<x10_int,T>::template itable<x10::lang::Reference> *it = x10aux::findITable<Fun_0_1<x10_int,T> >(initAsRef->_getITables());
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (initAsRef.operator->()->*(it->apply))(i);
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length, x10_int offset,
                                                               x10aux::ref<ValRail<T> > other) {
            x10aux::nullCheck(other);
            R rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i+offset];
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length, x10_int offset,
                                                               x10aux::ref<Rail<T> > other) {
            x10aux::nullCheck(other);
            R rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i+offset];
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10aux::ref<ValRail<T> > other) {
            x10aux::nullCheck(other);
            x10_int length = other->FMGL(length);
            R rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i];
            }
            return rail;
        }


        template <class T> x10aux::ref<x10::lang::String> Rail<T>::toString() {
            return x10aux::railToString<T>(FMGL(length), raw());
        }
        

        template <class T> x10aux::ref<Iterator<T> > Rail<T>::iterator() {
            return Rail__RailIterator<T>::_make(this);
        }


        template <class T> void Rail<T>::reset(T val) {
            for (x10_int i=0 ; i<FMGL(length) ; ++i) {
                (*this)[i] = val;
            }
        }


        template <class T> void Rail<T>::reset(x10aux::ref<Fun_0_1<x10_int,T> > init) {
            x10aux::ref<x10::lang::Reference> initAsRef = init;
            typename Fun_0_1<x10_int,T>::template itable<x10::lang::Reference> *it = x10aux::findITable<Fun_0_1<x10_int,T> >(initAsRef->_getITables());
            for (x10_int i=0 ; i<FMGL(length) ; ++i) {
                (*this)[i] = (initAsRef.operator->()->*(it->apply))(i);
            }
        }


        template <class T> x10aux::ref<Rail<T> > Rail<T>::makeCUDA(x10::lang::Place p,
                                                                   x10_int length) {

            // create a local proxy with the right size, but rather than
            // pointing to a remote object of type rail, it just points to the
            // raw storage

            // one peculiarity of this design is that the GPU never knows how large its
            // rails are, but the host actually does!

            R proxy = x10aux::alloc_rail_remote<T,Rail<T> >(length);
            proxy->location = p.FMGL(id);
            x10aux::set_remote_ref(proxy.operator->(),
                                   x10aux::remote_alloc(p.FMGL(id), ((size_t)length)*sizeof(T)) );
            return proxy;
        }


        // {{{ *** COPY TO ***


        // bf {{{
        template <class T> void *Rail<T>::_copy_to_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            
            typedef x10::util::Pair<R,x10_int> P;
            x10_ubyte code = buf.read<x10_ubyte>();
            R this_;
            x10_int dst_off;
            _X_("Finding a rail for copyTo ("<<(int)code<<")");
            switch (code) {
                case 0: { // get rail+offset explicitly
                    this_ = buf.read<R>();
                    dst_off = buf.read<x10_int>();
                    break;
                }
                case 1: case 2: { // get closure with which to find rail+offset
                    x10aux::ref<Reference> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    P pair = (bf.operator->()->*(x10aux::findITable<Fun_0_0<P> >(bf->_getITables())->apply))();
                    this_ = pair.FMGL(first);
                    dst_off = pair.FMGL(second);
                    x10aux::dealloc(bf.operator->());
                    break;
                }
                default:
                abort();
            }

            // FIXME: should catch exception here, add to finish state, return NULL
            x10aux::checkRailBounds(dst_off, this_->FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, this_->FMGL(length));

            return &this_->_data[dst_off];
        } // }}}
                                                                                           
        // nf {{{
        template <class T> void Rail<T>::_copy_to_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            x10_ubyte code = buf.read<x10_ubyte>();
            _X_("Completing a rail copyTo ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    buf.read<R>();
                    buf.read<x10_int>();
                    Rail_notifyEnclosingFinish(buf);
                } break;
                case 1: {
                    x10aux::ref<Reference> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    x10aux::dealloc(bf.operator->());
                    Rail_notifyEnclosingFinish(buf);
                    break;
                }
                // case 255 on death row
                case 2: {
                    x10aux::ref<Reference> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    x10aux::dealloc(bf.operator->());
                    x10aux::ref<Reference> vf = buf.read<x10aux::ref<VoidFun_0_0> >();
                    (vf.operator->()->*(x10aux::findITable<VoidFun_0_0>(vf->_getITables())->apply))();
                    x10aux::dealloc(vf.operator->());
                    break;
                }
                default: abort();
            }
        } // }}}
                                                                                           
        // cuda bf {{{
        template <class T> void *Rail<T>::_copy_to_cuda_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);

            x10_ubyte code = buf.read<x10_ubyte>();
            x10_int dst_off;
            x10_ulong addr;
            _X_("Finding a rail for cuda copyTo ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    Object::_reference_state rr = Object::_deserialize_reference_state(buf);
                    // doesn't work because we don't know what gpu we're going to
                    // assert(rr.loc == here);
                    addr = rr.ref;
                    buf.read<x10_int>();
                    dst_off = buf.read<x10_int>();
                    break;
                }
                default:
                abort();
            }

            return (void*)(size_t)(addr + sizeof(T)*dst_off);
        } // }}}
                                                                                           
        // cuda nf {{{
        template <class T> void Rail<T>::_copy_to_cuda_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            x10_ubyte code = buf.read<x10_ubyte>();
            _X_("Completing a rail cuda copyTo ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    Object::_reference_state rr = Object::_deserialize_reference_state(buf);
                    buf.read<x10_int>();
                    buf.read<x10_int>();
                    Rail_notifyEnclosingFinish(buf);
                    break;
                }
                default: abort();
            }
        } // }}}
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_to_serialization_id =
            x10aux::DeserializationDispatcher
                ::addPutFunctions(Rail<T>::_copy_to_buffer_finder,
                                  Rail<T>::_copy_to_notifier,
                                  Rail<T>::_copy_to_cuda_buffer_finder,
                                  Rail<T>::_copy_to_cuda_notifier);

        // RAIL FINISH (0) {{{
        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10aux::ref<Rail<T> > dst, x10_int dst_off,
                                                 x10_int len)
        {
            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::place dst_place = x10aux::location(dst);
            assert(dst_place != x10aux::here); // handle in X10 code wrapper
            x10aux::serialization_buffer buf;
            buf.realloc_func = x10aux::put_realloc;
            x10_ubyte code = 0;
            buf.write(code);
            buf.write(dst);
            buf.write(dst_off);
            Rail_serialize_finish_state(dst_place, buf);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        } // }}}

        // CLOSURE FINISH (1) {{{
        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10::lang::Place dst_place_,
                                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >,
                                                                                     x10_int> > > dst_finder,
                                                 x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            R this_ = this;
            x10aux::place dst_place = dst_place_.FMGL(id);

            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::ref<Reference> df = dst_finder;
            assert(dst_place != x10aux::here); // handle in X10 code wrapper
            Rail_serializeAndSendPut(dst_place_, df, 1, _copy_to_serialization_id,
                                     &_data[src_off], len * sizeof(T));
        } // }}}

        // CLOSURE NOTIFIER (2) (this one designed for LU) {{{
        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10::lang::Place dst_place_,
                                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >, x10_int> > > dst_finder,
                                                 x10_int len,
                                                 x10aux::ref<VoidFun_0_0> notifier)
        {
            typedef x10::util::Pair<R,x10_int> P;
            R this_ = this;
            x10aux::place dst_place = dst_place_.FMGL(id);

            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::ref<Reference> df = dst_finder;
            x10aux::ref<Reference> n = notifier;
            assert(dst_place != x10aux::here); // handle in X10 code wrapper
            x10aux::serialization_buffer buf;
            buf.realloc_func = x10aux::put_realloc;
            x10_ubyte code = 2;
            buf.write(code);
            buf.write(df);
            buf.write(n);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        } // }}}

        // }}}



        // {{{ *** COPY FROM ***

        // bf {{{
        template <class T> void *Rail<T>::_copy_from_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            x10_ubyte code = buf.read<x10_ubyte>();
            R this_;
            x10_int src_off;
            _X_("Finding a rail for copyFrom ("<<(int)code<<")");
            switch (code) {
                case 0: { // get rail+offset explicitly
                    this_ = buf.read<R>();
                    src_off = buf.read<x10_int>();
                    break;
                }
                case 1: { // get rail+offset from closure
                    x10aux::ref<Reference> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    P pair = (bf.operator->()->*(x10aux::findITable<Fun_0_0<P> >(bf->_getITables())->apply))();
                    this_ = pair.FMGL(first);
                    src_off = pair.FMGL(second);
                    x10aux::dealloc(bf.operator->());
                    break;
                }
                default:
                abort();
            }

            // FIXME: should catch exception here, add to finish state, return NULL
            x10aux::checkRailBounds(src_off, this_->FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, this_->FMGL(length));

            return &this_->_data[src_off];
        } // }}}
                                                                                           
        // nf {{{
        template <class T> void Rail<T>::_copy_from_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            x10_ubyte code = buf.read<x10_ubyte>();
            _X_("Completing a rail copyFrom ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    buf.read<R>();
                    buf.read<x10_int>();
                    Rail_notifyEnclosingFinish(buf);
                } break;
                case 1: {
                    x10aux::ref<Reference> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    x10aux::dealloc(bf.operator->());
                    Rail_notifyEnclosingFinish(buf);
                    break;
                }
                default: abort();
            }
        } // }}}
                                                                                           
        // cuda bf {{{
        template <class T> void *Rail<T>::_copy_from_cuda_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            x10_ubyte code = buf.read<x10_ubyte>();
            x10_ulong addr;
            x10_int src_off;
            _X_("Finding a rail for copyFrom ("<<(int)code<<")");
            switch (code) {
                case 0: { // get rail+offset explicitly
                    Object::_reference_state rr = Object::_deserialize_reference_state(buf);
                    // doesn't work because we don't know what gpu we're going to
                    // assert(rr.loc == here);
                    addr = rr.ref;
                    buf.read<x10_int>();
                    src_off = buf.read<x10_int>();
                } break;
                default:
                abort();
            }

            return (void*)(size_t)(addr + sizeof(T)*src_off);
        } // }}}
                                                                                           
        // cuda nf {{{
        template <class T> void Rail<T>::_copy_from_cuda_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            x10_ubyte code = buf.read<x10_ubyte>();
            _X_("Completing a rail copyFrom ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    buf.read<R>();
                    buf.read<x10_int>();
                    Rail_notifyEnclosingFinish(buf);
                } break;
                default: abort();
            }
        } // }}}
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_from_serialization_id =
            x10aux::DeserializationDispatcher
                ::addGetFunctions(Rail<T>::_copy_from_buffer_finder,
                                  Rail<T>::_copy_from_notifier,
                                  Rail<T>::_copy_from_cuda_buffer_finder,
                                  Rail<T>::_copy_from_cuda_notifier);

        // RAIL FINISH (0) {{{
        template <class T> void Rail<T>::copyFrom (x10_int dst_off,
                                                   x10aux::ref<Rail<T> > src, x10_int src_off,
                                                   x10_int len)
        {
            // check beginning and end of range
            x10aux::checkRailBounds(dst_off, FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, FMGL(length));
            x10aux::place src_place = x10aux::location(src);
            assert(src_place != x10aux::here);
            x10aux::serialization_buffer buf;
            buf.realloc_func = x10aux::get_realloc;
            x10_ubyte code = 0;
            buf.write(code);
            buf.write(src);
            buf.write(src_off);
            Rail_serialize_finish_state(x10aux::here, buf);
            x10aux::send_get(src_place, _copy_from_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));
        } // }}}

        // CLOSURE FINISH (1) {{{
        template <class T>
        void Rail<T>::copyFrom (x10_int dst_off, x10::lang::Place src_place_,
                                x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >,
                                                                    x10_int> > > dst_finder,
                                x10_int len)
        {
            typedef x10::util::Pair<R,x10_int> P;
            R this_ = this;
            x10aux::place src_place = src_place_.FMGL(id);
            assert(src_place != x10aux::here);
            // check beginning and end of range
            x10aux::checkRailBounds(dst_off, FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, FMGL(length));
            x10aux::ref<Reference> df = dst_finder;
            assert(src_place!=x10aux::here);
            Rail_serializeAndSendGet(src_place_, df, 1, _copy_from_serialization_id,
                                     &_data[dst_off], len * sizeof(T));
        } // }}}

        // }}}



        template<class T> const x10aux::serialization_id_t Rail<T>::_serialization_id =
            x10aux::DeserializationDispatcher
                ::addDeserializer(Rail<T>::template _deserializer<Object>);

        // Specialized serialization
        template <class T> void Rail<T>::_serialize(x10aux::ref<Rail<T> > this_,
                                                    x10aux::serialization_buffer &buf) {
            Object::_serialize_reference(this_, buf);
            if (this_ != x10aux::null) {
                this_->_serialize_body(buf);
            }
        }

        template <class T> void Rail<T>::_serialize_body(x10aux::serialization_buffer &buf) {
            x10_int length = this->FMGL(length);
            buf.write(length);
            this->Object::_serialize_body(buf); // intentional change of order
        }

        template <class T> void Rail<T>::_deserialize_body(x10aux::deserialization_buffer &buf) {
            // length read out earlier, in _deserializer()
            this->Object::_deserialize_body(buf);
        }

        template <class T> template<class S> x10aux::ref<S> Rail<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10_int length = buf.read<x10_int>();
            // Don't allocate any storage for the data - it's a remote rail
            R this_ = x10aux::alloc_rail_remote<T,Rail<T> >(0);
            buf.record_reference(this_); // TODO: avoid; no global refs; final class
            // But the above set the length to 0, so set it correctly
            const_cast<x10_int&>(this_->FMGL(length)) = length;
            this_->_deserialize_body(buf);
            return this_;
        }

        // Specialized deserialization
        template <class T> template<class S> x10aux::ref<S> Rail<T>::_deserialize(x10aux::deserialization_buffer &buf) {
            Object::_reference_state rr = Object::_deserialize_reference_state(buf);
            R this_;
            if (rr.ref != 0) {
                this_ = Rail<T>::template _deserializer<Rail<T> >(buf);
            }
            return Object::_finalize_reference<T>(this_, rr);
        }
    }
}

#endif
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
