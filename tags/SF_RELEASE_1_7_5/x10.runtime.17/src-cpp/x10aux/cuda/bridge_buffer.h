#ifdef X10_USE_CUDA_HOST

#ifndef X10AUX_CUDA_BRIDGE_BUFFER_H
#define X10AUX_CUDA_BRIDGE_BUFFER_H

#include <x10aux/config.h>

namespace x10aux {

    template<class T> class BridgeBuffer {

        protected:

        T *host;
        T *device;
        std::size_t cnt;


        public:

        BridgeBuffer (std::size_t count_, bool zero=false)
          : cnt(count_)
        {
            std::size_t sz = count_*sizeof(T);
            CU_ASSERT(cudaMallocHost((void**)&host, sz));
            if (zero) memset(host, 0, sz);

            CU_ASSERT(cudaMalloc((void**)&device, sz));
            if (zero) push(0);
        }

        std::size_t size (void) const { return cnt; }

        T &operator* (void) const { return *host; }
        T *operator[] (std::size_t index) { return &host[index]; }
        const T *operator[] (std::size_t index) const  { return &host[index]; }
        // sometimes operator[] does not work in cuda
        T &lookup (std::size_t index) { return host[index]; }
        const T &lookup (std::size_t index) const { return host[index]; }

        T *getDevicePtr (void) const { return device; }


        void push (cudaStream_t stream) { push(stream, 0, cnt); }

        void push (cudaStream_t stream, std::size_t just)
        { push(stream, just, just+1); }

        void push (cudaStream_t stream, std::size_t from, std::size_t to)
        {
            CU_ASSERT(cudaMemcpyAsync(device+from, host+from, (to-from)*sizeof(T),
                                      cudaMemcpyHostToDevice, stream));
            CU_ASSERT(cudaStreamSynchronize(stream));
        }


        void pull (cudaStream_t stream) { pull(stream, 0, cnt); }

        void pull (cudaStream_t stream, std::size_t just)
        { pull(stream, just, just+1); }

        void pull (cudaStream_t stream, std::size_t from, std::size_t to)
        {
            CU_ASSERT(cudaMemcpyAsync(host+from, device+from, (to-from)*sizeof(T),
                                      cudaMemcpyDeviceToHost, stream));
            CU_ASSERT(cudaStreamSynchronize(stream));
        }
    };

}

#endif

#endif

// vim: shiftwidth=4:tabstop=4:expandtab:textwidth=100
