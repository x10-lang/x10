This is a Java binding for the STP decision procedure.
The binding was done on 4 Jan 2008 by akiezun@mit.edu and is relased under the MIT licence.

You can use either the STPJNI class, which provides a raw, C-like access to STP, or you can use the STPObject hierarchy, which gives more 
object-oriented access. The latter is almost always preferable. 

The code was developed and run under 32-bit Linux. To use on a different OS/architecture, you'll need to do some work.
In particular, I think 64-bit may be a problem because the port uses Java int type (32-bit, signed) to store pointers.
To build the C++ code, use the compile.sh script. You will need to tweak the directories for your installation.

To run the examples (and use the binding in general), you need to set LD_LIBRARY_PATH to the lib folder (or use the java.library.path property).

The binding is almost finished. The only remaining APIs are: 
(left undone because they require some C hacking and I did not need them immediately - please send me patches)

   void vc_printExprToBuffer(VC vc, Expr e, char **buf, unsigned long * len);
   void vc_printQueryStateToBuffer(VC vc, Expr e, char **buf, unsigned long *len, int simplify_print);
   void vc_printCounterExampleToBuffer(VC vc, char **buf,unsigned long *len);
   unsigned long long int getBVUnsignedLongLong(Expr e);
   unsigned int getBVUnsigned(Expr e);
   Expr vc_bvConstExprFromLL(VC vc, int n_bits, unsigned long long value);
   void vc_registerErrorHandler(void (*error_hdlr)(const char* err_msg));  

Please send enhancements, fixes and problem reports to me or Vijay Ganesh, the author or STP. 