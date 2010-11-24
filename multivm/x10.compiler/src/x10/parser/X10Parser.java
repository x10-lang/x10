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
               //#line 197 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 195 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 197 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName0(TypeName);
                        break;
            }
            //
            // Rule 2:  PackageName ::= PackageName . ErrorId
            //
            case 2: {
               //#line 202 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 200 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 202 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName0(PackageName);
                        break;
            }
            //
            // Rule 3:  ExpressionName ::= AmbiguousName . ErrorId
            //
            case 3: {
               //#line 207 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 205 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 207 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName0(AmbiguousName);
                        break;
            }
            //
            // Rule 4:  MethodName ::= AmbiguousName . ErrorId
            //
            case 4: {
               //#line 212 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 210 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 212 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName0(AmbiguousName);
                        break;
            }
            //
            // Rule 5:  PackageOrTypeName ::= PackageOrTypeName . ErrorId
            //
            case 5: {
               //#line 217 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 215 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 217 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName0(PackageOrTypeName);
                        break;
            }
            //
            // Rule 6:  AmbiguousName ::= AmbiguousName . ErrorId
            //
            case 6: {
               //#line 222 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 220 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 222 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName0(AmbiguousName);
                        break;
            }
            //
            // Rule 7:  FieldAccess ::= Primary . ErrorId
            //
            case 7: {
               //#line 227 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 225 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 227 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess0(Primary);
                    break;
            }
            //
            // Rule 8:  FieldAccess ::= super . ErrorId
            //
            case 8: {
               //#line 231 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 231 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess1();
                    break;
            }
            //
            // Rule 9:  FieldAccess ::= ClassName . super$sup . ErrorId
            //
            case 9: {
               //#line 235 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 233 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 233 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 235 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess2(ClassName);
                    break;
            }
            //
            // Rule 10:  MethodInvocation ::= MethodPrimaryPrefix ( ArgumentListopt )
            //
            case 10: {
               //#line 240 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 238 "x10/parser/x10.g"
                Object MethodPrimaryPrefix = (Object) getRhsSym(1);
                //#line 238 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 240 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation0(MethodPrimaryPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 11:  MethodInvocation ::= MethodSuperPrefix ( ArgumentListopt )
            //
            case 11: {
               //#line 244 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 242 "x10/parser/x10.g"
                Object MethodSuperPrefix = (Object) getRhsSym(1);
                //#line 242 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 244 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation1(MethodSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 12:  MethodInvocation ::= MethodClassNameSuperPrefix ( ArgumentListopt )
            //
            case 12: {
               //#line 248 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 246 "x10/parser/x10.g"
                Object MethodClassNameSuperPrefix = (Object) getRhsSym(1);
                //#line 246 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 248 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation2(MethodClassNameSuperPrefix,ArgumentListopt);
                    break;
            }
            //
            // Rule 13:  MethodPrimaryPrefix ::= Primary . ErrorId$ErrorId
            //
            case 13: {
               //#line 253 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 251 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 251 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 253 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodPrimaryPrefix0(Primary);
                    break;
            }
            //
            // Rule 14:  MethodSuperPrefix ::= super . ErrorId$ErrorId
            //
            case 14: {
               //#line 257 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 255 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(3);
                //#line 257 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSuperPrefix0();
                    break;
            }
            //
            // Rule 15:  MethodClassNameSuperPrefix ::= ClassName . super$sup . ErrorId$ErrorId
            //
            case 15: {
               //#line 261 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 259 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 259 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 259 "x10/parser/x10.g"
                IToken ErrorId = (IToken) getRhsIToken(5);
                //#line 261 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodClassNameSuperPrefix0(ClassName);
                    break;
            }
            //
            // Rule 16:  Modifiersopt ::= $Empty
            //
            case 16: {
               //#line 270 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 270 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt0();
                    break;
            }
            //
            // Rule 17:  Modifiersopt ::= Modifiersopt Modifier
            //
            case 17: {
               //#line 274 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 272 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 272 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 274 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifiersopt1(Modifiersopt,Modifier);
                    break;
            }
            //
            // Rule 18:  Modifier ::= abstract
            //
            case 18: {
               //#line 279 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 279 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier0();
                    break;
            }
            //
            // Rule 19:  Modifier ::= Annotation
            //
            case 19: {
               //#line 283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 281 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier1(Annotation);
                    break;
            }
            //
            // Rule 20:  Modifier ::= atomic
            //
            case 20: {
               //#line 287 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier2();
                    break;
            }
            //
            // Rule 21:  Modifier ::= final
            //
            case 21: {
               //#line 296 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 296 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier3();
                    break;
            }
            //
            // Rule 22:  Modifier ::= native
            //
            case 22: {
               //#line 305 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 305 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier4();
                    break;
            }
            //
            // Rule 23:  Modifier ::= private
            //
            case 23: {
               //#line 309 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 309 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier5();
                    break;
            }
            //
            // Rule 24:  Modifier ::= protected
            //
            case 24: {
               //#line 313 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 313 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier6();
                    break;
            }
            //
            // Rule 25:  Modifier ::= public
            //
            case 25: {
               //#line 317 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier7();
                    break;
            }
            //
            // Rule 26:  Modifier ::= static
            //
            case 26: {
               //#line 321 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 321 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier8();
                    break;
            }
            //
            // Rule 27:  Modifier ::= transient
            //
            case 27: {
               //#line 325 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 325 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier9();
                    break;
            }
            //
            // Rule 28:  Modifier ::= clocked
            //
            case 28: {
               //#line 329 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 329 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Modifier10();
                    break;
            }
            //
            // Rule 30:  MethodModifiersopt ::= MethodModifiersopt property$property
            //
            case 30: {
               //#line 335 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 333 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 333 "x10/parser/x10.g"
                IToken property = (IToken) getRhsIToken(2);
                //#line 335 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt1(MethodModifiersopt);
                    break;
            }
            //
            // Rule 31:  MethodModifiersopt ::= MethodModifiersopt Modifier
            //
            case 31: {
               //#line 339 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 337 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 337 "x10/parser/x10.g"
                Object Modifier = (Object) getRhsSym(2);
                //#line 339 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodModifiersopt2(MethodModifiersopt,Modifier);
                    break;
            }
            //
            // Rule 32:  TypeDefDeclaration ::= Modifiersopt type Identifier TypeParametersopt FormalParametersopt WhereClauseopt = Type ;
            //
            case 32: {
               //#line 344 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 342 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 342 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 342 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 342 "x10/parser/x10.g"
                Object FormalParametersopt = (Object) getRhsSym(5);
                //#line 342 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 342 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 344 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDefDeclaration0(Modifiersopt,Identifier,TypeParametersopt,FormalParametersopt,WhereClauseopt,Type);
                    break;
            }
            //
            // Rule 33:  Properties ::= ( PropertyList )
            //
            case 33: {
               //#line 349 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 347 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(2);
                //#line 349 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Properties0(PropertyList);
                 break;
            } 
            //
            // Rule 34:  PropertyList ::= Property
            //
            case 34: {
               //#line 354 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 352 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(1);
                //#line 354 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList0(Property);
                    break;
            }
            //
            // Rule 35:  PropertyList ::= PropertyList , Property
            //
            case 35: {
               //#line 358 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 356 "x10/parser/x10.g"
                Object PropertyList = (Object) getRhsSym(1);
                //#line 356 "x10/parser/x10.g"
                Object Property = (Object) getRhsSym(3);
                //#line 358 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyList1(PropertyList,Property);
                    break;
            }
            //
            // Rule 36:  Property ::= Annotationsopt Identifier ResultType
            //
            case 36: {
               //#line 364 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 362 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 362 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 362 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(3);
                //#line 364 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Property0(Annotationsopt,Identifier,ResultType);
                    break;
            }
            //
            // Rule 37:  MethodDeclaration ::= MethodModifiersopt def Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 37: {
               //#line 369 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 367 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 367 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 367 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 367 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 367 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 367 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 367 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 367 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 369 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            

                                        
                    break;
            }
            //
            // Rule 38:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 38: {
               //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 374 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 374 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 374 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 374 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 374 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(9);
                //#line 374 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(11);
                //#line 374 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(12);
                //#line 374 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(13);
                //#line 374 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(14);
                //#line 376 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration1(MethodModifiersopt,TypeParametersopt,fp1,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 39:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 39: {
               //#line 381 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 379 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 379 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 379 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 379 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(6);
                //#line 379 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(8);
                //#line 379 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(9);
                //#line 379 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 379 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 381 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration2(MethodModifiersopt,TypeParametersopt,PrefixOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                
                    break;
            }
            //
            // Rule 40:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt this BinOp ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 40: {
               //#line 386 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 384 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 384 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 384 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(5);
                //#line 384 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(7);
                //#line 384 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 384 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 384 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 384 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 386 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration3(MethodModifiersopt,TypeParametersopt,BinOp,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                               
                    break;
            }
            //
            // Rule 41:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) BinOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 41: {
               //#line 391 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 389 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 389 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 389 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 389 "x10/parser/x10.g"
                Object BinOp = (Object) getRhsSym(7);
                //#line 389 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 389 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 389 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 389 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 391 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration4(MethodModifiersopt,TypeParametersopt,fp1,BinOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 42:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt PrefixOp this WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 42: {
               //#line 396 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 394 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 394 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 394 "x10/parser/x10.g"
                Object PrefixOp = (Object) getRhsSym(4);
                //#line 394 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 394 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 394 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 394 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 396 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration5(MethodModifiersopt,TypeParametersopt,PrefixOp,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                            
                    break;
            }
            //
            // Rule 43:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 43: {
               //#line 401 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 399 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 399 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 399 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 399 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 399 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 399 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 399 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(9);
                //#line 401 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration6(MethodModifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 44:  MethodDeclaration ::= MethodModifiersopt operator this TypeParametersopt FormalParameters = ( FormalParameter$fp2 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 44: {
               //#line 406 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 404 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 404 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 404 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 404 "x10/parser/x10.g"
                Object fp2 = (Object) getRhsSym(8);
                //#line 404 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(10);
                //#line 404 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(11);
                //#line 404 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(12);
                //#line 404 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(13);
                //#line 406 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration7(MethodModifiersopt,TypeParametersopt,FormalParameters,fp2,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 45:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as Type WhereClauseopt Offersopt MethodBody
            //
            case 45: {
               //#line 411 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 409 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 409 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 409 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 409 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 409 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 409 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 409 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 411 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 416 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 414 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 414 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 414 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 414 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 414 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 414 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 414 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 416 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 47: {
               //#line 421 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 419 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 419 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 419 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 419 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(7);
                //#line 419 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(8);
                //#line 419 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(9);
                //#line 419 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(10);
                //#line 421 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 48:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 48: {
               //#line 427 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 425 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 425 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 425 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 425 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(4);
                //#line 425 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 425 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(6);
                //#line 425 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(7);
                //#line 427 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 49:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 49: {
               //#line 432 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 430 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 430 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 430 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(3);
                //#line 430 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 430 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(5);
                //#line 432 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration1(MethodModifiersopt,Identifier,WhereClauseopt,HasResultTypeopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 50:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 50: {
               //#line 438 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 436 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 436 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 438 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 51:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 51: {
               //#line 442 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 440 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 440 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 442 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation1(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 52:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 446 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 444 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 444 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 444 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 446 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation2(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 53:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 450 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 448 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 448 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 448 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 450 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation3(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 54:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 54: {
               //#line 455 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 453 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 453 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 453 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 453 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 453 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 453 "x10/parser/x10.g"
                Object ExtendsInterfacesopt = (Object) getRhsSym(7);
                //#line 453 "x10/parser/x10.g"
                Object InterfaceBody = (Object) getRhsSym(8);
                //#line 455 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalInterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
                    break;
            }
            //
            // Rule 55:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 55: {
               //#line 460 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 458 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 458 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(3);
                //#line 458 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(5);
                //#line 458 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(7);
                //#line 460 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 56:  ClassInstanceCreationExpression ::= new TypeName [ Type ] [ ArgumentListopt ]
            //
            case 56: {
               //#line 464 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 462 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 462 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(4);
                //#line 462 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 464 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression1(TypeName,Type,ArgumentListopt);
                    break;
            }
            //
            // Rule 57:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 468 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 466 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 466 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 466 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 466 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 466 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 468 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 58:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 472 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 470 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 470 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 470 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 470 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 470 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 472 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression3(AmbiguousName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 59:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 475 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 475 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 477 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 62:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 62: {
               //#line 486 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 484 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(1);
                //#line 484 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(3);
                //#line 484 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 484 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(6);
                //#line 484 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 486 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
                    break;
            }
            //
            // Rule 64:  AnnotatedType ::= Type Annotations
            //
            case 64: {
               //#line 498 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 496 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 496 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(2);
                //#line 498 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotatedType0(Type,Annotations);
                    break;
            }
            //
            // Rule 67:  ConstrainedType ::= ( Type )
            //
            case 67: {
               //#line 505 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 503 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 505 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstrainedType2(Type);
                    break;
            }
            //
            // Rule 68:  SimpleNamedType ::= TypeName
            //
            case 68: {
               //#line 511 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 509 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 511 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType0(TypeName);
                    break;
            }
            //
            // Rule 69:  SimpleNamedType ::= Primary . Identifier
            //
            case 69: {
               //#line 515 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 513 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 513 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 515 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType1(Primary,Identifier);
                    break;
            }
            //
            // Rule 70:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 70: {
               //#line 519 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 517 "x10/parser/x10.g"
                Object DepNamedType = (Object) getRhsSym(1);
                //#line 517 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 519 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType2(DepNamedType,Identifier);
                    break;
            }
            //
            // Rule 71:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 71: {
               //#line 524 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 522 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 522 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(2);
                //#line 524 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType0(SimpleNamedType,DepParameters);
                    break;
            }
            //
            // Rule 72:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 72: {
               //#line 528 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 526 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 526 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 528 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType1(SimpleNamedType,Arguments);
                    break;
            }
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 73: {
               //#line 532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 530 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 530 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 530 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 532 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 74: {
               //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 534 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 534 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
                    break;
            }
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 75: {
               //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 538 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 538 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 538 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
                    break;
            }
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 76: {
               //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 542 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 542 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 542 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
                    break;
            }
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 77: {
               //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 546 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 546 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 546 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 546 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(4);
                //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 80:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 80: {
               //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 554 "x10/parser/x10.g"
                Object ExistentialListopt = (Object) getRhsSym(2);
                //#line 554 "x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
                    break;
            }
            //
            // Rule 81:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 81: {
               //#line 562 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 560 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(2);
                //#line 562 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
                    break;
            }
            //
            // Rule 82:  TypeParameters ::= [ TypeParameterList ]
            //
            case 82: {
               //#line 567 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 565 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(2);
                //#line 567 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameters0(TypeParameterList);
                    break;
            }
            //
            // Rule 83:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 83: {
               //#line 572 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 570 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(2);
                //#line 572 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameters0(FormalParameterListopt);
                    break;
            }
            //
            // Rule 84:  Conjunction ::= Expression
            //
            case 84: {
               //#line 577 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 575 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 577 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction0(Expression);
                    break;
            }
            //
            // Rule 85:  Conjunction ::= Conjunction , Expression
            //
            case 85: {
               //#line 581 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 579 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 579 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 581 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction1(Conjunction,Expression);
                    break;
            }
            //
            // Rule 86:  HasZeroConstraint ::= Type$t1 haszero
            //
            case 86: {
               //#line 586 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 584 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 586 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasZeroConstraint0(t1);
                    break;
            }
            //
            // Rule 87:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 87: {
               //#line 591 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 589 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 589 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 591 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint0(t1,t2);
                    break;
            }
            //
            // Rule 88:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 88: {
               //#line 595 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 593 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 593 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 595 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint1(t1,t2);
                    break;
            }
            //
            // Rule 89:  WhereClause ::= DepParameters
            //
            case 89: {
               //#line 600 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 598 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(1);
                //#line 600 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhereClause0(DepParameters);
                      break;
            }
            //
            // Rule 90:  Conjunctionopt ::= $Empty
            //
            case 90: {
               //#line 605 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 605 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt0();
                      break;
            }
            //
            // Rule 91:  Conjunctionopt ::= Conjunction
            //
            case 91: {
               //#line 609 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 607 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 609 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt1(Conjunction);
                    break;
            }
            //
            // Rule 92:  ExistentialListopt ::= $Empty
            //
            case 92: {
               //#line 614 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 614 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt0();
                      break;
            }
            //
            // Rule 93:  ExistentialListopt ::= ExistentialList ;
            //
            case 93: {
               //#line 618 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 616 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 618 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt1(ExistentialList);
                    break;
            }
            //
            // Rule 94:  ExistentialList ::= FormalParameter
            //
            case 94: {
               //#line 623 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 621 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 623 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList0(FormalParameter);
                    break;
            }
            //
            // Rule 95:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 95: {
               //#line 627 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 625 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 625 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 627 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList1(ExistentialList,FormalParameter);
                    break;
            }
            //
            // Rule 98:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 98: {
               //#line 637 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 635 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 635 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 635 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 635 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 635 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 635 "x10/parser/x10.g"
                Object Superopt = (Object) getRhsSym(7);
                //#line 635 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(8);
                //#line 635 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(9);
                //#line 637 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 99:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 99: {
               //#line 643 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 641 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 641 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 641 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 641 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 641 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 641 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(7);
                //#line 641 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(8);
                //#line 643 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 100:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 100: {
               //#line 648 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 646 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 646 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 646 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 646 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 646 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 646 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 646 "x10/parser/x10.g"
                Object ConstructorBody = (Object) getRhsSym(9);
                //#line 648 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                           
                    break;
            }
            //
            // Rule 101:  Super ::= extends ClassType
            //
            case 101: {
               //#line 654 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 652 "x10/parser/x10.g"
                Object ClassType = (Object) getRhsSym(2);
                //#line 654 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Super0(ClassType);
                    break;
            }
            //
            // Rule 102:  FieldKeyword ::= val
            //
            case 102: {
               //#line 659 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 659 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword0();
                    break;
            }
            //
            // Rule 103:  FieldKeyword ::= var
            //
            case 103: {
               //#line 663 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 663 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword1();
                    break;
            }
            //
            // Rule 104:  VarKeyword ::= val
            //
            case 104: {
               //#line 670 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 670 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword0();
                    break;
            }
            //
            // Rule 105:  VarKeyword ::= var
            //
            case 105: {
               //#line 674 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 674 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword1();
                    break;
            }
            //
            // Rule 106:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 106: {
               //#line 680 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 678 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 678 "x10/parser/x10.g"
                Object FieldKeyword = (Object) getRhsSym(2);
                //#line 678 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(3);
                //#line 680 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 107:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 107: {
               //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 684 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 684 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(2);
                //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 110:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 110: {
               //#line 698 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 696 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 696 "x10/parser/x10.g"
                Object NonExpressionStatement = (Object) getRhsSym(2);
                //#line 698 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
                    break;
            }
            //
            // Rule 136:  OfferStatement ::= offer Expression ;
            //
            case 136: {
               //#line 731 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 729 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 731 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OfferStatement0(Expression);
                    break;
            }
            //
            // Rule 137:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 137: {
               //#line 736 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 734 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 734 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 736 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 138:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 138: {
               //#line 741 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 739 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 739 "x10/parser/x10.g"
                Object s1 = (Object) getRhsSym(5);
                //#line 739 "x10/parser/x10.g"
                Object s2 = (Object) getRhsSym(7);
                //#line 741 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenElseStatement0(Expression,s1,s2);
                    break;
            }
            //
            // Rule 139:  EmptyStatement ::= ;
            //
            case 139: {
               //#line 746 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 746 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EmptyStatement0();
                    break;
            }
            //
            // Rule 140:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 140: {
               //#line 751 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 749 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 749 "x10/parser/x10.g"
                Object LoopStatement = (Object) getRhsSym(3);
                //#line 751 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LabeledStatement0(Identifier,LoopStatement);
                    break;
            }
            //
            // Rule 145:  ExpressionStatement ::= StatementExpression ;
            //
            case 145: {
               //#line 762 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 760 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 762 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionStatement0(StatementExpression);
                    break;
            }
            //
            // Rule 153:  AssertStatement ::= assert Expression ;
            //
            case 153: {
               //#line 775 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 773 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 775 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement0(Expression);
                    break;
            }
            //
            // Rule 154:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 154: {
               //#line 779 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 777 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(2);
                //#line 777 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(4);
                //#line 779 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement1(expr1,expr2);
                    break;
            }
            //
            // Rule 155:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 155: {
               //#line 784 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 782 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 782 "x10/parser/x10.g"
                Object SwitchBlock = (Object) getRhsSym(5);
                //#line 784 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchStatement0(Expression,SwitchBlock);
                    break;
            }
            //
            // Rule 156:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 156: {
               //#line 789 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 787 "x10/parser/x10.g"
                Object SwitchBlockStatementGroupsopt = (Object) getRhsSym(2);
                //#line 787 "x10/parser/x10.g"
                Object SwitchLabelsopt = (Object) getRhsSym(3);
                //#line 789 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
                    break;
            }
            //
            // Rule 158:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 158: {
               //#line 795 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 793 "x10/parser/x10.g"
                Object SwitchBlockStatementGroups = (Object) getRhsSym(1);
                //#line 793 "x10/parser/x10.g"
                Object SwitchBlockStatementGroup = (Object) getRhsSym(2);
                //#line 795 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
                    break;
            }
            //
            // Rule 159:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 159: {
               //#line 800 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 798 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 798 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(2);
                //#line 800 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
                    break;
            }
            //
            // Rule 160:  SwitchLabels ::= SwitchLabel
            //
            case 160: {
               //#line 805 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 803 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(1);
                //#line 805 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels0(SwitchLabel);
                    break;
            }
            //
            // Rule 161:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 161: {
               //#line 809 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 807 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 807 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(2);
                //#line 809 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
                    break;
            }
            //
            // Rule 162:  SwitchLabel ::= case ConstantExpression :
            //
            case 162: {
               //#line 814 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 812 "x10/parser/x10.g"
                Object ConstantExpression = (Object) getRhsSym(2);
                //#line 814 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel0(ConstantExpression);
                    break;
            }
            //
            // Rule 163:  SwitchLabel ::= default :
            //
            case 163: {
               //#line 818 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 818 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel1();
                    break;
            }
            //
            // Rule 164:  WhileStatement ::= while ( Expression ) Statement
            //
            case 164: {
               //#line 823 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 821 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 821 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 823 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhileStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 165:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 165: {
               //#line 828 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 826 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 826 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 828 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DoStatement0(Statement,Expression);
                    break;
            }
            //
            // Rule 168:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 168: {
               //#line 836 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 834 "x10/parser/x10.g"
                Object ForInitopt = (Object) getRhsSym(3);
                //#line 834 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(5);
                //#line 834 "x10/parser/x10.g"
                Object ForUpdateopt = (Object) getRhsSym(7);
                //#line 834 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(9);
                //#line 836 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
                    break;
            }
            //
            // Rule 170:  ForInit ::= LocalVariableDeclaration
            //
            case 170: {
               //#line 842 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 840 "x10/parser/x10.g"
                Object LocalVariableDeclaration = (Object) getRhsSym(1);
                //#line 842 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInit1(LocalVariableDeclaration);
                    break;
            }
            //
            // Rule 172:  StatementExpressionList ::= StatementExpression
            //
            case 172: {
               //#line 849 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 847 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 849 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList0(StatementExpression);
                    break;
            }
            //
            // Rule 173:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 173: {
               //#line 853 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 851 "x10/parser/x10.g"
                Object StatementExpressionList = (Object) getRhsSym(1);
                //#line 851 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(3);
                //#line 853 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
                    break;
            }
            //
            // Rule 174:  BreakStatement ::= break Identifieropt ;
            //
            case 174: {
               //#line 858 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 856 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 858 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BreakStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 175:  ContinueStatement ::= continue Identifieropt ;
            //
            case 175: {
               //#line 863 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 861 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 863 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ContinueStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 176:  ReturnStatement ::= return Expressionopt ;
            //
            case 176: {
               //#line 868 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 866 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(2);
                //#line 868 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ReturnStatement0(Expressionopt);
                    break;
            }
            //
            // Rule 177:  ThrowStatement ::= throw Expression ;
            //
            case 177: {
               //#line 873 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 871 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 873 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ThrowStatement0(Expression);
                    break;
            }
            //
            // Rule 178:  TryStatement ::= try Block Catches
            //
            case 178: {
               //#line 878 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 876 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 876 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(3);
                //#line 878 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement0(Block,Catches);
                    break;
            }
            //
            // Rule 179:  TryStatement ::= try Block Catchesopt Finally
            //
            case 179: {
               //#line 882 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 880 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 880 "x10/parser/x10.g"
                Object Catchesopt = (Object) getRhsSym(3);
                //#line 880 "x10/parser/x10.g"
                Object Finally = (Object) getRhsSym(4);
                //#line 882 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement1(Block,Catchesopt,Finally);
                    break;
            }
            //
            // Rule 180:  Catches ::= CatchClause
            //
            case 180: {
               //#line 887 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 885 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(1);
                //#line 887 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches0(CatchClause);
                    break;
            }
            //
            // Rule 181:  Catches ::= Catches CatchClause
            //
            case 181: {
               //#line 891 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 889 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(1);
                //#line 889 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(2);
                //#line 891 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches1(Catches,CatchClause);
                    break;
            }
            //
            // Rule 182:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 182: {
               //#line 896 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 894 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 894 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 896 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CatchClause0(FormalParameter,Block);
                    break;
            }
            //
            // Rule 183:  Finally ::= finally Block
            //
            case 183: {
               //#line 901 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 899 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 901 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Finally0(Block);
                    break;
            }
            //
            // Rule 184:  ClockedClause ::= clocked ( ClockList )
            //
            case 184: {
               //#line 906 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 904 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(3);
                //#line 906 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClause0(ClockList);
                    break;
            }
            //
            // Rule 185:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 185: {
               //#line 912 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 910 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(2);
                //#line 910 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 912 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement0(ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 186:  AsyncStatement ::= clocked async Statement
            //
            case 186: {
               //#line 916 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 914 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 916 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement1(Statement);
                    break;
            }
            //
            // Rule 187:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 187: {
               //#line 922 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 920 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 920 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 922 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
                    break;
            }
            //
            // Rule 188:  AtomicStatement ::= atomic Statement
            //
            case 188: {
               //#line 927 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 925 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 927 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtomicStatement0(Statement);
                    break;
            }
            //
            // Rule 189:  WhenStatement ::= when ( Expression ) Statement
            //
            case 189: {
               //#line 933 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 931 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 931 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 933 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 190:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 190: {
               //#line 995 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 993 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 993 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 993 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(7);
                //#line 993 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(8);
                //#line 995 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 191:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 191: {
               //#line 999 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 997 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 997 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 999 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 192:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 192: {
               //#line 1003 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1001 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1001 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1001 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1003 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 193:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 193: {
               //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1005 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1005 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 194:  FinishStatement ::= finish Statement
            //
            case 194: {
               //#line 1013 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1011 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1013 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 195:  FinishStatement ::= clocked finish Statement
            //
            case 195: {
               //#line 1017 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1015 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1017 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 196:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 196: {
               //#line 1021 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1019 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1021 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 198:  NextStatement ::= next ;
            //
            case 198: {
               //#line 1028 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1028 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 199:  ResumeStatement ::= resume ;
            //
            case 199: {
               //#line 1033 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1033 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 200:  ClockList ::= Clock
            //
            case 200: {
               //#line 1038 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1036 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1038 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 201:  ClockList ::= ClockList , Clock
            //
            case 201: {
               //#line 1042 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1040 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1040 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1042 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 202:  Clock ::= Expression
            //
            case 202: {
               //#line 1048 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1046 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1048 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 204:  CastExpression ::= ExpressionName
            //
            case 204: {
               //#line 1060 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1058 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1060 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 205:  CastExpression ::= CastExpression as Type
            //
            case 205: {
               //#line 1064 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1062 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1062 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1064 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 206:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 206: {
               //#line 1070 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1068 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1070 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 207:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 207: {
               //#line 1074 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1072 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1072 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1074 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 208:  TypeParameterList ::= TypeParameter
            //
            case 208: {
               //#line 1079 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1077 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1079 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 209:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 209: {
               //#line 1083 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1081 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1081 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1083 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 210:  TypeParamWithVariance ::= Identifier
            //
            case 210: {
               //#line 1088 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1086 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1088 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 211:  TypeParamWithVariance ::= + Identifier
            //
            case 211: {
               //#line 1092 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1090 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1092 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= - Identifier
            //
            case 212: {
               //#line 1096 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1094 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1096 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParameter ::= Identifier
            //
            case 213: {
               //#line 1101 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1099 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1101 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 214:  AssignmentExpression ::= Expression$expr1 -> Expression$expr2
            //
            case 214: {
               //#line 1125 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1123 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 1123 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 1125 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentExpression0(expr1,expr2);
                    break;
            }
            //
            // Rule 215:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 215: {
               //#line 1129 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1127 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1127 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1127 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1127 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1127 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1129 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 216:  LastExpression ::= Expression
            //
            case 216: {
               //#line 1134 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1132 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1134 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 217:  ClosureBody ::= ConditionalExpression
            //
            case 217: {
               //#line 1139 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1137 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1139 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 218: {
               //#line 1143 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1141 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1141 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1141 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1143 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 219:  ClosureBody ::= Annotationsopt Block
            //
            case 219: {
               //#line 1147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1145 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1145 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1147 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 220:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 220: {
               //#line 1153 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1151 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1151 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1153 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 221:  FinishExpression ::= finish ( Expression ) Block
            //
            case 221: {
               //#line 1158 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1156 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1156 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1158 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1202 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1202 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 226:  TypeName ::= Identifier
            //
            case 226: {
               //#line 1213 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1211 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1213 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 227:  TypeName ::= TypeName . Identifier
            //
            case 227: {
               //#line 1217 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1215 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1215 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1217 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 229:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 229: {
               //#line 1224 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1222 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1224 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 230:  TypeArgumentList ::= Type
            //
            case 230: {
               //#line 1230 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1228 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1230 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 231:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 231: {
               //#line 1234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1232 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1232 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1234 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 232:  PackageName ::= Identifier
            //
            case 232: {
               //#line 1243 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1241 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1243 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 233:  PackageName ::= PackageName . Identifier
            //
            case 233: {
               //#line 1247 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1245 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1245 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1247 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 234:  ExpressionName ::= Identifier
            //
            case 234: {
               //#line 1258 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1256 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1258 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 235:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 235: {
               //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1260 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1260 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 236:  MethodName ::= Identifier
            //
            case 236: {
               //#line 1267 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1265 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1267 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 237:  MethodName ::= AmbiguousName . Identifier
            //
            case 237: {
               //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1269 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1269 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 238:  PackageOrTypeName ::= Identifier
            //
            case 238: {
               //#line 1276 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1274 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1276 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 239:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 239: {
               //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1278 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1278 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 240:  AmbiguousName ::= Identifier
            //
            case 240: {
               //#line 1285 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1283 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1285 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 241:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 241: {
               //#line 1289 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1287 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1287 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1289 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 242:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 242: {
               //#line 1296 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1294 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1294 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1296 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 243: {
               //#line 1300 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1298 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1298 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1298 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1300 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1302 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1302 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1302 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 245: {
               //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1306 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1306 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1306 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1306 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1306 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 246:  ImportDeclarations ::= ImportDeclaration
            //
            case 246: {
               //#line 1313 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1311 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1313 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 247: {
               //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1315 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1315 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 248:  TypeDeclarations ::= TypeDeclaration
            //
            case 248: {
               //#line 1322 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1320 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1322 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 249: {
               //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1324 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1324 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 250:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 250: {
               //#line 1331 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1329 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1329 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1331 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 253:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 253: {
               //#line 1342 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1340 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1342 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 254:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 254: {
               //#line 1347 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1345 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1347 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 258:  TypeDeclaration ::= ;
            //
            case 258: {
               //#line 1361 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1361 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 259:  Interfaces ::= implements InterfaceTypeList
            //
            case 259: {
               //#line 1477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1475 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1477 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 260:  InterfaceTypeList ::= Type
            //
            case 260: {
               //#line 1482 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1480 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1482 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 261:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 261: {
               //#line 1486 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1484 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1484 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1486 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 262:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 262: {
               //#line 1494 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1492 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1494 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 264:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 264: {
               //#line 1500 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1498 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1498 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1500 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 266:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 266: {
               //#line 1520 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1518 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1520 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 268:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 268: {
               //#line 1526 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1524 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1526 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 269: {
               //#line 1530 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1528 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1530 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 270: {
               //#line 1534 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1532 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1534 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 271: {
               //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1536 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 272: {
               //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1540 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 273:  ClassMemberDeclaration ::= ;
            //
            case 273: {
               //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 274:  FormalDeclarators ::= FormalDeclarator
            //
            case 274: {
               //#line 1551 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1549 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1551 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 275:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 275: {
               //#line 1555 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1553 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1553 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1555 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 276:  FieldDeclarators ::= FieldDeclarator
            //
            case 276: {
               //#line 1561 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1559 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1561 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 277:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 277: {
               //#line 1565 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1563 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1563 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1565 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 278:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 278: {
               //#line 1571 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1569 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1571 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 279:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 279: {
               //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1573 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1573 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 280:  VariableDeclarators ::= VariableDeclarator
            //
            case 280: {
               //#line 1580 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1578 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1580 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 281:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 281: {
               //#line 1584 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1582 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1582 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1584 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 283:  ResultType ::= : Type
            //
            case 283: {
               //#line 1638 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1636 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1638 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 284:  HasResultType ::= : Type
            //
            case 284: {
               //#line 1642 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1640 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1642 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 285:  HasResultType ::= <: Type
            //
            case 285: {
               //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1644 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 286:  FormalParameterList ::= FormalParameter
            //
            case 286: {
               //#line 1660 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1658 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1660 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 287:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 287: {
               //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1662 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1662 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 288:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 288: {
               //#line 1669 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1667 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1667 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1669 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 289: {
               //#line 1673 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1671 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1671 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1673 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 290: {
               //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1675 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1675 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1675 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 291:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 291: {
               //#line 1682 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1680 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1680 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1682 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 292:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 292: {
               //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1684 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1684 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1684 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 293:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 293: {
               //#line 1691 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1689 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1689 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1691 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 294: {
               //#line 1695 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1693 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1693 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1693 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1695 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 295:  FormalParameter ::= Type
            //
            case 295: {
               //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1697 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 296:  Offers ::= offers Type
            //
            case 296: {
               //#line 1837 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1835 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1837 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 297:  MethodBody ::= = LastExpression ;
            //
            case 297: {
               //#line 1843 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1841 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1843 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 298: {
               //#line 1847 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1845 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1845 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1845 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1847 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= = Annotationsopt Block
            //
            case 299: {
               //#line 1851 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1849 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1851 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= Annotationsopt Block
            //
            case 300: {
               //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1853 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1853 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1925 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1923 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1925 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 303:  ConstructorBody ::= ConstructorBlock
            //
            case 303: {
               //#line 1929 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1927 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1929 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 304: {
               //#line 1933 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1931 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1933 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= = AssignPropertyCall
            //
            case 305: {
               //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1935 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1942 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1942 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 308:  Arguments ::= ( ArgumentListopt )
            //
            case 308: {
               //#line 1949 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1947 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1949 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 310:  ExtendsInterfaces ::= extends Type
            //
            case 310: {
               //#line 2005 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2003 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2005 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 311:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 311: {
               //#line 2009 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2007 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2007 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2009 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 312:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 312: {
               //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2015 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2017 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 314:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 314: {
               //#line 2023 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2021 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2021 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2023 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 315: {
               //#line 2028 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2026 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2028 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 316: {
               //#line 2032 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2030 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2032 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 317: {
               //#line 2036 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2034 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2036 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 318: {
               //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 319: {
               //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 320: {
               //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 321:  InterfaceMemberDeclaration ::= ;
            //
            case 321: {
               //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 322:  Annotations ::= Annotation
            //
            case 322: {
               //#line 2057 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2055 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2057 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 323:  Annotations ::= Annotations Annotation
            //
            case 323: {
               //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2059 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 324:  Annotation ::= @ NamedType
            //
            case 324: {
               //#line 2066 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2064 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2066 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 325:  Identifier ::= IDENTIFIER$ident
            //
            case 325: {
               //#line 2080 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2078 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2080 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 326:  Block ::= { BlockStatementsopt }
            //
            case 326: {
               //#line 2115 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2113 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2115 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 327:  BlockStatements ::= BlockStatement
            //
            case 327: {
               //#line 2120 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2118 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2120 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 328:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 328: {
               //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2122 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2122 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 330:  BlockStatement ::= ClassDeclaration
            //
            case 330: {
               //#line 2130 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2128 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2130 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= TypeDefDeclaration
            //
            case 331: {
               //#line 2134 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2132 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2134 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 332:  BlockStatement ::= Statement
            //
            case 332: {
               //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2136 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 333:  IdentifierList ::= Identifier
            //
            case 333: {
               //#line 2143 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2141 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2143 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 334:  IdentifierList ::= IdentifierList , Identifier
            //
            case 334: {
               //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2145 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2145 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 335:  FormalDeclarator ::= Identifier ResultType
            //
            case 335: {
               //#line 2152 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2150 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2150 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2152 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 336: {
               //#line 2156 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2154 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2154 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2156 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 337:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 337: {
               //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2158 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2158 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 338:  FieldDeclarator ::= Identifier HasResultType
            //
            case 338: {
               //#line 2165 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2163 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2163 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2165 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 339:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 339: {
               //#line 2169 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2167 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2167 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2167 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2169 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 340:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2174 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2172 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2172 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2172 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2174 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2178 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2176 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2176 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2176 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2178 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 342: {
               //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2180 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2180 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2180 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2180 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 343: {
               //#line 2187 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2185 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2185 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2185 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2187 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2191 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2189 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2189 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2189 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2191 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 345:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 345: {
               //#line 2195 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2193 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2193 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2193 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2193 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2195 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 347:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 347: {
               //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2200 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2200 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2200 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 348: {
               //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2205 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2205 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 349:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 349: {
               //#line 2212 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2210 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2210 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2210 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2212 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 350:  Primary ::= here
            //
            case 350: {
               //#line 2223 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2223 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 351:  Primary ::= [ ArgumentListopt ]
            //
            case 351: {
               //#line 2227 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2225 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2227 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 353:  Primary ::= self
            //
            case 353: {
               //#line 2233 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2233 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 354:  Primary ::= this
            //
            case 354: {
               //#line 2237 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2237 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 355:  Primary ::= ClassName . this
            //
            case 355: {
               //#line 2241 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2239 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2241 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 356:  Primary ::= ( Expression )
            //
            case 356: {
               //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2243 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 362:  OperatorFunction ::= TypeName . +
            //
            case 362: {
               //#line 2255 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2253 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2255 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . -
            //
            case 363: {
               //#line 2259 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2257 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2259 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . *
            //
            case 364: {
               //#line 2263 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2261 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2263 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . /
            //
            case 365: {
               //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2265 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . %
            //
            case 366: {
               //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2269 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . &
            //
            case 367: {
               //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2273 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . |
            //
            case 368: {
               //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . ^
            //
            case 369: {
               //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2281 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . <<
            //
            case 370: {
               //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2285 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . >>
            //
            case 371: {
               //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2289 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . >>>
            //
            case 372: {
               //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . <
            //
            case 373: {
               //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . <=
            //
            case 374: {
               //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2301 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . >=
            //
            case 375: {
               //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2305 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . >
            //
            case 376: {
               //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2309 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . ==
            //
            case 377: {
               //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 378:  OperatorFunction ::= TypeName . !=
            //
            case 378: {
               //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 379:  Literal ::= IntegerLiteral$lit
            //
            case 379: {
               //#line 2325 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2323 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2325 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 380:  Literal ::= LongLiteral$lit
            //
            case 380: {
               //#line 2329 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2327 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2329 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 381:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 381: {
               //#line 2333 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2331 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2333 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 382:  Literal ::= UnsignedLongLiteral$lit
            //
            case 382: {
               //#line 2337 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2335 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2337 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 383:  Literal ::= FloatingPointLiteral$lit
            //
            case 383: {
               //#line 2341 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2339 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2341 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 384:  Literal ::= DoubleLiteral$lit
            //
            case 384: {
               //#line 2345 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2343 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2345 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 385:  Literal ::= BooleanLiteral
            //
            case 385: {
               //#line 2349 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2347 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2349 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 386:  Literal ::= CharacterLiteral$lit
            //
            case 386: {
               //#line 2353 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2351 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2353 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 387:  Literal ::= StringLiteral$str
            //
            case 387: {
               //#line 2357 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2355 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2357 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 388:  Literal ::= null
            //
            case 388: {
               //#line 2361 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2361 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 389:  BooleanLiteral ::= true$trueLiteral
            //
            case 389: {
               //#line 2366 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2364 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2366 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 390:  BooleanLiteral ::= false$falseLiteral
            //
            case 390: {
               //#line 2370 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2368 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2370 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 391:  ArgumentList ::= Expression
            //
            case 391: {
               //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2376 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 392:  ArgumentList ::= ArgumentList , Expression
            //
            case 392: {
               //#line 2382 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2380 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2380 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2382 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 393:  FieldAccess ::= Primary . Identifier
            //
            case 393: {
               //#line 2387 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2385 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2385 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2387 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 394:  FieldAccess ::= super . Identifier
            //
            case 394: {
               //#line 2391 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2389 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2391 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 395:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 395: {
               //#line 2395 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2393 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2393 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2393 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2395 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 396:  FieldAccess ::= Primary . class$c
            //
            case 396: {
               //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2397 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 397:  FieldAccess ::= super . class$c
            //
            case 397: {
               //#line 2403 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2401 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2403 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 398: {
               //#line 2407 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2405 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2405 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2405 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2407 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 399:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 399: {
               //#line 2412 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2410 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2410 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2410 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2412 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 400:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 400: {
               //#line 2416 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2414 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2414 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2414 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2414 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2416 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 401:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 401: {
               //#line 2420 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2418 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2418 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2418 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2420 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 402:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 402: {
               //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2422 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2422 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2422 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2422 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2422 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 403:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 403: {
               //#line 2428 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2426 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2426 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2426 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2428 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 404:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 404: {
               //#line 2433 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2431 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2431 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2433 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 405:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 405: {
               //#line 2437 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2435 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2435 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2435 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2437 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 406:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 406: {
               //#line 2441 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2439 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2439 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2441 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 407:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 407: {
               //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2443 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2443 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2443 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2443 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 411:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 411: {
               //#line 2454 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2452 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2454 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 412:  PostDecrementExpression ::= PostfixExpression --
            //
            case 412: {
               //#line 2459 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2457 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2459 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 415:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 415: {
               //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2464 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2466 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 416:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 416: {
               //#line 2470 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2468 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2470 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 419:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 419: {
               //#line 2477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2475 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2475 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2477 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 420:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 420: {
               //#line 2482 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2480 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2482 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 421:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2485 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 423:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 423: {
               //#line 2493 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2491 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2493 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 424:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 424: {
               //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2495 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 426:  MultiplicativeExpression ::= MultiplicativeExpression * UnaryExpression
            //
            case 426: {
               //#line 2503 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2501 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2501 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2503 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 427:  MultiplicativeExpression ::= MultiplicativeExpression / UnaryExpression
            //
            case 427: {
               //#line 2507 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2505 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2505 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2507 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 428:  MultiplicativeExpression ::= MultiplicativeExpression % UnaryExpression
            //
            case 428: {
               //#line 2511 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2509 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2509 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(3);
                //#line 2511 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,UnaryExpression);
                    break;
            }
            //
            // Rule 430:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 430: {
               //#line 2517 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2515 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2515 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2517 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 431:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 431: {
               //#line 2521 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2519 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2519 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2521 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 433:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 433: {
               //#line 2527 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2525 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2525 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2527 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 434:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 434: {
               //#line 2531 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2529 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2529 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2531 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 435:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 435: {
               //#line 2535 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2533 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2533 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2535 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 437:  RangeExpression ::= ShiftExpression$expr1 .. ShiftExpression$expr2
            //
            case 437: {
               //#line 2541 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2539 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2539 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2541 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 441:  RelationalExpression ::= RelationalExpression < RangeExpression
            //
            case 441: {
               //#line 2549 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2547 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2547 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2549 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 442:  RelationalExpression ::= RelationalExpression > RangeExpression
            //
            case 442: {
               //#line 2553 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2551 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2551 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2553 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 443:  RelationalExpression ::= RelationalExpression <= RangeExpression
            //
            case 443: {
               //#line 2557 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2555 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2555 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2557 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 444:  RelationalExpression ::= RelationalExpression >= RangeExpression
            //
            case 444: {
               //#line 2561 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2559 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2559 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2561 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,RangeExpression);
                    break;
            }
            //
            // Rule 445:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 445: {
               //#line 2565 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2563 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2563 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2565 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 446:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 446: {
               //#line 2569 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2567 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2567 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2569 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 448: {
               //#line 2575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2573 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2573 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 449:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 449: {
               //#line 2579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2577 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2577 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 450:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 450: {
               //#line 2583 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2581 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2581 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2583 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 452:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 452: {
               //#line 2589 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2587 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2587 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2589 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 454:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 454: {
               //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2593 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2593 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2595 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 456:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 456: {
               //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2599 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2599 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2601 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 458:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 458: {
               //#line 2607 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2605 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2605 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2607 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 460:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 460: {
               //#line 2613 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2611 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2611 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2613 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 465:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 465: {
               //#line 2623 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2621 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2621 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2621 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2623 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 468:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 468: {
               //#line 2631 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2629 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2629 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2629 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2631 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 469:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 469: {
               //#line 2635 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2633 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2633 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2633 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2633 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2635 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 470:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 470: {
               //#line 2639 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2637 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2637 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2637 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2637 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2639 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 471:  LeftHandSide ::= ExpressionName
            //
            case 471: {
               //#line 2644 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2642 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2644 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 473:  AssignmentOperator ::= =
            //
            case 473: {
               //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 474:  AssignmentOperator ::= *=
            //
            case 474: {
               //#line 2654 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2654 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 475:  AssignmentOperator ::= /=
            //
            case 475: {
               //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 476:  AssignmentOperator ::= %=
            //
            case 476: {
               //#line 2662 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2662 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 477:  AssignmentOperator ::= +=
            //
            case 477: {
               //#line 2666 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2666 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 478:  AssignmentOperator ::= -=
            //
            case 478: {
               //#line 2670 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2670 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= <<=
            //
            case 479: {
               //#line 2674 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2674 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= >>=
            //
            case 480: {
               //#line 2678 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2678 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= >>>=
            //
            case 481: {
               //#line 2682 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2682 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= &=
            //
            case 482: {
               //#line 2686 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= ^=
            //
            case 483: {
               //#line 2690 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2690 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= |=
            //
            case 484: {
               //#line 2694 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2694 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 487:  PrefixOp ::= +
            //
            case 487: {
               //#line 2704 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2704 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 488:  PrefixOp ::= -
            //
            case 488: {
               //#line 2708 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2708 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 489:  PrefixOp ::= !
            //
            case 489: {
               //#line 2712 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2712 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 490:  PrefixOp ::= ~
            //
            case 490: {
               //#line 2716 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2716 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 491:  BinOp ::= +
            //
            case 491: {
               //#line 2721 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2721 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 492:  BinOp ::= -
            //
            case 492: {
               //#line 2725 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2725 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 493:  BinOp ::= *
            //
            case 493: {
               //#line 2729 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2729 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 494:  BinOp ::= /
            //
            case 494: {
               //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 495:  BinOp ::= %
            //
            case 495: {
               //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 496:  BinOp ::= &
            //
            case 496: {
               //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2741 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 497:  BinOp ::= |
            //
            case 497: {
               //#line 2745 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2745 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 498:  BinOp ::= ^
            //
            case 498: {
               //#line 2749 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2749 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 499:  BinOp ::= &&
            //
            case 499: {
               //#line 2753 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2753 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 500:  BinOp ::= ||
            //
            case 500: {
               //#line 2757 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2757 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 501:  BinOp ::= <<
            //
            case 501: {
               //#line 2761 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2761 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 502:  BinOp ::= >>
            //
            case 502: {
               //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2765 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 503:  BinOp ::= >>>
            //
            case 503: {
               //#line 2769 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2769 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 504:  BinOp ::= >=
            //
            case 504: {
               //#line 2773 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2773 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 505:  BinOp ::= <=
            //
            case 505: {
               //#line 2777 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2777 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 506:  BinOp ::= >
            //
            case 506: {
               //#line 2781 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2781 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 507:  BinOp ::= <
            //
            case 507: {
               //#line 2785 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2785 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 508:  BinOp ::= ==
            //
            case 508: {
               //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 509:  BinOp ::= !=
            //
            case 509: {
               //#line 2796 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2796 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 510:  Catchesopt ::= $Empty
            //
            case 510: {
               //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2812 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2810 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2812 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 514:  ForUpdateopt ::= $Empty
            //
            case 514: {
               //#line 2817 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2817 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2827 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2827 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 520:  SwitchLabelsopt ::= $Empty
            //
            case 520: {
               //#line 2833 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2833 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 522:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 522: {
               //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 524:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 524: {
               //#line 2862 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2862 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 526:  ExtendsInterfacesopt ::= $Empty
            //
            case 526: {
               //#line 2868 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2868 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2898 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2898 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 532:  BlockStatementsopt ::= $Empty
            //
            case 532: {
               //#line 2904 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2904 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2924 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2924 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 538:  Offersopt ::= $Empty
            //
            case 538: {
               //#line 2936 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2936 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 540:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 540: {
               //#line 2972 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2972 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 542:  Interfacesopt ::= $Empty
            //
            case 542: {
               //#line 2978 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2978 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2988 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2988 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 548:  FormalParametersopt ::= $Empty
            //
            case 548: {
               //#line 2994 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2994 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 550:  Annotationsopt ::= $Empty
            //
            case 550: {
               //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 552:  TypeDeclarationsopt ::= $Empty
            //
            case 552: {
               //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 554:  ImportDeclarationsopt ::= $Empty
            //
            case 554: {
               //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3032 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3032 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 562:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 562: {
               //#line 3038 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3038 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 564:  Propertiesopt ::= $Empty
            //
            case 564: {
               //#line 3044 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

