/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10_LANG_STRING_H
#define X10_LANG_STRING_H

#include <cstring>

#include <x10aux/config.h>

#include <x10/lang/X10Class.h>
#include <x10/lang/CharSequence.h>
#include <x10/lang/Comparable.h>

namespace x10 {

    namespace util {
    	template<class T> class GrowableRail;
    }        

    namespace lang {
    	template<class T> class Rail;
    
        class String : public X10Class {
            const char *FMGL(content);
            std::size_t FMGL(content_length);

            public:
            const char *c_str() const { return FMGL(content); }

            RTT_H_DECLS_CLASS;

            static Comparable<String*>::itable<String> _itable_Comparable;
            static CharSequence::itable<String> _itable_CharSequence;
            static ::x10aux::itable_entry _itables[3];
            virtual ::x10aux::itable_entry* _getITables() { return _itables; }

            void _constructor(const char *content, bool steal);
            // Set steal to true if you have just allocated the char * with
            // alloc_printf or it's otherwise OK if the String frees it.  Leave
            // steal false for string literals which ought not to be freed.
            // Leave it false for 'static' malloced char* such as the RTT type
            // names that also ought not to be freed.
            static String* _make(const char *content, bool steal) {
                String* this_ = new (::x10aux::alloc<String>()) String();
                this_->_constructor(content, steal);
                return this_;
            }

            void _constructor();
            static String* _make() {
                String* this_ = new (::x10aux::alloc<String>()) String();
                this_->_constructor();
                return this_;
            }

            void _constructor(String* s);
            static String* _make(String* s) {
                String* this_ = new (::x10aux::alloc<String>()) String();
                this_->_constructor(s);
                return this_;
            }

            static String* _make(::x10::lang::Rail<x10_byte>* rail);

            static String* _make(::x10::util::GrowableRail<x10_byte>* grail);
            
            void _constructor(::x10::lang::Rail<x10_byte>* rail, x10_int start, x10_int length);
            static String* _make(::x10::lang::Rail<x10_byte>* rail, x10_int start, x10_int length) {
                String* this_ = new (::x10aux::alloc<String>()) String();
                this_->_constructor(rail, start, length);
                return this_;
            }

            static String* _make(::x10::lang::Rail<x10_char>* rail);
            
            void _constructor(::x10::lang::Rail<x10_char>* rail, x10_int start, x10_int length);
            static String* _make(::x10::lang::Rail<x10_char>* rail, x10_int start, x10_int length) {
                String* this_ = new (::x10aux::alloc<String>()) String();
                this_->_constructor(rail, start, length);
                return this_;
            }


            // This is for string literals, brought out here so we have easier control
            // (Can later make this return a String without allocation)
            static String* Lit(const char *s) {
                return _make(s, false);
            }

            // Useful when we have a malloced char* instead of a literal
            static String* Steal(const char *s) {
                return _make(s, true);
            }

            String* trim();

            String* toString() { return this; }

            x10_int hashCode();

            x10_int length() { return (x10_int) FMGL(content_length); }
            x10_int indexOf(String* s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(String* s, x10_int i);

            x10_int lastIndexOf(String* s) {
                return lastIndexOf(s, this->length()-1);
            }

            x10_int lastIndexOf(x10_char c, x10_int i);

            x10_int lastIndexOf(x10_char c) {
                return lastIndexOf(c, this->length()-1);
            }

            CharSequence* subSequence(x10_int start, x10_int end) {
                return reinterpret_cast<CharSequence*>(substring(start, end));
            }

            String* substring(x10_int start, x10_int end);

            String* substring(x10_int start) {
                return substring(start, this->length());
            }

            // Forwarding method needed so that String can be used in Generic contexts (T <: (nat)=>char)
            x10_char __apply(x10_int i) { return charAt(i); }
            
            x10_char charAt(x10_int i);

            ::x10::lang::Rail<x10_char>* chars();

            ::x10::lang::Rail<x10_byte>* bytes();

            static const ::x10aux::serialization_id_t _serialization_id;

            virtual ::x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(::x10aux::serialization_buffer& buf);

            static Reference* _deserializer(::x10aux::deserialization_buffer &buf);

            void _deserialize_body(::x10aux::deserialization_buffer &buf);

            virtual void _destructor();

            static String* format(String* format, ::x10::lang::Rail<Any*>* parms);

            virtual x10_boolean equals(::x10::lang::Any* p0);

            x10_boolean equalsIgnoreCase(::x10::lang::String* s);

            String* toLowerCase();

            String* toUpperCase();

            x10_int compareTo(::x10::lang::String* s);

            x10_int compareToIgnoreCase(::x10::lang::String* s);

            x10_boolean startsWith(::x10::lang::String* s);

            x10_boolean endsWith(::x10::lang::String* s);

            String () : FMGL(content)(NULL) { }

            // For use in compiler generated code for static string literals.
            // Invariant: Caller must ensure lifetime of content
            //   is at least as long as that of the String instance since
            //   we are NOT copying the content into a new object.
            String(const char* s) : FMGL(content)(s),
                FMGL(content_length)(strlen(s)) { }
            
            template<class T> static String* __plus(String*, T);
            template<class T> static String* __plus(T, String*);
            static String* __plus(String* p1, String* p2);
            static String* __plus(String* p1, x10_boolean p2);
            static String* __plus(String* p1, x10_byte p2);
            static String* __plus(String* p1, x10_ubyte p2);
            static String* __plus(String* p1, x10_short p2);
            static String* __plus(String* p1, x10_ushort p2);
            static String* __plus(String* p1, x10_int p2);
            static String* __plus(String* p1, x10_uint p2);
            static String* __plus(String* p1, x10_long p2);
            static String* __plus(String* p1, x10_ulong p2);
            static String* __plus(x10_boolean p1, String* p2);
            static String* __plus(x10_byte p1, String* p2);
            static String* __plus(x10_ubyte p1, String* p2);
            static String* __plus(x10_short p1, String* p2);
            static String* __plus(x10_ushort p1, String* p2);
            static String* __plus(x10_int p1, String* p2);
            static String* __plus(x10_uint p1, String* p2);
            static String* __plus(x10_long p1, String* p2);
            static String* __plus(x10_ulong p1, String* p2);
        };

        template<class T> String* x10::lang::String::__plus(T p1, String* p2) {
            return String::__plus(::x10aux::safe_to_string(p1), p2);
        }
        template<class T> String* x10::lang::String::__plus(String* p1, T p2) {
            return String::__plus(p1, ::x10aux::safe_to_string(p2));
        }

        #ifndef NO_IOSTREAM
        inline std::ostream &operator<<(std::ostream &o, String *v) {
            return o << v->c_str();
        }
        #endif

    } // namespace ::x10::lang

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
