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
import java.util.List;

import net.sourceforge.plantuml.geom.LineSegmentDouble;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;

class SmartConnection {

	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	private final List<Box2D> forbidden;

	public SmartConnection(double x1, double y1, double x2, double y2, List<Box2D> forbidden) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.forbidden = forbidden;
	}

	public SmartConnection(Point2D p1, Point2D p2, List<Box2D> b) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), b);
	}

	public void draw(UGraphic ug, HtmlColor color) {
		final LineSegmentDouble seg = new LineSegmentDouble(x1, y1, x2, y2);
		boolean clash = intersect(seg);
		if (clash) {
			ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK)).apply(new UStroke(1.0));
		} else {
			ug = ug.apply(new UChangeColor(color)).apply(new UStroke(1.5));
		}
		seg.draw(ug);
	}

	private boolean intersect(LineSegmentDouble seg) {
		for (Box2D box : forbidden) {
			if (box.doesIntersect(seg)) {
				return true;
			}
		}
		return false;
	}

	public void drawEx1(UGraphic ug, HtmlColor color) {
		ug = ug.apply(new UChangeColor(color)).apply(new UStroke(1.5));
		final double orthoX = -(y2 - y1);
		final double orthoY = x2 - x1;
		for (int i = -10; i <= 10; i++) {
			for (int j = -10; j <= 10; j++) {
				final double d1x = orthoX * i / 10.0;
				final double d1y = orthoY * i / 10.0;
				final double c1x = (x1 + x2) / 2 + d1x;
				final double c1y = (y1 + y2) / 2 + d1y;
				final double d2x = orthoX * j / 10.0;
				final double d2y = orthoY * j / 10.0;
				final double c2x = (x1 + x2) / 2 + d2x;
				final double c2y = (y1 + y2) / 2 + d2y;
				final UPath path = new UPath();
				path.moveTo(x1, y1);
				path.cubicTo(c1x, c1y, c2x, c2y, x2, y2);
				ug.draw(path);
			}
		}
	}

}
