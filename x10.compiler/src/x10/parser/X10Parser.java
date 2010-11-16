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
               //#line 207 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 205 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 207 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName0(TypeName);
                        break;
            }
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
               //#line 212 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 210 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 212 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName0(PackageName);
                        break;
            }
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
               //#line 217 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 215 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 217 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName0(AmbiguousName);
                        break;
            }
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
               //#line 222 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 220 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 222 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName0(AmbiguousName);
                        break;
            }
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
               //#line 227 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 225 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 227 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName0(PackageOrTypeName);
                        break;
            }
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
               //#line 232 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 230 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 232 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName0(AmbiguousName);
                        break;
            }
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
               //#line 237 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 235 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 237 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess0(Primary);
                    break;
            }
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 241 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 241 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess1();
                    break;
            }
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 245 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 243 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 243 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 245 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess2(ClassName);
                    break;
            }
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 250 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 248 "x10/parser/x10.g"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 248 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 250 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation0(MethodPrimaryPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 254 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 252 "x10/parser/x10.g"
                Object MethodSuperPrefix = (Object) getRhsSym(1);
                //#line 252 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 254 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation1(MethodSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 258 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 256 "x10/parser/x10.g"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 256 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 258 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation2(MethodClassNameSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
               //#line 263 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 261 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 261 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 263 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodPrimaryPrefix0(Primary);
                    break;
            }
            //
            // Rule 14:  MethodSuperPrefix ::= super . ErrorId$ErrorId
            //
            case 14: {
               //#line 267 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 265 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 267 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSuperPrefix0();
                    break;
            }
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 269 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 269 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 269 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodClassNameSuperPrefix0(ClassName);
                    break;
            }
            //
            // Rule 16:  Modifiersopt ::= $Empty
            //
            case 16: {
               //#line 277 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 277 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt0();
                    break;
            }
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 281 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 279 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 279 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 281 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt1(Modifiersopt,Modifier);
                    break;
            }
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 286 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 286 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier0();
                    break;
            }
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 290 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 288 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 290 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier1(Annotation);
                    break;
            }
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 294 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 294 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier2();
                    break;
            }
            //
            // Rule 21:  Modifier ::= final
            //
            case 21: {
               //#line 303 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier3();
                    break;
            }
            //
            // Rule 22:  Modifier ::= native
            //
            case 22: {
               //#line 312 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 312 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier4();
                    break;
            }
            //
            // Rule 23:  Modifier ::= private
            //
            case 23: {
               //#line 316 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 316 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier5();
                    break;
            }
            //
            // Rule 24:  Modifier ::= protected
            //
            case 24: {
               //#line 320 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 320 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier6();
                    break;
            }
            //
            // Rule 25:  Modifier ::= public
            //
            case 25: {
               //#line 324 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 324 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier7();
                    break;
            }
            //
            // Rule 26:  Modifier ::= static
            //
            case 26: {
               //#line 328 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 328 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier8();
                    break;
            }
            //
            // Rule 27:  Modifier ::= transient
            //
            case 27: {
               //#line 332 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 332 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier9();
                    break;
            }
            //
            // Rule 28:  Modifier ::= clocked
            //
            case 28: {
               //#line 336 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 336 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier10();
                    break;
            }
            //
            // Rule 30:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 30: {
               //#line 342 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 340 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 340 "x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 342 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt1(MethodModifiersopt);
                    break;
            }
            //
            // Rule 31:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 31: {
               //#line 346 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 344 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 344 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 346 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt2(MethodModifiersopt,Modifier);
                    break;
            }
            //
            // Rule 32:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 32: {
               //#line 351 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 349 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 349 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 349 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 349 "x10/parser/x10.g"
                Object FormalParametersopt = (Object) getRhsSym(5);
                //#line 349 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 349 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 351 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDefDeclaration0(Modifiersopt,Identifier,TypeParametersopt,FormalParametersopt,WhereClauseopt,Type);
                    break;
            }
            //
            // Rule 33:  Properties ::= ( PropertyList )
            //
            case 33: {
               //#line 356 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 354 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(2);
                //#line 356 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Properties0(PropertyList);
                 break;
            } 
            //
            // Rule 34:  PropertyList ::= Property
            //
            case 34: {
               //#line 361 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 359 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(1);
                //#line 361 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList0(Property);
                    break;
            }
            //
            // Rule 35:  PropertyList ::= PropertyList , Property
            //
            case 35: {
               //#line 365 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 363 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(1);
                //#line 363 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(3);
                //#line 365 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList1(PropertyList,Property);
                    break;
            }
            //
            // Rule 36:  Property ::= Annotationsopt Identifier ResultType
            //
            case 36: {
               //#line 371 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 369 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 369 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 369 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(3);
                //#line 371 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Property0(Annotationsopt,Identifier,ResultType);
                    break;
            }
            //
            // Rule 37:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 37: {
               //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 374 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 374 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 374 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 374 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 374 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 374 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 374 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 374 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            

                                        
                    break;
            }
            //
            // Rule 38:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 38: {
               //#line 383 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 381 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 381 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 381 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 381 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 381 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(9);
                //#line 381 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(11);
                //#line 381 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(12);
                //#line 381 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(13);
                //#line 381 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(14);
                //#line 383 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration1(MethodModifiersopt,TypeParametersopt,fp1,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 39:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 39: {
               //#line 388 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 386 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 386 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 386 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 386 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(6);
                //#line 386 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(8);
                //#line 386 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(9);
                //#line 386 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 386 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 388 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration2(MethodModifiersopt,TypeParametersopt,PrefixOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 40:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 40: {
               //#line 393 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 391 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 391 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 391 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(5);
                //#line 391 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(7);
                //#line 391 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 391 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 391 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 391 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 393 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration3(MethodModifiersopt,TypeParametersopt,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                               
                    break;
            }
            //
            // Rule 41:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 41: {
               //#line 398 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 396 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 396 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 396 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 396 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 396 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 396 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 396 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 396 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 398 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration4(MethodModifiersopt,TypeParametersopt,fp1,BinOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 42:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 42: {
               //#line 403 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 401 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 401 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 401 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 401 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 401 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 401 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 401 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 403 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration5(MethodModifiersopt,TypeParametersopt,PrefixOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            
                    break;
            }
            //
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 43: {
               //#line 408 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 406 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 406 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 406 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 406 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 406 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 406 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 406 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 408 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration6(MethodModifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 44: {
               //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 411 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 411 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 411 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 411 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(8);
                //#line 411 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(10);
                //#line 411 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(11);
                //#line 411 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(12);
                //#line 411 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(13);
                //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration7(MethodModifiersopt,TypeParametersopt,FormalParameters,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Offersopt MethodBody
            //
            case 45: {
               //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 416 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 416 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 416 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 416 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 416 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 416 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 416 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 421 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 421 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 421 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 421 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 421 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 421 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 421 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 47: {
               //#line 428 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 426 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 426 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 426 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 426 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(7);
                //#line 426 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(8);
                //#line 426 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(9);
                //#line 426 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(10);
                //#line 428 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 48:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 48: {
               //#line 434 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 432 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 432 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 432 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 432 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(4);
                //#line 432 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 432 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(6);
                //#line 432 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(7);
                //#line 434 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 49:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 49: {
               //#line 439 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 437 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 437 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 437 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(3);
                //#line 437 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 437 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(5);
                //#line 439 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration1(MethodModifiersopt,Identifier,WhereClauseopt,HasResultTypeopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 50:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 50: {
               //#line 445 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 443 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 443 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 445 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 51:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 51: {
               //#line 449 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 447 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 447 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 449 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation1(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 52:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 453 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 451 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 451 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 451 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 453 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation2(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 53:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 457 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 455 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 455 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 455 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 457 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation3(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 54:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 54: {
               //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 460 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 460 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 460 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 460 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 460 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 460 "x10/parser/x10.g"
                Object ExtendsInterfacesopt = (Object) getRhsSym(7);
                //#line 460 "x10/parser/x10.g"
                Object InterfaceBody = (Object) getRhsSym(8);
                //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalInterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
                    break;
            }
            //
            // Rule 55:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 55: {
               //#line 467 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 465 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 465 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(3);
                //#line 465 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(5);
                //#line 465 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(7);
                //#line 467 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 56:  ClassInstanceCreationExpression ::= new TypeName [ Type ] [ ArgumentListopt ]
            //
            case 56: {
               //#line 471 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 469 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 469 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(4);
                //#line 469 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 471 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression1(TypeName,Type,ArgumentListopt);
                    break;
            }
            //
            // Rule 57:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 475 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 473 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 473 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 473 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 473 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 473 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 475 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 58:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 479 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 477 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 477 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 477 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 477 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 477 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 479 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression3(AmbiguousName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 59:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 484 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 482 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 482 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 484 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 62:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 62: {
               //#line 493 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 491 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(1);
                //#line 491 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(3);
                //#line 491 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 491 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(6);
                //#line 491 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 493 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
                    break;
            }
            //
            // Rule 64:  AnnotatedType ::= Type Annotations
            //
            case 64: {
               //#line 505 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 503 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 503 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(2);
                //#line 505 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotatedType0(Type,Annotations);
                    break;
            }
            //
            // Rule 67:  ConstrainedType ::= ( Type )
            //
            case 67: {
               //#line 512 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 510 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 512 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstrainedType2(Type);
                    break;
            }
            //
            // Rule 68:  SimpleNamedType ::= TypeName
            //
            case 68: {
               //#line 518 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 516 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 518 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType0(TypeName);
                    break;
            }
            //
            // Rule 69:  SimpleNamedType ::= Primary . Identifier
            //
            case 69: {
               //#line 522 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 520 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 520 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 522 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType1(Primary,Identifier);
                    break;
            }
            //
            // Rule 70:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 70: {
               //#line 526 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 524 "x10/parser/x10.g"
                Object DepNamedType = (Object) getRhsSym(1);
                //#line 524 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 526 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType2(DepNamedType,Identifier);
                    break;
            }
            //
            // Rule 71:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 71: {
               //#line 531 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 529 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 529 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(2);
                //#line 531 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType0(SimpleNamedType,DepParameters);
                    break;
            }
            //
            // Rule 72:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 72: {
               //#line 535 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 533 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 533 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 535 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType1(SimpleNamedType,Arguments);
                    break;
            }
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 73: {
               //#line 539 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 537 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 537 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 537 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 539 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 74: {
               //#line 543 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 541 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 541 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 543 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
                    break;
            }
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 75: {
               //#line 547 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 545 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 545 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 545 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 547 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
                    break;
            }
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 76: {
               //#line 551 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 549 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 549 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 549 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 551 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
                    break;
            }
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 77: {
               //#line 555 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 553 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 553 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 553 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 553 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(4);
                //#line 555 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 80:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 80: {
               //#line 563 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 561 "x10/parser/x10.g"
                Object ExistentialListopt = (Object) getRhsSym(2);
                //#line 561 "x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 563 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
                    break;
            }
            //
            // Rule 81:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 81: {
               //#line 569 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 567 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(2);
                //#line 569 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
                    break;
            }
            //
            // Rule 82:  TypeParameters ::= [ TypeParameterList ]
            //
            case 82: {
               //#line 574 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 572 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(2);
                //#line 574 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameters0(TypeParameterList);
                    break;
            }
            //
            // Rule 83:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 83: {
               //#line 579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 577 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(2);
                //#line 579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameters0(FormalParameterListopt);
                    break;
            }
            //
            // Rule 84:  Conjunction ::= Expression
            //
            case 84: {
               //#line 584 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 582 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 584 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction0(Expression);
                    break;
            }
            //
            // Rule 85:  Conjunction ::= Conjunction , Expression
            //
            case 85: {
               //#line 588 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 586 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 586 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 588 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction1(Conjunction,Expression);
                    break;
            }
            //
            // Rule 86:  HasZeroConstraint ::= Type$t1 haszero
            //
            case 86: {
               //#line 593 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 591 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 593 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasZeroConstraint0(t1);
                    break;
            }
            //
            // Rule 87:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 87: {
               //#line 598 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 596 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 596 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 598 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint0(t1,t2);
                    break;
            }
            //
            // Rule 88:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 88: {
               //#line 602 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 600 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 600 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 602 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint1(t1,t2);
                    break;
            }
            //
            // Rule 89:  WhereClause ::= DepParameters
            //
            case 89: {
               //#line 607 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 605 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(1);
                //#line 607 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhereClause0(DepParameters);
                      break;
            }
            //
            // Rule 90:  Conjunctionopt ::= $Empty
            //
            case 90: {
               //#line 612 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 612 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt0();
                      break;
            }
            //
            // Rule 91:  Conjunctionopt ::= Conjunction
            //
            case 91: {
               //#line 616 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 614 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 616 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt1(Conjunction);
                    break;
            }
            //
            // Rule 92:  ExistentialListopt ::= $Empty
            //
            case 92: {
               //#line 621 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 621 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt0();
                      break;
            }
            //
            // Rule 93:  ExistentialListopt ::= ExistentialList ;
            //
            case 93: {
               //#line 625 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 623 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 625 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt1(ExistentialList);
                    break;
            }
            //
            // Rule 94:  ExistentialList ::= FormalParameter
            //
            case 94: {
               //#line 630 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 628 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 630 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList0(FormalParameter);
                    break;
            }
            //
            // Rule 95:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 95: {
               //#line 634 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 632 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 632 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 634 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList1(ExistentialList,FormalParameter);
                    break;
            }
            //
            // Rule 98:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 98: {
               //#line 644 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 642 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 642 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 642 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 642 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 642 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 642 "x10/parser/x10.g"
                Object Superopt = (Object) getRhsSym(7);
                //#line 642 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(8);
                //#line 642 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(9);
                //#line 644 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 99:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 99: {
               //#line 650 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 648 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 648 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 648 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 648 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 648 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 648 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(7);
                //#line 648 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(8);
                //#line 650 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 100:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 100: {
               //#line 655 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 653 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 653 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 653 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 653 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 653 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 653 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 653 "x10/parser/x10.g"
                Object ConstructorBody = (Object) getRhsSym(9);
                //#line 655 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                           
                    break;
            }
            //
            // Rule 101:  Super ::= extends ClassType
            //
            case 101: {
               //#line 661 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 659 "x10/parser/x10.g"
                Object ClassType = (Object) getRhsSym(2);
                //#line 661 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Super0(ClassType);
                    break;
            }
            //
            // Rule 102:  FieldKeyword ::= val
            //
            case 102: {
               //#line 666 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 666 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword0();
                    break;
            }
            //
            // Rule 103:  FieldKeyword ::= var
            //
            case 103: {
               //#line 670 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 670 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword1();
                    break;
            }
            //
            // Rule 104:  VarKeyword ::= val
            //
            case 104: {
               //#line 677 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword0();
                    break;
            }
            //
            // Rule 105:  VarKeyword ::= var
            //
            case 105: {
               //#line 681 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword1();
                    break;
            }
            //
            // Rule 106:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 106: {
               //#line 687 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 685 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 685 "x10/parser/x10.g"
                Object FieldKeyword = (Object) getRhsSym(2);
                //#line 685 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(3);
                //#line 687 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 107:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 107: {
               //#line 693 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 691 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 691 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(2);
                //#line 693 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 110:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 110: {
               //#line 705 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 703 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 703 "x10/parser/x10.g"
                Object NonExpressionStatement = (Object) getRhsSym(2);
                //#line 705 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
                    break;
            }
            //
            // Rule 136:  OfferStatement ::= offer Expression ;
            //
            case 136: {
               //#line 738 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 736 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 738 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OfferStatement0(Expression);
                    break;
            }
            //
            // Rule 137:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 137: {
               //#line 743 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 741 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 741 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 743 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 138:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 138: {
               //#line 748 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 746 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 746 "x10/parser/x10.g"
                Object s1 = (Object) getRhsSym(5);
                //#line 746 "x10/parser/x10.g"
                Object s2 = (Object) getRhsSym(7);
                //#line 748 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenElseStatement0(Expression,s1,s2);
                    break;
            }
            //
            // Rule 139:  EmptyStatement ::= ;
            //
            case 139: {
               //#line 753 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 753 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EmptyStatement0();
                    break;
            }
            //
            // Rule 140:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 140: {
               //#line 758 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 756 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 756 "x10/parser/x10.g"
                Object LoopStatement = (Object) getRhsSym(3);
                //#line 758 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LabeledStatement0(Identifier,LoopStatement);
                    break;
            }
            //
            // Rule 145:  ExpressionStatement ::= StatementExpression ;
            //
            case 145: {
               //#line 769 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 767 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 769 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionStatement0(StatementExpression);
                    break;
            }
            //
            // Rule 153:  AssertStatement ::= assert Expression ;
            //
            case 153: {
               //#line 782 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 780 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 782 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement0(Expression);
                    break;
            }
            //
            // Rule 154:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 154: {
               //#line 786 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 784 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(2);
                //#line 784 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(4);
                //#line 786 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement1(expr1,expr2);
                    break;
            }
            //
            // Rule 155:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 155: {
               //#line 791 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 789 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 789 "x10/parser/x10.g"
                Object SwitchBlock = (Object) getRhsSym(5);
                //#line 791 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchStatement0(Expression,SwitchBlock);
                    break;
            }
            //
            // Rule 156:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 156: {
               //#line 796 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 794 "x10/parser/x10.g"
                Object SwitchBlockStatementGroupsopt = (Object) getRhsSym(2);
                //#line 794 "x10/parser/x10.g"
                Object SwitchLabelsopt = (Object) getRhsSym(3);
                //#line 796 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
                    break;
            }
            //
            // Rule 158:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 158: {
               //#line 802 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 800 "x10/parser/x10.g"
                Object SwitchBlockStatementGroups = (Object) getRhsSym(1);
                //#line 800 "x10/parser/x10.g"
                Object SwitchBlockStatementGroup = (Object) getRhsSym(2);
                //#line 802 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
                    break;
            }
            //
            // Rule 159:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 159: {
               //#line 807 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 805 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 805 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(2);
                //#line 807 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
                    break;
            }
            //
            // Rule 160:  SwitchLabels ::= SwitchLabel
            //
            case 160: {
               //#line 812 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 810 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(1);
                //#line 812 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels0(SwitchLabel);
                    break;
            }
            //
            // Rule 161:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 161: {
               //#line 816 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 814 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 814 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(2);
                //#line 816 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
                    break;
            }
            //
            // Rule 162:  SwitchLabel ::= case ConstantExpression :
            //
            case 162: {
               //#line 821 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 819 "x10/parser/x10.g"
                Object ConstantExpression = (Object) getRhsSym(2);
                //#line 821 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel0(ConstantExpression);
                    break;
            }
            //
            // Rule 163:  SwitchLabel ::= default :
            //
            case 163: {
               //#line 825 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 825 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel1();
                    break;
            }
            //
            // Rule 164:  WhileStatement ::= while ( Expression ) Statement
            //
            case 164: {
               //#line 830 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 828 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 828 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 830 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhileStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 165:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 165: {
               //#line 835 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 833 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 833 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 835 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DoStatement0(Statement,Expression);
                    break;
            }
            //
            // Rule 168:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 168: {
               //#line 843 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 841 "x10/parser/x10.g"
                Object ForInitopt = (Object) getRhsSym(3);
                //#line 841 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(5);
                //#line 841 "x10/parser/x10.g"
                Object ForUpdateopt = (Object) getRhsSym(7);
                //#line 841 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(9);
                //#line 843 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
                    break;
            }
            //
            // Rule 170:  ForInit ::= LocalVariableDeclaration
            //
            case 170: {
               //#line 849 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 847 "x10/parser/x10.g"
                Object LocalVariableDeclaration = (Object) getRhsSym(1);
                //#line 849 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInit1(LocalVariableDeclaration);
                    break;
            }
            //
            // Rule 172:  StatementExpressionList ::= StatementExpression
            //
            case 172: {
               //#line 856 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 854 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 856 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList0(StatementExpression);
                    break;
            }
            //
            // Rule 173:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 173: {
               //#line 860 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 858 "x10/parser/x10.g"
                Object StatementExpressionList = (Object) getRhsSym(1);
                //#line 858 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(3);
                //#line 860 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
                    break;
            }
            //
            // Rule 174:  BreakStatement ::= break Identifieropt ;
            //
            case 174: {
               //#line 865 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 863 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 865 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BreakStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 175:  ContinueStatement ::= continue Identifieropt ;
            //
            case 175: {
               //#line 870 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 868 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 870 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ContinueStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 176:  ReturnStatement ::= return Expressionopt ;
            //
            case 176: {
               //#line 875 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 873 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(2);
                //#line 875 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ReturnStatement0(Expressionopt);
                    break;
            }
            //
            // Rule 177:  ThrowStatement ::= throw Expression ;
            //
            case 177: {
               //#line 880 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 878 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 880 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ThrowStatement0(Expression);
                    break;
            }
            //
            // Rule 178:  TryStatement ::= try Block Catches
            //
            case 178: {
               //#line 885 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 883 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 883 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(3);
                //#line 885 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement0(Block,Catches);
                    break;
            }
            //
            // Rule 179:  TryStatement ::= try Block Catchesopt Finally
            //
            case 179: {
               //#line 889 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 887 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 887 "x10/parser/x10.g"
                Object Catchesopt = (Object) getRhsSym(3);
                //#line 887 "x10/parser/x10.g"
                Object Finally = (Object) getRhsSym(4);
                //#line 889 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement1(Block,Catchesopt,Finally);
                    break;
            }
            //
            // Rule 180:  Catches ::= CatchClause
            //
            case 180: {
               //#line 894 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 892 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(1);
                //#line 894 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches0(CatchClause);
                    break;
            }
            //
            // Rule 181:  Catches ::= Catches CatchClause
            //
            case 181: {
               //#line 898 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 896 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(1);
                //#line 896 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(2);
                //#line 898 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches1(Catches,CatchClause);
                    break;
            }
            //
            // Rule 182:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 182: {
               //#line 903 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 901 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 901 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 903 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CatchClause0(FormalParameter,Block);
                    break;
            }
            //
            // Rule 183:  Finally ::= finally Block
            //
            case 183: {
               //#line 908 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 906 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 908 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Finally0(Block);
                    break;
            }
            //
            // Rule 184:  ClockedClause ::= clocked ( ClockList )
            //
            case 184: {
               //#line 913 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 911 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(3);
                //#line 913 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClause0(ClockList);
                    break;
            }
            //
            // Rule 185:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 185: {
               //#line 919 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 917 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(2);
                //#line 917 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 919 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement0(ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 186:  AsyncStatement ::= clocked async Statement
            //
            case 186: {
               //#line 923 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 921 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 923 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement1(Statement);
                    break;
            }
            //
            // Rule 187:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 187: {
               //#line 929 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 927 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 927 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 929 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
                    break;
            }
            //
            // Rule 188:  AtomicStatement ::= atomic Statement
            //
            case 188: {
               //#line 934 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 932 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 934 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtomicStatement0(Statement);
                    break;
            }
            //
            // Rule 189:  WhenStatement ::= when ( Expression ) Statement
            //
            case 189: {
               //#line 940 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 938 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 938 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 940 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 190:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 190: {
               //#line 1002 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1000 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1000 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1000 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(7);
                //#line 1000 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(8);
                //#line 1002 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 191:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 191: {
               //#line 1006 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1004 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1004 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1006 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 192:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 192: {
               //#line 1010 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1008 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1008 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1008 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1010 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 193:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 193: {
               //#line 1014 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1012 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1012 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1014 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 194:  FinishStatement ::= finish Statement
            //
            case 194: {
               //#line 1020 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1018 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1020 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 195:  FinishStatement ::= clocked finish Statement
            //
            case 195: {
               //#line 1024 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1022 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1024 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 196:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 196: {
               //#line 1028 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1026 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1028 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 198:  NextStatement ::= next ;
            //
            case 198: {
               //#line 1035 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1035 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 199:  ResumeStatement ::= resume ;
            //
            case 199: {
               //#line 1040 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 200:  ClockList ::= Clock
            //
            case 200: {
               //#line 1045 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1043 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1045 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 201:  ClockList ::= ClockList , Clock
            //
            case 201: {
               //#line 1049 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1047 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1047 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1049 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 202:  Clock ::= Expression
            //
            case 202: {
               //#line 1055 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1053 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1055 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 204:  CastExpression ::= ExpressionName
            //
            case 204: {
               //#line 1067 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1065 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1067 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 205:  CastExpression ::= CastExpression as Type
            //
            case 205: {
               //#line 1071 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1069 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1069 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1071 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 206:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 206: {
               //#line 1077 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1075 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1077 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 207:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 207: {
               //#line 1081 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1079 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1079 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1081 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 208:  TypeParameterList ::= TypeParameter
            //
            case 208: {
               //#line 1086 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1084 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1086 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 209:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 209: {
               //#line 1090 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1088 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1088 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1090 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 210:  TypeParamWithVariance ::= Identifier
            //
            case 210: {
               //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1093 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 211:  TypeParamWithVariance ::= + Identifier
            //
            case 211: {
               //#line 1099 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1097 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1099 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= - Identifier
            //
            case 212: {
               //#line 1103 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1101 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1103 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParameter ::= Identifier
            //
            case 213: {
               //#line 1108 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1106 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1108 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 214:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 214: {
               //#line 1132 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1130 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 1130 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 1132 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentExpression0(expr1,expr2);
                    break;
            }
            //
            // Rule 215:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 215: {
               //#line 1136 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1134 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1134 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1134 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1134 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1134 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1136 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 216:  LastExpression ::= Expression
            //
            case 216: {
               //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1139 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 217:  ClosureBody ::= ConditionalExpression
            //
            case 217: {
               //#line 1146 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1144 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1146 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 218: {
               //#line 1150 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1148 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1148 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1148 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1150 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 219:  ClosureBody ::= Annotationsopt Block
            //
            case 219: {
               //#line 1154 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1152 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1152 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1154 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 220:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 220: {
               //#line 1160 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1158 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1158 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1160 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 221:  FinishExpression ::= finish ( Expression ) Block
            //
            case 221: {
               //#line 1165 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1163 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1163 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1165 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1209 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1209 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 226:  TypeName ::= Identifier
            //
            case 226: {
               //#line 1220 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1218 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1220 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 227:  TypeName ::= TypeName . Identifier
            //
            case 227: {
               //#line 1224 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1222 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1222 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1224 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 229:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 229: {
               //#line 1231 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1229 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1231 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 230:  TypeArgumentList ::= Type
            //
            case 230: {
               //#line 1237 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1235 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1237 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 231:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 231: {
               //#line 1241 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1239 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1239 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1241 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 232:  PackageName ::= Identifier
            //
            case 232: {
               //#line 1250 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1248 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1250 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 233:  PackageName ::= PackageName . Identifier
            //
            case 233: {
               //#line 1254 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1252 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1252 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1254 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 234:  ExpressionName ::= Identifier
            //
            case 234: {
               //#line 1265 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1263 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1265 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 235:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 235: {
               //#line 1269 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1267 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1267 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1269 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 236:  MethodName ::= Identifier
            //
            case 236: {
               //#line 1274 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1272 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1274 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 237:  MethodName ::= AmbiguousName . Identifier
            //
            case 237: {
               //#line 1278 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1276 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1276 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1278 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 238:  PackageOrTypeName ::= Identifier
            //
            case 238: {
               //#line 1283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1281 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 239:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 239: {
               //#line 1287 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1285 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1285 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 240:  AmbiguousName ::= Identifier
            //
            case 240: {
               //#line 1292 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1290 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1292 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 241:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 1296 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1294 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1294 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1296 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 242:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 242: {
               //#line 1303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1301 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1301 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 243: {
               //#line 1307 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1305 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1305 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1305 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1307 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1311 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1309 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1309 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1309 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1309 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1311 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 245: {
               //#line 1315 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1313 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1313 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1313 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1313 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1313 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1315 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 246:  ImportDeclarations ::= ImportDeclaration
            //
            case 246: {
               //#line 1320 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1318 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1320 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 247: {
               //#line 1324 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1322 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1324 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 248:  TypeDeclarations ::= TypeDeclaration
            //
            case 248: {
               //#line 1329 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1327 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1329 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 249: {
               //#line 1333 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1331 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1331 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1333 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 250:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 250: {
               //#line 1338 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1336 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1336 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1338 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 253:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 253: {
               //#line 1349 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1347 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1349 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 254:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 254: {
               //#line 1354 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1352 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1354 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 258:  TypeDeclaration ::= ;
            //
            case 258: {
               //#line 1368 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1368 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 259:  Interfaces ::= implements InterfaceTypeList
            //
            case 259: {
               //#line 1484 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1482 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1484 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 260:  InterfaceTypeList ::= Type
            //
            case 260: {
               //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1487 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 261:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 261: {
               //#line 1493 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1491 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1491 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1493 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 262:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 262: {
               //#line 1501 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1499 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1501 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 264:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 264: {
               //#line 1507 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1505 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1505 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1507 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 266:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 266: {
               //#line 1527 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1525 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1527 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 268:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 268: {
               //#line 1533 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1531 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1533 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 269: {
               //#line 1537 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1535 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1537 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 270: {
               //#line 1541 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1539 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1541 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 271: {
               //#line 1545 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1543 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1545 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 272: {
               //#line 1549 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1547 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1549 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 273:  ClassMemberDeclaration ::= ;
            //
            case 273: {
               //#line 1553 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1553 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 274:  FormalDeclarators ::= FormalDeclarator
            //
            case 274: {
               //#line 1558 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1556 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1558 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 275:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 275: {
               //#line 1562 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1560 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1560 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1562 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 276:  FieldDeclarators ::= FieldDeclarator
            //
            case 276: {
               //#line 1568 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1566 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1568 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 277:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 277: {
               //#line 1572 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1570 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1570 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1572 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 278:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 278: {
               //#line 1578 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1576 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1578 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 279:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 279: {
               //#line 1582 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1580 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1580 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1582 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 280:  VariableDeclarators ::= VariableDeclarator
            //
            case 280: {
               //#line 1587 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1585 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1587 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 281:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 281: {
               //#line 1591 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1589 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1589 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1591 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 283:  ResultType ::= : Type
            //
            case 283: {
               //#line 1645 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1643 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1645 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 284:  HasResultType ::= : Type
            //
            case 284: {
               //#line 1649 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1647 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1649 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 285:  HasResultType ::= <: Type
            //
            case 285: {
               //#line 1653 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1651 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1653 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 286:  FormalParameterList ::= FormalParameter
            //
            case 286: {
               //#line 1667 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1665 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1667 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 287:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 287: {
               //#line 1671 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1669 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1669 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1671 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 288:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 288: {
               //#line 1676 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1674 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1674 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1676 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 289: {
               //#line 1680 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1678 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1678 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1680 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 290: {
               //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1682 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1682 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 291:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 291: {
               //#line 1689 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1687 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1687 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1689 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 292:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 292: {
               //#line 1693 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1691 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1691 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1691 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1693 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 293:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 293: {
               //#line 1698 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1696 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1696 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1698 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 294: {
               //#line 1702 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1700 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1700 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1700 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1702 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 295:  FormalParameter ::= Type
            //
            case 295: {
               //#line 1706 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1704 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1706 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 296:  Offers ::= offers Type
            //
            case 296: {
               //#line 1844 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1842 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1844 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 297:  MethodBody ::= = LastExpression ;
            //
            case 297: {
               //#line 1850 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1848 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1850 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 298: {
               //#line 1854 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1852 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1852 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1852 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1854 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= = Annotationsopt Block
            //
            case 299: {
               //#line 1858 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1856 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1856 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1858 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= Annotationsopt Block
            //
            case 300: {
               //#line 1862 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1860 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1860 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1862 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1932 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1930 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1932 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 303:  ConstructorBody ::= ConstructorBlock
            //
            case 303: {
               //#line 1936 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1934 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1936 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 304: {
               //#line 1940 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1938 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1940 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= = AssignPropertyCall
            //
            case 305: {
               //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1942 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1951 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1949 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1949 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1951 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 308:  Arguments ::= ( ArgumentListopt )
            //
            case 308: {
               //#line 1956 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1954 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1956 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 310:  ExtendsInterfaces ::= extends Type
            //
            case 310: {
               //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2010 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 311:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 311: {
               //#line 2016 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2014 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2014 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2016 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 312:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 312: {
               //#line 2024 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2022 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2024 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 314:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 314: {
               //#line 2030 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2028 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2028 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2030 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 315: {
               //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2033 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 316: {
               //#line 2039 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2037 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2039 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 317: {
               //#line 2043 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2041 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2043 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 318: {
               //#line 2047 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2045 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2047 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 319: {
               //#line 2051 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2049 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2051 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 320: {
               //#line 2055 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2053 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2055 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 321:  InterfaceMemberDeclaration ::= ;
            //
            case 321: {
               //#line 2059 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2059 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 322:  Annotations ::= Annotation
            //
            case 322: {
               //#line 2064 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2062 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2064 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 323:  Annotations ::= Annotations Annotation
            //
            case 323: {
               //#line 2068 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2066 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2066 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2068 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 324:  Annotation ::= @ NamedType
            //
            case 324: {
               //#line 2073 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2071 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2073 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 325:  Identifier ::= IDENTIFIER$ident
            //
            case 325: {
               //#line 2087 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2085 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2087 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 326:  Block ::= { BlockStatementsopt }
            //
            case 326: {
               //#line 2122 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2120 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2122 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 327:  BlockStatements ::= BlockStatement
            //
            case 327: {
               //#line 2127 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2125 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2127 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 328:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 328: {
               //#line 2131 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2129 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2129 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2131 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 330:  BlockStatement ::= ClassDeclaration
            //
            case 330: {
               //#line 2137 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2135 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2137 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= TypeDefDeclaration
            //
            case 331: {
               //#line 2141 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2139 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2141 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 332:  BlockStatement ::= Statement
            //
            case 332: {
               //#line 2145 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2143 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2145 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 333:  IdentifierList ::= Identifier
            //
            case 333: {
               //#line 2150 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2148 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2150 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 334:  IdentifierList ::= IdentifierList , Identifier
            //
            case 334: {
               //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2152 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2152 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 335:  FormalDeclarator ::= Identifier ResultType
            //
            case 335: {
               //#line 2159 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2157 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2157 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2159 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 336: {
               //#line 2163 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2161 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2161 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2163 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 337:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 337: {
               //#line 2167 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2165 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2165 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2165 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2167 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 338:  FieldDeclarator ::= Identifier HasResultType
            //
            case 338: {
               //#line 2172 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2170 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2170 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2172 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 339:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 339: {
               //#line 2176 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2174 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2174 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2174 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2176 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 340:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2181 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2179 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2179 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2179 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2181 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2185 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2183 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2183 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2183 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2185 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 342: {
               //#line 2189 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2187 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2187 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2187 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2187 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2189 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 343: {
               //#line 2194 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2192 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2192 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2192 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2194 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2198 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2196 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2196 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2196 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2198 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 345:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 345: {
               //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2200 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2200 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2200 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2200 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 347:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 347: {
               //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2207 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2207 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 348: {
               //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2212 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2212 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 349:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 349: {
               //#line 2219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2217 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2217 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2217 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2219 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 350:  Primary ::= here
            //
            case 350: {
               //#line 2230 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2230 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 351:  Primary ::= [ ArgumentListopt ]
            //
            case 351: {
               //#line 2234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2232 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2234 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 353:  Primary ::= self
            //
            case 353: {
               //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 354:  Primary ::= this
            //
            case 354: {
               //#line 2244 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2244 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 355:  Primary ::= ClassName . this
            //
            case 355: {
               //#line 2248 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2246 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2248 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 356:  Primary ::= ( Expression )
            //
            case 356: {
               //#line 2252 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2250 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2252 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 362:  OperatorFunction ::= TypeName . +
            //
            case 362: {
               //#line 2262 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2260 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2262 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . -
            //
            case 363: {
               //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2264 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . *
            //
            case 364: {
               //#line 2270 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2268 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2270 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . /
            //
            case 365: {
               //#line 2274 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2272 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2274 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . %
            //
            case 366: {
               //#line 2278 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2276 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2278 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . &
            //
            case 367: {
               //#line 2282 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2280 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2282 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . |
            //
            case 368: {
               //#line 2286 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2284 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2286 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . ^
            //
            case 369: {
               //#line 2290 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2288 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2290 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . <<
            //
            case 370: {
               //#line 2294 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2292 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2294 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . >>
            //
            case 371: {
               //#line 2298 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2296 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2298 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . >>>
            //
            case 372: {
               //#line 2302 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2300 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2302 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . <
            //
            case 373: {
               //#line 2306 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2304 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2306 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . <=
            //
            case 374: {
               //#line 2310 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2308 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2310 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . >=
            //
            case 375: {
               //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . >
            //
            case 376: {
               //#line 2318 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2316 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2318 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . ==
            //
            case 377: {
               //#line 2322 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2320 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2322 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 378:  OperatorFunction ::= TypeName . !=
            //
            case 378: {
               //#line 2326 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2324 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2326 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 379:  Literal ::= IntegerLiteral$lit
            //
            case 379: {
               //#line 2332 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2330 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2332 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 380:  Literal ::= LongLiteral$lit
            //
            case 380: {
               //#line 2336 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2334 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2336 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 381:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 381: {
               //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2338 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 382:  Literal ::= UnsignedLongLiteral$lit
            //
            case 382: {
               //#line 2344 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2342 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2344 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 383:  Literal ::= FloatingPointLiteral$lit
            //
            case 383: {
               //#line 2348 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2346 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2348 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 384:  Literal ::= DoubleLiteral$lit
            //
            case 384: {
               //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2350 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 385:  Literal ::= BooleanLiteral
            //
            case 385: {
               //#line 2356 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2354 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2356 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 386:  Literal ::= CharacterLiteral$lit
            //
            case 386: {
               //#line 2360 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2358 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2360 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 387:  Literal ::= StringLiteral$str
            //
            case 387: {
               //#line 2364 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2362 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2364 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 388:  Literal ::= null
            //
            case 388: {
               //#line 2368 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2368 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 389:  BooleanLiteral ::= true$trueLiteral
            //
            case 389: {
               //#line 2373 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2371 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2373 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 390:  BooleanLiteral ::= false$falseLiteral
            //
            case 390: {
               //#line 2377 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2375 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2377 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 391:  ArgumentList ::= Expression
            //
            case 391: {
               //#line 2385 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2383 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2385 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 392:  ArgumentList ::= ArgumentList , Expression
            //
            case 392: {
               //#line 2389 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2387 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2387 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2389 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 393:  FieldAccess ::= Primary . Identifier
            //
            case 393: {
               //#line 2394 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2392 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2392 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2394 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 394:  FieldAccess ::= super . Identifier
            //
            case 394: {
               //#line 2398 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2396 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2398 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 395:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 395: {
               //#line 2402 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2400 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2400 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2400 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2402 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 396:  FieldAccess ::= Primary . class$c
            //
            case 396: {
               //#line 2406 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2404 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2404 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2406 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 397:  FieldAccess ::= super . class$c
            //
            case 397: {
               //#line 2410 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2408 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2410 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 398: {
               //#line 2414 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2412 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2412 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2412 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2414 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 399:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 399: {
               //#line 2419 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2417 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2417 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2417 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2419 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 400:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 400: {
               //#line 2423 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2421 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2421 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2421 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2421 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2423 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 401:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 401: {
               //#line 2427 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2425 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2425 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2425 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2427 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 402:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 402: {
               //#line 2431 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2429 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2429 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2429 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2429 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2429 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2431 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 403:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 403: {
               //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2433 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2433 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2433 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 404:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 404: {
               //#line 2440 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2438 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2438 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2440 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 405:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 405: {
               //#line 2444 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2442 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2442 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2442 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2444 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 406:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 406: {
               //#line 2448 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2446 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2446 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2448 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 407:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 407: {
               //#line 2452 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2450 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2450 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2450 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2450 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2452 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 411:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 411: {
               //#line 2461 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2459 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2461 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 412:  PostDecrementExpression ::= PostfixExpression --
            //
            case 412: {
               //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2464 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 415:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 415: {
               //#line 2473 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2471 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2473 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 416:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 416: {
               //#line 2477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2475 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2477 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 419:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 419: {
               //#line 2484 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2482 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2482 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2484 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 420:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 420: {
               //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2487 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 421:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2492 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 423:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 423: {
               //#line 2500 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2498 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2500 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 424:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 424: {
               //#line 2504 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2502 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2504 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 426:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 426: {
               //#line 2510 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2508 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2508 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2510 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 427:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 427: {
               //#line 2514 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2512 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2512 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2514 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 428:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 428: {
               //#line 2518 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2516 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2516 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2518 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 430:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 430: {
               //#line 2524 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2522 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2522 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2524 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 431:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 431: {
               //#line 2528 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2526 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2526 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2528 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 433:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 433: {
               //#line 2534 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2532 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2532 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2534 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 434:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 434: {
               //#line 2538 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2536 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2536 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2538 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 435:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 435: {
               //#line 2542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2540 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2540 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 437:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 437: {
               //#line 2548 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2546 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2546 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2548 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 441:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 441: {
               //#line 2556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2554 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2554 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 442:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 442: {
               //#line 2560 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2558 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2558 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2560 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 443:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 443: {
               //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2562 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2562 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 444:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 444: {
               //#line 2568 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2566 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2566 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2568 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 445:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 445: {
               //#line 2572 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2570 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2570 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2572 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 446:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 446: {
               //#line 2576 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2574 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2574 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2576 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 448: {
               //#line 2582 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2580 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2580 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2582 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 449:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 449: {
               //#line 2586 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2584 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2584 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2586 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 450:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 450: {
               //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2588 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2588 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 452:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 452: {
               //#line 2596 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2594 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2594 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2596 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 454:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 454: {
               //#line 2602 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2600 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2600 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2602 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 456:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 456: {
               //#line 2608 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2606 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2606 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2608 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 458:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 458: {
               //#line 2614 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2612 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2612 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2614 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 460:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 460: {
               //#line 2620 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2618 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2618 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2620 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 465:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 465: {
               //#line 2630 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2628 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2628 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2628 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2630 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 468:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 468: {
               //#line 2638 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2636 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2636 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2636 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2638 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 469:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 469: {
               //#line 2642 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2640 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2640 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2640 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2642 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 470:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 470: {
               //#line 2646 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2644 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2644 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2644 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2644 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2646 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 471:  LeftHandSide ::= ExpressionName
            //
            case 471: {
               //#line 2651 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2649 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2651 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 473:  AssignmentOperator ::= =
            //
            case 473: {
               //#line 2657 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2657 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 474:  AssignmentOperator ::= *=
            //
            case 474: {
               //#line 2661 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2661 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 475:  AssignmentOperator ::= /=
            //
            case 475: {
               //#line 2665 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2665 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 476:  AssignmentOperator ::= %=
            //
            case 476: {
               //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 477:  AssignmentOperator ::= +=
            //
            case 477: {
               //#line 2673 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2673 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 478:  AssignmentOperator ::= -=
            //
            case 478: {
               //#line 2677 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= <<=
            //
            case 479: {
               //#line 2681 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= >>=
            //
            case 480: {
               //#line 2685 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2685 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= >>>=
            //
            case 481: {
               //#line 2689 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2689 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= &=
            //
            case 482: {
               //#line 2693 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2693 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= ^=
            //
            case 483: {
               //#line 2697 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2697 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= |=
            //
            case 484: {
               //#line 2701 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2701 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 487:  PrefixOp ::= +
            //
            case 487: {
               //#line 2711 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2711 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 488:  PrefixOp ::= -
            //
            case 488: {
               //#line 2715 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2715 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 489:  PrefixOp ::= !
            //
            case 489: {
               //#line 2719 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2719 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 490:  PrefixOp ::= ~
            //
            case 490: {
               //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 491:  BinOp ::= +
            //
            case 491: {
               //#line 2728 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2728 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 492:  BinOp ::= -
            //
            case 492: {
               //#line 2732 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2732 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 493:  BinOp ::= *
            //
            case 493: {
               //#line 2736 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2736 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 494:  BinOp ::= /
            //
            case 494: {
               //#line 2740 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2740 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 495:  BinOp ::= %
            //
            case 495: {
               //#line 2744 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2744 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 496:  BinOp ::= &
            //
            case 496: {
               //#line 2748 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2748 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 497:  BinOp ::= |
            //
            case 497: {
               //#line 2752 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2752 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 498:  BinOp ::= ^
            //
            case 498: {
               //#line 2756 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2756 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 499:  BinOp ::= &&
            //
            case 499: {
               //#line 2760 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2760 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 500:  BinOp ::= ||
            //
            case 500: {
               //#line 2764 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2764 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 501:  BinOp ::= <<
            //
            case 501: {
               //#line 2768 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2768 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 502:  BinOp ::= >>
            //
            case 502: {
               //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 503:  BinOp ::= >>>
            //
            case 503: {
               //#line 2776 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2776 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 504:  BinOp ::= >=
            //
            case 504: {
               //#line 2780 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2780 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 505:  BinOp ::= <=
            //
            case 505: {
               //#line 2784 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2784 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 506:  BinOp ::= >
            //
            case 506: {
               //#line 2788 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2788 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 507:  BinOp ::= <
            //
            case 507: {
               //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 508:  BinOp ::= ==
            //
            case 508: {
               //#line 2799 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2799 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 509:  BinOp ::= !=
            //
            case 509: {
               //#line 2803 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2803 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 510:  Catchesopt ::= $Empty
            //
            case 510: {
               //#line 2811 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2811 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2819 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2817 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2819 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 514:  ForUpdateopt ::= $Empty
            //
            case 514: {
               //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2834 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2834 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 520:  SwitchLabelsopt ::= $Empty
            //
            case 520: {
               //#line 2840 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2840 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 522:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 522: {
               //#line 2846 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2846 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 524:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 524: {
               //#line 2869 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2869 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 526:  ExtendsInterfacesopt ::= $Empty
            //
            case 526: {
               //#line 2875 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2875 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2905 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2905 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 532:  BlockStatementsopt ::= $Empty
            //
            case 532: {
               //#line 2911 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2911 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2931 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2931 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 538:  Offersopt ::= $Empty
            //
            case 538: {
               //#line 2943 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2943 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 540:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 540: {
               //#line 2979 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2979 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 542:  Interfacesopt ::= $Empty
            //
            case 542: {
               //#line 2985 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2985 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2995 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2995 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 548:  FormalParametersopt ::= $Empty
            //
            case 548: {
               //#line 3001 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3001 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 550:  Annotationsopt ::= $Empty
            //
            case 550: {
               //#line 3007 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 552:  TypeDeclarationsopt ::= $Empty
            //
            case 552: {
               //#line 3013 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3013 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 554:  ImportDeclarationsopt ::= $Empty
            //
            case 554: {
               //#line 3019 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3019 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3039 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3039 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 562:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 562: {
               //#line 3045 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3045 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 564:  Propertiesopt ::= $Empty
            //
            case 564: {
               //#line 3051 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3051 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

