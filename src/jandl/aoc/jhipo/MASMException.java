/*
 * File:		MASMException .java
 * Classname:	MASMException 
 * 
 * Description:	custom exception for MASM.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

public class MASMException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MASMException() {
		super();
	}

	public MASMException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MASMException(String arg0) {
		super(arg0);
	}

	public MASMException(Throwable arg0) {
		super(arg0);
	}

}
