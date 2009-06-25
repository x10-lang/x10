#ifndef __X10LANG_H
#define __X10LANG_H

#include <stdio.h>
#include <string>
#include <iostream>
#include <stdint.h>
#ifdef __GNUC__
#include <cxxabi.h>
#endif
#include <x10/x10lib.h>

using namespace std;

#ifdef TRACE_ALLOC
#define _T_(x) cerr << x10lib::here() << ": MM: " << x << endl
#else
#define _T_(x)
#endif

#ifdef TRACE_REF
#define _R_(x) cerr << x10lib::here() << ": RR: " << x << endl
#else
#define _R_(x)
#endif

#ifdef DEBUG
#define _D_(x) cerr << x10lib::here() << ": " << x << endl
#else
#define _D_(x)
#endif

#ifdef __GNUC__
#define TYPENAME(T) (const char*)abi::__cxa_demangle(typeid(T).name(), NULL, NULL, NULL)
#define TYPEID(T,D) TYPENAME(T)
#else
#define TYPENAME(T) typeid(T).name()
#define TYPEID(T,D) D
#endif

typedef bool     x10_boolean;
typedef uint16_t x10_char;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;

namespace x10 {
    class __init__;
    template<class T> class ref;
    namespace lang {
        class String;
        // x10.lang.Object
        class Object {
            int __count; // Ref counting implementation
        protected:
            const char* _type;
        public:
            explicit Object(const char* _t = NULL) : __count(0), _type(_t?_t:TYPEID(*this,"x10::lang::Object")) { _T_("Creating object " << this << " of type " << _type); }
            virtual ~Object() { _T_("Destroying object " << this << " of type " << _type); }
            template<class T> friend class x10::ref;
            virtual x10_int hashCode() { return 0; }
            virtual x10::ref<String> toString();
            // TODO: toString(), equals()
        };
    }

    // Object allocation
    template<class T> extern T* alloc(size_t size = sizeof(T), T* ptr = NULL);
    template<class T> extern void dealloc(T* obj);

