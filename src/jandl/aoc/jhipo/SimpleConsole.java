/*
 * File:		SimpleConsole.java
 * Classname:	SimpleConsole
 * 
 * Description:	Default virtual device that allows basic I/O operations like a simple text console.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

import java.util.Scanner;

public class SimpleConsole implements Device {
	private Scanner teclado = new Scanner(System.in);

	@Override
	public void close() {
		teclado.close();
	}
	
	@Override
	public boolean isGUI() {
		return false;
	}

	@Override
	public byte read() {
		System.out.printf("> ?\b");
		byte data = (byte)(teclado.nextShort(16) % 0xFF);
		teclado.nextLine();
		return data;
	}

	@Override
	public void setAddress(Integer address) {
		return;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	@Override
	public void write(byte data) {
		System.out.printf("> %02X%s", data, Tools.NL);
	}

}
