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
            // Rule 193:  AtEachStatement ::= ateach ( Expression ) Statement
            //
            case 193: {
               //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1005 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1005 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1007 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtEachStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 194:  EnhancedForStatement ::= for ( LoopIndex in Expression ) Statement
            //
            case 194: {
               //#line 1011 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1009 "x10/parser/x10.g"
                Object LoopIndex = (Object) getRhsSym(3);
                //#line 1009 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(5);
                //#line 1009 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(7);
                //#line 1011 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement0(LoopIndex,Expression,Statement);
                    break;
            }
            //
            // Rule 195:  EnhancedForStatement ::= for ( Expression ) Statement
            //
            case 195: {
               //#line 1015 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1013 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1013 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(5);
                //#line 1015 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EnhancedForStatement1(Expression,Statement);
                    break;
            }
            //
            // Rule 196:  FinishStatement ::= finish Statement
            //
            case 196: {
               //#line 1021 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1019 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(2);
                //#line 1021 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement0(Statement);
                    break;
            }
            //
            // Rule 197:  FinishStatement ::= clocked finish Statement
            //
            case 197: {
               //#line 1025 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1023 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(3);
                //#line 1025 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FinishStatement1(Statement);
                    break;
            }
            //
            // Rule 198:  PlaceExpressionSingleList ::= ( PlaceExpression )
            //
            case 198: {
               //#line 1029 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1027 "x10/parser/x10.g"
                Object PlaceExpression = (Object) getRhsSym(2);
                //#line 1029 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PlaceExpressionSingleList0(PlaceExpression);
                    break;
            }
            //
            // Rule 200:  NextStatement ::= next ;
            //
            case 200: {
               //#line 1036 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1036 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_NextStatement0();
                    break;
            }
            //
            // Rule 201:  ResumeStatement ::= resume ;
            //
            case 201: {
               //#line 1041 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1041 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResumeStatement0();
                    break;
            }
            //
            // Rule 202:  ClockList ::= Clock
            //
            case 202: {
               //#line 1046 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1044 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(1);
                //#line 1046 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList0(Clock);
                    break;
            }
            //
            // Rule 203:  ClockList ::= ClockList , Clock
            //
            case 203: {
               //#line 1050 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1048 "x10/parser/x10.g"
                Object ClockList = (Object) getRhsSym(1);
                //#line 1048 "x10/parser/x10.g"
                Object Clock = (Object) getRhsSym(3);
                //#line 1050 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockList1(ClockList,Clock);
                    break;
            }
            //
            // Rule 204:  Clock ::= Expression
            //
            case 204: {
               //#line 1056 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1054 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1056 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Clock0(Expression);
                    break;
            }
            //
            // Rule 206:  CastExpression ::= ExpressionName
            //
            case 206: {
               //#line 1068 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1066 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 1068 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression1(ExpressionName);
                    break;
            }
            //
            // Rule 207:  CastExpression ::= CastExpression as Type
            //
            case 207: {
               //#line 1072 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1070 "x10/parser/x10.g"
                Object CastExpression = (Object) getRhsSym(1);
                //#line 1070 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1072 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CastExpression2(CastExpression,Type);
                    break;
            }
            //
            // Rule 208:  TypeParamWithVarianceList ::= TypeParamWithVariance
            //
            case 208: {
               //#line 1078 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1076 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(1);
                //#line 1078 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList0(TypeParamWithVariance);
                    break;
            }
            //
            // Rule 209:  TypeParamWithVarianceList ::= TypeParamWithVarianceList , TypeParamWithVariance
            //
            case 209: {
               //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1080 "x10/parser/x10.g"
                Object TypeParamWithVarianceList = (Object) getRhsSym(1);
                //#line 1080 "x10/parser/x10.g"
                Object TypeParamWithVariance = (Object) getRhsSym(3);
                //#line 1082 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVarianceList1(TypeParamWithVarianceList,TypeParamWithVariance);
                    break;
            }
            //
            // Rule 210:  TypeParameterList ::= TypeParameter
            //
            case 210: {
               //#line 1087 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1085 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(1);
                //#line 1087 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList0(TypeParameter);
                    break;
            }
            //
            // Rule 211:  TypeParameterList ::= TypeParameterList , TypeParameter
            //
            case 211: {
               //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1089 "x10/parser/x10.g"
                Object TypeParameterList = (Object) getRhsSym(1);
                //#line 1089 "x10/parser/x10.g"
                Object TypeParameter = (Object) getRhsSym(3);
                //#line 1091 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameterList1(TypeParameterList,TypeParameter);
                    break;
            }
            //
            // Rule 212:  TypeParamWithVariance ::= Identifier
            //
            case 212: {
               //#line 1096 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1094 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1096 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance0(Identifier);
                    break;
            }
            //
            // Rule 213:  TypeParamWithVariance ::= + Identifier
            //
            case 213: {
               //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1098 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1100 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance1(Identifier);
                    break;
            }
            //
            // Rule 214:  TypeParamWithVariance ::= - Identifier
            //
            case 214: {
               //#line 1104 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1102 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(2);
                //#line 1104 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamWithVariance2(Identifier);
                    break;
            }
            //
            // Rule 215:  TypeParameter ::= Identifier
            //
            case 215: {
               //#line 1109 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1107 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1109 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParameter0(Identifier);
                    break;
            }
            //
            // Rule 216:  ClosureExpression ::= FormalParameters WhereClauseopt HasResultTypeopt Offersopt => ClosureBody
            //
            case 216: {
               //#line 1133 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1131 "x10/parser/x10.g"
                Object FormalParameters = (Object) getRhsSym(1);
                //#line 1131 "x10/parser/x10.g"
                Object WhereClauseopt = (Object) getRhsSym(2);
                //#line 1131 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(3);
                //#line 1131 "x10/parser/x10.g"
                Object Offersopt = (Object) getRhsSym(4);
                //#line 1131 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(6);
                //#line 1133 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureExpression0(FormalParameters,WhereClauseopt,HasResultTypeopt,Offersopt,ClosureBody);
                    break;
            }
            //
            // Rule 217:  LastExpression ::= Expression
            //
            case 217: {
               //#line 1138 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1136 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 1138 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LastExpression0(Expression);
                    break;
            }
            //
            // Rule 218:  ClosureBody ::= ConditionalExpression
            //
            case 218: {
               //#line 1143 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1141 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(1);
                //#line 1143 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody0(ConditionalExpression);
                    break;
            }
            //
            // Rule 219:  ClosureBody ::= Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 219: {
               //#line 1147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1145 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1145 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1145 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(4);
                //#line 1147 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 220:  ClosureBody ::= Annotationsopt Block
            //
            case 220: {
               //#line 1151 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1149 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1149 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1151 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClosureBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 221:  AtExpression ::= at PlaceExpressionSingleList ClosureBody
            //
            case 221: {
               //#line 1157 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1155 "x10/parser/x10.g"
                Object PlaceExpressionSingleList = (Object) getRhsSym(2);
                //#line 1155 "x10/parser/x10.g"
                Object ClosureBody = (Object) getRhsSym(3);
                //#line 1157 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AtExpression0(PlaceExpressionSingleList,ClosureBody);
                    break;
            }
            //
            // Rule 222:  FinishExpression ::= finish ( Expression ) Block
            //
            case 222: {
               //#line 1162 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1160 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 1160 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(5);
                //#line 1162 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1206 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1206 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClockedClauseopt0();
                    break;
            }
            //
            // Rule 227:  TypeName ::= Identifier
            //
            case 227: {
               //#line 1217 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1215 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1217 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName1(Identifier);
                    break;
            }
            //
            // Rule 228:  TypeName ::= TypeName . Identifier
            //
            case 228: {
               //#line 1221 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1219 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 1219 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1221 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeName2(TypeName,Identifier);
                    break;
            }
            //
            // Rule 230:  TypeArguments ::= [ TypeArgumentList ]
            //
            case 230: {
               //#line 1228 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1226 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(2);
                //#line 1228 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArguments0(TypeArgumentList);
                    break;
            }
            //
            // Rule 231:  TypeArgumentList ::= Type
            //
            case 231: {
               //#line 1234 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1232 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1234 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList0(Type);
                    break;
            }
            //
            // Rule 232:  TypeArgumentList ::= TypeArgumentList , Type
            //
            case 232: {
               //#line 1238 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1236 "x10/parser/x10.g"
                Object TypeArgumentList = (Object) getRhsSym(1);
                //#line 1236 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1238 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentList1(TypeArgumentList,Type);
                    break;
            }
            //
            // Rule 233:  PackageName ::= Identifier
            //
            case 233: {
               //#line 1247 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1245 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1247 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName1(Identifier);
                    break;
            }
            //
            // Rule 234:  PackageName ::= PackageName . Identifier
            //
            case 234: {
               //#line 1251 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1249 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(1);
                //#line 1249 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1251 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageName2(PackageName,Identifier);
                    break;
            }
            //
            // Rule 235:  ExpressionName ::= Identifier
            //
            case 235: {
               //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1260 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1262 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName1(Identifier);
                    break;
            }
            //
            // Rule 236:  ExpressionName ::= AmbiguousName . Identifier
            //
            case 236: {
               //#line 1266 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1264 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1264 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1266 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExpressionName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 237:  MethodName ::= Identifier
            //
            case 237: {
               //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1269 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName1(Identifier);
                    break;
            }
            //
            // Rule 238:  MethodName ::= AmbiguousName . Identifier
            //
            case 238: {
               //#line 1275 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1273 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1273 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1275 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 239:  PackageOrTypeName ::= Identifier
            //
            case 239: {
               //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1278 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1280 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName1(Identifier);
                    break;
            }
            //
            // Rule 240:  PackageOrTypeName ::= PackageOrTypeName . Identifier
            //
            case 240: {
               //#line 1284 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1282 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(1);
                //#line 1282 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1284 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageOrTypeName2(PackageOrTypeName,Identifier);
                    break;
            }
            //
            // Rule 241:  AmbiguousName ::= Identifier
            //
            case 241: {
               //#line 1289 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1287 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1289 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName1(Identifier);
                    break;
            }
            //
            // Rule 242:  AmbiguousName ::= AmbiguousName . Identifier
            //
            case 242: {
               //#line 1293 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1291 "x10/parser/x10.g"
                Object AmbiguousName = (Object) getRhsSym(1);
                //#line 1291 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 1293 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AmbiguousName2(AmbiguousName,Identifier);
                    break;
            }
            //
            // Rule 243:  CompilationUnit ::= PackageDeclarationopt TypeDeclarationsopt
            //
            case 243: {
               //#line 1300 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1298 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1298 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(2);
                //#line 1300 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit0(PackageDeclarationopt,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 244:  CompilationUnit ::= PackageDeclarationopt ImportDeclarations TypeDeclarationsopt
            //
            case 244: {
               //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1302 "x10/parser/x10.g"
                Object PackageDeclarationopt = (Object) getRhsSym(1);
                //#line 1302 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1302 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(3);
                //#line 1304 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit1(PackageDeclarationopt,ImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 245:  CompilationUnit ::= ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 245: {
               //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1306 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1306 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(2);
                //#line 1306 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(3);
                //#line 1306 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(4);
                //#line 1308 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit2(ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 246:  CompilationUnit ::= PackageDeclaration ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
            //
            case 246: {
               //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1310 "x10/parser/x10.g"
                Object PackageDeclaration = (Object) getRhsSym(1);
                //#line 1310 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(2);
                //#line 1310 "x10/parser/x10.g"
                Object misplacedPackageDeclaration = (Object) getRhsSym(3);
                //#line 1310 "x10/parser/x10.g"
                Object misplacedImportDeclarations = (Object) getRhsSym(4);
                //#line 1310 "x10/parser/x10.g"
                Object TypeDeclarationsopt = (Object) getRhsSym(5);
                //#line 1312 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_CompilationUnit3(PackageDeclaration,ImportDeclarations,misplacedPackageDeclaration,misplacedImportDeclarations,TypeDeclarationsopt);
                    break;
            }
            //
            // Rule 247:  ImportDeclarations ::= ImportDeclaration
            //
            case 247: {
               //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1315 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(1);
                //#line 1317 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations0(ImportDeclaration);
                    break;
            }
            //
            // Rule 248:  ImportDeclarations ::= ImportDeclarations ImportDeclaration
            //
            case 248: {
               //#line 1321 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1319 "x10/parser/x10.g"
                Object ImportDeclarations = (Object) getRhsSym(1);
                //#line 1319 "x10/parser/x10.g"
                Object ImportDeclaration = (Object) getRhsSym(2);
                //#line 1321 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ImportDeclarations1(ImportDeclarations,ImportDeclaration);
                    break;
            }
            //
            // Rule 249:  TypeDeclarations ::= TypeDeclaration
            //
            case 249: {
               //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1324 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(1);
                //#line 1326 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations0(TypeDeclaration);
                    break;
            }
            //
            // Rule 250:  TypeDeclarations ::= TypeDeclarations TypeDeclaration
            //
            case 250: {
               //#line 1330 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1328 "x10/parser/x10.g"
                Object TypeDeclarations = (Object) getRhsSym(1);
                //#line 1328 "x10/parser/x10.g"
                Object TypeDeclaration = (Object) getRhsSym(2);
                //#line 1330 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarations1(TypeDeclarations,TypeDeclaration);
                    break;
            }
            //
            // Rule 251:  PackageDeclaration ::= Annotationsopt package PackageName ;
            //
            case 251: {
               //#line 1335 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1333 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1333 "x10/parser/x10.g"
                Object PackageName = (Object) getRhsSym(3);
                //#line 1335 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PackageDeclaration0(Annotationsopt,PackageName);
                    break;
            }
            //
            // Rule 254:  SingleTypeImportDeclaration ::= import TypeName ;
            //
            case 254: {
               //#line 1346 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1344 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(2);
                //#line 1346 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SingleTypeImportDeclaration0(TypeName);
                    break;
            }
            //
            // Rule 255:  TypeImportOnDemandDeclaration ::= import PackageOrTypeName . * ;
            //
            case 255: {
               //#line 1351 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1349 "x10/parser/x10.g"
                Object PackageOrTypeName = (Object) getRhsSym(2);
                //#line 1351 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeImportOnDemandDeclaration0(PackageOrTypeName);
                    break;
            }
            //
            // Rule 259:  TypeDeclaration ::= ;
            //
            case 259: {
               //#line 1365 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1365 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclaration3();
                    break;
            }
            //
            // Rule 260:  Interfaces ::= implements InterfaceTypeList
            //
            case 260: {
               //#line 1481 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1479 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(2);
                //#line 1481 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Interfaces0(InterfaceTypeList);
                    break;
            }
            //
            // Rule 261:  InterfaceTypeList ::= Type
            //
            case 261: {
               //#line 1486 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1484 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1486 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList0(Type);
                    break;
            }
            //
            // Rule 262:  InterfaceTypeList ::= InterfaceTypeList , Type
            //
            case 262: {
               //#line 1490 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1488 "x10/parser/x10.g"
                Object InterfaceTypeList = (Object) getRhsSym(1);
                //#line 1488 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 1490 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceTypeList1(InterfaceTypeList,Type);
                    break;
            }
            //
            // Rule 263:  ClassBody ::= { ClassBodyDeclarationsopt }
            //
            case 263: {
               //#line 1498 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1496 "x10/parser/x10.g"
                Object ClassBodyDeclarationsopt = (Object) getRhsSym(2);
                //#line 1498 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBody0(ClassBodyDeclarationsopt);
                    break;
            }
            //
            // Rule 265:  ClassBodyDeclarations ::= ClassBodyDeclarations ClassBodyDeclaration
            //
            case 265: {
               //#line 1504 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1502 "x10/parser/x10.g"
                Object ClassBodyDeclarations = (Object) getRhsSym(1);
                //#line 1502 "x10/parser/x10.g"
                Object ClassBodyDeclaration = (Object) getRhsSym(2);
                //#line 1504 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarations1(ClassBodyDeclarations,ClassBodyDeclaration);
                    break;
            }
            //
            // Rule 267:  ClassBodyDeclaration ::= ConstructorDeclaration
            //
            case 267: {
               //#line 1524 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1522 "x10/parser/x10.g"
                Object ConstructorDeclaration = (Object) getRhsSym(1);
                //#line 1524 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclaration1(ConstructorDeclaration);
                    break;
            }
            //
            // Rule 269:  ClassMemberDeclaration ::= MethodDeclaration
            //
            case 269: {
               //#line 1530 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1528 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 1530 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration1(MethodDeclaration);
                    break;
            }
            //
            // Rule 270:  ClassMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 270: {
               //#line 1534 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1532 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 1534 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration2(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 271:  ClassMemberDeclaration ::= TypeDefDeclaration
            //
            case 271: {
               //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1536 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 1538 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration3(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 272:  ClassMemberDeclaration ::= ClassDeclaration
            //
            case 272: {
               //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1540 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 1542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration4(ClassDeclaration);
                    break;
            }
            //
            // Rule 273:  ClassMemberDeclaration ::= InterfaceDeclaration
            //
            case 273: {
               //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1544 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 1546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration5(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 274:  ClassMemberDeclaration ::= ;
            //
            case 274: {
               //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 1550 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassMemberDeclaration6();
                    break;
            }
            //
            // Rule 275:  FormalDeclarators ::= FormalDeclarator
            //
            case 275: {
               //#line 1555 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1553 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(1);
                //#line 1555 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators0(FormalDeclarator);
                    break;
            }
            //
            // Rule 276:  FormalDeclarators ::= FormalDeclarators , FormalDeclarator
            //
            case 276: {
               //#line 1559 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1557 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(1);
                //#line 1557 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1559 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarators1(FormalDeclarators,FormalDeclarator);
                    break;
            }
            //
            // Rule 277:  FieldDeclarators ::= FieldDeclarator
            //
            case 277: {
               //#line 1565 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1563 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(1);
                //#line 1565 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators0(FieldDeclarator);
                    break;
            }
            //
            // Rule 278:  FieldDeclarators ::= FieldDeclarators , FieldDeclarator
            //
            case 278: {
               //#line 1569 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1567 "x10/parser/x10.g"
                Object FieldDeclarators = (Object) getRhsSym(1);
                //#line 1567 "x10/parser/x10.g"
                Object FieldDeclarator = (Object) getRhsSym(3);
                //#line 1569 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarators1(FieldDeclarators,FieldDeclarator);
                    break;
            }
            //
            // Rule 279:  VariableDeclaratorsWithType ::= VariableDeclaratorWithType
            //
            case 279: {
               //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1573 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(1);
                //#line 1575 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType0(VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 280:  VariableDeclaratorsWithType ::= VariableDeclaratorsWithType , VariableDeclaratorWithType
            //
            case 280: {
               //#line 1579 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1577 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(1);
                //#line 1577 "x10/parser/x10.g"
                Object VariableDeclaratorWithType = (Object) getRhsSym(3);
                //#line 1579 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorsWithType1(VariableDeclaratorsWithType,VariableDeclaratorWithType);
                    break;
            }
            //
            // Rule 281:  VariableDeclarators ::= VariableDeclarator
            //
            case 281: {
               //#line 1584 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1582 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(1);
                //#line 1584 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators0(VariableDeclarator);
                    break;
            }
            //
            // Rule 282:  VariableDeclarators ::= VariableDeclarators , VariableDeclarator
            //
            case 282: {
               //#line 1588 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1586 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(1);
                //#line 1586 "x10/parser/x10.g"
                Object VariableDeclarator = (Object) getRhsSym(3);
                //#line 1588 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarators1(VariableDeclarators,VariableDeclarator);
                    break;
            }
            //
            // Rule 284:  ResultType ::= : Type
            //
            case 284: {
               //#line 1642 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1640 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1642 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ResultType0(Type);
                    break;
            }
            //
            // Rule 285:  HasResultType ::= : Type
            //
            case 285: {
               //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1644 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1646 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType0(Type);
                    break;
            }
            //
            // Rule 286:  HasResultType ::= <: Type
            //
            case 286: {
               //#line 1650 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1648 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1650 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_HasResultType1(Type);
                    break;
            }
            //
            // Rule 287:  FormalParameterList ::= FormalParameter
            //
            case 287: {
               //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1662 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(1);
                //#line 1664 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList0(FormalParameter);
                    break;
            }
            //
            // Rule 288:  FormalParameterList ::= FormalParameterList , FormalParameter
            //
            case 288: {
               //#line 1668 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1666 "x10/parser/x10.g"
                Object FormalParameterList = (Object) getRhsSym(1);
                //#line 1666 "x10/parser/x10.g"
                Object FormalParameter = (Object) getRhsSym(3);
                //#line 1668 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterList1(FormalParameterList,FormalParameter);
                    break;
            }
            //
            // Rule 289:  LoopIndexDeclarator ::= Identifier HasResultTypeopt
            //
            case 289: {
               //#line 1673 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1671 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1671 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 1673 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator0(Identifier,HasResultTypeopt);
                    break;
            }
            //
            // Rule 290:  LoopIndexDeclarator ::= [ IdentifierList ] HasResultTypeopt
            //
            case 290: {
               //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1675 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 1675 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 1677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator1(IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 291:  LoopIndexDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt
            //
            case 291: {
               //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1679 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 1679 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 1679 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 1681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndexDeclarator2(Identifier,IdentifierList,HasResultTypeopt);
                    break;
            }
            //
            // Rule 292:  LoopIndex ::= Modifiersopt LoopIndexDeclarator
            //
            case 292: {
               //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1684 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1684 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(2);
                //#line 1686 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex0(Modifiersopt,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 293:  LoopIndex ::= Modifiersopt VarKeyword LoopIndexDeclarator
            //
            case 293: {
               //#line 1690 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1688 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1688 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1688 "x10/parser/x10.g"
                Object LoopIndexDeclarator = (Object) getRhsSym(3);
                //#line 1690 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LoopIndex1(Modifiersopt,VarKeyword,LoopIndexDeclarator);
                    break;
            }
            //
            // Rule 294:  FormalParameter ::= Modifiersopt FormalDeclarator
            //
            case 294: {
               //#line 1695 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1693 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1693 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(2);
                //#line 1695 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter0(Modifiersopt,FormalDeclarator);
                    break;
            }
            //
            // Rule 295:  FormalParameter ::= Modifiersopt VarKeyword FormalDeclarator
            //
            case 295: {
               //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1697 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 1697 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 1697 "x10/parser/x10.g"
                Object FormalDeclarator = (Object) getRhsSym(3);
                //#line 1699 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter1(Modifiersopt,VarKeyword,FormalDeclarator);
                    break;
            }
            //
            // Rule 296:  FormalParameter ::= Type
            //
            case 296: {
               //#line 1703 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1701 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(1);
                //#line 1703 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameter2(Type);
                    break;
            }
            //
            // Rule 297:  Offers ::= offers Type
            //
            case 297: {
               //#line 1841 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1839 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 1841 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offers0(Type);
                    break;
            }
            //
            // Rule 298:  MethodBody ::= = LastExpression ;
            //
            case 298: {
               //#line 1847 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1845 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(2);
                //#line 1847 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody0(LastExpression);
                    break;
            }
            //
            // Rule 299:  MethodBody ::= = Annotationsopt { BlockStatementsopt LastExpression }
            //
            case 299: {
               //#line 1851 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1849 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1849 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(4);
                //#line 1849 "x10/parser/x10.g"
                Object LastExpression = (Object) getRhsSym(5);
                //#line 1851 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody1(Annotationsopt,BlockStatementsopt,LastExpression);
                    break;
            }
            //
            // Rule 300:  MethodBody ::= = Annotationsopt Block
            //
            case 300: {
               //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1853 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(2);
                //#line 1853 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(3);
                //#line 1855 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodBody2(Annotationsopt,Block);
                    break;
            }
            //
            // Rule 301:  MethodBody ::= Annotationsopt Block
            //
            case 301: {
               //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1857 "x10/parser/x10.g"
                Object Annotationsopt = (Object) getRhsSym(1);
                //#line 1857 "x10/parser/x10.g"
                Object Block = (Object) getRhsSym(2);
                //#line 1859 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1929 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1927 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(2);
                //#line 1929 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody0(ConstructorBlock);
                    break;
            }
            //
            // Rule 304:  ConstructorBody ::= ConstructorBlock
            //
            case 304: {
               //#line 1933 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1931 "x10/parser/x10.g"
                Object ConstructorBlock = (Object) getRhsSym(1);
                //#line 1933 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody1(ConstructorBlock);
                    break;
            }
            //
            // Rule 305:  ConstructorBody ::= = ExplicitConstructorInvocation
            //
            case 305: {
               //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1935 "x10/parser/x10.g"
                Object ExplicitConstructorInvocation = (Object) getRhsSym(2);
                //#line 1937 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBody2(ExplicitConstructorInvocation);
                    break;
            }
            //
            // Rule 306:  ConstructorBody ::= = AssignPropertyCall
            //
            case 306: {
               //#line 1941 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1939 "x10/parser/x10.g"
                Object AssignPropertyCall = (Object) getRhsSym(2);
                //#line 1941 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 1948 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1946 "x10/parser/x10.g"
                Object ExplicitConstructorInvocationopt = (Object) getRhsSym(2);
                //#line 1946 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(3);
                //#line 1948 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConstructorBlock0(ExplicitConstructorInvocationopt,BlockStatementsopt);
                    break;
            }
            //
            // Rule 309:  Arguments ::= ( ArgumentListopt )
            //
            case 309: {
               //#line 1953 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 1951 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 1953 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Arguments0(ArgumentListopt);
                    break;
            }
            //
            // Rule 311:  ExtendsInterfaces ::= extends Type
            //
            case 311: {
               //#line 2009 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2007 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(2);
                //#line 2009 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces0(Type);
                    break;
            }
            //
            // Rule 312:  ExtendsInterfaces ::= ExtendsInterfaces , Type
            //
            case 312: {
               //#line 2013 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2011 "x10/parser/x10.g"
                Object ExtendsInterfaces = (Object) getRhsSym(1);
                //#line 2011 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2013 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExtendsInterfaces1(ExtendsInterfaces,Type);
                    break;
            }
            //
            // Rule 313:  InterfaceBody ::= { InterfaceMemberDeclarationsopt }
            //
            case 313: {
               //#line 2021 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2019 "x10/parser/x10.g"
                Object InterfaceMemberDeclarationsopt = (Object) getRhsSym(2);
                //#line 2021 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceBody0(InterfaceMemberDeclarationsopt);
                    break;
            }
            //
            // Rule 315:  InterfaceMemberDeclarations ::= InterfaceMemberDeclarations InterfaceMemberDeclaration
            //
            case 315: {
               //#line 2027 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2025 "x10/parser/x10.g"
                Object InterfaceMemberDeclarations = (Object) getRhsSym(1);
                //#line 2025 "x10/parser/x10.g"
                Object InterfaceMemberDeclaration = (Object) getRhsSym(2);
                //#line 2027 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarations1(InterfaceMemberDeclarations,InterfaceMemberDeclaration);
                    break;
            }
            //
            // Rule 316:  InterfaceMemberDeclaration ::= MethodDeclaration
            //
            case 316: {
               //#line 2032 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2030 "x10/parser/x10.g"
                Object MethodDeclaration = (Object) getRhsSym(1);
                //#line 2032 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration0(MethodDeclaration);
                    break;
            }
            //
            // Rule 317:  InterfaceMemberDeclaration ::= PropertyMethodDeclaration
            //
            case 317: {
               //#line 2036 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2034 "x10/parser/x10.g"
                Object PropertyMethodDeclaration = (Object) getRhsSym(1);
                //#line 2036 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration1(PropertyMethodDeclaration);
                    break;
            }
            //
            // Rule 318:  InterfaceMemberDeclaration ::= FieldDeclaration
            //
            case 318: {
               //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2038 "x10/parser/x10.g"
                Object FieldDeclaration = (Object) getRhsSym(1);
                //#line 2040 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration2(FieldDeclaration);
                    break;
            }
            //
            // Rule 319:  InterfaceMemberDeclaration ::= ClassDeclaration
            //
            case 319: {
               //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2042 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2044 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration3(ClassDeclaration);
                    break;
            }
            //
            // Rule 320:  InterfaceMemberDeclaration ::= InterfaceDeclaration
            //
            case 320: {
               //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2046 "x10/parser/x10.g"
                Object InterfaceDeclaration = (Object) getRhsSym(1);
                //#line 2048 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration4(InterfaceDeclaration);
                    break;
            }
            //
            // Rule 321:  InterfaceMemberDeclaration ::= TypeDefDeclaration
            //
            case 321: {
               //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2050 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2052 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration5(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 322:  InterfaceMemberDeclaration ::= ;
            //
            case 322: {
               //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2056 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclaration6();
                    break;
            }
            //
            // Rule 323:  Annotations ::= Annotation
            //
            case 323: {
               //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2059 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(1);
                //#line 2061 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations0(Annotation);
                    break;
            }
            //
            // Rule 324:  Annotations ::= Annotations Annotation
            //
            case 324: {
               //#line 2065 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2063 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2063 "x10/parser/x10.g"
                Object Annotation = (Object) getRhsSym(2);
                //#line 2065 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotations1(Annotations,Annotation);
                    break;
            }
            //
            // Rule 325:  Annotation ::= @ NamedType
            //
            case 325: {
               //#line 2070 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2068 "x10/parser/x10.g"
                Object NamedType = (Object) getRhsSym(2);
                //#line 2070 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotation0(NamedType);
                    break;
            }
            //
            // Rule 326:  Identifier ::= IDENTIFIER$ident
            //
            case 326: {
               //#line 2084 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2082 "x10/parser/x10.g"
                IToken ident = (IToken) getRhsIToken(1);
                //#line 2084 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifier0();
                    break;
            }
            //
            // Rule 327:  Block ::= { BlockStatementsopt }
            //
            case 327: {
               //#line 2119 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2117 "x10/parser/x10.g"
                Object BlockStatementsopt = (Object) getRhsSym(2);
                //#line 2119 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Block0(BlockStatementsopt);
                    break;
            }
            //
            // Rule 328:  BlockStatements ::= BlockStatement
            //
            case 328: {
               //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2122 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(1);
                //#line 2124 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements0(BlockStatement);
                    break;
            }
            //
            // Rule 329:  BlockStatements ::= BlockStatements BlockStatement
            //
            case 329: {
               //#line 2128 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2126 "x10/parser/x10.g"
                Object BlockStatements = (Object) getRhsSym(1);
                //#line 2126 "x10/parser/x10.g"
                Object BlockStatement = (Object) getRhsSym(2);
                //#line 2128 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatements1(BlockStatements,BlockStatement);
                    break;
            }
            //
            // Rule 331:  BlockStatement ::= ClassDeclaration
            //
            case 331: {
               //#line 2134 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2132 "x10/parser/x10.g"
                Object ClassDeclaration = (Object) getRhsSym(1);
                //#line 2134 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement1(ClassDeclaration);
                    break;
            }
            //
            // Rule 332:  BlockStatement ::= TypeDefDeclaration
            //
            case 332: {
               //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2136 "x10/parser/x10.g"
                Object TypeDefDeclaration = (Object) getRhsSym(1);
                //#line 2138 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement2(TypeDefDeclaration);
                    break;
            }
            //
            // Rule 333:  BlockStatement ::= Statement
            //
            case 333: {
               //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2140 "x10/parser/x10.g"
                Object Statement = (Object) getRhsSym(1);
                //#line 2142 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BlockStatement3(Statement);
                    break;
            }
            //
            // Rule 334:  IdentifierList ::= Identifier
            //
            case 334: {
               //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2145 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2147 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList0(Identifier);
                    break;
            }
            //
            // Rule 335:  IdentifierList ::= IdentifierList , Identifier
            //
            case 335: {
               //#line 2151 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2149 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(1);
                //#line 2149 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2151 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_IdentifierList1(IdentifierList,Identifier);
                    break;
            }
            //
            // Rule 336:  FormalDeclarator ::= Identifier ResultType
            //
            case 336: {
               //#line 2156 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2154 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2154 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(2);
                //#line 2156 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator0(Identifier,ResultType);
                    break;
            }
            //
            // Rule 337:  FormalDeclarator ::= [ IdentifierList ] ResultType
            //
            case 337: {
               //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2158 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2158 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(4);
                //#line 2160 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator1(IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 338:  FormalDeclarator ::= Identifier [ IdentifierList ] ResultType
            //
            case 338: {
               //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2162 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2162 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2162 "x10/parser/x10.g"
                Object ResultType = (Object) getRhsSym(5);
                //#line 2164 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalDeclarator2(Identifier,IdentifierList,ResultType);
                    break;
            }
            //
            // Rule 339:  FieldDeclarator ::= Identifier HasResultType
            //
            case 339: {
               //#line 2169 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2167 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2167 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2169 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator0(Identifier,HasResultType);
                    break;
            }
            //
            // Rule 340:  FieldDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 340: {
               //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2171 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2171 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2171 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2173 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldDeclarator1(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 341:  VariableDeclarator ::= Identifier HasResultTypeopt = VariableInitializer
            //
            case 341: {
               //#line 2178 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2176 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2176 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(2);
                //#line 2176 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2178 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator0(Identifier,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 342:  VariableDeclarator ::= [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 342: {
               //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2180 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2180 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(4);
                //#line 2180 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2182 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator1(IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 343:  VariableDeclarator ::= Identifier [ IdentifierList ] HasResultTypeopt = VariableInitializer
            //
            case 343: {
               //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2184 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2184 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2184 "x10/parser/x10.g"
                Object HasResultTypeopt = (Object) getRhsSym(5);
                //#line 2184 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2186 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclarator2(Identifier,IdentifierList,HasResultTypeopt,VariableInitializer);
                    break;
            }
            //
            // Rule 344:  VariableDeclaratorWithType ::= Identifier HasResultType = VariableInitializer
            //
            case 344: {
               //#line 2191 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2189 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2189 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(2);
                //#line 2189 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(4);
                //#line 2191 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType0(Identifier,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 345:  VariableDeclaratorWithType ::= [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 345: {
               //#line 2195 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2193 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(2);
                //#line 2193 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(4);
                //#line 2193 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(6);
                //#line 2195 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType1(IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 346:  VariableDeclaratorWithType ::= Identifier [ IdentifierList ] HasResultType = VariableInitializer
            //
            case 346: {
               //#line 2199 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2197 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2197 "x10/parser/x10.g"
                Object IdentifierList = (Object) getRhsSym(3);
                //#line 2197 "x10/parser/x10.g"
                Object HasResultType = (Object) getRhsSym(5);
                //#line 2197 "x10/parser/x10.g"
                Object VariableInitializer = (Object) getRhsSym(7);
                //#line 2199 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_VariableDeclaratorWithType2(Identifier,IdentifierList,HasResultType,VariableInitializer);
                    break;
            }
            //
            // Rule 348:  LocalVariableDeclaration ::= Modifiersopt VarKeyword VariableDeclarators
            //
            case 348: {
               //#line 2206 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2204 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2204 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2204 "x10/parser/x10.g"
                Object VariableDeclarators = (Object) getRhsSym(3);
                //#line 2206 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration0(Modifiersopt,VarKeyword,VariableDeclarators);
    
                    break;
            }
            //
            // Rule 349:  LocalVariableDeclaration ::= Modifiersopt VariableDeclaratorsWithType
            //
            case 349: {
               //#line 2211 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2209 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2209 "x10/parser/x10.g"
                Object VariableDeclaratorsWithType = (Object) getRhsSym(2);
                //#line 2211 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration1(Modifiersopt,VariableDeclaratorsWithType);
    
                    break;
            }
            //
            // Rule 350:  LocalVariableDeclaration ::= Modifiersopt VarKeyword FormalDeclarators
            //
            case 350: {
               //#line 2216 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2214 "x10/parser/x10.g"
                Object Modifiersopt = (Object) getRhsSym(1);
                //#line 2214 "x10/parser/x10.g"
                Object VarKeyword = (Object) getRhsSym(2);
                //#line 2214 "x10/parser/x10.g"
                Object FormalDeclarators = (Object) getRhsSym(3);
                //#line 2216 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LocalVariableDeclaration2(Modifiersopt,VarKeyword,FormalDeclarators);
    
                    break;
            }
            //
            // Rule 351:  Primary ::= here
            //
            case 351: {
               //#line 2227 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2227 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary0();
                    break;
            }
            //
            // Rule 352:  Primary ::= [ ArgumentListopt ]
            //
            case 352: {
               //#line 2231 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2229 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(2);
                //#line 2231 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary1(ArgumentListopt);
                    break;
            }
            //
            // Rule 354:  Primary ::= self
            //
            case 354: {
               //#line 2237 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2237 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary3();
                    break;
            }
            //
            // Rule 355:  Primary ::= this
            //
            case 355: {
               //#line 2241 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2241 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary4();
                    break;
            }
            //
            // Rule 356:  Primary ::= ClassName . this
            //
            case 356: {
               //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2243 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2245 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary5(ClassName);
                    break;
            }
            //
            // Rule 357:  Primary ::= ( Expression )
            //
            case 357: {
               //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2247 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(2);
                //#line 2249 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Primary6(Expression);
                    break;
            }
            //
            // Rule 363:  OperatorFunction ::= TypeName . +
            //
            case 363: {
               //#line 2259 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2257 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2259 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction0(TypeName);
                    break;
            }
            //
            // Rule 364:  OperatorFunction ::= TypeName . -
            //
            case 364: {
               //#line 2263 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2261 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2263 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction1(TypeName);
                    break;
            }
            //
            // Rule 365:  OperatorFunction ::= TypeName . *
            //
            case 365: {
               //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2265 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2267 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction2(TypeName);
                    break;
            }
            //
            // Rule 366:  OperatorFunction ::= TypeName . /
            //
            case 366: {
               //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2269 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2271 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction3(TypeName);
                    break;
            }
            //
            // Rule 367:  OperatorFunction ::= TypeName . %
            //
            case 367: {
               //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2273 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2275 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction4(TypeName);
                    break;
            }
            //
            // Rule 368:  OperatorFunction ::= TypeName . &
            //
            case 368: {
               //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2277 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2279 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction5(TypeName);
                    break;
            }
            //
            // Rule 369:  OperatorFunction ::= TypeName . |
            //
            case 369: {
               //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2281 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2283 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction6(TypeName);
                    break;
            }
            //
            // Rule 370:  OperatorFunction ::= TypeName . ^
            //
            case 370: {
               //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2285 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2287 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction7(TypeName);
                    break;
            }
            //
            // Rule 371:  OperatorFunction ::= TypeName . <<
            //
            case 371: {
               //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2289 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2291 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction8(TypeName);
                    break;
            }
            //
            // Rule 372:  OperatorFunction ::= TypeName . >>
            //
            case 372: {
               //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2293 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2295 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction9(TypeName);
                    break;
            }
            //
            // Rule 373:  OperatorFunction ::= TypeName . >>>
            //
            case 373: {
               //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2297 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2299 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction10(TypeName);
                    break;
            }
            //
            // Rule 374:  OperatorFunction ::= TypeName . <
            //
            case 374: {
               //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2301 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2303 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction11(TypeName);
                    break;
            }
            //
            // Rule 375:  OperatorFunction ::= TypeName . <=
            //
            case 375: {
               //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2305 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2307 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction12(TypeName);
                    break;
            }
            //
            // Rule 376:  OperatorFunction ::= TypeName . >=
            //
            case 376: {
               //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2309 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2311 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction13(TypeName);
                    break;
            }
            //
            // Rule 377:  OperatorFunction ::= TypeName . >
            //
            case 377: {
               //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2313 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2315 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction14(TypeName);
                    break;
            }
            //
            // Rule 378:  OperatorFunction ::= TypeName . ==
            //
            case 378: {
               //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2317 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2319 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction15(TypeName);
                    break;
            }
            //
            // Rule 379:  OperatorFunction ::= TypeName . !=
            //
            case 379: {
               //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2321 "x10/parser/x10.g"
                Object TypeName = (Object) getRhsSym(1);
                //#line 2323 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_OperatorFunction16(TypeName);
                    break;
            }
            //
            // Rule 380:  Literal ::= IntegerLiteral$lit
            //
            case 380: {
               //#line 2328 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2326 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2328 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal0();
                    break;
            }
            //
            // Rule 381:  Literal ::= LongLiteral$lit
            //
            case 381: {
               //#line 2332 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2330 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2332 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal1();
                    break;
            }
            //
            // Rule 382:  Literal ::= ByteLiteral
            //
            case 382: {
               //#line 2336 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2336 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralByte();
                    break;
            }
            //
            // Rule 383:  Literal ::= UnsignedByteLiteral
            //
            case 383: {
               //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2340 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUByte();
                    break;
            }
            //
            // Rule 384:  Literal ::= ShortLiteral
            //
            case 384: {
               //#line 2344 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2344 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralShort();
                    break;
            }
            //
            // Rule 385:  Literal ::= UnsignedShortLiteral
            //
            case 385: {
               //#line 2348 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2348 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LiteralUShort();
                    break;
            }
            //
            // Rule 386:  Literal ::= UnsignedIntegerLiteral$lit
            //
            case 386: {
               //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2350 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2352 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal2();
                    break;
            }
            //
            // Rule 387:  Literal ::= UnsignedLongLiteral$lit
            //
            case 387: {
               //#line 2356 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2354 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2356 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal3();
                    break;
            }
            //
            // Rule 388:  Literal ::= FloatingPointLiteral$lit
            //
            case 388: {
               //#line 2360 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2358 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2360 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal4();
                    break;
            }
            //
            // Rule 389:  Literal ::= DoubleLiteral$lit
            //
            case 389: {
               //#line 2364 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2362 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2364 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal5();
                    break;
            }
            //
            // Rule 390:  Literal ::= BooleanLiteral
            //
            case 390: {
               //#line 2368 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2366 "x10/parser/x10.g"
                Object BooleanLiteral = (Object) getRhsSym(1);
                //#line 2368 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal6(BooleanLiteral);
                    break;
            }
            //
            // Rule 391:  Literal ::= CharacterLiteral$lit
            //
            case 391: {
               //#line 2372 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2370 "x10/parser/x10.g"
                IToken lit = (IToken) getRhsIToken(1);
                //#line 2372 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal7();
                    break;
            }
            //
            // Rule 392:  Literal ::= StringLiteral$str
            //
            case 392: {
               //#line 2376 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2374 "x10/parser/x10.g"
                IToken str = (IToken) getRhsIToken(1);
                //#line 2376 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal8();
                    break;
            }
            //
            // Rule 393:  Literal ::= null
            //
            case 393: {
               //#line 2380 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2380 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Literal9();
                    break;
            }
            //
            // Rule 394:  BooleanLiteral ::= true$trueLiteral
            //
            case 394: {
               //#line 2385 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2383 "x10/parser/x10.g"
                IToken trueLiteral = (IToken) getRhsIToken(1);
                //#line 2385 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral0();
                    break;
            }
            //
            // Rule 395:  BooleanLiteral ::= false$falseLiteral
            //
            case 395: {
               //#line 2389 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2387 "x10/parser/x10.g"
                IToken falseLiteral = (IToken) getRhsIToken(1);
                //#line 2389 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BooleanLiteral1();
                    break;
            }
            //
            // Rule 396:  ArgumentList ::= Expression
            //
            case 396: {
               //#line 2397 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2395 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(1);
                //#line 2397 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList0(Expression);
                    break;
            }
            //
            // Rule 397:  ArgumentList ::= ArgumentList , Expression
            //
            case 397: {
               //#line 2401 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2399 "x10/parser/x10.g"
                Object ArgumentList = (Object) getRhsSym(1);
                //#line 2399 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2401 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentList1(ArgumentList,Expression);
                    break;
            }
            //
            // Rule 398:  FieldAccess ::= Primary . Identifier
            //
            case 398: {
               //#line 2406 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2404 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2404 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2406 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess3(Primary,Identifier);
                    break;
            }
            //
            // Rule 399:  FieldAccess ::= super . Identifier
            //
            case 399: {
               //#line 2410 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2408 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2410 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess4(Identifier);
                    break;
            }
            //
            // Rule 400:  FieldAccess ::= ClassName . super$sup . Identifier
            //
            case 400: {
               //#line 2414 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2412 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2412 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2412 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2414 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess5(ClassName,Identifier);
                    break;
            }
            //
            // Rule 401:  FieldAccess ::= Primary . class$c
            //
            case 401: {
               //#line 2418 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2416 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2416 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2418 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess6(Primary);
                    break;
            }
            //
            // Rule 402:  FieldAccess ::= super . class$c
            //
            case 402: {
               //#line 2422 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2420 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(3);
                //#line 2422 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess7();
                    break;
            }
            //
            // Rule 403:  FieldAccess ::= ClassName . super$sup . class$c
            //
            case 403: {
               //#line 2426 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2424 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2424 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2424 "x10/parser/x10.g"
                IToken c = (IToken) getRhsIToken(5);
                //#line 2426 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FieldAccess8(ClassName);
                    break;
            }
            //
            // Rule 404:  MethodInvocation ::= MethodName TypeArgumentsopt ( ArgumentListopt )
            //
            case 404: {
               //#line 2431 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2429 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2429 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2429 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2431 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation3(MethodName,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 405:  MethodInvocation ::= Primary . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 405: {
               //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2433 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2433 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2433 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2433 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2435 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation4(Primary,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 406:  MethodInvocation ::= super . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 406: {
               //#line 2439 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2437 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2437 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(4);
                //#line 2437 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(6);
                //#line 2439 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation5(Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 407:  MethodInvocation ::= ClassName . super$sup . Identifier TypeArgumentsopt ( ArgumentListopt )
            //
            case 407: {
               //#line 2443 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2441 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2441 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2441 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2441 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(6);
                //#line 2441 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(8);
                //#line 2443 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation6(ClassName,Identifier,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 408:  MethodInvocation ::= Primary TypeArgumentsopt ( ArgumentListopt )
            //
            case 408: {
               //#line 2447 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2445 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2445 "x10/parser/x10.g"
                Object TypeArgumentsopt = (Object) getRhsSym(2);
                //#line 2445 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(4);
                //#line 2447 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodInvocation7(Primary,TypeArgumentsopt,ArgumentListopt);
                    break;
            }
            //
            // Rule 409:  MethodSelection ::= MethodName . ( FormalParameterListopt )
            //
            case 409: {
               //#line 2452 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2450 "x10/parser/x10.g"
                Object MethodName = (Object) getRhsSym(1);
                //#line 2450 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(4);
                //#line 2452 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection0(MethodName,FormalParameterListopt);
                    break;
            }
            //
            // Rule 410:  MethodSelection ::= Primary . Identifier . ( FormalParameterListopt )
            //
            case 410: {
               //#line 2456 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2454 "x10/parser/x10.g"
                Object Primary = (Object) getRhsSym(1);
                //#line 2454 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2454 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2456 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection1(Primary,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 411:  MethodSelection ::= super . Identifier . ( FormalParameterListopt )
            //
            case 411: {
               //#line 2460 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2458 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(3);
                //#line 2458 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(6);
                //#line 2460 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection2(Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 412:  MethodSelection ::= ClassName . super$sup . Identifier . ( FormalParameterListopt )
            //
            case 412: {
               //#line 2464 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2462 "x10/parser/x10.g"
                Object ClassName = (Object) getRhsSym(1);
                //#line 2462 "x10/parser/x10.g"
                IToken sup = (IToken) getRhsIToken(3);
                //#line 2462 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(5);
                //#line 2462 "x10/parser/x10.g"
                Object FormalParameterListopt = (Object) getRhsSym(8);
                //#line 2464 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MethodSelection3(ClassName,Identifier,FormalParameterListopt);
                    break;
            }
            //
            // Rule 416:  PostIncrementExpression ::= PostfixExpression ++
            //
            case 416: {
               //#line 2473 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2471 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2473 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostIncrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 417:  PostDecrementExpression ::= PostfixExpression --
            //
            case 417: {
               //#line 2478 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2476 "x10/parser/x10.g"
                Object PostfixExpression = (Object) getRhsSym(1);
                //#line 2478 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PostDecrementExpression0(PostfixExpression);
                    break;
            }
            //
            // Rule 420:  UnannotatedUnaryExpression ::= + UnaryExpressionNotPlusMinus
            //
            case 420: {
               //#line 2485 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2483 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2485 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression2(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 421:  UnannotatedUnaryExpression ::= - UnaryExpressionNotPlusMinus
            //
            case 421: {
               //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2487 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2489 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnannotatedUnaryExpression3(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 424:  UnaryExpression ::= Annotations UnannotatedUnaryExpression
            //
            case 424: {
               //#line 2496 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2494 "x10/parser/x10.g"
                Object Annotations = (Object) getRhsSym(1);
                //#line 2494 "x10/parser/x10.g"
                Object UnannotatedUnaryExpression = (Object) getRhsSym(2);
                //#line 2496 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpression1(Annotations,UnannotatedUnaryExpression);
                    break;
            }
            //
            // Rule 425:  PreIncrementExpression ::= ++ UnaryExpressionNotPlusMinus
            //
            case 425: {
               //#line 2501 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2499 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2501 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreIncrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 426:  PreDecrementExpression ::= -- UnaryExpressionNotPlusMinus
            //
            case 426: {
               //#line 2506 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2504 "x10/parser/x10.g"
                Object UnaryExpressionNotPlusMinus = (Object) getRhsSym(2);
                //#line 2506 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PreDecrementExpression0(UnaryExpressionNotPlusMinus);
                    break;
            }
            //
            // Rule 428:  UnaryExpressionNotPlusMinus ::= ~ UnaryExpression
            //
            case 428: {
               //#line 2512 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2510 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2512 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus1(UnaryExpression);
                    break;
            }
            //
            // Rule 429:  UnaryExpressionNotPlusMinus ::= ! UnaryExpression
            //
            case 429: {
               //#line 2516 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2514 "x10/parser/x10.g"
                Object UnaryExpression = (Object) getRhsSym(2);
                //#line 2516 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_UnaryExpressionNotPlusMinus2(UnaryExpression);
                    break;
            }
            //
            // Rule 431:  RangeExpression ::= RangeExpression$expr1 .. UnaryExpression$expr2
            //
            case 431: {
               //#line 2522 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2520 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2520 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2522 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RangeExpression1(expr1,expr2);
                    break;
            }
            //
            // Rule 433:  MultiplicativeExpression ::= MultiplicativeExpression * RangeExpression
            //
            case 433: {
               //#line 2528 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2526 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2526 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2528 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression1(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 434:  MultiplicativeExpression ::= MultiplicativeExpression / RangeExpression
            //
            case 434: {
               //#line 2532 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2530 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2530 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2532 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression2(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 435:  MultiplicativeExpression ::= MultiplicativeExpression % RangeExpression
            //
            case 435: {
               //#line 2536 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2534 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(1);
                //#line 2534 "x10/parser/x10.g"
                Object RangeExpression = (Object) getRhsSym(3);
                //#line 2536 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_MultiplicativeExpression3(MultiplicativeExpression,RangeExpression);
                    break;
            }
            //
            // Rule 437:  AdditiveExpression ::= AdditiveExpression + MultiplicativeExpression
            //
            case 437: {
               //#line 2542 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2540 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2540 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2542 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression1(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 438:  AdditiveExpression ::= AdditiveExpression - MultiplicativeExpression
            //
            case 438: {
               //#line 2546 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2544 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(1);
                //#line 2544 "x10/parser/x10.g"
                Object MultiplicativeExpression = (Object) getRhsSym(3);
                //#line 2546 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AdditiveExpression2(AdditiveExpression,MultiplicativeExpression);
                    break;
            }
            //
            // Rule 440:  ShiftExpression ::= ShiftExpression << AdditiveExpression
            //
            case 440: {
               //#line 2552 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2550 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2550 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2552 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression1(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 441:  ShiftExpression ::= ShiftExpression >> AdditiveExpression
            //
            case 441: {
               //#line 2556 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2554 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2554 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2556 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression2(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 442:  ShiftExpression ::= ShiftExpression >>> AdditiveExpression
            //
            case 442: {
               //#line 2560 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2558 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(1);
                //#line 2558 "x10/parser/x10.g"
                Object AdditiveExpression = (Object) getRhsSym(3);
                //#line 2560 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression3(ShiftExpression,AdditiveExpression);
                    break;
            }
            //
            // Rule 443:  ShiftExpression ::= ShiftExpression$expr1 -> AdditiveExpression$expr2
            //
            case 443: {
               //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2562 "x10/parser/x10.g"
                Object expr1 = (Object) getRhsSym(1);
                //#line 2562 "x10/parser/x10.g"
                Object expr2 = (Object) getRhsSym(3);
                //#line 2564 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ShiftExpression4(expr1,expr2);
                    break;
            }
            //
            // Rule 447:  RelationalExpression ::= RelationalExpression < ShiftExpression
            //
            case 447: {
               //#line 2572 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2570 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2570 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2572 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression3(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 448:  RelationalExpression ::= RelationalExpression > ShiftExpression
            //
            case 448: {
               //#line 2576 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2574 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2574 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2576 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression4(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 449:  RelationalExpression ::= RelationalExpression <= ShiftExpression
            //
            case 449: {
               //#line 2580 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2578 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2578 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2580 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression5(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 450:  RelationalExpression ::= RelationalExpression >= ShiftExpression
            //
            case 450: {
               //#line 2584 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2582 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2582 "x10/parser/x10.g"
                Object ShiftExpression = (Object) getRhsSym(3);
                //#line 2584 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression6(RelationalExpression,ShiftExpression);
                    break;
            }
            //
            // Rule 451:  RelationalExpression ::= RelationalExpression instanceof Type
            //
            case 451: {
               //#line 2588 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2586 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(1);
                //#line 2586 "x10/parser/x10.g"
                Object Type = (Object) getRhsSym(3);
                //#line 2588 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_RelationalExpression7(RelationalExpression,Type);
                    break;
            }
            //
            // Rule 453:  EqualityExpression ::= EqualityExpression == RelationalExpression
            //
            case 453: {
               //#line 2594 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2592 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2592 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2594 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression1(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 454:  EqualityExpression ::= EqualityExpression != RelationalExpression
            //
            case 454: {
               //#line 2598 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2596 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(1);
                //#line 2596 "x10/parser/x10.g"
                Object RelationalExpression = (Object) getRhsSym(3);
                //#line 2598 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression2(EqualityExpression,RelationalExpression);
                    break;
            }
            //
            // Rule 455:  EqualityExpression ::= Type$t1 == Type$t2
            //
            case 455: {
               //#line 2602 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2600 "x10/parser/x10.g"
                Object t1 = (Object) getRhsSym(1);
                //#line 2600 "x10/parser/x10.g"
                Object t2 = (Object) getRhsSym(3);
                //#line 2602 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_EqualityExpression3(t1,t2);
                    break;
            }
            //
            // Rule 457:  AndExpression ::= AndExpression & EqualityExpression
            //
            case 457: {
               //#line 2608 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2606 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(1);
                //#line 2606 "x10/parser/x10.g"
                Object EqualityExpression = (Object) getRhsSym(3);
                //#line 2608 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AndExpression1(AndExpression,EqualityExpression);
                    break;
            }
            //
            // Rule 459:  ExclusiveOrExpression ::= ExclusiveOrExpression ^ AndExpression
            //
            case 459: {
               //#line 2614 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2612 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2612 "x10/parser/x10.g"
                Object AndExpression = (Object) getRhsSym(3);
                //#line 2614 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ExclusiveOrExpression1(ExclusiveOrExpression,AndExpression);
                    break;
            }
            //
            // Rule 461:  InclusiveOrExpression ::= InclusiveOrExpression | ExclusiveOrExpression
            //
            case 461: {
               //#line 2620 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2618 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(1);
                //#line 2618 "x10/parser/x10.g"
                Object ExclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2620 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InclusiveOrExpression1(InclusiveOrExpression,ExclusiveOrExpression);
                    break;
            }
            //
            // Rule 463:  ConditionalAndExpression ::= ConditionalAndExpression && InclusiveOrExpression
            //
            case 463: {
               //#line 2626 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2624 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(1);
                //#line 2624 "x10/parser/x10.g"
                Object InclusiveOrExpression = (Object) getRhsSym(3);
                //#line 2626 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalAndExpression1(ConditionalAndExpression,InclusiveOrExpression);
                    break;
            }
            //
            // Rule 465:  ConditionalOrExpression ::= ConditionalOrExpression || ConditionalAndExpression
            //
            case 465: {
               //#line 2632 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2630 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2630 "x10/parser/x10.g"
                Object ConditionalAndExpression = (Object) getRhsSym(3);
                //#line 2632 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalOrExpression1(ConditionalOrExpression,ConditionalAndExpression);
                    break;
            }
            //
            // Rule 470:  ConditionalExpression ::= ConditionalOrExpression ? Expression : ConditionalExpression
            //
            case 470: {
               //#line 2642 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2640 "x10/parser/x10.g"
                Object ConditionalOrExpression = (Object) getRhsSym(1);
                //#line 2640 "x10/parser/x10.g"
                Object Expression = (Object) getRhsSym(3);
                //#line 2640 "x10/parser/x10.g"
                Object ConditionalExpression = (Object) getRhsSym(5);
                //#line 2642 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ConditionalExpression4(ConditionalOrExpression,Expression,ConditionalExpression);
                    break;
            }
            //
            // Rule 473:  Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression
            //
            case 473: {
               //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2648 "x10/parser/x10.g"
                Object LeftHandSide = (Object) getRhsSym(1);
                //#line 2648 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(2);
                //#line 2648 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(3);
                //#line 2650 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment0(LeftHandSide,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 474:  Assignment ::= ExpressionName$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 474: {
               //#line 2654 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2652 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2652 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2652 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2652 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2654 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment1(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 475:  Assignment ::= Primary$e1 ( ArgumentListopt ) AssignmentOperator AssignmentExpression
            //
            case 475: {
               //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2656 "x10/parser/x10.g"
                Object e1 = (Object) getRhsSym(1);
                //#line 2656 "x10/parser/x10.g"
                Object ArgumentListopt = (Object) getRhsSym(3);
                //#line 2656 "x10/parser/x10.g"
                Object AssignmentOperator = (Object) getRhsSym(5);
                //#line 2656 "x10/parser/x10.g"
                Object AssignmentExpression = (Object) getRhsSym(6);
                //#line 2658 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Assignment2(e1,ArgumentListopt,AssignmentOperator,AssignmentExpression);
                    break;
            }
            //
            // Rule 476:  LeftHandSide ::= ExpressionName
            //
            case 476: {
               //#line 2663 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2661 "x10/parser/x10.g"
                Object ExpressionName = (Object) getRhsSym(1);
                //#line 2663 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_LeftHandSide0(ExpressionName);
                    break;
            }
            //
            // Rule 478:  AssignmentOperator ::= =
            //
            case 478: {
               //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2669 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator0();
                    break;
            }
            //
            // Rule 479:  AssignmentOperator ::= *=
            //
            case 479: {
               //#line 2673 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2673 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator1();
                    break;
            }
            //
            // Rule 480:  AssignmentOperator ::= /=
            //
            case 480: {
               //#line 2677 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2677 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator2();
                    break;
            }
            //
            // Rule 481:  AssignmentOperator ::= %=
            //
            case 481: {
               //#line 2681 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2681 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator3();
                    break;
            }
            //
            // Rule 482:  AssignmentOperator ::= +=
            //
            case 482: {
               //#line 2685 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2685 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator4();
                    break;
            }
            //
            // Rule 483:  AssignmentOperator ::= -=
            //
            case 483: {
               //#line 2689 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2689 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator5();
                    break;
            }
            //
            // Rule 484:  AssignmentOperator ::= <<=
            //
            case 484: {
               //#line 2693 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2693 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator6();
                    break;
            }
            //
            // Rule 485:  AssignmentOperator ::= >>=
            //
            case 485: {
               //#line 2697 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2697 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator7();
                    break;
            }
            //
            // Rule 486:  AssignmentOperator ::= >>>=
            //
            case 486: {
               //#line 2701 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2701 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator8();
                    break;
            }
            //
            // Rule 487:  AssignmentOperator ::= &=
            //
            case 487: {
               //#line 2705 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2705 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator9();
                    break;
            }
            //
            // Rule 488:  AssignmentOperator ::= ^=
            //
            case 488: {
               //#line 2709 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2709 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator10();
                    break;
            }
            //
            // Rule 489:  AssignmentOperator ::= |=
            //
            case 489: {
               //#line 2713 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2713 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_AssignmentOperator11();
                    break;
            }
            //
            // Rule 492:  PrefixOp ::= +
            //
            case 492: {
               //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2723 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp0();
                    break;
            }
            //
            // Rule 493:  PrefixOp ::= -
            //
            case 493: {
               //#line 2727 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2727 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp1();
                    break;
            }
            //
            // Rule 494:  PrefixOp ::= !
            //
            case 494: {
               //#line 2731 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2731 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp2();
                    break;
            }
            //
            // Rule 495:  PrefixOp ::= ~
            //
            case 495: {
               //#line 2735 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2735 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_PrefixOp3();
                    break;
            }
            //
            // Rule 496:  BinOp ::= +
            //
            case 496: {
               //#line 2740 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2740 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp0();
                    break;
            }
            //
            // Rule 497:  BinOp ::= -
            //
            case 497: {
               //#line 2744 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2744 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp1();
                    break;
            }
            //
            // Rule 498:  BinOp ::= *
            //
            case 498: {
               //#line 2748 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2748 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp2();
                    break;
            }
            //
            // Rule 499:  BinOp ::= /
            //
            case 499: {
               //#line 2752 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2752 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp3();
                    break;
            }
            //
            // Rule 500:  BinOp ::= %
            //
            case 500: {
               //#line 2756 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2756 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp4();
                    break;
            }
            //
            // Rule 501:  BinOp ::= &
            //
            case 501: {
               //#line 2760 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2760 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp5();
                    break;
            }
            //
            // Rule 502:  BinOp ::= |
            //
            case 502: {
               //#line 2764 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2764 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp6();
                    break;
            }
            //
            // Rule 503:  BinOp ::= ^
            //
            case 503: {
               //#line 2768 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2768 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp7();
                    break;
            }
            //
            // Rule 504:  BinOp ::= &&
            //
            case 504: {
               //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2772 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp8();
                    break;
            }
            //
            // Rule 505:  BinOp ::= ||
            //
            case 505: {
               //#line 2776 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2776 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp9();
                    break;
            }
            //
            // Rule 506:  BinOp ::= <<
            //
            case 506: {
               //#line 2780 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2780 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp10();
                    break;
            }
            //
            // Rule 507:  BinOp ::= >>
            //
            case 507: {
               //#line 2784 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2784 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp11();
                    break;
            }
            //
            // Rule 508:  BinOp ::= >>>
            //
            case 508: {
               //#line 2788 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2788 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp12();
                    break;
            }
            //
            // Rule 509:  BinOp ::= >=
            //
            case 509: {
               //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2792 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp13();
                    break;
            }
            //
            // Rule 510:  BinOp ::= <=
            //
            case 510: {
               //#line 2796 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2796 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp14();
                    break;
            }
            //
            // Rule 511:  BinOp ::= >
            //
            case 511: {
               //#line 2800 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2800 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp15();
                    break;
            }
            //
            // Rule 512:  BinOp ::= <
            //
            case 512: {
               //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2804 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp16();
                    break;
            }
            //
            // Rule 513:  BinOp ::= ==
            //
            case 513: {
               //#line 2811 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2811 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp17();
                    break;
            }
            //
            // Rule 514:  BinOp ::= !=
            //
            case 514: {
               //#line 2815 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2815 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp18();
                    break;
            }
            //
            // Rule 515:  BinOp ::= ..
            //
            case 515: {
               //#line 2820 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2820 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp19();
                    break;
            }
            //
            // Rule 516:  BinOp ::= ->
            //
            case 516: {
               //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2824 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_BinOp20();
                    break;
            }
            //
            // Rule 517:  Catchesopt ::= $Empty
            //
            case 517: {
               //#line 2832 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2832 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2840 "lpg.generator/templates/java/btParserTemplateF.gi"
                //#line 2838 "x10/parser/x10.g"
                Object Identifier = (Object) getRhsSym(1);
                //#line 2840 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Identifieropt1(Identifier);
                    break;
            }
            //
            // Rule 521:  ForUpdateopt ::= $Empty
            //
            case 521: {
               //#line 2845 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2845 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2855 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2855 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ForInitopt0();
                    break;
            }
            //
            // Rule 527:  SwitchLabelsopt ::= $Empty
            //
            case 527: {
               //#line 2861 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2861 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchLabelsopt0();
                    break;
            }
            //
            // Rule 529:  SwitchBlockStatementGroupsopt ::= $Empty
            //
            case 529: {
               //#line 2867 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2867 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_SwitchBlockStatementGroupsopt0();
                    break;
            }
            //
            // Rule 531:  InterfaceMemberDeclarationsopt ::= $Empty
            //
            case 531: {
               //#line 2890 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2890 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_InterfaceMemberDeclarationsopt0();
                    break;
            }
            //
            // Rule 533:  ExtendsInterfacesopt ::= $Empty
            //
            case 533: {
               //#line 2896 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2896 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2926 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2926 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ArgumentListopt0();
                    break;
            }
            //
            // Rule 539:  BlockStatementsopt ::= $Empty
            //
            case 539: {
               //#line 2932 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2932 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 2952 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2952 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParameterListopt0();
                    break;
            }
            //
            // Rule 545:  Offersopt ::= $Empty
            //
            case 545: {
               //#line 2964 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 2964 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Offersopt0();
                    break;
            }
            //
            // Rule 547:  ClassBodyDeclarationsopt ::= $Empty
            //
            case 547: {
               //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3000 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_ClassBodyDeclarationsopt0();
                    break;
            }
            //
            // Rule 549:  Interfacesopt ::= $Empty
            //
            case 549: {
               //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3006 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3016 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3016 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParametersopt0();
                    break;
            }
            //
            // Rule 555:  FormalParametersopt ::= $Empty
            //
            case 555: {
               //#line 3022 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3022 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_FormalParametersopt0();
                    break;
            }
            //
            // Rule 557:  Annotationsopt ::= $Empty
            //
            case 557: {
               //#line 3028 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3028 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Annotationsopt0();
                    break;
            }
            //
            // Rule 559:  TypeDeclarationsopt ::= $Empty
            //
            case 559: {
               //#line 3034 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3034 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeDeclarationsopt0();
                    break;
            }
            //
            // Rule 561:  ImportDeclarationsopt ::= $Empty
            //
            case 561: {
               //#line 3040 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3040 "lpg.generator/templates/java/btParserTemplateF.gi"
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
               //#line 3060 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3060 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeArgumentsopt0();
                    break;
            }
            //
            // Rule 569:  TypeParamsWithVarianceopt ::= $Empty
            //
            case 569: {
               //#line 3066 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3066 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_TypeParamsWithVarianceopt0();
                    break;
            }
            //
            // Rule 571:  Propertiesopt ::= $Empty
            //
            case 571: {
               //#line 3072 "lpg.generator/templates/java/btParserTemplateF.gi"
                
                //#line 3072 "lpg.generator/templates/java/btParserTemplateF.gi"
		r.rule_Propertiesopt0();
                    break;
            }
    
            default:
                break;
        }
        return;
    }
}

