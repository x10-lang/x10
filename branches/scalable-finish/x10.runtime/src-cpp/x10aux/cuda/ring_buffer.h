#ifdef X10_USE_CUDA_HOST

#ifndef X10AUX_CUDA_RING_BUFFER_H
#define X10AUX_CUDA_RING_BUFFER_H

#include <x10aux/config.h>
#include <x10aux/cuda/cuda_utils.h>
#include <x10aux/cuda/bridge_buffer.h>

namespace x10aux {

    class RingBufferI {

        public:

        char * buf;

        // used to wrap at end of buffer, assumes sz is a power of 2
        int sz_mask;

        int * push_index;
        int * pop_index;
        int * overrun_counter;


        void init (char *buf_, int sz, int *indexes)
        {
            buf = buf_;
            sz_mask = sz-1;
            push_index = indexes;
            pop_index = indexes+1;
            overrun_counter = indexes+2;
        }

        #ifdef X10_USE_CUDA_DEVICE
        inline __device__ void write (char *msg)
        {
            int push_pos = *push_index;
            int pop_pos = *pop_index;
            for (char *ptr=msg ; *ptr!='\0' ; ++ptr) {
                buf[push_pos] = *ptr;
                push_pos++;
                push_pos &= sz_mask;
                if (push_pos+1 == pop_pos) {
                    (*overrun_counter)++;
                    return;
                }
            }
            *push_index = push_pos;
        }
        #endif

    };

    #ifdef X10_USE_CUDA_DEVICE
    __device__ void write_external (RingBufferI *rbi, char *msg)
    {
        int push_pos = *rbi->push_index;
        int pop_pos = *rbi->pop_index;
        for (char *ptr=msg ; *ptr!='\0' ; ++ptr) {
            rbi->buf[push_pos++] = *ptr;
            push_pos &= rbi->sz_mask;
            if (push_pos+1 == pop_pos) {
                (*rbi->overrun_counter)++;
                return;
            }
        }
        *rbi->push_index = push_pos;
    }
    #endif


    /*
    push_index is incremented by the device, chased by host increments of pop_index.

    ---------------------------------
    |X|X|X|X|s|o|m|e| |m|s|g|X|X|X|X|
    ---------------------------------
             ^indexes[1]     ^indexes[0]
              (pop_index)     (push_index)

    indexes[2] (overrun_counter) is incremented by the device when data is lost due
    to insufficient host popping.

    */
    class RingBuffer {
        protected:
        BridgeBuffer<int> indexes;
        BridgeBuffer<char> buf;
        int *push_index, *pop_index, *overrun_counter;

        public:
        RingBuffer()
          : indexes(3,true), buf(16*1024)
        {
            push_index = &indexes.lookup(0);
            pop_index = &indexes.lookup(1);
            overrun_counter = &indexes.lookup(2);
        }

        void poll (void)
        {
            cudaStream_t stream = x10aux::cuda_read_stream;
            // on the fast path, issue 1 dma
            indexes.pull(stream);
            if (*push_index != *pop_index) {
                // just pull the relevant part of the buffer
                buf.pull(stream, *pop_index, *push_index);
                // force terminate the message
                // (this local modification will not be propagated)
                buf.lookup(*push_index) = '\0';
                fprintf(stderr,"output: %s",&buf.lookup(*pop_index));
                // set pop index == the push_index
                *pop_index = *push_index;
                // propagate change back to gpu
                indexes.push(stream,1);
            }
            if (*overrun_counter) {
                fprintf(stderr,"At least %d device messages were lost.\n",
                               *overrun_counter);
                *overrun_counter = 0;
                indexes.push(stream,2);
            }
        }

        // hack to avoid cuda copy constructor bug
        void forDevice (RingBufferI &rbi) const
        {
            rbi.init(buf.getDevicePtr(), buf.size(), indexes.getDevicePtr());
        }

    };

}

#endif // X10AUX_CUDA_H

#endif // X10_USE_CUDA_HOST

// vim: shiftwidth=4:tabstop=4:textwidth=100:expandtab
