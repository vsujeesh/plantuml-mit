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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class FtileKilled extends AbstractFtile {

	private final Ftile tile;

	public FtileKilled(Ftile tileToKill) {
		super(tileToKill.skinParam());
		this.tile = tileToKill;
	}
	
	@Override
	public Collection<Ftile> getMyChildren() {
		return tile.getMyChildren();
	}

	public Set<Swimlane> getSwimlanes() {
		return tile.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return tile.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return tile.getSwimlaneOut();
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final FtileGeometry geo = tile.calculateDimension(stringBounder);
		return new FtileGeometry(tile.calculateDimension(stringBounder), geo.getLeft(), geo.getInY());
	}

	public void drawU(UGraphic ug) {
		ug.draw(tile);
	}

}
