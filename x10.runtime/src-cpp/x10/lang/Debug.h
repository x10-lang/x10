/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** Debug macro routines. **/

#ifndef __XRX_DEBUG_H
#define __XRX_DEBUG_H

#include <iostream>

#ifdef XRX_DEBUG

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
