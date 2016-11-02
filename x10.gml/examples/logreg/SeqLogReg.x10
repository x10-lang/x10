/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 */
import x10.matrix.ElemType;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.util.Debug;

/**
 * Sequential implementation of logistic regression
 */
public class SeqLogReg(N:Long, D:Long) {
    private val C = 2;
    private val tolerance = 0.000001f;
    private val eta0 = 0.0001f;
    private val eta1 = 0.25f;
    private val eta2 = 0.75f;
    private val sigma1 = 0.25f;
    private val sigma2 = 0.5f;
    private val sigma3 = 4.0f;
    private val psi = 0.1f;
    
    private val regularization:Float;
    private val maxiter:Long;
    private val maxinneriter:Long; 
    
    /** Matrix of training examples */
    val X:DenseMatrix;
    /** Vector of training regression targets */
    val y:Vector(X.M);
    /** Learned model weight vector, used for future predictions */
    private val B:Vector(D);
    
    private val bias:Boolean;
    
    private val lambda:Vector(D);
    private val P:Vector(N);
    private val Grad:Vector(D);
    private val S:Vector(D);
    private val R:Vector(D);
    private val V:Vector(D);
    private val Q:Vector(N);
    private val HV:Vector(D);
    private val Snew:Vector(D);
    private val Bnew:Vector(D);
    
    private val tmpDist:Vector(N);
    private val tmpDup:Vector(D);
    
    private val LT1:Vector(N);
    private val LT2:Vector(N);
    
    private val Pnew:Vector(N);
    
    private var delta:ElemType;
    private var obj:ElemType;
    private var norm_Grad:ElemType;
    private var norm_Grad_initial:ElemType;
    private var norm_R2:ElemType;
    private var iter:Long;        
    private var converge:Boolean;
    
	public def this(N:Long, D:Long, x_:DenseMatrix, y_:Vector,
					it:Int, nit:Int, reg:Float, bias:Boolean) {
	    property(N, D);
		X=x_; 
		y=y_ as Vector(N);
        this.maxiter = it;
        this.maxinneriter = (nit == 0n) ? D : nit as Long; 
        this.regularization = reg;
        this.bias = bias;
        
        lambda = Vector.make(D);
        B = Vector.make(D);
        P = Vector.make(N); 
        Grad = Vector.make(D); 
        
        //temp data
        tmpDist = Vector.make(N);
        tmpDup = Vector.make(D);
        S = Vector.make(D);
        R = Vector.make(D);
        V = Vector.make(D);
        Q = Vector.make(N);
        HV = Vector.make(D);
        Snew = Vector.make(D);
        Bnew = Vector.make(D);
        Pnew = Vector.make(N);
        //SystemML 2-column table (LT) transformed into 2 vectors
        LT1 = Vector.make(N);
        LT2 = Vector.make(N);        
	}
	
	public def run() {
	    init();
	    while (!converge) {
	        step();
	    }
	    return B;    
	}
	   
	private def init() {
        // K = 1, Table Y in systemML is composed of two columns, first = 2-y, second = y-1
        
        iter = 1;
        
        //scale_lambda = matrix (1, rows = D, cols = 1); scale_lambda [D, 1] = 0;
        tmpDup.init( (i:Long)=>{ 
                if (!bias) return 1.0;
                else return (i==D-1)? 0.0 : 1.0; 
        });
        
        //rowSums_X_sq = rowSums (X ^ 2);
        X.rowSumTo(tmpDist, (a:ElemType)=>{ a * a });
        
        //lambda = (scale_lambda %*% matrix (1, rows = 1, cols = K)) * regularization;
        lambda.scale(regularization, tmpDup); 
        
        //delta = 0.5 * sqrt (D) / max (sqrt (rowSums_X_sq));
        delta = 0.5 * Math.sqrt (D) / tmpDist.max((a:ElemType)=>{ Math.sqrt(a) });
        
        //B = matrix (0, rows = D, cols = K);
        //B.init( 0.0);
        
        //P = matrix (1, rows = N, cols = K+1); P = P / (K + 1); 
        P.init(0.5);
        
        //obj = N * log (K + 1);
        obj = N * Math.log(C);
        
        // Grad = t(X) %*% (P [, 1:K] - Y [, 1:K]);    //GML note: Y[,1:K] is 2-y
        // Grad = Grad + lambda * B;   (B is Zero, nothing to be done)
        P.copyTo(tmpDist);
        tmpDist.cellAdd(y).cellSub(2.0); 
        Grad.mult( tmpDist, X);                                  
        
        //norm_Grad = sqrt (sum (Grad ^ 2));
        norm_Grad = Math.sqrt ( Grad.sum((a:ElemType)=> { a * a }) ); 
        
        //norm_Grad_initial = norm_Grad;
        norm_Grad_initial = norm_Grad;
        
        //converge = (norm_Grad < tol) | (iter > maxiter);
        converge = (norm_Grad < tolerance) | (iter > maxiter) ;
	}
	

	
	
