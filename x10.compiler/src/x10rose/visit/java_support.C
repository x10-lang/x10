#include "sage3basic.h"

// DQ (10/14/2010):  This should only be included by source files that require it.
// This fixed a reported bug which caused conflicts with autoconf macros (e.g. PACKAGE_BUGREPORT).
// Interestingly it must be at the top of the list of include files.
#include "rose_config.h"

// Java support for calling C/C++.
#include <jni.h>

// Support functions declaration of function defined in this file.
#include "java_support.h"
#include "jni_token.h"

SgGlobal *globalScope = NULL;
SgClassType *ObjectClassType = NULL;
SgClassType *StringClassType = NULL;
SgClassType *ClassClassType = NULL;
SgClassDefinition *ObjectClassDefinition = NULL;
vector<SgClassDefinition *> importedPackages;
vector<SgClassType *> importedTypes;

bool isImportedType(SgClassType *class_type) {
    //
    // Look to see if the type is an imported type.
    //
    for (vector<SgClassType *>::iterator ptype = importedTypes.begin(); ptype != importedTypes.end(); ptype++) {
        if ((*ptype) == class_type) { // definitely an imported type
            return true;
        }
    }

    //
    // Look to see if the type can be imported on-demand.
    //
    for (vector<SgClassDefinition *>::iterator package = importedPackages.begin(); package != importedPackages.end(); package++) {
        SgClassSymbol *class_symbol = (*package) -> lookup_class_symbol(class_type -> get_name());
        if (class_symbol && class_symbol -> get_type() == class_type) { // an on-demand imported type?
// TODO: Remove this !
/*
if ((*package) -> get_declaration() -> get_name().getString().compare("java_lang") != 0) {
cout << "*** The type "
     << class_type -> get_qualified_name().getString()
     << " was imported on-demand"
<< endl;
cout.flush();
}
*/
            return true;
        }
    }

    return false;
}

SgArrayType *getUniqueArrayType(SgType *base_type, int num_dimensions) {
    ROSE_ASSERT(num_dimensions > 0);
    ostringstream convert;     // stream used for the conversion
    convert << num_dimensions; // insert the textual representation of num_dimensions in the characters in the stream
    string dimensions = convert.str();

    AstArrayTypeAttribute *attribute = (AstArrayTypeAttribute *) base_type -> getAttribute(dimensions);
    if (attribute == NULL) {
        SgArrayType *array_type = SageBuilder::buildArrayType(base_type);
        array_type -> set_rank(num_dimensions);
        attribute = new AstArrayTypeAttribute(array_type);
        base_type -> setAttribute(dimensions, attribute);
    }

    return attribute -> getArrayType();
}


//
// Turn Java array into a pointer to make it look like C++ in order to please the consistency check in Cxx_Grammar.C
//
SgPointerType *getUniquePointerType(SgType *base_type, int num_dimensions) {
    ROSE_ASSERT(base_type);
    ROSE_ASSERT(num_dimensions > 0);
    ostringstream convert;     // stream used for the conversion
    convert << num_dimensions; // insert the textual representation of num_dimensions in the characters in the stream
    string dimensions = convert.str() + "!";

    AstPointerTypeAttribute *attribute = (AstPointerTypeAttribute *) base_type -> getAttribute(dimensions);
    if (attribute == NULL) { // This Array type does not yet exist!
        //
        // WARNING:  Do not use this function: SageBuilder::buildPointerType(base_type), to create a
        // SgPointerType!!!  For some reason, it may return a pointer type that is different from the
        // base_type that is passed as argument.  Instead, use the command "new SgPointerType(base_type)".
        //
        SgPointerType *pointer_type =  (num_dimensions == 1 // end the recursion
                                           ? new SgPointerType(base_type)
                                           : pointer_type =  SageBuilder::buildPointerType(getUniquePointerType(base_type, num_dimensions - 1)));

        attribute = new AstPointerTypeAttribute(pointer_type);
        base_type -> setAttribute(dimensions, attribute);
    }

    return attribute -> getPointerType();
}


SgJavaParameterizedType *getUniqueParameterizedType(SgClassType *raw_type, SgTemplateParameterPtrList &newArgs) {
    if (! raw_type -> attributeExists("parameterized types")) {
        raw_type -> setAttribute("parameterized types", new AstParameterizedTypeAttribute(raw_type) );
    }
    AstParameterizedTypeAttribute *attribute = (AstParameterizedTypeAttribute *) raw_type -> getAttribute("parameterized types");
    ROSE_ASSERT(attribute);

    return attribute -> findOrInsertParameterizedType(newArgs);
}


//
// Generate the unbound wildcard if it does not yet exist and return it.  Once the unbound Wildcard
// is generated, it is attached to the Object type so that it can be retrieved later. 
//
SgJavaWildcardType *getUniqueWildcardUnbound() {
    ROSE_ASSERT(::ObjectClassType);
    AstSgNodeAttribute *attribute = (AstSgNodeAttribute *) ::ObjectClassType -> getAttribute("unbound");
    if (! attribute) {
        SgJavaWildcardType *wildcard = new SgJavaWildcardType();
        wildcard -> set_is_unbound(true);
        wildcard -> set_has_extends(false);
        wildcard -> set_has_super(false);

        attribute = new AstSgNodeAttribute(wildcard);
        ::ObjectClassType -> setAttribute("unbound", attribute);
    }

    return isSgJavaWildcardType(attribute -> getNode());
}


//
// If it does not exist yet, generate wildcard type that extends this type.  Return the wildcard in question. 
//
SgJavaWildcardType *getUniqueWildcardExtends(SgType *type) {
    ROSE_ASSERT(type);
    AstSgNodeAttribute *attribute = (AstSgNodeAttribute *) type -> getAttribute("extends");
    if (! attribute) {
        SgJavaWildcardType *wildcard = new SgJavaWildcardType(type);
        wildcard -> set_is_unbound(false);
        wildcard -> set_has_extends(true);
        wildcard -> set_has_super(false);

        attribute = new AstSgNodeAttribute(wildcard);
        type -> setAttribute("extends", attribute);
    }

    return isSgJavaWildcardType(attribute -> getNode());
}


//
// If it does not exist yet, generate a super wildcard for this type.  Return the wildcard in question. 
//
SgJavaWildcardType *getUniqueWildcardSuper(SgType *type) {
    ROSE_ASSERT(type);
    AstSgNodeAttribute *attribute = (AstSgNodeAttribute *) type -> getAttribute("super");
    if (! attribute) {
        SgJavaWildcardType *wildcard = new SgJavaWildcardType(type);
        wildcard -> set_is_unbound(false);
        wildcard -> set_has_extends(false);
        wildcard -> set_has_super(true);

        attribute = new AstSgNodeAttribute(wildcard);
        type -> setAttribute("super", attribute);
    }

    return isSgJavaWildcardType(attribute -> getNode());
}