    class __ref {
    protected:
        __ref(void* val = NULL) : _val(val) { }
    public: // [IP] temporary
        void* _val;
    };
    template<class T> class ref : public __ref {
    public:
        ~ref() { }
//        ref(const ref<T>& _ref) : _val(_ref._val) { _R_("Copying reference " << &_ref << "(" << _ref._val << ") of type " << TYPENAME(T) << " to " << this); }
        ref(const ref<T>& _ref) : __ref(_ref._val) { _R_("Copying reference " << &_ref << "(" << _ref._val << ") of type " << TYPENAME(T) << " to " << this); }
        // FIXME: something is wrong with the return value; r1 = r2 = r3 doesn't work in xlC
        const ref<T>& operator=(const ref<T>& _ref) { _val = _ref._val; _R_("Assigning reference " << &_ref << "(" << _ref._val << ") of type " << TYPENAME(T) << " to " << this); return *this; }
//        ref(const T* val = NULL) : _val(const_cast<T*>(val)) { }
        ref(const T* val = NULL) : __ref(const_cast<T*>(val)) { }
        ref(const T& val);
//        template<class S> operator ref<S>() const { ref<S> _ref(dynamic_cast<S*>(_val)); _R_("Casting reference " << this << "(" << _val << ") of type " << TYPENAME(T) << " to type " << TYPENAME(S) << " into " << &_ref); return _ref; }
        template<class S> operator ref<S>() const { ref<S> _ref(dynamic_cast<S*>((T*)_val)); _R_("Casting reference " << this << "(" << _val << ") of type " << TYPENAME(T) << " to type " << TYPENAME(S) << " into " << &_ref); return _ref; }
//        T& operator*() const { _R_("Accessing object (*) via reference " << this << "(" << _val << ") of type " << TYPENAME(T)); return *_val; } // FIXME: throw NullPointerException
        T& operator*() const { _R_("Accessing object (*) via reference " << this << "(" << _val << ") of type " << TYPENAME(T)); return *(T*)_val; } // FIXME: throw NullPointerException
//        T* operator->() const { _R_("Accessing object (->) via reference " << this << "(" << _val << ") of type " << TYPENAME(T)); return _val; }
        T* operator->() const { _R_("Accessing object (->) via reference " << this << "(" << _val << ") of type " << TYPENAME(T)); return (T*)_val; }
        bool isNull() const { _R_("Nullcheck reference " << this << "(" << _val << ") of type " << TYPENAME(T)); return _val == NULL; }
    private:
//        T* _val;
    };
//    template<class T> class ref {
//        void inc(T* o) {
//            //cout << "Ref " << this << " copied" << endl;
//            // TODO: ref count
//            if (o != NULL) {
//                o->__count++;
//                //cout << "    type=" << TYPEID(o,"null") << endl;
//                //cout << "    count=" << o->__count << endl;
//            }
//        }
//        void dec(T* o) {
//            //cout << "Ref " << this << " destroyed" << endl;
//            // TODO: ref count
//            if (o != NULL) {
//                o->__count--;
//                //cout << "    type=" << TYPENAME(o) << endl;
//                //cout << "    count=" << o->__count << endl;
//            //    if (!o->__count) dealloc(o);
//            }
//        }
//    public:
//        ~ref() { dec(_val); }
//        ref(const ref<T>& _ref) : _val(_ref._val) { inc(_val); }
//        const ref<T>& operator=(const ref<T>& _ref) { *(const_cast<T**const>(&_val)) = _ref._val; inc(_val); return *this; }
//        ref(const T& val) : _val(new (alloc<T>()) T(val)) { inc(_val); }
////        ref(T*const val = NULL) : _val(val) { inc(_val); }
//        ref(const T*const val = NULL) : _val(const_cast<T*const>(val)) { inc(_val); }
//        template<class S> operator ref<S>() { return ref<S>(dynamic_cast<S*const>(_val)); }
//        const T& operator*() const { return *_val; } // FIXME: throw NullPointerException
//        T& operator*() { return *_val; } // FIXME: throw NullPointerException
//        const T* operator->() const { return _val; }
//        T* operator->() { return _val; }
//        bool isNull() const { return _val == NULL; }
//        // TODO: reference counting
//    private:
//        T* const _val;
//    };

#define INSTANCEOF(v,T) (!!((T)(v)).operator->())

//    template<typename T> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const T& v);
    template<class T> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<T>& v);
    template<> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<x10::lang::String>& s2);
};
namespace java {
    namespace lang {
        // java.lang.Integer
        class Integer : public x10::lang::Object {
            Integer() { } // Cannot instantiate
        public:
            static const x10_int parseInt(const x10::ref<x10::lang::String>& s);
        };
    }
};
namespace x10 {
    namespace io {
        // x10.io.PrintStream
        class PrintStream : public x10::lang::Object {
            FILE* _stream;
        public:
            explicit PrintStream(FILE* stream) : Object(TYPEID(*this,"x10::io::PrintStream")), _stream(stream) { }
            void println(const x10::ref<x10::lang::String>& str) const;
            // TODO [IP]
//            void println(const x10::ref<x10::lang::Object>& str) const;
            void println(x10_boolean b) const;
            void println(x10_int i) const;
            void println(x10_long l) const;
//            template<typename T> void println(T* o) const;
            void println(const x10::lang::String& str) const;
        };
//        template<typename T> void PrintStream::println(T* o) const {
//            println(x10::ref<T>(o));
//        }
    }
    namespace lang {
        // x10.lang.System
        class System {
        private:
            System() { } // Cannot instantiate
        public:
            static x10_long nanoTime();
            static void exit(x10_int ret);
            static const ref<x10::io::PrintStream> out;
            static const ref<x10::io::PrintStream> err;
        private:
            static void __init__out_err();
            friend class x10::__init__;
        };
    }
    template<class T> class array;
    extern array<ref<x10::lang::String> >* convert_args(int ac, char **av);
    extern void free_args(array<ref<x10::lang::String> > *arr);
    namespace lang {
        // x10.lang.String
        class String : public Object, public std::string {
            static const string to_string(x10_boolean b);
            static const string to_string(x10_int i);
            static const string to_string(x10_long i);
            static const string to_string(x10_double i);
        public:
            String() : Object(TYPEID(*this,"x10::lang::String")), string("") { }
            String(const string& content) : Object(TYPEID(*this,"x10::lang::String")), string(content) { }
            String(const char* s) : Object(TYPEID(*this,"x10::lang::String")), string(s) { }
            String(x10_boolean b) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(b)) { }
            String(x10_int i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(x10_char c) : Object(TYPEID(*this,"x10::lang::String")), string(to_string((x10_int)c)) { }
            String(x10_short s) : Object(TYPEID(*this,"x10::lang::String")), string(to_string((x10_int)s)) { }
            String(x10_long i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(x10_float f) : Object(TYPEID(*this,"x10::lang::String")), string(to_string((x10_double)f)) { }
            String(x10_double i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(const String& s) : Object(TYPEID(*this,"x10::lang::String")), string(dynamic_cast<const string&>(s)) { }
            const String& operator=(const String& s) {
                string::operator=(dynamic_cast<const string&>(s));
                return *this;
            }
            x10::ref<String> toString() { return this; }
//            x10_boolean equals(const ref<Object>& s) const;  // FIXME [IP] investigate
            // Why do we need this? -Krishna. It causes a conflict with the following function.
            x10_boolean equals(const ref<String>& s) const;
            x10_int length() const { return (x10_int) std::string::length(); }
            String operator+(const String& s) const;
            friend array<ref<x10::lang::String> >* x10::convert_args(int ac, char **av);
            friend void x10::free_args(array<ref<x10::lang::String> > *arr);
        };

        template<typename T> String operator+(const T& v, const String& s);
        template<typename T> String operator+(const ref<T>& v, const String& s);

        template<typename T> String operator+(const T& v, const String& s) {
            return String(v) + s;
        }

        template<typename T> String operator+(const ref<T>& v, const String& s) {
            return String(*v) + s;
        }
    }

//    template<typename T> inline ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const T& v) {
//        return s1 + ref<x10::lang::String>(new (alloc<x10::lang::String>()) x10::lang::String(v));
//    }

    template<typename T> inline ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<T>& v) {
        return s1 + v->toString();
    }

