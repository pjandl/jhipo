/*
 * File:		Instruction.java
 * Classname:	Instruction
 * 
 * Description:	model an processor instruction.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

public class Instruction {
	private String mnemonic;
	private byte opCode;
	private boolean multiByte;

	public Instruction(String mnemonic, int opCode, boolean multiByte) {
		this.mnemonic = mnemonic.toUpperCase();
		if (opCode < 0 || opCode > 255) {
			throw new RuntimeException(String.format(
					"[Instruction | Error] invalid opCode 0x%02X", opCode));
		}
		this.opCode = (byte)opCode;
		this.multiByte = multiByte;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public byte getOpCode() {
		return opCode;
	}

	public boolean isMultiByte() {
		return multiByte;
	}

	@Override
	public String toString() {
		return String.format("%4s | 0x%02X | %s", mnemonic, opCode, multiByte ? "mb" : "sb");
	}

}
