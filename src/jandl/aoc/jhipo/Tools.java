/*
 * File:		Tools.java
 * Classname:	Tools
 * 
 * Description:	Helper class with subsystem interconnections.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Tools {
	public static String NL = System.getProperty("line.separator");

	public static void loadMemory(Memory memory, byte[] program) {
		for (int add = 0; add < program.length; add++) {
			memory.write(add, program[add]);
		}
	}

	public static void loadMemory(Memory memory, String fileName) throws IOException, RuntimeException {
		byte programBuffer[] = new byte[Memory.COLS * Memory.ROWS];
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			if (lineCount == programBuffer.length) {
				br.close();
				throw new RuntimeException(String.format("[Tools | error] buffer overflow: %d]", lineCount));
			}
			int pos = line.startsWith("0x") ? 2 : 0;
			programBuffer[lineCount] = (byte)Short.parseShort(line.substring(pos).trim(), 16);
			lineCount++;
		}
		br.close();
		loadMemory(memory, programBuffer);
	}

	public static void loadDevices(IO io, String fileName) throws Exception {
		Properties config = new Properties();
		int i = 0;
		String sClassName = null, sAddress = null;
		try {
			config.loadFromXML(new FileInputStream(fileName));
			while ((sClassName = config.getProperty("device" + i)) != null) {
				sAddress = config.getProperty("address" + i);
				Integer address = Integer.parseInt(sAddress, 16);
				Device device = (Device) Class.forName(sClassName).newInstance();
				device.setAddress(address);
				io.registerDevice(address, device);
				i++;
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException(
					String.format("invalid device address (%s=%s)%s", sAddress, sClassName, Tools.NL));
		} catch (FileNotFoundException e) {
			System.out.printf("[JHIPOVM | warning] io configuration file not found, using default device.%s", Tools.NL);
			io.registerDevice(0x10, new SimpleConsole());
			config.setProperty("device0", SimpleConsole.class.getName());
			config.setProperty("address0", "10");
			config.storeToXML(new FileOutputStream(fileName), "JHIPOVM I/O device list [auto-generated]");
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("device configuration (%s=%s)%s%s", sAddress, sClassName, Tools.NL, e.getMessage()));
		}
	}

}
