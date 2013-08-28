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

#include <x10/lang/X10Class.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/Comparable.h>

namespace x10 {
    namespace array { template<class T> class Array; }
    
    namespace lang {

        class String : public X10Class {
            const char *FMGL(content);
            std::size_t FMGL(content_length);

            private:
            static void _formatHelper(std::ostringstream &ss, char* fmt, x10::lang::Any* p);
            
            public:
            const char *c_str() const { return FMGL(content); }
            const char *c_str__tm__(x10tm::TMThread *SelfTM) const { return FMGL(content); }

            RTT_H_DECLS_CLASS;

            static Comparable<String*>::itable<String> _itable_Comparable;
            static x10aux::itable_entry _itables[2];
            virtual x10aux::itable_entry* _getITables() { return _itables; }

            void _constructor(const char *content, bool steal);
            // Set steal to true if you have just allocated the char * with
            // alloc_printf or it's otherwise OK if the String frees it.  Leave
            // steal false for string literals which ought not to be freed.
            // Leave it false for 'static' malloced char* such as the RTT type
            // names that also ought not to be freed.
            static String* _make(const char *content, bool steal) {
                String* this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(content, steal);
                return this_;
            }

            void _constructor();
            static String* _make() {
                String* this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor();
                return this_;
            }

            void _constructor(String* s);
            static String* _make(String* s) {
                String* this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(s);
                return this_;
            }

            static String* _make(x10::array::Array<x10_byte>* array);

            void _constructor(x10::array::Array<x10_byte>* array, x10_int start, x10_int length);
            static String* _make(x10::array::Array<x10_byte>* array, x10_int start, x10_int length) {
                String* this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(array, start, length);
                return this_;
            }

            static String* _make(x10::array::Array<x10_char>* array);
            
            void _constructor(x10::array::Array<x10_char>* array, x10_int start, x10_int length);
            static String* _make(x10::array::Array<x10_char>* array, x10_int start, x10_int length) {
                String* this_ = new (x10aux::alloc<String>()) String();
                this_->_constructor(array, start, length);
                return this_;
            }


			// Set steal to true if you have just allocated the char * with
			// alloc_printf or it's otherwise OK if the String frees it.  Leave
			// steal false for string literals which ought not to be freed.
			// Leave it false for 'static' malloced char* such as the RTT type
			// names that also ought not to be freed.
			static String* _make__tm__(x10tm::TMThread *SelfTM, const char *content, bool steal) {
				String* this_ = new (x10aux::alloc<String>()) String();
				this_->_constructor(content, steal);
				return this_;
			}


			static String* _make__tm__(x10tm::TMThread *SelfTM) {
				String* this_ = new (x10aux::alloc<String>()) String();
				this_->_constructor();
				return this_;
			}


			static String* _make__tm__(x10tm::TMThread *SelfTM, String* s) {
				String* this_ = new (x10aux::alloc<String>()) String();
				this_->_constructor(s);
				return this_;
			}

			static String* _make__tm__(x10tm::TMThread *SelfTM, x10::array::Array<x10_byte>* array);


			static String* _make__tm__(x10tm::TMThread *SelfTM, x10::array::Array<x10_byte>* array, x10_int start, x10_int length) {
				String* this_ = new (x10aux::alloc<String>()) String();
				this_->_constructor(array, start, length);
				return this_;
			}

			static String* _make__tm__(x10tm::TMThread *SelfTM, x10::array::Array<x10_char>* array);


