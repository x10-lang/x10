#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <string>

#include <x10aux/config.h>

#include <x10/lang/Value.h>
#include <x10aux/string_utils.h>
#include <x10aux/RTT.h>

namespace x10 {

    namespace lang {

        template<class T> class Rail;

        class String : public Value, public std::string {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* const it;
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual std::string name() const { return "x10.lang.String"; }
            };

            virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<String>(); }

            String() : std::string("") { }

            String(const std::string& content) : std::string(content) { }

            String(const char *s) : std::string(s) { }

            explicit String(const x10aux::ref<String>& s)
              : std::string(static_cast<std::string&>(*s)) { }

            String(x10_boolean v);
            String(x10_byte v);
            String(x10_char v);
            String(x10_short v);
            String(x10_int v);
            String(x10_long v);
            String(x10_float v);
            String(x10_double v);

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

        };

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

    } // namespace x10::lang

} // namespace x10

x10::lang::String operator+(const x10::lang::String &s1, const x10::lang::String& s2);
x10::lang::String operator+(const x10::lang::String &s1, x10aux::ref<x10::lang::String> s2);
x10::lang::String operator+(x10aux::ref<x10::lang::String> s1, const x10::lang::String& s2);
x10::lang::String operator+(x10aux::ref<x10::lang::String> s1, x10aux::ref<x10::lang::String> s2);

x10::lang::String operator+=(const x10::lang::String &s1, const x10::lang::String& s2);
x10::lang::String operator+=(const x10::lang::String &s1, x10aux::ref<x10::lang::String> s2);
x10::lang::String operator+=(x10aux::ref<x10::lang::String> s1, const x10::lang::String& s2);
x10::lang::String operator+=(x10aux::ref<x10::lang::String> s1, x10aux::ref<x10::lang::String> s2);

std::ostream &operator << (std::ostream &o, x10aux::ref<x10::lang::String> s);


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
