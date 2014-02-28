/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10rose.visit;

/*
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import java.io.*;
*/

public class JNI {
	
	public static native void cactionTest();

   public static native void cactionInsertImportedPackage(String package_name);
   public static native void cactionInsertImportedType(String package_name, String type_name);
   public static native void cactionPushPackage(String package_name);
   public static native void cactionPopPackage();
   public static native void cactionSetupObject();
   public static native void cactionInsertClassStart(String java_string);  

	public static native void cactionTypeDeclaration(String package_name, String type_name, boolean is_annotation_interface, boolean is_interface, boolean is_enum, boolean is_abstract, boolean is_final, boolean is_private, boolean is_public, boolean is_protected, boolean is_static, boolean is_strictfp);

	public static native void cactionMethodDeclaration(String name, int method_index, int number_of_arguments, JavaToken method_location, JavaToken args_location);

	public static native void cactionConstructorDeclaration(String filename, int constructor_index);
	
	public static native void cactionCompilationUnitList(int argc, String[] argv);
	
	public static native void cactionCompilationUnitDeclaration(String packageName, String path);
	
	public static native void cactionTypeReference(String packageName, String name);

//	JNIEXPORT void JNICALL Java_JavaParser_cactionCatchArgument(JNIEnv *, jclass, jstring, jobject);
//
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionArrayTypeReference(JNIEnv *, jclass, jint, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionStringLiteral(JNIEnv *, jclass, jstring, jobject); 
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionBreakStatement(JNIEnv *, jclass, jstring, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionCaseStatement(JNIEnv *, jclass, jboolean, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionCharLiteral(JNIEnv *, jclass, jchar, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionConditionalExpressionEnd(JNIEnv *, jclass, jobject); 
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionContinueStatement(JNIEnv *, jclass, jstring, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionForStatement(JNIEnv *, jclass, jobject); 
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionIfStatement(JNIEnv *, jclass, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionIntLiteral(JNIEnv *, jclass, jint, jstring, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionLongLiteral(JNIEnv *, jclass, jlong, jstring, jobject);
//
//	JNIEXPORT void JNICALL Java_JavaParser_cactionSwitchStatement(JNIEnv *, jclass, jobject);
	
	
    static {
        // Requires a ROSE installation, but only if x10rose.ExtensionInfo is classloaded
//        System.loadLibrary("astRewriteExample1");
			try {
//        		System.loadLibrary("Test");
        System.loadLibrary("X10Traversal");
//        System.loadLibrary("JavaTraversal");
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error er) {
				er.printStackTrace();
			}
    }
}
