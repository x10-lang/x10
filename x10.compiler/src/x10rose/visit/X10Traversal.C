#include <stdio.h>
//#include <stdlib.h>

#include "sage3basic.h"
#include "rose_config.h"

#include "X10Traversal.h"


#include "java_support.h"
#include "jni_token.h"
#include "VisitorContext.h"

//#include <string>

//#define DEBUG



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionTest(JNIEnv *, jclass) 
{
#ifdef DEBUG
	printf("cactionTest\n");
#endif
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionPushPackage(JNIEnv *env, jclass, jstring package_name, jobject jToken) {
    SgName original_package_name = convertJavaStringToCxxString(env, package_name);
    SgName converted_package_name = convertJavaPackageNameToCxxString(env, package_name);
    SgClassDefinition *package = findOrInsertPackage(original_package_name, converted_package_name, env, jToken);
    ROSE_ASSERT(package);
    astJavaScopeStack.push(::globalScope);  // Push the global scope onto the stack.
    astJavaScopeStack.push(package);        // Push the package onto the scopestack.
}


JNIEXPORT void JNICALL Java_x10rose_visit_JNI_cactionImportReference(JNIEnv *env, jclass,
                                                              jboolean java_is_static,
                                                              jstring java_package_name,
                                                              jstring java_type_name,
                                                              jstring java_name_suffix,
                                                              jboolean java_contains_wildcard,
                                                              jobject jToken) {
    // This is the import statement.  The semantics is to include the named file and add its 
    // declarations to the global scope so that they can be referenced by the current file.
    // The import directive tells the compiler where to look for the class definitions 
    // when it comes upon a class that it cannot find in the default java.lang package.

    if (SgProject::get_verbose() > 1)
        printf ("Inside of Java_x10rose_visit_JNI_cactionImportReference() \n");

    bool is_static = java_is_static;

    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name),
           type_name = convertJavaStringToCxxString(env, java_type_name),
           name_suffix = convertJavaStringToCxxString(env, java_name_suffix),
           qualifiedName = convertJavaStringToCxxString(env, java_package_name) +
                           string(package_name.getString().size() && type_name.getString().size() ? "." : "") +
                           type_name.getString() +
                           (name_suffix.getString().size() ? ("." + name_suffix.getString()) : "");

    bool contains_wildcard = java_contains_wildcard;

    // I could not debug passing a Java "Boolean" variable, but "int" works fine.
    // containsWildcard = convertJavaBooleanToCxxBoolean(env, input_containsWildcard);
    // containsWildcard = (bool) (env -> CallBooleanMethod(xxx, input_containsWildcard) == 1);
    // containsWildcard = (bool) input_containsWildcard;
    // containsWildcard = (bool) (env -> CallStaticBooleanMethod(xxx, java_containsWildcard) == 1);
    // containsWildcard = (java_containsWildcard == 1);

    // printf ("import qualifiedName = %s containsWildcard = %s \n", qualifiedName.str(), containsWildcard ? "true" : "false");

    SgJavaImportStatement *importStatement = new SgJavaImportStatement(qualifiedName, contains_wildcard);
    ROSE_ASSERT(importStatement != NULL);
    importStatement -> set_firstNondefiningDeclaration(importStatement);
    importStatement -> set_definingDeclaration(importStatement);
    ROSE_ASSERT(importStatement == importStatement ->  get_firstNondefiningDeclaration());
    ROSE_ASSERT(importStatement == importStatement ->  get_definingDeclaration());
    importStatement -> set_parent(astJavaScopeStack.top()); // We also have to set the parent so that the stack debugging output will work.
    setJavaSourcePosition(importStatement, env, jToken);

    if (is_static) {
        importStatement -> get_declarationModifier().get_storageModifier().setStatic();
    }

    ROSE_ASSERT(! astJavaScopeStack.empty());

    // DQ (7/31/2011): This should be left on the stack instead of being added to the current scope before the end of the scope.
    // printf ("Previously calling appendStatement in cactionImportReference() \n");
    // appendStatement(importStatement);
    astJavaComponentStack.push(importStatement);

    if (type_name.getString().size() > 0) { // we are importing a type?
        SgClassType *class_type =  isSgClassType(lookupTypeByName(package_name, type_name, 0));
        ROSE_ASSERT(class_type);

        // DQ (8/22/2011): The Java generics support is tied to the handling of the import statement so that we can find
        // the parameterized class from the name when it appears without name qualification.
        // The import statement should act like the using namespace directive in C++ to bring in a class or set of classes
        // so that they will be visible in the current scope.  On the Java side the classes have all been read.  Now we
        // just have to build the SgAliasSymbol in the current scope (do this tomorrow morning).
        // printf ("Now build the SgAliasSymbol in the current scope \n");

        // DQ (8/23/2011): This is part of the AST post-processing, but it has to be done as we process the Java import 
        // statements (top down) so that the symbol tables will be correct and variable, function, and type references 
        // will be resolved correctly.

        // This is most likely global scope (where import statements are typically used).
        SgScopeStatement *currentScope = astJavaScopeStack.top();
        ROSE_ASSERT(currentScope != NULL);

        // SgSymbol *importClassSymbol = lookupSymbolInParentScopesUsingQualifiedName(qualifiedName, currentScope);
        SgSymbol *importClassSymbol = class_type -> getAssociatedDeclaration() -> search_for_symbol_from_symbol_table();

        ROSE_ASSERT(importClassSymbol != NULL);

        if (contains_wildcard) {
            // This processing requires that we inject alias symbols from the reference class for all of its data members and member functions. 

            // Note that the enum values for e_default and e_public are equal.
            // SgAccessModifier::access_modifier_enum accessLevel = SgAccessModifier::e_default;
            SgAccessModifier::access_modifier_enum accessLevel = SgAccessModifier::e_public;

            SgScopeStatement *referencedScope = get_scope_from_symbol(importClassSymbol);
            ROSE_ASSERT(referencedScope != NULL);

            FixupAstSymbolTablesToSupportAliasedSymbols::injectSymbolsFromReferencedScopeIntoCurrentScope(referencedScope, currentScope, accessLevel);
        }
        else {
            // Find the referenced class and insert its symbol as an alias into the current scope.

            // printf ("Find the referenced class and insert its symbol as an alias into the current scope. \n");

            SgAliasSymbol *aliasSymbol = new SgAliasSymbol(importClassSymbol);

            // Use the current name and the alias to the symbol
            list<SgName> qualifiedNameList = generateQualifierList(qualifiedName);
            SgName unqualifiedName = *(qualifiedNameList.rbegin());

            // printf ("Building an alias (SgAliasSymbol) for unqualifiedName = %s in qualifiedName = %s \n", unqualifiedName.str(), qualifiedName.str());

            currentScope -> insert_symbol(unqualifiedName, aliasSymbol);
        }
    }

    if (SgProject::get_verbose() > 1)
        printf ("Leaving Java_x10rose_visit_JNI_cactionImportReference() \n");
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionCompilationUnitDeclaration(JNIEnv *env, jclass, jstring java_package_name, jstring java_filename, jobject jToken) {
    if (SgProject::get_verbose() > 0)
         printf ("Inside of Java_JavaParser_cactionCompilationUnitDeclaration() \n");

    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name);
    ROSE_ASSERT(astJavaScopeStack.top() == ::globalScope); // There must be a scope element in the scope stack.
    SgClassSymbol *namespace_symbol = ::globalScope -> lookup_class_symbol(package_name);
//printf("namespace_symbol=%p\n", namespace_symbol);	
    ROSE_ASSERT(namespace_symbol);
    SgClassDeclaration *declaration = (SgClassDeclaration *) namespace_symbol -> get_declaration() -> get_definingDeclaration();
    SgClassDefinition *package = declaration -> get_definition();
    ROSE_ASSERT(package);

    //
    // Tag the package so that the unparser can process its containing user-defined types.
    //
    AstRegExAttribute *attribute =  new AstRegExAttribute(convertJavaStringToCxxString(env, java_package_name));
    package -> setAttribute("translated_package", attribute);
    astJavaScopeStack.push(package); // Push the package onto the scopestack.

    // Example of how to get the string...but we don't really use the absolutePathFilename in this function.
    const char *absolutePathFilename = env -> GetStringUTFChars(java_filename, NULL);
    ROSE_ASSERT(absolutePathFilename != NULL);
    // printf ("Inside of Java_JavaParser_cactionCompilationUnitDeclaration absolutePathFilename = %s \n", absolutePathFilename);
    env -> ReleaseStringUTFChars(java_filename, absolutePathFilename);

    // This is already setup by ROSE as part of basic file initialization before calling ECJ.
    ROSE_ASSERT(OpenFortranParser_globalFilePointer != NULL);

    astJavaComponentStack.push(astJavaScopeStack.top()); // To mark the end of the list of components in this Compilation unit.
printf("cactionCompilationUnitDeclaration\n");
}