//
// Always map the Rose SgTypeString into java.lang.String before making comparison. This is required
// because Rose assigns the the type SgTypeString by default to a string constant (an SgStringVal).
//
bool isCompatibleTypes(SgType *source_type, SgType *target_type) {
    if (isSgJavaParameterizedType(source_type))
        source_type = isSgJavaParameterizedType(source_type) -> get_raw_type();
    if (isSgJavaParameterizedType(target_type))
        target_type = isSgJavaParameterizedType(target_type) -> get_raw_type();

    if (isSgTypeString(source_type))
        source_type = StringClassType;
    if (isSgTypeString(target_type))
        target_type = StringClassType;

    return source_type == target_type;
}

//
//
//
string getPrimitiveTypeName(SgType *type) {
    string type_name;

    if (isSgTypeBool(type)) {
        type_name = "boolean";
    }
    else if (isSgTypeSignedChar(type)) {
        type_name = "byte";
    }
    else if (isSgTypeWchar(type)) {
        type_name = "char";
    }
    else if (isSgTypeInt(type)) {
        type_name = "int";
    }
    else if (isSgTypeShort(type)) {
        type_name = "short";
    }
    else if (isSgTypeFloat(type)) {
        type_name = "float";
    }
    else if (isSgTypeLong(type)) {
        type_name = "long";
    }
    else if (isSgTypeDouble(type)) {
        type_name = "double";
    }
    else if (isSgTypeVoid(type)) {
        type_name = "void";
    }
    else if (isSgTypeString(type)) {
        type_name = getTypeName(StringClassType);
    }
    else {
        ROSE_ASSERT(type);
cout << "***I don't recognize the type " << type -> class_name() << endl;
        ROSE_ASSERT(false);
    }

    return type_name;
}


//
//
//
string getWildcardTypeName(SgJavaWildcardType *wild_type) {
    string name = "?";

    if (! wild_type -> get_is_unbound()) {
        name += (wild_type -> get_has_extends() ? " extends " : " super ");
        name += getTypeName(wild_type -> get_bound_type());
    }

    return name;
}


//
//
//
string getFullyQualifiedName(SgClassDefinition *definition) {
    if (definition -> attributeExists("namespace")) {
        AstRegExAttribute *attribute = (AstRegExAttribute *) definition -> getAttribute("namespace");
        return attribute -> expression;
    }

    string name = definition -> get_declaration() -> get_name();
    if (isSgClassDefinition(definition -> get_scope())) {
        string prefix = getFullyQualifiedName((SgClassDefinition *) definition -> get_scope());
        return (prefix.size() > 0 ? (prefix + ".") : "") + name;
    }
    else if (isSgFunctionDefinition(definition -> get_scope()) || definition -> get_declaration() -> get_type() -> attributeExists("is_parameter_type")) {
        return name;
    }

    ROSE_ASSERT(false /* && definition -> get_scope() -> class_name() */);

    return "";
}

//
//
//
string getFullyQualifiedTypeName(SgClassType *class_type) {
    AstRegExAttribute *attribute = (AstRegExAttribute *) class_type -> getAttribute("name");
    if (! attribute) {
        SgClassDeclaration *declaration = isSgClassDeclaration(class_type -> get_declaration());
        ROSE_ASSERT(declaration);
        SgClassDeclaration *defining_declaration = isSgClassDeclaration(declaration -> get_definingDeclaration());
        ROSE_ASSERT(defining_declaration);
        SgClassDefinition *definition = defining_declaration -> get_definition();
        ROSE_ASSERT(definition);
        attribute = new AstRegExAttribute(getFullyQualifiedName(definition));
        class_type -> setAttribute("name", attribute);
    }

    return attribute -> expression;
}

string getFullyQualifiedTypeName(SgJavaParameterizedType *parm_type) {
    ROSE_ASSERT(isSgClassType(parm_type -> get_raw_type()));
    string result = getFullyQualifiedTypeName(isSgClassType(parm_type -> get_raw_type()));

    result += "<";
    SgTemplateParameterPtrList arg_list = parm_type -> get_type_list() -> get_args();
    for (int i = 0; i < arg_list.size(); i++) {
        SgTemplateParameter *templateParameter = arg_list[i];
        SgType *argument_type = templateParameter -> get_type();
        SgJavaParameterizedType *p_type = isSgJavaParameterizedType(argument_type);
        SgClassType *c_type = isSgClassType(argument_type);
        result += (p_type ? getFullyQualifiedTypeName(p_type)
                          : c_type ? getFullyQualifiedTypeName(c_type)
                                   : getTypeName(argument_type));
        if (i + 1 < arg_list.size()) {
            result += ", ";
        }
    }
    result += ">";

    return result;
}


string getTypeName(SgClassType *class_type) {
    if (class_type -> attributeExists("has_conflicts")) // a class known to be in conflict based on the on-demand imported packages?
        return getFullyQualifiedTypeName(class_type);

    SgName type_name = class_type -> get_name();
    if (class_type -> attributeExists("is_parameter_type") || isImportedType(class_type)) // a parameter or an imported type?
        return type_name.getString();

    string result = type_name.getString(),
           last_classname = result;

    SgClassSymbol *class_symbol = lookupTypeSymbol(type_name);
    if (class_symbol && class_symbol -> get_type() == class_type) { // the type is visible
        return result;
    }

    SgScopeStatement *scope;
    SgClassDeclaration *class_declaration = isSgClassDeclaration(class_type -> get_declaration());
    ROSE_ASSERT(class_declaration);
    for (scope = class_declaration -> get_definingDeclaration() -> get_scope(); scope != ::globalScope; scope = class_declaration -> get_scope()) {
        SgClassDefinition *class_definition = isSgClassDefinition(scope);
        if ((! class_definition) || class_definition == astJavaScopeStack.top()) {
            break;
        }

        class_declaration = isSgClassDeclaration(class_definition -> get_parent());
        ROSE_ASSERT(class_declaration);

        last_classname = class_declaration -> get_name().getString();
        result = last_classname + "." + result;

        if (isImportedType(class_declaration -> get_type())) {
            break;
        }
    }

    return (scope == ::globalScope ? getFullyQualifiedTypeName(class_type) : result);
}


string getTypeName(SgJavaParameterizedType *parm_type) {
    ROSE_ASSERT(isSgClassType(parm_type -> get_raw_type()));
    string result = getTypeName(isSgClassType(parm_type -> get_raw_type()));

    result += "<";
    SgTemplateParameterPtrList arg_list = parm_type -> get_type_list() -> get_args();
    for (int i = 0; i < arg_list.size(); i++) {
        SgTemplateParameter *templateParameter = arg_list[i];
        SgType *argument_type = templateParameter -> get_type();
        result += getTypeName(argument_type);
        if (i + 1 < arg_list.size()) {
            result += ", ";
        }
    }
    result += ">";

    return result;
}


