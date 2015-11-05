/*
 * File:		JHIPOVM.java
 * Classname:	JHIPOVM
 * 
 * Description:	that gathers processor, memory and IO subsystem to create the JHIPO Virtual Machine.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

import java.io.IOException;

public class JHIPOVM implements AutoCloseable {
	public static boolean VERBOSE = false;
	public static boolean DEBUG = false;

	private IO io;
	private Memory memory;
	private Processor processor;

	public JHIPOVM() throws Exception {
		io = new IO();
		loadDevices();
		memory = new Memory(256);
		processor = new Processor(memory, io);
	}

	public void loadDevices() throws Exception {
		Tools.loadDevices(io, "jhipovm-config.xml");
	}

	public void loadMemory(String fileName) throws Exception {
		Tools.loadMemory(memory, fileName);
	}

	public void run() throws Exception {
		Processor.debug = DEBUG;
		processor.start();
	}

	public Memory getMemory() {
		return memory;
	}

	public IO getIO() {
		return io;
	}

	public Processor getProcessor() {
		return processor;
	}

	@Override
	public void close() throws Exception {
		if (io.isGUIPresent()) {
			System.out.println("<Press ENTER to shutdown JHIPOVM>");
			try {
				System.in.read();
			} catch (IOException e) {
			}
		}
		io.shutdown();
	}
}
