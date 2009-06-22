%Recover
    ErrorId
%End

%Notice
/.
////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2007 IBM Corporation.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
//Contributors:
//    Philippe Charles (pcharles@us.ibm.com) - initial API and implementation

////////////////////////////////////////////////////////////////////////////////
./
%End

%Rules
    TypeName ::= TypeName . ErrorId
            
    PackageName ::= PackageName . ErrorId
                
    ExpressionName ::= AmbiguousName . ErrorId
            
    MethodName ::= AmbiguousName . ErrorId
            
    PackageOrTypeName ::= PackageOrTypeName . ErrorId
            
    AmbiguousName ::= AmbiguousName . ErrorId
            
    FieldAccess ::= Primary . ErrorId
                          | super . ErrorId
                          | ClassName . super$sup . ErrorId
        
    MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
                               | MethodSuperPrefix ( ArgumentListopt )
                               | MethodClassNameSuperPrefix ( ArgumentListopt )
        
    MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            MethodSuperPrefix ::= super . ErrorId$ErrorId
            MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
        %End

%Types
    Object ::= MethodPrimaryPrefix
             | MethodClassNameSuperPrefix
    polyglot.lex.Identifier ::= MethodSuperPrefix
%End
