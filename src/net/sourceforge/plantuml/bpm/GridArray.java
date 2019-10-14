/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under The MIT License (Massachusetts Institute of Technology License)
 * 
 * See http://opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.bpm;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GridArray implements UDrawable {

	private final int lines;
	private final int cols;
	private final Placeable data[][];
	private final ISkinParam skinParam;

	// private final List<GridEdge> edges = new ArrayList<GridEdge>();

	public GridArray(ISkinParam skinParam, int lines, int cols) {
		this.skinParam = skinParam;
		this.lines = lines;
		this.cols = cols;
		this.data = new Placeable[lines][cols];
	}

	@Override
	public String toString() {
		return "" + lines + "x" + cols;
	}

	public void setData(int l, int c, Placeable element) {
		data[l][c] = element;
	}

	public Placeable getData(int l, int c) {
		return data[l][c];
	}

	public final int getRows() {
		return cols;
	}

	public final int getLines() {
		return lines;
	}

	private double getHeightOfLine(StringBounder stringBounder, int line) {
		double height = 0;
		for (int i = 0; i < cols; i++) {
			final Placeable cell = data[line][i];
			if (cell == null) {
				continue;
			}
			height = Math.max(height, cell.getDimension(stringBounder, skinParam).getHeight());
		}
		return height;
	}

	private double getWidthOfCol(StringBounder stringBounder, int col) {
		double width = 0;
		for (int i = 0; i < lines; i++) {
			final Placeable cell = data[i][col];
			if (cell == null) {
				continue;
			}
			width = Math.max(width, cell.getDimension(stringBounder, skinParam).getWidth());
		}
		return width;
	}

	private final double margin = 30;

	public void drawU(UGraphic ug) {
		// printMe();

		final StringBounder stringBounder = ug.getStringBounder();

		// for (GridEdge edge : edges) {
		// // System.err.println("Drawing " + edge);
		// final int from[] = getCoord(edge.getFrom());
		// final int to[] = getCoord(edge.getTo());
		// final Point2D pt1 = getCenterOf(stringBounder, from[0], from[1]);
		// final Point2D pt2 = getCenterOf(stringBounder, to[0], to[1]);
		// drawArrow(ug, pt1, pt2);
		// }

		double dy = 0;
		drawInternalGrid(ug);
		for (int l = 0; l < lines; l++) {
			double dx = 0;
			final double heightOfLine = getHeightOfLine(stringBounder, l);
			for (int r = 0; r < cols; r++) {
				final double widthOfCol = getWidthOfCol(stringBounder, r);
				final Placeable cell = data[l][r];
				if (cell != null) {
					final Dimension2D dim = cell.getDimension(stringBounder, skinParam);

					cell.toTextBlock(skinParam).drawU(
							ug.apply(new UTranslate(dx + (widthOfCol + margin - dim.getWidth()) / 2, dy
									+ (heightOfLine + margin - dim.getHeight()) / 2)));
				}
				dx += widthOfCol + margin;
			}
			dy += heightOfLine + margin;
		}

	}

	private void drawInternalGrid(UGraphic ug) {
		double heightMax = 0;
		for (int l = 0; l < lines; l++) {
			heightMax += getHeightOfLine(ug.getStringBounder(), l) + margin;
		}
		double widthMax = 0;
		for (int c = 0; c < cols; c++) {
			widthMax += getWidthOfCol(ug.getStringBounder(), c) + margin;
		}
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		double y = 0;
		for (int l = 0; l < lines; l++) {
			ug.apply(new UTranslate(0, y)).draw(new ULine(widthMax, 0));
			y += getHeightOfLine(ug.getStringBounder(), l) + margin;
		}
		double x = 0;
		for (int c = 0; c < cols; c++) {
			ug.apply(new UTranslate(x, 0)).draw(new ULine(0, heightMax));
			x += getWidthOfCol(ug.getStringBounder(), c) + margin;
		}

	}

	private void drawArrow(UGraphic ug, Point2D pt1, Point2D pt2) {
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLUE));
		final ULine line = new ULine(pt2.getX() - pt1.getX(), pt2.getY() - pt1.getY());
		ug.apply(new UTranslate(pt1)).draw(line);
	}

	private Point2D getCenterOf(StringBounder stringBounder, int c, int l) {
		double x = getWidthOfCol(stringBounder, c) / 2 + margin / 2;
		for (int i = 0; i < c; i++) {
			final double widthOfCol = getWidthOfCol(stringBounder, i);
			x += widthOfCol + margin;
		}
		double y = getHeightOfLine(stringBounder, l) / 2 + margin / 2;
		for (int i = 0; i < l; i++) {
			final double heightOfLine = getHeightOfLine(stringBounder, i);
			y += heightOfLine + margin;
		}
		return new Point2D.Double(x, y);
	}

	private int[] getCoord(Cell someCell) {
		for (int l = 0; l < lines; l++) {
			for (int c = 0; c < cols; c++) {
				final Placeable cell = data[l][c];
				if (cell == someCell.getData()) {
					return new int[] { c, l };
				}
			}
		}
		throw new IllegalArgumentException();
	}

	private void printMe() {
		for (int l = 0; l < lines; l++) {
			for (int c = 0; c < cols; c++) {
				final Placeable cell = data[l][c];
				System.err.print(cell);
				System.err.print("  ;  ");
			}
			System.err.println();
		}
	}

	// void addEdgesInternal(List<GridEdge> edges) {
	// this.edges.addAll(edges);
	// }

}
