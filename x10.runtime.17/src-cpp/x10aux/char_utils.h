#ifndef X10AUX_CHAR_UTILS_H
#define X10AUX_CHAR_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}


namespace x10aux {
	class char_utils {
	public:
		static const ref<x10::lang::String> toString(x10_char value, x10_int radix);
		static const ref<x10::lang::String> toHexString(x10_char value);
		static const ref<x10::lang::String> toOctalString(x10_char value);
		static const ref<x10::lang::String> toBinaryString(x10_char value);
		static const ref<x10::lang::String> toString(x10_char value);
		static x10_char parseChar(const ref<x10::lang::String>& s, x10_int radix);
		static x10_char parseChar(const ref<x10::lang::String>& s);
		static x10_char reverseBytes(x10_char value);
	};
}

#endif
