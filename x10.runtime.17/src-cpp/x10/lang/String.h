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
                static const RTT* const it;

                RTT() : RuntimeType()
                { }

                virtual std::string name() const {
                    return "x10.lang.String";
                }

            };

/*
            static std::string to_string(const x10_boolean b);
            static std::string to_string(const x10_char c);
            static std::string to_string(const x10_int i);
            static std::string to_string(const x10_long i);
            static std::string to_string(const x10_double i);
            //static std::string to_string(const place& p);
            static std::string to_string(const x10aux::ref<Rail<x10_char> >& value);
            static std::string to_string(const x10aux::ref<Rail<x10_char> >& value, x10_int offset, x10_int count);
            static const std::string to_string(const x10aux::ref<String>& s);
//            static const string to_string(const x10aux::ref<Object>& o);
//            */
        protected:
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<String>();
            }

        public:
            String() : Value(), std::string("") { }
            String(const std::string& content) : Value(), std::string(content){}
            String(const char* s) : Value(), std::string(s) { }
/*
            String(const x10_boolean b) : Object(), string(to_string(b)) { }
            String(const x10_int i) : Value(), string(to_string(i)) { }
            String(const x10_char c) : Value(), string(to_string(c)) { }
            String(const x10_short s) : Value(), string(to_string((x10_int)s)) { }
            String(const x10_long i) : Value(), string(to_string(i)) { }
            String(const x10_float f) : Value(), string(to_string((x10_double)f)) { }
            String(const x10_double i) : Value(), string(to_string(i)) { }
            //String(const place& p) : Value(), string(to_string(p)) { }
            String(const x10aux::ref<Rail<x10_char> >& value) : Value(), string(to_string(value)) { }
            String(const x10aux::ref<Rail<x10_char> >& value, x10_int offset, x10_int count) : Value(), string(to_string(value, offset, count)) { }
            String(const String& s) : Value(), string(dynamic_cast<const string&>(s)) { }
*/
            explicit String(const x10aux::ref<String>& s)
              : Value(), std::string(*static_cast<std::string*>(&*s)) { }
/*
            //explicit String(const x10::ref<Object>& o) : Value(), string(to_string(o)) { }
            const String& operator=(const String& s) {
                string::operator=(dynamic_cast<const string&>(s));
                return *this;
            }
*/
            x10aux::ref<String> toString() const;

            x10_int hashCode() const;

            x10_boolean equals(const x10aux::ref<Object> &s) const;

            x10_int length() const { return (x10_int) std::string::length(); }
            //String operator+(const String& s) const;
            x10_int indexOf(const x10aux::ref<String>& s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(const x10aux::ref<String>& s, x10_int i = 0);
            x10_int lastIndexOf(x10_char c, x10_int i = 0);
            //String substring(x10_int start, x10_int end) const;
            //String substring(x10_int start) const { return substring(start, this->length()); }
            //x10_char charAt(x10_int i) const;
            //x10aux::ref<Rail<x10_char> > toCharArray() const;

            //friend Rail<x10aux::ref<String> >* x10::convert_args(int ac, char **av);
            //friend void x10::free_args(Rail<x10aux::ref<String> > *arr);
        };

    } // namespace x10::lang

} // namespace x10


#endif
