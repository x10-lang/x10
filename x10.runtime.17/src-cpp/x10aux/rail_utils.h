#ifndef X10AUX_RAIL_UTIL_H
#define X10AUX_RAIL_UTIL_H

#include <x10/lang/Value.h>
#include <x10/lang/String.h>

namespace x10aux {


        template<class T> class AnyRail : public x10::lang::Value {

            public:

            // 32 bit array indexes
            const x10_int FMGL(length);

            T data[1];

            private: AnyRail(const AnyRail<T>& arr); // disabled

            public:

            AnyRail(x10_int length_)
              : Value(), FMGL(length)(length_) { }

            virtual ~AnyRail() {
                for (int i=0; i<FMGL(length); i++)
                    data[i].~T();
            }

            virtual const RuntimeType *_type() const = 0;

            void _check_bounds(x10_int index) const {
                (void)index;
                #ifndef NO_BOUNDS_CHECKS
                #ifndef NO_EXCEPTIONS
                if (index<0 || index>FMGL(length)) {
                    std::stringstream msg;
                    msg<<index<<" not in [0,"<<FMGL(length)<<")";
                    //String msg_str(msg.str());
/* FIXME            
                    throw x10aux::ref<ArrayIndexOutOfBoundsException>
                        (
                          new (x10aux::alloc<ArrayIndexOutOfBoundsException>())
                            ArrayIndexOutOfBoundsException(msg_str);
                        );
*/
                }
                #endif
                #endif
            }

            virtual bool equals (const ref<x10::lang::Object> &other) const {
                if (!_type()->concreteInstanceOf(other)) return false;
                const AnyRail &other_rail = static_cast<AnyRail&>(*other);
                // different sizes so false
                if (other_rail.FMGL(length)!=FMGL(length)) return false;
/*              FIXME: ARRRRRRRRRRRRGH
                for (x10_int index=0 ; index<FMGL(length) ; ++index) {
                    if (other_rail[index]!=this->operator[](index))
                        return false;
                }
*/
                return true;
            }

            virtual x10_int hashCode() const { return 0; }

            virtual ref<x10::lang::String> toString() const {
                return new (alloc<x10::lang::String>()) x10::lang::String();
            }
                

            const T& operator[](x10_int index) const {
                _check_bounds(index);
                return data[index];
            }   

            T& operator[](x10_int index) {
                _check_bounds(index);
                return data[index];
            }
          
            T* raw() { return data; }

        };

        template<class T, class R> R* alloc_rail(x10_int length) {
            size_t sz = sizeof(R) + length*sizeof(T);
            R *rail = new (x10aux::alloc<R>(sz)) R(length);
            _M_("In alloc_rail<"<<getRTT<T>()->name()
                                <<","<<getRTT<R>()->name()<<">"
                <<": rail = " << (void*)rail << "; length = " << length);
            return rail;
        }

        template<class T, class R> R* alloc_rail(x10_int length, const T& v0) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 0);
            (*rail)[0] = v0;
            return rail;
        }

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 1);
            (*rail)[0] = v0;
            (*rail)[1] = v1;
            return rail;
        }

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1,
                                                                 const T& v2) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 2);
            (*rail)[0] = v0;
            (*rail)[1] = v1;
            (*rail)[2] = v2;
            return rail;
        }

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1,
                                                                 const T& v2,
                                                                 const T& v3) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 3);
            (*rail)[0] = v0;
            (*rail)[1] = v1;
            (*rail)[2] = v2;
            (*rail)[3] = v3;
            return rail;
        }

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1,
                                                                 const T& v2,
                                                                 const T& v3,
                                                                 const T& v4) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 4);
            (*rail)[0] = v0;
            (*rail)[1] = v1;
            (*rail)[2] = v2;
            (*rail)[3] = v3;
            (*rail)[4] = v4;
            return rail;
        }

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1,
                                                                 const T& v2,
                                                                 const T& v3,
                                                                 const T& v4,
                                                                 const T& v5) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 5);
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

        template<class T, class R> R *alloc_rail(x10_int length, const T& v0,
                                                                 const T& v1,
                                                                 const T& v2,
                                                                 const T& v3,
                                                                 const T& v4,
                                                                 const T& v5,
                                                                 const T& v6,
                                                                 ...) {
            R* rail = alloc_rail<T,R>(length);
            assert (length > 6);
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


        template<class T, class R> void free_rail(const x10aux::ref<R> &rail) {
            rail->~R();
            x10aux::dealloc<R >(&*rail);
        }

}

#endif
