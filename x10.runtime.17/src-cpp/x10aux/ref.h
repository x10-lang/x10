namespace x10aux {
    template <class T> class ref;
}


#ifndef X10REF_H
#define X10REF_H

#include <cstdlib>

#include <x10aux/config.h>

namespace x10aux {
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
                //_R_("    type=" << TYPEID(o,"null"));
                //_R_("    count=" << o->__count);
            }
        }
        void _dec(T* o) { // TODO
            if (o != NULL) {
                o->__count--;
                //_R_("    type=" << DEMANGLE(TYPENAME(o)));
                //_R_("    count=" << o->__count);
                // TODO
                //if (!o->__count) { o->~T(); dealloc(o); }
            }
        }
#endif
        public:
        ~ref() { DEC(_val); }
        ref(const ref<T>& _ref) : REF_INIT(_ref._val) {
            _R_("Copying reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(T)) << " to " << this);
            INC(_val);
        }
        // FIXME: something is wrong with the return value; r1 = r2 = r3 doesn't work in xlC
        const ref<T>& operator=(const ref<T>& _ref) {
            _val = _ref._val;
            _R_("Assigning reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(T)) << " to " << this);
            INC(_val);
            return *this;
        }
        ref(const T* val = NULL) : REF_INIT(const_cast<T*>(val)) {
            INC(_val);
        }
        ref(const T& val);
        template<class S> operator ref<S>() const {
            ref<S> _ref(dynamic_cast<S*>((T*)_val));
            _R_("Casting reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)) << " to type " << DEMANGLE(TYPENAME(S)) << " into " << &_ref);
            return _ref;
        }
// TODO: find out which one is better -- the cast operator, or the constructor + assignment
//        template<class S> ref(const ref<S>& _ref) : REF_INIT(dynamic_cast<T*>((S*)_ref._val)) {
//            _R_("Casting reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(S)) << " to type " << DEMANGLE(TYPENAME(T)) << " into " << this);
//            INC(_val);
//        }
//        template<class S> const ref<T>& operator=(const ref<S>& _ref) {
//            _val = dynamic_cast<T*>((S*)_ref._val);
//            _R_("Casting reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(S)) << " to type " << DEMANGLE(TYPENAME(T)) << " into " << this);
//            INC(_val);
//            return *this;
//        }
        T& operator*() const { // FIXME: throw NullPointerException
            _R_("Accessing object (*) via reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return *(T*)_val;
        }
        T* operator->() const { // FIXME: should we throw NullPointerException here?
            _R_("Accessing object (->) via reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return (T*)_val;
        }
        bool isNull() const {
            _R_("Nullcheck reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return _val == NULL;
        }

/* TODO: come back to this later once i find out why we need it
        bool operator==(const T* val) const {
            return _val == val;
        }
        bool operator!=(const T* val) const {
            return _val != val;
        }
*/

        bool operator==(const ref<T>& _ref) const {
            return _val == _ref._val;
        }


        bool operator!=(const ref<T>& _ref) const { 
            return _val != _ref._val;
        }

#ifndef REF_STRIP_TYPE
    public: // [IP] temporary
        T* _val;
#endif
    };

} //namespace x10


#endif

