/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Executable.h,v 1.2 2007-12-14 13:39:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_EXECUTABLE_H
#define __X10_XWS_EXECUTABLE_H

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

  class Worker;
  
  class Executable {
  public:
    virtual Executable *execute(Worker *ws) = 0;
  };

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_EXECUTABLE_H */
