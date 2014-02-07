/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
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
        
    private int pcValue; //current pc Value
    
    List<Stmt> fastStmts;
    List<Stmt> resumeStmts; //store all the stmts before PC Change
    List<Stmt> resumePostStmts; //store all the stmts after PC Change
    List<Stmt> backStmts;


    /**
     * Initialize a TransCodes with as-is pcValue
     * @param pcValue
     */
    public TransCodes(int pcValue){
        this.pcValue = pcValue;
        
        fastStmts = new ArrayList<Stmt>();
        resumeStmts = new ArrayList<Stmt>();
        resumePostStmts = new ArrayList<Stmt>();
        backStmts = new ArrayList<Stmt>();
    }
    
    public void increasePC(){
        pcValue ++;
    }
    
    public int pcValue() {
        return pcValue;
    } 
    
    public void addFast(List<Stmt> ss){
        fastStmts.addAll(ss);
    }
    
    public void addFast(Stmt s){
        fastStmts.add(s);
    }

    public void addResume(List<Stmt> ss){
        resumeStmts.addAll(ss);
    }
    
    public void addResume(Stmt s){
        resumeStmts.add(s);
    }
    
    public void addResumePost(List<Stmt> ss){
        resumePostStmts.addAll(ss);
    }
    
    public void addPostResume(Stmt s){
        resumePostStmts.add(s);
    }
    
    public void addBack(List<Stmt> ss){
        backStmts.addAll(ss);
    }
    
    public void addBack(Stmt s){
        backStmts.add(s);
    }
    
    public List<Stmt> getFastStmts(){
        return fastStmts;
    }
    
    public List<Stmt> getResumeStmts(){
        return resumeStmts;
    }
    
    public List<Stmt> getResumePostStmts(){
        return resumePostStmts;
    }
    
    public List<Stmt> getBackStmts(){
        return backStmts;
    }
   
}
