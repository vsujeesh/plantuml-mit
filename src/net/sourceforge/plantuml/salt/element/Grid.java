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
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.salt.Cell;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Grid {

	private final double[] rowsStart;
	private final double[] colsStart;
	private final TableStrategy strategy;
	private final TextBlock title;

	private final Set<Segment> horizontals = new HashSet<Segment>();
	private final Set<Segment> verticals = new HashSet<Segment>();

	public Grid(double[] rowsStart, double[] colsStart, TableStrategy strategy, TextBlock title) {
		this.title = title;
		this.rowsStart = rowsStart;
		this.colsStart = colsStart;
		this.strategy = strategy;
		if (strategy == TableStrategy.DRAW_OUTSIDE || strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE
				|| strategy == TableStrategy.DRAW_ALL) {
			addOutside();
		}
	}

	private void addOutside() {
		final int nbRow = rowsStart.length;
		final int nbCol = colsStart.length;
		for (int c = 0; c < nbCol - 1; c++) {
			horizontals.add(new Segment(0, c));
			horizontals.add(new Segment(nbRow - 1, c));
		}
		for (int r = 0; r < nbRow - 1; r++) {
			verticals.add(new Segment(r, 0));
			verticals.add(new Segment(r, nbCol - 1));
		}

	}

	public void drawU(UGraphic ug, double x, double y) {
		// Hlines
		for (Segment seg : horizontals) {
			final int row1 = seg.getRow();
			final int col1 = seg.getCol();
			final double width = colsStart[col1 + 1] - colsStart[col1];
			ug.apply(new UTranslate(x + colsStart[col1], y + rowsStart[row1])).draw(new ULine(width, 0));
		}
		// Vlines
		for (Segment seg : verticals) {
			final int row1 = seg.getRow();
			final int col1 = seg.getCol();
			final double height = rowsStart[row1 + 1] - rowsStart[row1];
			ug.apply(new UTranslate(x + colsStart[col1], y + rowsStart[row1])).draw(new ULine(0, height));
		}

		final Dimension2D dim = title.calculateDimension(ug.getStringBounder());

		if (dim.getWidth() > 0 && dim.getHeight() > 0) {
			final UGraphic ug2 = ug.apply(new UTranslate(x + 6, y - dim.getHeight() * 0));
			ug2.apply(new UChangeBackColor(HtmlColorUtils.WHITE)).apply(new UChangeColor(HtmlColorUtils.WHITE))
					.draw(new URectangle(dim));
			title.drawU(ug2);
		}

	}

	public void addCell(Cell cell) {

		if (strategy == TableStrategy.DRAW_NONE || strategy == TableStrategy.DRAW_OUTSIDE
				|| strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE) {
			return;
		}

		if (strategy == TableStrategy.DRAW_HORIZONTAL || strategy == TableStrategy.DRAW_ALL) {
			// Hlines
			for (int c = cell.getMinCol(); c <= cell.getMaxCol(); c++) {
				horizontals.add(new Segment(cell.getMinRow(), c));
				horizontals.add(new Segment(cell.getMaxRow() + 1, c));
			}
		}
		if (strategy == TableStrategy.DRAW_VERTICAL || strategy == TableStrategy.DRAW_ALL) {
			// Vlines
			for (int r = cell.getMinRow(); r <= cell.getMaxRow(); r++) {
				verticals.add(new Segment(r, cell.getMinCol()));
				verticals.add(new Segment(r, cell.getMaxCol() + 1));
			}
		}
	}
}