			static String* _make__tm__(x10tm::TMThread *SelfTM, x10::array::Array<x10_char>* array, x10_int start, x10_int length) {
				String* this_ = new (x10aux::alloc<String>()) String();
				this_->_constructor(array, start, length);
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

            static String* Lit__tm__(x10tm::TMThread *SelfTM, const char *s) {
				return _make(s, false);
			}

			// Useful when we have a malloced char* instead of a literal
			static String* Steal__tm__(x10tm::TMThread *SelfTM, const char *s) {
				return _make(s, true);
			}

            String* trim();
            String* trim__tm__(x10tm::TMThread *SelfTM) {
            	return trim();
            }

            String* toString() { return this; }

            x10_int hashCode();

            x10_int length() { return (x10_int) FMGL(content_length); }
            x10_int indexOf(String* s, x10_int i = 0);
            x10_int indexOf(x10_char c, x10_int i = 0);
            x10_int lastIndexOf(String* s, x10_int i);

            x10_int length__tm__(x10tm::TMThread *SelfTM) { return (x10_int) FMGL(content_length); }
			x10_int indexOf__tm__(x10tm::TMThread *SelfTM, String* s, x10_int i = 0) {
				return indexOf(s, i);
			}
			x10_int indexOf__tm__(x10tm::TMThread *SelfTM, x10_char c, x10_int i = 0) {
				return indexOf(c, i);
			}

			x10_int lastIndexOf__tm__(x10tm::TMThread *SelfTM, String* s, x10_int i) {
				return lastIndexOf(s, i);
			}

            x10_int lastIndexOf(String* s) {
                return lastIndexOf(s, this->length()-1);
            }

            x10_int lastIndexOf(x10_char c, x10_int i);

            x10_int lastIndexOf(x10_char c) {
                return lastIndexOf(c, this->length()-1);
            }

            String* substring(x10_int start, x10_int end);

            String* substring(x10_int start) {
                return substring(start, this->length());
            }

            x10_int lastIndexOf__tm__(x10tm::TMThread *SelfTM, String* s) {
				return lastIndexOf(s, this->length()-1);
			}

			x10_int lastIndexOf__tm__(x10tm::TMThread *SelfTM, x10_char c, x10_int i);

			x10_int lastIndexOf__tm__(x10tm::TMThread *SelfTM, x10_char c) {
				return lastIndexOf(c, this->length()-1);
			}

			String* substring__tm__(x10tm::TMThread *SelfTM, x10_int start, x10_int end) {
				return substring(start, end);
			}

			String* substring__tm__(x10tm::TMThread *SelfTM, x10_int start) {
				return substring(start, this->length());
			}


            // Forwarding method needed so that String can be used in Generic contexts (T <: (nat)=>char)
            x10_char __apply(x10_int i) { return charAt(i); }
            
            x10_char charAt(x10_int i);

            x10::array::Array<x10_char>* chars();

            x10::array::Array<x10_byte>* bytes();

            x10_char __apply__tm__(x10tm::TMThread *SelfTM, x10_int i) { return charAt(i); }

			x10_char charAt__tm__(x10tm::TMThread *SelfTM, x10_int i) {
				return charAt(i);
			}

			x10::array::Array<x10_char>* chars__tm__(x10tm::TMThread *SelfTM) {
				return chars();
			}

			x10::array::Array<x10_byte>* bytes__tm__(x10tm::TMThread *SelfTM) {
				return bytes();
			}

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer& buf);

            static Reference* _deserializer(x10aux::deserialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);

            virtual void _destructor();

            static String* format(String* format, x10::array::Array<Any*>* parms);
            static String* format__tm__(x10tm::TMThread *SelfTM, String* format_p, x10::array::Array<Any*>* parms) {
            	return format(format_p, parms);
            }

            virtual x10_boolean equals(x10::lang::Any* p0);

            x10_boolean equalsIgnoreCase(x10::lang::String* s);

            virtual x10_boolean equals__tm__(x10tm::TMThread *SelfTM, x10::lang::Any* p0) {
            	return equals(p0);
            }

            x10_boolean equalsIgnoreCase__tm__(x10tm::TMThread *SelfTM, x10::lang::String* s) {
            	return equalsIgnoreCase(s);
            }


            String* toLowerCase();

            String* toUpperCase();

            x10_int compareTo(x10::lang::String* s);
            x10_int compareTo__tm__(x10tm::TMThread *SelfTM, x10::lang::String* s);

            x10_int compareToIgnoreCase(x10::lang::String* s);

            x10_boolean startsWith(x10::lang::String* s);

            x10_boolean endsWith(x10::lang::String* s);

            String () : FMGL(content)(NULL) { }
            virtual ~String () {
                #ifndef X10_USE_BDWGC
                x10aux::dealloc(FMGL(content));
                #endif
            }

            template<class T1, class T2> static String* __plus(T1, T2);
            template<class T1, class T2> static String* __plus__tm__(x10tm::TMThread *SelfTM, T1, T2);
        };

        template<class T1, class T2>
        String* x10::lang::String::__plus(T1 p1, T2 p2) {
            return String::Steal(x10aux::alloc_printf("%s%s",
                                                      x10aux::safe_to_string(p1)->c_str(),
                                                      x10aux::safe_to_string(p2)->c_str()));
        }

        template<class T1, class T2>
		String* x10::lang::String::__plus__tm__(x10tm::TMThread *SelfTM, T1 p1, T2 p2) {
			return String::Steal(x10aux::alloc_printf("%s%s",
													  x10aux::safe_to_string(p1)->c_str(),
													  x10aux::safe_to_string(p2)->c_str()));
		}
            
        #ifndef NO_IOSTREAM
        inline std::ostream &operator<<(std::ostream &o, String *v) {
            return o << v->c_str();
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
