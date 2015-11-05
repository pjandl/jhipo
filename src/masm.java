/*
 * File:		masm.java
 * Classname:	masm
 * 
 * Description:	command line tool to invoke masm (macroassembler) to compile assembly program.
 * 
 * Author:		Peter Jandl Junior
 * Date:		2015-04-30	
 * 
 */
import jandl.aoc.jhipo.MASM;
import jandl.aoc.jhipo.MASMException;
import jandl.aoc.jhipo.Tools;

import java.io.IOException;

public class masm {
	// flags
	private static boolean executeProgram = false;

	public static void main(String[] args) {
		if (args.length == 0) {
			showHelp(1);
		}
		int i = 0;
		while (i < args.length) {
			if (args[i].charAt(0) == '+') {
				if (args[i].contains("v")) {
					MASM.VERBOSE = true;
				}
				if (args[i].contains("d")) {
					MASM.DEBUG = true;
				}
				if (args[i].contains("s")) {
					MASM.SHOW_SYMBOL_TABLE = true;
				}
				if (args[i].contains("c")) {
					MASM.SYNTAX_CHECK_ONLY = true;
				}
				if (args[i].contains("a")) {
					MASM.SHOW_ASSEMBLY = true;
				}
				if (args[i].contains("n")) {
					MASM.CASE_SENSITIVE = true;
				}
				if (args[i].contains("e")) {
					executeProgram = true;
				}
				i++;
			} else if (args[i].charAt(0) == '-') {
				if (args[i].contains("v")) {
					MASM.VERBOSE = false;
				}
				if (args[i].contains("d")) {
					MASM.DEBUG = false;
				}
				if (args[i].contains("s")) {
					MASM.SHOW_SYMBOL_TABLE = false;
				}
				if (args[i].contains("c")) {
					MASM.SYNTAX_CHECK_ONLY = false;
				}
				if (args[i].contains("a")) {
					MASM.SHOW_ASSEMBLY = false;
				}
				if (args[i].contains("n")) {
					MASM.CASE_SENSITIVE = false;
				}
				if (args[i].contains("e")) {
					executeProgram = false;
				}
				i++;
			} else if (args[i].charAt(0) == '?') {
				showHelp(0);
			} else {
				break;
			}
		}
		if (i >= args.length) {
			showHelp(2);
		}
		MASM masm = new MASM();
		String outputFileName = null;
		try {
			if (args.length > i + 1) {
				outputFileName = args[i + 1];
			} else {
				outputFileName = MASM.DEFAULT_OUTPUT_FILENAME;
			}
			masm.assembly(args[i], outputFileName);
		} catch (MASMException e ) {
			System.err.printf("[MASM | error] %s.%s", e.getLocalizedMessage(),
					Tools.NL);
		} catch (IOException e ) {
			System.err.printf("[MASM | error] %s.%s", e.getLocalizedMessage(),
					Tools.NL);
		} catch (Exception e) {
			System.err.printf("[MASM | internalerror] %s: %s.%s", e.getClass().getName(), e.getLocalizedMessage(),
					Tools.NL);
			if (MASM.DEBUG) {
				e.printStackTrace();
			}
		}
		if (executeProgram) {
			String[] jhipoArgs = {
					"+aes", outputFileName
			};
			jhipovm.main(jhipoArgs);
		}
	}

	public static void showHelp(int code) {
		switch (code) {
		case 1:
			System.err.printf("[MASM | help] no arguments supplied.%s",
					Tools.NL);
			break;
		case 2:
			System.err.printf("[MASM | help] no input filename supplied.%s",
					Tools.NL);
			break;
		}
		System.out
				.printf("%sUsage:%s\tmasm [options] <inputFileName> [outputFileName]%s",
						Tools.NL, Tools.NL, Tools.NL);
		System.out.printf("Options (default ON):%s", Tools.NL);
		System.out.printf("\t<+|->a\tshow assembly     [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->c\tsyntax check only [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->d\tdebug mode        [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->e\texecute in VM     [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->n\tcase sensitive    [ON|off]%s", Tools.NL);
		System.out.printf("\t<+|->s\tsymbol table      [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->v\tverbose mode      [on|OFF]%s", Tools.NL);
		System.exit(-1);
	}

}
