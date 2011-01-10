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
            // Rule 63:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 63: {
               //#line 487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 485 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(1);
                //#line 485 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(3);
                //#line 485 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 485 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(6);
                //#line 485 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 487 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
                    break;
            }
            //
            // Rule 65:  AnnotatedType ::= Type Annotations
            //
            case 65: {
               //#line 499 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 497 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 497 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(2);
                //#line 499 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotatedType0(Type,Annotations);
                    break;
            }
            //
            // Rule 68:  ConstrainedType ::= ( Type )
            //
            case 68: {
               //#line 506 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 504 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 506 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstrainedType2(Type);
                    break;
            }
            //
            // Rule 69:  VoidType ::= void
            //
            case 69: {
               //#line 511 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 511 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VoidType0();
                    break;
            }
            //
            // Rule 70:  SimpleNamedType ::= TypeName
            //
            case 70: {
               //#line 517 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 515 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 517 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType0(TypeName);
                    break;
            }
            //
            // Rule 71:  SimpleNamedType ::= Primary . Identifier
            //
            case 71: {
               //#line 521 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 519 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 519 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 521 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType1(Primary,Identifier);
                    break;
            }
            //
            // Rule 72:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 72: {
               //#line 525 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 523 "x10/parser/x10.g"
                Object DepNamedType = (Object) getRhsSym(1);
                //#line 523 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 525 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType2(DepNamedType,Identifier);
                    break;
            }
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 73: {
               //#line 530 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 528 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 528 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(2);
                //#line 530 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType0(SimpleNamedType,DepParameters);
                    break;
            }
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 74: {
               //#line 534 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 532 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 532 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 534 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType1(SimpleNamedType,Arguments);
                    break;
            }
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 75: {
               //#line 538 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 536 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 536 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 536 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 538 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 76: {
               //#line 542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 540 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 540 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
                    break;
            }
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 77: {
               //#line 546 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 544 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 544 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 544 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
                    break;
            }
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 78: {
               //#line 550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 548 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 548 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 548 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 550 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
                    break;
            }
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 79: {
               //#line 554 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 552 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 552 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 552 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 552 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(4);
                //#line 554 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 82:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 82: {
               //#line 562 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 560 "x10/parser/x10.g"
                Object ExistentialListopt = (Object) getRhsSym(2);
                //#line 560 "x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 562 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
                    break;
            }
            //
            // Rule 83:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 83: {
               //#line 568 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 566 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(2);
                //#line 568 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
                    break;
            }
            //
            // Rule 84:  TypeParameters ::= [ TypeParameterList ]
            //
            case 84: {
               //#line 573 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 571 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(2);
                //#line 573 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameters0(TypeParameterList);
                    break;
            }
            //
            // Rule 85:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 85: {
               //#line 578 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 576 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(2);
                //#line 578 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameters0(FormalParameterListopt);
                    break;
            }
            //
            // Rule 86:  Conjunction ::= Expression
            //
            case 86: {
               //#line 583 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 581 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 583 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction0(Expression);
                    break;
            }
            //
            // Rule 87:  Conjunction ::= Conjunction , Expression
            //
            case 87: {
               //#line 587 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 585 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 585 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 587 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction1(Conjunction,Expression);
                    break;
            }
            //
            // Rule 88:  HasZeroConstraint ::= Type$t1 haszero
            //
            case 88: {
               //#line 592 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 590 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 592 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasZeroConstraint0(t1);
                    break;
            }
            //
            // Rule 89:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 89: {
               //#line 597 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 595 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 595 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 597 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint0(t1,t2);
                    break;
            }
            //
            // Rule 90:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 90: {
               //#line 601 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 599 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 599 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 601 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint1(t1,t2);
                    break;
            }
            //
            // Rule 91:  WhereClause ::= DepParameters
            //
            case 91: {
               //#line 606 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 604 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(1);
                //#line 606 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhereClause0(DepParameters);
                      break;
            }
            //
            // Rule 92:  Conjunctionopt ::= $Empty
            //
            case 92: {
               //#line 611 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 611 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt0();
                      break;
            }
            //
            // Rule 93:  Conjunctionopt ::= Conjunction
            //
            case 93: {
               //#line 615 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 613 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 615 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt1(Conjunction);
                    break;
            }
            //
            // Rule 94:  ExistentialListopt ::= $Empty
            //
            case 94: {
               //#line 620 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 620 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt0();
                      break;
            }
            //
            // Rule 95:  ExistentialListopt ::= ExistentialList ;
            //
            case 95: {
               //#line 624 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 622 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 624 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt1(ExistentialList);
                    break;
            }
            //
            // Rule 96:  ExistentialList ::= FormalParameter
            //
            case 96: {
               //#line 629 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 627 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 629 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList0(FormalParameter);
                    break;
            }
            //
            // Rule 97:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 97: {
               //#line 633 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 631 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 631 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 633 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList1(ExistentialList,FormalParameter);
                    break;
            }
            //
            // Rule 100:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 100: {
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
                Object Superopt = (Object) getRhsSym(7);
                //#line 641 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(8);
                //#line 641 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(9);
                //#line 643 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 101:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 101: {
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
                Object Interfacesopt = (Object) getRhsSym(7);
                //#line 647 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(8);
                //#line 649 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 102:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 102: {
               //#line 654 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 652 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 652 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 652 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 652 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 652 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 652 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 652 "x10/parser/x10.g"
                Object ConstructorBody = (Object) getRhsSym(9);
                //#line 654 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                           
                    break;
            }
            //
            // Rule 103:  Super ::= extends ClassType
            //
            case 103: {
               //#line 660 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 658 "x10/parser/x10.g"
                Object ClassType = (Object) getRhsSym(2);
                //#line 660 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Super0(ClassType);
                    break;
            }
            //
            // Rule 104:  FieldKeyword ::= val
            //
            case 104: {
               //#line 665 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 665 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword0();
                    break;
            }
            //
            // Rule 105:  FieldKeyword ::= var
            //
            case 105: {
               //#line 669 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 669 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword1();
                    break;
            }
            //
            // Rule 106:  VarKeyword ::= val
            //
            case 106: {
               //#line 676 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 676 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword0();
                    break;
            }
            //
            // Rule 107:  VarKeyword ::= var
            //
            case 107: {
               //#line 680 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 680 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword1();
                    break;
            }
            //
            // Rule 108:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 108: {
               //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 684 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 684 "x10/parser/x10.g"
                Object FieldKeyword = (Object) getRhsSym(2);
                //#line 684 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(3);
                //#line 686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 109:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 109: {
               //#line 692 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 690 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 690 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(2);
                //#line 692 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 112:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 112: {
               //#line 704 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 702 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 702 "x10/parser/x10.g"
                Object NonExpressionStatement = (Object) getRhsSym(2);
                //#line 704 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
                    break;
            }
            //
            // Rule 138:  OfferStatement ::= offer Expression ;
            //
            case 138: {
               //#line 737 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 735 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 737 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OfferStatement0(Expression);
                    break;
            }
            //
            // Rule 139:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 139: {
               //#line 742 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 740 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 740 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 742 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 140:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 140: {
               //#line 747 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 745 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 745 "x10/parser/x10.g"
                Object s1 = (Object) getRhsSym(5);
                //#line 745 "x10/parser/x10.g"
                Object s2 = (Object) getRhsSym(7);
                //#line 747 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenElseStatement0(Expression,s1,s2);
                    break;
            }
            //
            // Rule 141:  EmptyStatement ::= ;
            //
            case 141: {
               //#line 752 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 752 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EmptyStatement0();
                    break;
            }
            //
            // Rule 142:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 142: {
               //#line 757 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 755 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 755 "x10/parser/x10.g"
                Object LoopStatement = (Object) getRhsSym(3);
                //#line 757 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LabeledStatement0(Identifier,LoopStatement);
                    break;
            }
            //
            // Rule 147:  ExpressionStatement ::= StatementExpression ;
            //
            case 147: {
               //#line 768 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 766 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 768 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionStatement0(StatementExpression);
                    break;
            }
            //
            // Rule 155:  AssertStatement ::= assert Expression ;
            //
            case 155: {
               //#line 781 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 779 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 781 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement0(Expression);
                    break;
            }
            //
            // Rule 156:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 156: {
               //#line 785 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 783 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(2);
                //#line 783 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(4);
                //#line 785 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement1(expr1,expr2);
                    break;
            }
            //
            // Rule 157:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 157: {
               //#line 790 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 788 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 788 "x10/parser/x10.g"
                Object SwitchBlock = (Object) getRhsSym(5);
                //#line 790 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchStatement0(Expression,SwitchBlock);
                    break;
            }
            //
            // Rule 158:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 158: {
               //#line 795 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 793 "x10/parser/x10.g"
                Object SwitchBlockStatementGroupsopt = (Object) getRhsSym(2);
                //#line 793 "x10/parser/x10.g"
                Object SwitchLabelsopt = (Object) getRhsSym(3);
                //#line 795 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
                    break;
            }
            //
            // Rule 160:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 160: {
               //#line 801 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 799 "x10/parser/x10.g"
                Object SwitchBlockStatementGroups = (Object) getRhsSym(1);
                //#line 799 "x10/parser/x10.g"
                Object SwitchBlockStatementGroup = (Object) getRhsSym(2);
                //#line 801 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
                    break;
            }
            //
            // Rule 161:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 161: {
               //#line 806 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 804 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 804 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(2);
                //#line 806 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
                    break;
            }
            //
            // Rule 162:  SwitchLabels ::= SwitchLabel
            //
            case 162: {
               //#line 811 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 809 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(1);
                //#line 811 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels0(SwitchLabel);
                    break;
            }
            //
            // Rule 163:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 163: {
               //#line 815 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 813 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 813 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(2);
                //#line 815 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
                    break;
            }
            //
            // Rule 164:  SwitchLabel ::= case ConstantExpression :
            //
            case 164: {
               //#line 820 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 818 "x10/parser/x10.g"
                Object ConstantExpression = (Object) getRhsSym(2);
                //#line 820 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel0(ConstantExpression);
                    break;
            }
            //
            // Rule 165:  SwitchLabel ::= default :
            //
            case 165: {
               //#line 824 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 824 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel1();
                    break;
            }
            //
            // Rule 166:  WhileStatement ::= while ( Expression ) Statement
            //
            case 166: {
               //#line 829 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 827 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 827 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 829 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhileStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 167:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 167: {
               //#line 834 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 832 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 832 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 834 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DoStatement0(Statement,Expression);
                    break;
            }
            //
            // Rule 170:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 170: {
               //#line 842 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 840 "x10/parser/x10.g"
                Object ForInitopt = (Object) getRhsSym(3);
                //#line 840 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(5);
                //#line 840 "x10/parser/x10.g"
                Object ForUpdateopt = (Object) getRhsSym(7);
                //#line 840 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(9);
                //#line 842 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
                    break;
            }
            //
            // Rule 172:  ForInit ::= LocalVariableDeclaration
            //
            case 172: {
               //#line 848 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 846 "x10/parser/x10.g"
                Object LocalVariableDeclaration = (Object) getRhsSym(1);
                //#line 848 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInit1(LocalVariableDeclaration);
                    break;
            }
            //
            // Rule 174:  StatementExpressionList ::= StatementExpression
            //
            case 174: {
               //#line 855 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 853 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 855 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList0(StatementExpression);
                    break;
            }
            //
            // Rule 175:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 175: {
               //#line 859 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 857 "x10/parser/x10.g"
                Object StatementExpressionList = (Object) getRhsSym(1);
                //#line 857 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(3);
                //#line 859 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
                    break;
            }
            //
            // Rule 176:  BreakStatement ::= break Identifieropt ;
            //
            case 176: {
               //#line 864 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 862 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 864 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BreakStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 177:  ContinueStatement ::= continue Identifieropt ;
            //
            case 177: {
               //#line 869 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 867 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 869 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ContinueStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 178:  ReturnStatement ::= return Expressionopt ;
            //
            case 178: {
               //#line 874 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 872 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(2);
                //#line 874 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ReturnStatement0(Expressionopt);
                    break;
            }
            //
            // Rule 179:  ThrowStatement ::= throw Expression ;
            //
            case 179: {
               //#line 879 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 877 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 879 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ThrowStatement0(Expression);
                    break;
            }
            //
            // Rule 180:  TryStatement ::= try Block Catches
            //
            case 180: {
               //#line 884 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 882 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 882 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(3);
                //#line 884 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement0(Block,Catches);
                    break;
            }
            //
            // Rule 181:  TryStatement ::= try Block Catchesopt Finally
            //
            case 181: {
               //#line 888 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 886 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 886 "x10/parser/x10.g"
                Object Catchesopt = (Object) getRhsSym(3);
                //#line 886 "x10/parser/x10.g"
                Object Finally = (Object) getRhsSym(4);
                //#line 888 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement1(Block,Catchesopt,Finally);
                    break;
            }
            //
            // Rule 182:  Catches ::= CatchClause
            //
            case 182: {
               //#line 893 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 891 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(1);
                //#line 893 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches0(CatchClause);
                    break;
            }
            //
            // Rule 183:  Catches ::= Catches CatchClause
            //
            case 183: {
               //#line 897 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 895 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(1);
                //#line 895 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(2);
                //#line 897 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches1(Catches,CatchClause);
                    break;
            }
            //
            // Rule 184:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 184: {
               //#line 902 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 900 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 900 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 902 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CatchClause0(FormalParameter,Block);
                    break;
            }
            //
            // Rule 185:  Finally ::= finally Block
            //
            case 185: {
               //#line 907 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 905 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 907 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Finally0(Block);
                    break;
            }
            //
            // Rule 186:  ClockedClause ::= clocked ( ClockList )
            //
            case 186: {
               //#line 912 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 910 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(3);
                //#line 912 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClause0(ClockList);
                    break;
            }
            //
            // Rule 187:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 187: {
               //#line 918 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 916 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(2);
                //#line 916 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 918 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement0(ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 188:  AsyncStatement ::= clocked async Statement
            //
            case 188: {
               //#line 922 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 920 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 922 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement1(Statement);
                    break;
            }
            //
            // Rule 189:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 189: {
               //#line 928 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 926 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 926 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 928 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
                    break;
            }
            //
            // Rule 190:  AtomicStatement ::= atomic Statement
            //
            case 190: {
               //#line 933 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 931 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 933 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtomicStatement0(Statement);
                    break;
            }
            //
            // Rule 191:  WhenStatement ::= when ( Expression ) Statement
            //
            case 191: {
               //#line 939 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 937 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 937 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 939 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 192:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 192: {
               //#line 1001 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 999 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 999 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 999 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(7);
                //#line 999 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(8);
                //#line 1001 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 193:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 193: {
               //#line 1005 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1003 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1003 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1005 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 194:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 194: {
               //#line 1009 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1007 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1007 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1007 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1009 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 195:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 195: {
               //#line 1013 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1011 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1011 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1013 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 196:  FinishStatement ::= finish Statement
            //
            case 196: {
               //#line 1019 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1017 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1019 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 197:  FinishStatement ::= clocked finish Statement
            //
            case 197: {
               //#line 1023 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1021 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1023 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 198:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 198: {
               //#line 1027 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1025 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1027 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 200:  NextStatement ::= next ;
            //
            case 200: {
               //#line 1034 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1034 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 201:  ResumeStatement ::= resume ;
            //
            case 201: {
               //#line 1039 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1039 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 202:  ClockList ::= Clock
            //
            case 202: {
               //#line 1044 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1042 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 203:  ClockList ::= ClockList , Clock
            //
            case 203: {
               //#line 1048 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1046 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1046 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1048 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 204:  Clock ::= Expression
            //
            case 204: {
               //#line 1054 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1052 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1054 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 206:  CastExpression ::= ExpressionName
            //
            case 206: {
               //#line 1066 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1064 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1066 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 207:  CastExpression ::= CastExpression as Type
            //
            case 207: {
               //#line 1070 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1068 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1068 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1070 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 208:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 208: {
               //#line 1076 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1074 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1076 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 209:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 209: {
               //#line 1080 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1078 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1078 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1080 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 210:  TypeParameterList ::= TypeParameter
            //
            case 210: {
               //#line 1085 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1083 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1085 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 211:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 211: {
               //#line 1089 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1087 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1087 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1089 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= Identifier
            //
            case 212: {
               //#line 1094 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1092 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1094 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParamWithVariance ::= + Identifier
            //
            case 213: {
               //#line 1098 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1096 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1098 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 214:  TypeParamWithVariance ::= - Identifier
            //
            case 214: {
               //#line 1102 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1100 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1102 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 215:  TypeParameter ::= Identifier
            //
            case 215: {
               //#line 1107 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1105 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1107 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 216:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 216: {
               //#line 1131 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1129 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1129 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1129 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1129 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1129 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1131 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 217:  LastExpression ::= Expression
            //
            case 217: {
               //#line 1136 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1134 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1136 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= ConditionalExpression
            //
            case 218: {
               //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1139 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1141 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 219:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 219: {
               //#line 1145 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1143 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1143 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1143 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1145 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 220:  ClosureBody ::= Annotationsopt Block
            //
            case 220: {
               //#line 1149 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1147 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1147 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1149 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 221:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 221: {
               //#line 1155 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1153 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1153 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1155 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 222:  FinishExpression ::= finish ( Expression ) Block
            //
            case 222: {
               //#line 1160 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1158 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1158 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1160 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishExpression0(Expression,Block);
                    break;
            }
            //
            // Rule 223:  WhereClauseopt ::= $Empty
            //
            case 223:
                setResult(null);
                break;

            //
            // Rule 225:  ClockedClauseopt ::= $Empty
            //
            case 225: {
               //#line 1204 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1204 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 227:  TypeName ::= Identifier
            //
            case 227: {
               //#line 1215 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1213 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1215 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 228:  TypeName ::= TypeName . Identifier
            //
            case 228: {
               //#line 1219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1217 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1217 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1219 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 230:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 230: {
               //#line 1226 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1224 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1226 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 231:  TypeArgumentList ::= Type
            //
            case 231: {
               //#line 1232 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1230 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1232 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 232:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 232: {
               //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1234 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1234 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1236 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 233:  PackageName ::= Identifier
            //
            case 233: {
               //#line 1245 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1243 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1245 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 234:  PackageName ::= PackageName . Identifier
            //
            case 234: {
               //#line 1249 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1247 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1247 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1249 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 235:  ExpressionName ::= Identifier
            //
            case 235: {
               //#line 1260 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1258 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1260 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 236:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 236: {
               //#line 1264 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1262 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1262 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1264 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 237:  MethodName ::= Identifier
            //
            case 237: {
               //#line 1269 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1267 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1269 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 238:  MethodName ::= AmbiguousName . Identifier
            //
            case 238: {
               //#line 1273 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1271 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1271 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1273 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 239:  PackageOrTypeName ::= Identifier
            //
            case 239: {
               //#line 1278 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1276 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1278 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 240:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 240: {
               //#line 1282 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1280 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1280 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1282 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 241:  AmbiguousName ::= Identifier
            //
            case 241: {
               //#line 1287 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1285 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 242:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 242: {
               //#line 1291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1289 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1289 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 243: {
               //#line 1298 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1296 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1296 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1298 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1302 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1300 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1300 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1300 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1302 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 245: {
               //#line 1306 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1304 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1304 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1304 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1304 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1306 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 246:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 246: {
               //#line 1310 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1308 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1308 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1308 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1308 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1308 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1310 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclaration
            //
            case 247: {
               //#line 1315 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1313 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1315 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 248:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 248: {
               //#line 1319 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1317 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1317 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1319 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclaration
            //
            case 249: {
               //#line 1324 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1322 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1324 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 250: {
               //#line 1328 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1326 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1326 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1328 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 251:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 251: {
               //#line 1333 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1331 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1331 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1333 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 254:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 254: {
               //#line 1344 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1342 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1344 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 255:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 255: {
               //#line 1349 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1347 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1349 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 259:  TypeDeclaration ::= ;
            //
            case 259: {
               //#line 1363 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1363 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 260:  Interfaces ::= implements InterfaceTypeList
            //
            case 260: {
               //#line 1479 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1477 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1479 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 261:  InterfaceTypeList ::= Type
            //
            case 261: {
               //#line 1484 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1482 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1484 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 262:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 262: {
               //#line 1488 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1486 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1486 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1488 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 263:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 263: {
               //#line 1496 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1494 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1496 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 265:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 265: {
               //#line 1502 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1500 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1500 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1502 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 267:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 267: {
               //#line 1522 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1520 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1522 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 269: {
               //#line 1528 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1526 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1528 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 270: {
               //#line 1532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1530 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1532 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 271: {
               //#line 1536 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1534 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1536 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 272: {
               //#line 1540 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1538 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1540 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 273:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 273: {
               //#line 1544 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1542 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1544 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 274:  ClassMemberDeclaration ::= ;
            //
            case 274: {
               //#line 1548 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1548 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 275:  FormalDeclarators ::= FormalDeclarator
            //
            case 275: {
               //#line 1553 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1551 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1553 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 276:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 276: {
               //#line 1557 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1555 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1555 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1557 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 277:  FieldDeclarators ::= FieldDeclarator
            //
            case 277: {
               //#line 1563 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1561 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1563 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 278:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 278: {
               //#line 1567 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1565 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1565 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1567 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 279:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 279: {
               //#line 1573 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1571 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1573 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 280:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 280: {
               //#line 1577 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1575 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1575 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1577 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 281:  VariableDeclarators ::= VariableDeclarator
            //
            case 281: {
               //#line 1582 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1580 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1582 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 282:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 282: {
               //#line 1586 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1584 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1584 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1586 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 284:  ResultType ::= : Type
            //
            case 284: {
               //#line 1640 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1638 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1640 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 285:  HasResultType ::= : Type
            //
            case 285: {
               //#line 1644 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1642 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1644 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 286:  HasResultType ::= <: Type
            //
            case 286: {
               //#line 1648 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1646 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1648 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 287:  FormalParameterList ::= FormalParameter
            //
            case 287: {
               //#line 1662 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1660 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1662 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 288:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 288: {
               //#line 1666 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1664 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1664 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1666 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 289: {
               //#line 1671 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1669 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1669 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1671 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 290: {
               //#line 1675 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1673 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1673 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1675 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 291:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 291: {
               //#line 1679 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1677 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1677 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1677 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1679 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 292:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 292: {
               //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1682 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1682 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1684 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 293:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 293: {
               //#line 1688 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1686 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1686 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1686 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1688 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 294: {
               //#line 1693 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1691 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1691 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1693 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 295:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 295: {
               //#line 1697 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1695 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1695 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1695 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1697 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 296:  FormalParameter ::= Type
            //
            case 296: {
               //#line 1701 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1699 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1701 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 297:  Offers ::= offers Type
            //
            case 297: {
               //#line 1839 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1837 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1839 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = LastExpression ;
            //
            case 298: {
               //#line 1845 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1843 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1845 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 299: {
               //#line 1849 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1847 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1847 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1847 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1849 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= = Annotationsopt Block
            //
            case 300: {
               //#line 1853 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1851 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1851 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1853 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 301:  MethodBody ::= Annotationsopt Block
            //
            case 301: {
               //#line 1857 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1855 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1855 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1857 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody3(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 302:  MethodBody ::= ;
            //
            case 302:
                setResult(null);
                break;

            //
            // Rule 303:  ConstructorBody ::= = ConstructorBlock
            //
            case 303: {
               //#line 1927 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1925 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1927 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= ConstructorBlock
            //
            case 304: {
               //#line 1931 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1929 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1931 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 305: {
               //#line 1935 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1933 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1935 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 306:  ConstructorBody ::= = AssignPropertyCall
            //
            case 306: {
               //#line 1939 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1937 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1939 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody3(AssignPropertyCall);
                    break;
            }
            //
            // Rule 307:  ConstructorBody ::= ;
            //
            case 307:
                setResult(null);
                break;

            //
            // Rule 308:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 308: {
               //#line 1946 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1944 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1944 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1946 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 309:  Arguments ::= ( ArgumentListopt )
            //
            case 309: {
               //#line 1951 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1949 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1951 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 311:  ExtendsInterfaces ::= extends Type
            //
            case 311: {
               //#line 2007 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2005 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 312:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 312: {
               //#line 2011 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2009 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2009 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2011 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 313:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 313: {
               //#line 2019 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2017 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2019 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 315: {
               //#line 2025 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2023 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2023 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2025 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 316: {
               //#line 2030 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2028 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2030 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 317: {
               //#line 2034 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2032 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2034 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 318: {
               //#line 2038 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2036 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2038 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 319: {
               //#line 2042 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2040 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2042 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 320: {
               //#line 2046 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2044 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2046 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 321:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 321: {
               //#line 2050 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2048 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2050 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 322:  InterfaceMemberDeclaration ::= ;
            //
            case 322: {
               //#line 2054 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2054 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 323:  Annotations ::= Annotation
            //
            case 323: {
               //#line 2059 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2057 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2059 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 324:  Annotations ::= Annotations Annotation
            //
            case 324: {
               //#line 2063 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2061 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2061 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2063 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 325:  Annotation ::= @ NamedType
            //
            case 325: {
               //#line 2068 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2066 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2068 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 326:  Identifier ::= IDENTIFIER$ident
            //
            case 326: {
               //#line 2082 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2080 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2082 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 327:  Block ::= { BlockStatementsopt }
            //
            case 327: {
               //#line 2117 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2115 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2117 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 328:  BlockStatements ::= BlockStatement
            //
            case 328: {
               //#line 2122 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2120 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2122 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 329:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 329: {
               //#line 2126 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2124 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2124 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2126 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= ClassDeclaration
            //
            case 331: {
               //#line 2132 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2130 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2132 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 332:  BlockStatement ::= TypeDefDeclaration
            //
            case 332: {
               //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2134 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2136 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 333:  BlockStatement ::= Statement
            //
            case 333: {
               //#line 2140 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2138 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2140 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 334:  IdentifierList ::= Identifier
            //
            case 334: {
               //#line 2145 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2143 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2145 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 335:  IdentifierList ::= IdentifierList , Identifier
            //
            case 335: {
               //#line 2149 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2147 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2147 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2149 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= Identifier ResultType
            //
            case 336: {
               //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2152 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2152 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2154 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 337:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 337: {
               //#line 2158 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2156 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2156 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2158 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 338:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 338: {
               //#line 2162 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2160 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2160 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2160 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2162 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 339:  FieldDeclarator ::= Identifier HasResultType
            //
            case 339: {
               //#line 2167 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2165 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2165 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2167 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 340:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2171 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2169 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2169 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2169 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2171 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2176 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2174 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2174 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2174 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2176 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 342: {
               //#line 2180 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2178 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2178 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2178 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2180 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 343: {
               //#line 2184 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2182 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2182 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2182 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2182 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2184 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2189 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2187 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2187 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2187 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2189 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 345:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 345: {
               //#line 2193 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2191 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2191 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2191 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2193 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 346:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 346: {
               //#line 2197 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2195 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2195 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2195 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2195 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2197 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 348: {
               //#line 2204 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2202 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2202 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2202 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2204 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 349:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 349: {
               //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2207 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2207 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2209 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 350:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 350: {
               //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2212 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2212 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2212 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2214 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 351:  Primary ::= here
            //
            case 351: {
               //#line 2225 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2225 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 352:  Primary ::= [ ArgumentListopt ]
            //
            case 352: {
               //#line 2229 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2227 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2229 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 354:  Primary ::= self
            //
            case 354: {
               //#line 2235 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2235 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 355:  Primary ::= this
            //
            case 355: {
               //#line 2239 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2239 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 356:  Primary ::= ClassName . this
            //
            case 356: {
               //#line 2243 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2241 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2243 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 357:  Primary ::= ( Expression )
            //
            case 357: {
               //#line 2247 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2245 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2247 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . +
            //
            case 363: {
               //#line 2257 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2255 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2257 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . -
            //
            case 364: {
               //#line 2261 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2259 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2261 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . *
            //
            case 365: {
               //#line 2265 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2263 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2265 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . /
            //
            case 366: {
               //#line 2269 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2267 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2269 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . %
            //
            case 367: {
               //#line 2273 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2271 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2273 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . &
            //
            case 368: {
               //#line 2277 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2275 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2277 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . |
            //
            case 369: {
               //#line 2281 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2279 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2281 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . ^
            //
            case 370: {
               //#line 2285 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2283 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2285 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . <<
            //
            case 371: {
               //#line 2289 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2287 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2289 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . >>
            //
            case 372: {
               //#line 2293 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2291 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2293 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . >>>
            //
            case 373: {
               //#line 2297 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2295 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2297 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . <
            //
            case 374: {
               //#line 2301 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2299 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2301 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . <=
            //
            case 375: {
               //#line 2305 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2303 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2305 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . >=
            //
            case 376: {
               //#line 2309 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2307 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2309 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . >
            //
            case 377: {
               //#line 2313 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2311 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2313 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 378:  OperatorFunction ::= TypeName . ==
            //
            case 378: {
               //#line 2317 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2315 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 379:  OperatorFunction ::= TypeName . !=
            //
            case 379: {
               //#line 2321 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2319 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2321 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 380:  Literal ::= IntegerLiteral$lit
            //
            case 380: {
               //#line 2326 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2324 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2326 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 381:  Literal ::= LongLiteral$lit
            //
            case 381: {
               //#line 2330 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2328 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2330 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 382:  Literal ::= ByteLiteral
            //
            case 382: {
               //#line 2334 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2334 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralByte();
                    break;
            }
            //
            // Rule 383:  Literal ::= UnsignedByteLiteral
            //
            case 383: {
               //#line 2338 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2338 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUByte();
                    break;
            }
            //
            // Rule 384:  Literal ::= ShortLiteral
            //
            case 384: {
               //#line 2342 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2342 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralShort();
                    break;
            }
            //
            // Rule 385:  Literal ::= UnsignedShortLiteral
            //
            case 385: {
               //#line 2346 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2346 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUShort();
                    break;
            }
            //
            // Rule 386:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 386: {
               //#line 2350 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2348 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2350 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 387:  Literal ::= UnsignedLongLiteral$lit
            //
            case 387: {
               //#line 2354 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2352 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2354 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 388:  Literal ::= FloatingPointLiteral$lit
            //
            case 388: {
               //#line 2358 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2356 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2358 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 389:  Literal ::= DoubleLiteral$lit
            //
            case 389: {
               //#line 2362 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2360 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2362 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 390:  Literal ::= BooleanLiteral
            //
            case 390: {
               //#line 2366 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2364 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2366 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 391:  Literal ::= CharacterLiteral$lit
            //
            case 391: {
               //#line 2370 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2368 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2370 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 392:  Literal ::= StringLiteral$str
            //
            case 392: {
               //#line 2374 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2372 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2374 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 393:  Literal ::= null
            //
            case 393: {
               //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2378 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 394:  BooleanLiteral ::= true$trueLiteral
            //
            case 394: {
               //#line 2383 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2381 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2383 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 395:  BooleanLiteral ::= false$falseLiteral
            //
            case 395: {
               //#line 2387 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2385 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2387 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 396:  ArgumentList ::= Expression
            //
            case 396: {
               //#line 2395 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2393 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2395 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 397:  ArgumentList ::= ArgumentList , Expression
            //
            case 397: {
               //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2397 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2397 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2399 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= Primary . Identifier
            //
            case 398: {
               //#line 2404 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2402 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2402 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2404 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 399:  FieldAccess ::= super . Identifier
            //
            case 399: {
               //#line 2408 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2406 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2408 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 400:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 400: {
               //#line 2412 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2410 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2410 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2410 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2412 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 401:  FieldAccess ::= Primary . class$c
            //
            case 401: {
               //#line 2416 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2414 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2414 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2416 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 402:  FieldAccess ::= super . class$c
            //
            case 402: {
               //#line 2420 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2418 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2420 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 403:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 403: {
               //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2422 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2422 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2422 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2424 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 404:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 404: {
               //#line 2429 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2427 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2427 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2427 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2429 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 405:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 405: {
               //#line 2433 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2431 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2431 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2431 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2431 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2433 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 406:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 406: {
               //#line 2437 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2435 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2435 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2435 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2437 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 407:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 407: {
               //#line 2441 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2439 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2439 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2439 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2439 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2439 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2441 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 408:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 408: {
               //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2443 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2443 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2443 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2445 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 409:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 409: {
               //#line 2450 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2448 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2448 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2450 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 410:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 410: {
               //#line 2454 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2452 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2452 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2452 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2454 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 411:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 411: {
               //#line 2458 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2456 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2456 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2458 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 412:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 412: {
               //#line 2462 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2460 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2460 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2460 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2460 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2462 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 416:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 416: {
               //#line 2471 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2469 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2471 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 417:  PostDecrementExpression ::= PostfixExpression --
            //
            case 417: {
               //#line 2476 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2474 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2476 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 420:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 420: {
               //#line 2483 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2481 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2483 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 421:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2485 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 424:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 424: {
               //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2492 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2492 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2494 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 425:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 425: {
               //#line 2499 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2497 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2499 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 426:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 426: {
               //#line 2504 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2502 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2504 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 428:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 428: {
               //#line 2510 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2508 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2510 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 429:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 429: {
               //#line 2514 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2512 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2514 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 431:  RangeExpression ::= RangeExpression$expr1 .. UnaryExpression$expr2
            //
            case 431: {
               //#line 2520 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2518 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2518 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2520 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 433:  MultiplicativeExpression ::= MultiplicativeExpression * RangeExpression
            //
            case 433: {
               //#line 2526 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2524 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2524 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2526 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 434:  MultiplicativeExpression ::= MultiplicativeExpression / RangeExpression
            //
            case 434: {
               //#line 2530 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2528 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2528 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2530 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 435:  MultiplicativeExpression ::= MultiplicativeExpression % RangeExpression
            //
            case 435: {
               //#line 2534 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2532 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2532 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2534 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 437:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 437: {
               //#line 2540 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2538 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2538 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2540 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 438:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 438: {
               //#line 2544 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2542 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2542 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2544 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 440:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 440: {
               //#line 2550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2548 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2548 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2550 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 441:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 441: {
               //#line 2554 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2552 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2552 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2554 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 442:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 442: {
               //#line 2558 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2556 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2556 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2558 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 443:  ShiftExpression ::= ShiftExpression$expr1 -> AdditiveExpression$expr2
            //
            case 443: {
               //#line 2562 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2560 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2560 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2562 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression4(expr1,expr2);
                    break;
            }
            //
            // Rule 447:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 447: {
               //#line 2570 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2568 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2568 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2570 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 448: {
               //#line 2574 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2572 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2572 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2574 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 449:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 449: {
               //#line 2578 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2576 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2576 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2578 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 450:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 450: {
               //#line 2582 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2580 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2580 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2582 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 451:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 451: {
               //#line 2586 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2584 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2584 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2586 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 452:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 452: {
               //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2588 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2588 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2590 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 454:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 454: {
               //#line 2596 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2594 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2594 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2596 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 455:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 455: {
               //#line 2600 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2598 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2598 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2600 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 456:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 456: {
               //#line 2604 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2602 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2602 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2604 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 458:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 458: {
               //#line 2610 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2608 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2608 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2610 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 460:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 460: {
               //#line 2616 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2614 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2614 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2616 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 462:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 462: {
               //#line 2622 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2620 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2620 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2622 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 464:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 464: {
               //#line 2628 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2626 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2626 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2628 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 466:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 466: {
               //#line 2634 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2632 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2632 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2634 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 471:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 471: {
               //#line 2644 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2642 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2642 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2642 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2644 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 474:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 474: {
               //#line 2652 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2650 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2650 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2650 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2652 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 475:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 475: {
               //#line 2656 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2654 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2654 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2654 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2654 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2656 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 476:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 476: {
               //#line 2660 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2658 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2658 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2658 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2658 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2660 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 477:  LeftHandSide ::= ExpressionName
            //
            case 477: {
               //#line 2665 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2663 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2665 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= =
            //
            case 479: {
               //#line 2671 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2671 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= *=
            //
            case 480: {
               //#line 2675 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2675 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= /=
            //
            case 481: {
               //#line 2679 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2679 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= %=
            //
            case 482: {
               //#line 2683 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2683 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= +=
            //
            case 483: {
               //#line 2687 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2687 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= -=
            //
            case 484: {
               //#line 2691 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2691 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 485:  AssignmentOperator ::= <<=
            //
            case 485: {
               //#line 2695 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2695 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 486:  AssignmentOperator ::= >>=
            //
            case 486: {
               //#line 2699 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2699 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 487:  AssignmentOperator ::= >>>=
            //
            case 487: {
               //#line 2703 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2703 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 488:  AssignmentOperator ::= &=
            //
            case 488: {
               //#line 2707 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2707 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 489:  AssignmentOperator ::= ^=
            //
            case 489: {
               //#line 2711 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2711 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 490:  AssignmentOperator ::= |=
            //
            case 490: {
               //#line 2715 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2715 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 493:  PrefixOp ::= +
            //
            case 493: {
               //#line 2725 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2725 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 494:  PrefixOp ::= -
            //
            case 494: {
               //#line 2729 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2729 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 495:  PrefixOp ::= !
            //
            case 495: {
               //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2733 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 496:  PrefixOp ::= ~
            //
            case 496: {
               //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2737 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 497:  BinOp ::= +
            //
            case 497: {
               //#line 2742 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2742 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 498:  BinOp ::= -
            //
            case 498: {
               //#line 2746 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2746 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 499:  BinOp ::= *
            //
            case 499: {
               //#line 2750 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2750 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 500:  BinOp ::= /
            //
            case 500: {
               //#line 2754 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2754 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 501:  BinOp ::= %
            //
            case 501: {
               //#line 2758 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2758 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 502:  BinOp ::= &
            //
            case 502: {
               //#line 2762 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2762 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 503:  BinOp ::= |
            //
            case 503: {
               //#line 2766 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2766 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 504:  BinOp ::= ^
            //
            case 504: {
               //#line 2770 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2770 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 505:  BinOp ::= &&
            //
            case 505: {
               //#line 2774 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2774 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 506:  BinOp ::= ||
            //
            case 506: {
               //#line 2778 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2778 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 507:  BinOp ::= <<
            //
            case 507: {
               //#line 2782 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2782 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 508:  BinOp ::= >>
            //
            case 508: {
               //#line 2786 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2786 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 509:  BinOp ::= >>>
            //
            case 509: {
               //#line 2790 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2790 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 510:  BinOp ::= >=
            //
            case 510: {
               //#line 2794 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2794 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 511:  BinOp ::= <=
            //
            case 511: {
               //#line 2798 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2798 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 512:  BinOp ::= >
            //
            case 512: {
               //#line 2802 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2802 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 513:  BinOp ::= <
            //
            case 513: {
               //#line 2806 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2806 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 514:  BinOp ::= ==
            //
            case 514: {
               //#line 2813 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2813 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 515:  BinOp ::= !=
            //
            case 515: {
               //#line 2817 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2817 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 516:  BinOp ::= ..
            //
            case 516: {
               //#line 2822 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2822 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp19();
                    break;
            }
            //
            // Rule 517:  BinOp ::= ->
            //
            case 517: {
               //#line 2826 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2826 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp20();
                    break;
            }
            //
            // Rule 518:  BinOp ::= in
            //
            case 518: {
               //#line 2830 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2830 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp21();
                    break;
            }
            //
            // Rule 519:  Catchesopt ::= $Empty
            //
            case 519: {
               //#line 2838 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2838 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catchesopt0();
                    break;
            }
            //
            // Rule 521:  Identifieropt ::= $Empty
            //
            case 521:
                setResult(null);
                break;

            //
            // Rule 522:  Identifieropt ::= Identifier
            //
            case 522: {
               //#line 2846 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2844 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2846 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 523:  ForUpdateopt ::= $Empty
            //
            case 523: {
               //#line 2851 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2851 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForUpdateopt0();
                    break;
            }
            //
            // Rule 525:  Expressionopt ::= $Empty
            //
            case 525:
                setResult(null);
                break;

            //
            // Rule 527:  ForInitopt ::= $Empty
            //
            case 527: {
               //#line 2861 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2861 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 529:  SwitchLabelsopt ::= $Empty
            //
            case 529: {
               //#line 2867 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2867 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 531:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 531: {
               //#line 2873 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2873 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 533:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 533: {
               //#line 2896 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2896 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 535:  ExtendsInterfacesopt ::= $Empty
            //
            case 535: {
               //#line 2902 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2902 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfacesopt0();
                    break;
            }
            //
            // Rule 537:  ClassBodyopt ::= $Empty
            //
            case 537:
                setResult(null);
                break;

            //
            // Rule 539:  ArgumentListopt ::= $Empty
            //
            case 539: {
               //#line 2932 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2932 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 541:  BlockStatementsopt ::= $Empty
            //
            case 541: {
               //#line 2938 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2938 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatementsopt0();
                    break;
            }
            //
            // Rule 543:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 543:
                setResult(null);
                break;

            //
            // Rule 545:  FormalParameterListopt ::= $Empty
            //
            case 545: {
               //#line 2958 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2958 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 547:  Offersopt ::= $Empty
            //
            case 547: {
               //#line 2970 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2970 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 549:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 549: {
               //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 551:  Interfacesopt ::= $Empty
            //
            case 551: {
               //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3012 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfacesopt0();
                    break;
            }
            //
            // Rule 553:  Superopt ::= $Empty
            //
            case 553:
                setResult(null);
                break;

            //
            // Rule 555:  TypeParametersopt ::= $Empty
            //
            case 555: {
               //#line 3022 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3022 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 557:  FormalParametersopt ::= $Empty
            //
            case 557: {
               //#line 3028 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3028 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 559:  Annotationsopt ::= $Empty
            //
            case 559: {
               //#line 3034 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3034 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 561:  TypeDeclarationsopt ::= $Empty
            //
            case 561: {
               //#line 3040 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 563:  ImportDeclarationsopt ::= $Empty
            //
            case 563: {
               //#line 3046 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3046 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarationsopt0();
                    break;
            }
            //
            // Rule 565:  PackageDeclarationopt ::= $Empty
            //
            case 565:
                setResult(null);
                break;

            //
            // Rule 567:  HasResultTypeopt ::= $Empty
            //
            case 567:
                setResult(null);
                break;

            //
            // Rule 569:  TypeArgumentsopt ::= $Empty
            //
            case 569: {
               //#line 3066 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3066 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 571:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 571: {
               //#line 3072 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3072 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 573:  Propertiesopt ::= $Empty
            //
            case 573: {
               //#line 3078 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3078 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

