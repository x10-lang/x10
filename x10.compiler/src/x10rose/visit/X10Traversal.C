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
Java_x10rose_visit_JNI_cactionThisReference(JNIEnv *env, jclass, jobject jToken) {
    SgClassDefinition *class_definition = getCurrentTypeDefinition();
    ROSE_ASSERT(! class_definition -> attributeExists("namespace"));

    string className = class_definition -> get_declaration() -> get_name();
    // printf ("Current class for ThisReference is: %s \n", className.c_str());

    SgClassSymbol *class_symbol = isSgClassSymbol(class_definition -> get_declaration() -> search_for_symbol_from_symbol_table());
    ROSE_ASSERT(class_symbol != NULL);

    SgThisExp *thisExp = SageBuilder::buildThisExp(class_symbol);
    ROSE_ASSERT(thisExp != NULL);

    astJavaComponentStack.push(thisExp);
}

JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionSuperReference(JNIEnv *env, jclass, jobject jToken) {
    SgClassDefinition *class_definition = getCurrentTypeDefinition();
    ROSE_ASSERT(class_definition -> get_declaration() && (! class_definition -> attributeExists("namespace")));

    vector<SgBaseClass *> &inheritances = class_definition -> get_inheritances();
    if (inheritances.size() == 0 || inheritances[0] -> get_base_class() -> get_explicit_interface()) { // no super class specified?
        class_definition = ::ObjectClassDefinition; // ... then Object is the super class.
    }
    else {
        SgClassDeclaration *super_declaration = inheritances[0] -> get_base_class();
        ROSE_ASSERT(super_declaration && (! super_declaration -> get_explicit_interface())); // this class must have a super class
        class_definition = super_declaration -> get_definition(); // get the super class definition
    }

    SgClassSymbol *class_symbol = isSgClassSymbol(class_definition -> get_declaration() -> search_for_symbol_from_symbol_table());
    ROSE_ASSERT(class_symbol != NULL);

    SgSuperExp *superExp = SageBuilder::buildSuperExp(class_symbol);
    ROSE_ASSERT(superExp != NULL);

    astJavaComponentStack.push(superExp);
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionEmptyStatement(JNIEnv *env, jclass, jobject jToken) {
    // Nothing to do;
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionEmptyStatementEnd(JNIEnv *env, jclass, jobject jToken) {
    SgNullStatement *stmt = SageBuilder::buildNullStatement();
    ROSE_ASSERT(stmt != NULL);
//    setJavaSourcePosition(stmt, env, jToken);
    astJavaComponentStack.push(stmt);
}



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionConstructorDeclaration(JNIEnv *env, jclass, jstring java_string, jint constructor_index, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Build a SgMemberFunctionDeclaration (constructor) \n");

    SgName name = convertJavaStringToCxxString(env, java_string);

    SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
    ROSE_ASSERT(class_definition != NULL  && (! class_definition -> attributeExists("namespace")));

// MH-20140313 test
    SgScopeStatement *type_space = new SgScopeStatement();
    type_space -> set_parent(class_definition);

	
    AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("type_parameter_space");
    ROSE_ASSERT(attribute);
//    SgScopeStatement *type_space = isSgScopeStatement(attribute -> getNode(constructor_index));
//    ROSE_ASSERT(type_space);
        attribute -> setNode(type_space, constructor_index);

    astJavaScopeStack.push(type_space);
/*
    if (method_index >= 0) {
        AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("type_parameter_space");
        ROSE_ASSERT(attribute);
        attribute -> setNode(type_space, method_index);
    }

    astJavaScopeStack.push(type_space);
*/

    attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("class_members");
    ROSE_ASSERT(attribute);
    SgFunctionDefinition *constructor_definition = isSgFunctionDefinition(attribute -> getNode(constructor_index));
    ROSE_ASSERT(constructor_definition);
    astJavaScopeStack.push(constructor_definition);
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);

    //TODO: REMOVE this!!!
    //
    // We start by pushing a VOID return type to make the constructor look like a method.
    //
    //    astJavaComponentStack.push(SgTypeVoid::createType()); 
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionConstructorDeclarationHeader(JNIEnv *env, jclass,
                                                                           jstring java_string,
                                                                           jboolean java_is_native,
                                                                           jboolean java_is_private,
                                                                           jint java_numberOfTypeParameters,
                                                                           jint java_numberOfArguments,
                                                                           jint java_numberOfThrownExceptions,
                                                                           jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("End of SgMemberFunctionDeclaration (constructor) \n");

    SgName name   = convertJavaStringToCxxString(env, java_string);
    bool is_native = java_is_native;
    bool is_private = java_is_private;

    // DQ (7/31/2011): Add more precise handling of the statement stack.
    // This does not count (include) explicit constructor calls...
    int number_of_type_parameters = java_numberOfTypeParameters;
    int numberOfArguments = java_numberOfArguments;
    int numberOfThrownExceptions = java_numberOfThrownExceptions;

    //
    // TODO:
    //
    string exceptions = "";
    for (int i = 0; i < numberOfThrownExceptions; i++) { // Pop the Thrown types
        SgType *type = astJavaComponentStack.popType();
        SgClassType *class_type = isSgClassType(type);
        ROSE_ASSERT(class_type);

        string throw_name = getTypeName(class_type); // getFullyQualifiedTypeName(class_type);

        exceptions += throw_name;

        if (i < numberOfThrownExceptions - 1)
            exceptions += ", ";
    }

// TODO: Remove this!
    /*
    SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
    ROSE_ASSERT(class_definition != NULL  && (! class_definition -> attributeExists("namespace"))); // we must be inside a class scope

    //
    // NOTE that we left the arguments and the return type on the Component stack.  They will be processed
    // by this function call. 
    //
    SgMemberFunctionDeclaration *constructor_declaration = lookupMemberFunctionDeclarationInClassScope(class_definition, name, numberOfArguments); // buildDefiningMemberFunction(name, class_definition, numberOfArguments);
    ROSE_ASSERT(constructor_declaration);

    SgFunctionDefinition *constructor_definition = constructor_declaration -> get_definition();
    ROSE_ASSERT(constructor_definition != NULL);

    ROSE_ASSERT(numberOfTypeParameters == 0); // TODO: pop and process the Type Parameters.

    astJavaScopeStack.push(constructor_definition);
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);
    ROSE_ASSERT(constructor_definition -> get_body() != NULL);
    astJavaScopeStack.push(constructor_definition -> get_body());
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);

    setJavaSourcePosition(constructor_definition -> get_body(), env, jToken);

    */

    SgFunctionDefinition *constructor_definition = isSgFunctionDefinition(astJavaScopeStack.top());
    ROSE_ASSERT(constructor_definition -> get_body() != NULL);
    astJavaScopeStack.push(constructor_definition -> get_body());
    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);

    setJavaSourcePosition(constructor_definition -> get_body(), env, jToken);

    SgFunctionDeclaration *constructor_declaration = constructor_definition -> get_declaration();
    if (exceptions.size()) {
        constructor_declaration -> setAttribute("exception", new AstRegExAttribute(exceptions));
    }

    // Since this is a constructor, set it explicitly as such.
    constructor_declaration -> get_specialFunctionModifier().setConstructor();

    // Set the Java specific modifiers
    if (is_native) {
        constructor_declaration -> get_functionModifier().setJavaNative();
    }


    // TODO: We need the next 3 lines for EDG4 
    // SgMemberFunctionDeclaration *nondefining_constructor_declaration = isSgMemberFunctionDeclaration(constructor_declaration -> get_firstNondefiningDeclaration());
    // ROSE_ASSERT(nondefining_constructor_declaration);
    // nondefining_constructor_declaration -> get_declarationModifier().get_accessModifier().set_modifier(constructor_declaration -> get_declarationModifier().get_accessModifier().get_modifier());
}



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionConstructorDeclarationEnd(JNIEnv *env, jclass, jint java_numberOfStatements, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("End of SgMemberFunctionDeclaration (constructor) \n");

    // DQ (7/31/2011): Add more precise handling of the statement stack.
    // This does not count (include) explicit constructor calls...
    int numberOfStatements = java_numberOfStatements;

    if (SgProject::get_verbose() > 0)
        printf ("cactionConstructorDeclarationEnd(): numberOfStatements = %d\n", numberOfStatements);

    // Pop the constructor body...
    ROSE_ASSERT(! astJavaScopeStack.empty());
    SgBasicBlock *constructor_body = astJavaScopeStack.popBasicBlock();
    for (int i = 0; i  < numberOfStatements; i++) {
        SgStatement *statement = astJavaComponentStack.popStatement();
        if (SgProject::get_verbose() > 2) {
            cerr << "(4) Adding statement "
                 << statement -> class_name()
                 << " to a constructor declaration block"
                 << endl;
            cerr.flush();
        }
        constructor_body -> prepend_statement(statement);
    }

    /* SgFunctionDefinition *memberFunctionDefinition = */
    astJavaScopeStack.popFunctionDefinition();

    SgScopeStatement *type_space = isSgScopeStatement(astJavaScopeStack.pop());
    ROSE_ASSERT(type_space);
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
Java_x10rose_visit_JNI_cactionInsertClassStart(JNIEnv *env, jclass xxx, jstring java_string, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_string);

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionInsertClassStart(): = %s \n", name.str());
    SgScopeStatement *outerScope = astJavaScopeStack.top();
    ROSE_ASSERT(outerScope != NULL);
    SgClassDeclaration *class_declaration = buildDefiningClassDeclaration(name, outerScope);
//    setJavaSourcePosition(class_declaration, env, jToken);
    SgClassDefinition *class_definition = class_declaration -> get_definition();
    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));