JNIEXPORT void JNICALL Java_x10rose_visit_JNI_cactionInsertImportedPackage(JNIEnv *env, jclass, jstring package_name, jobject jToken) {
    SgName original_package_name = convertJavaStringToCxxString(env, package_name);
    SgName converted_package_name = convertJavaPackageNameToCxxString(env, package_name);
    SgClassDefinition *package = findOrInsertPackage(original_package_name, converted_package_name, env, jToken);
    ROSE_ASSERT(package);

    importedPackages.push_back(package);
}



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionInsertImportedType(JNIEnv *env, jclass, jstring java_package_name,  jstring java_type_name, jobject jToken) {
    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name),
           type_name = convertJavaStringToCxxString(env, java_type_name);

    SgClassType *class_type = isSgClassType(lookupTypeByName(package_name, type_name, 0));
    ROSE_ASSERT(class_type);
    importedTypes.push_back(class_type);
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionCompilationUnitList(JNIEnv *env, jclass, jint, jobjectArray) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_x10rose_visit_JNI_cactionCompilationUnitList \n");

    // This is already setup by ROSE as part of basic file initialization before calling ECJ.
    ROSE_ASSERT(OpenFortranParser_globalFilePointer != NULL);
    if (SgProject::get_verbose() > 0)
        printf ("OpenFortranParser_globalFilePointer = %s \n", OpenFortranParser_globalFilePointer -> class_name().c_str());

    // TODO: We need the next line for EDG4 
    // SageBuilder::setSourcePositionClassificationMode(SageBuilder::e_sourcePositionFrontendConstruction);

    SgSourceFile *sourceFile = isSgSourceFile(OpenFortranParser_globalFilePointer);
    ROSE_ASSERT(sourceFile != NULL);

    if (SgProject::get_verbose() > 0)
        printf ("sourceFile -> getFileName() = %s \n", sourceFile -> getFileName().c_str());

    // We don't use the SgProject but since it should have already been built, we can verify that it is present.
    SgProject *project = sourceFile -> get_project();
    ROSE_ASSERT(project != NULL);

    // Get the pointer to the global scope and push it onto the astJavaScopeStack.
    ::globalScope = sourceFile -> get_globalScope();
    ROSE_ASSERT(::globalScope != NULL);

    //
    // At this point, the scope stack should be empty. Push the global scope into it.
    //
    ROSE_ASSERT(astJavaScopeStack.empty());
    astJavaScopeStack.push(::globalScope); // Push the global scope onto the stack.

    // Verify that the parent is set, these AST nodes are already setup by ROSE before calling this function.
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);

     if (SgProject::get_verbose() > 0)
        printf ("Leaving Java_x10rose_visit_JNI_cactionCompilationUnitList \n");
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionSetupObject(JNIEnv *env, jclass) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionSetupObject\n");

    ROSE_ASSERT(astJavaScopeStack.size());
    ::ObjectClassDefinition = isSgClassDefinition(astJavaScopeStack.top());
