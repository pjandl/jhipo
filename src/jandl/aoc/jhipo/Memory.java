/*
 * File:		Memory.java
 * Classname:	Memory
 * 
 * Description:	Memory subsystem that storage data for JHIPOVM architecture.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

public class Memory {
	public static int ROWS = 16;
	public static int COLS = 16;
	private byte bank[];

	public Memory(int size) throws Exception {
		if ((int) Math.pow(2, Processor.MEMORY_ADDRESS_SIZE) < size) {
			throw new Exception(String.format(
					"[Memory | error] invalid memory size: %d > %d", size,
					((int) Math.pow(2, Processor.MEMORY_ADDRESS_SIZE) - 1)));
		}
		if (ROWS * COLS != size) {
			throw new Exception(String.format(
					"[Memory | error] invalid ROWS*COLS: %d,%d", ROWS, COLS));
		}
		bank = new byte[size];
	}

	public short read(int address) throws RuntimeException {
		try {
			return bank[address];
		} catch (ArrayIndexOutOfBoundsException aie) {
			throw new RuntimeException(String.format(
					"[Memory | error] invalid read address: %02X]", address), aie);
		}
	}

	public void write(int address, byte data) throws RuntimeException {
		try {
			bank[address] = data;
		} catch (ArrayIndexOutOfBoundsException aie) {
			throw new RuntimeException(String.format(
					"[Memory | error ] invalid write address: %02X]", address),
					aie);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Memory | Map]");
		sb.append(Tools.NL);
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				sb.append(String.format("%02X", bank[ROWS * c + r]));
				sb.append("  ");
			}
			sb.append(Tools.NL);
		}
		return sb.toString();
	}
}
