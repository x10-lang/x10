#ifndef __HWI_H
#define __HWI_H

#include <x10lang.h>
using namespace x10::lang;
class HWI {
public:
	virtual x10::ref<String> hw() = 0;
};

#endif
