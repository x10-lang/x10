#ifndef EXCEPTION_H_
#define EXCEPTION_H_

#include <string>

namespace x10lib_cws {

class Exception {
protected:
	virtual std::string toString() {}
	virtual ~Exception() {}
};

};

#endif /*EXCEPTION_H_*/
