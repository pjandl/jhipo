package jandl.aoc.device;

import jandl.aoc.jhipo.Device;
import jandl.aoc.jhipo.Tools;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;

public abstract class SingleLineHexaDisplay extends JFrame implements Device {
	private static final long serialVersionUID = 1L;
	public static int DIGIT_NUMBER = 8;
	private SegmentedDisplay[] digit;

	public SingleLineHexaDisplay(Color displayColor) {
		super("SingleLineHexaDisplay");
		digit = new SegmentedDisplay[DIGIT_NUMBER];
		setLayout(new GridLayout(1, DIGIT_NUMBER));
		for (int i = 0; i < DIGIT_NUMBER; i++) {
			digit[i] = new SegmentedDisplay(displayColor);
			digit[i].setDisplayValue((byte)-1);
			add(digit[i]);
		}
		pack();
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	@Override
	public void close() {
		dispose();
	}
	
	@Override
	public boolean isGUI() {
		return true;
	}

	@Override
	public byte read() {
		throw new UnsupportedOperationException("can't read.");
	}

	@Override
	public void write(byte data) {
		// desloca dois dígitos para direita
		for(int i=DIGIT_NUMBER-1; i>1; i--) {
			digit[i].setDisplayValue(digit[i-2].getDisplayValue());
		}
		System.out.printf("> %02X%s", data, Tools.NL);
		digit[1].setDisplayValue((byte) (data % 16));
		digit[0].setDisplayValue((byte) (data / 16));
	}
	
	@Override
	public void setAddress(Integer address) {
		setTitle(String.format("%s::%02X", getTitle(), address));
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}

}
