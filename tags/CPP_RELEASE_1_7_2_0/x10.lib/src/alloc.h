/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: alloc.h,v 1.14 2008-06-04 12:15:08 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ALLOC_H
#define __X10_ALLOC_H

#include <x10/types.h>
#include <x10/register.h>
#include <x10/xassert.h>

namespace x10lib {
	extern int __x10_num_places;

	class Allocator {
		private:
			char *pointer_;
			void **addr_table_;
			uint64_t offset_;
			uint64_t prev_offset_;
			size_t size_;
		public:
		
			Allocator(size_t size) :
					offset_(0), prev_offset_(0), size_(size)
			{
				pointer_ = new char[size];
				assert(pointer_ != 0);
				addr_table_ = new void*[__x10_num_places];
				assert(addr_table_ != 0);
				AddressInit(pointer_, addr_table_);
			}

			~Allocator()
			{
				delete [] pointer_;
				delete [] addr_table_;
			}

			char *addr() const
			{
				return pointer_;
			}

			char *chunk(size_t size)
			{
				char *addr = pointer_ + offset_;
				prev_offset_ = offset_;
				offset_ += size;
				return addr;
			}

			void *addr_table(int i)
			{
				return addr_table_[i];
			}

			const uint64_t offset() const
			{
				return offset_;
			}

			const uint64_t prev_offset() const
			{
				return prev_offset_;
			}
	};

} /* closing brace for namespace x10lib */

#endif /* __X10_ALLOC_H */
