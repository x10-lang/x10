/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
/*****************************************************
 * WARNING!  THIS IS A GENERATED FILE.  DO NOT EDIT! *
 *****************************************************/

package x10.parser;

import lpg.runtime.*;

public class X10Parser implements RuleAction
{
    private PrsStream prsStream = null;
    
    private boolean unimplementedSymbolsWarning = false;

    private static ParseTable prsTable = new X10Parserprs();
    public ParseTable getParseTable() { return prsTable; }

    private BacktrackingParser btParser = null;
    public BacktrackingParser getParser() { return btParser; }

    private void setResult(Object object) { btParser.setSym1(object); }
    public Object getRhsSym(int i) { return btParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return btParser.getToken(i); }
    public IToken getRhsIToken(int i) { return prsStream.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return btParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return prsStream.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return btParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return prsStream.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return btParser.getFirstToken(); }
    public IToken getLeftIToken()  { return prsStream.getIToken(getLeftSpan()); }

    public int getRightSpan() { return btParser.getLastToken(); }
    public IToken getRightIToken() { return prsStream.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = btParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public void reset(ILexStream lexStream)
    {
        prsStream = new PrsStream(lexStream);
        btParser.reset(prsStream);

        try
        {
            prsStream.remapTerminalSymbols(orderedTerminalSymbols(), prsTable.getEoftSymbol());
        }
        catch (NullExportedSymbolsException e) {
        }
        catch (NullTerminalSymbolsException e) {
        }
        catch (UnimplementedTerminalsException e)
        {
            if (unimplementedSymbolsWarning) {
                java.util.ArrayList<Integer> unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = unimplemented_symbols.get(i);
                    System.out.println("    " + X10Parsersym.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();
            }
        }
        catch (UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 X10Parsersym.orderedTerminalSymbols[prsTable.getEoftSymbol()]));
        } 
    }
    
    public X10Parser()
    {
        try
        {
            btParser = new BacktrackingParser(prsStream, prsTable, (RuleAction) this);
        }
        catch (NotBacktrackParseTableException e)
        {
            throw new Error(new NotBacktrackParseTableException
                                ("Regenerate X10Parserprs.java with -BACKTRACK option"));
        }
        catch (BadParseSymFileException e)
        {
            throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- X10Parsersym.java"));
        }
    }
    
    public X10Parser(ILexStream lexStream)
    {
        this();
        reset(lexStream);
    }
    
    public int numTokenKinds() { return X10Parsersym.numTokenKinds; }
    public String[] orderedTerminalSymbols() { return X10Parsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return X10Parsersym.orderedTerminalSymbols[kind]; }
    public int getEOFTokenKind() { return prsTable.getEoftSymbol(); }
    public IPrsStream getIPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getParseStream() { return prsStream; }

    public polyglot.ast.Node parser()
    {
        return parser(null, 0);
    }
    
    public polyglot.ast.Node parser(Monitor monitor)
    {
        return parser(monitor, 0);
    }
    
