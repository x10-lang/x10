/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import lpg.runtime.IMessageHandler;
import lpg.runtime.ParseErrorCodes;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

public class BuilderExtensionInfo extends polyglot.ext.x10.ExtensionInfo {
        private final X10Builder fBuilder;

        public BuilderExtensionInfo(X10Builder builder) {
            this.fBuilder= builder;
        }

        @Override
    	protected Scheduler createScheduler() {
    		return new X10Scheduler(this) {
    			@Override
    			public List<Goal> goals(Job job) {
    				List<Goal> goals= super.goals(job);
    				Goal endGoal = goals.get(goals.size()-1);
    				if(!(endGoal.name().equals("End"))) {
    					throw new IllegalStateException("Not an End Goal?");
    				}
					endGoal.addPrereq(new CollectBookmarksGoal(job, fBuilder));
					return goals;
    			}
    		};
    	}
    	/**
    	 * Exactly like the base-class implementation, but sets the lexer up with an IMessageHandler.
    	 */
    	public Parser parser(Reader reader, FileSource source, final ErrorQueue eq) {
//    	    if (source.path().endsWith(XML_FILE_DOT_EXTENSION)) {
//    	        return new DomParser(reader, (X10TypeSystem) ts, (X10NodeFactory) nf, source, eq);
//    	    }

    	    try {
                //
                // X10Lexer may be invoked using one of two constructors.
                // One constructor takes as argument a string representing
                // a (fully-qualified) filename; the other constructor takes
                // as arguments a (file) Reader and a string representing the
                // name of the file in question. Invoking the first
                // constructor is more efficient because a buffered File is created
                // from that string and read with one (read) operation. However,
                // we depend on Polyglot to provide us with a fully qualified
                // name for the file. In Version 1.3.0, source.name() yielded a
                // fully-qualified name. In 1.3.2, source.path() yields a fully-
                // qualified name. If this assumption still holds then the
                // first constructor will work.
                // The advantage of using the Reader constructor is that it
                // will always work, though not as efficiently.
                //
    	    	final X10Lexer x10_lexer = new X10Lexer(reader, source.name());
                //
//                final X10Lexer x10_lexer= (source instanceof SourceLoader.ZipSource) ? new X10Lexer() : new X10Lexer(source.path());//PORT1.7 zip file requires different Lexer
//                if (source instanceof SourceLoader.ZipSource) {
//                	//PORT 1.7 special case for loading from jar file
//                	SourceLoader.ZipSource zipSource = (SourceLoader.ZipSource) source;
//                	String name= zipSource.name();
//                	String contents= BuilderUtils.getFileContents(zipSource.open());
//
//                	x10_lexer.initialize(contents.toCharArray(), name);
//                }
                x10_lexer.getILexStream().setMessageHandler(new IMessageHandler() {
                    public void handleMessage(int errorCode, int[] msgLocation, int[] errorLocation, String filename, String[] errorInfo) {
                       //PORT1.7 -- need to get info (e.g. is this zipfile?) 
                    	// need file and entry name from sourceLoader$ZipSource
                    	// to be able to issue accurate error messages
                    	// store enough info in the Position so that createMarkers can put marker in right spot (or ???)
                    	//(but.. runtime jar file will usually be outside the workspace)
                    	// UBT
                    	// check whether this message corresponds to something outside workspace; if so, discard or put "someplace obvious"
                    	//            ( visible)  -- project root folder???  so that marker will show up and info isn't lost
                    	Position p= new Position(null, filename, msgLocation[IMessageHandler.START_LINE_INDEX],
                                msgLocation[IMessageHandler.START_COLUMN_INDEX], msgLocation[IMessageHandler.END_LINE_INDEX],
                                msgLocation[IMessageHandler.END_COLUMN_INDEX], msgLocation[IMessageHandler.OFFSET_INDEX],
                                msgLocation[IMessageHandler.OFFSET_INDEX] + msgLocation[IMessageHandler.LENGTH_INDEX]);
                        eq.enqueue(ErrorInfo.SYNTAX_ERROR, errorInfo[0] + " " + ParseErrorCodes.errorMsgText[errorCode], p);
                    }
                });
                X10Parser x10_parser= new X10Parser(x10_lexer.getILexStream(), ts, nf, source, eq); // Create the parser
                x10_lexer.lexer(x10_parser.getIPrsStream());
                return x10_parser; // Parse the token stream to produce an AST
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("Could not parse " + source.path());
        }
    }