/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 *
 */

#ifndef __debug_helper_h__
#define __debug_helper_h__

#define X10_DEBUGGER_ID "X10_DEBUGGER_ID"
#define X10_DEBUGGER_NAME "X10_DEBUGGER_NAME"

class DebugHelper
{
	public:
		static void attachDebugger(void);
	private:
		static int getArg0(pid_t, char*, int);
		static int getNextPid(pid_t&, pid_t&);
		static int waitForStartSignal(unsigned);
};

#endif