//
//
//
string getTypeName(SgType *type) {
    SgJavaParameterizedType *parm_type = isSgJavaParameterizedType(type); 
    SgClassType *class_type = isSgClassType(type);
    SgPointerType *pointer_type = isSgPointerType(type);
    SgJavaWildcardType *wild_type = isSgJavaWildcardType(type);
    string result;

    if (parm_type) {
         result = getTypeName(parm_type);
    }
    else if (class_type) {
         result = getTypeName(class_type);
    }
    else if (pointer_type) {
         result = getTypeName(pointer_type -> get_base_type()) + "[]";
    }
    else if (wild_type) {
         result = getWildcardTypeName(wild_type);
    }
    else result = getPrimitiveTypeName(type);

    return result;
}

//
// Replace newline character by its escape character sequence.
//
// TODO: PC Question: Shouldn't the unparser be doing this as it is already processing
//                    other escape sequences such as \" and \'.
//
string normalize(string source) {
    string target = "";
    for (string::iterator it = source.begin(); it < source.end(); it++) {
        switch(*it) {
            case '\0':
                target += "\\0";
                break;
            case '\n':
                target += "\\n";
                break;
            case '\r':
                target += "\\r";
                break;
            case '\\':
                target += "\\\\";
                break;
            default:
                target += (*it);
                break;
        }
    }

    return target;
}

// 
// Global stack of scopes.
// 
extern ScopeStack astJavaScopeStack;

// 
// Global stack of expressions and statements
// 
ComponentStack astJavaComponentStack;

// 
// 
// 
string getCurrentJavaFilename() {
    ROSE_ASSERT(::globalScope != NULL);
    SgSourceFile *sourceFile = isSgSourceFile(::globalScope -> get_parent());
    ROSE_ASSERT(sourceFile != NULL);

    return sourceFile -> getFileName();
 }

/*
 * Wrapper to create an Sg_File_Info from line/col info
 */
Sg_File_Info *createSgFileInfo(int line, int col) {
    // Sg_File_Info *sg_fi = Sg_File_Info::generateDefaultFileInfo();
    Sg_File_Info *sg_fi = new Sg_File_Info(getCurrentJavaFilename(), line, col);
    // sg_fi -> set_line(line);
    // sg_fi -> set_col(col);

    ROSE_ASSERT(sg_fi -> isTransformation()    == false);
    ROSE_ASSERT(sg_fi -> isCompilerGenerated() == false);

    if (line == 0 && col == 0) {
        if (SgProject::get_verbose() > 2)
            printf ("Found source position info (line == 0 && col == 0) indicating compiler generated code \n");

        sg_fi -> setCompilerGenerated();

        // sg_fi -> display("Found source position info (line == 0 && col == 0) indicating compiler generated code");
    }

    return sg_fi;
}


//
//
//
void setJavaSourcePosition(SgLocatedNode*locatedNode, JavaSourceCodePosition *posInfo) {
    // This function sets the source position if java position information has been provided
    // (posInfo != NULL), otherwise it is marked as not available.
    // These nodes WILL be unparsed in the code generation phase.
    // if (posInfo -> getLineStart() == 0) {
    if (posInfo -> getLineEnd() == 0) {
        if (locatedNode -> get_startOfConstruct() == NULL){
            locatedNode -> set_startOfConstruct(Sg_File_Info::generateDefaultFileInfoForCompilerGeneratedNode());
        }
        if (locatedNode -> get_endOfConstruct() == NULL){
            locatedNode -> set_endOfConstruct(Sg_File_Info::generateDefaultFileInfoForCompilerGeneratedNode());
        }
        setJavaSourcePositionUnavailableInFrontend(locatedNode);
        return;
    }

    // The SgLocatedNode has both a startOfConstruct and endOfConstruct source position.
    ROSE_ASSERT(locatedNode != NULL);

    // Make sure we never try to reset the source position of the global scope (set elsewhere in ROSE).
    ROSE_ASSERT(isSgGlobal(locatedNode) == NULL);

    // Check the endOfConstruct first since it is most likely NULL (helpful in debugging)
    if (locatedNode -> get_endOfConstruct() != NULL || locatedNode -> get_startOfConstruct() != NULL) {
        if (SgProject::get_verbose() > 1) {
            printf ("In setSourcePosition(SgLocatedNode *locatedNode): Warning about existing file info data at locatedNode = %p = %s \n", locatedNode, locatedNode -> class_name().c_str());
        }

        if (locatedNode -> get_startOfConstruct() != NULL) {
            delete locatedNode -> get_startOfConstruct();
            locatedNode -> set_startOfConstruct(NULL);
        }

        if (locatedNode -> get_endOfConstruct() != NULL) {
            delete locatedNode -> get_endOfConstruct();
            locatedNode -> set_endOfConstruct(NULL);
        }
    }

    // DQ (8/16/2011): Added support for setting the operator source code position 
    // (Note that for expressions get_file_info() returns get_operatorPosition()).
    SgExpression *expression = isSgExpression(locatedNode);
    if (expression != NULL) {
        if (expression -> get_operatorPosition() != NULL) {
            delete expression -> get_operatorPosition();
            expression -> set_operatorPosition(NULL);
        }
    }

    if (posInfo == NULL) {
        // Call a mechanism defined in the SageInterface support

        printf ("ERROR: JavaSourceCodePosition *posInfo == NULL triggering use of SageInterface::setSourcePosition() (locatedNode = %p = %s) \n", locatedNode, locatedNode -> class_name().c_str());
        ROSE_ASSERT(false);

        SageInterface::setSourcePosition(locatedNode);
    } 
    else {
        // java position info is available
        Sg_File_Info *start_fileInfo = createSgFileInfo(posInfo -> getLineStart(), posInfo -> getColStart());
        Sg_File_Info *end_fileInfo   = createSgFileInfo(posInfo -> getLineEnd(), posInfo -> getColEnd());

        ROSE_ASSERT(start_fileInfo -> isTransformation() == false);
        ROSE_ASSERT(end_fileInfo -> isTransformation() == false);

        // updating the sgnode
        locatedNode -> set_startOfConstruct(start_fileInfo);
        locatedNode -> set_endOfConstruct(end_fileInfo);

        ROSE_ASSERT(start_fileInfo -> isTransformation() == false);
        ROSE_ASSERT(end_fileInfo -> isTransformation() == false);

        ROSE_ASSERT(locatedNode -> get_startOfConstruct() -> isTransformation() == false);
        ROSE_ASSERT(locatedNode -> get_endOfConstruct() -> isTransformation() == false);

        // DQ (8/16/2011): Added support for setting the operator source code position 
        // (Note that for expressions get_file_info() returns get_operatorPosition()).
        // SgExpression *expression = isSgExpression(locatedNode);
        if (expression != NULL) {
            Sg_File_Info *operator_fileInfo = createSgFileInfo(posInfo -> getLineStart(), posInfo -> getColStart());
            expression -> set_operatorPosition(operator_fileInfo);

            ROSE_ASSERT(locatedNode -> get_file_info() -> isTransformation() == false);
        }

        ROSE_ASSERT(locatedNode -> get_file_info() -> isTransformation() == false);
    }

    ROSE_ASSERT(locatedNode -> get_file_info() -> isTransformation() == false);
    ROSE_ASSERT(locatedNode -> get_startOfConstruct() -> isTransformation() == false);
    ROSE_ASSERT(locatedNode -> get_endOfConstruct() -> isTransformation() == false);
}

