/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2015.
 */

#ifndef X10AUX_CHUNKED_ARRAY_H
#define X10AUX_CHUNKED_ARRAY_H

#include <x10aux/alloc.h>

namespace x10aux {

#define X10AUX_CHUNKED_ARRAY_CHUNKSIZE 64
    
    template<typename T> class chunked_array {
      private:
        T** chunks;
        size_t num_chunks;
        bool sysAlloc;

      public:
        chunked_array(bool sa) : chunks(NULL), num_chunks(0), sysAlloc(sa) { }
        chunked_array(bool sa, size_t init_size) : chunks(NULL), num_chunks(0), sysAlloc(sa) {
            ensure_capacity(init_size);
        }
        ~chunked_array() {
            if (num_chunks > 0 && sysAlloc) {
                for (size_t i=0; i<num_chunks; i++) {
                    if (chunks[i] != NULL) {
                        ::x10aux::system_dealloc(chunks[i]);
                    }
                }
                ::x10aux::system_dealloc(chunks);
            }
            num_chunks = 0;
            chunks = NULL;
        }

        void ensure_capacity(size_t index) {
            size_t new_num_chunks = (index / X10AUX_CHUNKED_ARRAY_CHUNKSIZE)+1;
            if (new_num_chunks < num_chunks) return;

            T** new_chunks;
            if (sysAlloc) {
                new_chunks = ::x10aux::system_realloc<T*>(chunks, (new_num_chunks+1)*sizeof(T*));
                for (size_t i=num_chunks; i<new_num_chunks; i++) {
                    new_chunks[i] = x10aux::system_alloc_z<T>(X10AUX_CHUNKED_ARRAY_CHUNKSIZE*sizeof(T));
                }
            } else {
                new_chunks = ::x10aux::realloc<T*>(chunks, (new_num_chunks+1)*sizeof(T*));
                for (size_t i=num_chunks; i<new_num_chunks; i++) {
                    new_chunks[i] = x10aux::alloc_z<T>(X10AUX_CHUNKED_ARRAY_CHUNKSIZE*sizeof(T));
                }
            }
            chunks = new_chunks;
            num_chunks = new_num_chunks;
        }

        T &operator[] (size_t index) {
            size_t chunk_num = index / X10AUX_CHUNKED_ARRAY_CHUNKSIZE;
            size_t chunk_idx = index % X10AUX_CHUNKED_ARRAY_CHUNKSIZE;
            assert(chunk_num < num_chunks);
            return chunks[chunk_num][chunk_idx];
        }

        const T &operator[] (size_t index) const {
            size_t chunk_num = index / X10AUX_CHUNKED_ARRAY_CHUNKSIZE;
            size_t chunk_idx = index % X10AUX_CHUNKED_ARRAY_CHUNKSIZE;
            assert(chunk_num < num_chunks);
            return chunks[chunk_num][chunk_idx];
        }
        
    };

}

#endif // X10AUX_CHUNKED_ARRAY_H
