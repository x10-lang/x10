/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <cstring>

#include <x10aux/config.h>
#include <x10aux/string_utils.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/Comparable.h>

namespace x10 {
    namespace array { template<class T> class Array; }
    
    namespace lang {

        class String : public Reference {
            const char *FMGL(content);
            std::size_t FMGL(content_length);

            private:
            static void _formatHelper(std::ostringstream &ss, char* fmt, x10aux::ref<x10::lang::Any> p);
            
            public:
            const char *c_str() const { return FMGL(content); }

            RTT_H_DECLS_CLASS;

            static Fun_0_1<x10_int, x10_char>::itable<String> _itable_Fun_0_1;
            static Comparable<x10aux::ref<String> >::itable<String> _itable_Comparable;
            static x10aux::itable_entry _itables[3];
            virtual x10aux::itable_entry* _getITables() { return _itables; }

            void _constructor(const char *content, bool steal);
            // Set steal to true if you have just allocated the char * with
            // alloc_printf or it's otherwise OK if the String frees it.  Leave
            // steal false for string literals which ought not to be freed.
            // Leave it false for 'static' malloced char* such as the RTT type
            // names that also ought not to be freed.
            static x10aux::ref<String> _make(const char *content, bool steal) {
                x10aux::ref<String> this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(content, steal);
                return this_;
            }

            void _constructor();
            static x10aux::ref<String> _make() {
                x10aux::ref<String> this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor();
                return this_;
            }

            void _constructor(x10aux::ref<String> s);
            static x10aux::ref<String> _make(x10aux::ref<String> s) {
                x10aux::ref<String> this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(s);
                return this_;
            }

            void _constructor(x10aux::ref<x10::array::Array<x10_byte> > array, x10_int start, x10_int length);
            static x10aux::ref<String> _make(x10aux::ref<x10::array::Array<x10_byte> > array, x10_int start, x10_int length) {
                x10aux::ref<String> this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(array, start, length);
                return this_;
            }

            void _constructor(x10aux::ref<x10::array::Array<x10_char> > array, x10_int start, x10_int length);
            static x10aux::ref<String> _make(x10aux::ref<x10::array::Array<x10_char> > array, x10_int start, x10_int length) {
                x10aux::ref<String> this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(array, start, length);
                return this_;
            }


            // This is for string literals, brought out here so we have easier control
            // (Can later make this return a String without allocation)
            static x10aux::ref<String> Lit(const char *s) {
                return _make(s, false);
            }

            // Useful when we have a malloced char* instead of a literal
            static x10aux::ref<String> Steal(const char *s) {
                return _make(s, true);
            }

            x10aux::ref<String> trim();

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

            // Forwarding method needed so that String can be used in Generic contexts (T <: (nat)=>char)
            x10_char __apply(x10_int i) { return charAt(i); }
            
            x10_char charAt(x10_int i);

            x10aux::ref<x10::array::Array<x10_char> > chars();

            x10aux::ref<x10::array::Array<x10_byte> > bytes();

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer& buf);

            static x10aux::ref<Reference> _deserializer(x10aux::deserialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            virtual void _destructor();

            static x10aux::ref<String> format(x10aux::ref<String> format,
                                              x10aux::ref<x10::array::Array<x10aux::ref<Any> > > parms);

            virtual x10_boolean equals(x10aux::ref<x10::lang::Any> p0);

            x10_boolean equalsIgnoreCase(x10aux::ref<x10::lang::String> s);

            x10aux::ref<String> toLowerCase();

            x10aux::ref<String> toUpperCase();

            x10_int compareTo(x10aux::ref<x10::lang::String> s);

            x10_int compareToIgnoreCase(x10aux::ref<x10::lang::String> s);

            x10_boolean startsWith(x10aux::ref<x10::lang::String> s);

            x10_boolean endsWith(x10aux::ref<x10::lang::String> s);

            String () : FMGL(content)(NULL) { }
            virtual ~String () {
                #ifndef X10_USE_BDWGC
                x10aux::dealloc(FMGL(content));
                #endif
            }
        };


        // Adding reference classes (String+String) (String+Reference) (Reference+String)
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

    } // namespace x10::lang

} // namespace x10


#endif


#ifndef X10_LANG_STRING_H_NODEPS
#define X10_LANG_STRING_H_NODEPS
/*
 * Must include header files for any types
 * mentioned in @Native annotations but not
 * present in method return types.
 */
#define X10_LANG_STRINGHELPER_H_NODEPS
#include <x10/lang/StringHelper.h>
#undef X10_LANG_STRINGHELPER_H_NODEPS

#endif

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
