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
package net.sourceforge.plantuml.ugraphic.arc;

import java.awt.geom.PathIterator;


/*
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

/**
 * The <code>ExtendedPathIterator</code> class represents a geometric path constructed from straight lines, quadratic
 * and cubic (Bezier) curves and elliptical arcs. This interface is identical to that of PathIterator except it can
 * return SEG_ARCTO from currentSegment, also the array of values passed to currentSegment must be of length 7 or an
 * error will be thrown.
 * 
 * This does not extend PathIterator as it would break the interface contract for that class.
 * 
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id: ExtendedPathIterator.java 475477 2006-11-15 22:44:28Z cam $
 */
public interface ExtendedPathIterator {

	/**
	 * The segment type constant that specifies that the preceding subpath should be closed by appending a line segment
	 * back to the point corresponding to the most recent SEG_MOVETO.
	 */
	int SEG_CLOSE = PathIterator.SEG_CLOSE;

	/**
	 * The segment type constant for a point that specifies the end point of a line to be drawn from the most recently
	 * specified point.
	 */
	int SEG_MOVETO = PathIterator.SEG_MOVETO;

	/**
	 * The segment type constant for a point that specifies the end point of a line to be drawn from the most recently
	 * specified point.
	 */
	int SEG_LINETO = PathIterator.SEG_LINETO;

	/**
	 * The segment type constant for the pair of points that specify a quadratic parametric curve to be drawn from the
	 * most recently specified point. The curve is interpolated by solving the parametric control equation in the range
	 * (t=[0..1]) using the most recently specified (current) point (CP), the first control point (P1), and the final
	 * interpolated control point (P2).
	 */
	int SEG_QUADTO = PathIterator.SEG_QUADTO;

	/**
	 * The segment type constant for the set of 3 points that specify a cubic parametric curve to be drawn from the most
	 * recently specified point. The curve is interpolated by solving the parametric control equation in the range
	 * (t=[0..1]) using the most recently specified (current) point (CP), the first control point (P1), the second
	 * control point (P2), and the final interpolated control point (P3).
	 */
	int SEG_CUBICTO = PathIterator.SEG_CUBICTO;

	/**
	 * The segment type constant for an elliptical arc. This consists of Seven values [rx, ry, angle, largeArcFlag,
	 * sweepFlag, x, y]. rx, ry are the radious of the ellipse. angle is angle of the x axis of the ellipse.
	 * largeArcFlag is zero if the smaller of the two arcs are to be used. sweepFlag is zero if the 'left' branch is
	 * taken one otherwise. x and y are the destination for the ellipse.
	 */
	int SEG_ARCTO = 4321;

	/**
	 * The winding rule constant for specifying an even-odd rule for determining the interior of a path. The even-odd
	 * rule specifies that a point lies inside the path if a ray drawn in any direction from that point to infinity is
	 * crossed by path segments an odd number of times.
	 */
	int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

	/**
	 * The winding rule constant for specifying a non-zero rule for determining the interior of a path. The non-zero
	 * rule specifies that a point lies inside the path if a ray drawn in any direction from that point to infinity is
	 * crossed by path segments a different number of times in the counter-clockwise direction than the clockwise
	 * direction.
	 */
	int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

	int currentSegment();

	int currentSegment(double[] coords);

	int getWindingRule();

	boolean isDone();

	void next();
}
