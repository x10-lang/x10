#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <string>

#include <x10aux/config.h>

#include <x10/lang/Value.h>
//#include <x10aux/string_utils.h>
#include <x10aux/RTT.h>
#include <x10aux/ref.h>

namespace x10 {

    namespace lang {

        template<class T> class Rail;
        template<class T> class ValRail;

        class String : public Value, public std::string {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* const it;
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual std::string name() const { return "x10.lang.String"; }
            };

            virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<String>(); }

            // These are our 2 workhorses - the first one is for literals
            explicit String(const std::string& content = std::string()) : std::string(content) { }
            explicit String(const x10aux::ref<String>& s) : std::string(*s) { }

            // This is for string literals, brought out here so we have easier control
            // (Can later make this return a String without allocation)
            static x10aux::ref<String> Lit(const char *s) { return X10NEW(String)(std::string(s)); }

/*
            String(x10_boolean v);
            String(x10_byte v);
            String(x10_char v);
            String(x10_short v);
            String(x10_int v);
            String(x10_long v);
            String(x10_float v);
            String(x10_double v);
*/

            operator x10aux::ref<Value> () {
                return x10aux::ref<String>(this);
            }

            operator x10aux::ref<String> () {
                return new (x10aux::alloc<String>()) String(static_cast<std::string&>(*this));
            }

            x10aux::ref<String> toString();

            x10_int hashCode();

            x10_boolean equals(x10aux::ref<Object> s);

            x10_int length() { return (x10_int) std::string::length(); }
            x10_int indexOf(x10aux::ref<String> s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(x10aux::ref<String> s, x10_int i = 0);
            x10_int lastIndexOf(x10_char c, x10_int i = 0);
            String substring(x10_int start, x10_int end);

            String substring(x10_int start) {
                return substring(start, this->length());
            }

            x10_char charAt(x10_int i);

            x10aux::ref<Rail<x10_char> > chars();

            x10aux::ref<Rail<x10_byte> > bytes();

            virtual void _serialize(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
                (void)buf; (void)m; abort();
                //x10aux::_serialize_ref(this, buf, m);
            }
            virtual void _serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            virtual void _deserialize_fields(x10aux::serialization_buffer& buf);

            static x10aux::ref<String> format(x10aux::ref<String> format,
                                              x10aux::ref<ValRail<x10aux::ref<Object> > > parms);

            static x10aux::ref<String> format(x10aux::ref<String> format,
                                              x10aux::ref<Rail<x10aux::ref<Object> > > parms);

        };


        // Catch all default case that gives an error that we can identify while debugging
        // If you get this case then you are adding things other than the categories below
        template<class T1, class T2> struct OpPlus {
            static x10aux::ref<String> _(x10aux::ref<T1> p1, x10aux::ref<T2> p2) {
                return stringOpNotImplementedForTheseTwoTypes(p1,p2);
            }
        };


        // String + String
        template<> struct OpPlus<String,String> {
            static x10aux::ref<String> _(x10aux::ref<String> s1, x10aux::ref<String> s2) {
                //strings can't be null!
                //if (s1 == x10aux::null) s1 = X10NEW(String)("null");
                //if (s2 == x10aux::null) s2 = X10NEW(String)("null");
                return X10NEW(String)(*s1+*s2);
            }
        };
        // String+Object
        template<class T> struct OpPlus<String,T> {
            static x10aux::ref<String> _(x10aux::ref<String> s, x10aux::ref<T> o) {
                //strings can't be null!
                //if (s == x10aux::null) s = X10NEW(String)("null");
                if (o == x10aux::null) o = X10NEW(String)("null");
                return X10NEW(String)(*s+*o->toString());
            }
        };
        //Object+String
        template<class T> struct OpPlus<T,String> {
            static x10aux::ref<String> _(x10aux::ref<T> o, x10aux::ref<String> s) {
                if (o == x10aux::null) o = X10NEW(String)("null"); 
                //strings can't be null!
                //if (s == x10aux::null) s = X10NEW(String)("null"); 
                return X10NEW(String)(*o->toString()+*s);
            }
        };


        // Adding reference classes (String+String) (String+Object) (Object+String)
        template<class T1, class T2>
        x10aux::ref<String> operator+(x10aux::ref<T1> p1, x10aux::ref<T2> p2) {
            return OpPlus<T1,T2>::_(p1,p2);
        }
            
        // Postfixing primitives
        x10aux::ref<String> operator+(x10_boolean v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_byte v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_char v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_short v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_int v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_long v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_float v, x10aux::ref<String> s);
        x10aux::ref<String> operator+(x10_double v, x10aux::ref<String> s);

        // Prefixing primitives
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_boolean v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_byte v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_char v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_short v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_int v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_long v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_float v);
        x10aux::ref<String> operator+(x10aux::ref<String> s, x10_double v);

        template<class T>
        x10aux::ref<String> operator+=(x10aux::ref<String> &s1, T v) {
            return s1 = s1 + v;
        }


/*
        template<typename T> String operator+(T v, const String& s);
        template<typename T> String operator+(x10aux::ref<T> v, const String& s);
        template<typename T> String operator+(x10aux::ref<String> v, T v);
        template<> String operator+(x10aux::ref<String> v, String s);

        template<typename T> String operator+(T v, const String& s) {
            return String(v) + s;
        }

        template<typename T> String operator+(x10aux::ref<T> v, const String& s) {
            return *(v->toString()) + s;
        }

        template<typename T> String operator+(x10aux::ref<String> s, T v) {
            return *s + v;
        }

        template<> String operator+(x10aux::ref<String> s, String v) {
            return *s + v;
        }
*/


    } // namespace x10::lang

} // namespace x10


// these are optimisations to avoid malloc / gc / leaks
/*
x10::lang::String operator+(const x10::lang::String &s1, const x10::lang::String& s2);
x10::lang::String operator+(const x10::lang::String &s1, x10aux::ref<x10::lang::Object> s2);
x10::lang::String operator+(x10aux::ref<x10::lang::Object> s1, const x10::lang::String& s2);
*/

/*
// rewrite the ref so it points to a new string, return the new string

// rewrite the first argument so it points to a new string, return the new string
x10aux::ref<x10::lang::String> operator+=(x10aux::ref<x10::lang::String> &s1,
                                          x10aux::ref<x10::lang::Object> s2);

*/


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
