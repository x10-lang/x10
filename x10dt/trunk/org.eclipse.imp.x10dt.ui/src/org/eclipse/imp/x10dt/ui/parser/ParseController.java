/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import lpg.runtime.ILexStream;
import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.ILexer;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParser;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.jface.text.IRegion;

import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.util.ErrorInfo;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;
import x10.visit.InstanceInvariantChecker;
import x10.visit.PositionInvariantChecker;

public class ParseController extends SimpleLPGParseController {
	public interface InvariantViolationHandler {
		public void clear();
		public void handleViolation(ErrorInfo error);
		public void consumeAST(Node root);
	}

    private CompilerDelegate fCompiler;
    private PMMonitor fMonitor;
    private IPrsStream fPrsStream;
    private ILexStream fLexStream;
    private InvariantViolationHandler fViolationHandler;

    public ParseController() {
    	super(X10DTCorePlugin.kLanguageName);
    }

    public IParser getParser() {
    	return fParser;
    }

    public ILexer getLexer() {
        return fLexer;
    }

    public CompilerDelegate getCompiler() {
        initializeGlobalsIfNeeded();
        return fCompiler;
    }

    @Override
    public ISourcePositionLocator getSourcePositionLocator() {
    	return new PolyglotNodeLocator(fProject, getLexer() != null ? getLexer().getILexStream() : null);
    }

    public ILanguageSyntaxProperties getSyntaxProperties() {
        return new X10SyntaxProperties();
    }

