#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <string>

#include <x10aux/config.h>

#include <x10/lang/Value.h>

namespace x10 {

    namespace lang {

        template<class T> class Rail;

        class String : public Value, public std::string { // value?

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* const it;

                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }

                virtual std::string name() const {
                    return "x10.lang.String";
                }

            };

        protected:
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<String>();
            }

        public:
            String() : Value(), std::string("") { }
            String(const std::string& content) : Value(), std::string(content){}
            String(const char* s) : Value(), std::string(s) { }
            explicit String(const x10aux::ref<String>& s)
              : Value(), std::string(*static_cast<std::string*>(&*s)) { }
/*
            explicit String(const x10::ref<Object>& o) : Value(), string(to_string(o)) { }
            const String& operator=(const String& s) {
                string::operator=(dynamic_cast<const string&>(s));
                return *this;
            }
*/

            operator x10aux::ref<String> () {
                return new (x10aux::alloc<String>()) String(this);
            }
            x10aux::ref<String> toString();

            x10_int hashCode();

            x10_boolean equals(const x10aux::ref<Object> &s);

            x10_int length() { return (x10_int) std::string::length(); }
            String operator+(const String& s);
            x10_int indexOf(const x10aux::ref<String>& s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(const x10aux::ref<String>& s, x10_int i = 0);
            x10_int lastIndexOf(x10_char c, x10_int i = 0);
            String substring(x10_int start, x10_int end);
            String substring(x10_int start) {
                return substring(start, this->length());
            }

            x10_char charAt(x10_int i);

            x10aux::ref<Rail<x10_char> > toCharRail(void);

        };

    } // namespace x10::lang

} // namespace x10

std::ostream &operator << (std::ostream &o, x10aux::ref<x10::lang::String> s);


#endif
