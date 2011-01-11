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
#include <x10/util/IndexedMemoryChunk.struct_h>

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
namespace x10 { namespace util { template<class T> class IndexedMemoryChunk; } }

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

            // Support length() property method.
            // TODO: If we eliminate auto-generated property getters, then we should delete this method
            GPUSAFE x10_int length() { return FMGL(length); }
            
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

            GPUSAFE x10::util::IndexedMemoryChunk<T> indexedMemoryChunk();

            GPUSAFE T set(T v, x10_int index) { 
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

            virtual x10aux::ref<Iterator<T> > iterator();

            void reset(x10aux::ref<Fun_0_1<x10_int,T> > init);
            void reset(T val);

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            void _serialize_body(x10aux::serialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            template<class S> static x10aux::ref<S> _deserializer(x10aux::deserialization_buffer &buf);

            virtual x10aux::ref<String> toString();
        };

        template<class T> x10aux::RuntimeType Rail<T>::rtt;

        template<> class Rail<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }

            template <class T> static x10aux::ref<Rail<T> > make(x10_int length) { return makeAligned<T>(length, 8); }
            template <class T> static x10aux::ref<Rail<T> > makeAligned(x10_int length, x10_int alignment);

            template <class T> static x10aux::ref<Rail<T> > make(x10_int length, T init) { return makeAligned<T>(length, init, 8); }
            template <class T> static x10aux::ref<Rail<T> > makeAligned(x10_int length, T init, x10_int alignment);
            
            template <class T> static x10aux::ref<Rail<T> > make(x10_int length, x10aux::ref<Fun_0_1<x10_int,T> > init) {
                return makeAligned<T>(length, init, 8);
            }
            template <class T> static x10aux::ref<Rail<T> > makeAligned(x10_int length, x10aux::ref<Fun_0_1<x10_int,T> > init, x10_int alignment);

            template <class T> static x10aux::ref<Rail<T> > make(x10_int length, x10_int offset, x10aux::ref<Rail<T> > other) {
                return makeAligned<T>(length, offset, other, 8);
            }
            template <class T> static x10aux::ref<Rail<T> > makeAligned(x10_int length, x10_int offset, x10aux::ref<Rail<T> > other, x10_int alignment);
            
            template <class T> static x10aux::ref<Rail<T> > makePinned(x10_int length, x10aux::ref<Fun_0_1<x10_int,T> > init);

            template <class T> static x10aux::ref<Rail<T> > makeCUDA(x10::lang::Place p, x10_int length);
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
#include <x10aux/itables.h>
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION

namespace x10 {

    namespace lang {

        template<class T> void Rail<T>::_initRTT() {
            if (rtt.initStageOne(x10aux::getRTT<Rail<void> >())) return;
            x10::lang::_initRTTHelper_Rail(&rtt, x10aux::getRTT<T>(),
                                           x10aux::getRTT<Settable<x10_int,T> >(),
                                           x10aux::getRTT<Iterable<T> >());
        }

        template <class T> typename Iterable<T>::template itable<Rail<T> > Rail<T>::_itable_iterable(&Rail<T>::equals,
                                                                                                     &Rail<T>::hashCode,
                                                                                                     &Rail<T>::iterator,
                                                                                                     &Rail<T>::toString,
                                                                                                     &Rail<T>::typeName);

        template <class T> typename Settable<x10_int, T>::template itable<Rail<T> > Rail<T>::_itable_settable(&Rail<T>::equals,
                                                                                                              &Rail<T>::hashCode,
                                                                                                              &Rail<T>::set,
                                                                                                              &Rail<T>::toString,
                                                                                                              &Rail<T>::typeName);
        
        template <class T> x10aux::itable_entry x10::lang::Rail<T>::_itables[3] = {
            x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<T> >, &x10::lang::Rail<T>::_itable_iterable),
            x10aux::itable_entry(&x10aux::getRTT<x10::lang::Settable<x10_int, T> >, &x10::lang::Rail<T>::_itable_iterable),
            x10aux::itable_entry(NULL,  (void*)x10aux::getRTT<Rail<T> >())
        };