//    ROSE_ASSERT(::ObjectClassDefinition  && (! ::ObjectClassDefinition -> attributeExists("namespace")));
//    ROSE_ASSERT(::ObjectClassDefinition -> get_qualified_name().getString().compare("java_lang.Object") == 0);

    SgName main_package_name = "java_lang",
           object_name       = "Object";
    ::ObjectClassType = isSgClassType(lookupTypeByName(main_package_name, object_name, 0));
    ROSE_ASSERT(::ObjectClassType);

    if (SgProject::get_verbose() > 0)
        printf ("Leaving Java_JavaParser_cactionSetupObject\n");
}



JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionInsertClassStart(JNIEnv *env, jclass, jstring java_string, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_string);

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionInsertClassStart(): = %s \n", name.str());

    SgScopeStatement *outerScope = astJavaScopeStack.top();
    ROSE_ASSERT(outerScope != NULL);
    SgClassDeclaration *class_declaration = buildDefiningClassDeclaration(name, outerScope);
    setJavaSourcePosition(class_declaration, env, jToken);
    SgClassDefinition *class_definition = class_declaration -> get_definition();
    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));
    setJavaSourcePosition(class_definition, env, jToken);

    astJavaScopeStack.push(class_definition); // to contain the class members...
}



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionTypeDeclaration(JNIEnv *env, jclass,
                                                              jstring java_package_name,
                                                              jstring java_type_name,
                                                              jboolean java_is_annotation_interface,
                                                              jboolean java_is_interface,
                                                              jboolean java_is_enum,
                                                              jboolean java_is_abstract,
                                                              jboolean java_is_final,
                                                              jboolean java_is_private,
                                                              jboolean java_is_public,
                                                              jboolean java_is_protected,
                                                              jboolean java_is_static,
                                                              jboolean java_is_strictfp)
{
//		    astJavaScopeStack.push(::globalScope);

	   // We could provide a constructor for "SgName" that takes a "jstring".  This might help support a simpler interface.
	    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name),
	           type_name = convertJavaStringToCxxString(env, java_type_name);
		
