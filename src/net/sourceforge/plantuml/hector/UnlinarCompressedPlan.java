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
package net.sourceforge.plantuml.hector;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.geom.LineSegmentDouble;
import net.sourceforge.plantuml.hector.UnlinearCompression.Rounding;

class UnlinarCompressedPlan {

	private final UnlinearCompression compX;
	private final UnlinearCompression compY;

	public UnlinarCompressedPlan(double inner, double outer) {
		this(inner, outer, inner, outer);
	}

	public UnlinarCompressedPlan(double innerx, double outerx, double innery, double outery) {
		this.compX = new UnlinearCompression(innerx, outerx);
		this.compY = new UnlinearCompression(innery, outery);
	}

	public double getInnerX() {
		return compX.innerSize();
	}

	public double getInnerY() {
		return compY.innerSize();
	}

	public HectorPath uncompressSegmentSimple(Point2D pp1, Point2D pp2) {
		final HectorPath result = new HectorPath();
		result.add(new LineSegmentDouble(uncompress(pp1, UnlinearCompression.Rounding.CENTRAL), uncompress(pp2,
				UnlinearCompression.Rounding.CENTRAL)));
		return result;
	}

	public HectorPath uncompressSegment(Point2D pp1, Point2D pp2) {
		double x1 = pp1.getX();
		double y1 = pp1.getY();
		final double x2 = pp2.getX();
		final double y2 = pp2.getY();
		final HectorPath result = new HectorPath();
		final double y[] = compY.encounteredSingularities(y1, y2);
		if (y.length == 0 || x1 == x2) {
			result.add(new LineSegmentDouble(uncompress(pp1, UnlinearCompression.Rounding.CENTRAL), uncompress(pp2,
					UnlinearCompression.Rounding.CENTRAL)));
			return result;
		}
		System.err.println("len=" + y.length);
		final LineSegmentDouble segment = new LineSegmentDouble(pp1, pp2);
		for (int i = 0; i < y.length; i++) {
			final double x = segment.getIntersectionHorizontal(y[i]);
			final Rounding r = i == 0 ? UnlinearCompression.Rounding.CENTRAL : UnlinearCompression.Rounding.BORDER_2;
			result.add(uncompress(x1, y1, r), uncompress(x, y[i], UnlinearCompression.Rounding.BORDER_1));
			x1 = x;
			y1 = y[i];
		}
		result.add(uncompress(x1, y1, UnlinearCompression.Rounding.BORDER_2), uncompress(x2, y2,
				UnlinearCompression.Rounding.CENTRAL));
		return result;

	}

	public HectorPath uncompress(LineSegmentDouble segment) {
		double x1 = segment.getX1();
		double y1 = segment.getY1();
		final double x2 = segment.getX2();
		final double y2 = segment.getY2();
		final HectorPath result = new HectorPath();
		final double x[] = compX.encounteredSingularities(x1, x2);
		if (x.length == 0) {
			result.add(getUncompressedSegment(x1, y1, x2, y2, UnlinearCompression.Rounding.BORDER_2));
			return result;
		}
		for (int i = 0; i < x.length; i++) {
			final double y = segment.getIntersectionVertical(x[i]);
			result.add(getUncompressedSegment(x1, y1, x[i], y, UnlinearCompression.Rounding.BORDER_2));
			x1 = x[i];
			y1 = y;
		}
		result.add(getUncompressedSegment(x1, y1, x2, y2, UnlinearCompression.Rounding.BORDER_2));
		return result;
	}

	public Point2D uncompress(Point2D pt, UnlinearCompression.Rounding rounding) {
		return uncompress(pt.getX(), pt.getY(), rounding);
	}

	public Point2D uncompress(double x, double y, UnlinearCompression.Rounding rounding) {
		return new Point2D.Double(compX.uncompress(x, rounding), compY.uncompress(y, rounding));
	}

	private LineSegmentDouble getUncompressedSegment(final double x1, final double y1, final double x2,
			final double y2, UnlinearCompression.Rounding rounding) {
		final LineSegmentDouble un1 = new LineSegmentDouble(compX.uncompress(x1, rounding), compY.uncompress(y1,
				rounding), compX.uncompress(x2, rounding), compY.uncompress(y2, rounding));
		return un1;
	}

	// private LineSegmentDouble getUncompressedSegmentRoundBefore(final double
	// x1, final double y1, final double x2,
	// final double y2) {
	// final LineSegmentDouble un1 = new LineSegmentDouble(compX.uncompress(x1),
	// compY.uncompress(y1),
	// compX.uncompress(x2) - compX.innerSize(), compY.uncompress(y2));
	// return un1;
	// }

}