        template <class T> x10::util::IndexedMemoryChunk<T> Rail<T>::indexedMemoryChunk() {
            return x10::util::IndexedMemoryChunk<T>((T*)_data, (x10_int)FMGL(length));
        }

        template <class T> x10aux::ref<Rail<T> > Rail<void>::makeAligned(x10_int length, x10_int alignment) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_aligned_rail<T,Rail<T> >(length, alignment);
            // Memset both for efficiency and to allow T to be a struct.
            memset(rail->raw(), 0, length * sizeof(T));
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<void>::makeAligned(x10_int length, T init, x10_int alignment) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_aligned_rail<T,Rail<T> >(length, alignment);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = init;
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<void>::makeAligned(x10_int length,
                                                                         x10aux::ref<Fun_0_1<x10_int,T> > init,
                                                                         x10_int alignment ) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_aligned_rail<T,Rail<T> >(length, alignment);
            x10aux::ref<x10::lang::Reference> initAsRef = init;
            typename Fun_0_1<x10_int,T>::template itable<x10::lang::Reference> *it = x10aux::findITable<Fun_0_1<x10_int,T> >(initAsRef->_getITables());
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (initAsRef.operator->()->*(it->apply))(i);
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<void>::makeAligned(x10_int length, x10_int offset,
                                                                         x10aux::ref<Rail<T> > other,
                                                                         x10_int alignment) {
            x10aux::nullCheck(other);
            x10aux::ref<Rail<T> > rail = x10aux::alloc_aligned_rail<T,Rail<T> >(length, alignment);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i+offset];
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


        template <class T> x10aux::ref<Rail<T> > Rail<void>::makeCUDA(x10::lang::Place p,
                                                                      x10_int length) {

            // create a local proxy with the right size, but rather than
            // pointing to a remote object of type rail, it just points to the
            // raw storage

            // one peculiarity of this design is that the GPU never knows how large its
            // rails are, but the host actually does!

//            x10aux::ref<Rail<T> > proxy = x10aux::alloc_remote_rail<T,Rail<T> >(length);
//            proxy->location = p.FMGL(id);
//            x10aux::set_remote_ref(proxy.operator->(),
//                                   x10aux::remote_alloc(p.FMGL(id), ((size_t)length)*sizeof(T)) );
//            return proxy;
            assert(false); // code abouve BROKEN by 2.1 object model changes
            return NULL;
        }


        template<class T> const x10aux::serialization_id_t Rail<T>::_serialization_id =
            x10aux::DeserializationDispatcher
                ::addDeserializer(Rail<T>::template _deserializer<Reference>, x10aux::CLOSURE_KIND_NOT_ASYNC);

        template <class T> void Rail<T>::_serialize_body(x10aux::serialization_buffer &buf) {
            x10_int length = this->FMGL(length);
            buf.write(length);
            this->Object::_serialize_body(buf); // intentional change of order
            T* raw = this->raw();
            for (x10_int i=0 ; i<length ; ++i) {
                buf.write(raw[i]); // avoid bounds check
            }
        }

        template <class T> void Rail<T>::_deserialize_body(x10aux::deserialization_buffer &buf) {
            // length read out earlier, in _deserializer()
            this->Object::_deserialize_body(buf);
            x10_int length = this->FMGL(length);
            T* raw = this->raw();
            for (x10_int i=0 ; i<length ; ++i) {
                raw[i] = buf.read<T>(); // avoid bounds check
            }
        }

        template <class T> template<class S> x10aux::ref<S> Rail<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10_int length = buf.read<x10_int>();
            x10aux::ref<Rail<T> > this_ = x10aux::alloc_rail<T,Rail<T> >(length);
            buf.record_reference(this_); 
            this_->_deserialize_body(buf);
            return this_;
        }
    }
}

#endif
#endif
// vim:tabstop=4:shiftwidth=4:expandtab