// push package
//	Java_x10rose_visit_JNI_cactionPushPackage(env, NULL, java_package_name, NULL);

	SgClassDeclaration *class_declaration = buildDefiningClassDeclaration(type_name, ::globalScope);
//	cout << "class decl="  << class_declaration << endl;
   ROSE_ASSERT(class_declaration);
	
	SgClassDefinition *definition = class_declaration->get_definition(); 
	ROSE_ASSERT(definition);
//	astJavaScopeStack.push(class_declaration->get_definition()); 
	astJavaScopeStack.push(definition); 
	astJavaComponentStack.push(definition);

	SgSymbol *sym = globalScope->lookup_symbol(type_name);
	cout << "sym=" << sym << endl;

//	insert_symbol(type_name, class_declaration->get_symbol_from_symbol_table());
	cout << "class symbol=" << class_declaration->get_symbol_from_symbol_table() << endl;

	cout << "cactionTypeDecl=" << type_name << ", decl=" << definition << endl;
#if 0
	    SgType *type = lookupTypeByName(package_name, type_name, 0 /* not an array - number of dimensions is 0 */);
		
	    bool is_annotation_interface = java_is_annotation_interface;
	    bool is_interface = java_is_interface;
	    bool is_enum = java_is_enum;

	    bool is_abstract = java_is_abstract;
	    bool is_final = java_is_final;
	    bool is_private = java_is_private;
	    bool is_public = java_is_public;
	    bool is_protected = java_is_protected;
	    bool is_static = java_is_static;
	    bool is_strictfp = java_is_strictfp;

	    if (SgProject::get_verbose() > 0)
	        printf ("Build class type: name = %s \n", type_name.str());

	    ROSE_ASSERT(astJavaScopeStack.top() != NULL);

	    SgClassDeclaration *class_declaration = (SgClassDeclaration *) type -> getAssociatedDeclaration() -> get_definingDeclaration();
	    ROSE_ASSERT(class_declaration);
	    SgClassDefinition *class_definition = class_declaration -> get_definition();
	    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));

	    class_declaration -> setAttribute("user-defined-type", new AstRegExAttribute(type_name));
	    class_declaration -> set_explicit_annotation_interface(is_annotation_interface);      // Identify whether or not this is an annotation interface.
	    class_declaration -> set_explicit_interface(is_annotation_interface || is_interface); // Identify whether or not this is an interface.
	    class_declaration -> set_explicit_enum(is_enum);                                      // Identify whether or not this is an enum.

	    if (is_abstract && (! is_annotation_interface) && (! is_interface) && (! is_enum)) // Enum should not be marked as abstract; Interfaces need not be marked as abstract
	         class_declaration -> get_declarationModifier().setJavaAbstract();
	    else class_declaration -> get_declarationModifier().unsetJavaAbstract();
	    if (is_final && (! is_enum)) // Enum should not be marked as final
	         class_declaration -> get_declarationModifier().setFinal();
	    else class_declaration -> get_declarationModifier().unsetFinal();
	    if (is_strictfp)
	        ; // charles4 - TODO: there is currently no place to hang this information.

	    class_declaration -> get_declarationModifier().get_accessModifier().set_modifier(SgAccessModifier::e_unknown);
	    if (is_private) {
	        class_declaration -> get_declarationModifier().get_accessModifier().setPrivate();
	    }
	    if (is_public) {
	        class_declaration -> get_declarationModifier().get_accessModifier().setPublic();
	    }
	    if (is_protected) {
	        class_declaration -> get_declarationModifier().get_accessModifier().setProtected();
	    }

	    class_declaration -> get_declarationModifier().get_storageModifier().set_modifier(SgStorageModifier::e_unknown);
	    if (is_static && (! is_annotation_interface) && (! is_interface)) { // Interfaces need not be marked as static
	        class_declaration -> get_declarationModifier().get_storageModifier().setStatic();
	    }

	    // TODO: We need the next 4 lines for EDG4
	    // SgClassDeclaration *nondefining_class_declaration = isSgClassDeclaration(class_declaration -> get_firstNondefiningDeclaration());
	    // ROSE_ASSERT(nondefining_class_declaration);
	    // nondefining_class_declaration -> get_declarationModifier().get_accessModifier().set_modifier(class_declaration -> get_declarationModifier().get_accessModifier().get_modifier());
	    // ROSE_ASSERT(nondefining_class_declaration -> get_declarationModifier().get_accessModifier().get_modifier() == class_declaration -> get_declarationModifier().get_accessModifier().get_modifier());
	  
	    astJavaScopeStack.push(class_definition);     // Open new scope for this type.
	    astJavaComponentStack.push(class_definition); // To mark the end of the list of components in this type.

	    if (SgProject::get_verbose() > 0)
	        astJavaScopeStack.top() -> get_file_info() -> display("source position in Java_x10rose_visit_JNI_cactionTypeDeclaration(): debug");

	    if (SgProject::get_verbose() > 0)
	        printf ("Leaving Java_x10rose_visit_JNI_cactionTypeDeclaration() \n");

