
// DQ (10/30/2010): Added support for reflection to get methods in implicitly included objects.
// import java.lang.reflect.*;

package x10rose.visit;

// DQ (10/12/2010): Make more like the OFP implementation (using Callable<Boolean> abstract base class). 
// class JavaTraversal {
import java.util.concurrent.Callable;
class JavaParser  implements Callable<Boolean> {
    // DQ (11/17/2010): This is the main class for the connection of the ECJ front-end to ROSE.
    // The design is that we use ECJ mostly unmodified, and use the visitor
    // traversal (ASTVisitor) of the ECJ AST.  Specifically we derive a class 
    // (ecjASTVisitory) from the abstract class (ASTVisitor in ECJ) and define
    // all of it's members so that we can support traversing the ECJ defined 
    // AST and construct the ROSE AST.  Most of the IR nodes used to support this
    // or borrowed from the existing C and C++ support in ROSE.  We will however
    // add any required IR nodes as needed.

    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    public static native void cactionTest();
/*    
    public static native void cactionJavadoc(JavaToken jToken);
    public static native void cactionJavadocClassScope(JavaToken jToken);
    public static native void cactionJavadocAllocationExpression(JavaToken jToken);
    public static native void cactionJavadocAllocationExpressionClassScope(JavaToken jToken);
    public static native void cactionJavadocArgumentExpression(JavaToken jToken);
    public static native void cactionJavadocArgumentExpressionClassScope(JavaToken jToken);
    public static native void cactionJavadocArrayQualifiedTypeReference(JavaToken jToken);
    public static native void cactionJavadocArrayQualifiedTypeReferenceClassScope(JavaToken jToken);
    public static native void cactionJavadocArraySingleTypeReference(JavaToken jToken);
    public static native void cactionJavadocArraySingleTypeReferenceClassScope(JavaToken jToken);
    public static native void cactionJavadocFieldReference(JavaToken jToken);
    public static native void cactionJavadocFieldReferenceClassScope(JavaToken jToken);
    public static native void cactionJavadocImplicitTypeReference(JavaToken jToken);
    public static native void cactionJavadocImplicitTypeReferenceClassScope(JavaToken jToken);
    public static native void cactionJavadocMessageSend(JavaToken jToken);
    public static native void cactionJavadocMessageSendClassScope(JavaToken jToken);
    public static native void cactionJavadocQualifiedTypeReference(JavaToken jToken);
    public static native void cactionJavadocQualifiedTypeReferenceClassScope(JavaToken jToken);

    public static native void cactionJavadocReturnStatement(JavaToken jToken);
    public static native void cactionJavadocReturnStatementClassScope(JavaToken jToken);
    public static native void cactionJavadocSingleNameReference(JavaToken jToken);
    public static native void cactionJavadocSingleNameReferenceClassScope(JavaToken jToken);
    public static native void cactionJavadocSingleTypeReference(JavaToken jToken);
    public static native void cactionJavadocSingleTypeReferenceClassScope(JavaToken jToken);
*/     
    // Support for primative types.
    public static native void generateBooleanType();
    public static native void generateByteType();
    public static native void generateCharType();
    public static native void generateIntType();
    public static native void generateShortType();
    public static native void generateFloatType();
    public static native void generateLongType();
    public static native void generateDoubleType();
    public static native void generateNullType();


    // Save the compilationResult as we process the CompilationUnitDeclaration class.
    // public CompilationResult rose_compilationResult;

    // DQ (10/12/2010): Added boolean value to report error to C++ calling program (similar to OFP).
    // public static boolean hasErrorOccurred = false;

    // DQ: This is the name of the C++ *.so file which has the implementations of the JNI functions.
    // The library with the C++ implementation of these function must be loaded in order to call the 
    // JNI functions.
    static { 

			System.loadLibrary("JavaTraversal"); 

			cactionTest();
		}

    // DQ (10/12/2010): Implemented abstract baseclass "call()" member function (similar to OFP).
    // This provides the support to detect errors and communicate them back to ROSE (C++ code).
    public Boolean call() throws Exception {
        // boolean error = false;
        // boolean error = true;

        // if (JavaParserSupport.verboseLevel > 0)
        //    System.out.println("Parser exiting normally");

        // return new Boolean(error);
        // return Boolean.valueOf(error);
        return Boolean.TRUE;
    } // end call()
}
