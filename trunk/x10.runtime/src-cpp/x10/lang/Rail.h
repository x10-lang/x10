#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/Settable.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/ValRail.h>

namespace x10 {

    namespace lang {

        void _initRTTHelper_Rail(x10aux::RuntimeType *location, const x10aux::RuntimeType *element,
                                 const x10aux::RuntimeType *p1, const x10aux::RuntimeType *p2);

        template<class P1, class R> class Fun_0_1;

        template<class T> class Rail : public Ref {
            public:
            RTT_H_DECLS_CLASS;

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
                x10aux::checkRailBounds(index, FMGL(length));
                return _data[index];
            }
      
            T* raw() { return _data; }

            virtual x10aux::ref<ValRail<T> > view (void) {
                return new (x10aux::alloc<ValRail<T> >()) ValRail<T>(FMGL(length),_data);
            }

            virtual x10aux::ref<Iterator<T> > iterator() {
                x10aux::ref<RailIterator<T> > tmp = new (x10aux::alloc<RailIterator<T> >()) RailIterator<T> (this->FMGL(length), this->raw());
                return tmp;
            }   

            static x10aux::ref<Rail<T> > make(x10_int length);
            static x10aux::ref<Rail<T> > make(x10_int length,
                                              x10aux::ref<Fun_0_1<x10_int,T> > init);
            static x10aux::ref<Rail<T> > make(x10aux::ref<ValRail<T> > other);

            static const x10aux::serialization_id_t _copy_to_serialization_id;

            static void *_copy_to_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            virtual void copyTo (x10_int src_off, x10aux::ref<Rail<T> > dst, x10_int dst_off,
                                 x10_int len);

            static const x10aux::serialization_id_t _copy_from_serialization_id;

            static void *_copy_from_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            virtual void copyFrom (x10_int dst_off, x10aux::ref<Rail<T> > src, x10_int src_off,
                                   x10_int len);

            virtual void copyFrom (x10_int dst_off, x10aux::ref<ValRail<T> > src, x10_int src_off,
                                   x10_int len);

            virtual x10aux::ref<String> toString() { return x10aux::railToString<T,Rail<T> >(this); }
        };

        template<class T> x10aux::RuntimeType Rail<T>::rtt;

        template<> class Rail<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
        
        template<class T> void Rail<T>::_initRTT() {
            rtt.canonical = &rtt;
            x10::lang::_initRTTHelper_Rail(&rtt, x10aux::getRTT<T>(),
                                           x10aux::getRTT<Settable<x10_int,T> >(),
                                           x10aux::getRTT<Iterable<T> >());
        }

        template <class T> typename Iterable<T>::template itable<Rail<T> > Rail<T>::_itable_iterable(&Rail<T>::iterator);

        template <class T> typename Settable<x10_int, T>::template itable<Rail<T> > Rail<T>::_itable_settable(&Rail<T>::set);
        
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
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            x10aux::ref<x10::lang::Object> initAsObj = init;
            typename Fun_0_1<x10_int,T>::template itable<x10::lang::Object> *it = x10aux::findITable<Fun_0_1<x10_int,T> >(initAsObj->_getITables());
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (initAsObj.operator->()->*(it->apply))(i);
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10aux::ref<ValRail<T> > other) {
            x10aux::nullCheck(other);
            x10aux::placeCheck(other);
            x10_int length = other->FMGL(length);
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i];
            }
            return rail;
        }

        template <class T> void *Rail<T>::_copy_to_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            x10aux::ref<Rail<T> > this_ = buf.read<x10aux::ref<Rail<T> > >();
            x10_int dst_off = buf.read<x10_int>();
            x10aux::checkRailBounds(dst_off, this_->FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, this_->FMGL(length));
            return &this_->_data[dst_off];
            // catch exception and update finish
        }
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_to_serialization_id =
            x10aux::DeserializationDispatcher
                ::addPutBufferFinder(Rail<T>::_copy_to_buffer_finder);

        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10aux::ref<Rail<T> > dst, x10_int dst_off,
                                                 x10_int len)
        {
            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10_int dst_place = x10aux::location(dst);
            if (dst_place == x10aux::here) {
                if (dst==this) {
                    fprintf(stderr,"TODO: implement rail self-copies ("__FILELINE__")\n");
                    abort();
                }
                // check beginning and end of range
                x10aux::checkRailBounds(dst_off, dst->FMGL(length));
                x10aux::checkRailBounds(dst_off+len-1, dst->FMGL(length));
                for (x10_int i=0 ; i<len ; ++i) {
                    dst->_data[i+dst_off] = this->_data[i+src_off];
                }
                return;
            }
            x10aux::serialization_buffer buf;
            x10aux::addr_map m;
            buf.realloc_func = x10aux::put_realloc;
            //buf.write(finish_state, m);
            buf.write(dst, m);
            buf.write(dst_off, m);
            // get finish state: runtime().finishStates.get(rid)
            // f.notifySubActivitySpawn(PLACES[dst_place]);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        }

        template <class T> void *Rail<T>::_copy_from_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            // FIXME: supplied length param is wrong
            //assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            x10aux::ref<Rail<T> > this_ = buf.read<x10aux::ref<Rail<T> > >();
            x10_int src_off = buf.read<x10_int>();
            x10aux::checkRailBounds(src_off, this_->FMGL(length));
            //x10aux::checkRailBounds(src_off+len-1, this_->FMGL(length));
            return &this_->_data[src_off];
        }
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_from_serialization_id =
            x10aux::DeserializationDispatcher
                ::addGetBufferFinder(Rail<T>::_copy_from_buffer_finder);

        template <class T> void Rail<T>::copyFrom (x10_int dst_off,
                                                   x10aux::ref<Rail<T> > src, x10_int src_off,
                                                   x10_int len)
        {
            // check beginning and end of range
            x10aux::checkRailBounds(dst_off, FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, FMGL(length));
            x10_int src_place = x10aux::location(src);
            if (src_place == x10aux::here) {
                if (src==this) {
                    fprintf(stderr,"TODO: implement rail self-copies ("__FILELINE__")\n");
                    abort();
                }
                // check beginning and end of range
                x10aux::checkRailBounds(src_off, src->FMGL(length));
                x10aux::checkRailBounds(src_off+len-1, src->FMGL(length));
                for (x10_int i=0 ; i<len ; ++i) {
                    this->_data[i+dst_off] = src->_data[i+src_off];
                }
                return;
            }
            x10aux::serialization_buffer buf;
            x10aux::addr_map m;
            buf.realloc_func = x10aux::get_realloc;
            buf.write(src, m);
            buf.write(src_off, m);
            x10aux::send_get(x10aux::location(src), _copy_from_serialization_id,
                             buf, &_data[dst_off], len * sizeof(T));

        }

        template <class T> void Rail<T>::copyFrom (x10_int dst_off,
                                                   x10aux::ref<ValRail<T> > src, x10_int src_off,
                                                   x10_int len)
        {
            // check beginning and end of range
            x10aux::checkRailBounds(dst_off, FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, FMGL(length));
            assert(src!=this); // rail and valrail may not overlap
            // check beginning and end of range
            x10aux::checkRailBounds(src_off, src->FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, src->FMGL(length));
            for (x10_int i=0 ; i<len ; ++i) {
                this->_data[i+dst_off] = src->_data[i+src_off];
            }
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