    @Override
    public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
        super.initialize(filePath, project, handler);
        fMonitor= new PMMonitor(null);
    }

    public void setViolationHandler(InvariantViolationHandler handler) {
    	fViolationHandler= handler;
    }

    public Object parse(String contents, IProgressMonitor monitor) {
        FileSource fileSource= null;
        try {
            fMonitor.setMonitor(monitor);
            
            String path= fProject != null ? fProject.getRawProject().getLocation().append(fFilePath).toOSString():fFilePath.toOSString();
            File file= new File(path);
            fileSource= new FileSource(new StringResource(contents, file, path));

            List<Source> streams= new ArrayList<Source>();
            // Bug 526: NPE when opening a file outside the workspace due to null fProject.
            IProject proj= (fProject != null) ? fProject.getRawProject() : null;

            streams.add(fileSource);
            IPath sourcePath = (fProject != null) ? Platform.getLocation().append(fProject.getName()).append(fFilePath) : fFilePath;

            fCompiler= new CompilerDelegate(fMonitor, handler, proj, sourcePath, fViolationHandler); // Create the compiler
            fCompiler.compile(streams);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            // RMF 8/2/2006 - retrieve the AST, token stream and lex stream, if they exist; front-end semantic
            // checks may fail, even though the AST/token-stream are well-formed enough to support various IDE
        	// services, like syntax highlighting and the outline view's contents.
            if (fileSource != null && fCompiler != null) {
            	final X10Parser parser= fCompiler.getParserFor(fileSource);
            	final X10Lexer lexer= fCompiler.getLexerFor(fileSource);
            	fPrsStream = parser.getIPrsStream();
            	fLexStream = lexer.getILexStream();
            	fParser = new ParserDelegate(parser); // HACK - SimpleLPGParseController.cacheKeywordsOnce() needs an IParser and an ILexer, so create them here. Luckily, they're just lightweight wrappers...
            	fLexer = new LexerDelegate(lexer);
            	fCurrentAst= fCompiler.getASTFor(fileSource); // getASTFor(fileSource); // TODO use commandLineJobs() instead?
            }
            if (fViolationHandler != null && fCurrentAst != null) {
            	Job job= fCompiler.getJobFor(fileSource);
            	PositionInvariantChecker pic= new PositionInvariantChecker(job, "");
            	InstanceInvariantChecker iic= new InstanceInvariantChecker(job);

            	((Node) fCurrentAst).visit(pic);
            	((Node) fCurrentAst).visit(iic);

            	fViolationHandler.consumeAST((Node) fCurrentAst);
            }
            // RMF 8/2/2006 - cacheKeywordsOnce() must have been run for syntax highlighting to work.
            // Must do this after attempting parsing (even though that might fail), since it depends
            // on the parser/lexer being set in the ExtensionInfo, which only happens as a result of
            // ExtensionInfo.parser(). Ugghh.
            if (fParser != null) {
            	cacheKeywordsOnce();
            }
            fCompiler = null;
            fParser = null;
            fLexer = null;
            Globals.initialize(null);
        }
        return fCurrentAst;
    }

    // TODO Use this rather than fCompiler.getASTFor() ? (more reliable?)
    private Object getASTFor(Source source) {
    	Collection<Job> cmdJobs = fCompiler.getExtInfo().scheduler().commandLineJobs();
    	for(Job job: cmdJobs) {
    		if (job.source().equals(source)) {
    			return job.ast();
    		}
    	}
    	return null;
    }

    public ILexStream getLexStream() {
    	return fLexStream;
    }

    public IPrsStream getParseStream() {
    	return fPrsStream;
    }

    /**
     * Polyglot has a thread-local variable that must be initialized in each thread
     * that uses the front end. The Eclipse platform creates many threads that may
     * access the compiler front end (on behalf of various services like hover help
     * or the documentation provider), and we don't have control over where/when this
     * happens. As a result, this method is used to initialize the thread-local variable
     * from within those accessor methods that give the client access to the front-end.
     */
    private void initializeGlobalsIfNeeded() {
        if (Globals.Compiler() == null) {
            Globals.initialize(fCompiler.getCompiler());
        }
    }

    @Override
    public Object getCurrentAst() {
//    	if (fCompiler == null)
//    		return null;
//        initializeGlobalsIfNeeded();
        return super.getCurrentAst();
    }

    @Override
    public Iterator<IToken> getTokenIterator(IRegion region) {
      final int regionOffset= region.getOffset();
      final int regionLength= region.getLength();
      final int regionEnd= regionOffset + regionLength - 1;

      if (fPrsStream == null) {
    	  return new Iterator<IToken>() {
			public boolean hasNext() {
				return false;
			}

			public IToken next() {
				return null;
			}

			public void remove() {
			} };
      }
      return new Iterator<IToken>() {
          final IPrsStream stream= fPrsStream;
          final int firstTokIdx= getTokenIndexAtCharacter(regionOffset);
          final int lastTokIdx;
          {
              int endIdx= getTokenIndexAtCharacter(regionEnd);
              char[] streamChars= stream.getInputChars();
              int streamLen= streamChars.length;
              try {
                  if (regionEnd >= 1 && regionEnd < streamLen
                          && streamChars[regionEnd] == IToken.EOF) {
                      // skip EOF token (assume LPG puts one at end of input
                      // character stream, since it does)
                      endIdx--;
                  }
              } catch (ArrayIndexOutOfBoundsException e) {
                  ErrorHandler.logError("ParseController.getTokenIterator(IRegion): error initializing lastTokIdx",
                          e);
                  // System.err.println("getTokenIterator: new Iterator(..)<init>: ArrayIndexOutOfBoundsException");
                  // System.err.println(" regionEnd = " + regionEnd + ", endIdx = " + endIdx + ", streamLen = " + streamLen + ",
                  // inputChars.length = " + streamChars.length);
              }
              lastTokIdx= endIdx;
          }
          int curTokIdx= Math.max(1, firstTokIdx); // skip bogus initial token

          private int getTokenIndexAtCharacter(int offset) {
              int result= stream.getTokenIndexAtCharacter(offset);
              // getTokenIndexAtCharacter() answers the negative of the index of the
              // preceding token if the given offset is not actually within a token.
              if (result < 0) {
                  result= -result + 1;
              }

              // The above may leave result set to a value that is one more than the
              // last token index, so return the last token index if that's the case
              // (This can happen if the end of the file contains some text that
              // does not correspond to a token--e.g., if the text represents an adjunct
              // or something unrecognized)
              if (result >= stream.getTokens().size())
                  result= stream.getTokens().size() - 1;

              return result;
          }

          // The following declarations cover the whole input stream, which
          // may be a proper superset of the range of the given region.
          // For now, that's a simple way to collect the information, and
          // most often the given region corresponds to the whole input anyway.
          // In any case, iteration is based on the range of the given region.

          // The preceding adjuncts for each token
          IToken[][] precedingAdjuncts= new IToken[lastTokIdx + 1][];
          {
              stream.setStreamLength();
              for(int i= 0; i < precedingAdjuncts.length; i++) {
                  precedingAdjuncts[i]= stream.getPrecedingAdjuncts(i);
              }
          }

          // The current indices for each array of preceding adjuncts
          int[] nextPrecedingAdjunct= new int[lastTokIdx + 1];
          {
              for(int i= 0; i < nextPrecedingAdjunct.length; i++) {
                  if (precedingAdjuncts[i].length == 0)
                      nextPrecedingAdjunct[i]= -1;
                  else
                      nextPrecedingAdjunct[i]= 0;
              }
          }

          // The following adjuncts (for the last token only)
          IToken[] followingAdjuncts;
          {
              if (lastTokIdx <= 0)
                  followingAdjuncts= new IToken[0];
              else
                  followingAdjuncts= stream.getFollowingAdjuncts(lastTokIdx);
          }

          // The current index for the array of following adjuncts
          int nextFollowingAdjunct;
          {
              if (followingAdjuncts.length == 0)
                  nextFollowingAdjunct= -1;
              else
                  nextFollowingAdjunct= 0;
          }

          // To support hasNext(); initial values may be reset if appropriate
          private boolean finalTokenReturned= regionEnd < 1 || lastTokIdx <= 0;
          private boolean finalAdjunctsReturned= !(followingAdjuncts.length > 0);

          /**
           * Tests whether the iterator has any unreturned tokens. These may
           * include "regular" tokens and "adjunct" tokens (e.g., representing
           * comments).
           * 
           * @return True if there is another token available, false otherwise
           */
          public boolean hasNext() {
              return !(finalTokenReturned && finalAdjunctsReturned);
          }

          /**
           * Returns the next available token in the iterator (or null if
           * there is none)
           * 
           * Will return a valid token under conditions that would cause
           * hasNext() to to return true; conversely, will return null under
           * conditions that would cause hasNext() to return false.
           * 
           * As a side effect, updates the flags that are used to compute the
           * value returned by hasNext().
           * 
           * The returned token may be a "regular" token (which will have a
           * corresponding AST node) or an "adjunct" token (which will
           * represent a comment). The tokens are returned in the order in
           * which they occur in the text, regardless of their kind.
           * 
           */
          public IToken next() {
              int next= -1; // for convenience

              // If we're not all the way through the tokens
              if (curTokIdx <= lastTokIdx) {

                  // First check for any remaining preceding adjuncts
                  // of the current token
                  next= nextPrecedingAdjunct[curTokIdx];
                  // If the current token has any unreturned preceding
                  // adjuncts
                  if (next >= 0 && next < precedingAdjuncts[curTokIdx].length) {
                      // Return the next preceding adjunct, incrementing the
                      // adjunct index afterwards
                      return precedingAdjuncts[curTokIdx][nextPrecedingAdjunct[curTokIdx]++];
                  }

                  // Flag whether the current token is the last one
                  finalTokenReturned= curTokIdx >= lastTokIdx;

                  // Return the current token, incrementing the token index
                  // afterwards
                  return stream.getIToken(curTokIdx++);
              }

              // If there are any adjuncts following the last token
              if (nextFollowingAdjunct >= 0 && nextFollowingAdjunct < followingAdjuncts.length) {
                  // Flag whether the current adjunct is the last one
                  finalAdjunctsReturned= (nextFollowingAdjunct + 1) >= followingAdjuncts.length;

                  // Return the current adjunct, incrementing the adjunct
                  // index afterwards
                  return followingAdjuncts[nextFollowingAdjunct++];
              }

              return null;
          }

          public void remove() {
              throw new UnsupportedOperationException("Unimplemented");
          }
      };
    }
}