    public polyglot.ast.Node parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }

    public polyglot.ast.Node parser(Monitor monitor, int error_repair_count)
    {
        btParser.setMonitor(monitor);
        
        try
        {
            return (polyglot.ast.Node) btParser.fuzzyParse(error_repair_count);
        }
        catch (BadParseException e)
        {
            prsStream.reset(e.error_token); // point to error token

            DiagnoseParser diagnoseParser = new DiagnoseParser(prsStream, prsTable);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }

    //
    // Additional entry points, if any
    //
    

    public x10.parser.X10SemanticRules r;

    @SuppressWarnings("unchecked") // Casting Object to various generic types
    public void ruleAction(int ruleNumber)
    {
        switch (ruleNumber)
        {

            //
            // Rule 1:  TypeName ::= TypeName . ErrorId
            //
            case 1: {
               //#line 209 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 207 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 209 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName0(TypeName);
                        break;
            }
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
               //#line 214 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 212 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 214 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName0(PackageName);
                        break;
            }
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
               //#line 219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 217 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 219 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName0(AmbiguousName);
                        break;
            }
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
               //#line 224 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 222 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 224 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName0(AmbiguousName);
                        break;
            }
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
               //#line 229 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 227 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 229 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName0(PackageOrTypeName);
                        break;
            }
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
               //#line 234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 232 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 234 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName0(AmbiguousName);
                        break;
            }
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
               //#line 239 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 237 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 239 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess0(Primary);
                    break;
            }
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 243 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 243 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess1();
                    break;
            }
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 247 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 245 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 245 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 247 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess2(ClassName);
                    break;
            }
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 252 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 250 "x10/parser/x10.g"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 250 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 252 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation0(MethodPrimaryPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 256 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 254 "x10/parser/x10.g"
                Object MethodSuperPrefix = (Object) getRhsSym(1);
                //#line 254 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 256 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation1(MethodSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 260 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 258 "x10/parser/x10.g"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 258 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 260 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation2(MethodClassNameSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
               //#line 265 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 263 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 263 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 265 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodPrimaryPrefix0(Primary);
                    break;
            }
            //
            // Rule 14:  MethodSuperPrefix ::= super . ErrorId$ErrorId
            //
            case 14: {
               //#line 269 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 267 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 269 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSuperPrefix0();
                    break;
            }
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 273 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 271 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 271 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 271 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 273 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodClassNameSuperPrefix0(ClassName);
                    break;
            }
            //
            // Rule 16:  Modifiersopt ::= $Empty
            //
            case 16: {
               //#line 282 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 282 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt0();
                    break;
            }
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 286 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 284 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 284 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 286 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt1(Modifiersopt,Modifier);
                    break;
            }
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 291 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier0();
                    break;
            }
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 295 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 293 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 295 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier1(Annotation);
                    break;
            }
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 299 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 299 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier2();
                    break;
            }
            //
            // Rule 21:  Modifier ::= final
            //
            case 21: {
               //#line 308 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 308 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier3();
                    break;
            }
            //
            // Rule 22:  Modifier ::= native
            //
            case 22: {
               //#line 317 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier4();
                    break;
            }
            //
            // Rule 23:  Modifier ::= private
            //
            case 23: {
               //#line 321 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 321 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier5();
                    break;
            }
            //
            // Rule 24:  Modifier ::= protected
            //
            case 24: {
               //#line 325 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 325 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier6();
                    break;
            }
            //
            // Rule 25:  Modifier ::= public
            //
            case 25: {
               //#line 329 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 329 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier7();
                    break;
            }
            //
            // Rule 26:  Modifier ::= static
            //
            case 26: {
               //#line 333 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 333 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier8();
                    break;
            }
            //
            // Rule 27:  Modifier ::= transient
            //
            case 27: {
               //#line 337 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 337 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier9();
                    break;
            }
            //
            // Rule 28:  Modifier ::= clocked
            //
            case 28: {
               //#line 341 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 341 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier10();
                    break;
            }
            //
            // Rule 30:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 30: {
               //#line 347 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 345 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 345 "x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 347 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt1(MethodModifiersopt);
                    break;
            }
            //
            // Rule 31:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 31: {
               //#line 351 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 349 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 349 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 351 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt2(MethodModifiersopt,Modifier);
                    break;
            }
            //
            // Rule 32:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 32: {
               //#line 356 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 354 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 354 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 354 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 354 "x10/parser/x10.g"
                Object FormalParametersopt = (Object) getRhsSym(5);
                //#line 354 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 354 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 356 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDefDeclaration0(Modifiersopt,Identifier,TypeParametersopt,FormalParametersopt,WhereClauseopt,Type);
                    break;
            }
            //
            // Rule 33:  Properties ::= ( PropertyList )
            //
            case 33: {
               //#line 361 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 359 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(2);
                //#line 361 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Properties0(PropertyList);
                 break;
            } 
            //
            // Rule 34:  PropertyList ::= Property
            //
            case 34: {
               //#line 366 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 364 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(1);
                //#line 366 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList0(Property);
                    break;
            }
            //
            // Rule 35:  PropertyList ::= PropertyList , Property
            //
            case 35: {
               //#line 370 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 368 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(1);
                //#line 368 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(3);
                //#line 370 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList1(PropertyList,Property);
                    break;
            }
            //
            // Rule 36:  Property ::= Annotationsopt Identifier ResultType
            //
            case 36: {
               //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 374 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 374 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 374 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(3);
                //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Property0(Annotationsopt,Identifier,ResultType);
                    break;
            }
            //
            // Rule 37:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 37: {
               //#line 381 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 379 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 379 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 379 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 379 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 379 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 379 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 379 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 379 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 381 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            

                                        
                    break;
            }
            //
            // Rule 38:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 38: {
               //#line 388 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 386 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 386 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 386 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 386 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 386 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(9);
                //#line 386 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(11);
                //#line 386 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(12);
                //#line 386 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(13);
                //#line 386 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(14);
                //#line 388 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration1(MethodModifiersopt,TypeParametersopt,fp1,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 39:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 39: {
               //#line 393 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 391 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 391 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 391 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 391 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(6);
                //#line 391 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(8);
                //#line 391 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(9);
                //#line 391 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 391 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 393 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration2(MethodModifiersopt,TypeParametersopt,PrefixOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 40:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 40: {
               //#line 398 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 396 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 396 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 396 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(5);
                //#line 396 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(7);
                //#line 396 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 396 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 396 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 396 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 398 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration3(MethodModifiersopt,TypeParametersopt,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                               
                    break;
            }
            //
            // Rule 41:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 41: {
               //#line 403 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 401 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 401 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 401 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 401 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 401 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 401 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 401 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 401 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 403 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration4(MethodModifiersopt,TypeParametersopt,fp1,BinOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 42:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 42: {
               //#line 408 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 406 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 406 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 406 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 406 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 406 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 406 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 406 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 408 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration5(MethodModifiersopt,TypeParametersopt,PrefixOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            
                    break;
            }
            //
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 43: {
               //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 411 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 411 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 411 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 411 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 411 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 411 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 411 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration6(MethodModifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 44: {
               //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 416 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 416 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 416 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 416 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(8);
                //#line 416 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(10);
                //#line 416 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(11);
                //#line 416 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(12);
                //#line 416 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(13);
                //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration7(MethodModifiersopt,TypeParametersopt,FormalParameters,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Offersopt MethodBody
            //
            case 45: {
               //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 421 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 421 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 421 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 421 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 421 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 421 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 421 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 428 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 426 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 426 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 426 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 426 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 426 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 426 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 426 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 428 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 47: {
               //#line 433 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 431 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 431 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 431 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 431 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(7);
                //#line 431 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(8);
                //#line 431 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(9);
                //#line 431 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(10);
                //#line 433 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 48:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 48: {
               //#line 439 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 437 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 437 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 437 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 437 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(4);
                //#line 437 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 437 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(6);
                //#line 437 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(7);
                //#line 439 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 49:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 49: {
               //#line 444 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 442 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 442 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 442 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(3);
                //#line 442 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 442 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(5);
                //#line 444 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration1(MethodModifiersopt,Identifier,WhereClauseopt,HasResultTypeopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 50:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 50: {
               //#line 450 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 448 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 448 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 450 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 51:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 51: {
               //#line 454 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 452 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 452 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 454 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation1(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 52:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 458 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 456 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 456 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 456 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 458 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation2(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 53:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 460 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 460 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 460 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation3(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 54:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 54: {
               //#line 467 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 465 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 465 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 465 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 465 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 465 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 465 "x10/parser/x10.g"
                Object ExtendsInterfacesopt = (Object) getRhsSym(7);
                //#line 465 "x10/parser/x10.g"
                Object InterfaceBody = (Object) getRhsSym(8);
                //#line 467 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalInterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
                    break;
            }
            //
            // Rule 55:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 55: {
               //#line 472 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 470 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 470 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(3);
                //#line 470 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(5);
                //#line 470 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(7);
                //#line 472 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 56:  ClassInstanceCreationExpression ::= new TypeName [ Type ] [ ArgumentListopt ]
            //
            case 56: {
               //#line 476 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 474 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 474 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(4);
                //#line 474 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 476 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression1(TypeName,Type,ArgumentListopt);
                    break;
            }
            //
            // Rule 57:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 480 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 478 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 478 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 478 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 478 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 478 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 480 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 58:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 484 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 482 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 482 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 482 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 482 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 482 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 484 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression3(AmbiguousName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 59:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 487 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 487 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 62:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 62: {
               //#line 498 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 496 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(1);
                //#line 496 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(3);
                //#line 496 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 496 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(6);
                //#line 496 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 498 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
                    break;
            }
            //
            // Rule 64:  AnnotatedType ::= Type Annotations
            //
            case 64: {
               //#line 510 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 508 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 508 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(2);
                //#line 510 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotatedType0(Type,Annotations);
                    break;
            }
            //
            // Rule 67:  ConstrainedType ::= ( Type )
            //
            case 67: {
               //#line 517 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 515 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 517 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstrainedType2(Type);
                    break;
            }
            //
            // Rule 68:  SimpleNamedType ::= TypeName
            //
            case 68: {
               //#line 523 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 521 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 523 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType0(TypeName);
                    break;
            }
            //
            // Rule 69:  SimpleNamedType ::= Primary . Identifier
            //
            case 69: {
               //#line 527 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 525 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 525 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 527 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType1(Primary,Identifier);
                    break;
            }
            //
            // Rule 70:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 70: {
               //#line 531 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 529 "x10/parser/x10.g"
                Object DepNamedType = (Object) getRhsSym(1);
                //#line 529 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 531 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType2(DepNamedType,Identifier);
                    break;
            }
            //
            // Rule 71:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 71: {
               //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 534 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 534 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(2);
                //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType0(SimpleNamedType,DepParameters);
                    break;
            }
            //
            // Rule 72:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 72: {
               //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 538 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 538 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType1(SimpleNamedType,Arguments);
                    break;
            }
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 73: {
               //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 542 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 542 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 542 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 74: {
               //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 546 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 546 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
                    break;
            }
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 75: {
               //#line 552 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 550 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 550 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 550 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 552 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
                    break;
            }
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 76: {
               //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 554 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 554 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 554 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
                    break;
            }
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 77: {
               //#line 560 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 558 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 558 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 558 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 558 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(4);
                //#line 560 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 80:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 80: {
               //#line 568 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 566 "x10/parser/x10.g"
                Object ExistentialListopt = (Object) getRhsSym(2);
                //#line 566 "x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 568 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
                    break;
            }
            //
            // Rule 81:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 81: {
               //#line 574 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 572 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(2);
                //#line 574 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
                    break;
            }
            //
            // Rule 82:  TypeParameters ::= [ TypeParameterList ]
            //
            case 82: {
               //#line 579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 577 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(2);
                //#line 579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameters0(TypeParameterList);
                    break;
            }
            //
            // Rule 83:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 83: {
               //#line 584 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 582 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(2);
                //#line 584 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameters0(FormalParameterListopt);
                    break;
            }
            //
            // Rule 84:  Conjunction ::= Expression
            //
            case 84: {
               //#line 589 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 587 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 589 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction0(Expression);
                    break;
            }
            //
            // Rule 85:  Conjunction ::= Conjunction , Expression
            //
            case 85: {
               //#line 593 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 591 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 591 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 593 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction1(Conjunction,Expression);
                    break;
            }
            //
            // Rule 86:  HasZeroConstraint ::= Type$t1 haszero
            //
            case 86: {
               //#line 598 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 596 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 598 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasZeroConstraint0(t1);
                    break;
            }
            //
            // Rule 87:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 87: {
               //#line 603 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 601 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 601 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 603 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint0(t1,t2);
                    break;
            }
            //
            // Rule 88:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 88: {
               //#line 607 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 605 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 605 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 607 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint1(t1,t2);
                    break;
            }
            //
            // Rule 89:  WhereClause ::= DepParameters
            //
            case 89: {
               //#line 612 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 610 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(1);
                //#line 612 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhereClause0(DepParameters);
                      break;
            }
            //
            // Rule 90:  Conjunctionopt ::= $Empty
            //
            case 90: {
               //#line 617 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 617 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt0();
                      break;
            }
            //
            // Rule 91:  Conjunctionopt ::= Conjunction
            //
            case 91: {
               //#line 621 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 619 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 621 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt1(Conjunction);
                    break;
            }
            //
            // Rule 92:  ExistentialListopt ::= $Empty
            //
            case 92: {
               //#line 626 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 626 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt0();
                      break;
            }
            //
            // Rule 93:  ExistentialListopt ::= ExistentialList ;
            //
            case 93: {
               //#line 630 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 628 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 630 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt1(ExistentialList);
                    break;
            }
            //
            // Rule 94:  ExistentialList ::= FormalParameter
            //
            case 94: {
               //#line 635 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 633 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 635 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList0(FormalParameter);
                    break;
            }
            //
            // Rule 95:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 95: {
               //#line 639 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 637 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 637 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 639 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList1(ExistentialList,FormalParameter);
                    break;
            }
            //
            // Rule 98:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 98: {
               //#line 649 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 647 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 647 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 647 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 647 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 647 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 647 "x10/parser/x10.g"
                Object Superopt = (Object) getRhsSym(7);
                //#line 647 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(8);
                //#line 647 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(9);
                //#line 649 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 99:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 99: {
               //#line 655 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 653 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 653 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 653 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 653 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 653 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 653 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(7);
                //#line 653 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(8);
                //#line 655 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 100:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 100: {
               //#line 660 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 658 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 658 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 658 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 658 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 658 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 658 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 658 "x10/parser/x10.g"
                Object ConstructorBody = (Object) getRhsSym(9);
                //#line 660 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                           
                    break;
            }
            //
            // Rule 101:  Super ::= extends ClassType
            //
            case 101: {
               //#line 666 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 664 "x10/parser/x10.g"
                Object ClassType = (Object) getRhsSym(2);
                //#line 666 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Super0(ClassType);
                    break;
            }
            //
            // Rule 102:  FieldKeyword ::= val
            //
            case 102: {
               //#line 671 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 671 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword0();
                    break;
            }
            //
            // Rule 103:  FieldKeyword ::= var
            //
            case 103: {
               //#line 675 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 675 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword1();
                    break;
            }
            //
            // Rule 104:  VarKeyword ::= val
            //
            case 104: {
               //#line 682 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 682 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword0();
                    break;
            }
            //
            // Rule 105:  VarKeyword ::= var
            //
            case 105: {
               //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword1();
                    break;
            }
            //
            // Rule 106:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 106: {
               //#line 692 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 690 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 690 "x10/parser/x10.g"
                Object FieldKeyword = (Object) getRhsSym(2);
                //#line 690 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(3);
                //#line 692 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 107:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 107: {
               //#line 698 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 696 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 696 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(2);
                //#line 698 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 110:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 110: {
               //#line 710 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 708 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 708 "x10/parser/x10.g"
                Object NonExpressionStatement = (Object) getRhsSym(2);
                //#line 710 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
                    break;
            }
            //
            // Rule 136:  OfferStatement ::= offer Expression ;
            //
            case 136: {
               //#line 743 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 741 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 743 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OfferStatement0(Expression);
                    break;
            }
            //
            // Rule 137:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 137: {
               //#line 748 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 746 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 746 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 748 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 138:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 138: {
               //#line 753 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 751 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 751 "x10/parser/x10.g"
                Object s1 = (Object) getRhsSym(5);
                //#line 751 "x10/parser/x10.g"
                Object s2 = (Object) getRhsSym(7);
                //#line 753 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenElseStatement0(Expression,s1,s2);
                    break;
            }
            //
            // Rule 139:  EmptyStatement ::= ;
            //
            case 139: {
               //#line 758 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 758 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EmptyStatement0();
                    break;
            }
            //
            // Rule 140:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 140: {
               //#line 763 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 761 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 761 "x10/parser/x10.g"
                Object LoopStatement = (Object) getRhsSym(3);
                //#line 763 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LabeledStatement0(Identifier,LoopStatement);
                    break;
            }
            //
            // Rule 145:  ExpressionStatement ::= StatementExpression ;
            //
            case 145: {
               //#line 774 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 772 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 774 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionStatement0(StatementExpression);
                    break;
            }
            //
            // Rule 153:  AssertStatement ::= assert Expression ;
            //
            case 153: {
               //#line 787 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 785 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 787 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement0(Expression);
                    break;
            }
            //
            // Rule 154:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 154: {
               //#line 791 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 789 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(2);
                //#line 789 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(4);
                //#line 791 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement1(expr1,expr2);
                    break;
            }
            //
            // Rule 155:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 155: {
               //#line 796 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 794 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 794 "x10/parser/x10.g"
                Object SwitchBlock = (Object) getRhsSym(5);
                //#line 796 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchStatement0(Expression,SwitchBlock);
                    break;
            }
            //
            // Rule 156:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 156: {
               //#line 801 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 799 "x10/parser/x10.g"
                Object SwitchBlockStatementGroupsopt = (Object) getRhsSym(2);
                //#line 799 "x10/parser/x10.g"
                Object SwitchLabelsopt = (Object) getRhsSym(3);
                //#line 801 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
                    break;
            }
            //
            // Rule 158:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 158: {
               //#line 807 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 805 "x10/parser/x10.g"
                Object SwitchBlockStatementGroups = (Object) getRhsSym(1);
                //#line 805 "x10/parser/x10.g"
                Object SwitchBlockStatementGroup = (Object) getRhsSym(2);
                //#line 807 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
                    break;
            }
            //
            // Rule 159:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 159: {
               //#line 812 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 810 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 810 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(2);
                //#line 812 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
                    break;
            }
            //
            // Rule 160:  SwitchLabels ::= SwitchLabel
            //
            case 160: {
               //#line 817 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 815 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(1);
                //#line 817 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels0(SwitchLabel);
                    break;
            }
            //
            // Rule 161:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 161: {
               //#line 821 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 819 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 819 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(2);
                //#line 821 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
                    break;
            }
            //
            // Rule 162:  SwitchLabel ::= case ConstantExpression :
            //
            case 162: {
               //#line 826 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 824 "x10/parser/x10.g"
                Object ConstantExpression = (Object) getRhsSym(2);
                //#line 826 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel0(ConstantExpression);
                    break;
            }
            //
            // Rule 163:  SwitchLabel ::= default :
            //
            case 163: {
               //#line 830 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 830 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel1();
                    break;
            }
            //
            // Rule 164:  WhileStatement ::= while ( Expression ) Statement
            //
            case 164: {
               //#line 835 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 833 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 833 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 835 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhileStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 165:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 165: {
               //#line 840 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 838 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 838 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 840 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DoStatement0(Statement,Expression);
                    break;
            }
            //
            // Rule 168:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 168: {
               //#line 848 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 846 "x10/parser/x10.g"
                Object ForInitopt = (Object) getRhsSym(3);
                //#line 846 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(5);
                //#line 846 "x10/parser/x10.g"
                Object ForUpdateopt = (Object) getRhsSym(7);
                //#line 846 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(9);
                //#line 848 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
                    break;
            }
            //
            // Rule 170:  ForInit ::= LocalVariableDeclaration
            //
            case 170: {
               //#line 854 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 852 "x10/parser/x10.g"
                Object LocalVariableDeclaration = (Object) getRhsSym(1);
                //#line 854 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInit1(LocalVariableDeclaration);
                    break;
            }
            //
            // Rule 172:  StatementExpressionList ::= StatementExpression
            //
            case 172: {
               //#line 861 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 859 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 861 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList0(StatementExpression);
                    break;
            }
            //
            // Rule 173:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 173: {
               //#line 865 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 863 "x10/parser/x10.g"
                Object StatementExpressionList = (Object) getRhsSym(1);
                //#line 863 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(3);
                //#line 865 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
                    break;
            }
            //
            // Rule 174:  BreakStatement ::= break Identifieropt ;
            //
            case 174: {
               //#line 870 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 868 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 870 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BreakStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 175:  ContinueStatement ::= continue Identifieropt ;
            //
            case 175: {
               //#line 875 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 873 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 875 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ContinueStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 176:  ReturnStatement ::= return Expressionopt ;
            //
            case 176: {
               //#line 880 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 878 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(2);
                //#line 880 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ReturnStatement0(Expressionopt);
                    break;
            }
            //
            // Rule 177:  ThrowStatement ::= throw Expression ;
            //
            case 177: {
               //#line 885 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 883 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 885 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ThrowStatement0(Expression);
                    break;
            }
            //
            // Rule 178:  TryStatement ::= try Block Catches
            //
            case 178: {
               //#line 890 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 888 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 888 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(3);
                //#line 890 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement0(Block,Catches);
                    break;
            }
            //
            // Rule 179:  TryStatement ::= try Block Catchesopt Finally
            //
            case 179: {
               //#line 894 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 892 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 892 "x10/parser/x10.g"
                Object Catchesopt = (Object) getRhsSym(3);
                //#line 892 "x10/parser/x10.g"
                Object Finally = (Object) getRhsSym(4);
                //#line 894 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement1(Block,Catchesopt,Finally);
                    break;
            }
            //
            // Rule 180:  Catches ::= CatchClause
            //
            case 180: {
               //#line 899 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 897 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(1);
                //#line 899 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches0(CatchClause);
                    break;
            }
            //
            // Rule 181:  Catches ::= Catches CatchClause
            //
            case 181: {
               //#line 903 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 901 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(1);
                //#line 901 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(2);
                //#line 903 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches1(Catches,CatchClause);
                    break;
            }
            //
            // Rule 182:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 182: {
               //#line 908 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 906 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 906 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 908 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CatchClause0(FormalParameter,Block);
                    break;
            }
            //
            // Rule 183:  Finally ::= finally Block
            //
            case 183: {
               //#line 913 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 911 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 913 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Finally0(Block);
                    break;
            }
            //
            // Rule 184:  ClockedClause ::= clocked ( ClockList )
            //
            case 184: {
               //#line 918 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 916 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(3);
                //#line 918 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClause0(ClockList);
                    break;
            }
            //
            // Rule 185:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 185: {
               //#line 924 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 922 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(2);
                //#line 922 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 924 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement0(ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 186:  AsyncStatement ::= clocked async Statement
            //
            case 186: {
               //#line 928 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 926 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 928 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement1(Statement);
                    break;
            }
            //
            // Rule 187:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 187: {
               //#line 934 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 932 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 932 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 934 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
                    break;
            }
            //
            // Rule 188:  AtomicStatement ::= atomic Statement
            //
            case 188: {
               //#line 939 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 937 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 939 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtomicStatement0(Statement);
                    break;
            }
            //
            // Rule 189:  WhenStatement ::= when ( Expression ) Statement
            //
            case 189: {
               //#line 945 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 943 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 943 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 945 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 190:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 190: {
               //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1005 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1005 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1005 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(7);
                //#line 1005 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(8);
                //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 191:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 191: {
               //#line 1011 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1009 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1009 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1011 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 192:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 192: {
               //#line 1015 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1013 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1013 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1013 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1015 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 193:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 193: {
               //#line 1019 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1017 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1017 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1019 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 194:  FinishStatement ::= finish Statement
            //
            case 194: {
               //#line 1025 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1023 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1025 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 195:  FinishStatement ::= clocked finish Statement
            //
            case 195: {
               //#line 1029 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1027 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1029 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 196:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 196: {
               //#line 1033 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1031 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1033 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 198:  NextStatement ::= next ;
            //
            case 198: {
               //#line 1040 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 199:  ResumeStatement ::= resume ;
            //
            case 199: {
               //#line 1045 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1045 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 200:  ClockList ::= Clock
            //
            case 200: {
               //#line 1050 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1048 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1050 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 201:  ClockList ::= ClockList , Clock
            //
            case 201: {
               //#line 1054 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1052 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1052 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1054 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 202:  Clock ::= Expression
            //
            case 202: {
               //#line 1060 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1058 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1060 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 204:  CastExpression ::= ExpressionName
            //
            case 204: {
               //#line 1072 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1070 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1072 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 205:  CastExpression ::= CastExpression as Type
            //
            case 205: {
               //#line 1076 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1074 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1074 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1076 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 206:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 206: {
               //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1080 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 207:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 207: {
               //#line 1086 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1084 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1084 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1086 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 208:  TypeParameterList ::= TypeParameter
            //
            case 208: {
               //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1089 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 209:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 209: {
               //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1093 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1093 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 210:  TypeParamWithVariance ::= Identifier
            //
            case 210: {
               //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1098 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 211:  TypeParamWithVariance ::= + Identifier
            //
            case 211: {
               //#line 1104 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1102 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1104 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= - Identifier
            //
            case 212: {
               //#line 1108 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1106 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1108 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParameter ::= Identifier
            //
            case 213: {
               //#line 1113 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1111 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1113 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 214:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 214: {
               //#line 1137 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1135 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 1135 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 1137 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentExpression0(expr1,expr2);
                    break;
            }
            //
            // Rule 215:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 215: {
               //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1139 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1139 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1139 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1139 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1139 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 216:  LastExpression ::= Expression
            //
            case 216: {
               //#line 1146 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1144 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1146 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 217:  ClosureBody ::= ConditionalExpression
            //
            case 217: {
               //#line 1151 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1149 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1151 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 218: {
               //#line 1155 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1153 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1153 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1153 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1155 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 219:  ClosureBody ::= Annotationsopt Block
            //
            case 219: {
               //#line 1159 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1157 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1157 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1159 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 220:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 220: {
               //#line 1165 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1163 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1163 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1165 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 221:  FinishExpression ::= finish ( Expression ) Block
            //
            case 221: {
               //#line 1170 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1168 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1168 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1170 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishExpression0(Expression,Block);
                    break;
            }
            //
            // Rule 222:  WhereClauseopt ::= $Empty
            //
            case 222:
                setResult(null);
                break;

            //
            // Rule 224:  ClockedClauseopt ::= $Empty
            //
            case 224: {
               //#line 1214 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1214 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 226:  TypeName ::= Identifier
            //
            case 226: {
               //#line 1225 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1223 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1225 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 227:  TypeName ::= TypeName . Identifier
            //
            case 227: {
               //#line 1229 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1227 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1229 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 229:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 229: {
               //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1234 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 230:  TypeArgumentList ::= Type
            //
            case 230: {
               //#line 1242 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1240 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1242 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 231:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 231: {
               //#line 1246 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1244 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1244 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1246 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 232:  PackageName ::= Identifier
            //
            case 232: {
               //#line 1255 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1253 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1255 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 233:  PackageName ::= PackageName . Identifier
            //
            case 233: {
               //#line 1259 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1257 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1257 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1259 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 234:  ExpressionName ::= Identifier
            //
            case 234: {
               //#line 1270 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1268 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1270 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 235:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 235: {
               //#line 1274 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1272 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1272 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1274 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 236:  MethodName ::= Identifier
            //
            case 236: {
               //#line 1279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1277 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1279 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 237:  MethodName ::= AmbiguousName . Identifier
            //
            case 237: {
               //#line 1283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1281 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1281 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 238:  PackageOrTypeName ::= Identifier
            //
            case 238: {
               //#line 1288 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1286 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1288 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 239:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 239: {
               //#line 1292 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1290 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1290 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1292 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 240:  AmbiguousName ::= Identifier
            //
            case 240: {
               //#line 1297 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1295 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1297 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 241:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 1301 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1299 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1299 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1301 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 242:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 242: {
               //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1306 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1306 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 243: {
               //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1310 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1310 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1310 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1316 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1314 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1314 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1314 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1314 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1316 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 245: {
               //#line 1320 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1318 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1318 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1318 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1318 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1318 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1320 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 246:  ImportDeclarations ::= ImportDeclaration
            //
            case 246: {
               //#line 1325 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1323 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1325 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 247: {
               //#line 1329 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1327 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1327 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1329 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 248:  TypeDeclarations ::= TypeDeclaration
            //
            case 248: {
               //#line 1334 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1332 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1334 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 249: {
               //#line 1338 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1336 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1336 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1338 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 250:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 250: {
               //#line 1343 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1341 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1341 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1343 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 253:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 253: {
               //#line 1354 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1352 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1354 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 254:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 254: {
               //#line 1359 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1357 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1359 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 258:  TypeDeclaration ::= ;
            //
            case 258: {
               //#line 1373 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1373 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 259:  Interfaces ::= implements InterfaceTypeList
            //
            case 259: {
               //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1487 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 260:  InterfaceTypeList ::= Type
            //
            case 260: {
               //#line 1494 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1492 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1494 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 261:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 261: {
               //#line 1498 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1496 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1496 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1498 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 262:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 262: {
               //#line 1506 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1504 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1506 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 264:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 264: {
               //#line 1512 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1510 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1510 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1512 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 266:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 266: {
               //#line 1532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1530 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1532 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 268:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 268: {
               //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1536 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 269: {
               //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1540 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 270: {
               //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 271: {
               //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1548 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 272: {
               //#line 1554 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1552 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1554 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 273:  ClassMemberDeclaration ::= ;
            //
            case 273: {
               //#line 1558 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1558 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 274:  FormalDeclarators ::= FormalDeclarator
            //
            case 274: {
               //#line 1563 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1561 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1563 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 275:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 275: {
               //#line 1567 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1565 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1565 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1567 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 276:  FieldDeclarators ::= FieldDeclarator
            //
            case 276: {
               //#line 1573 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1571 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1573 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 277:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 277: {
               //#line 1577 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1575 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1575 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1577 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 278:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 278: {
               //#line 1583 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1581 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1583 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 279:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 279: {
               //#line 1587 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1585 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1585 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1587 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 280:  VariableDeclarators ::= VariableDeclarator
            //
            case 280: {
               //#line 1592 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1590 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1592 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 281:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 281: {
               //#line 1596 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1594 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1594 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1596 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 283:  ResultType ::= : Type
            //
            case 283: {
               //#line 1650 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1648 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1650 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 284:  HasResultType ::= : Type
            //
            case 284: {
               //#line 1654 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1652 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1654 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 285:  HasResultType ::= <: Type
            //
            case 285: {
               //#line 1658 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1656 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1658 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 286:  FormalParameterList ::= FormalParameter
            //
            case 286: {
               //#line 1672 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1670 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1672 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 287:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 287: {
               //#line 1676 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1674 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1674 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1676 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 288:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 288: {
               //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1679 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1679 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 289: {
               //#line 1685 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1683 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1683 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1685 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 290: {
               //#line 1689 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1687 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1687 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1687 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1689 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 291:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 291: {
               //#line 1694 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1692 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1692 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1694 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 292:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 292: {
               //#line 1698 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1696 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1696 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1696 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1698 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 293:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 293: {
               //#line 1703 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1701 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1701 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1703 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 294: {
               //#line 1707 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1705 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1705 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1705 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1707 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 295:  FormalParameter ::= Type
            //
            case 295: {
               //#line 1711 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1709 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1711 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 296:  Offers ::= offers Type
            //
            case 296: {
               //#line 1849 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1847 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1849 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 297:  MethodBody ::= = LastExpression ;
            //
            case 297: {
               //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1853 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 298: {
               //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1857 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1857 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1857 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= = Annotationsopt Block
            //
            case 299: {
               //#line 1863 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1861 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1861 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1863 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= Annotationsopt Block
            //
            case 300: {
               //#line 1867 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1865 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1865 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1867 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody3(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 301:  MethodBody ::= ;
            //
            case 301:
                setResult(null);
                break;

            //
            // Rule 302:  ConstructorBody ::= = ConstructorBlock
            //
            case 302: {
               //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1935 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 303:  ConstructorBody ::= ConstructorBlock
            //
            case 303: {
               //#line 1941 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1939 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1941 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 304: {
               //#line 1945 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1943 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1945 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= = AssignPropertyCall
            //
            case 305: {
               //#line 1949 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1947 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1949 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody3(AssignPropertyCall);
                    break;
            }
            //
            // Rule 306:  ConstructorBody ::= ;
            //
            case 306:
                setResult(null);
                break;

            //
            // Rule 307:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 307: {
               //#line 1956 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1954 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1954 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1956 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 308:  Arguments ::= ( ArgumentListopt )
            //
            case 308: {
               //#line 1961 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1959 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1961 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 310:  ExtendsInterfaces ::= extends Type
            //
            case 310: {
               //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2015 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 311:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 311: {
               //#line 2021 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2019 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2019 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2021 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 312:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 312: {
               //#line 2029 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2027 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2029 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 314:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 314: {
               //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2033 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2033 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 315: {
               //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 316: {
               //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 317: {
               //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 318: {
               //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2050 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 319: {
               //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2054 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 320: {
               //#line 2060 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2058 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2060 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 321:  InterfaceMemberDeclaration ::= ;
            //
            case 321: {
               //#line 2064 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2064 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 322:  Annotations ::= Annotation
            //
            case 322: {
               //#line 2069 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2067 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2069 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 323:  Annotations ::= Annotations Annotation
            //
            case 323: {
               //#line 2073 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2071 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2071 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2073 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 324:  Annotation ::= @ NamedType
            //
            case 324: {
               //#line 2078 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2076 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2078 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 325:  Identifier ::= IDENTIFIER$ident
            //
            case 325: {
               //#line 2092 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2090 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2092 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 326:  Block ::= { BlockStatementsopt }
            //
            case 326: {
               //#line 2127 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2125 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2127 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 327:  BlockStatements ::= BlockStatement
            //
            case 327: {
               //#line 2132 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2130 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2132 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 328:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 328: {
               //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2134 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2134 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 330:  BlockStatement ::= ClassDeclaration
            //
            case 330: {
               //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2140 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= TypeDefDeclaration
            //
            case 331: {
               //#line 2146 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2144 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2146 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 332:  BlockStatement ::= Statement
            //
            case 332: {
               //#line 2150 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2148 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2150 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 333:  IdentifierList ::= Identifier
            //
            case 333: {
               //#line 2155 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2153 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2155 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 334:  IdentifierList ::= IdentifierList , Identifier
            //
            case 334: {
               //#line 2159 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2157 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2157 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2159 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 335:  FormalDeclarator ::= Identifier ResultType
            //
            case 335: {
               //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2162 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2162 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 336: {
               //#line 2168 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2166 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2166 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2168 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 337:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 337: {
               //#line 2172 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2170 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2170 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2170 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2172 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 338:  FieldDeclarator ::= Identifier HasResultType
            //
            case 338: {
               //#line 2177 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2175 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2175 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2177 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 339:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 339: {
               //#line 2181 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2179 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2179 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2179 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2181 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 340:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2184 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2184 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2184 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2190 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2188 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2188 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2188 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2190 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 342: {
               //#line 2194 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2192 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2192 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2192 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2192 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2194 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 343: {
               //#line 2199 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2197 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2197 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2197 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2199 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2203 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2201 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2201 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2201 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2203 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 345:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 345: {
               //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2205 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2205 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2205 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2205 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 347:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 347: {
               //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2212 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2212 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2212 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 348: {
               //#line 2219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2217 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2217 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2219 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 349:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 349: {
               //#line 2224 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2222 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2222 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2222 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2224 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 350:  Primary ::= here
            //
            case 350: {
               //#line 2235 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2235 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 351:  Primary ::= [ ArgumentListopt ]
            //
            case 351: {
               //#line 2239 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2237 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2239 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 353:  Primary ::= self
            //
            case 353: {
               //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 354:  Primary ::= this
            //
            case 354: {
               //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 355:  Primary ::= ClassName . this
            //
            case 355: {
               //#line 2253 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2251 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2253 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 356:  Primary ::= ( Expression )
            //
            case 356: {
               //#line 2257 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2255 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2257 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 362:  OperatorFunction ::= TypeName . +
            //
            case 362: {
               //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2265 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . -
            //
            case 363: {
               //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2269 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . *
            //
            case 364: {
               //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2273 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . /
            //
            case 365: {
               //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . %
            //
            case 366: {
               //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2281 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . &
            //
            case 367: {
               //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2285 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . |
            //
            case 368: {
               //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2289 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . ^
            //
            case 369: {
               //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . <<
            //
            case 370: {
               //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . >>
            //
            case 371: {
               //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2301 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . >>>
            //
            case 372: {
               //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2305 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . <
            //
            case 373: {
               //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2309 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . <=
            //
            case 374: {
               //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . >=
            //
            case 375: {
               //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . >
            //
            case 376: {
               //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2321 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . ==
            //
            case 377: {
               //#line 2327 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2325 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2327 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 378:  OperatorFunction ::= TypeName . !=
            //
            case 378: {
               //#line 2331 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2329 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2331 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 379:  Literal ::= IntegerLiteral$lit
            //
            case 379: {
               //#line 2337 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2335 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2337 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 380:  Literal ::= LongLiteral$lit
            //
            case 380: {
               //#line 2341 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2339 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2341 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 381:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 381: {
               //#line 2345 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2343 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2345 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 382:  Literal ::= UnsignedLongLiteral$lit
            //
            case 382: {
               //#line 2349 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2347 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2349 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 383:  Literal ::= FloatingPointLiteral$lit
            //
            case 383: {
               //#line 2353 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2351 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2353 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 384:  Literal ::= DoubleLiteral$lit
            //
            case 384: {
               //#line 2357 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2357 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 385:  Literal ::= BooleanLiteral
            //
            case 385: {
               //#line 2361 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2359 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2361 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 386:  Literal ::= CharacterLiteral$lit
            //
            case 386: {
               //#line 2365 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2363 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2365 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 387:  Literal ::= StringLiteral$str
            //
            case 387: {
               //#line 2369 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2367 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2369 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 388:  Literal ::= null
            //
            case 388: {
               //#line 2373 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2373 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 389:  BooleanLiteral ::= true$trueLiteral
            //
            case 389: {
               //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2376 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 390:  BooleanLiteral ::= false$falseLiteral
            //
            case 390: {
               //#line 2382 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2380 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2382 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 391:  ArgumentList ::= Expression
            //
            case 391: {
               //#line 2390 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2388 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2390 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 392:  ArgumentList ::= ArgumentList , Expression
            //
            case 392: {
               //#line 2394 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2392 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2392 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2394 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 393:  FieldAccess ::= Primary . Identifier
            //
            case 393: {
               //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2397 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 394:  FieldAccess ::= super . Identifier
            //
            case 394: {
               //#line 2403 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2401 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2403 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 395:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 395: {
               //#line 2407 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2405 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2405 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2405 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2407 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 396:  FieldAccess ::= Primary . class$c
            //
            case 396: {
               //#line 2411 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2409 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2409 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2411 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 397:  FieldAccess ::= super . class$c
            //
            case 397: {
               //#line 2415 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2413 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2415 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 398: {
               //#line 2419 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2417 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2417 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2417 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2419 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 399:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 399: {
               //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2422 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2422 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2422 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 400:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 400: {
               //#line 2428 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2426 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2426 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2426 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2426 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2428 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 401:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 401: {
               //#line 2432 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2430 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2430 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2430 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2432 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 402:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 402: {
               //#line 2436 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2434 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2434 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2434 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2434 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2434 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2436 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 403:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 403: {
               //#line 2440 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2438 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2438 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2438 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2440 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 404:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 404: {
               //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2443 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2443 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 405:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 405: {
               //#line 2449 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2447 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2447 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2447 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2449 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 406:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 406: {
               //#line 2453 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2451 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2451 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2453 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 407:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 407: {
               //#line 2457 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2455 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2455 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2455 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2455 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2457 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 411:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 411: {
               //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2464 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 412:  PostDecrementExpression ::= PostfixExpression --
            //
            case 412: {
               //#line 2471 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2469 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2471 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 415:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 415: {
               //#line 2478 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2476 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2478 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 416:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 416: {
               //#line 2482 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2480 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2482 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 419:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 419: {
               //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2487 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2487 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 420:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 420: {
               //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2492 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 421:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 2499 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2497 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2499 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 423:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 423: {
               //#line 2505 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2503 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2505 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 424:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 424: {
               //#line 2509 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2507 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2509 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 426:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 426: {
               //#line 2515 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2513 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2513 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2515 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 427:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 427: {
               //#line 2519 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2517 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2517 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2519 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 428:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 428: {
               //#line 2523 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2521 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2521 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2523 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 430:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 430: {
               //#line 2529 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2527 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2527 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2529 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 431:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 431: {
               //#line 2533 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2531 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2531 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2533 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 433:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 433: {
               //#line 2539 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2537 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2537 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2539 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 434:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 434: {
               //#line 2543 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2541 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2541 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2543 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 435:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 435: {
               //#line 2547 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2545 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2545 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2547 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 437:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 437: {
               //#line 2553 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2551 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2551 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2553 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 441:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 441: {
               //#line 2561 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2559 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2559 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2561 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 442:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 442: {
               //#line 2565 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2563 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2563 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2565 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 443:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 443: {
               //#line 2569 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2567 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2567 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2569 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 444:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 444: {
               //#line 2573 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2571 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2571 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2573 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 445:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 445: {
               //#line 2577 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2575 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2575 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2577 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 446:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 446: {
               //#line 2581 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2579 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2579 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2581 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 448: {
               //#line 2587 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2585 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2585 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2587 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 449:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 449: {
               //#line 2591 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2589 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2589 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2591 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 450:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 450: {
               //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2593 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2593 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 452:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 452: {
               //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2599 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2599 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 454:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 454: {
               //#line 2607 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2605 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2605 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2607 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 456:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 456: {
               //#line 2613 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2611 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2611 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2613 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 458:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 458: {
               //#line 2619 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2617 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2617 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2619 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 460:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 460: {
               //#line 2625 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2623 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2623 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2625 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 465:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 465: {
               //#line 2635 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2633 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2633 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2635 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 468:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 468: {
               //#line 2643 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2641 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2641 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2641 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2643 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 469:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 469: {
               //#line 2647 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2645 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2645 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2645 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2645 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2647 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 470:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 470: {
               //#line 2651 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2649 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2649 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2649 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2649 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2651 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 471:  LeftHandSide ::= ExpressionName
            //
            case 471: {
               //#line 2656 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2656 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 473:  AssignmentOperator ::= =
            //
            case 473: {
               //#line 2662 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2662 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 474:  AssignmentOperator ::= *=
            //
            case 474: {
               //#line 2666 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2666 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 475:  AssignmentOperator ::= /=
            //
            case 475: {
               //#line 2670 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2670 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 476:  AssignmentOperator ::= %=
            //
            case 476: {
               //#line 2674 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2674 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 477:  AssignmentOperator ::= +=
            //
            case 477: {
               //#line 2678 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2678 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 478:  AssignmentOperator ::= -=
            //
            case 478: {
               //#line 2682 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2682 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= <<=
            //
            case 479: {
               //#line 2686 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= >>=
            //
            case 480: {
               //#line 2690 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2690 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= >>>=
            //
            case 481: {
               //#line 2694 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2694 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= &=
            //
            case 482: {
               //#line 2698 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2698 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= ^=
            //
            case 483: {
               //#line 2702 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2702 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= |=
            //
            case 484: {
               //#line 2706 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2706 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 487:  PrefixOp ::= +
            //
            case 487: {
               //#line 2716 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2716 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 488:  PrefixOp ::= -
            //
            case 488: {
               //#line 2720 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2720 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 489:  PrefixOp ::= !
            //
            case 489: {
               //#line 2724 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2724 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 490:  PrefixOp ::= ~
            //
            case 490: {
               //#line 2728 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2728 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 491:  BinOp ::= +
            //
            case 491: {
               //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 492:  BinOp ::= -
            //
            case 492: {
               //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 493:  BinOp ::= *
            //
            case 493: {
               //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 494:  BinOp ::= /
            //
            case 494: {
               //#line 2745 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2745 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 495:  BinOp ::= %
            //
            case 495: {
               //#line 2749 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2749 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 496:  BinOp ::= &
            //
            case 496: {
               //#line 2753 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2753 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 497:  BinOp ::= |
            //
            case 497: {
               //#line 2757 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2757 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 498:  BinOp ::= ^
            //
            case 498: {
               //#line 2761 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2761 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 499:  BinOp ::= &&
            //
            case 499: {
               //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 500:  BinOp ::= ||
            //
            case 500: {
               //#line 2769 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2769 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 501:  BinOp ::= <<
            //
            case 501: {
               //#line 2773 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2773 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 502:  BinOp ::= >>
            //
            case 502: {
               //#line 2777 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2777 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 503:  BinOp ::= >>>
            //
            case 503: {
               //#line 2781 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2781 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 504:  BinOp ::= >=
            //
            case 504: {
               //#line 2785 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2785 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 505:  BinOp ::= <=
            //
            case 505: {
               //#line 2789 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2789 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 506:  BinOp ::= >
            //
            case 506: {
               //#line 2793 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2793 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 507:  BinOp ::= <
            //
            case 507: {
               //#line 2797 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2797 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 508:  BinOp ::= ==
            //
            case 508: {
               //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 509:  BinOp ::= !=
            //
            case 509: {
               //#line 2808 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2808 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 510:  Catchesopt ::= $Empty
            //
            case 510: {
               //#line 2816 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2816 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catchesopt0();
                    break;
            }
            //
            // Rule 512:  Identifieropt ::= $Empty
            //
            case 512:
                setResult(null);
                break;

            //
            // Rule 513:  Identifieropt ::= Identifier
            //
            case 513: {
               //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2822 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 514:  ForUpdateopt ::= $Empty
            //
            case 514: {
               //#line 2829 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2829 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForUpdateopt0();
                    break;
            }
            //
            // Rule 516:  Expressionopt ::= $Empty
            //
            case 516:
                setResult(null);
                break;

            //
            // Rule 518:  ForInitopt ::= $Empty
            //
            case 518: {
               //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 520:  SwitchLabelsopt ::= $Empty
            //
            case 520: {
               //#line 2845 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2845 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 522:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 522: {
               //#line 2851 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2851 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 524:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 524: {
               //#line 2874 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2874 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 526:  ExtendsInterfacesopt ::= $Empty
            //
            case 526: {
               //#line 2880 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2880 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfacesopt0();
                    break;
            }
            //
            // Rule 528:  ClassBodyopt ::= $Empty
            //
            case 528:
                setResult(null);
                break;

            //
            // Rule 530:  ArgumentListopt ::= $Empty
            //
            case 530: {
               //#line 2910 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2910 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 532:  BlockStatementsopt ::= $Empty
            //
            case 532: {
               //#line 2916 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2916 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatementsopt0();
                    break;
            }
            //
            // Rule 534:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 534:
                setResult(null);
                break;

            //
            // Rule 536:  FormalParameterListopt ::= $Empty
            //
            case 536: {
               //#line 2936 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2936 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 538:  Offersopt ::= $Empty
            //
            case 538: {
               //#line 2948 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2948 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 540:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 540: {
               //#line 2984 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2984 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 542:  Interfacesopt ::= $Empty
            //
            case 542: {
               //#line 2990 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2990 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfacesopt0();
                    break;
            }
            //
            // Rule 544:  Superopt ::= $Empty
            //
            case 544:
                setResult(null);
                break;

            //
            // Rule 546:  TypeParametersopt ::= $Empty
            //
            case 546: {
               //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 548:  FormalParametersopt ::= $Empty
            //
            case 548: {
               //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 550:  Annotationsopt ::= $Empty
            //
            case 550: {
               //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 552:  TypeDeclarationsopt ::= $Empty
            //
            case 552: {
               //#line 3018 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3018 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 554:  ImportDeclarationsopt ::= $Empty
            //
            case 554: {
               //#line 3024 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3024 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarationsopt0();
                    break;
            }
            //
            // Rule 556:  PackageDeclarationopt ::= $Empty
            //
            case 556:
                setResult(null);
                break;

            //
            // Rule 558:  HasResultTypeopt ::= $Empty
            //
            case 558:
                setResult(null);
                break;

            //
            // Rule 560:  TypeArgumentsopt ::= $Empty
            //
            case 560: {
               //#line 3044 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 562:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 562: {
               //#line 3050 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3050 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 564:  Propertiesopt ::= $Empty
            //
            case 564: {
               //#line 3056 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3056 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

