#ifndef X10AUX_RAIL_UTILS_H
#define X10AUX_RAIL_UTILS_H

#ifndef NO_IOSTREAM
#include <sstream>
#endif

#include <cstdarg>

#include <x10aux/config.h>


namespace x10 { namespace lang {
    class String;
} }
#include <x10/lang/Iterator.h>

#include <x10/lang/Iterable.h>

namespace x10aux {

    void throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) X10_PRAGMA_NORETURN;

    template<class T> class AnyRail : public virtual x10::lang::Iterable<T> { 
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

        private: AnyRail(const AnyRail<T> &arr); // disabled

        public:

        virtual const RuntimeType *_type() const = 0;

        AnyRail(x10_int length_, T* storage)
            : FMGL(length)(length_),  _data(storage) { }

        void _check_bounds(x10_int index) const {
            #ifndef NO_BOUNDS_CHECKS
            // Since we know length is non-negative and Rails are zero-based,
            // the bounds check can be optimized to a single unsigned comparison.
            // The C++ compiler won't do this for us, since it doesn't know that length is non-negative.
            if (((x10_unsigned_int)index) >= ((x10_unsigned_int)FMGL(length))) {
                x10aux::throwArrayIndexOutOfBoundsException(index, FMGL(length));
            }
            #endif
        }

        virtual ref<x10::lang::String> toString();

        GPUSAFE T apply(x10_int index) {
            // do bounds check
            return operator[](index);
        }   

        GPUSAFE T& operator[](x10_int index) {
            _check_bounds(index);
            return _data[index];
        }
      
        T* raw() { return _data; }

    };

    template<class T, class R> R* alloc_rail(x10_int length);
    template<class T, class R> R* alloc_rail(x10_int length, T v0);
    template<class T, class R> R* alloc_rail(x10_int length, T v0, T v1);
    template<class T, class R> R* alloc_rail(x10_int length, T v0, T v1, T v2);
    template<class T, class R> R* alloc_rail(x10_int length, T v0, T v1, T v2, T v3);
    template<class T, class R> R* alloc_rail(x10_int length, T v0, T v1, T v2, T v3, T v4);
    template<class T, class R> R* alloc_rail(x10_int length, T v0, T v1, T v2, T v3, T v4, T v5);
    template<class T, class R> R *alloc_rail(x10_int length, T v0, T v1, T v2, T v3, T v4, T v5, T v6, ...);
}

#include <x10/lang/String.h>

namespace x10aux {

    template<class T> ref<x10::lang::String> AnyRail<T>::toString() {
        if (this->FMGL(length)==0) {
            return x10::lang::String::Lit("[]");
        }
        #ifndef NO_IOSTREAM
        std::stringstream ss;
        const char *prefix = "[";
        for (x10_int i=0 ; i<this->FMGL(length) ; ++i) {
            T element = (*this)[i];
            ss << prefix << x10aux::safe_to_string(element);
            prefix = ",";
        }
        ss << "]";
        return x10::lang::String::Lit(ss.str().c_str());
        #else
        return x10::lang::String::Lit("[NO_IOSTREAM so no contents]");
        #endif
    }

    template<class T, class R> R* alloc_rail(x10_int length) {
        /*
         * We allocate a single piece of storage that is big enough for both
         * R and its (aligned) backing data array. We then do some pointer arithmetic
         * to get the aligned "interior pointer" to use for data and pass it as
         * an argument to R's constructor (R's data ptr is declared const to enable compiler optimization).
         */
        size_t alignPad = 8;
        size_t alignDelta = alignPad-1;
        size_t alignMask = ~alignDelta;
        size_t sz = sizeof(R) + alignPad + length*sizeof(T);
        R* uninitialized_rail = x10aux::alloc<R>(sz);
        size_t raw_rail = (size_t)uninitialized_rail;
        size_t raw_data = (raw_rail + sizeof(R) + alignDelta) & alignMask;
        R *rail = new (uninitialized_rail) R(length, (T*)raw_data);
        _M_("In alloc_rail<"<<getRTT<T>()->name()
                            <<","<<getRTT<R>()->name()<<">"
            <<": rail = " << (void*)rail << "; length = " << length);
        return rail;
    }

    template<class T, class R> R* alloc_rail(x10_int length, T v0) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        return rail;
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        return rail;
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1,
                                                             T v2) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        (*rail)[2] = v2;
        return rail;
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1,
                                                             T v2,
                                                             T v3) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        (*rail)[2] = v2;
        (*rail)[3] = v3;
        return rail;
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1,
                                                             T v2,
                                                             T v3,
                                                             T v4) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        (*rail)[2] = v2;
        (*rail)[3] = v3;
        (*rail)[4] = v4;
        return rail;
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1,
                                                             T v2,
                                                             T v3,
                                                             T v4,
                                                             T v5) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        (*rail)[2] = v2;
        (*rail)[3] = v3;
        (*rail)[4] = v4;
        (*rail)[5] = v5;
        return rail;
    }

    // init elements 7 though length
    template<class T> inline void init_rail(AnyRail<T> *rail,
                                            x10_int length,
                                            va_list init) {
        for (int i = 7; i < length; i++)
            (*rail)[i] = va_arg(init, T);
    }
    // init elements 7 though length: specialize for x10_byte
    template<> inline void init_rail<x10_byte>(AnyRail<x10_byte> *rail,
                                               x10_int length,
                                               va_list init) {
        for (int i = 7; i < length; i++)
            (*rail)[i] = (x10_byte)va_arg(init, int);
    }
    // init elements 7 though length: specialize for x10_char
    template<> inline void init_rail<x10_char>(AnyRail<x10_char> *rail,
                                               x10_int length,
                                               va_list init) {
        for (int i = 7; i < length; i++)
            (*rail)[i] = (x10_char)va_arg(init, int);
    }
    // init elements 7 though length: specialize for x10_short
    template<> inline void init_rail<x10_short>(AnyRail<x10_short> *rail,
                                                x10_int length,
                                                va_list init) {
        for (int i = 7; i < length; i++)
            (*rail)[i] = (x10_short)va_arg(init, int);
    }
    // init elements 7 though length: specialize for x10_float
    template<> inline void init_rail<x10_float>(AnyRail<x10_float> *rail,
                                                x10_int length,
                                                va_list init) {
        for (int i = 7; i < length; i++)
            (*rail)[i] = (x10_float)va_arg(init, double);
    }

    template<class T, class R> R *alloc_rail(x10_int length, T v0,
                                                             T v1,
                                                             T v2,
                                                             T v3,
                                                             T v4,
                                                             T v5,
                                                             T v6,
                                                             ...) {
        R* rail = alloc_rail<T,R>(length);
        (*rail)[0] = v0;
        (*rail)[1] = v1;
        (*rail)[2] = v2;
        (*rail)[3] = v3;
        (*rail)[4] = v4;
        (*rail)[5] = v5;
        (*rail)[6] = v6;
        va_list init;
        va_start(init, v6);
        // Need to specialize this loop
        //for (int i = 7; i < length; i++)
        //    (*rail)[i] = va_arg(init, T);
        init_rail(rail, length, init);
        va_end(init);
        return rail;
    }


    template<class T, class R> void free_rail(x10aux::ref<R> rail) {
        x10aux::dealloc<R >(&*rail);
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
