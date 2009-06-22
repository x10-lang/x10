%Recover
    ErrorId
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