	private def step() {
        // # SOLVE TRUST REGION SUB-PROBLEM  //  
        var alpha:ElemType = 0.0;
    
        // S = matrix (0, rows = D, cols = K);
        S.reset();                   
        
        // R = - Grad;
        R.scale(-1.0 as ElemType, Grad);    
        
        // V = R;
        R.copyTo(V); 
        
        // delta2 = delta ^ 2;
        val delta2 = delta * delta ;
        
        // inneriter = 1;
        var inneriter:Long = 1;                      
        
        // norm_R2 = sum (R ^ 2);
        var norm_R2:ElemType = R.sum( (a:ElemType)=>{ a * a }  );  
        
        // innerconverge = (sqrt (norm_R2) <= psi * norm_Grad);
        var innerconverge:Boolean = (Math.sqrt (norm_R2) <= psi * norm_Grad);   
        
        // is_trust_boundary_reached = 0;
        var is_trust_boundary_reached:Boolean = false;                                
        
        while (! innerconverge){
            // ssX_V = V;
            V.copyTo(tmpDup);
            
            // Q = P [, 1:K] * (X %*% ssX_V);
            tmpDist.mult(X, tmpDup);                                        
            tmpDist.copyTo(Q);
            Q.cellMult(P);
            
            // HV = t(X) %*% (Q - P [, 1:K] * (rowSums (Q) %*% matrix (1, rows = 1, cols = K)));
            P.copyTo(tmpDist);
            tmpDist.cellMult(Q);
            Q.cellSub(tmpDist);
            HV.mult(Q, X);                                               
            
            // HV = HV + lambda * V;
            V.copyTo(tmpDup);
            tmpDup.cellMult(lambda);
            HV.cellAdd(tmpDup);
            
            // alpha = norm_R2 / sum (V * HV);
            V.copyTo(tmpDup);
            tmpDup.cellMult(HV);
            val VHVsum = tmpDup.sum();                        
            alpha = norm_R2 / VHVsum;
            
            //Snew = S + alpha * V;
            V.copyTo(tmpDup);
            tmpDup.scale(alpha);
            S.copyTo(Snew);
            Snew.cellAdd(tmpDup);
            
            //norm_Snew2 = sum (Snew ^ 2);
            val norm_Snew2 = Snew.sum( (a:ElemType)=>{ a * a }  );
            
            if (norm_Snew2 <= delta2) {
                //S = Snew;
                Snew.copyTo(S);
                
                //R = R - alpha * HV;
                HV.copyTo(tmpDup);
                tmpDup.scale(alpha);
                R.cellSub(tmpDup);
                
                //old_norm_R2 = norm_R2 
                val old_norm_R2 = norm_R2;
                        
                //norm_R2 = sum (R ^ 2);
                norm_R2 = R.sum( (a:ElemType)=>{ a * a }  ); 
                
                //V = R + (norm_R2 / old_norm_R2) * V;
                val beta = norm_R2 / old_norm_R2;
                V.copyTo(tmpDup);
                tmpDup.scale(beta).cellAdd(R);
                tmpDup.copyTo(V);
                
                //innerconverge = (sqrt (norm_R2) <= psi * norm_Grad);
                innerconverge = (Math.sqrt (norm_R2) <= psi * norm_Grad);
            } else {
                //is_trust_boundary_reached = 1;
                is_trust_boundary_reached = true;
                
                //sv = sum (S * V);
                V.copyTo(tmpDup);
                tmpDup.cellMult(S);
                val sv = tmpDup.sum();  
                
                //v2 = sum (V ^ 2);
                val v2 = V.sum( (a:ElemType)=>{ a * a }  );
                
                //s2 = sum (S ^ 2);
                val s2 = S.sum( (a:ElemType)=>{ a * a }  );
                
                //rad = sqrt (sv ^ 2 + v2 * (delta2 - s2));
                val rad = Math.sqrt (sv * sv + v2 * (delta2 - s2));
                
                //same if-else from system-ml code
                if (sv >= 0.0) {
                    alpha = (delta2 - s2) / (sv + rad);
                } else {
                    alpha = (rad - sv) / v2;
                }
                
                //S = S + alpha * V;
                V.copyTo(tmpDup);
                tmpDup.scale(alpha);
                S.cellAdd(tmpDup);
                
                
                //R = R - alpha * HV;
                HV.copyTo(tmpDup);
                tmpDup.scale(alpha);
                R.cellSub(tmpDup);
                
                
                innerconverge = true;
            }
            
            inneriter = inneriter + 1;
            innerconverge = innerconverge | (inneriter > maxinneriter);
        }
        // # END TRUST REGION SUB-PROBLEM
        
        
        //# compute rho, update B, obtain delta
        //gs = sum (S * Grad);
        S.copyTo(tmpDup);
        tmpDup.cellMult(Grad);
        val gs = tmpDup.sum();
        
        //qk = - 0.5 * (gs - sum (S * R));
        S.copyTo(tmpDup);
        tmpDup.cellMult(R);
        val SRsum = tmpDup.sum();
        val qk = - 0.5 * (gs - SRsum);
        
        //B_new = B + S;
        B.copyTo(Bnew);
        Bnew.cellAdd(S);
        
        //ssX_B_new = B_new;
        //LT = append ((X %*% ssX_B_new), matrix (0, rows = N, cols = 1));
        LT1.mult(X, Bnew);
        
        //LT = LT - rowMaxs (LT) %*% matrix (1, rows = 1, cols = K+1);
        LT2.map(LT1, (a:ElemType)=>{  a > 0 ? -1 * a  : 0.0 } );
        LT1.map( (a:ElemType)=>{ a >= 0 ? 0.0 : a  } );
        
        //sum (Y * LT) 
        y.copyTo(tmpDist);
        tmpDist.map( (a:ElemType)=>{ 2 - a } ); // first column is Y1 = 2-y
        val Y1dot = tmpDist.dot(LT1);
        tmpDist.map( (a:ElemType)=>{ (a * -1) + 1 } ); // second column is Y2 = y-1 (compute it from first column by (-Y1 +1) 
        val Y2dot = tmpDist.dot(LT2);
        val YLTsum = Y1dot + Y2dot;
        
        //exp_LT = exp (LT);
        LT1.map( (a:ElemType)=>{ Math.exp(a) } );
        LT2.map( (a:ElemType)=>{ Math.exp(a) } );
        
        //P_new  = exp_LT / (rowSums (exp_LT) %*% matrix (1, rows = 1, cols = K+1));
        //rowSums (exp_LT)
        LT1.copyTo(tmpDist);
        tmpDist.cellAdd(LT2);
        
        //sum (log (rowSums (exp_LT)))
        val L1L2ExpLogsum = tmpDist.sum( (a:ElemType)=>{ Math.log(a) } ) ;
        
        //P_new  = exp_LT / rowSums (exp_LT)
        LT1.copyTo(Pnew);
        Pnew.cellDiv(tmpDist);
        
        //obj_new = - sum (Y * LT) + sum (log (rowSums (exp_LT))) + 0.5 * sum (lambda * (B_new ^ 2));
        //lambda * (B_new ^ 2)
        Bnew.copyTo(tmpDup);
        tmpDup.map((a:ElemType)=>{ a * a }).cellMult(lambda);
        val lambdaBnew_sum = tmpDup.sum();
        
        val obj_new = -1 * YLTsum + L1L2ExpLogsum + 0.5 * lambdaBnew_sum;
         
        //# Consider updating LT in the inner loop
        //# Consider the big "obj" and "obj_new" rounding-off their small difference below:

        //actred = (obj - obj_new);
        val actred = (obj - obj_new);
        
        //rho = actred / qk;
        val rho = actred / qk;
        
        //is_rho_accepted = (rho > eta0);
        val is_rho_accepted = (rho > eta0);
        
        //snorm = sqrt (sum (S ^ 2));
        val snorm = Math.sqrt( S.sum( (a:ElemType)=>{ a * a } ) );
        
        if (iter == 1) {
           delta = Math.min (delta, snorm);
        }

        val alpha2 = obj_new - obj - gs;
        if (alpha2 <= 0) {
           alpha = sigma3;
        } 
        else {
           alpha = Math.max (sigma1, -0.5 * gs / alpha2);
        }
        
        if (rho < eta0) {
            delta = Math.min (Math.max (alpha, sigma1) * snorm, sigma2 * delta);
        }
        else {
            if (rho < eta1) {
                delta = Math.max (sigma1 * delta, Math.min (alpha * snorm, sigma2 * delta));
            }
            else { 
                if (rho < eta2) {
                    delta = Math.max (sigma1 * delta, Math.min (alpha * snorm, sigma3 * delta));
                }
                else {
                    delta = Math.max (delta, Math.min (alpha * snorm, sigma3 * delta));
                }
            }
        } 
        
        if (is_trust_boundary_reached) {
            Console.OUT.println ("-- Outer Iteration " + iter + ": Had " + (inneriter - 1) + " CG iterations, trust bound REACHED");
        } else {
            Console.OUT.println ("-- Outer Iteration " + iter + ": Had " + (inneriter - 1) + " CG iterations");
        }
    
        Console.OUT.println ("   -- Obj.Reduction:  Actual = " + actred + ",  Predicted = " + qk + 
                "  (A/P: " + (Math.round (10000.0 * rho) / 10000.0) + "),  Trust Delta = " + delta);
        
        if (is_rho_accepted) {
            //B = B_new;
            Bnew.copyTo(B);
            
            //P = P_new;
            Pnew.copyTo(P);
            
            //Grad = t(X) %*% (P [, 1:K] - Y [, 1:K]);
            P.copyTo(tmpDist);
            tmpDist.cellAdd(y).cellSub(2.0); 
            Grad.mult(tmpDist, X); 
            
            //Grad = Grad + lambda * B;
            B.copyTo(tmpDup);
            tmpDup.cellMult(lambda);
            Grad.cellAdd(tmpDup);
                         
            //norm_Grad = sqrt (sum (Grad ^ 2));
            norm_Grad = Math.sqrt ( Grad.sum( (a:ElemType)=>{ a * a } ) );
            
            //obj = obj_new;
            obj = obj_new;
            Console.OUT.println ("   -- New Objective = " + obj + ",  Beta Change Norm = " + snorm + ",  Gradient Norm = " + norm_Grad);
        }
        
        //iter = iter + 1;
        iter = iter + 1;

        /*converge = ((norm_Grad < (tol * norm_Grad_initial)) | (iter > maxiter) |
                   ((is_trust_boundary_reached == 0) & (Math.abs (actred) < (Math.abs (obj) + Math.abs (obj_new)) * 0.00000000000001)));*/
       
        converge = ((norm_Grad < (tolerance * norm_Grad_initial)) | (iter > maxiter) |
            ((is_trust_boundary_reached == false) & (Math.abs (actred) < (Math.abs (obj) + Math.abs (obj_new)) * 0.00000000000001)));
        
        if (converge) { 
            Console.OUT.println ("Termination / Convergence condition satisfied."); 
        } else { 
            Console.OUT.println (" "); 
        }
        
    }
	
}
