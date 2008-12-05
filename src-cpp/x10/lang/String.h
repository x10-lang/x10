#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <string>

#include <x10aux/config.h>

#include <x10/lang/Value.h>

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
            static x10aux::ref<String> _make(const std::string& content = std::string()) {
                return (new (x10aux::alloc<String>()) String())->_constructor(content);
            }
            x10aux::ref<String> _constructor(const std::string& content = std::string()) {
                std::string::operator=(content);
                return this;
            }
            static x10aux::ref<String> _make(const x10aux::ref<String>& s) {
                return (new (x10aux::alloc<String>()) String())->_constructor(*s);
            }

            // This is for string literals, brought out here so we have easier control
            // (Can later make this return a String without allocation)
            static x10aux::ref<String> Lit(const char *s) { return Lit(std::string(s)); }
            static x10aux::ref<String> Lit(const std::string& s)
            { return _make(s); }

            operator x10aux::ref<Value> () {
                return x10aux::ref<String>(this);
            }

            operator x10aux::ref<String> () {
                return _make(*this);
            }

            x10aux::ref<String> toString();

            x10_int hashCode();

            x10_boolean equals(x10aux::ref<Object> s);

            x10_int length() { return (x10_int) std::string::length(); }
            x10_int indexOf(x10aux::ref<String> s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(x10aux::ref<String> s, x10_int i = 0);
            x10_int lastIndexOf(x10_char c, x10_int i = 0);
            x10aux::ref<String> substring(x10_int start, x10_int end);

            x10aux::ref<String> substring(x10_int start) {
                return substring(start, this->length());
            }

            x10_char charAt(x10_int i);

            x10aux::ref<ValRail<x10_char> > chars();

            x10aux::ref<ValRail<x10_byte> > bytes();

            static void _serialize(x10aux::ref<String> this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m)
            {
                this_->_serialize_body(buf, m);
            }

            template<class T> static x10aux::ref<T> _deserialize(x10aux::serialization_buffer &buf){
                x10_int sz = buf.read<x10_int>();
                x10aux::ref<String> this_ = _make(std::string(sz,'x'));
                for (x10_int i=0 ; i<sz ; ++i) {
                    (*this_)[i] = (char)buf.read<x10_char>();
                }
                // there are no fields
                _S_("Deserialized string was: \""<<this_<<"\"");
                return this_;
            }

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer& buf, x10aux::addr_map &m) {
                buf.write(_serialization_id, m);
            }

            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map &m) {
                // only support strings that are shorter than 4billion chars
                x10_int sz = size();
                buf.write(sz,m);
                for (x10_int i=0 ; i<sz ; ++i) {
                    buf.write((x10_char)at(i),m);
                }
            }

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
                //if (s1 == x10aux::null) s1 = _make("null");
                //if (s2 == x10aux::null) s2 = _make("null");
                return String::_make(*s1+*s2);
            }
        };
        // String+Object
        template<class T> struct OpPlus<String,T> {
            static x10aux::ref<String> _(x10aux::ref<String> s, x10aux::ref<T> o) {
                //strings can't be null!
                //if (s == x10aux::null) s = _make("null");
                if (o == x10aux::null) return String::_make(*s+std::string("null"));
                return String::_make(*s+*o->toString());
            }
        };
        //Object+String
        template<class T> struct OpPlus<T,String> {
            static x10aux::ref<String> _(x10aux::ref<T> o, x10aux::ref<String> s) {
                if (o == x10aux::null) return String::_make(std::string("null")+*s);
                //strings can't be null!
                //if (s == x10aux::null) s = _make("null"); 
                return String::_make(*o->toString()+*s);
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


    } // namespace x10::lang

} // namespace x10


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
