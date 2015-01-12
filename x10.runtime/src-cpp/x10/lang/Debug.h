/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

/** Debug macro routines. **/

#ifndef __XRX_DEBUG_H
#define __XRX_DEBUG_H

#ifdef XRX_DEBUG
#include <iostream>

#define __xrxDPr() \
do { \
		std::cerr << __func__ << ": <<...called...>>" << std::endl; \
} while(0)

#define __xrxDPrStart() \
do { \
		std::cerr << __func__ << ": <<...call begin" << std::endl; \
} while(0)

#define __xrxDPrEnd() \
do { \
		std::cerr << __func__ << ": call end...>>" << std::endl; \
} while(0)

#define __xrxDPrMesg(message) \
do { \
		std::cerr << __func__ << ": <<" << message << ">>" << std::endl; \
} while(0)

#else /* !XRX_DEBUG */

#define __xrxDPr() \
do { \
	/* do nothing */ \
} while(0)

#define __xrxDPrStart() \
do { \
	/* do nothing */ \
} while(0)

#define __xrxDPrEnd() \
do { \
	/* do nothing */ \
} while(0)

#define __xrxDPrMesg(message) \
do { \
	/* do nothing */ \
} while(0)

#endif /* XRX_DEBUG */

#endif /* __XRX_DEBUG_H */
// vim:tabstop=4:shiftwidth=4:expandtab
