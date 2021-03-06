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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Balloon implements UDrawable {

	private final Point2D center;
	private final double radius;

	public Balloon(Point2D center, double radius) {
		if (radius < 0) {
			throw new IllegalArgumentException();
		}
		this.center = center;
		this.radius = radius;
	}

	public static Balloon fromRadiusSegment(Segment centerToContact) {
		throw new UnsupportedOperationException();
	}

	public Point2D getPointOnCircle(double a) {
		return new Point2D.Double(center.getX() + radius * Math.cos(a), center.getY() + radius * Math.sin(a));
	}

	public Segment getSegmentCenterToPointOnCircle(double a) {
		return new Segment(center, getPointOnCircle(a));
	}

	public Balloon translate(UTranslate translate) {
		return new Balloon(translate.getTranslated(center), radius);
	}

	public Balloon rotate(RotationZoom rotationZoom) {
		return new Balloon(rotationZoom.getPoint(center), rotationZoom.applyZoom(radius));
	}

	@Override
	public String toString() {
		return "Balloon(" + center + "," + radius + ")";
	}

	public Point2D getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public void drawU(UGraphic ug) {
		UEllipse circle = new UEllipse(2 * radius, 2 * radius);
		ug.apply(new UTranslate(center.getX() - circle.getWidth() / 2, center.getY() - circle.getHeight() / 2)).draw(
				circle);
	}

	public Balloon getInsideTangentBalloon1(double angle, double curvation) {
		final double f = radius - curvation;
		final double e = (radius * radius - f * f) / 2 / radius;
		final RotationZoom rotation = RotationZoom.rotationInRadians(angle);
		final Point2D p1 = rotation.getPoint(f, e);
		final Point2D newCenter = new Point2D.Double(center.getX() + p1.getX(), center.getY() + p1.getY());
		return new Balloon(newCenter, e);
	}

	public Balloon getInsideTangentBalloon2(double angle, double curvation) {
		final double f = radius - curvation;
		final double e = (radius * radius - f * f) / 2 / radius;
		final RotationZoom rotation = RotationZoom.rotationInRadians(angle);
		final Point2D p1 = rotation.getPoint(f, -e);
		final Point2D newCenter = new Point2D.Double(center.getX() + p1.getX(), center.getY() + p1.getY());
		return new Balloon(newCenter, e);
	}

	public Point2D getPointOnCirclePassingByThisPoint(Point2D passingBy) {
		final Segment seg = new Segment(center, passingBy);
		return seg.getFromAtoB(radius);
	}

	public Point2D getPointOnCircleOppositeToThisPoint(Point2D passingBy) {
		final Segment seg = new Segment(center, passingBy);
		return seg.getFromAtoB(-radius);
	}

}
