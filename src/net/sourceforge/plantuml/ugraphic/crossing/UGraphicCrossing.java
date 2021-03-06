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
package net.sourceforge.plantuml.ugraphic.crossing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cute.Balloon;
import net.sourceforge.plantuml.cute.CrossingSegment;
import net.sourceforge.plantuml.geom.LineSegmentDouble;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class UGraphicCrossing implements UGraphic {

	private final UGraphic ug;
	private final List<Pending> lines;
	private final UTranslate translate;
	
	static class Pending {
		final UGraphic ug;
		final LineSegmentDouble segment;
		final UTranslate translate;

		Pending(UGraphic ug, UTranslate translate, LineSegmentDouble segment) {
			this.ug = ug;
			this.segment = segment;
			this.translate = translate;
		}

		void drawNow(HtmlColor color) {
			if (color == null) {
				segment.draw(ug);
			} else {
				segment.draw(ug.apply(new UChangeColor(color)));
			}
		}

		List<Point2D> getCollisionsWith(List<Pending> others) {
			final List<Point2D> result = new ArrayList<Point2D>();
			for (Pending other : others) {
				if (isClose(segment.getP1(), other.segment.getP1()) || isClose(segment.getP1(), other.segment.getP2())
						|| isClose(segment.getP2(), other.segment.getP1())
						|| isClose(segment.getP2(), other.segment.getP2())) {
					continue;
				}
				final Point2D inter = segment.getSegIntersection(other.segment);
				if (inter != null) {
					result.add(inter);
				}
			}
			return result;
		}
	}

	public UGraphicCrossing(UGraphic ug) {
		this(ug, new UTranslate(), new ArrayList<Pending>());
	}

	private static boolean isClose(Point2D p1, Point2D p2) {
		return p1.distance(p2) < 0.1;
	}

	private UGraphicCrossing(UGraphic ug, UTranslate translate, List<Pending> lines) {
		this.ug = ug;
		this.translate = translate;
		this.lines = lines;
	}

	public StringBounder getStringBounder() {
		return ug.getStringBounder();
	}

	public UParam getParam() {
		return ug.getParam();
	}

	public void draw(UShape shape) {
		if (shape instanceof DotPath) {
			drawDotPath((DotPath) shape);
		} else {
			ug.draw(shape);
		}
	}

	private void drawDotPath(DotPath dotPath) {
		if (dotPath.isLine()) {
			for (LineSegmentDouble seg : dotPath.getLineSegments()) {
				lines.add(new Pending(ug.apply(translate.reverse()), translate, seg.translate(translate)));
			}
		} else {
			ug.draw(dotPath);
		}
	}

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new UGraphicCrossing(ug.apply(change), translate.compose((UTranslate) change), lines);
		} else {
			return new UGraphicCrossing(ug.apply(change), translate, lines);
		}
	}

	public ColorMapper getColorMapper() {
		return ug.getColorMapper();
	}

	public void startUrl(Url url) {
		ug.startUrl(url);
	}

	public void closeAction() {
		ug.closeAction();
	}

	public void flushUg() {
		final List<Pending> pendings = new ArrayList<Pending>();
		final List<Balloon> balloons = new ArrayList<Balloon>();
		for (Pending p : lines) {
			final List<Point2D> tmp = p.getCollisionsWith(lines);
			for (Point2D pt : tmp) {
				balloons.add(new Balloon(pt, 5));
			}
			// if (tmp.size() == 0) {
			// p.drawNow(null);
			// } else {
			// pendings.add(p);
			// }
		}
		for (Balloon b : balloons) {
			b.drawU(ug.apply(new UChangeBackColor(HtmlColorUtils.GREEN)).apply(new UChangeColor(HtmlColorUtils.GREEN)));
		}
		for (Pending p : lines) {
			for (Balloon b : balloons) {
				List<Point2D> pts = new CrossingSegment(b, p.segment).intersection();
				for (Point2D pt : pts) {
					final Balloon s2 = new Balloon(pt, 2);
					s2.drawU(ug.apply(new UChangeBackColor(HtmlColorUtils.BLUE)).apply(new UChangeColor(HtmlColorUtils.BLUE)));
				}
			}
		}
		ug.flushUg();
	}

	public boolean matchesProperty(String propertyName) {
		return ug.matchesProperty(propertyName);
	}

}
