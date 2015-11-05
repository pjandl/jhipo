/*
 * File:		IO.java
 * Classname:	IO
 * 
 * Description:	IO subsystem that manages virtual devices for JHIPOVM architecture.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class IO {
	private HashMap<Integer, Device> deviceMap;

	public IO() {
		deviceMap = new HashMap<Integer,Device>();
	}
	
	public boolean isGUIPresent() {
		boolean guiPresent = false;
		Set<Integer> addressSet = deviceMap.keySet();
		for(Integer address: addressSet) {
			if (deviceMap.get(address).isGUI()) {
				guiPresent = true;
			}
		}
		return guiPresent;
	}

	public void registerDevice(Integer address, Device device) {
		deviceMap.put(address, device);
		if (JHIPOVM.DEBUG) {
			System.out.printf("{0x%02X=%s}%s", address, device.getClass().getName(), Tools.NL);
		}
	}

	public short read(int address) {
		if (address >= ((int) Math.pow(2, Processor.IO_ADDRESS_SIZE) - 1)) {
			throw new RuntimeException(String.format(
					"[IO | error] invalid read address: %02X", address));
		}
		Device device = deviceMap.get(address);
		if (device == null) {
			throw new RuntimeException(String.format(
					"[IO | error] no device found at address: %02X", address));
		}
		System.out.printf("IO[0x%02X| %s | read]%s", address, device.toString(), Tools.NL);
		return device.read();
	}

	public void write(int address, byte data) {
		if (address >= ((int) Math.pow(2, Processor.IO_ADDRESS_SIZE) - 1)) {
			throw new RuntimeException(String.format(
					"[IO | error] invalid write address: %02X]", address));
		}
		Device device = deviceMap.get(address);
		if (device == null) {
			throw new RuntimeException(String.format(
					"[IO | error] no device found at address: %02X]", address));
		}
		System.out.printf("IO[0x%02X| %s | write]%s", address, device.toString(), Tools.NL);
		device.write(data);
	}

	public void shutdown() {
		for(Device device: deviceMap.values()) {
			device.close();
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[IO | Map]");
		sb.append(Tools.NL);
		sb.append(String.format("+------+-------------------------------------------------------------------+%s", Tools.NL));
		sb.append(String.format("| Addr | Device                                                            |%s", Tools.NL));
		sb.append(String.format("+------+-------------------------------------------------------------------+%s", Tools.NL));
		Iterator<Integer> i = deviceMap.keySet().iterator();
		while (i.hasNext()) {
			Integer key = i.next();
			sb.append(String.format("| 0x%02X | %-65s |%s", key,
					deviceMap.get(key), Tools.NL));
		}
		sb.append(String.format("+------+-------------------------------------------------------------------+%s", Tools.NL));
		return sb.toString();
	}

}
