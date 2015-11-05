/*
 * File:		Device.java
 * interface:	Device
 * 
 * Description:	interface to define virtual device for JHIPO.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

public interface Device extends AutoCloseable {
	void close();
	boolean isGUI();
	byte read();
	void setAddress(Integer address);
	void write(byte data);
}
