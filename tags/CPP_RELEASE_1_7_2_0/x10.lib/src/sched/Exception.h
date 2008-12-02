/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Exception.h,v 1.3 2007-12-14 13:39:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_EXCEPTION_H
#define __X10_XWS_EXCEPTION_H

#include <string>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

class Exception {
protected:
	virtual std::string toString() { return std::string();}
	virtual ~Exception() {}
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_EXCEPTION_H */
