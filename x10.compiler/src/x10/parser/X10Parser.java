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
               //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 411 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 411 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 411 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 411 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 411 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 411 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(10);
                //#line 411 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(11);
                //#line 413 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration8(MethodModifiersopt,TypeParametersopt,fp1,Type,WhereClauseopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 46:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) as ? WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 46: {
               //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 416 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 416 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 416 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 416 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(9);
                //#line 416 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(10);
                //#line 416 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(11);
                //#line 416 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(12);
                //#line 418 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration9(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 47:  MethodDeclaration ::= MethodModifiersopt operator TypeParametersopt ( FormalParameter$fp1 ) WhereClauseopt HasResultTypeopt Offersopt MethodBody
            //
            case 47: {
               //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 421 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 421 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 421 "x10/parser/x10.g"
                Object fp1 = (Object) getRhsSym(5);
                //#line 421 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(7);
                //#line 421 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(8);
                //#line 421 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(9);
                //#line 421 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(10);
                //#line 423 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodDeclaration10(MethodModifiersopt,TypeParametersopt,fp1,WhereClauseopt,HasResultTypeopt,Offersopt,MethodBody);
                                                 
                    break;
            }
            //
            // Rule 48:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt MethodBody
            //
            case 48: {
               //#line 429 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 427 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 427 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 427 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(3);
                //#line 427 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(4);
                //#line 427 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 427 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(6);
                //#line 427 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(7);
                //#line 429 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration0(MethodModifiersopt,Identifier,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,MethodBody);
                                              
                    break;
            }
            //
            // Rule 49:  PropertyMethodDeclaration ::= MethodModifiersopt Identifier WhereClauseopt HasResultTypeopt MethodBody
            //
            case 49: {
               //#line 434 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 432 "x10/parser/x10.g"
                Object MethodModifiersopt = (Object) getRhsSym(1);
                //#line 432 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 432 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(3);
                //#line 432 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 432 "x10/parser/x10.g"
                Object MethodBody = (Object) getRhsSym(5);
                //#line 434 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PropertyMethodDeclaration1(MethodModifiersopt,Identifier,WhereClauseopt,HasResultTypeopt,MethodBody);
                                             
                    break;
            }
            //
            // Rule 50:  ExplicitConstructorInvocation ::= this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 50: {
               //#line 440 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 438 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 438 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 440 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 51:  ExplicitConstructorInvocation ::= super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 51: {
               //#line 444 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 442 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 442 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 444 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation1(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 52:  ExplicitConstructorInvocation ::= Primary . this TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 52: {
               //#line 448 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 446 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 446 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 446 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 448 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation2(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 53:  ExplicitConstructorInvocation ::= Primary . super TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 53: {
               //#line 452 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 450 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 450 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 450 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 452 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExplicitConstructorInvocation3(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 54:  NormalInterfaceDeclaration ::= Modifiersopt interface Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt ExtendsInterfacesopt InterfaceBody
            //
            case 54: {
               //#line 457 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 455 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 455 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 455 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 455 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 455 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 455 "x10/parser/x10.g"
                Object ExtendsInterfacesopt = (Object) getRhsSym(7);
                //#line 455 "x10/parser/x10.g"
                Object InterfaceBody = (Object) getRhsSym(8);
                //#line 457 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalInterfaceDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,ExtendsInterfacesopt,InterfaceBody);
                    break;
            }
            //
            // Rule 55:  ClassInstanceCreationExpression ::= new TypeName TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 55: {
               //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 460 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 460 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(3);
                //#line 460 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(5);
                //#line 460 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(7);
                //#line 462 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression0(TypeName,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 56:  ClassInstanceCreationExpression ::= new TypeName [ Type ] [ ArgumentListopt ]
            //
            case 56: {
               //#line 466 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 464 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 464 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(4);
                //#line 464 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 466 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression1(TypeName,Type,ArgumentListopt);
                    break;
            }
            //
            // Rule 57:  ClassInstanceCreationExpression ::= Primary . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 57: {
               //#line 470 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 468 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 468 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 468 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 468 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 468 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 470 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression2(Primary,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 58:  ClassInstanceCreationExpression ::= AmbiguousName . new Identifier TypeArgumentsopt ( ArgumentListopt ) ClassBodyopt
            //
            case 58: {
               //#line 474 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 472 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 472 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(4);
                //#line 472 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(5);
                //#line 472 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(7);
                //#line 472 "x10/parser/x10.g"
                Object ClassBodyopt = (Object) getRhsSym(9);
                //#line 474 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassInstanceCreationExpression3(AmbiguousName,Identifier,TypeArgumentsopt,ArgumentListopt,ClassBodyopt);
                    break;
            }
            //
            // Rule 59:  AssignPropertyCall ::= property TypeArgumentsopt ( ArgumentListopt ) ;
            //
            case 59: {
               //#line 479 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 477 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 477 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 479 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignPropertyCall0(TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 63:  FunctionType ::= TypeParametersopt ( FormalParameterListopt ) WhereClauseopt Offersopt => Type
            //
            case 63: {
               //#line 489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 487 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(1);
                //#line 487 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(3);
                //#line 487 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(5);
                //#line 487 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(6);
                //#line 487 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(8);
                //#line 489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FunctionType0(TypeParametersopt,FormalParameterListopt,WhereClauseopt,Offersopt,Type);
                    break;
            }
            //
            // Rule 65:  AnnotatedType ::= Type Annotations
            //
            case 65: {
               //#line 501 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 499 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 499 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(2);
                //#line 501 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotatedType0(Type,Annotations);
                    break;
            }
            //
            // Rule 68:  ConstrainedType ::= ( Type )
            //
            case 68: {
               //#line 508 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 506 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 508 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstrainedType2(Type);
                    break;
            }
            //
            // Rule 69:  VoidType ::= void
            //
            case 69: {
               //#line 513 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 513 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VoidType0();
                    break;
            }
            //
            // Rule 70:  SimpleNamedType ::= TypeName
            //
            case 70: {
               //#line 519 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 517 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 519 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType0(TypeName);
                    break;
            }
            //
            // Rule 71:  SimpleNamedType ::= Primary . Identifier
            //
            case 71: {
               //#line 523 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 521 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 521 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 523 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType1(Primary,Identifier);
                    break;
            }
            //
            // Rule 72:  SimpleNamedType ::= DepNamedType . Identifier
            //
            case 72: {
               //#line 527 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 525 "x10/parser/x10.g"
                Object DepNamedType = (Object) getRhsSym(1);
                //#line 525 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 527 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SimpleNamedType2(DepNamedType,Identifier);
                    break;
            }
            //
            // Rule 73:  DepNamedType ::= SimpleNamedType DepParameters
            //
            case 73: {
               //#line 532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 530 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 530 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(2);
                //#line 532 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType0(SimpleNamedType,DepParameters);
                    break;
            }
            //
            // Rule 74:  DepNamedType ::= SimpleNamedType Arguments
            //
            case 74: {
               //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 534 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 534 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 536 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType1(SimpleNamedType,Arguments);
                    break;
            }
            //
            // Rule 75:  DepNamedType ::= SimpleNamedType Arguments DepParameters
            //
            case 75: {
               //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 538 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 538 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(2);
                //#line 538 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 540 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType2(SimpleNamedType,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 76:  DepNamedType ::= SimpleNamedType TypeArguments
            //
            case 76: {
               //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 542 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 542 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 544 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType3(SimpleNamedType,TypeArguments);
                    break;
            }
            //
            // Rule 77:  DepNamedType ::= SimpleNamedType TypeArguments DepParameters
            //
            case 77: {
               //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 546 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 546 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 546 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(3);
                //#line 548 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType4(SimpleNamedType,TypeArguments,DepParameters);
                    break;
            }
            //
            // Rule 78:  DepNamedType ::= SimpleNamedType TypeArguments Arguments
            //
            case 78: {
               //#line 552 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 550 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 550 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 550 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 552 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType5(SimpleNamedType,TypeArguments,Arguments);
                    break;
            }
            //
            // Rule 79:  DepNamedType ::= SimpleNamedType TypeArguments Arguments DepParameters
            //
            case 79: {
               //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 554 "x10/parser/x10.g"
                Object SimpleNamedType = (Object) getRhsSym(1);
                //#line 554 "x10/parser/x10.g"
                Object TypeArguments = (Object) getRhsSym(2);
                //#line 554 "x10/parser/x10.g"
                Object Arguments = (Object) getRhsSym(3);
                //#line 554 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(4);
                //#line 556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepNamedType6(SimpleNamedType,TypeArguments,Arguments,DepParameters);
                    break;
            }
            //
            // Rule 82:  DepParameters ::= { ExistentialListopt Conjunctionopt }
            //
            case 82: {
               //#line 564 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 562 "x10/parser/x10.g"
                Object ExistentialListopt = (Object) getRhsSym(2);
                //#line 562 "x10/parser/x10.g"
                Object Conjunctionopt = (Object) getRhsSym(3);
                //#line 564 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DepParameters0(ExistentialListopt,Conjunctionopt);
                    break;
            }
            //
            // Rule 83:  TypeParamsWithVariance ::= [ TypeParamWithVarianceList ]
            //
            case 83: {
               //#line 570 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 568 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(2);
                //#line 570 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVariance0(TypeParamWithVarianceList);
                    break;
            }
            //
            // Rule 84:  TypeParameters ::= [ TypeParameterList ]
            //
            case 84: {
               //#line 575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 573 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(2);
                //#line 575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameters0(TypeParameterList);
                    break;
            }
            //
            // Rule 85:  FormalParameters ::= ( FormalParameterListopt )
            //
            case 85: {
               //#line 580 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 578 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(2);
                //#line 580 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameters0(FormalParameterListopt);
                    break;
            }
            //
            // Rule 86:  Conjunction ::= Expression
            //
            case 86: {
               //#line 585 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 583 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 585 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction0(Expression);
                    break;
            }
            //
            // Rule 87:  Conjunction ::= Conjunction , Expression
            //
            case 87: {
               //#line 589 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 587 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 587 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 589 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunction1(Conjunction,Expression);
                    break;
            }
            //
            // Rule 88:  HasZeroConstraint ::= Type$t1 haszero
            //
            case 88: {
               //#line 594 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 592 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 594 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasZeroConstraint0(t1);
                    break;
            }
            //
            // Rule 89:  SubtypeConstraint ::= Type$t1 <: Type$t2
            //
            case 89: {
               //#line 599 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 597 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 597 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 599 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint0(t1,t2);
                    break;
            }
            //
            // Rule 90:  SubtypeConstraint ::= Type$t1 :> Type$t2
            //
            case 90: {
               //#line 603 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 601 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 601 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 603 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SubtypeConstraint1(t1,t2);
                    break;
            }
            //
            // Rule 91:  WhereClause ::= DepParameters
            //
            case 91: {
               //#line 608 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 606 "x10/parser/x10.g"
                Object DepParameters = (Object) getRhsSym(1);
                //#line 608 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhereClause0(DepParameters);
                      break;
            }
            //
            // Rule 92:  Conjunctionopt ::= $Empty
            //
            case 92: {
               //#line 613 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 613 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt0();
                      break;
            }
            //
            // Rule 93:  Conjunctionopt ::= Conjunction
            //
            case 93: {
               //#line 617 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 615 "x10/parser/x10.g"
                Object Conjunction = (Object) getRhsSym(1);
                //#line 617 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Conjunctionopt1(Conjunction);
                    break;
            }
            //
            // Rule 94:  ExistentialListopt ::= $Empty
            //
            case 94: {
               //#line 622 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 622 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt0();
                      break;
            }
            //
            // Rule 95:  ExistentialListopt ::= ExistentialList ;
            //
            case 95: {
               //#line 626 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 624 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 626 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialListopt1(ExistentialList);
                    break;
            }
            //
            // Rule 96:  ExistentialList ::= FormalParameter
            //
            case 96: {
               //#line 631 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 629 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 631 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList0(FormalParameter);
                    break;
            }
            //
            // Rule 97:  ExistentialList ::= ExistentialList ; FormalParameter
            //
            case 97: {
               //#line 635 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 633 "x10/parser/x10.g"
                Object ExistentialList = (Object) getRhsSym(1);
                //#line 633 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 635 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExistentialList1(ExistentialList,FormalParameter);
                    break;
            }
            //
            // Rule 100:  NormalClassDeclaration ::= Modifiersopt class Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Superopt Interfacesopt ClassBody
            //
            case 100: {
               //#line 645 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 643 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 643 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 643 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 643 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 643 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 643 "x10/parser/x10.g"
                Object Superopt = (Object) getRhsSym(7);
                //#line 643 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(8);
                //#line 643 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(9);
                //#line 645 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NormalClassDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Superopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 101:  StructDeclaration ::= Modifiersopt struct Identifier TypeParamsWithVarianceopt Propertiesopt WhereClauseopt Interfacesopt ClassBody
            //
            case 101: {
               //#line 651 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 649 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 649 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 649 "x10/parser/x10.g"
                Object TypeParamsWithVarianceopt = (Object) getRhsSym(4);
                //#line 649 "x10/parser/x10.g"
                Object Propertiesopt = (Object) getRhsSym(5);
                //#line 649 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 649 "x10/parser/x10.g"
                Object Interfacesopt = (Object) getRhsSym(7);
                //#line 649 "x10/parser/x10.g"
                Object ClassBody = (Object) getRhsSym(8);
                //#line 651 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StructDeclaration0(Modifiersopt,Identifier,TypeParamsWithVarianceopt,Propertiesopt,WhereClauseopt,Interfacesopt,ClassBody);
                    break;
            }
            //
            // Rule 102:  ConstructorDeclaration ::= Modifiersopt def this TypeParametersopt FormalParameters WhereClauseopt HasResultTypeopt Offersopt ConstructorBody
            //
            case 102: {
               //#line 656 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 654 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 654 "x10/parser/x10.g"
                Object TypeParametersopt = (Object) getRhsSym(4);
                //#line 654 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(5);
                //#line 654 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(6);
                //#line 654 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(7);
                //#line 654 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(8);
                //#line 654 "x10/parser/x10.g"
                Object ConstructorBody = (Object) getRhsSym(9);
                //#line 656 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorDeclaration0(Modifiersopt,TypeParametersopt,FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ConstructorBody);
                                                           
                    break;
            }
            //
            // Rule 103:  Super ::= extends ClassType
            //
            case 103: {
               //#line 662 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 660 "x10/parser/x10.g"
                Object ClassType = (Object) getRhsSym(2);
                //#line 662 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Super0(ClassType);
                    break;
            }
            //
            // Rule 104:  FieldKeyword ::= val
            //
            case 104: {
               //#line 667 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 667 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword0();
                    break;
            }
            //
            // Rule 105:  FieldKeyword ::= var
            //
            case 105: {
               //#line 671 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 671 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldKeyword1();
                    break;
            }
            //
            // Rule 106:  VarKeyword ::= val
            //
            case 106: {
               //#line 678 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 678 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword0();
                    break;
            }
            //
            // Rule 107:  VarKeyword ::= var
            //
            case 107: {
               //#line 682 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 682 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VarKeyword1();
                    break;
            }
            //
            // Rule 108:  FieldDeclaration ::= Modifiersopt FieldKeyword FieldDeclarators ;
            //
            case 108: {
               //#line 688 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 686 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 686 "x10/parser/x10.g"
                Object FieldKeyword = (Object) getRhsSym(2);
                //#line 686 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(3);
                //#line 688 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration0(Modifiersopt,FieldKeyword,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 109:  FieldDeclaration ::= Modifiersopt FieldDeclarators ;
            //
            case 109: {
               //#line 694 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 692 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 692 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(2);
                //#line 694 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclaration1(Modifiersopt,FieldDeclarators);
    
                    break;
            }
            //
            // Rule 112:  AnnotationStatement ::= Annotationsopt NonExpressionStatement
            //
            case 112: {
               //#line 706 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 704 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 704 "x10/parser/x10.g"
                Object NonExpressionStatement = (Object) getRhsSym(2);
                //#line 706 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AnnotationStatement0(Annotationsopt,NonExpressionStatement);
                    break;
            }
            //
            // Rule 138:  OfferStatement ::= offer Expression ;
            //
            case 138: {
               //#line 739 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 737 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 739 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OfferStatement0(Expression);
                    break;
            }
            //
            // Rule 139:  IfThenStatement ::= if ( Expression ) Statement
            //
            case 139: {
               //#line 744 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 742 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 742 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 744 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 140:  IfThenElseStatement ::= if ( Expression ) Statement$s1 else Statement$s2
            //
            case 140: {
               //#line 749 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 747 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 747 "x10/parser/x10.g"
                Object s1 = (Object) getRhsSym(5);
                //#line 747 "x10/parser/x10.g"
                Object s2 = (Object) getRhsSym(7);
                //#line 749 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IfThenElseStatement0(Expression,s1,s2);
                    break;
            }
            //
            // Rule 141:  EmptyStatement ::= ;
            //
            case 141: {
               //#line 754 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 754 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EmptyStatement0();
                    break;
            }
            //
            // Rule 142:  LabeledStatement ::= Identifier : LoopStatement
            //
            case 142: {
               //#line 759 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 757 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 757 "x10/parser/x10.g"
                Object LoopStatement = (Object) getRhsSym(3);
                //#line 759 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LabeledStatement0(Identifier,LoopStatement);
                    break;
            }
            //
            // Rule 147:  ExpressionStatement ::= StatementExpression ;
            //
            case 147: {
               //#line 770 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 768 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 770 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionStatement0(StatementExpression);
                    break;
            }
            //
            // Rule 155:  AssertStatement ::= assert Expression ;
            //
            case 155: {
               //#line 783 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 781 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 783 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement0(Expression);
                    break;
            }
            //
            // Rule 156:  AssertStatement ::= assert Expression$expr1 : Expression$expr2 ;
            //
            case 156: {
               //#line 787 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 785 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(2);
                //#line 785 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(4);
                //#line 787 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssertStatement1(expr1,expr2);
                    break;
            }
            //
            // Rule 157:  SwitchStatement ::= switch ( Expression ) SwitchBlock
            //
            case 157: {
               //#line 792 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 790 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 790 "x10/parser/x10.g"
                Object SwitchBlock = (Object) getRhsSym(5);
                //#line 792 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchStatement0(Expression,SwitchBlock);
                    break;
            }
            //
            // Rule 158:  SwitchBlock ::= { SwitchBlockStatementGroupsopt SwitchLabelsopt }
            //
            case 158: {
               //#line 797 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 795 "x10/parser/x10.g"
                Object SwitchBlockStatementGroupsopt = (Object) getRhsSym(2);
                //#line 795 "x10/parser/x10.g"
                Object SwitchLabelsopt = (Object) getRhsSym(3);
                //#line 797 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlock0(SwitchBlockStatementGroupsopt,SwitchLabelsopt);
                    break;
            }
            //
            // Rule 160:  SwitchBlockStatementGroups ::= SwitchBlockStatementGroups SwitchBlockStatementGroup
            //
            case 160: {
               //#line 803 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 801 "x10/parser/x10.g"
                Object SwitchBlockStatementGroups = (Object) getRhsSym(1);
                //#line 801 "x10/parser/x10.g"
                Object SwitchBlockStatementGroup = (Object) getRhsSym(2);
                //#line 803 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroups1(SwitchBlockStatementGroups,SwitchBlockStatementGroup);
                    break;
            }
            //
            // Rule 161:  SwitchBlockStatementGroup ::= SwitchLabels BlockStatements
            //
            case 161: {
               //#line 808 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 806 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 806 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(2);
                //#line 808 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroup0(SwitchLabels,BlockStatements);
                    break;
            }
            //
            // Rule 162:  SwitchLabels ::= SwitchLabel
            //
            case 162: {
               //#line 813 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 811 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(1);
                //#line 813 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels0(SwitchLabel);
                    break;
            }
            //
            // Rule 163:  SwitchLabels ::= SwitchLabels SwitchLabel
            //
            case 163: {
               //#line 817 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 815 "x10/parser/x10.g"
                Object SwitchLabels = (Object) getRhsSym(1);
                //#line 815 "x10/parser/x10.g"
                Object SwitchLabel = (Object) getRhsSym(2);
                //#line 817 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabels1(SwitchLabels,SwitchLabel);
                    break;
            }
            //
            // Rule 164:  SwitchLabel ::= case ConstantExpression :
            //
            case 164: {
               //#line 822 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 820 "x10/parser/x10.g"
                Object ConstantExpression = (Object) getRhsSym(2);
                //#line 822 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel0(ConstantExpression);
                    break;
            }
            //
            // Rule 165:  SwitchLabel ::= default :
            //
            case 165: {
               //#line 826 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 826 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabel1();
                    break;
            }
            //
            // Rule 166:  WhileStatement ::= while ( Expression ) Statement
            //
            case 166: {
               //#line 831 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 829 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 829 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 831 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhileStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 167:  DoStatement ::= do Statement while ( Expression ) ;
            //
            case 167: {
               //#line 836 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 834 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 834 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 836 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_DoStatement0(Statement,Expression);
                    break;
            }
            //
            // Rule 170:  BasicForStatement ::= for ( ForInitopt ; Expressionopt ; ForUpdateopt ) Statement
            //
            case 170: {
               //#line 844 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 842 "x10/parser/x10.g"
                Object ForInitopt = (Object) getRhsSym(3);
                //#line 842 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(5);
                //#line 842 "x10/parser/x10.g"
                Object ForUpdateopt = (Object) getRhsSym(7);
                //#line 842 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(9);
                //#line 844 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BasicForStatement0(ForInitopt,Expressionopt,ForUpdateopt,Statement);
                    break;
            }
            //
            // Rule 172:  ForInit ::= LocalVariableDeclaration
            //
            case 172: {
               //#line 850 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 848 "x10/parser/x10.g"
                Object LocalVariableDeclaration = (Object) getRhsSym(1);
                //#line 850 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInit1(LocalVariableDeclaration);
                    break;
            }
            //
            // Rule 174:  StatementExpressionList ::= StatementExpression
            //
            case 174: {
               //#line 857 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 855 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(1);
                //#line 857 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList0(StatementExpression);
                    break;
            }
            //
            // Rule 175:  StatementExpressionList ::= StatementExpressionList , StatementExpression
            //
            case 175: {
               //#line 861 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 859 "x10/parser/x10.g"
                Object StatementExpressionList = (Object) getRhsSym(1);
                //#line 859 "x10/parser/x10.g"
                Object StatementExpression = (Object) getRhsSym(3);
                //#line 861 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_StatementExpressionList1(StatementExpressionList,StatementExpression);
                    break;
            }
            //
            // Rule 176:  BreakStatement ::= break Identifieropt ;
            //
            case 176: {
               //#line 866 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 864 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 866 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BreakStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 177:  ContinueStatement ::= continue Identifieropt ;
            //
            case 177: {
               //#line 871 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 869 "x10/parser/x10.g"
                Object Identifieropt = (Object) getRhsSym(2);
                //#line 871 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ContinueStatement0(Identifieropt);
                    break;
            }
            //
            // Rule 178:  ReturnStatement ::= return Expressionopt ;
            //
            case 178: {
               //#line 876 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 874 "x10/parser/x10.g"
                Object Expressionopt = (Object) getRhsSym(2);
                //#line 876 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ReturnStatement0(Expressionopt);
                    break;
            }
            //
            // Rule 179:  ThrowStatement ::= throw Expression ;
            //
            case 179: {
               //#line 881 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 879 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 881 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ThrowStatement0(Expression);
                    break;
            }
            //
            // Rule 180:  TryStatement ::= try Block Catches
            //
            case 180: {
               //#line 886 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 884 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 884 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(3);
                //#line 886 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement0(Block,Catches);
                    break;
            }
            //
            // Rule 181:  TryStatement ::= try Block Catchesopt Finally
            //
            case 181: {
               //#line 890 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 888 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 888 "x10/parser/x10.g"
                Object Catchesopt = (Object) getRhsSym(3);
                //#line 888 "x10/parser/x10.g"
                Object Finally = (Object) getRhsSym(4);
                //#line 890 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TryStatement1(Block,Catchesopt,Finally);
                    break;
            }
            //
            // Rule 182:  Catches ::= CatchClause
            //
            case 182: {
               //#line 895 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 893 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(1);
                //#line 895 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches0(CatchClause);
                    break;
            }
            //
            // Rule 183:  Catches ::= Catches CatchClause
            //
            case 183: {
               //#line 899 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 897 "x10/parser/x10.g"
                Object Catches = (Object) getRhsSym(1);
                //#line 897 "x10/parser/x10.g"
                Object CatchClause = (Object) getRhsSym(2);
                //#line 899 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catches1(Catches,CatchClause);
                    break;
            }
            //
            // Rule 184:  CatchClause ::= catch ( FormalParameter ) Block
            //
            case 184: {
               //#line 904 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 902 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 902 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 904 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CatchClause0(FormalParameter,Block);
                    break;
            }
            //
            // Rule 185:  Finally ::= finally Block
            //
            case 185: {
               //#line 909 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 907 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 909 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Finally0(Block);
                    break;
            }
            //
            // Rule 186:  ClockedClause ::= clocked ( ClockList )
            //
            case 186: {
               //#line 914 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 912 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(3);
                //#line 914 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClause0(ClockList);
                    break;
            }
            //
            // Rule 187:  AsyncStatement ::= async ClockedClauseopt Statement
            //
            case 187: {
               //#line 920 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 918 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(2);
                //#line 918 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 920 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement0(ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 188:  AsyncStatement ::= clocked async Statement
            //
            case 188: {
               //#line 924 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 922 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 924 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AsyncStatement1(Statement);
                    break;
            }
            //
            // Rule 189:  AtStatement ::= at PlaceExpressionSingleList Statement
            //
            case 189: {
               //#line 930 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 928 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 928 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 930 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtStatement0(PlaceExpressionSingleList,Statement);
                    break;
            }
            //
            // Rule 190:  AtomicStatement ::= atomic Statement
            //
            case 190: {
               //#line 935 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 933 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 935 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtomicStatement0(Statement);
                    break;
            }
            //
            // Rule 191:  WhenStatement ::= when ( Expression ) Statement
            //
            case 191: {
               //#line 941 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 939 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 939 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 941 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_WhenStatement0(Expression,Statement);
                    break;
            }
            //
            // Rule 192:  AtEachStatement ::= ateach ( LoopIndex in Expression ) ClockedClauseopt Statement
            //
            case 192: {
               //#line 1003 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1001 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1001 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1001 "x10/parser/x10.g"
                Object ClockedClauseopt = (Object) getRhsSym(7);
                //#line 1001 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(8);
                //#line 1003 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement0(LoopIndex,Expression,ClockedClauseopt,Statement);
                    break;
            }
            //
            // Rule 193:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 193: {
               //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1005 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1005 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1005 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 194:  FinishStatement ::= finish Statement
            //
            case 194: {
               //#line 1012 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1010 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1012 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 195:  FinishStatement ::= clocked finish Statement
            //
            case 195: {
               //#line 1016 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1014 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1016 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 196:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 196: {
               //#line 1020 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1018 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1020 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 198:  NextStatement ::= next ;
            //
            case 198: {
               //#line 1027 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1027 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 199:  ResumeStatement ::= resume ;
            //
            case 199: {
               //#line 1032 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1032 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 200:  ClockList ::= Clock
            //
            case 200: {
               //#line 1037 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1035 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1037 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 201:  ClockList ::= ClockList , Clock
            //
            case 201: {
               //#line 1041 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1039 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1039 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1041 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 202:  Clock ::= Expression
            //
            case 202: {
               //#line 1047 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1045 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1047 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 204:  CastExpression ::= ExpressionName
            //
            case 204: {
               //#line 1059 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1057 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1059 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 205:  CastExpression ::= CastExpression as Type
            //
            case 205: {
               //#line 1063 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1061 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1061 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1063 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 206:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 206: {
               //#line 1069 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1067 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1069 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 207:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 207: {
               //#line 1073 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1071 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1071 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1073 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 208:  TypeParameterList ::= TypeParameter
            //
            case 208: {
               //#line 1078 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1076 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1078 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 209:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 209: {
               //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1080 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1080 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 210:  TypeParamWithVariance ::= Identifier
            //
            case 210: {
               //#line 1087 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1085 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1087 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 211:  TypeParamWithVariance ::= + Identifier
            //
            case 211: {
               //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1089 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= - Identifier
            //
            case 212: {
               //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1093 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1095 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParameter ::= Identifier
            //
            case 213: {
               //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1098 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 214:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 214: {
               //#line 1124 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1122 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1122 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1122 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1122 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1122 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1124 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 215:  LastExpression ::= Expression
            //
            case 215: {
               //#line 1129 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1127 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1129 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 216:  ClosureBody ::= ConditionalExpression
            //
            case 216: {
               //#line 1134 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1132 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1134 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 217:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 217: {
               //#line 1138 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1136 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1136 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1136 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1138 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= Annotationsopt Block
            //
            case 218: {
               //#line 1142 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1140 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1140 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1142 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 219:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 219: {
               //#line 1148 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1146 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1146 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1148 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 220:  FinishExpression ::= finish ( Expression ) Block
            //
            case 220: {
               //#line 1153 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1151 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1151 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1153 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishExpression0(Expression,Block);
                    break;
            }
            //
            // Rule 221:  WhereClauseopt ::= $Empty
            //
            case 221:
                setResult(null);
                break;

            //
            // Rule 223:  ClockedClauseopt ::= $Empty
            //
            case 223: {
               //#line 1197 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1197 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 225:  TypeName ::= Identifier
            //
            case 225: {
               //#line 1208 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1206 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1208 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 226:  TypeName ::= TypeName . Identifier
            //
            case 226: {
               //#line 1212 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1210 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1210 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1212 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 228:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 228: {
               //#line 1219 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1217 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1219 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 229:  TypeArgumentList ::= Type
            //
            case 229: {
               //#line 1225 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1223 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1225 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 230:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 230: {
               //#line 1229 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1227 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1227 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1229 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 231:  PackageName ::= Identifier
            //
            case 231: {
               //#line 1238 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1236 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1238 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 232:  PackageName ::= PackageName . Identifier
            //
            case 232: {
               //#line 1242 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1240 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1240 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1242 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 233:  ExpressionName ::= Identifier
            //
            case 233: {
               //#line 1253 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1251 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1253 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 234:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 234: {
               //#line 1257 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1255 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1255 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1257 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 235:  MethodName ::= Identifier
            //
            case 235: {
               //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1260 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 236:  MethodName ::= AmbiguousName . Identifier
            //
            case 236: {
               //#line 1266 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1264 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1264 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1266 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 237:  PackageOrTypeName ::= Identifier
            //
            case 237: {
               //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1269 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 238:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 238: {
               //#line 1275 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1273 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1273 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1275 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 239:  AmbiguousName ::= Identifier
            //
            case 239: {
               //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1278 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 240:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 240: {
               //#line 1284 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1282 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1282 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1284 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 241:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 241: {
               //#line 1291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1289 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1289 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 242:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 242: {
               //#line 1295 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1293 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1293 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1293 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1295 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 243: {
               //#line 1299 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1297 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1297 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1297 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1297 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1299 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1301 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1301 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1301 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1301 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1301 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  ImportDeclarations ::= ImportDeclaration
            //
            case 245: {
               //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1306 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 246:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 246: {
               //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1310 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1310 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 247:  TypeDeclarations ::= TypeDeclaration
            //
            case 247: {
               //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1315 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 248:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 248: {
               //#line 1321 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1319 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1319 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1321 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 249:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 249: {
               //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1324 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1324 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 252:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 252: {
               //#line 1337 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1335 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1337 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 253:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 253: {
               //#line 1342 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1340 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1342 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 257:  TypeDeclaration ::= ;
            //
            case 257: {
               //#line 1356 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1356 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 258:  Interfaces ::= implements InterfaceTypeList
            //
            case 258: {
               //#line 1472 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1470 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1472 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 259:  InterfaceTypeList ::= Type
            //
            case 259: {
               //#line 1477 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1475 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1477 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 260:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 260: {
               //#line 1481 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1479 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1479 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1481 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 261:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 261: {
               //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1487 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 263:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 263: {
               //#line 1495 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1493 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1493 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1495 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 265:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 265: {
               //#line 1515 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1513 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1515 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 267:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 267: {
               //#line 1521 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1519 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1521 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 268:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 268: {
               //#line 1525 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1523 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1525 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 269: {
               //#line 1529 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1527 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1529 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 270: {
               //#line 1533 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1531 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1533 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 271: {
               //#line 1537 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1535 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1537 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= ;
            //
            case 272: {
               //#line 1541 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1541 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 273:  FormalDeclarators ::= FormalDeclarator
            //
            case 273: {
               //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 274:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 274: {
               //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1548 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1548 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 275:  FieldDeclarators ::= FieldDeclarator
            //
            case 275: {
               //#line 1556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1554 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 276:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 276: {
               //#line 1560 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1558 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1558 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1560 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 277:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 277: {
               //#line 1566 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1564 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1566 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 278:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 278: {
               //#line 1570 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1568 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1568 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1570 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 279:  VariableDeclarators ::= VariableDeclarator
            //
            case 279: {
               //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1573 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 280:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 280: {
               //#line 1579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1577 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1577 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 282:  ResultType ::= : Type
            //
            case 282: {
               //#line 1633 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1631 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1633 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 283:  HasResultType ::= : Type
            //
            case 283: {
               //#line 1637 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1635 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1637 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 284:  HasResultType ::= <: Type
            //
            case 284: {
               //#line 1641 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1639 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1641 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 285:  FormalParameterList ::= FormalParameter
            //
            case 285: {
               //#line 1655 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1653 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1655 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 286:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 286: {
               //#line 1659 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1657 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1657 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1659 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 287:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 287: {
               //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1662 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1662 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 288:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 288: {
               //#line 1668 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1666 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1666 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1668 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 289: {
               //#line 1672 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1670 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1670 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1670 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1672 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 290: {
               //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1675 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1675 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 291:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 291: {
               //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1679 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1679 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1679 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 292:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 292: {
               //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1684 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1684 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 293:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 293: {
               //#line 1690 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1688 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1688 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1688 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1690 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Type
            //
            case 294: {
               //#line 1694 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1692 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1694 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 295:  Offers ::= offers Type
            //
            case 295: {
               //#line 1832 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1830 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1832 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 296:  MethodBody ::= = LastExpression ;
            //
            case 296: {
               //#line 1838 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1836 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1838 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 297:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 297: {
               //#line 1842 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1840 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1840 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1840 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1842 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = Annotationsopt Block
            //
            case 298: {
               //#line 1846 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1844 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1844 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1846 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= Annotationsopt Block
            //
            case 299: {
               //#line 1850 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1848 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1848 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1850 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody3(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= ;
            //
            case 300:
                setResult(null);
                break;

            //
            // Rule 301:  ConstructorBody ::= = ConstructorBlock
            //
            case 301: {
               //#line 1920 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1918 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1920 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 302:  ConstructorBody ::= ConstructorBlock
            //
            case 302: {
               //#line 1924 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1922 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1924 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 303:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 303: {
               //#line 1928 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1926 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1928 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= = AssignPropertyCall
            //
            case 304: {
               //#line 1932 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1930 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1932 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody3(AssignPropertyCall);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= ;
            //
            case 305:
                setResult(null);
                break;

            //
            // Rule 306:  ConstructorBlock ::= { ExplicitConstructorInvocationopt BlockStatementsopt }
            //
            case 306: {
               //#line 1939 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1937 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1937 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1939 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 307:  Arguments ::= ( ArgumentListopt )
            //
            case 307: {
               //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1942 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1944 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 309:  ExtendsInterfaces ::= extends Type
            //
            case 309: {
               //#line 2000 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1998 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2000 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 310:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 310: {
               //#line 2004 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2002 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2002 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2004 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 311:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 311: {
               //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2010 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2012 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 313:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 313: {
               //#line 2018 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2016 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2016 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2018 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 314:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 314: {
               //#line 2023 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2021 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2023 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 315: {
               //#line 2027 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2025 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2027 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 316: {
               //#line 2031 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2029 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2031 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 317: {
               //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2033 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2035 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 318: {
               //#line 2039 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2037 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2039 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 319: {
               //#line 2043 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2041 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2043 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= ;
            //
            case 320: {
               //#line 2047 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2047 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 321:  Annotations ::= Annotation
            //
            case 321: {
               //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2050 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 322:  Annotations ::= Annotations Annotation
            //
            case 322: {
               //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2054 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2054 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 323:  Annotation ::= @ NamedType
            //
            case 323: {
               //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 324:  Identifier ::= IDENTIFIER$ident
            //
            case 324: {
               //#line 2075 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2073 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2075 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 325:  Block ::= { BlockStatementsopt }
            //
            case 325: {
               //#line 2110 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2108 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2110 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 326:  BlockStatements ::= BlockStatement
            //
            case 326: {
               //#line 2115 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2113 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2115 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 327:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 327: {
               //#line 2119 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2117 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2117 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2119 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 329:  BlockStatement ::= ClassDeclaration
            //
            case 329: {
               //#line 2125 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2123 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2125 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 330:  BlockStatement ::= TypeDefDeclaration
            //
            case 330: {
               //#line 2129 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2127 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2129 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= Statement
            //
            case 331: {
               //#line 2133 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2131 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2133 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 332:  IdentifierList ::= Identifier
            //
            case 332: {
               //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2136 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 333:  IdentifierList ::= IdentifierList , Identifier
            //
            case 333: {
               //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2140 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2140 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 334:  FormalDeclarator ::= Identifier ResultType
            //
            case 334: {
               //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2145 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2145 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 335:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 335: {
               //#line 2151 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2149 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2149 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2151 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 336: {
               //#line 2155 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2153 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2153 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2153 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2155 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 337:  FieldDeclarator ::= Identifier HasResultType
            //
            case 337: {
               //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2158 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 338:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 338: {
               //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2162 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2162 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2162 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 339:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
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
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 340:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2171 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2171 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2171 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2177 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2175 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2175 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2175 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2175 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2177 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 342: {
               //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2180 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2180 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2180 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 343: {
               //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2184 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2184 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2184 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2190 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2188 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2188 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2188 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2188 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2190 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 346:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 346: {
               //#line 2197 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2195 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2195 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2195 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2197 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 347:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 347: {
               //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2200 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2200 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2202 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 348: {
               //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2205 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2205 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2205 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2207 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 349:  Primary ::= here
            //
            case 349: {
               //#line 2218 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2218 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 350:  Primary ::= [ ArgumentListopt ]
            //
            case 350: {
               //#line 2222 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2220 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2222 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 352:  Primary ::= self
            //
            case 352: {
               //#line 2228 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2228 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 353:  Primary ::= this
            //
            case 353: {
               //#line 2232 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2232 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 354:  Primary ::= ClassName . this
            //
            case 354: {
               //#line 2236 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2234 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2236 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 355:  Primary ::= ( Expression )
            //
            case 355: {
               //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2238 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2240 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 361:  OperatorFunction ::= TypeName . +
            //
            case 361: {
               //#line 2250 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2248 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2250 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 362:  OperatorFunction ::= TypeName . -
            //
            case 362: {
               //#line 2254 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2252 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2254 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . *
            //
            case 363: {
               //#line 2258 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2256 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2258 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . /
            //
            case 364: {
               //#line 2262 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2260 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2262 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . %
            //
            case 365: {
               //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2264 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2266 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . &
            //
            case 366: {
               //#line 2270 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2268 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2270 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . |
            //
            case 367: {
               //#line 2274 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2272 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2274 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . ^
            //
            case 368: {
               //#line 2278 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2276 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2278 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . <<
            //
            case 369: {
               //#line 2282 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2280 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2282 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . >>
            //
            case 370: {
               //#line 2286 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2284 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2286 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . >>>
            //
            case 371: {
               //#line 2290 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2288 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2290 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . <
            //
            case 372: {
               //#line 2294 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2292 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2294 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . <=
            //
            case 373: {
               //#line 2298 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2296 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2298 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . >=
            //
            case 374: {
               //#line 2302 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2300 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2302 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . >
            //
            case 375: {
               //#line 2306 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2304 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2306 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . ==
            //
            case 376: {
               //#line 2310 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2308 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2310 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . !=
            //
            case 377: {
               //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2312 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2314 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 378:  Literal ::= IntegerLiteral$lit
            //
            case 378: {
               //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 379:  Literal ::= LongLiteral$lit
            //
            case 379: {
               //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2321 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 380:  Literal ::= ByteLiteral
            //
            case 380: {
               //#line 2327 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2327 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralByte();
                    break;
            }
            //
            // Rule 381:  Literal ::= UnsignedByteLiteral
            //
            case 381: {
               //#line 2331 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2331 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUByte();
                    break;
            }
            //
            // Rule 382:  Literal ::= ShortLiteral
            //
            case 382: {
               //#line 2335 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2335 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralShort();
                    break;
            }
            //
            // Rule 383:  Literal ::= UnsignedShortLiteral
            //
            case 383: {
               //#line 2339 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2339 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUShort();
                    break;
            }
            //
            // Rule 384:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 384: {
               //#line 2343 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2341 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2343 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 385:  Literal ::= UnsignedLongLiteral$lit
            //
            case 385: {
               //#line 2347 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2345 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2347 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 386:  Literal ::= FloatingPointLiteral$lit
            //
            case 386: {
               //#line 2351 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2349 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2351 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 387:  Literal ::= DoubleLiteral$lit
            //
            case 387: {
               //#line 2355 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2353 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2355 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 388:  Literal ::= BooleanLiteral
            //
            case 388: {
               //#line 2359 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2357 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2359 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 389:  Literal ::= CharacterLiteral$lit
            //
            case 389: {
               //#line 2363 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2361 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2363 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 390:  Literal ::= StringLiteral$str
            //
            case 390: {
               //#line 2367 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2365 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2367 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 391:  Literal ::= null
            //
            case 391: {
               //#line 2371 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2371 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 392:  BooleanLiteral ::= true$trueLiteral
            //
            case 392: {
               //#line 2376 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2374 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2376 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 393:  BooleanLiteral ::= false$falseLiteral
            //
            case 393: {
               //#line 2380 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2378 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2380 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 394:  ArgumentList ::= Expression
            //
            case 394: {
               //#line 2388 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2386 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2388 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 395:  ArgumentList ::= ArgumentList , Expression
            //
            case 395: {
               //#line 2392 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2390 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2390 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2392 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 396:  FieldAccess ::= Primary . Identifier
            //
            case 396: {
               //#line 2397 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2395 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2395 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2397 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 397:  FieldAccess ::= super . Identifier
            //
            case 397: {
               //#line 2401 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2399 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2401 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 398: {
               //#line 2405 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2403 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2403 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2403 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2405 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 399:  FieldAccess ::= Primary . class$c
            //
            case 399: {
               //#line 2409 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2407 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2407 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2409 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 400:  FieldAccess ::= super . class$c
            //
            case 400: {
               //#line 2413 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2411 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2413 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 401:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 401: {
               //#line 2417 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2415 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2415 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2415 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2417 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 402:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 402: {
               //#line 2422 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2420 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2420 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2420 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2422 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 403:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 403: {
               //#line 2426 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2424 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2424 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2424 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2424 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2426 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 404:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 404: {
               //#line 2430 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2428 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2428 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2428 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2430 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 405:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 405: {
               //#line 2434 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2432 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2432 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2432 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2432 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2432 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2434 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 406:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 406: {
               //#line 2438 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2436 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2436 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2436 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2438 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 407:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 407: {
               //#line 2443 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2441 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2441 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2443 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 408:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 408: {
               //#line 2447 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2445 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2445 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2445 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2447 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 409:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 409: {
               //#line 2451 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2449 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2449 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2451 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 410:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 410: {
               //#line 2455 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2453 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2453 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2453 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2453 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2455 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 414:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 414: {
               //#line 2464 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2462 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2464 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 415:  PostDecrementExpression ::= PostfixExpression --
            //
            case 415: {
               //#line 2469 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2467 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2469 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 418:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 418: {
               //#line 2476 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2474 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2476 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 419:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 419: {
               //#line 2480 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2478 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2480 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 422:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 422: {
               //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2485 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2485 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2487 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 423:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 423: {
               //#line 2492 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2490 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2492 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 424:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 424: {
               //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2495 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2497 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 426:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 426: {
               //#line 2503 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2501 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2503 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 427:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 427: {
               //#line 2507 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2505 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2507 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 429:  RangeExpression ::= RangeExpression$expr1 .. UnaryExpression$expr2
            //
            case 429: {
               //#line 2513 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2511 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2511 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2513 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 431:  MultiplicativeExpression ::= MultiplicativeExpression * RangeExpression
            //
            case 431: {
               //#line 2519 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2517 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2517 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2519 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 432:  MultiplicativeExpression ::= MultiplicativeExpression / RangeExpression
            //
            case 432: {
               //#line 2523 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2521 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2521 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2523 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 433:  MultiplicativeExpression ::= MultiplicativeExpression % RangeExpression
            //
            case 433: {
               //#line 2527 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2525 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2525 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2527 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 435:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 435: {
               //#line 2533 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2531 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2531 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2533 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 436:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 436: {
               //#line 2537 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2535 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2535 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2537 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 438:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 438: {
               //#line 2543 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2541 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2541 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2543 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 439:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 439: {
               //#line 2547 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2545 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2545 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2547 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 440:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 440: {
               //#line 2551 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2549 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2549 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2551 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 441:  ShiftExpression ::= ShiftExpression$expr1 -> AdditiveExpression$expr2
            //
            case 441: {
               //#line 2555 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2553 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2553 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2555 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression4(expr1,expr2);
                    break;
            }
            //
            // Rule 445:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 445: {
               //#line 2563 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2561 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2561 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2563 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 446:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 446: {
               //#line 2567 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2565 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2565 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2567 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 447:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 447: {
               //#line 2571 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2569 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2569 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2571 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 448: {
               //#line 2575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2573 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2573 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 449:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 449: {
               //#line 2579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2577 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2577 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 450:  RelationalExpression ::= RelationalExpression in ShiftExpression
            //
            case 450: {
               //#line 2583 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2581 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2581 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2583 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression8(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 452:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 452: {
               //#line 2589 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2587 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2587 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2589 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 453:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 453: {
               //#line 2593 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2591 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2591 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2593 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 454:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 454: {
               //#line 2597 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2595 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2595 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2597 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 456:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 456: {
               //#line 2603 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2601 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2601 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2603 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 458:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 458: {
               //#line 2609 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2607 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2607 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2609 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 460:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 460: {
               //#line 2615 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2613 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2613 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2615 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 462:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 462: {
               //#line 2621 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2619 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2619 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2621 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 464:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 464: {
               //#line 2627 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2625 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2625 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2627 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 469:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 469: {
               //#line 2637 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2635 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2635 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2635 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2637 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 472:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 472: {
               //#line 2645 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2643 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2643 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2643 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2645 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 473:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 473: {
               //#line 2649 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2647 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2647 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2647 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2647 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2649 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 474:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 474: {
               //#line 2653 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2651 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2651 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2651 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2651 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2653 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 475:  LeftHandSide ::= ExpressionName
            //
            case 475: {
               //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2656 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 477:  AssignmentOperator ::= =
            //
            case 477: {
               //#line 2664 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2664 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 478:  AssignmentOperator ::= *=
            //
            case 478: {
               //#line 2668 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2668 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= /=
            //
            case 479: {
               //#line 2672 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2672 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= %=
            //
            case 480: {
               //#line 2676 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2676 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= +=
            //
            case 481: {
               //#line 2680 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2680 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= -=
            //
            case 482: {
               //#line 2684 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2684 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= <<=
            //
            case 483: {
               //#line 2688 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2688 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= >>=
            //
            case 484: {
               //#line 2692 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2692 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 485:  AssignmentOperator ::= >>>=
            //
            case 485: {
               //#line 2696 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2696 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 486:  AssignmentOperator ::= &=
            //
            case 486: {
               //#line 2700 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2700 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 487:  AssignmentOperator ::= ^=
            //
            case 487: {
               //#line 2704 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2704 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 488:  AssignmentOperator ::= |=
            //
            case 488: {
               //#line 2708 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2708 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 491:  PrefixOp ::= +
            //
            case 491: {
               //#line 2718 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2718 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 492:  PrefixOp ::= -
            //
            case 492: {
               //#line 2722 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2722 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 493:  PrefixOp ::= !
            //
            case 493: {
               //#line 2726 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2726 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 494:  PrefixOp ::= ~
            //
            case 494: {
               //#line 2730 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2730 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 495:  BinOp ::= +
            //
            case 495: {
               //#line 2735 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2735 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 496:  BinOp ::= -
            //
            case 496: {
               //#line 2739 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2739 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 497:  BinOp ::= *
            //
            case 497: {
               //#line 2743 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2743 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 498:  BinOp ::= /
            //
            case 498: {
               //#line 2747 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2747 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 499:  BinOp ::= %
            //
            case 499: {
               //#line 2751 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2751 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 500:  BinOp ::= &
            //
            case 500: {
               //#line 2755 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2755 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 501:  BinOp ::= |
            //
            case 501: {
               //#line 2759 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2759 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 502:  BinOp ::= ^
            //
            case 502: {
               //#line 2763 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2763 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 503:  BinOp ::= &&
            //
            case 503: {
               //#line 2767 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2767 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 504:  BinOp ::= ||
            //
            case 504: {
               //#line 2771 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2771 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 505:  BinOp ::= <<
            //
            case 505: {
               //#line 2775 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2775 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 506:  BinOp ::= >>
            //
            case 506: {
               //#line 2779 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2779 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 507:  BinOp ::= >>>
            //
            case 507: {
               //#line 2783 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2783 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 508:  BinOp ::= >=
            //
            case 508: {
               //#line 2787 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2787 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 509:  BinOp ::= <=
            //
            case 509: {
               //#line 2791 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2791 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 510:  BinOp ::= >
            //
            case 510: {
               //#line 2795 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2795 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 511:  BinOp ::= <
            //
            case 511: {
               //#line 2799 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2799 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 512:  BinOp ::= ==
            //
            case 512: {
               //#line 2806 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2806 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 513:  BinOp ::= !=
            //
            case 513: {
               //#line 2810 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2810 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 514:  BinOp ::= ..
            //
            case 514: {
               //#line 2815 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2815 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp19();
                    break;
            }
            //
            // Rule 515:  BinOp ::= ->
            //
            case 515: {
               //#line 2819 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2819 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp20();
                    break;
            }
            //
            // Rule 516:  BinOp ::= in
            //
            case 516: {
               //#line 2823 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2823 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp21();
                    break;
            }
            //
            // Rule 517:  Catchesopt ::= $Empty
            //
            case 517: {
               //#line 2831 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2831 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Catchesopt0();
                    break;
            }
            //
            // Rule 519:  Identifieropt ::= $Empty
            //
            case 519:
                setResult(null);
                break;

            //
            // Rule 520:  Identifieropt ::= Identifier
            //
            case 520: {
               //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2837 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2839 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 521:  ForUpdateopt ::= $Empty
            //
            case 521: {
               //#line 2844 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2844 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForUpdateopt0();
                    break;
            }
            //
            // Rule 523:  Expressionopt ::= $Empty
            //
            case 523:
                setResult(null);
                break;

            //
            // Rule 525:  ForInitopt ::= $Empty
            //
            case 525: {
               //#line 2854 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2854 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 527:  SwitchLabelsopt ::= $Empty
            //
            case 527: {
               //#line 2860 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2860 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 529:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 529: {
               //#line 2866 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2866 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 531:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 531: {
               //#line 2889 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2889 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 533:  ExtendsInterfacesopt ::= $Empty
            //
            case 533: {
               //#line 2895 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2895 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfacesopt0();
                    break;
            }
            //
            // Rule 535:  ClassBodyopt ::= $Empty
            //
            case 535:
                setResult(null);
                break;

            //
            // Rule 537:  ArgumentListopt ::= $Empty
            //
            case 537: {
               //#line 2925 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2925 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 539:  BlockStatementsopt ::= $Empty
            //
            case 539: {
               //#line 2931 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2931 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatementsopt0();
                    break;
            }
            //
            // Rule 541:  ExplicitConstructorInvocationopt ::= $Empty
            //
            case 541:
                setResult(null);
                break;

            //
            // Rule 543:  FormalParameterListopt ::= $Empty
            //
            case 543: {
               //#line 2951 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2951 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 545:  Offersopt ::= $Empty
            //
            case 545: {
               //#line 2963 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2963 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 547:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 547: {
               //#line 2999 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2999 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 549:  Interfacesopt ::= $Empty
            //
            case 549: {
               //#line 3005 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3005 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfacesopt0();
                    break;
            }
            //
            // Rule 551:  Superopt ::= $Empty
            //
            case 551:
                setResult(null);
                break;

            //
            // Rule 553:  TypeParametersopt ::= $Empty
            //
            case 553: {
               //#line 3015 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3015 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 555:  FormalParametersopt ::= $Empty
            //
            case 555: {
               //#line 3021 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3021 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 557:  Annotationsopt ::= $Empty
            //
            case 557: {
               //#line 3027 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3027 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 559:  TypeDeclarationsopt ::= $Empty
            //
            case 559: {
               //#line 3033 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3033 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 561:  ImportDeclarationsopt ::= $Empty
            //
            case 561: {
               //#line 3039 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3039 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarationsopt0();
                    break;
            }
            //
            // Rule 563:  PackageDeclarationopt ::= $Empty
            //
            case 563:
                setResult(null);
                break;

            //
            // Rule 565:  HasResultTypeopt ::= $Empty
            //
            case 565:
                setResult(null);
                break;

            //
            // Rule 567:  TypeArgumentsopt ::= $Empty
            //
            case 567: {
               //#line 3059 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3059 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 569:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 569: {
               //#line 3065 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3065 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 571:  Propertiesopt ::= $Empty
            //
            case 571: {
               //#line 3071 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3071 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

