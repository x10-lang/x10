#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>
#include <x10aux/itables.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/Settable.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/ValRail.h>
#include <x10/lang/Place.struct_h>
#include <x10/util/Pair.struct_h>

#include <stdio.h>

namespace x10 { namespace lang { class VoidFun_0_0; } }
namespace x10 { namespace lang { template<class R> class Fun_0_0; } }


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

            #if 0
            virtual x10aux::ref<ValRail<T> > view (void) {
                ValRail<T>* rail = new (x10aux::alloc<ValRail<T> >()) ValRail<T>(FMGL(length),_data);
                rail->x10::lang::Ref::_constructor();
                return rail;
            }
            #endif

            virtual x10aux::ref<Iterator<T> > iterator() {
                x10aux::ref<RailIterator<T> > tmp = new (x10aux::alloc<RailIterator<T> >()) RailIterator<T> (this->FMGL(length), this->raw());
                return tmp;
            }   

            static x10aux::ref<Rail<T> > make(x10_int length);
            static x10aux::ref<Rail<T> > make(x10_int length,
                                              x10aux::ref<Fun_0_1<x10_int,T> > init);
            static x10aux::ref<Rail<T> > make(x10aux::ref<ValRail<T> > other);

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            static void _serialize(x10aux::ref<Rail<T> > this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m);

            void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            template<class S> static x10aux::ref<S> _deserializer(x10aux::deserialization_buffer &buf);

            template<class S> static x10aux::ref<S> _deserialize(x10aux::deserialization_buffer &buf);

            static const x10aux::serialization_id_t _copy_to_serialization_id;

            static void *_copy_to_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_to_notifier(x10aux::deserialization_buffer&, x10_int);

            virtual void copyTo (x10_int src_off, x10aux::ref<Rail<T> > dst, x10_int dst_off,
                                 x10_int len);

            virtual void copyTo (x10_int src_off,
                                 x10::lang::Place dst_place,
                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >,
                                                                     x10_int> > > dst_finder,
                                 x10_int len);

            // on death row
            virtual void copyTo (x10_int src_off,
                                 x10::lang::Place dst_place,
                                 x10aux::ref<Fun_0_0<x10aux::ref<Rail<T> > > > dst_finder,
                                 x10_int len,
                                 x10aux::ref<VoidFun_0_0> notifier);

            virtual void copyTo (x10_int src_off,
                                 x10::lang::Place dst_place,
                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >, x10_int> > > dst_finder,
                                 x10_int len,
                                 x10aux::ref<VoidFun_0_0> notifier);

            static const x10aux::serialization_id_t _copy_from_serialization_id;

            static void *_copy_from_buffer_finder(x10aux::deserialization_buffer&, x10_int);

            static void _copy_from_notifier(x10aux::deserialization_buffer&, x10_int);

            virtual void copyFrom (x10_int dst_off, x10aux::ref<Rail<T> > src, x10_int src_off,
                                   x10_int len);

            virtual void copyFrom (x10_int dst_off, x10aux::ref<ValRail<T> > src, x10_int src_off,
                                   x10_int len);

            virtual x10aux::ref<String> toString() { return x10aux::railToString<T,Rail<T> >(this); }
        };

        void Rail_notifyEnclosingFinish(x10aux::deserialization_buffer& buf);

        void Rail_serializeAndSend(x10::lang::Place dst_place_, x10aux::ref<x10::lang::Object> df, x10aux::serialization_id_t _id, void* data, size_t size);

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
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/Fun_0_0.h>
#include <x10aux/itables.h>
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION

namespace x10 {

