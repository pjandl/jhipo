/*
 * File:		MASM.java
 * Classname:	MASM
 * 
 * Description:	macroassembler compiler for JHIPO system.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MASM {
	public static int MAX_SIZE = 256;
	public static boolean CASE_SENSITIVE = true;
	public static boolean DEBUG = false;
	public static boolean SHOW_ASSEMBLY = false;
	public static boolean SHOW_SYMBOL_TABLE = false;
	public static boolean SYNTAX_CHECK_ONLY = false;
	public static boolean VERBOSE = false;
	public static String DEFAULT_OUTPUT_FILENAME = "a.out";
	private StreamTokenizer st;
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	private int tc;
	private Token currentToken;
	private Instruction lastInstruction;
	private HashMap<String, Integer> symbolTable;
	private Object[] program;
	private int byteCount = 0;

	// private int heap = 0;

	public MASM() {
		symbolTable = new HashMap<String, Integer>();
		program = new Object[MAX_SIZE];
	}

	public void assembly(String inputFileName, String outputFileName)
			throws Exception {
		if (VERBOSE) {
			System.out.printf("[MASM] input filename: %s%s", inputFileName,
					Tools.NL);
		}
		passZero(inputFileName);
		passOne();
		if (!SYNTAX_CHECK_ONLY) {
			passTwo(outputFileName);
		}
		if (VERBOSE) {
			System.out.printf("[MASM] done.\n");
		}
	}

	private void passZero(String fileName) throws IOException {
		if (VERBOSE) {
			System.out.printf("[MASM] pass zero%s", Tools.NL);
		}
		// Cria e prepara StreamTokenizer
		st = new StreamTokenizer(new BufferedReader(new FileReader(fileName)));
		st.eolIsSignificant(true);
		st.ordinaryChars(14, 31);	// chars abaixo de espaço
		st.ordinaryChars(33, 47);   // ! até /
		st.ordinaryChars(58, 64);	// : até @
		st.ordinaryChars(91, 94);	// [ até ^
		st.ordinaryChar(96);		// ` 
		st.ordinaryChars(123,127);	// { até ~
		st.wordChars(95, 95);		// _ (underline) pode ser usado em identificadores
		st.commentChar(';');
		// Processa arquivo de entrada em lista de tokens
		int tokenType = st.nextToken();
		while (tokenType != StreamTokenizer.TT_EOF) {
			if (tokenType != StreamTokenizer.TT_EOL) {
				Token tk = new Token(st.sval, st.ttype, (int) st.nval,
						st.lineno());
				tokenList.add(tk);
				if (DEBUG) {
					System.out.printf("%s%s", tk, Tools.NL);
				}
			}
			tokenType = st.nextToken();
		}
		// Adiciona token de finalização
		tokenList.add(new Token(st.sval, st.ttype, (int) st.nval, st.lineno()));
	}

	private void passOne() throws Exception {
		if (VERBOSE) {
			System.out.printf("[MASM] pass one%s", Tools.NL);
		}
		byteCount = 0;
		tc = 0;
		getToken();
		expandProgram();
		if (tokenList.size() > tc) {
			throw new RuntimeException(
					String.format("input was not fully parsed"));
		}
		if (DEBUG) {
			System.out.println(tokenList);
		}
		if (SHOW_SYMBOL_TABLE) {
			System.out.printf("[MASM] symbol table:%s", Tools.NL);
			System.out.printf("+------------------+------+------+%s", Tools.NL);
			System.out.printf("|           Symbol | Addr | Byte |%s", Tools.NL);
			System.out.printf("+------------------+------+------+%s", Tools.NL);
			Object[] orderedKeySet = symbolTable.keySet().toArray();
			Arrays.sort(orderedKeySet);
			for (Object k : orderedKeySet) {
				int address = symbolTable.get(k);
				System.out.printf("| %16s |   %02X |  %03d |%s", k.toString(),
						address, address, Tools.NL);
			}
			System.out.printf("+------------------+------+------+%s", Tools.NL);
		}
		if (DEBUG) {
			System.out.println(Arrays.toString(program));
		}
	}

	private void passTwo(String outputFileName) throws Exception {
		if (VERBOSE) {
			System.out.printf("[MASM] pass two%s", Tools.NL);
		}
		if (outputFileName == null) {
			outputFileName = DEFAULT_OUTPUT_FILENAME;
		}
		if (VERBOSE) {
			System.out.printf("[MASM] output filename: %s%s", outputFileName,
					Tools.NL);
		}
		if (SHOW_ASSEMBLY) {
			System.out.printf("[MASM] generated assembly:%s", Tools.NL);
		}
		PrintWriter pw = new PrintWriter(new FileWriter(outputFileName));
		for (int p = 0; p < byteCount; p++) {
			Object nextByte = program[p];
			if (nextByte instanceof String) {
				Integer address = CASE_SENSITIVE ? symbolTable.get(nextByte)
						: symbolTable.get(((String) nextByte).toUpperCase());
				if (address == null) {
					pw.close();
					int line = findLineNumberOf(nextByte.toString());
					if (DEBUG) {
						System.out.printf("%s --> %d\n", nextByte.toString(),
								line);
					}
					throw new MASMException(String.format(
							"undefined symbol %s at line %d", nextByte, line));
				}
				nextByte = address;
			}
			pw.printf("0x%02X%s", nextByte, Tools.NL);
			if (SHOW_ASSEMBLY) {
				System.out.printf("0x%02X%s", nextByte, Tools.NL);
			}
		}
		pw.close();
	}

	private void getToken() {
		currentToken = tokenList.get(tc++);
		if (DEBUG) {
			System.out.println("-->" + currentToken);
		}
	}

	private void expandProgram() {
		if (DEBUG) {
			System.out.println("-->expandProgram()");
		}
		// verifica se não é cadeia vazia
		if (currentToken.type != StreamTokenizer.TT_EOF) {
			// verifica se label (não mnemonico)
			if (currentToken.type == StreamTokenizer.TT_WORD
					&& !isMnemonic(currentToken.lexeme)) {
				expandLabel();
			}
			if (currentToken.type == StreamTokenizer.TT_NUMBER) {
				expandByteValue();
			} else {
				expandInstruction();
			}
			// continua expansão
			expandProgram();
		}
		if (DEBUG) {
			System.out.println("<--expandProgram()");
		}
	}

	private void expandLabel() {
		if (DEBUG) {
			System.out.println("-->expandLabel()");
		}
		// define label na tabela de simbolos
		String label = CASE_SENSITIVE ? currentToken.lexeme
				: currentToken.lexeme.toUpperCase();
		symbolTable.put(label, byteCount);
		if (DEBUG) {
			System.out.printf("{consumed} %s = %d\n", currentToken.lexeme,
					byteCount);
		}
		getToken();
		// consome pontuação
		if (currentToken.type != ':') {
			throw new MASMException(
					String.format("missing ':' after %s at line %d", label,
							currentToken.line));
		}
		if (DEBUG) {
			System.out.printf("{consumed} :\n");
		}
		getToken();
		if (DEBUG) {
			System.out.println("<--expandLabel()");
		}
	}

	private void expandByteValue() {
		if (DEBUG) {
			System.out.println("-->expandByteValue()");
		}
		boolean valid = false;
		int byteValue = 0;
		// consome prefixo '0'
		getToken();
		if (currentToken.type == StreamTokenizer.TT_WORD
				&& currentToken.lexeme.charAt(0) == 'x') {
			try {
				// valor com prefixo '0x'
				byteValue = Integer.parseInt(currentToken.lexeme.substring(1),
						16);
				valid = true;
			} catch (NumberFormatException nfe) {
			}
		}
		if (!valid) {
			throw new MASMException(String.format(
					"bytevalue expected at line %d: found %s",
					currentToken.line, currentToken.lexeme));
		}
		if (byteValue > 255) {
			throw new MASMException(String.format(
					"bytevalue expected at line %d: found %d > 255",
					currentToken.line, byteValue));
		}
		program[byteCount++] = byteValue;
		if (DEBUG) {
			System.out.printf("{consumed} %s\n", currentToken.lexeme);
		}
		getToken();
		if (DEBUG) {
			System.out.println("<--expandByteValue()");
		}
	}

	private void expandInstruction() {
		if (DEBUG) {
			System.out.println("-->expandInstruction()");
		}
		if (isMnemonic(currentToken.lexeme)) {
			program[byteCount++] = lastInstruction.getOpCode();
			if (DEBUG) {
				System.out.printf("{consumed} %s = %02X\n",
						currentToken.lexeme, program[byteCount - 1]);
			}
			getToken();
			if (lastInstruction.isMultiByte()) {
				expandAddress();
			}
		} else {
			throw new MASMException(String.format(
					"instruction expected at line %d: found %s",
					currentToken.line, currentToken.lexeme));
		}
		if (DEBUG) {
			System.out.println("<--expandInstruction()");
		}
	}

	private void expandAddress() {
		if (DEBUG) {
			System.out.println("-->expandAddress()");
		}
		if (currentToken.type == StreamTokenizer.TT_WORD) {
			if (!isMnemonic(currentToken.lexeme)) {
				program[byteCount++] = currentToken.lexeme;
				if (DEBUG) {
					System.out.printf("{consumed} %s\n", currentToken.lexeme);
				}
				getToken();
				if (DEBUG) {
					System.out.println("<--expandAddress()");
				}
				return;
			}
		} else if (currentToken.type == StreamTokenizer.TT_NUMBER) {
			// consome prefixo '0'
			getToken();
			try {
				int address = Integer.parseInt(
						currentToken.lexeme.substring(1), 16);
				program[byteCount++] = address;
				if (DEBUG) {
					System.out.printf("{consumed} %s = %02X\n",
							currentToken.lexeme, program[byteCount - 1]);
				}
				getToken();
				if (DEBUG) {
					System.out.println("<--expandAddress()");
				}
				return;
			} catch (NumberFormatException nfe) {
			}
		}
		throw new MASMException(String.format(
				"address expected at line %d: found %s", currentToken.line,
				currentToken.lexeme));
	}

	private boolean isMnemonic(String value) {
		if (DEBUG) {
			System.out.print("isMnemonic(" + value + ")?: ");
		}
		if (!CASE_SENSITIVE) {
			value = value.toUpperCase();
		}
		for (int opCode = 0; opCode < Processor.MNEMONIC.length; opCode++) {
			if (Processor.MNEMONIC[opCode].getMnemonic().equals(value)) {
				lastInstruction = Processor.MNEMONIC[opCode];
				if (DEBUG) {
					System.out.println("true");
				}
				return true;
			}
		}
		if (DEBUG) {
			System.out.println("false");
		}
		return false;
	}

	private int findLineNumberOf(String lexeme) {
		if (DEBUG) {
			System.out.print("findLineNumberOf(" + lexeme + ")?: ");
		}
		int line = -1;
		for (Token token : tokenList) {
			if (token.lexeme == null) {
				continue;
			} else if (token.lexeme.equals(lexeme)) {
				line = token.line;
				break;
			}
		}
		if (DEBUG) {
			System.out.println(line);
		}
		return line;
	}

	private class Token {
		public String lexeme;
		public int type;
		public int value;
		public int line;

		public Token(String lexeme, int type, int value, int line) {
			this.lexeme = lexeme;
			this.type = type;
			this.value = value;
			this.line = line;
		}

		public String toString() {
			String ttype = null;
			switch (type) {
			case StreamTokenizer.TT_EOF:
				ttype = "EOF";
				break;
			case StreamTokenizer.TT_EOL:
				ttype = "EOL";
				break;
			case StreamTokenizer.TT_NUMBER:
				ttype = "NUM";
				break;
			case StreamTokenizer.TT_WORD:
				ttype = "WOR";
				break;
			default:
				ttype = "" + type;
			}
			return String.format("[%-5s | %3s | %3d ]:%03d", lexeme, ttype,
					value, line);
		}
	}
}
