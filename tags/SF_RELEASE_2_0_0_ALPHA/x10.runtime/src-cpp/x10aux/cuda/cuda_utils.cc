#ifdef X10_USE_CUDA_HOST

#include <x10aux/cuda/cuda_utils.h>

using namespace x10aux;

cudaStream_t x10aux::cuda_base_stream;
cudaStream_t x10aux::cuda_read_stream;

#endif