#endif


//Java_x10rose_visit_JNI_cactionInsertClassStart();

	    printf("TypeDeclaration ends\n");
}

JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionMethodDeclaration(JNIEnv *env, jclass, 
																jstring java_string, 
																jint method_index, 
																jint number_of_arguments, 
 																jobject method_location, 
																jobject args_location,
																jobject jToken) {
	printf("MethodDeclaration start\n");
   SgName name = convertJavaStringToCxxString(env, java_string);
	
	SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
	cout << "***** name=" << name << ", def=" << class_definition << endl;
   ROSE_ASSERT(class_definition != NULL && (! class_definition -> attributeExists("namespace")));
	
    //
    // There is no reason to distinguish between defining and non-defining declarations in Java...
    //
   SgMemberFunctionDeclaration *method_declaration = buildDefiningMemberFunction(name, class_definition, number_of_arguments, env, method_location, args_location);
   setJavaSourcePosition(method_declaration, env, method_location);
   ROSE_ASSERT(method_declaration != NULL);

   SgFunctionDefinition *method_definition = method_declaration -> get_definition();
   ROSE_ASSERT(method_definition);

	astJavaScopeStack.push(method_definition); 
	astJavaComponentStack.push(method_definition);

// MH-20140226
// this is a temporary approach to avoid errors
	astJavaScopeStack.push(class_definition); 
	astJavaComponentStack.push(class_definition);

	printf("MethodDeclaration end\n");
}




JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionConstructorDeclaration(JNIEnv *env, jclass, 
																		jstring java_string, jint constructor_index, jobject jToken) {

#ifdef DEBUG
	printf("ConstructorDeclaration\n");
#endif
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionTypeReference(JNIEnv *env, jclass, 
															jstring java_package_name, 
															jstring java_type_name, 
															jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside cactionTypeReference\n");

    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name),
           type_name = convertJavaStringToCxxString(env, java_type_name);

    SgType *type = lookupTypeByName(package_name, type_name, 0 /* not an array - number of dimensions is 0 */);

// MH-20140226
// If type is null, a new type should be defined
		cout << "PACKAGE_NAME=" << package_name << ", TYPE_NAME=" << type_name << endl;
#if 1
	if (type == NULL) {
		// cactionInsertClassStart() does not have a parameter for specifying a package name, although
		// this is fine for x10 because x10 classes have been fully qualified with package 
		Java_x10rose_visit_JNI_cactionInsertClassStart(env, NULL, java_type_name, jToken);
	}	
#else
	ROSE_ASSERT(type != NULL);
#endif

// TODO: Remove this
//if (isSgClassType(type)) {
//SgClassType *class_type = isSgClassType(type);
//cout << "Came across type " << getTypeName(class_type) << endl;
//cout.flush();
//}

    astJavaComponentStack.push(type);

    if (SgProject::get_verbose() > 0)
        printf ("Exiting cactionTypeReference\n");
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionCatchArgument(JNIEnv *env, jclass, jstring java_argument_name, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build a function argument \n");

    SgName argument_name = convertJavaStringToCxxString(env, java_argument_name);

    SgCatchOptionStmt *catch_option_stmt = SageBuilder::buildCatchOptionStmt();
    ROSE_ASSERT(catch_option_stmt != NULL);
    setJavaSourcePosition(catch_option_stmt, env, jToken);
    catch_option_stmt -> set_parent(astJavaScopeStack.top());
    astJavaScopeStack.push(catch_option_stmt);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionArrayTypeReference(JNIEnv *env, jclass, jint java_num_dimensions, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build a array type \n");

    int num_dimensions = java_num_dimensions;
    ROSE_ASSERT(num_dimensions > 0);
    SgType *base_type = astJavaComponentStack.popType();
    ROSE_ASSERT(base_type);
    SgType *array_type = getUniquePointerType(base_type, num_dimensions);
    ROSE_ASSERT(array_type);

    astJavaComponentStack.push(array_type);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionStringLiteral(JNIEnv *env, jclass, jstring java_string, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build a SgStringVal \n");

    ROSE_ASSERT(! astJavaScopeStack.empty());

    // string stringLiteral = "stringLiteral_abc";
    SgName stringLiteral = convertJavaStringValToWString(env, java_string); // convertJavaStringToCxxString(env, java_string);

    // printf ("Building a string value expression = %s \n", stringLiteral.str());

    SgStringVal *stringValue = SageBuilder::buildStringVal(stringLiteral); // new SgStringVal(stringLiteral); 
    ROSE_ASSERT(stringValue != NULL);

    // Set the source code position (default values for now).
    // setJavaSourcePosition(stringValue);
    setJavaSourcePosition(stringValue, env, jToken);

    astJavaComponentStack.push(stringValue);
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionBreakStatement(JNIEnv *env, jclass, jstring java_string, jobject jToken) {
    SgBreakStmt *stmt = SageBuilder::buildBreakStmt();
    ROSE_ASSERT(stmt != NULL);

    string label_name = convertJavaStringToCxxString(env, java_string);
    if (label_name.length() > 0) {
        assert(lookupLabelByName(label_name) != NULL);
        stmt -> set_do_string_label(label_name);
    }

    setJavaSourcePosition(stmt, env, jToken);
    astJavaComponentStack.push(stmt);
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionCaseStatement(JNIEnv *env, jclass, jboolean hasCaseExpression, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Inside of Java_x10rose_visit_JNI_cactionCaseStatement() \n");

    // 
    // We build on the way down because the scope information and symbol table information is contained
    // in the Ast node.  This AST node is a subclass of SgScopeStatement
    //
    SgStatement *caseStatement = NULL;
    if (hasCaseExpression) {
        caseStatement = SageBuilder::buildCaseOptionStmt(); // the body will be added later
    } else {
        caseStatement = SageBuilder::buildDefaultOptionStmt(); // the body will be added later
    }
    ROSE_ASSERT(caseStatement != NULL);

    setJavaSourcePosition(caseStatement, env, jToken);

    // DQ (7/30/2011): For the build interface to work we have to initialize the parent pointer to the SgForStatement.
    // Charles4 (8/23/2011): When and why parent pointers should be set needs to be clarified. Perhaps the SageBuilder
    // functions should be revisited?
    caseStatement -> set_parent(astJavaScopeStack.top());
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionCharLiteral(JNIEnv *env, jclass, jchar java_char_value, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build a CharVal \n");

    ROSE_ASSERT(! astJavaScopeStack.empty());

    wchar_t value = java_char_value;

    SgWcharVal *charValue = SageBuilder::buildWcharVal(value);
    ROSE_ASSERT(charValue != NULL);

    setJavaSourcePosition(charValue, env, jToken);

    astJavaComponentStack.push(charValue);
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionConditionalExpressionEnd(JNIEnv *env, jclass, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_x10rose_visit_JNI_cactionConditionalExpressionEnd() \n");

    SgExpression *false_exp = astJavaComponentStack.popExpression();

    SgExpression *true_exp = astJavaComponentStack.popExpression();

    SgExpression *test_exp = astJavaComponentStack.popExpression();

    // Build the assignment operator and push it onto the stack.
    SgConditionalExp *conditional = SageBuilder::buildConditionalExp(test_exp, true_exp, false_exp);
    ROSE_ASSERT(conditional != NULL);

    astJavaComponentStack.push(conditional);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionContinueStatement(JNIEnv *env, jclass, jstring java_string, jobject jToken) {
    SgContinueStmt *stmt = SageBuilder::buildContinueStmt();
    ROSE_ASSERT(stmt != NULL);

    string label_name = convertJavaStringToCxxString(env, java_string);
    if (label_name.length() > 0) {
        assert(lookupLabelByName(label_name) != NULL);
        stmt -> set_do_string_label(label_name);
    }

    setJavaSourcePosition(stmt, env, jToken);
    astJavaComponentStack.push(stmt);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionForStatement(JNIEnv *env, jclass, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Inside of Java_x10rose_visit_JNI_cactionForStatement() \n");

    SgNullStatement *assignmentStatement  = SageBuilder::buildNullStatement();
    SgNullStatement *testStatement        = SageBuilder::buildNullStatement();
    SgNullExpression *incrementExpression = SageBuilder::buildNullExpression();
    SgNullStatement *bodyStatement        = SageBuilder::buildNullStatement();

    // The SageBuilder::buildForStatement() function works better if we provide a proper SgForInitStatement
    // Else the original SgForInitStatement built by the SgForStatement constructor will be left dangling...
    // and this causes an error in the AST post processing and testing.
    SgStatementPtrList statements;
    statements.push_back(assignmentStatement);
    SgForInitStatement *forInitStatement = SageBuilder::buildForInitStatement_nfi(statements);
    ROSE_ASSERT(forInitStatement != NULL);

    // We need to set the source code position information
    SageInterface::setOneSourcePositionForTransformation(forInitStatement);
    ROSE_ASSERT(forInitStatement -> get_startOfConstruct() != NULL);
    // printf ("forInitStatement = %p \n", forInitStatement);

    // It might be that we should build this on the way down so that we can have it on the stack 
    // before the body would be pushed onto the scope stack if a block is used.
    // SgForStatement *forStatement = SageBuilder::buildForStatement(assignmentStatement, testStatement, incrementExpression, bodyStatement);
    SgForStatement *forStatement = SageBuilder::buildForStatement(forInitStatement, testStatement, incrementExpression, bodyStatement);
    ROSE_ASSERT(forStatement != NULL);

    ROSE_ASSERT(forInitStatement -> get_startOfConstruct() != NULL);

    // printf ("forStatement -> get_for_init_stmt() = %p \n", forStatement -> get_for_init_stmt());

    // DQ (7/30/2011): For the build interface to wrk we have to initialize the parent pointer to the SgForStatement.
    forStatement -> set_parent(astJavaScopeStack.top());

    astJavaScopeStack.push(forStatement);
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionIfStatement(JNIEnv *env, jclass, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Inside of Java_x10rose_visit_JNI_cactionIfStatement() \n");

    // Build a SgIfStatement and push it onto the stack with a true block.

    // We need a predicate to use to call the SageBuilder::buildIfStmt() function.  So build a SgNullExpression for now. 
    SgNullStatement *temp_conditional = SageBuilder::buildNullStatement();
    SgNullStatement *true_block = SageBuilder::buildNullStatement();
    ROSE_ASSERT(true_block != NULL);

    SgIfStmt *ifStatement = SageBuilder::buildIfStmt(temp_conditional, true_block, NULL);
    ROSE_ASSERT(ifStatement != NULL);

    ifStatement -> set_parent(astJavaScopeStack.top());

    setJavaSourcePosition(ifStatement, env, jToken);

    // Push the SgIfStmt onto the stack, but not the true block.
    astJavaScopeStack.push(ifStatement);
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionIntLiteral(JNIEnv *env, jclass, jint java_value, jstring java_source, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build IntVal \n");

    ROSE_ASSERT(! astJavaScopeStack.empty());

    int value = java_value;
    SgName source = convertJavaStringToCxxString(env, java_source);

    // printf ("Building an integer value expression = %d = %s \n", value, valueString.c_str());

    SgIntVal *integerValue = new SgIntVal(value, source);
    ROSE_ASSERT(integerValue != NULL);

    setJavaSourcePosition(integerValue, env, jToken);

    astJavaComponentStack.push(integerValue);
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionLongLiteral(JNIEnv *env, jclass, jlong java_value, jstring java_source, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build LongVal \n");

    ROSE_ASSERT(! astJavaScopeStack.empty());

    long value = java_value;
    SgName source = convertJavaStringToCxxString(env, java_source);

    // printf ("Building an integer value expression = %d = %s \n", value, valueString.c_str());

    SgLongIntVal *longValue = new SgLongIntVal(value, source);
    ROSE_ASSERT(longValue != NULL);

    setJavaSourcePosition(longValue, env, jToken);

    astJavaComponentStack.push(longValue);
}


JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionSwitchStatement(JNIEnv *env, jclass, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Inside of Java_x10rose_visit_JNI_cactionSwitchStatement() \n");

    // 
    // We build on the way down because the scope information and symbol table information is contained
    // in the Ast node.  This AST node is a subclass of SgScopeStatement
    //
    SgSwitchStatement *switchStatement = SageBuilder::buildSwitchStatement();
    ROSE_ASSERT(switchStatement != NULL);

    setJavaSourcePosition(switchStatement, env, jToken);

    // DQ (7/30/2011): For the build interface to work we have to initialize the parent pointer to the SgForStatement.
    // Charles4 (8/23/2011): When and why parent pointers should be set needs to be clarified. Perhaps the SageBuilder
    // functions should be revisited?
    switchStatement -> set_parent(astJavaScopeStack.top());

    astJavaScopeStack.push(switchStatement);
}