//
//
//
void setJavaSourcePosition(SgLocatedNode *locatedNode, JNIEnv *env, jobject jToken) {
    setJavaSourcePosition(locatedNode, convert_Java_token(env, jToken) -> getSourcecodePosition());
}


//
//
//
void setJavaSourcePositionUnavailableInFrontend(SgLocatedNode *locatedNode) {
    ROSE_ASSERT(locatedNode != NULL);
    ROSE_ASSERT(locatedNode -> get_startOfConstruct() != NULL);
    ROSE_ASSERT(locatedNode -> get_endOfConstruct()   != NULL);

    // This is redundant for non-expression IR nodes.
    ROSE_ASSERT(locatedNode -> get_file_info() != NULL);

    locatedNode -> get_startOfConstruct() -> setSourcePositionUnavailableInFrontend();
    locatedNode -> get_endOfConstruct() -> setSourcePositionUnavailableInFrontend();

    locatedNode -> get_startOfConstruct() -> setOutputInCodeGeneration();
    locatedNode -> get_endOfConstruct() -> setOutputInCodeGeneration();

    locatedNode -> get_startOfConstruct() -> unsetTransformation();
    locatedNode -> get_endOfConstruct() -> unsetTransformation();

    locatedNode -> get_startOfConstruct() -> unsetCompilerGenerated();
    locatedNode -> get_endOfConstruct() -> unsetCompilerGenerated();

    // DQ (8/16/2011): Added support for setting the operator source code position 
    // (Note that for expressions get_file_info() returns get_operatorPosition()).
    SgExpression *expression = isSgExpression(locatedNode);
    if (expression != NULL) {
        ROSE_ASSERT(expression -> get_operatorPosition() != NULL);
        expression -> get_operatorPosition() -> setSourcePositionUnavailableInFrontend();
        expression -> get_operatorPosition() -> setOutputInCodeGeneration();

        expression -> get_operatorPosition() -> unsetTransformation();
        expression -> get_operatorPosition() -> unsetCompilerGenerated();
    }
}


//
// TODO: DO this right at some point !!!  In particular, this should produce a wstring ...
//
string convertJavaStringValToWString(JNIEnv *env, const jstring &java_string) {
    std::string value;

    const jchar *raw = env -> GetStringChars(java_string, NULL);
    if (raw != NULL) {
        jsize len = env -> GetStringLength(java_string);
        for (const jchar *temp = raw; len > 0; len--,temp++) {
            if (*temp > 127) {
                // TODO: Do the right thing!
                if (! ::globalScope -> attributeExists("contains_wide_characters")) {
                    ostringstream convert;     // stream used for the conversion
                    convert << ((int) *temp); // insert the textual representation of num_dimensions in the characters in the stream
                    ::globalScope -> setAttribute("contains_wide_characters", new AstRegExAttribute(convert.str()));
                }
                value += ' ';
            }
            else value += *temp;
        }
        env -> ReleaseStringChars(java_string, raw);
    }

// TODO: Remove this !!!
/*
    const char *str = env -> GetStringUTFChars(java_string, NULL);
    value =  str;
cout << "The converted string is: \"";
for (int i = 0; i < value.size(); i++)
cout << str[i];
cout << "\"" << endl;
*/
    return normalize(value);
}


string convertJavaStringToCxxString(JNIEnv *env, const jstring &java_string) {
     // Note that "env" can't be passed into this function as "const".
    const char *str = env -> GetStringUTFChars(java_string, NULL);
    ROSE_ASSERT(str != NULL);

    string returnString = str;

    // printf ("Inside of convertJavaStringToCxxString s = %s \n", str);

    // Note that str is not set to NULL.
    env -> ReleaseStringUTFChars(java_string, str);

    return normalize(returnString);
}


//
//
//
string convertJavaPackageNameToCxxString(JNIEnv *env, const jstring &java_string) {
    string package_name =  convertJavaStringToCxxString(env, java_string);
    replace(package_name.begin(), package_name.end(), '.', '_');
    return package_name;
}

SgClassDeclaration *buildDefiningClassDeclaration(SgName class_name, SgScopeStatement *scope) {
     SgClassDeclaration* nonDefiningDecl              = NULL;
     bool buildTemplateInstantiation                  = false;
     SgTemplateArgumentPtrList* templateArgumentsList = NULL;

     SgClassDeclaration* declaration = SageBuilder::buildClassDeclaration_nfi(class_name, SgClassDeclaration::e_class, scope, nonDefiningDecl /*, buildTemplateInstantiation, templateArgumentsList */);
     ROSE_ASSERT(declaration != NULL);
     declaration -> set_parent(scope);
     declaration -> set_scope(scope);

     return declaration;
}


SgClassDefinition *findOrInsertPackage(const SgName &original_package_name, const SgName &converted_package_name, JNIEnv *env, jobject loc) {
    SgClassSymbol *namespace_symbol = ::globalScope -> lookup_class_symbol(converted_package_name);
    SgClassDeclaration *declaration;
    if (namespace_symbol == NULL) {
        declaration = buildDefiningClassDeclaration(converted_package_name, ::globalScope); // SageBuilder::buildDefiningClassDeclaration(converted_package_name, ::globalScope);
        declaration -> setAttribute("namespace", new AstRegExAttribute(original_package_name));
        SgClassDefinition *definition = declaration -> get_definition();
        ROSE_ASSERT(definition);
        definition -> setAttribute("namespace", new AstRegExAttribute(original_package_name));
        ::globalScope -> append_declaration(declaration);
        declaration -> set_parent(::globalScope);

        setJavaSourcePosition(declaration, env, loc);
        setJavaSourcePosition(definition, env, loc);
    }
    else {
        declaration = (SgClassDeclaration *) namespace_symbol -> get_declaration() -> get_definingDeclaration();
    }

    ROSE_ASSERT(declaration);

    return declaration -> get_definition();
}


