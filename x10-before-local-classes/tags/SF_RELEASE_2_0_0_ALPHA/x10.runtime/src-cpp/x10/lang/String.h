#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <cstring>

#include <x10aux/config.h>
#include <x10aux/string_utils.h>

#include <x10/lang/Ref.h>

#ifdef __CYGWIN__
extern "C" char *strdup (const char *);
#endif
namespace x10 {

    namespace lang {

        template<class T> class Rail;
        template<class T> class ValRail;

        class String : public Ref {
            const char *FMGL(content);
            std::size_t FMGL(content_length);

            public:
            const char *c_str() const { return FMGL(content); }

            RTT_H_DECLS_CLASS;
            
            // Set steal to true if you have just allocated the char * with
            // alloc_printf or it's otherwise OK if the String frees it.  Leave
            // steal false for string literals which ought not to be freed.
            // Leave it false for 'static' malloced char* such as the RTT type
            // names that also ought not to be freed.
            static x10aux::ref<String> _make(const char *content, bool steal = false);
            x10aux::ref<String> _constructor(const char *content, std::size_t content_length) {
                this->Ref::_constructor();
                this->FMGL(content) = content;
                this->FMGL(content_length) = content_length;
                return this;
            }
            static x10aux::ref<String> _make(x10aux::ref<String> s);

            // This is for string literals, brought out here so we have easier control
            // (Can later make this return a String without allocation)
            static x10aux::ref<String> Lit(const char *s) {
                return _make(s);
            }

            // Useful when we have a malloced char* instead of a literal
            static x10aux::ref<String> Steal(const char *s) {
                return _make(s, true);
            }

            /*
            operator x10aux::ref<Ref> () {
                return x10aux::ref<String>(this);
            }

            operator x10aux::ref<String> () {
                return _make(*this);
            }
            */

            x10aux::ref<String> toString() { return this; }

            x10_int hashCode();

            x10_int length() { return (x10_int) FMGL(content_length); }
            x10_int indexOf(x10aux::ref<String> s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(x10aux::ref<String> s, x10_int i);

            x10_int lastIndexOf(x10aux::ref<String> s) {
                return lastIndexOf(s, this->length()-1);
            }

            x10_int lastIndexOf(x10_char c, x10_int i);

            x10_int lastIndexOf(x10_char c) {
                return lastIndexOf(c, this->length()-1);
            }

            x10aux::ref<String> substring(x10_int start, x10_int end);

            x10aux::ref<String> substring(x10_int start) {
                return substring(start, this->length());
            }

            x10aux::ref<ValRail<x10aux::ref<String> > > split(x10aux::ref<String> pat);

            x10_char charAt(x10_int i);

            x10aux::ref<ValRail<x10_char> > chars();

            x10aux::ref<ValRail<x10_byte> > bytes();

            static void _serialize(x10aux::ref<String> this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m);

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map &m);

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);

            virtual void _destructor();

            static x10aux::ref<String> format(x10aux::ref<String> format,
                                              x10aux::ref<ValRail<x10aux::ref<Object> > > parms);

            static x10aux::ref<String> format(x10aux::ref<String> format,
                                              x10aux::ref<Rail<x10aux::ref<Object> > > parms);

            virtual x10_boolean equals(x10aux::ref<x10::lang::Object> p0);

            String () : FMGL(content)(NULL) { }
            virtual ~String () {
                x10aux::dealloc(FMGL(content));
            }
        };


        // Adding reference classes (String+String) (String+Object) (Object+String)
        template<class T1, class T2>
        x10aux::ref<String> operator+(T1 p1, T2 p2) {
            return String::Steal(x10aux::alloc_printf("%s%s",
                                                      x10aux::safe_to_string(p1)->c_str(),
                                                      x10aux::safe_to_string(p2)->c_str()));
        }
            
        template<class T>
        x10aux::ref<String> operator+=(x10aux::ref<String> &s1, T v) {
            return s1 = s1 + v;
        }

        #ifndef NO_IOSTREAM
        inline std::ostream &operator<<(std::ostream &o, String &v) {
            return o << v.c_str();
        }
        #endif

        template<class T> x10aux::ref<T> String::_deserializer(x10aux::deserialization_buffer& buf) {
            x10aux::ref<String> this_ = new (x10aux::alloc_remote<String>()) String();
            this_->_deserialize_body(buf);
            return this_;
        }

        // Specialized deserialization
        template<class T> x10aux::ref<T> String::_deserialize(x10aux::deserialization_buffer &buf) {
            Ref::_reference_state rr = Ref::_deserialize_reference_state(buf);
            x10aux::ref<String> this_;
            if (rr.ref != 0) {
                this_ = String::_deserializer<String>(buf);
            }
            return Ref::_finalize_reference<T>(this_, rr);
        }

    } // namespace x10::lang

} // namespace x10


#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
