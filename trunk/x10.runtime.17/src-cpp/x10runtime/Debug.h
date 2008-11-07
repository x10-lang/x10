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

namespace xrx_runtime {

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
	const int do_nothing = 0; \
} while(0)

#define __xrxDPrStart() \
do { \
	const int do_nothing = 0; \
} while(0)

#define __xrxDPrEnd() \
do { \
	const int do_nothing = 0; \
} while(0)

#define __xrxDPrMesg(message) \
do { \
	const int do_nothing = 0; \
} while(0)

#endif /* XRX_DEBUG */

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_DEBUG_H */