SgMemberFunctionDeclaration *buildDefiningMemberFunction(const SgName &inputName, SgClassDefinition *class_definition, int num_arguments, JNIEnv *env, jobject method_location, jobject args_location) {
    if (SgProject::get_verbose() > 0)
        printf ("Inside of buildDefiningMemberFunction(): name = %s in scope = %p = %s = %s \n", inputName.str(), class_definition, class_definition -> class_name().c_str(), class_definition -> get_declaration() -> get_name().str());

    // This is abstracted so that we can build member functions as require to define Java specific default functions (e.g. super()).

    ROSE_ASSERT(class_definition != NULL);
    ROSE_ASSERT(class_definition -> get_declaration() != NULL);

    SgFunctionParameterTypeList *typeList = SageBuilder::buildFunctionParameterTypeList();
    ROSE_ASSERT(typeList != NULL);

    SgFunctionParameterList *parameterlist =  SageBuilder::buildFunctionParameterList();
    ROSE_ASSERT(parameterlist != NULL);

    // Loop over the types in the astJavaComponentStack (the rest of the stack).
    list<Sg_File_Info *> startLocation,
                         endLocation;
    list<SgInitializedName *> names;
    for (int i = 0; i < num_arguments; i++) { // charles4 10/12/2011: Reverse the content of the stack.
        SgNode *node = astJavaComponentStack.pop();
        SgInitializedName *initializedName = isSgInitializedName(node);
        ROSE_ASSERT(initializedName);
        names.push_front(initializedName);
        startLocation.push_front(initializedName -> get_startOfConstruct());
        endLocation.push_front(initializedName -> get_endOfConstruct());
    }

    // charles4 10/12/2011: Now, iterate over the list in the proper order
    while (! names.empty()) {
        SgInitializedName *initializedName = names.front();
        ROSE_ASSERT(initializedName != NULL);
        names.pop_front();

        SgType *parameterType = initializedName -> get_type();
        ROSE_ASSERT(parameterType != NULL);

        typeList -> append_argument(parameterType);

        parameterlist -> append_arg(initializedName);
        initializedName -> set_parent(parameterlist);
    }
// TODO: Remove this !!!
/*
cout << "Adding function " 
<< name
<< " in type "
<< class_definition -> get_qualified_name()
<< " with parameter types: (";
 SgTypePtrList::iterator i = typeList -> get_arguments().begin();
 if (i != typeList -> get_arguments().end()) {
cout << getTypeName(*i);
 for (i++; i != typeList -> get_arguments().end(); i++) {
cout << ", " << getTypeName(*i);
}
}
cout << ")"
<< endl;
cout.flush();
*/
    // This is the return type for the member function (top of the stack).
    SgType *return_type = astJavaComponentStack.popType();
    ROSE_ASSERT(return_type != NULL);

    // Specify if this is const, volatile, or restrict (0 implies normal member function).
    unsigned int mfunc_specifier = 0;
    SgMemberFunctionType *member_function_type = SageBuilder::buildMemberFunctionType(return_type, typeList, class_definition, mfunc_specifier);
    ROSE_ASSERT(member_function_type != NULL);

    // parameterlist = SageBuilder::buildFunctionParameterList(typeList);
    ROSE_ASSERT(parameterlist != NULL);

    //
    // PC: This needs to be reviewed.  Is it still needed?
    //
    // DQ (3/24/2011): Currently we am introducing a mechanism to make sure that overloaded function will have 
    // a unique name. It is temporary until we can handle correct mangled name support using the argument types.
    SgName name = inputName;
    SgFunctionSymbol *func_symbol = NULL;
    bool func_symbol_found = true;
    while (func_symbol_found == true) {
        // DQ (3/24/2011): This function should not already exist (else it should be an error).
        func_symbol = class_definition -> lookup_function_symbol(name, member_function_type);
        // ROSE_ASSERT(func_symbol == NULL);

        if (func_symbol != NULL) {
            func_symbol_found = true;

            // This is a temporary mean to force overloaded functions to have unique names.
            name += "_overloaded_";
            if (SgProject::get_verbose() > 0)
                printf ("Using a temporary mean to force overloaded functions to have unique names (name = %s) \n", name.str());
        }
        else {
            func_symbol_found = false;
        }
    }
    ROSE_ASSERT(name.getString().compare(inputName.getString()) == 0); // PC - 04-03-13 - added this check because I don't understand the reason for the code above.

    //SgMemberFunctionDeclaration*
    //buildNondefiningMemberFunctionDeclaration (const SgName & name, SgType* return_type, SgFunctionParameterList *parlist, SgScopeStatement* scope, SgExprListExp* decoratorList, unsigned int functionConstVolatileFlags, bool buildTemplateInstantiation, SgTemplateArgumentPtrList* templateArgumentsList);
    //
    // TODO: This line is needed for EDG4
    //    SgMemberFunctionDeclaration *nondefining_function_declaration = SageBuilder::buildNondefiningMemberFunctionDeclaration(name, return_type, parameterlist, class_definition, NULL, 0, false, NULL);

    //
    //SgMemberFunctionDeclaration*
    //buildDefiningMemberFunctionDeclaration (const SgName & name, SgType* return_type, SgFunctionParameterList *parlist, SgScopeStatement* scope, SgExprListExp* decoratorList, bool buildTemplateInstantiation, unsigned int functionConstVolatileFlags, SgMemberFunctionDeclaration* first_nondefinng_declaration, SgTemplateArgumentPtrList* templateArgumentsList);
    //
    // TODO: This line is needed for EDG4
    //    SgMemberFunctionDeclaration *function_declaration = SageBuilder::buildDefiningMemberFunctionDeclaration(name, return_type, parameterlist, class_definition, NULL, false, 0, nondefining_function_declaration, NULL);

    // TODO: Remove the EDG3 line below.
    SgMemberFunctionDeclaration *function_declaration = SageBuilder::buildDefiningMemberFunctionDeclaration(name, member_function_type, parameterlist, class_definition, NULL);
    ROSE_ASSERT(function_declaration);

    vector<SgInitializedName *> args = function_declaration -> get_args();
    setJavaSourcePosition(parameterlist, env, args_location);
    for (vector<SgInitializedName *>::iterator name_it = args.begin(); name_it != args.end(); name_it++) {
        SgInitializedName *locatedNode = *name_it;
        ROSE_ASSERT(! startLocation.empty());
        ROSE_ASSERT(! endLocation.empty());
        locatedNode -> set_startOfConstruct(startLocation.front());
        locatedNode -> set_endOfConstruct(endLocation.front());
        startLocation.pop_front();
        endLocation.pop_front();
    }
    ROSE_ASSERT(startLocation.empty());
    ROSE_ASSERT(endLocation.empty());
    ROSE_ASSERT(function_declaration != NULL);
    ROSE_ASSERT(function_declaration -> get_definingDeclaration() != NULL);
    ROSE_ASSERT(function_declaration -> get_definition() != NULL);

    setJavaSourcePosition(function_declaration -> get_definition(), env, method_location);

    return function_declaration;
}


