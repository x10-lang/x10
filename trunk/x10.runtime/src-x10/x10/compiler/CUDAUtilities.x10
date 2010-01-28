package x10.compiler;

/** A collection of independent functions useful in/around CUDA kernels.
 * @author Dave Cunningham
 */
public class CUDAUtilities {

    /** Automatically choose enough blocks to saturate the GPU.  This takes
     * account features of the GPU and kernel in question.  If running on the CPU
     * it returns a fixed number of blocks.  Intended to be used with autoThreads().
     * <p>
     * <code>
     * async (gpu) {
     *     val threads = CUDAUtilities.autoThreads(), blocks = CUDAUtilities.autoBlocks();
     *     for ((block) in 0..blocks-1) {
     *         ...
     *         for ((thread) in 0..threads-1) async { ... }
     *     }
     * }
     * </code>
     * @see autoThreads
     */
    public static def autoBlocks() = 8;

    /** Automatically choose enough threads to saturate the GPU.  
      * @see autoBlocks
      */
    public static def autoThreads() = 1;
}

// vim: shiftwidth=4:tabstop=4:expandtab

