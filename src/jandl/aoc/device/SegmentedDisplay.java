package jandl.aoc.device;

import java.awt.*;
import javax.swing.*;

public class SegmentedDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = false;

	/* Constants */
	private final static int SEGMENT_NUMBER = 7;
	private final static int A = 0;
	private final static int B = 1;
	private final static int C = 2;
	private final static int D = 3;
	private final static int E = 4;
	private final static int F = 5;
	private final static int G = 6;

	// Segment Display OFF = 0 and ON = 1
	private final static byte value[][] = {
			{ 1, 1, 1, 1, 1, 1, 0, },	// zero
			{ 0, 1, 1, 0, 0, 0, 0 },	// one
			{ 1, 1, 0, 1, 1, 0, 1 },	// two
			{ 1, 1, 1, 1, 0, 0, 1 },	// three
			{ 0, 1, 1, 0, 0, 1, 1 },	// four
			{ 1, 0, 1, 1, 0, 1, 1 },	// five
			{ 1, 0, 1, 1, 1, 1, 1 },	// six
			{ 1, 1, 1, 0, 0, 0, 0 },	// seven
			{ 1, 1, 1, 1, 1, 1, 1 },	// eight
			{ 1, 1, 1, 0, 0, 1, 1 },	// nine
			{ 1, 1, 1, 0, 1, 1, 1 },	// ten (A)
			{ 0, 0, 1, 1, 1, 1, 1 },	// eleven (B)
			{ 1, 0, 0, 1, 1, 1, 0 },	// twelve (C)
			{ 0, 1, 1, 1, 1, 0, 1 },	// thirteen (D)
			{ 1, 0, 0, 1, 1, 1, 1 },	// fourteen (E)
			{ 1, 0, 0, 0, 1, 1, 1 },	// fifteen (F)
			{ 0, 0, 0, 0, 0, 0, 0 }		// blank
	};

	private Color displayOffColor;
	private Color displayOnColor;

	private int x;
	private int y;

	private Polygon[] segments;

	private byte displayValue;

	public SegmentedDisplay(Color displayColor) {
		displayOffColor = displayColor.darker()
				.darker().darker().darker();
		displayOnColor = displayColor.brighter()
				.brighter().brighter().brighter().brighter();
		setPreferredSize(new Dimension(110, 170));
		setOpaque(true);
		setBackground(Color.black);
		x = 0;
		y = 0;
		createSegments();
		displayValue = 0;
	}

	public void setDisplayValue(byte displayValue) {
		if (displayValue>-1 && displayValue<16) {
			this.displayValue = displayValue;
		} else {	
			this.displayValue = 16;
		}
	}

	public void doCountUp() {
		int i = 0;
		do {
			setDisplayValue((byte)i);
			repaint();
			i++;
			try {
				Thread.sleep(300);
			} catch (InterruptedException ie) {
				if (DEBUG) {
					System.out.println(ie.getMessage());
				}
			}
		} while (i != 16);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (DEBUG) {
			System.out.println("paintComponent()");
		}
		super.paintComponent(g); // ajusta background color
		for (int i = 0; i < SEGMENT_NUMBER; i++) {
			setSegmentState(g, segments[i], value[displayValue][i]);
		}
	}

	private void createSegments() {
		segments = new Polygon[SEGMENT_NUMBER];

		segments[A] = new Polygon();
		segments[A].addPoint(x + 20, y + 8);
		segments[A].addPoint(x + 90, y + 8);
		segments[A].addPoint(x + 98, y + 15);
		segments[A].addPoint(x + 90, y + 22);
		segments[A].addPoint(x + 20, y + 22);
		segments[A].addPoint(x + 12, y + 15);

		segments[B] = new Polygon();
		segments[B].addPoint(x + 91, y + 23);
		segments[B].addPoint(x + 98, y + 18);
		segments[B].addPoint(x + 105, y + 23);
		segments[B].addPoint(x + 105, y + 81);
		segments[B].addPoint(x + 98, y + 89);
		segments[B].addPoint(x + 91, y + 81);

		segments[C] = new Polygon();
		segments[C].addPoint(x + 91, y + 97);
		segments[C].addPoint(x + 98, y + 89);
		segments[C].addPoint(x + 105, y + 97);
		segments[C].addPoint(x + 105, y + 154);
		segments[C].addPoint(x + 98, y + 159);
		segments[C].addPoint(x + 91, y + 154);

		segments[D] = new Polygon();
		segments[D].addPoint(x + 20, y + 155);
		segments[D].addPoint(x + 90, y + 155);
		segments[D].addPoint(x + 98, y + 162);
		segments[D].addPoint(x + 90, y + 169);
		segments[D].addPoint(x + 20, y + 169);
		segments[D].addPoint(x + 12, y + 162);

		segments[E] = new Polygon();
		segments[E].addPoint(x + 5, y + 97);
		segments[E].addPoint(x + 12, y + 89);
		segments[E].addPoint(x + 19, y + 97);
		segments[E].addPoint(x + 19, y + 154);
		segments[E].addPoint(x + 12, y + 159);
		segments[E].addPoint(x + 5, y + 154);

		segments[F] = new Polygon();
		segments[F].addPoint(x + 5, y + 23);
		segments[F].addPoint(x + 12, y + 18);
		segments[F].addPoint(x + 19, y + 23);
		segments[F].addPoint(x + 19, y + 81);
		segments[F].addPoint(x + 12, y + 89);
		segments[F].addPoint(x + 5, y + 81);

		segments[G] = new Polygon();
		segments[G].addPoint(x + 20, y + 82);
		segments[G].addPoint(x + 90, y + 82);
		segments[G].addPoint(x + 95, y + 89);
		segments[G].addPoint(x + 90, y + 96);
		segments[G].addPoint(x + 20, y + 96);
		segments[G].addPoint(x + 15, y + 89);
	}

	private void setSegmentState(Graphics graphics, Polygon segment, int state) {
		if (state == 0) {
			graphics.setColor(displayOffColor);
		} else {
			graphics.setColor(displayOnColor);
		}
		graphics.fillPolygon(segment);
		graphics.drawPolygon(segment);
	}

	public byte getDisplayValue() {
		return displayValue;
	}

}