/**
 * Although iterating over the methods this way appears to violate the Java spec rule that instructs
 * the compiler to "...get the list of methods seen from the class and get the most specific one...",
 * it is in fact correct since ECJ has already chosen the correct function and what we are doing
 * here is to look for a "perfect" mach with the function that was chosen.
 */
SgMemberFunctionDeclaration *findMemberFunctionDeclarationInClass(SgClassDefinition *class_definition, const SgName &function_name, list<SgType *>& formal_types) {
    SgMemberFunctionDeclaration *method_declaration = lookupMemberFunctionDeclarationInClassScope(class_definition, function_name, formal_types);
    if (method_declaration == NULL) {
        const SgBaseClassPtrList &inheritance = class_definition->get_inheritances();
        for (SgBaseClassPtrList::const_iterator it = inheritance.begin(); method_declaration == NULL && it != inheritance.end(); it++) { // Iterate over super class, if any, then the interfaces, if any.
            SgClassDeclaration *decl = (*it) -> get_base_class();
// TODO: Remove this !
/*
cout << "Looking for method "
     << function_name.getString()
     << " in class "
     << decl -> get_definition() -> get_qualified_name()
     << endl;
cout.flush();
*/
            method_declaration = findMemberFunctionDeclarationInClass(decl -> get_definition(), function_name, formal_types);
        }
    }
    return method_declaration;
}

/**
 * Lookup a member function in current class only (doesn't look in super and interfaces classes)
 */
SgMemberFunctionDeclaration *lookupMemberFunctionDeclarationInClassScope(SgClassDefinition *class_definition, const SgName &function_name, list<SgType *>& types) {
    int num_arguments = types.size();
    SgMemberFunctionDeclaration *method_declaration = NULL;
    vector<SgDeclarationStatement *> declarations = class_definition -> get_members();
    for (int i = 0; i < declarations.size(); i++, method_declaration = NULL) {
        SgDeclarationStatement *declaration = declarations[i];
        method_declaration = isSgMemberFunctionDeclaration(declaration);
        if (method_declaration && method_declaration -> get_name().getString().compare(function_name) == 0) {
            vector<SgInitializedName *> args = method_declaration -> get_args();
            if (args.size() == num_arguments) {
                list<SgType *>::const_iterator j = types.begin();
                int k;
                for (k = 0; k < num_arguments; k++, j++) {
                    SgType *type = (*j);
                    if (! isCompatibleTypes(type, args[k] -> get_type())) {
                        // Not all types are compatible, continue to look
                        break;
                    }
                }

                if (k == num_arguments) {// all the arguments match?
                    break;
                }
            }
        }
    }

    return method_declaration;
}

SgMemberFunctionDeclaration *lookupMemberFunctionDeclarationInClassScope(SgClassDefinition *class_definition, const SgName &function_name, int num_arguments) {
    ROSE_ASSERT(class_definition != NULL);

    // Loop over the types in the astJavaComponentStack (the rest of the stack).
    list<SgType *> types;
    for (int i = 0; i < num_arguments; i++) { // charles4 10/12/2011: Reverse the content of the stack.
        SgType *type = astJavaComponentStack.popType();
        types.push_front(type);
    }

    SgType *return_type = astJavaComponentStack.popType(); // Remove the return type ... We don't need it!
    ROSE_ASSERT(return_type != NULL);

    SgMemberFunctionDeclaration *method_declaration = NULL;
    method_declaration = lookupMemberFunctionDeclarationInClassScope(class_definition, function_name, types);
// TODO: REMOVE THIS !
if (!method_declaration){
cout << "Could not find function " << function_name.getString() << "(";
std::list<SgType*>::iterator i = types.begin();
if (i != types.end()) {
cout << getTypeName(*i);
for (i++; i != types.end(); i++) {
cout << ", " << getTypeName(*i);
}
}
cout << ") in class " 
<< class_definition -> get_qualified_name()
<< endl;
cout.flush();
}
    ROSE_ASSERT(method_declaration != NULL);

    return method_declaration;
}

SgMemberFunctionSymbol *findFunctionSymbolInClass(SgClassDefinition *class_definition, const SgName &function_name, list<SgType *> &formal_types) {
    ROSE_ASSERT(class_definition != NULL);

    SgMemberFunctionDeclaration *method_declaration = findMemberFunctionDeclarationInClass(class_definition, function_name, formal_types);
    if (method_declaration == NULL) {
        method_declaration = lookupMemberFunctionDeclarationInClassScope(ObjectClassDefinition, function_name, formal_types);
    }

// TODO: Remove this !!!
if (!method_declaration){
cout << "Could not find function " << function_name.getString() << "(";
std::list<SgType*>::iterator i = formal_types.begin();
if (i != formal_types.end()) {
cout << getTypeName(*i);
for (i++; i != formal_types.end(); i++) {
cout << ", " << getTypeName(*i);
}
}
cout << ") in class " 
<< class_definition -> get_qualified_name()
<< endl;
cout.flush();
}

    ROSE_ASSERT(method_declaration);

    SgSymbol *symbol =  method_declaration -> search_for_symbol_from_symbol_table();
    ROSE_ASSERT(symbol);
    SgMemberFunctionSymbol *function_symbol = isSgMemberFunctionSymbol(symbol);
    ROSE_ASSERT(function_symbol);

    return function_symbol;
}


list<SgName> generateQualifierList (const SgName &classNameWithQualification) {
    list<SgName> returnList;
    SgName classNameWithoutQualification;

    classNameWithoutQualification = classNameWithQualification;

    // Names of implicitly defined classes have names that start with "java." and these have to be translated.
    string original_classNameString = classNameWithQualification.str();
    string classNameString = classNameWithQualification.str();

    // Also replace '.' with '_'
    replace(classNameString.begin(), classNameString.end(), '.', '_');

    // Also replace '$' with '_' (not clear on what '$' means yet (something related to inner and outer class nesting).
    replace(classNameString.begin(), classNameString.end(), '$', '_');

    SgName name = classNameString;

    // We should not have a '.' in the class name.  Or it will fail the current ROSE name mangling tests.
    ROSE_ASSERT(classNameString.find('.') == string::npos);

    // DQ (3/20/2011): Detect use of '$' in class names. Current best reference 
    // is: http://www.java-forums.org/new-java/27577-specific-syntax-java-util-regex-pattern-node.html
    ROSE_ASSERT(classNameString.find('$') == string::npos);

    // Parse the original_classNameString to a list of what will be classes.
    size_t lastPosition = 0;
    size_t position = original_classNameString.find('.', lastPosition);
    while (position != string::npos) {
        string parentClassName = original_classNameString.substr(lastPosition, position - lastPosition);
        if (SgProject::get_verbose() > 0)
            printf ("parentClassName = %s \n", parentClassName.c_str());

        returnList.push_back(parentClassName);

        lastPosition = position+1;
        position = original_classNameString.find('.', lastPosition);
        if (SgProject::get_verbose() > 0)
            printf ("lastPosition = %zu position = %zu \n", lastPosition, position);
    }

    string className = original_classNameString.substr(lastPosition, position - lastPosition);

    if (SgProject::get_verbose() > 0)
        printf ("className for implicit (leaf) class = %s \n", className.c_str());

    // Reset the name for the most inner nested implicit class.  This allows a class such as "java.lang.System" 
    // to be build as "System" inside of "class "lang" inside of class "java" (without resetting the name we 
    // would have "java.lang.System" inside of "class "lang" inside of class "java").
    name = className;

    if (SgProject::get_verbose() > 0)
        printf ("last name = %s \n", name.str());

    // Push the last name onto the list.
    returnList.push_back(name);

    if (SgProject::get_verbose() > 0)
        printf ("returnList.size() = %zu \n", returnList.size());

    return returnList;
}


