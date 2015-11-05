package jandl.aoc.device;

import javax.swing.JFrame;
import javax.swing.JSlider;

import jandl.aoc.jhipo.Device;

public class SliderInputControl extends JFrame implements Device {
	private static final long serialVersionUID = 1L;
	private JSlider slider;

	public SliderInputControl() {
		super("SliderInputControl");
		slider = new JSlider(JSlider.VERTICAL, 0, 255, 5);
		slider.setMajorTickSpacing(32);
		slider.setMinorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setPaintTicks(false);
		slider.setPaintTrack(true);
		slider.setSnapToTicks(true);
		add("Center", slider);
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
		return (byte)slider.getValue();
	}

	@Override
	public void write(byte data) {
		throw new UnsupportedOperationException("can't write.");
	}
	
	@Override
	public void setAddress(Integer address) {
//		System.out.println(String.format("%s::%02X", getTitle(), address));
		setTitle(String.format("%s::%02X", getTitle(), address));
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	public static void main(String[] a) {
		@SuppressWarnings("resource")
		SliderInputControl sic = new SliderInputControl();
		sic.setDefaultCloseOperation(EXIT_ON_CLOSE);
		sic.setResizable(true);
		sic.setVisible(true);
	}

}
