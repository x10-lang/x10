/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2013.
 */

#ifndef X10AUX_SIMPLE_HASHMAP_H
#define X10AUX_SIMPLE_HASHMAP_H

#include <x10aux/alloc.h>

#define NUM_BUCKETS 100

namespace x10aux {
    // a specific hashCode method is needed for each concrete instantiation
    x10_uint simple_hash_code(x10_long id);
    x10_uint simple_hash_code(const void* id);

    template<typename Key, typename T>class simple_hashmap {
    public:
        class Bucket {
        public:
            Key _id;
            T _data;
            Bucket *_next;
        };

        Bucket **_buckets;

        simple_hashmap() {
            _buckets = ::x10aux::alloc_z<Bucket*>(NUM_BUCKETS*sizeof(Bucket*));
        }

        ~simple_hashmap() {
            Bucket *cur = _buckets[0];
            while (cur != NULL) {
                Bucket *old = cur;
                cur = cur->_next;
                ::x10aux::dealloc(old);
            }
            ::x10aux::dealloc(_buckets);
        }

        T get(Key id) {
            int bucket = simple_hash_code(id) % NUM_BUCKETS;
            Bucket *cur = _buckets[bucket];
            while (cur != NULL) {
                if (cur->_id == id) {
                    return cur->_data;
                }
                cur = cur->_next;
            }
            // didn't find it, which means we return NULL (match Java Map semantics)
            return NULL;
        }

        T put(Key id, const T data) {
            int bucket = simple_hash_code(id) % NUM_BUCKETS;
            // First, search to see if we are replacing an existing key
            Bucket *cur = _buckets[bucket];
            while (cur != NULL) {
                if (cur->_id == id) {
                    // Replace
                    T oldData = cur->_data;
                    cur->_data = data;
                    return oldData;
                }
                cur = cur->_next;
            }
            // Didn't find it.  Insert at head of bucket chain
            cur = alloc<Bucket>();
            cur->_id = id;
            cur->_data = data;
            cur->_next = _buckets[bucket];
            _buckets[bucket] = cur;
            return NULL;
        }

        T remove(Key id) {
            int bucket = simple_hash_code(id) % NUM_BUCKETS;
            Bucket **trailer = &(_buckets[bucket]);
            Bucket *cur = _buckets[bucket];
            while (cur != NULL) {
                if (cur->_id == id) {
                    // cut cur out of bucket chain by setting trailer to cur->next;
                    *trailer = cur->_next;
                    T data = cur->_data;
                    ::x10aux::dealloc(cur);
                    return data;
                }
                trailer = &(cur->_next);
                cur = cur->_next;
            }
            // Wasn't here in the first place. Since we optimize storing NULL
            // by not storing anything, this isn't an error.
            return NULL;
        }

        template<class U> friend const char *::x10aux::typeName();
    };
}

#endif