SgClassSymbol *lookupSimpleNameTypeInClass(const SgName &name, SgClassDefinition *class_definition) {
    ROSE_ASSERT(class_definition);
    ROSE_ASSERT(class_definition -> get_declaration());

cout << "lookupSimpleNameTypeInClass 1 " << endl;
#if 0
    SgClassSymbol *symbol = class_definition -> lookup_class_symbol(name);
#else
	SgClassSymbol *symbol = (SgClassSymbol *)globalScope->lookup_symbol(name);
#endif
cout << "lookupSimpleNameTypeInClass 2 " << symbol << endl;
    vector<SgBaseClass *> &inheritances = class_definition -> get_inheritances();
cout << "lookupSimpleNameTypeInClass 3 " << endl;
    for (int k = 0; symbol == NULL && k < (int) inheritances.size(); k++) {
        SgClassDeclaration *super_declaration = inheritances[k] -> get_base_class();
        class_definition = super_declaration -> get_definition(); // get the super class definition
        symbol = lookupSimpleNameTypeInClass(name, class_definition);
    }
cout << "lookupSimpleNameTypeInClass 4 " << endl;

    if (symbol == NULL) {
cout << "lookupSimpleNameTypeInClass 5 " << name << endl;
        symbol = ::ObjectClassDefinition -> lookup_class_symbol(name);
cout << "lookupSimpleNameTypeInClass 6 " << endl;
    }
cout << "lookupSimpleNameTypeInClass 7 " << endl;

    return symbol;
}


SgVariableSymbol *lookupSimpleNameVariableInClass(const SgName &name, SgClassDefinition *class_definition) {
    ROSE_ASSERT(class_definition);
    ROSE_ASSERT(class_definition -> get_declaration());

    SgVariableSymbol *symbol = class_definition -> lookup_variable_symbol(name);
    vector<SgBaseClass *> &inheritances = class_definition -> get_inheritances();
    for (int k = 0; symbol == NULL && k < (int) inheritances.size(); k++) {
        SgClassDeclaration *super_declaration = inheritances[k] -> get_base_class();
        class_definition = super_declaration -> get_definition(); // get the super class definition
        symbol = lookupSimpleNameVariableInClass(name, class_definition);
    }

    if (symbol == NULL) {
        symbol = ::ObjectClassDefinition -> lookup_variable_symbol(name);
    }

    return symbol;
}


//
// Search the scope stack for a variable declaration for the name in question.
//
SgVariableSymbol *lookupVariableByName(const SgName &name) {
    ROSE_ASSERT(! astJavaScopeStack.empty());

    SgSymbol *symbol = NULL;

    //
    // Iterate over the scope stack... At each point, look to see if the variable is there.
    // Note that in the case of a class, we recursively search the class as well as its
    // super class and interfaces.
    //
    for (std::list<SgScopeStatement*>::iterator i = astJavaScopeStack.begin(); (symbol == NULL || (! isSgVariableSymbol(symbol))) && i != astJavaScopeStack.end(); i++) {
        symbol = (isSgClassDefinition(*i)
                      ? lookupSimpleNameVariableInClass(name, (SgClassDefinition *) (*i))
                      : (*i) -> lookup_symbol(name));
        if ((*i) == ::globalScope)
            break;
    }

    return isSgVariableSymbol(symbol);
}


//
// Search the scope stack for a variable declaration for the name in question.
//
SgJavaLabelSymbol *lookupLabelByName(const SgName &name) {
    ROSE_ASSERT(! astJavaScopeStack.empty());

    SgSymbol *symbol = NULL;

    //
    // Iterate over the scope stack... At each point, look to see if the variable is there.
    // Note that in the case of a class, we recursively search the class as well as its
    // super class and interfaces.
    //
    for (std::list<SgScopeStatement*>::iterator i = astJavaScopeStack.begin(); (symbol == NULL || (! isSgJavaLabelSymbol(symbol))) && i != astJavaScopeStack.end(); i++) {
        if (isSgClassDefinition(*i))
            break;
        symbol = (*i) -> lookup_symbol(name);
        if ((*i) == ::globalScope)
            break;
    }

    return isSgJavaLabelSymbol(symbol);
}



SgClassSymbol *lookupTypeSymbol(SgName &type_name) {
    SgClassSymbol *class_symbol = NULL;
cout << "java_support.C lookupTypeSymbol 1 " << type_name << endl;
    //
    // Iterate over the scope stack... At each point, look to see if the variable is there.
    // Note that in the case of a class, we recursively search the class as well as its
    // super class and interfaces.
    //
    for (std::list<SgScopeStatement*>::iterator i = astJavaScopeStack.begin(); class_symbol == NULL && i != astJavaScopeStack.end(); i++) {
cout << "java_support.C lookupTypeSymbol 2" << *i << endl;
// TODO: Remove this!
/*
cout << "Looking for type parameter "
<< (type_name)
<< " in "
<< (isSgClassDefinition(*i) ? isSgClassDefinition(*i) -> get_qualified_name().getString()
                            : isSgFunctionDefinition(*i) ? isSgFunctionDefinition(*i) -> get_qualified_name().getString()
                                                         : (*i) -> class_name())
<< endl;
SgSymbol *symbol = (*i) -> lookup_class_symbol(type_name);
if (symbol != NULL) cout << "symbol " << symbol -> class_name() << " was found for " << (type_name).getString() << endl;
cout.flush();
*/

        class_symbol = (isSgClassDefinition(*i)
                            ? lookupSimpleNameTypeInClass((type_name), (SgClassDefinition *) (*i))
                            : (*i) -> lookup_class_symbol(type_name));
cout << "java_support.C lookupTypeSymbol 3" << endl;
        if ((*i) == ::globalScope)
            break;
    }
    // 
    // If the class_symbol still has not been found, look for it in java.lang! -> should be fixed to find x10.lang? 
    // At this point, the type_symbol in qustion must be a class_symbol.
    //
    if (class_symbol == NULL) {
        SgClassSymbol *namespace_symbol = ::globalScope -> lookup_class_symbol("java_lang");
        ROSE_ASSERT(namespace_symbol);
        SgClassDeclaration *declaration = (SgClassDeclaration *) namespace_symbol -> get_declaration() -> get_definingDeclaration();
        SgClassDefinition *package = declaration -> get_definition();
        ROSE_ASSERT(package);
        class_symbol = package -> lookup_class_symbol(type_name);
    }

cout << "java_support.C lookupTypeSymbol 4" << endl;
    return class_symbol;
}


