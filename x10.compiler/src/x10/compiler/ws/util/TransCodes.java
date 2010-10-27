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


package x10.compiler.ws.util;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Stmt;

/**
 * Utility class to store transformed codes for three paths, fast/slow/back, and new pcValue
 * 
 * Three are three lists, first(), second(), third().
 * User could use it to store temporary statements.
 * And it contains an int pcValue. User could use increPcValue() to increase it.
 * 
 * @author Haichuan
 *
 */
public class TransCodes {
    
    int pcValue;

    List<Stmt> flattenedCodes; //used to store flattened codes
    
    Triple<List<Stmt>, List<Stmt>, List<Stmt>> codes;
    public TransCodes(){
        codes = new Triple<List<Stmt>, List<Stmt>, List<Stmt>>(
                new ArrayList<Stmt>(), new ArrayList<Stmt>(), new ArrayList<Stmt>());
    }
    
    /**
     * Initialize a TransCodes with as-is pcValue
     * @param pcValue
     */
    public TransCodes(int pcValue){
        this();
        this.pcValue = pcValue;
    }
    
    public void addFirst(List<Stmt> ss){
        codes.first().addAll(ss);
    }
    
    public void addFirst(Stmt s){
        codes.first().add(s);
    }

    public void addSecond(List<Stmt> ss){
        codes.second().addAll(ss);
    }
    
    public void addSecond(Stmt s){
        codes.second().add(s);
    }
    
    public void addThird(List<Stmt> ss){
        codes.third().addAll(ss);
    }
    
    public void addThird(Stmt s){
        codes.third().add(s);
    }
    
    public List<Stmt> first(){
        return codes.first();
    }
    
    public List<Stmt> second(){
        return codes.second();
    }
    
    public List<Stmt> third(){
        return codes.third();
    }

    public void increPcValue(){
        pcValue ++;
    }
    
    public int getPcValue() {
        return pcValue;
    }
    
    public void setPcValue(int pcValue) {
        this.pcValue = pcValue;
    }
    
    public void addFlattened(Stmt stmt){
        if(flattenedCodes == null){
            flattenedCodes = new ArrayList<Stmt>();
        }
        if(stmt instanceof Block){
            //unroll block
            flattenedCodes.addAll(unrollBlock((Block)stmt));
        }
        else{
            flattenedCodes.add(stmt);
        }
    }
    
    
    protected List<Stmt> unrollBlock(Block block){
        
        List<Stmt> blockSS = block.statements();
        
        if(blockSS.size() == 1 && blockSS.get(0) instanceof Block){
            return unrollBlock((Block) blockSS.get(0));
        }
        return blockSS; 
    }
    
    
    public void addFlattened(List<Stmt> stmts){
        if(flattenedCodes == null){
            flattenedCodes = new ArrayList<Stmt>();
        }
        flattenedCodes.addAll(stmts);
    }

    public List<Stmt> getFlattenedCodes() {
        if(flattenedCodes == null){
            flattenedCodes = new ArrayList<Stmt>();
        }
        return flattenedCodes;
    }
}