    namespace lang {
        // x10.lang.Exception
        class Exception : public x10::lang::Object {
            const ref<String> message;
        public:
            explicit Exception(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::Exception")), message(NULL) { }
            explicit Exception(ref<String> message, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::Exception")), message(message) { }
            virtual ~Exception() { }
            virtual ref<String> getMessage() const { return message; }
            virtual void printStackTrace(ref<x10::io::PrintStream> out) const;
        };

        // x10.lang.RuntimeException
        class RuntimeException : public Exception {
        public:
            explicit RuntimeException(const char* _t = NULL) : Exception(_t?_t:TYPEID(*this,"x10::lang::RuntimeException")) { }
            explicit RuntimeException(ref<String> message, const char* _t = NULL) : Exception(message, _t?_t:TYPEID(*this,"x10::lang::RuntimeException")) { }
        };

        // x10.lang.RankMismatchException
        class RankMismatchException : public RuntimeException {
            x10_int _idx, _rank;
        public:
            RankMismatchException(x10_int idx, x10_int rank, const char* _t = NULL) :
                RuntimeException(_t?_t:TYPEID(*this,"x10::lang::RankMismatchException")), _idx(idx), _rank(rank) { }
            virtual ref<String> getMessage() const { return String("Expected ") + _idx + String("; actual ") + _rank; }
        };

        // x10.lang.MultipleExceptions
        class MultipleExceptions : public RuntimeException {
            const x10_int _num;
            Exception** _exc;
        public:
            MultipleExceptions(x10_int num, Exception const* const* exc, const char* _t = NULL) :
                RuntimeException(_t?_t:TYPEID(*this,"x10::lang::MultipleExceptions")),
                _num(num), _exc(new Exception*[num]) // FIXME: GC
            {
                memcpy(_exc, exc, num*sizeof(Exception*));
            }
            ~MultipleExceptions() {
                delete _exc;
            }
        };

        class region;
        template<int R> class _region;

        // x10.lang.point
        class point : public x10::lang::Object {
        protected:
            explicit point(int rank, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::point")), rank(rank) { }
        public:
            virtual x10_int get(x10_int idx) const = 0;
            const int rank;
            x10_int operator[](int idx) const {
#ifndef IGNORE_EXCEPTIONS
                if (idx > rank)
                    throw x10::ref<RankMismatchException>(new (x10::alloc<RankMismatchException>()) RankMismatchException(idx, rank));
#endif
//                cerr << "point->[]: applying to " << idx << endl;
                return get(idx);
            }
            friend class region;
        };
        template<int R> class _point : public point {
        public:
            friend class _region<R>;
        };
        template<> class _point<1> : public point {
        public: // [IP] temporary
            x10_int _i;
        protected:
            x10_int get(x10_int idx) const { return _i; }
        public:
            _point(x10_int i) : point(1, TYPEID(*this,"x10::lang::_point<1>")), _i(i) { }
            friend class _region<1>;
        };

        // x10.lang.Iterator
        template<class T> class Iterator {
        protected:
            Iterator() { }
        public:
            virtual bool hasNext() const = 0;
            virtual const T& next() = 0;
        };

        class dist;
        template<int R> class _dist;
        // x10.lang.region
        class region : public x10::lang::Object {
        protected:
            virtual x10_int _translate(x10_int idx) const { return _translate(_point<1>(idx)); }
            virtual x10_int _translate(const point& p) const = 0;
        public:
            const int rank;
            explicit region(int rank, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::region")), rank(rank) { }
            x10_int translate(const point& p) const {
#ifndef IGNORE_EXCEPTIONS
                if (p.rank != rank)
                    throw x10::ref<RankMismatchException>(new (x10::alloc<RankMismatchException>()) RankMismatchException(rank, p.rank));
#endif
                return _translate(p);
            }
            x10_int translate(x10_int idx) const {
#ifndef IGNORE_EXCEPTIONS
                if (1 != rank)
                    throw x10::ref<RankMismatchException>(new (x10::alloc<RankMismatchException>()) RankMismatchException(rank, 1));
#endif
                return _translate(idx);
            }
            virtual x10_int size() const = 0;
            virtual Iterator<point>& iterator() const = 0;
            virtual x10::ref<dist> toDistribution() const;
        };
        template<int R> class _region : public region {
        public:
            _region() : region(R, TYPEID(*this,"x10::lang::_region<R>")) { }
        };
        template<> class _region<1> : public region {
            class Iter : public Iterator<point> {
                x10_int _i;
                const _region<1>& region;
            public:
                explicit Iter(const _region<1>& region) : region(region), _i(region._lo) { }
                bool hasNext() const { return _i <= region._hi; }
                const point& next() { return *(new (x10::alloc<_point<1> >()) _point<1>(_i++)); } // FIXME: GC
            };
        protected:
            x10_int _translate(x10_int idx) const {
//                cerr << "Accessing [" << _lo << ":" << _hi << "] at " << idx << endl;
#ifndef IGNORE_EXCEPTIONS
                if (idx < _lo || idx > _hi) {  // Bounds check
                    throw x10::ref<RuntimeException>(new (x10::alloc<RuntimeException>()) RuntimeException(String("Index out of bounds")));
                }
#endif
                return idx - _lo;
            }
            x10_int _translate(const point& p) const {
                return _translate(reinterpret_cast<const _point<1>&>(p)._i);
            }
            _region(const _region&) : region(1, TYPEID(*this,"x10::lang::_region<1>")), _lo(0), _hi(0) { }
            const _region<1>& operator=(const _region<1>&) { return *this; }
        public:
            x10_int size() const { return _hi - _lo + 1; }
            const x10_int _lo, _hi;
            _region(x10_int lo, x10_int hi) : region(1, TYPEID(*this,"x10::lang::_region<1>")), _lo(lo), _hi(hi) { }
            Iterator<point>& iterator() const { return *(new (x10::alloc<Iter>()) Iter(*this)); } // FIXME: GC
        };

        // x10.lang.place
        class place {
        public:
            const x10_int id;
            place(x10_int id) : id(id) { }
            operator x10_int() const { return id; }
            place next() const { return abs((id + 1) % MAX_PLACES); }
            place prev() const { return abs((id - 1) % MAX_PLACES); }
            const place* operator->() const { return this; }
            place(const place& p) : id(p.id) { }
            const place& operator=(const place& p) {
                const_cast<x10_int&>(id) = p.id;
                return *this;
            }
            static const x10_int MAX_PLACES;
        private:
            static void __init__MAX_PLACES();
            friend class x10::__init__;
        };
        inline place here() { return (place) x10lib::here(); }

        // x10.lang.dist
        class _dist_unique;
        class dist : public x10::lang::Object {
        protected:
            virtual place _translate(x10_int idx) const { return _translate(_point<1>(idx)); }
            virtual place _translate(const point& p) const = 0;
        public:
            const int rank;
            const x10::lang::region& region;
            dist(int rank, const x10::lang::region& region, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::dist")), rank(rank), region(region) { }
            Iterator<point>& iterator() const { return region.iterator(); }
            place operator[](x10_int idx) const { return _translate(idx); }
            place operator[](const x10::ref<point>& p) const { return _translate(*p); }
            place get(const x10::ref<point>& p) const { return _translate(*p); }
            static const x10::ref<_dist_unique> UNIQUE;
        private:
            static void __init__UNIQUE();
            friend class x10::__init__;
        };
        template<int R> class _dist : public dist {
        public:
            _dist(const _region<R>& region, const char* _t) : dist(R, region, _t?_t:TYPEID(*this,"x10::lang::_dist<R>")) { }
        };
        class _dist_unique : public _dist<1> {
        protected:
            place _translate(x10_int idx) const {
                return (place) idx;
            }
            place _translate(const point& p) const {
                return (place) p[0];
            }
            _dist_unique(const _dist_unique& d) : _dist<1>(dynamic_cast<const _region<1>&>(d.region), TYPEID(*this,"x10::lang::_dist_unique")) {}
            const _dist_unique& operator=(const _dist_unique&) { return *this; }
        public:
            explicit _dist_unique(x10_int nplaces) : _dist<1>(*new (x10::alloc<_region<1> >()) _region<1>(0, nplaces-1), TYPEID(*this,"x10::lang::_dist_unique")) { }
            friend class dist;
        };
        class _dist_local : public dist {
            place _pl;
        protected:
            place _translate(x10_int idx) const {
#ifndef IGNORE_EXCEPTIONS
                if (1 != rank)
                    throw x10::ref<RankMismatchException>(new (x10::alloc<RankMismatchException>()) RankMismatchException(rank, 1));
#endif
                return _pl;
            }
            place _translate(const point& p) const {
                return _pl;
            }
            _dist_local(const _dist_local& d) : dist(d.rank, d.region, TYPEID(*this,"x10::lang::_dist_local")), _pl(d._pl) {}
            const _dist_local& operator=(const _dist_local&) { return *this; }
            explicit _dist_local(int rank, const x10::lang::region& region) : dist(rank, region, TYPEID(*this,"x10::lang::_dist_local")), _pl(here()) { }
            friend class region;
        };

        // x10.lang.region.toDistribution()
        inline x10::ref<dist> region::toDistribution() const {
            return new (x10::alloc<_dist_local>()) _dist_local(rank, *this);
        }
    } // x10::lang namespace

    // x10 array allocation
    template<class T> class x10array;
    template<class R> class _array_init;
    template<class T> x10array<T>* x10newArray(const ref<x10::lang::dist>& d, _array_init<T>* init = NULL);

    // x10 arrays
    class __x10array : public x10::lang::Object {
    protected:
        static void check_rank(int actual, int expected) { 
#ifndef IGNORE_EXCEPTIONS
            if (actual != expected)
                throw x10::ref<x10::lang::RankMismatchException>(new (x10::alloc<x10::lang::RankMismatchException>()) x10::lang::RankMismatchException(expected, actual));
#endif
        }
        explicit __x10array(const x10::lang::region& rgn, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::__x10array")), region(rgn) { }
        virtual ~__x10array() { }
    public:
        const x10::lang::region& region;
    };
    template<class T> class x10array : public __x10array {
    public: // [IP] temporary
        T _data[1];
    private:
        x10array(const x10array<T>& arr) : __x10array(arr.region, TYPEID(*this,"x10::array<T>")) { }
    public:
        explicit x10array(const x10::lang::region& rgn) : __x10array(rgn, TYPEID(*this,"x10::x10array<T>")) { }
        ~x10array() { for (int i = 0; i < region.size(); i++) _data[i].~T(); }
        const T& operator[](x10_int index) const;
        T& operator[](x10_int index);
        const T& operator[](const x10::lang::point& p) const;
        T& operator[](const x10::lang::point& p);
        friend x10array<T>* x10newArray<>(const ref<x10::lang::dist>& d, _array_init<T>* init);
        // TODO
        //friend void x10freeArray<>(x10array<T>* arr);
    };

    //////////////////////////////////////////////////////////////
    // x10 array implementation
    //////////////////////////////////////////////////////////////

    template<class T> inline const T& x10array<T>::operator[](const x10::lang::point& p) const {
////        cerr << "x10array->[] const: applying to " << p << endl;
////        cerr << "x10array->[] const: region rank is " << this->region.rank << endl;
////        cerr << "x10array->[] const: point rank is " << p.rank << endl;
        check_rank(p.rank, region.rank);
        // TODO: bounds check
//        int t_idx = this->region.translate(p);
////        cerr << "x10array->[] const: translated index " << t_idx << endl;
//        return this->_data[t_idx];
        return this->_data[this->region.translate(p)];
    }

    template<class T> inline T& x10array<T>::operator[](const x10::lang::point& p) {
////        cerr << "x10array->[]: applying to " << p << endl;
////        cerr << "x10array->[]: region rank is " << this->region.rank << endl;
////        cerr << "x10array->[]: point rank is " << p.rank << endl;
        check_rank(p.rank, region.rank);
        // TODO: bounds check
        return this->_data[this->region.translate(p)];
    }

    template<class T> inline const T& x10array<T>::operator[](x10_int index) const {
////        cerr << "x10array->[] const: applying to " << index << endl;
////        cerr << "x10array->[] const: region rank is " << this->region.rank << endl;
        check_rank(1, region.rank);
        // TODO: bounds check
//        const x10::lang::_point<1>& p = x10::lang::_point<1>(index);
////        cerr << "x10array->[] const: point is (" << p[0] << ")" << endl;
//        int t_idx = this->region.translate(p);
////        cerr << "x10array->[] const: translated index " << t_idx << endl;
//        return this->_data[t_idx];
        return this->_data[this->region.translate(index)];
    }

    template<class T> inline T& x10array<T>::operator[](x10_int index) {
////        cerr << "x10array->[]: applying to " << index << endl;
////        cerr << "x10array->[]: region rank is " << this->region.rank << endl;
        check_rank(1, region.rank);
        // TODO: bounds check
        return this->_data[this->region.translate(index)];
    }

    // x10.lang.Runtime.exitCode
    extern x10_int exitCode;

    // Java array allocation
    template<class T> extern array<T>* alloc_array(x10_int length, T* ptr = NULL);
    template<class T> extern void free_array(array<T>* arr);
    // Java arrays
    class __array : public x10::lang::Object {
    public:
        const x10_int length;
//        __array* operator->() { return this; }
//        const __array* operator->() const { return this; }
    protected:
        // Hack to quell compiler error
        __array(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::__array")), length(0) { }
        virtual ~__array() { }
    };
    // Java arrays
    template<class T> class array : public __array {
    public: // [IP] temporary
        T _data[1];
    private:
        array(const array<T>& arr) : __array(TYPEID(*this,"x10::array<T>")) { }
    public:
        array() : __array(TYPEID(*this,"x10::array<T>")) { }
        ~array() { for (int i = 0; i < length; i++) _data[i].~T(); }
        const T& operator[](x10_int index) const;
        T& operator[](x10_int index);
        friend array<T>* alloc_array<>(x10_int length, T* ptr);
        friend void free_array<>(array<T>* arr);
    };

    //////////////////////////////////////////////////////////////
    // Java array implementation
    //////////////////////////////////////////////////////////////

    template<class T> inline const T& array<T>::operator[](x10_int index) const {
        // TODO: bounds check
        return this->_data[index];
    }

    template<class T> inline T& array<T>::operator[](x10_int index) {
        // TODO: bounds check
        return this->_data[index];
    }

    //////////////////////////////////////////////////////////////
    // Array allocation implementation
    //////////////////////////////////////////////////////////////

    template<class T> array<T>* alloc_array(x10_int length, T* ptr) {
//        cerr << "alloc_array: allocating " << length << " elements" << endl;
        // The code below is the same as
        //   size_t size = sizeof(array<T>);
        // but ensures alignment
        array<T>* obj = NULL;
        size_t size = (size_t) (obj->_data);
        size += length * sizeof(T);
        array<T>* arr = new (x10::alloc<array<T> >(size)) array<T>();
        *(const_cast<x10_int *> (&arr->length)) = length;
//        cerr << "alloc_array: allocated " << arr->length << " elements" << endl;
        return arr;
    }

    template<class T> void free_array(array<T>* arr) {
        dealloc<array<T> >(arr);
    }

    //////////////////////////////////////////////////////////////
    // Object allocation implementation
    //////////////////////////////////////////////////////////////

    template<class T> T* alloc(size_t size, T* ptr) {
        _T_("Allocating " << size << " bytes of type " << TYPENAME(T*));
        T* ret = (T*)::calloc(size, 1);
        _T_("\t-> " << ret);
        return ret;
//        return (T*)::calloc(size, 1);
    }

    template<class T> void dealloc(T* obj) {
        obj->~T(); // FIXME: is this needed?
        _T_("Freeing chunk " << obj << " of type " << TYPENAME(T*));
        ::free((void*) obj);
    }

//	namespace lang {
//		class __System__static {
//		private:
//			__System__static() : out(System::out), err(System::err) { }
//			~__System__static() {
//				if (_instance != NULL)
//					delete _instance;
//			}
//			static __System__static* _instance;
//		public:
//			static const __System__static& instance() {
//				if (_instance == NULL)
//					_instance = new __System__static();
//				return *_instance;
//			}
//            const ref<x10::io::PrintStream> out;
//            const ref<x10::io::PrintStream> err;
//		};
//		extern const __System__static& System;
//	} // x10::lang namespace

    //////////////////////////////////////////////////////////////
    // Finish/async support
    //////////////////////////////////////////////////////////////

    extern int CS;
    void init();
    void shutdown();
#define TERMINATE_COMPUTATION 9999999
    int finish_start(int CS);
    void finish_end(ref<x10::lang::Exception> e);
    struct closure_args {
//        const int ___dummy;
//        closure_args() : ___dummy(0) { };
        closure_args() { };
        inline closure_args* ptr() { return this; }
    };

    //////////////////////////////////////////////////////////////
    // Array initializers
    //////////////////////////////////////////////////////////////

    // x10 array initializers
    template<class R> class _array_init {
    public:
        virtual R operator()(const x10::lang::point& p) const = 0;
        inline _array_init* ptr() { return this; }
    };

    template<class R> class __init_closure : public _array_init<R> {
        __init_closure(const __init_closure&) { }
    public:
//        __init_closure(R(* const _func)(void*,const x10::lang::point&), void* const _args)
        __init_closure(R(* const _func)(void*,x10::ref<x10::lang::point>), void* const _args)
            : func(_func), args(_args) { }
        virtual R operator()(const x10::lang::point& p) const { return func(args, &p); }
    private:
//        R(* const func)(void*,const x10::lang::point&);
        R(* const func)(void*,x10::ref<x10::lang::point>);
        void* const args;
    };

    //////////////////////////////////////////////////////////////
    // x10 array allocation implementation
    //////////////////////////////////////////////////////////////

//    template<class T> inline x10array<T>* x10newArray(const ref<x10::lang::dist>& d, T (*init)(void *, const x10::lang::point&), void* arg) { // }
    template<class T> inline x10array<T>* x10newArray(const ref<x10::lang::dist>& d, _array_init<T>* init) {
        // The code below is the same as
        //   size_t size = sizeof(x10array<T>);
        // but ensures alignment
#ifdef __GNUC__
        x10array<T>* obj = NULL;
        size_t size = (size_t) (obj->_data);
        assert (size == sizeof(x10array<T>) - sizeof(T));
#else
        size_t size = sizeof(x10array<T>) - sizeof(T);
#endif
        size += d->region.size() * sizeof(T);
        x10array<T>* arr = new (x10::alloc<x10array<T> >(size)) x10array<T>(d->region);
//        cerr << "x10newArray: allocated " << d->region.size() << " elements" << endl;
        if (init == NULL)
            return arr;
        x10::lang::Iterator<x10::lang::point>& i = d->region.iterator();
//        cerr << "x10newArray: got iterator" << endl;
        for (; i.hasNext(); ) {
            const x10::lang::point& p = i.next();
//            cerr << "\titerating over point " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
//            T val = init(arg, p);
            T val = (*init)(p);
//            cerr << "\tfinished initializer" << endl;
            x10_int idx = d->region.translate(p);
//            cerr << "\tobtained index: " << idx << endl;
            arr->_data[idx] = val;
//            cerr << "\tassigned value" << endl;
//            arr->_data[d->region.translate(p)] = init(p);
        }
//        delete (&i); // FIXME
        dealloc(&i);
        return arr;
    }

// [IP,PV]: this can be used for anything but field initializers.
// However, field initializers will not have an environment to capture (or, rather,
// will have trivial environment, i.e., static class scope for static fields, and "this" plus
// static class scope for instance fields).  So, use this for all array allocs except
// field inits, and pass an instance of __init_closure to x10newArray for field inits.
#define CONCAT(A,B) A##B
#define MakeName_(PFX,LINE) CONCAT(PFX,LINE)
#define MakeName(PFX) MakeName_(PFX,__LINE__)
////#define array_init(ret_type,body,args) \
////    ({ class MakeName(__array_init__) : public x10::_array_init<ret_type> {\
////       public: ret_type operator()args const body;\
////     } val; &val; })
//    // To allow x10::array_init(...)
//    template<class T> inline T __id__(T val) { return val; }
//// Warning: ret_type is repeated more than once in the macro below
//// [PV] args comes after body because they become available only after body traversal
//#define array_init(ret_type,body,args) \
//  __id__((x10::_array_init<ret_type>*)({ class MakeName(__array_init__) : public x10::_array_init<ret_type> {\
//             public: ret_type operator()args const body;\
//         } val; &val; }))

}; // x10 namespace

static x10::lang::place __here__ = x10::lang::here();

#define CONCAT(A,B) A##B

// [IP] TODO: replace the macros below with templates -- that's exactly what
// templates were designed for, AND they won't have the comma problem
#define async_name(id) CONCAT(async__,id)
#define async_closure(id, UnpackedBody, args) inline void async_name(id)args UnpackedBody

#define init_name(id) CONCAT(__init__,id)

#define array_init_closure_and_args_struct(id, type, UnpackedBody, args, structure) structure; type init_name(id)args UnpackedBody

#define async_unpacked_body(body, unpack) { unpack body }

#define array_init_unpacked_body(ignorable, body, unpack) { unpack body }

#define args_name(closure) CONCAT(closure,_args)

#define async_args_struct(id, typesAndArgs) \
    struct args_name(async_name(id)) : public x10::closure_args { \
        /* TODO: constructor */ \
        typesAndArgs \
    };

#define async_invocation(id, place, args) \
    do { \
        x10_int CONCAT(__PLACE,id) = place; \
        if (CONCAT(__PLACE,id) == __here__) async_name(id)args; \
        else x10lib::asyncSpawnInlineAgg(CONCAT(__PLACE,id), id, args_name(async_name(id))args.ptr(), sizeof(args_name(async_name(id)))); \
    } while(0)

#define array_init_args_struct(id, typesAndArgs) \
    struct args_name(init_name(id)) { \
        /* TODO: constructor */ \
        typesAndArgs \
    };

//#define array_init_closure_invocation(id, distribution, args) \
//    x10::x10newArray(distribution, \
//            init_name(id), \
//            args_name(init_name(id))args.ptr(), \
//            sizeof(args_name(init_name(id))))

#define array_init_closure_invocation(id, distribution, type, args) \
    x10::x10newArray(distribution, \
            x10::__init_closure<type >(init_name(id), \
            args_name(init_name(id))args.ptr()).ptr())

namespace x10 {
    class __init__ {
        static int count;
    public:
        __init__() {
            if (count++ == 0) {
                init();
            }
            __here__ = x10::lang::here();
        }
        ~__init__() {
            if (--count == 0) {
                shutdown();
            }
        }
        static void init() {
            x10::init();
            x10::lang::System::__init__out_err();
            x10::lang::place::__init__MAX_PLACES();
            x10::lang::dist::__init__UNIQUE();
        }
        static void shutdown() {
            x10::shutdown();
        }
    };
    static __init__ __init__counter;
};

#endif