SgType *lookupTypeByName(SgName &package_name, SgName &type_name, int num_dimensions) {
    SgType *type = NULL;
 
   list<SgName> qualifiedTypeName = generateQualifierList(type_name);

    ROSE_ASSERT(! astJavaScopeStack.empty());
    ROSE_ASSERT(qualifiedTypeName.size());

    SgClassSymbol *class_symbol = NULL;

    list<SgName>::iterator name = qualifiedTypeName.begin();

    if (package_name.getString().size() == 0) {
        if (type_name.getString().compare("boolean") == 0) {
            type = SgTypeBool::createType();
        }
        else if (type_name.getString().compare("byte") == 0) {
            type = SgTypeSignedChar::createType();
        }
        else if (type_name.getString().compare("char") == 0) {
            type = SgTypeWchar::createType();
        }
        else if (type_name.getString().compare("int") == 0) {
            type = SgTypeInt::createType();
        }
        else if (type_name.getString().compare("short") == 0) {
            type = SgTypeShort::createType();
        }
        else if (type_name.getString().compare("float") == 0) {
            type = SgTypeFloat::createType();
        }
        else if (type_name.getString().compare("long") == 0) {
            type = SgTypeLong::createType();
        }
        else if (type_name.getString().compare("double") == 0) {
            type = SgTypeDouble::createType();
        }
        else if (type_name.getString().compare("void") == 0) {
            type = SgTypeVoid::createType();
        }
        else {
            class_symbol = lookupTypeSymbol(*name);
        }
    }
    else {
        SgClassSymbol *namespace_symbol = ::globalScope -> lookup_class_symbol(package_name);
// TODO: Remove this!!!
if (! namespace_symbol){
cout << "The namespace : " << package_name << " does not exist in the global scope."
     << endl;
cout.flush();
}

        ROSE_ASSERT(namespace_symbol);
        SgClassDeclaration *declaration = (SgClassDeclaration *) namespace_symbol -> get_declaration() -> get_definingDeclaration();
        SgClassDefinition *package = declaration -> get_definition();
        ROSE_ASSERT(package);
        class_symbol = package -> lookup_class_symbol(*name);
// TODO: Remove this!!!
if (! class_symbol){
cout << "The name is: " << *name << "; "
     << "The package symbol is: " << package -> get_qualified_name() << "; "
     << "No symbol found for " << package_name.str() << (package_name.getString().size() ? "." : "") << (*name) << endl;
cout.flush();
}
    }

    //
    // If we are dealing with an instantiable type, we only have a class_symbol at this point.
    //
    if (type == NULL) { // not a primitive type
// TODO: Remove this!!!
if (! class_symbol){
cout << "No symbol found for " << package_name.str() << (package_name.getString().size() ? "." : "") << type_name.str() << endl;
cout.flush();
cout << "Here is the stack: " << endl;
for (std::list<SgScopeStatement*>::iterator i = astJavaScopeStack.begin(); i != astJavaScopeStack.end(); i++) {
cout << "    "
<< (isSgClassDefinition(*i) ? isSgClassDefinition(*i) -> get_qualified_name().getString()
                            : isSgFunctionDefinition(*i) ? isSgFunctionDefinition(*i) -> get_qualified_name().getString()
                                                         : (*i) -> class_name())
<< endl;
cout.flush();
}
}
        ROSE_ASSERT(class_symbol);

        for (name++; name != qualifiedTypeName.end(); name++) {
            SgClassDeclaration *declaration = isSgClassDeclaration(class_symbol -> get_declaration() -> get_definingDeclaration());
            ROSE_ASSERT(declaration);
            SgClassDefinition *definition = declaration -> get_definition();
            class_symbol = lookupSimpleNameTypeInClass((*name), definition);
            ROSE_ASSERT(class_symbol);
        }

        type = class_symbol -> get_type();

// TODO: This looks like DEAD CODE !!!
/*
        SgClassType *class_type = isSgClassType(type);
        ROSE_ASSERT(class_type);
        getFullyQualifiedTypeName(class_type);
*/
    }

    //
    // If we are dealing with an array, build the Array type...
    //
    if (num_dimensions > 0) {
        type = getUniquePointerType(type, num_dimensions); // getUniqueArrayType(type, num_dimensions);
    }

    ROSE_ASSERT(type);

    return type;
}


//
//
//
SgClassDefinition *getCurrentTypeDefinition() {
    SgClassDefinition *class_definition = NULL;
    std::list<SgScopeStatement*>::iterator i = astJavaScopeStack.begin();
    while (i != astJavaScopeStack.end() && isSgClassDefinition(*i) == NULL) {
        i++;
    }

    if (i != astJavaScopeStack.end()) {
        class_definition = isSgClassDefinition(*i);
    }
    else {
        printf ("Error in getCurrentTypeDefinition(): SgTypeDefinition not found \n");
        ROSE_ASSERT(false);
    }

    ROSE_ASSERT(class_definition != NULL);

    return class_definition;
}


//
//
//
SgScopeStatement *get_scope_from_symbol(SgSymbol *symbol) {
    SgScopeStatement *currentScope = NULL;
    if (isSgClassSymbol(symbol)) {
        SgClassSymbol *class_symbol = (SgClassSymbol *) symbol;

        SgDeclarationStatement *declarationFromSymbol = class_symbol -> get_declaration();
        ROSE_ASSERT(declarationFromSymbol != NULL);

        SgClassDeclaration *class_declaration  = isSgClassDeclaration(declarationFromSymbol -> get_definingDeclaration());
        ROSE_ASSERT(class_declaration != NULL);

        currentScope = class_declaration -> get_definition();
    }
    else if (isSgNamespaceSymbol(symbol)) {
        SgNamespaceSymbol *namespace_symbol = (SgNamespaceSymbol *) symbol;

        SgDeclarationStatement *declarationFromSymbol = namespace_symbol -> get_declaration();
        ROSE_ASSERT(declarationFromSymbol != NULL);

        SgNamespaceDeclarationStatement *namespace_declaration  = isSgNamespaceDeclarationStatement(declarationFromSymbol -> get_definingDeclaration());
        ROSE_ASSERT(namespace_declaration != NULL);

        currentScope = namespace_declaration -> get_definition();
    }

    ROSE_ASSERT(currentScope != NULL);

    return currentScope;
}