//    setJavaSourcePosition(class_definition, env, jToken);

    astJavaScopeStack.push(class_definition); // to contain the class members...
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionInsertClassEnd(JNIEnv *env, jclass xxx, jstring java_string, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_string);

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionInsertClassEnd: %s \n", name.str());
    ROSE_ASSERT(! astJavaScopeStack.empty());

    SgClassDefinition *class_definition = astJavaScopeStack.popClassDefinition();
    ROSE_ASSERT(! class_definition -> attributeExists("namespace"));
}




JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionInsertClassStart2(JNIEnv *env, jclass, jstring java_string, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_string);

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionInsertClassStart2(): = %s \n", name.str());

    SgScopeStatement *outerScope = astJavaScopeStack.top();
    ROSE_ASSERT(outerScope != NULL);
    SgClassDeclaration *class_declaration = buildDefiningClassDeclaration(name, outerScope);
#if 1
	SgClassType *unknown = SgClassType::createType(class_declaration, NULL);
    astJavaComponentStack.push(unknown);
//	printf("**1**\n");
#else
    SgClassDefinition *class_definition = class_declaration -> get_definition();
    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));
    setJavaSourcePosition(class_definition, env, jToken);
    astJavaScopeStack.push(class_definition); // to contain the class members...
#endif
}

JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionBuildClassSupportStart(JNIEnv *env, jclass xxx, jstring java_name, jstring java_external_name, jboolean java_user_defined_class, jboolean java_has_conflicts, jboolean java_is_interface, jboolean java_is_enum, jboolean java_is_anonymous, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_name);
    SgName external_name = convertJavaStringToCxxString(env, java_external_name);
    bool user_defined_class = java_user_defined_class;
    bool has_conflicts = java_has_conflicts;
    bool is_interface = java_is_interface;
    bool is_enum = java_is_enum;
    bool is_anonymous = java_is_anonymous;

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionBuildClassSupportStart(): %s %s \n", (is_interface ? "interface" : "class"), name.str());

    SgScopeStatement *outerScope = astJavaScopeStack.top();
    ROSE_ASSERT(outerScope != NULL);

    SgClassSymbol *class_symbol = outerScope -> lookup_class_symbol(name);
    ROSE_ASSERT(class_symbol);
    SgClassDeclaration *declaration = (SgClassDeclaration *) class_symbol -> get_declaration() -> get_definingDeclaration();
    ROSE_ASSERT(declaration);
    SgClassDefinition *class_definition = declaration -> get_definition();
    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));
    astJavaScopeStack.push(class_definition); // to contain the class members...

    declaration -> set_explicit_interface(is_interface); // Identify whether or not this is an interface.
    declaration -> set_explicit_enum(is_enum);           // Identify whether or not this is an enum.

    SgClassType *class_type = declaration -> get_type();
    if (external_name.getString().size() > 0) {
        ROSE_ASSERT(class_type);
        class_type -> setAttribute("name", new AstRegExAttribute(external_name.getString()));
        if (is_anonymous) {
            class_type -> setAttribute("anonymous", new AstRegExAttribute(""));
            declaration -> setAttribute("anonymous", new AstRegExAttribute(""));
        }
    }
    //
    // Identify classes that are conflicting that must be fully-qualified when used.
    //
    if (has_conflicts) {
        class_type -> setAttribute("has_conflicts", new AstRegExAttribute(""));
    }

    //
    // If this is a user-defined class, we may need to keep track of some of its class members.
    //
    if (user_defined_class) {
        class_definition -> setAttribute("class_members", new AstSgNodeListAttribute());
        class_definition -> setAttribute("type_parameter_space", new AstSgNodeListAttribute());
    }

    astJavaComponentStack.push(class_definition); // To mark the end of the list of components in this type.

    if (SgProject::get_verbose() > 0)
        printf ("Exiting Java_JavaParser_cactionBuildClassSupportStart(): %s %s \n", (is_interface ? "interface" : "class"), name.str());
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

	// MH-20140311
    definition -> setAttribute("class_members", new AstSgNodeListAttribute());
    definition -> setAttribute("type_parameter_space", new AstSgNodeListAttribute());

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


//Java_x10rose_visit_JNI_cactionInsertClassStart2();

	    printf("TypeDeclaration ends\n");
}

JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionBuildClassSupportEnd(JNIEnv *env, jclass xxx, jstring java_string, jobject jToken) {
    SgName name = convertJavaStringToCxxString(env, java_string);

    if (SgProject::get_verbose() > 0)
        printf ("Inside of Java_JavaParser_cactionBuildClassSupportEnd: %s \n", name.str());

    ROSE_ASSERT(! astJavaScopeStack.empty());

    SgClassDefinition *class_definition = astJavaScopeStack.popClassDefinition();
    ROSE_ASSERT(! class_definition -> attributeExists("namespace"));
    for (SgStatement *statement = astJavaComponentStack.popStatement();
#if 1
        statement != class_definition;
#else
		statement != NULL;
#endif
        statement = astJavaComponentStack.popStatement()) {
		
		cout << "statement=" << statement << endl;
#if 1	
        if (SgProject::get_verbose() > 2) {
            cerr << "(1) Adding statement "
                 << statement -> class_name()
                 << " to an implicit Type Declaration"
                 << endl;
            cerr.flush();
        }
        ROSE_ASSERT(statement != NULL);

        class_definition -> prepend_statement(statement);
#endif
    }


    ROSE_ASSERT(! astJavaScopeStack.empty());
    SgScopeStatement *outerScope = astJavaScopeStack.top();

    SgClassDeclaration *class_declaration = class_definition -> get_declaration();
    ROSE_ASSERT(class_declaration);


    //
    // TODO:  Review this because of the package issue and the inability to build a global AST.
    //
    ROSE_ASSERT(outerScope != NULL);
    if (isSgClassDefinition(outerScope) && isSgClassDefinition(outerScope) -> attributeExists("namespace")) { // a type in a package?
        isSgClassDefinition(outerScope) -> append_statement(class_declaration);
    }
    else if (isSgClassDefinition(outerScope) && (! isSgClassDefinition(outerScope) -> attributeExists("namespace"))) { // an inner type?
        astJavaComponentStack.push(class_declaration);
    }
    else if (isSgBasicBlock(outerScope)) { // a local type declaration?
        astJavaComponentStack.push(class_declaration);
    }
    else if (outerScope == ::globalScope) { // a user-defined type?
        ::globalScope -> append_statement(class_declaration);
    }
    else { // What is this?
#if 1 //MH-20140313 so far push global scope
        ::globalScope -> append_statement(class_declaration);
#else
        ROSE_ASSERT(false);
#endif
    }

    if (SgProject::get_verbose() > 0)
        printf ("Leaving Java_JavaParser_cactionBuildClassSupportEnd: %s \n", name.str());
}

JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionBlock(JNIEnv *env, jclass, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Build an SgBasicBlock scope \n");

    // There could be a conditional from an IF statement on the stack.
    // ROSE_ASSERT(astJavaExpressionStack.empty());

    // If there is an expression on the expression stack and an SgIfStmt on the scope stack then 
    // this might be a good time to associate the conditional with the SgIfStmt and have a more
    // enforceble rules going forward.  But then there might not be a SgBasicBlock, so don't do this.

    // Since we build the true body when we build the ifStmt, we need to detect and reuse this 
    // SgBasicBlock instead of building a new one.
    // SgBasicBlock *block = SageBuilder::buildBasicBlock();
    SgBasicBlock *block = NULL;
    if (isSgIfStmt(astJavaScopeStack.top())) {
        SgIfStmt *ifStatement = (SgIfStmt*) astJavaScopeStack.top();
        SgNullStatement *nullStatement = isSgNullStatement(ifStatement -> get_true_body());
        if (nullStatement != NULL) {
            // block = ifStatement -> get_true_body();
            block = SageBuilder::buildBasicBlock();
            ROSE_ASSERT(block != NULL);
            ifStatement -> set_true_body(block);

            delete nullStatement;
        }
        else {
            // Set the false body
            block = SageBuilder::buildBasicBlock();
            ROSE_ASSERT(block != NULL);
            ifStatement -> set_false_body(block);
        }
    }
    else if (isSgForStatement(astJavaScopeStack.top())) {
        // DQ (7/30/2011): Handle the case of a block after a SgForStatement
        // Because we build the SgForStatement on the stack and then the cactionBlock 
        // function is called, we have to detect and fixup the SgForStatement.
        SgForStatement *forStatement = (SgForStatement*) astJavaScopeStack.top();
        SgNullStatement *nullStatement = isSgNullStatement(forStatement -> get_loop_body());
        if (nullStatement != NULL) {
            block = SageBuilder::buildBasicBlock();
            ROSE_ASSERT(block != NULL);
            forStatement -> set_loop_body(block);
            delete nullStatement;
        }
    }
    else {
        block = SageBuilder::buildBasicBlock();
    }
    ROSE_ASSERT(block != NULL);

//    setJavaSourcePosition(block, env, jToken);

    block -> set_parent(astJavaScopeStack.top());
    ROSE_ASSERT(block -> get_parent() != NULL);

//MH-20140313
printf("BasicBlock push=%p\n", block);

    astJavaScopeStack.push(block);
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionBlockEnd(JNIEnv *env, jclass, jint java_numberOfStatements, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Pop the current SgBasicBlock scope off the scope stack...\n");

    int numberOfStatements = java_numberOfStatements;

    if (SgProject::get_verbose() > 2)
        printf ("In cactionBlockEnd(): numberOfStatements = %d \n", numberOfStatements);

    ROSE_ASSERT(! astJavaScopeStack.empty());

    // DQ (7/30/2011): Take the block off of the scope stack and put it onto the statement stack so that we can 
    // process either blocks of other statements uniformally.
    SgBasicBlock *body = astJavaScopeStack.popBasicBlock();
    for (int i = 0; i  < numberOfStatements; i++) {
        SgStatement *statement = astJavaComponentStack.popStatement();
        if (SgProject::get_verbose() > 2) {
            cerr << "(7) Adding statement "
                 << statement -> class_name()
                 << " to a block"
                 << endl;
            cerr.flush();
        }
        body -> prepend_statement(statement);
    }
//MH-20140313
//    astJavaComponentStack.push(body);
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionReturnStatement(JNIEnv *env, jclass, jobject jToken) {
    // Nothing to do !!!
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionReturnStatementEnd(JNIEnv *env, jclass, jboolean has_expression, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Inside of Java_JavaParser_cactionReturnStatementEnd() \n");

    // Build the Return Statement
    SgExpression *expression = (has_expression ? astJavaComponentStack.popExpression() : NULL);
    SgReturnStmt *returnStatement = SageBuilder::buildReturnStmt_nfi(expression);
    ROSE_ASSERT(has_expression || returnStatement -> get_expression() == NULL); // TODO: there is an issue with the implementation of buildReturnStmt()...
//    setJavaSourcePosition(returnStatement, env, jToken);

    // Pushing 'return' on the statement stack
    astJavaComponentStack.push(returnStatement);

}

JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionBinaryExpression(JNIEnv *env, jclass, jobject jToken) {
    // I don't think we need this function.
}


JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionBinaryExpressionEnd(JNIEnv *env, jclass, jint java_operator_kind, jobject jToken) {
    if (SgProject::get_verbose() > 2)
        printf ("Build an Binary Expression End \n");

    // These are the operator code values directly from ECJ.
    enum ops {
        ERROR_OPERATOR       = 0, // This is not a ECJ value 
        AND                  = 2,
        DIVIDE               = 9,
        GREATER              = 6,
        GREATER_EQUAL        = 7,
        LEFT_SHIFT           = 10,
        LESS                 = 4,
        LESS_EQUAL           = 5,
        MINUS                = 13,
        MULTIPLY             = 15,
        OR                   = 3,
        PLUS                 = 14,
        REMAINDER            = 16,
        RIGHT_SHIFT          = 17,
        UNSIGNED_RIGHT_SHIFT = 19,
        XOR                  = 8,
        OR_OR                = 100, // Handled by separate function 
        AND_AND              = 101, // Handled by separate function 
        LAST_OPERATOR
    };

    // printf ("PLUS = %d \n", PLUS);

    int operator_kind = java_operator_kind;
    //printf ("operator_kind = %d \n", operator_kind);

    switch(operator_kind) {
        // Operator codes used by the BinaryExpression in ECJ.
        case LESS:                 binaryExpressionSupport<SgLessThanOp>();       break;
        case LESS_EQUAL:           binaryExpressionSupport<SgLessOrEqualOp>();    break;
        case GREATER:              binaryExpressionSupport<SgGreaterThanOp>();    break;
        case GREATER_EQUAL:        binaryExpressionSupport<SgGreaterOrEqualOp>(); break;
        case AND:                  binaryExpressionSupport<SgBitAndOp>();         break;
        case OR:                   binaryExpressionSupport<SgBitOrOp>();          break;
        case XOR:                  binaryExpressionSupport<SgBitXorOp>();         break;
        case DIVIDE:               binaryExpressionSupport<SgDivideOp>();         break;
        case MINUS:                binaryExpressionSupport<SgSubtractOp>();       break;
        case PLUS:                 binaryExpressionSupport<SgAddOp>();            break;
        case MULTIPLY:             binaryExpressionSupport<SgMultiplyOp>();       break;
        case RIGHT_SHIFT:          binaryExpressionSupport<SgRshiftOp>();         break;
        case LEFT_SHIFT:           binaryExpressionSupport<SgLshiftOp>();         break;
        case REMAINDER:            binaryExpressionSupport<SgModOp>();            break;

        // This may have to handled special in ROSE. ROSE does not represent the semantics,
        // and so this support my require a special operator to support Java in ROSE. For
        // now we will use the more common SgRshiftOp.
        case UNSIGNED_RIGHT_SHIFT: binaryExpressionSupport<SgJavaUnsignedRshiftOp>();         break;

        // These are handled through separate functions (not a BinaryExpression in ECJ).
        case OR_OR:   ROSE_ASSERT(false); break;
        case AND_AND: ROSE_ASSERT(false); break;

        default:
            printf ("Error: default reached in cactionBinaryExpressionEnd() operator_kind = %d \n", operator_kind);
            ROSE_ASSERT(false);
    }

//    setJavaSourcePosition((SgLocatedNode *) astJavaComponentStack.top(), env, jToken);
}




JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionMethodDeclaration(JNIEnv *env, jclass, 
																jstring java_string, 
																jint method_index, 
																jint number_of_arguments, 
 																jobject method_location, 
																jobject args_location,
																jobject jToken) {
#if 0
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


/////////////////////////////////////
/////////////////////////////////////

//	astJavaScopeStack.push(method_definition); 
	astJavaComponentStack.push(method_definition);

// MH-20140226
// this is a temporary approach to avoid errors
// 
// MH-20140311
// seemed to be fixed
#if 1
	astJavaScopeStack.push(class_definition); 
	astJavaComponentStack.push(class_definition);
#endif

	printf("MethodDeclaration end\n");
#else
    if (SgProject::get_verbose() > 0)
        printf ("Build a SgMemberFunctionDeclaration \n");

    SgName name = convertJavaStringToCxxString(env, java_string);

    SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
    ROSE_ASSERT(class_definition != NULL  && (! class_definition -> attributeExists("namespace")));
    AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("type_parameter_space");
    ROSE_ASSERT(attribute);
    SgScopeStatement *type_space = isSgScopeStatement(attribute -> getNode(method_index));
    ROSE_ASSERT(type_space);

    attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("class_members");
    ROSE_ASSERT(attribute);
    SgFunctionDefinition *method_definition = isSgFunctionDefinition(attribute -> getNode(method_index));
    ROSE_ASSERT(method_definition);
    SgMemberFunctionDeclaration *method_declaration = isSgMemberFunctionDeclaration(method_definition -> get_declaration());
    ROSE_ASSERT(method_declaration);
    ROSE_ASSERT(method_declaration -> get_type());
    ROSE_ASSERT(method_declaration -> get_type() -> get_return_type());
    method_declaration -> setAttribute("type", new AstRegExAttribute(getTypeName(method_declaration -> get_type() -> get_return_type())));

    astJavaScopeStack.push(type_space);
    astJavaScopeStack.push(method_definition);
//MH-20140312
//temtatively push class_definition into astJavaScopeStack to pass assertion for checking if 
//stack top is class definition or not
    astJavaScopeStack.push(class_definition);

    ROSE_ASSERT(astJavaScopeStack.top() -> get_parent() != NULL);

#endif
}



JNIEXPORT void JNICALL 
Java_x10rose_visit_JNI_cactionMethodDeclarationEnd(JNIEnv *env, jclass, jint java_numberOfStatements, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Entering  cactionMethodDeclarationEnd (method) \n");

    // Pop the constructor body...
    ROSE_ASSERT(! astJavaScopeStack.empty());


    int numberOfStatements = java_numberOfStatements;

    if (SgProject::get_verbose() > 0)
        printf ("In cactionMethodDeclarationEnd(): numberOfStatements = %d\n", numberOfStatements);

    SgBasicBlock *method_body = astJavaScopeStack.popBasicBlock(); // pop the body block
    for (int i = 0; i < numberOfStatements; i++) {
         SgStatement *statement = astJavaComponentStack.popStatement();
         if (SgProject::get_verbose() > 2) {
             cerr << "(5) Adding statement "
                  << statement -> class_name()
                  << " to a method declaration block"
                  << endl;
             cerr.flush();
        }
printf("109\n");
        method_body -> prepend_statement(statement);
    }

printf("110\n");
    /* SgFunctionDefinition *memberFunctionDefinition = */
    astJavaScopeStack.popFunctionDefinition();

printf("111\n");
    SgScopeStatement *type_space = isSgScopeStatement(astJavaScopeStack.pop());
printf("112\n");
    ROSE_ASSERT(type_space);

    if (SgProject::get_verbose() > 0)
        printf ("Exiting  cactionMethodDeclarationEnd (method) \n");
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
		// cactionInsertClassStart2() does not have a parameter for specifying a package name, although
		// this is fine for x10 because x10 classes have been fully qualified with package  name
		Java_x10rose_visit_JNI_cactionInsertClassStart2(env, NULL, java_type_name, jToken);
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
	else
    astJavaComponentStack.push(type);

    if (SgProject::get_verbose() > 0)
        printf ("Exiting cactionTypeReference\n");
}



JNIEXPORT void JNICALL Java_x10rose_visit_JNI_cactionBuildMethodSupportStart(JNIEnv *env, jclass,
                                                                      jstring java_name,
                                                                      jint method_index,
                                                                      jobject method_location) {
    SgName name = convertJavaStringToCxxString(env, java_name);
    if (SgProject::get_verbose() > 1)
          printf ("Inside of BuildMethodSupportStart for method = %s \n", name.str());

    SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
printf("class_definition=%p\n", class_definition);
    ROSE_ASSERT(class_definition && (! class_definition -> attributeExists("namespace")));

    //
    // This scope will be used to store Type Parameters, if there are any.
    //
    SgScopeStatement *type_space = new SgScopeStatement();
    type_space -> set_parent(class_definition);
//    setJavaSourcePosition(type_space, env, method_location);

    if (method_index >= 0) {
        AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("type_parameter_space");
        ROSE_ASSERT(attribute);
        attribute -> setNode(type_space, method_index);
    }

    astJavaScopeStack.push(type_space);


    if (SgProject::get_verbose() > 1)
        printf ("Exiting BuildMethodSupportStart for method = %s \n", name.str());
}


JNIEXPORT void JNICALL Java_x10rose_visit_JNI_cactionBuildMethodSupportEnd(JNIEnv *env, jclass xxx,
                                                                    jstring java_string,
                                                                    jint method_index,
                                                                    jboolean java_is_constructor,
                                                                    jboolean java_is_abstract,
                                                                    jboolean java_is_native,
                                                                    jint java_number_of_type_parameters,
                                                                    jint java_number_of_arguments,
                                                                    jboolean java_is_user_defined,
                                                                    jobject method_location,
                                                                    jobject args_location) {
    SgName name = convertJavaStringToCxxString(env, java_string);
    int number_of_type_parameters = java_number_of_type_parameters;
    int number_of_arguments = java_number_of_arguments;
    bool is_constructor = java_is_constructor,
         is_abstract = java_is_abstract,
         is_native = java_is_native,
         is_user_defined = java_is_user_defined;

    if (SgProject::get_verbose() > 1)
        printf ("Build support for implicit class member function (method) name = %s \n", name.str());

    SgScopeStatement *type_space = isSgScopeStatement(astJavaScopeStack.pop());
    ROSE_ASSERT(type_space);


// TODO: Remove this !!!
//    SgFunctionDefinition *method_definition = isSgFunctionDefinition(((AstSgNodeAttribute *) type_space -> getAttribute("method")) -> getNode());
//    ROSE_ASSERT(method_definition);

    SgClassDefinition *class_definition = isSgClassDefinition(astJavaScopeStack.top());
    ROSE_ASSERT(class_definition != NULL && (! class_definition -> attributeExists("namespace")));

	cout << "***** name=" << name << ", def=" << class_definition << endl;

    //
    // There is no reason to distinguish between defining and non-defining declarations in Java...
    //
    SgMemberFunctionDeclaration *method_declaration = buildDefiningMemberFunction(name, class_definition, number_of_arguments, env, method_location, args_location);
    setJavaSourcePosition(method_declaration, env, method_location);
    ROSE_ASSERT(method_declaration != NULL);

    SgFunctionDefinition *method_definition = method_declaration -> get_definition();
    ROSE_ASSERT(method_definition);
    if (method_index >= 0) {
// TODO: Remove this !!!
//        method_definition -> setAttribute("type_space", new AstSgNodeAttribute(type_space));
//        AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("class_members");
//        ROSE_ASSERT(attribute);
//        attribute -> setNode(method_definition, method_index);

        AstSgNodeListAttribute *attribute = (AstSgNodeListAttribute *) class_definition -> getAttribute("class_members");
        ROSE_ASSERT(attribute);
        attribute -> setNode(method_definition, method_index);
    }

    if (is_constructor) {
        method_declaration -> get_specialFunctionModifier().setConstructor();
    }
    if (is_abstract) {
        method_declaration -> get_declarationModifier().setJavaAbstract();
        method_declaration -> setForward(); // indicate that this function does not contain a body.
    }
    if (is_native) {
        method_declaration -> get_functionModifier().setJavaNative();
        method_declaration -> setForward(); // indicate that this function does not contain a body.
    }

    if (number_of_type_parameters > 0) {
        list<SgTemplateParameter *> parameter_list;
        for (int i = 0; i < number_of_type_parameters; i++) { // Reverse the content of the stack.
            SgClassDeclaration *parameter_decl = isSgClassDeclaration(astJavaComponentStack.pop());
            ROSE_ASSERT(parameter_decl);
            SgTemplateParameter *parameter = new SgTemplateParameter(parameter_decl -> get_type(), NULL);
            parameter_list.push_front(parameter);
        }

        SgTemplateParameterPtrList final_list;
        while (! parameter_list.empty()) { // Now that we have the parameters in the right order, create the final list.
            SgTemplateParameter *parameter = parameter_list.front();
            parameter_list.pop_front();
            final_list.push_back(parameter);
        }

        SgTemplateParameterList *template_parameter_list = new SgTemplateParameterList();
        template_parameter_list -> set_args(final_list);
        method_declaration -> setAttribute("type_parameters", new AstSgNodeAttribute(template_parameter_list));
    }

    // TODO: We need the next 3 lines for EDG4 
    // SgMemberFunctionDeclaration *nondefining_method_declaration = isSgMemberFunctionDeclaration(method_declaration -> get_firstNondefiningDeclaration());
    // ROSE_ASSERT(nondefining_method_declaration);
    // nondefining_method_declaration -> get_declarationModifier().get_accessModifier().set_modifier(method_declaration -> get_declarationModifier().get_accessModifier().get_modifier());

    astJavaComponentStack.push(method_declaration);

//MH-20140312
// Tentatively push class definition onto stack top.
// So far, this is necessary for passing an assertion in Java_x10rose_visit_JNI_cactionBuildMethodSupportStart 
// for checking if stack top is class definition or not.
#if 1
    astJavaScopeStack.push(method_definition);
    astJavaScopeStack.push(class_definition);
#endif


#if 0
	SgSymbolTable *tab = class_definition->get_symbol_table();
	int size = tab->size();
	printf("Symbol table size=%d\n", size);
	tab->print();
#endif

    if (SgProject::get_verbose() > 1)
        printf ("Exiting build support for implicit class member function (method) name = %s \n", name.str());
}




JNIEXPORT void JNICALL
Java_x10rose_visit_JNI_cactionBuildArgumentSupport(JNIEnv *env, jclass, jstring java_argument_name, jboolean java_is_var_args, jboolean java_is_final, jobject jToken) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside of Build argument support\n");

// TODO: Remove this !!!
//    SgFunctionDefinition *method_definition = isSgFunctionDefinition(astJavaScopeStack.top());
//    ROSE_ASSERT(method_definition);

    SgName argument_name = convertJavaStringToCxxString(env, java_argument_name);
    bool is_final = java_is_final;
    bool is_var_args = java_is_var_args;

    if (SgProject::get_verbose() > 0)
        printf ("argument argument_name = %s \n", argument_name.str());

    SgType *argument_type = astJavaComponentStack.popType();
//cout << "ArgType="<< argument_type << endl;
    ROSE_ASSERT(argument_type);

    // Until we attached this to the AST, this will generate an error in the AST consistancy tests.
    SgInitializedName *initialized_name = SageBuilder::buildInitializedName(argument_name, argument_type, NULL);

//    setJavaSourcePosition(initialized_name, env, jToken);
    ROSE_ASSERT(initialized_name != NULL);

    //
    // TODO: This is a patch.  Currently, the final attribute can only be associated with a
    //       variable declaration. However, a parameter declaration is an SgInitializedName
    //       in the Sage III representation and not an SgVariableDeclaration.
    //
    // The correct code should look something like this:
    //
    //    if (is_final) {
    //        initialized_name -> get_declarationModifier().setFinal();
    //    }
    //
    if (is_final) {
        initialized_name -> setAttribute("final", new AstRegExAttribute(""));
    }

    //
    // Identify Arguments with var arguments.
    //
    if (is_var_args) {
        SgPointerType *array_type = isSgPointerType(argument_type);
        ROSE_ASSERT(array_type);
        SgType *element_type = array_type -> get_base_type();

        initialized_name -> setAttribute("var_args", new AstSgNodeAttribute(element_type));
        initialized_name -> setAttribute("type", new AstRegExAttribute(getTypeName(element_type) + "..."));
    }
    else initialized_name -> setAttribute("type", new AstRegExAttribute(getTypeName(argument_type)));

// TODO: Remove this !!!
//    initialized_name -> set_scope(method_definition);
//    method_definition -> insert_symbol(argument_name, new SgVariableSymbol(initialized_name));

    astJavaComponentStack.push(initialized_name);

// TODO: Remove this!
cout << "Pushed " << initialized_name -> class_name() << endl; cout.flush();

    if (SgProject::get_verbose() > 0)
        printf ("Exiting Build argument support\n");
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
Java_x10rose_visit_JNI_cactionSingleNameReference(JNIEnv *env, jclass, jstring java_package_name, jstring java_type_name, jstring java_name, jobject jToken) {
    SgName package_name = convertJavaPackageNameToCxxString(env, java_package_name),
           type_name = convertJavaStringToCxxString(env, java_type_name),
           name = convertJavaStringToCxxString(env, java_name);
//cout << "NAME=" << name << endl;
    SgVariableSymbol *variable_symbol = NULL;
    if (! type_name.getString().empty()) { // an instance variable?
        if (SgProject::get_verbose() > 0)
            printf ("Building a Single Name reference for name = %s%s%s \n", (package_name.getString().empty() ? "" : (package_name.getString() + ".")).c_str(), (type_name.getString() + ".").c_str(), name.str());

        SgType *type = lookupTypeByName(package_name, type_name, 0 /* not an array - number of dimensions is 0 */);
        ROSE_ASSERT(type);
        SgClassType *class_type = isSgClassType(type);
        ROSE_ASSERT(class_type);
        SgClassDeclaration *declaration = isSgClassDeclaration(class_type -> get_declaration() -> get_definingDeclaration());
        ROSE_ASSERT(declaration);
        ROSE_ASSERT(declaration -> get_definition());
        variable_symbol = lookupSimpleNameVariableInClass(name, declaration -> get_definition());
    }
    else { // a local variable!
        if (SgProject::get_verbose() > 0)
            printf ("Building a Single Name reference for name = %s \n", name.str());
        variable_symbol = lookupVariableByName(name);
    }

    ROSE_ASSERT(variable_symbol);
    SgVarRefExp *varRefExp = SageBuilder::buildVarRefExp(variable_symbol);
cout << "varRefExp=" << varRefExp << endl;
    ROSE_ASSERT(varRefExp != NULL);
    if (SgProject::get_verbose() > 0)
        printf ("In cactionSingleNameReference(): varRefExp = %p type = %p = %s \n", varRefExp, varRefExp -> get_type(), varRefExp -> get_type() -> class_name().c_str());

//    setJavaSourcePosition(varRefExp, env, jToken);

	varRefExp->get_file_info()->display("TEST DISPLAY");

#if 0
    ROSE_ASSERT(! varRefExp -> get_file_info() -> isTransformation());
    ROSE_ASSERT(! varRefExp -> get_file_info() -> isCompilerGenerated());
#endif
    astJavaComponentStack.push(varRefExp);
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



