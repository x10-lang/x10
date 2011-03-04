Compile like this:
mpCC -qinline -qhot -O5 -q64 -qarch=auto -qtune=auto KMeansNativeFusedFullyUnrolled.c++ -o KMeansNativeFusedFullyUnrolled -qlist                              

Summary of native versions performance on P7:


georges_code.c: not possible to compile (other files not committed), unfused, performs the same as KMeansNativeFusedFullyUnrolled
KMeansNative: unfused version, performs the same as KMeansNativeFused
KMeansNativeFused: fuse both kernels into a single loop, seems to be a good idea everywhere except the GPU
KMeansNativeFusedFullyUnrolled: stripmine outer loop 8 times to maximise register use, minimise bandwidth requirements
KMeansNativeFusedPartiallyUnrolled: stripmine outer loop and do not penetrate into innermost loops, performance is the same as KMeansNativeFused and KMeansNative

fusing the two kernels (classify and reaverage) does not affect performance
