#ifndef X10REF_H
#define X10REF_H

#include <cstdlib>

#include <x10aux/config.h>
#include <x10aux/RTT.h>

namespace x10 { namespace lang { class Object; } }

namespace x10aux {

    void throwNPE();

    class __ref {
        protected:
        #ifndef REF_STRIP_TYPE
        __ref(void* = NULL) { }
        #else
        __ref(void* val = NULL) : _val(val) { }
        public: // [IP] temporary
        void* _val;
        #endif
    };

    #ifndef REF_STRIP_TYPE
    #  define REF_INIT(v) __ref(), _val(v)
    #else
    #  define REF_INIT(v) __ref(v)
    #endif
    #ifdef REF_COUNTING
    #  define INC(x) _inc(x)
    #  define DEC(x) _dec(x)
    #else
    #  define INC(x)
    #  define DEC(x)
    #endif

    template<class T> class ref : public __ref {

        //typedef typename T::RTT RTT;

        #ifdef REF_COUNTING
        void _inc(T* o) { // TODO
            if (o != NULL) {
                o->__count++;
                //_R_("    type=" << TYPENAME(o));
                //_R_("    count=" << o->__count);
            }
        }
        void _dec(T* o) { // TODO
            if (o != NULL) {
                o->__count--;
                //_R_("    type=" << TYPENAME(o));
                //_R_("    count=" << o->__count);
                // TODO
                //if (!o->__count) { o->~T(); dealloc(o); }
            }
        }
        #endif

        public:

        ~ref() { DEC(_val); }

        // Copy between refs of the same type
        ref(const ref<T>& _ref) : REF_INIT(_ref._val) {
            _R_("Copying reference " << &_ref << "(" << _ref._val
                                     << ") of type " << TYPENAME(T)
                                     << " to " << this);
            INC(_val);
        }

        // Copy between refs of the same type
        // FIXME: something is wrong with the return value;
        // r1 = r2 = r3 doesn't work in xlC
        const ref<T>& operator=(const ref<T>& _ref) {
            _val = _ref._val;
            _R_("Assigning reference " << &_ref << "(" << _ref._val
                                       << ") of type " << TYPENAME(T)
                                       << " to " << this);
            INC(_val);
            return *this;
        }

        // This is the big one -- turns a pointer into a ref
        // currently an implicit conversion
        ref(T* const val = NULL) : REF_INIT(val) {
            INC(_val);
        }

        // Allow explicit casting between ref<S> and ref<T> dynamic_cast is
        // needed not to check for bad casts but to allow casting in the
        // context of virtual multiple inheritance.  Bad casts should never
        // happen, as the only places this operation is used are: class_cast
        // (which is guarded by a check) and upcasts from x10 code all operator
        // T are implicit conversions, this is no exception
/*
        template<class S> operator ref<S>() const {
            // (T*) cast needed when REF_STRIP_TYPE defined, otherwise harmless
            ref<S> _ref(dynamic_cast<S*>((T*)_val));
            _R_("Casting reference " << this << "(" << _val
                                     << ") of type " << TYPENAME(T)
                                     << " to type " << TYPENAME(S)
                                     << " into " << &_ref);
            return _ref;
        }
*/


        // Allow the construction of a ref<T> from a ref<S>
        template<class S> ref(const ref<S>& _ref)
            // (S*) cast needed when REF_STRIP_TYPE defined, otherwise harmless
          : REF_INIT(dynamic_cast<T*>((S*)_ref._val)) {
            _R_("Casting reference " << &_ref << "(" << _ref._val
                                     << ") of type " << TYPENAME(S)
                                     << " to type " << TYPENAME(T)
                                     << " into " << this << "("<<_val<<")");
            // assert that the above dynamic_cast was successful
            assert(isNull() == _ref.isNull());
            INC(_val);
        }
        
        // Allow the assignment of a ref<S> to a ref<T>
        template<class S> const ref<T> &operator=(const ref<S>& _ref) {
            // (S*) cast needed when REF_STRIP_TYPE defined, otherwise harmless
            _val = dynamic_cast<T*>((S*)_ref._val);
            _R_("Casting reference " << &_ref << "(" << _ref._val
                                     << ") of type " << TYPENAME(S)
                                     << " to type " << TYPENAME(T)
                                     << " into " << this << "("<<_val<<")");
            // assert that the above dynamic_cast was successful
            assert(isNull() == (_val==NULL));
            INC(_val);
            return *this;
        }

        inline void assertNonNull() const {
            #ifndef NO_EXCEPTIONS
            if (isNull()) throwNPE();
            #endif
        }

        T& operator*() const {
            _R_("Accessing object (*) via reference " << this << "(" << _val
                                      << ") of type " << TYPENAME(T));
            assertNonNull();
            return *(T*)_val;
        }

        T* get() const { 
            return (T*)_val;
        }

        T* operator->() const { 
            _R_("Accessing object (*) via reference " << this << "(" << _val
                                      << ") of type " << TYPENAME(T));
            assertNonNull();
            return (T*)_val;
        }

        bool isNull() const {
            _R_("Nullcheck reference " << this << "(" << _val
                                      << ") of type " << TYPENAME(T));
            return _val == NULL;
        }

        /* TODO: come back to this later once i find out why we need it [DC]
        bool operator==(const T* val) const {
            return _val == val;
        }
        bool operator!=(const T* val) const {
            return _val != val;
        }
        */

        // trivial operations that compare the contents of the two refs
        bool operator==(const ref<T>& _ref) const { return _val == _ref._val; }
        bool operator!=(const ref<T>& _ref) const { return _val != _ref._val; }


#ifndef REF_STRIP_TYPE
        T* _val;
#endif

    };

#ifndef NO_IOSTREAM
    template<class T> std::ostream& operator<<(std::ostream& o, ref<T> s) {
        if (s.isNull()) {
            o << "null";
        } else {
            o << *s;
        }
        return o;
    }
#endif

    // will be initialised to null
    typedef ref<x10::lang::Object> NullType;
    extern NullType null;

} //namespace x10


#endif

// vim:tabstop=4:shiftwidth=4:expandtab