    namespace lang {

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
            rail->x10::lang::Ref::_constructor();
            // Memset both for efficiency and to allow T to be a struct.
            memset(rail->raw(), 0, length * sizeof(T));
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length,
                                                               x10aux::ref<Fun_0_1<x10_int,T> > init ) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            rail->x10::lang::Ref::_constructor();
            x10aux::ref<x10::lang::Object> initAsObj = init;
            // FIXME:  This is a complete hack to compensate for some problem in the RTT infrastructure.  Same HACK in ValRail.h
            initAsObj->_type();
            typename Fun_0_1<x10_int,T>::template itable<x10::lang::Object> *it = x10aux::findITable<Fun_0_1<x10_int,T> >(initAsObj->_getITables());
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (initAsObj.operator->()->*(it->apply))(i);
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10aux::ref<ValRail<T> > other) {
            x10aux::nullCheck(other);
            x10_int length = other->FMGL(length);
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            rail->x10::lang::Ref::_constructor();
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i];
            }
            return rail;
        }

        template <class T> void *Rail<T>::_copy_to_buffer_finder (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10aux::ref<Rail<T> > R;
            typedef x10::util::Pair<R,x10_int> P;
            assert(len%sizeof(T) == 0); // we can only transmit whole array elements
            len /= sizeof(T);
            x10_byte code = buf.read<x10_byte>();
            R this_;
            x10_int dst_off;
            _X_("Finding a rail for copyTo ("<<(int)code<<")");
            switch (code) {
                case 0: {
                    this_ = buf.read<R>();
                    dst_off = buf.read<x10_int>();
                    break;
                }
                case 1: {
                    x10aux::ref<Object> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    P pair = (bf.operator->()->*(x10aux::findITable<Fun_0_0<P> >(bf->_getITables())->apply))();
                    this_ = pair.FMGL(first);
                    dst_off = pair.FMGL(second);
                    x10aux::dealloc(bf.operator->());
                    break;
                }
                case 2: {
                    // on death row
                    dst_off = 0;
                    x10aux::ref<Object> bf = buf.read<x10aux::ref<Fun_0_0<R> > >();
                    this_ = (bf.operator->()->*(x10aux::findITable<Fun_0_0<R> >(bf->_getITables())->apply))();
                    x10aux::dealloc(bf.operator->());
                    break;
                }
                case 3: {
                    x10aux::ref<Object> bf = buf.read<x10aux::ref<Fun_0_0<P> > >();
                    P pair = (bf.operator->()->*(x10aux::findITable<Fun_0_0<P> >(bf->_getITables())->apply))();
                    this_ = pair.FMGL(first);
                    dst_off = pair.FMGL(second);
                    x10aux::dealloc(bf.operator->());
                    break;
                }
                default:
                abort();
            }

            // FIXME: should catch exception here
            x10aux::checkRailBounds(dst_off, this_->FMGL(length));
            x10aux::checkRailBounds(dst_off+len-1, this_->FMGL(length));

            return &this_->_data[dst_off];
        }
                                                                                           
        template <class T> void Rail<T>::_copy_to_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            typedef x10aux::ref<Rail<T> > R;
            x10_byte code = buf.read<x10_byte>();
            _X_("Completing a rail copyTo ("<<(int)code<<")");
            switch (code) {
                case 0:
                break;
                case 1: {
                    x10aux::ref<Object> bf = buf.read<x10aux::ref<Fun_0_0<R> > >();
                    x10aux::dealloc(bf.operator->());
                    Rail_notifyEnclosingFinish(buf);
                    break;
                }
                // case 2 on death row
                case 2: case 3: {
                    x10aux::ref<Object> bf = buf.read<x10aux::ref<Fun_0_0<R> > >();
                    x10aux::dealloc(bf.operator->());
                    x10aux::ref<Object> vf = buf.read<x10aux::ref<VoidFun_0_0> >();
                    (vf.operator->()->*(x10aux::findITable<VoidFun_0_0>(vf->_getITables())->apply))();
                    x10aux::dealloc(vf.operator->());
                    break;
                }
                default: abort();
            }
        }
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_to_serialization_id =
            x10aux::DeserializationDispatcher
                ::addPutFunctions(Rail<T>::_copy_to_buffer_finder,
                                  Rail<T>::_copy_to_notifier);

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
            x10_byte code = 0;
            buf.write(code, m);
            buf.write(dst, m);
            buf.write(dst_off, m);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        }

        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10::lang::Place dst_place_,
                                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >,
                                                                                     x10_int> > > dst_finder,
                                                 x10_int len)
        {
            typedef x10aux::ref<Rail<T> > R;
            typedef x10::util::Pair<R,x10_int> P;
            R this_ = this;
            x10_int dst_place = dst_place_.FMGL(id);

            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::ref<Object> df = dst_finder;
            if (dst_place == x10aux::here) {
                P pair = (df.operator->()->*(x10aux::findITable<Fun_0_0<P> >(df->_getITables())->apply))();
                R dst = pair.FMGL(first);
                x10_int dst_off = pair.FMGL(second);
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
            Rail_serializeAndSend(dst_place_, df, _copy_to_serialization_id, &_data[src_off], len * sizeof(T));
        }


        // on death row
        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10::lang::Place dst_place_,
                                                 x10aux::ref<Fun_0_0<x10aux::ref<Rail<T> > > > dst_finder,
                                                 x10_int len,
                                                 x10aux::ref<VoidFun_0_0> notifier)
        {
            x10aux::ref<Rail<T> > this_ = this;
            x10_int dst_place = dst_place_.FMGL(id);

            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::ref<Object> df = dst_finder;
            x10aux::ref<Object> n = notifier;
            if (dst_place == x10aux::here) {
                x10aux::ref<Rail<T> > dst = (df.operator->()->*(x10aux::findITable<Fun_0_0<x10aux::ref<Rail<T> > > >(df->_getITables())->apply))();
                x10_int dst_off = 0;
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
                (n.operator->()->*(x10aux::findITable<VoidFun_0_0>(n->_getITables())->apply))();
                return;
            }
            x10aux::serialization_buffer buf;
            x10aux::addr_map m;
            buf.realloc_func = x10aux::put_realloc;
            x10_byte code = 2;
            buf.write(code, m);
            buf.write(df, m);
            buf.write(n, m);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        }



        template <class T> void Rail<T>::copyTo (x10_int src_off,
                                                 x10::lang::Place dst_place_,
                                                 x10aux::ref<Fun_0_0<x10::util::Pair<x10aux::ref<Rail<T> >, x10_int> > > dst_finder,
                                                 x10_int len,
                                                 x10aux::ref<VoidFun_0_0> notifier)
        {
            typedef x10aux::ref<Rail<T> > R;
            typedef x10::util::Pair<R,x10_int> P;
            R this_ = this;
            x10_int dst_place = dst_place_.FMGL(id);

            // check beginning and end of range
            x10aux::checkRailBounds(src_off, FMGL(length));
            x10aux::checkRailBounds(src_off+len-1, FMGL(length));
            x10aux::ref<Object> df = dst_finder;
            x10aux::ref<Object> n = notifier;
            if (dst_place == x10aux::here) {
                P pair = (df.operator->()->*(x10aux::findITable<Fun_0_0<P> >(df->_getITables())->apply))();
                x10aux::ref<Rail<T> > dst = pair.FMGL(first);
                x10_int dst_off = pair.FMGL(second);
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
                (n.operator->()->*(x10aux::findITable<VoidFun_0_0>(n->_getITables())->apply))();
                return;
            }
            x10aux::serialization_buffer buf;
            x10aux::addr_map m;
            buf.realloc_func = x10aux::put_realloc;
            x10_byte code = 3;
            buf.write(code, m);
            buf.write(df, m);
            buf.write(n, m);
            x10aux::send_put(dst_place, _copy_to_serialization_id,
                             buf, &_data[src_off], len * sizeof(T));

        }


        // {{{ copy_from

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
                                                                                           
        template <class T> void Rail<T>::_copy_from_notifier (
                                                   x10aux::deserialization_buffer &buf,
                                                   x10_int len)
        {
            // can't be bothered yet
        }
                                                                                           
        template<class T> const x10aux::serialization_id_t Rail<T>::_copy_from_serialization_id =
            x10aux::DeserializationDispatcher
                ::addGetFunctions(Rail<T>::_copy_from_buffer_finder,
                                  Rail<T>::_copy_from_notifier);

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

        // }}}

        template<class T> const x10aux::serialization_id_t Rail<T>::_serialization_id =
            x10aux::DeserializationDispatcher
                ::addDeserializer(Rail<T>::template _deserializer<Ref>);

        // Specialized serialization
        template <class T> void Rail<T>::_serialize(x10aux::ref<Rail<T> > this_,
                                                    x10aux::serialization_buffer &buf,
                                                    x10aux::addr_map &m) {
            Ref::_serialize_reference(this_, buf, m);
            if (this_ != x10aux::null) {
                this_->_serialize_body(buf, m);
            }
        }

        template <class T> void Rail<T>::_serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
            x10_int length = this->FMGL(length);
            buf.write(length, m);
            this->Ref::_serialize_body(buf, m); // intentional change of order
        }

        template <class T> void Rail<T>::_deserialize_body(x10aux::deserialization_buffer &buf) {
            // length read out earlier, in _deserializer()
            this->Ref::_deserialize_body(buf);
        }

        template <class T> template<class S> x10aux::ref<S> Rail<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10_int length = buf.read<x10_int>();
            // Don't allocate any storage for the data - it's a remote rail
            x10aux::ref<Rail<T> > this_ = x10aux::alloc_rail_remote<T,Rail<T> >(0);
            // But the above set the length to 0, so set it correctly
            const_cast<x10_int&>(this_->FMGL(length)) = length;
            this_->_deserialize_body(buf);
            return this_;
        }

        // Specialized deserialization
        template <class T> template<class S> x10aux::ref<S> Rail<T>::_deserialize(x10aux::deserialization_buffer &buf) {
            Ref::_reference_state rr = Ref::_deserialize_reference_state(buf);
            x10aux::ref<Rail<T> > this_;
            if (rr.ref != 0) {
                this_ = Rail<T>::template _deserializer<Rail<T> >(buf);
            }
            return Ref::_finalize_reference<T>(this_, rr);
        }
    }
}

#endif
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
