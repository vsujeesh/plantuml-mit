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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class USymbolFolder extends USymbol {

	private final static int marginTitleX1 = 3;
	private final static int marginTitleX2 = 3;
	private final static int marginTitleX3 = 7;
	private final static int marginTitleY0 = 0;
	private final static int marginTitleY1 = 3;
	private final static int marginTitleY2 = 3;

	private final SkinParameter skinParameter;
	private final boolean showTitle;

	public USymbolFolder(SkinParameter skinParameter, boolean showTitle) {
		this.skinParameter = skinParameter;
		this.showTitle = showTitle;
	}

	@Override
	public SkinParameter getSkinParameter() {
		return skinParameter;
	}

	private void drawFolder(UGraphic ug, double width, double height, Dimension2D dimTitle, boolean shadowing,
			double roundCorner) {

		final double wtitle;
		if (dimTitle.getWidth() == 0) {
			wtitle = Math.max(30, width / 4);
		} else {
			wtitle = dimTitle.getWidth() + marginTitleX1 + marginTitleX2;
		}
		final double htitle = getHTitle(dimTitle);

		final Shadowable shape;
		if (roundCorner == 0) {
			final UPolygon poly = new UPolygon();
			poly.addPoint(0, 0);
			poly.addPoint(wtitle, 0);

			poly.addPoint(wtitle + marginTitleX3, htitle);
			poly.addPoint(width, htitle);
			poly.addPoint(width, height);
			poly.addPoint(0, height);
			poly.addPoint(0, 0);
			shape = poly;
		} else {
			final UPath path = new UPath();
			path.moveTo(roundCorner / 2, 0);
			path.lineTo(wtitle - roundCorner / 2, 0);
			// path.lineTo(wtitle, roundCorner / 2);
			path.arcTo(new Point2D.Double(wtitle, roundCorner / 2), roundCorner / 2 * 1.5, 0, 1);
			path.lineTo(wtitle + marginTitleX3, htitle);
			path.lineTo(width - roundCorner / 2, htitle);
			path.arcTo(new Point2D.Double(width, htitle + roundCorner / 2), roundCorner / 2, 0, 1);
			path.lineTo(width, height - roundCorner / 2);
			path.arcTo(new Point2D.Double(width - roundCorner / 2, height), roundCorner / 2, 0, 1);
			path.lineTo(roundCorner / 2, height);
			path.arcTo(new Point2D.Double(0, height - roundCorner / 2), roundCorner / 2, 0, 1);
			path.lineTo(0, roundCorner / 2);
			path.arcTo(new Point2D.Double(roundCorner / 2, 0), roundCorner / 2, 0, 1);
			path.closePath();
			shape = path;
		}
		if (shadowing) {
			shape.setDeltaShadow(3.0);
		}
		ug.draw(shape);
		ug.apply(new UTranslate(0, htitle)).draw(new ULine(wtitle + marginTitleX3, 0));
	}

	private double getHTitle(Dimension2D dimTitle) {
		final double htitle;
		if (dimTitle.getWidth() == 0) {
			htitle = 10;
		} else {
			htitle = dimTitle.getHeight() + marginTitleY1 + marginTitleY2;
		}
		return htitle;
	}

	private Margin getMargin() {
		return new Margin(10, 10 + 10, 10 + 3, 10);
	}

	@Override
	public TextBlock asSmall(final TextBlock name, final TextBlock label, final TextBlock stereotype,
			final SymbolContext symbolContext, final HorizontalAlignment stereoAlignment) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final Dimension2D dim = calculateDimension(ug.getStringBounder());
				ug = UGraphicStencil.create(ug, getRectangleStencil(dim), new UStroke());
				ug = symbolContext.apply(ug);
				final Dimension2D dimName = showTitle ? name.calculateDimension(ug.getStringBounder())
						: new Dimension2DDouble(40, 15);
				drawFolder(ug, dim.getWidth(), dim.getHeight(), dimName, symbolContext.isShadowing(),
						symbolContext.getRoundCorner());
				final Margin margin = getMargin();
				final TextBlock tb = TextBlockUtils.mergeTB(stereotype, label, HorizontalAlignment.CENTER);
				if (showTitle) {
					name.drawU(ug.apply(new UTranslate(4, 3)));
				}
				tb.drawU(ug.apply(new UTranslate(margin.getX1(), margin.getY1() + dimName.getHeight())));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final Dimension2D dimName = name.calculateDimension(stringBounder);
				final Dimension2D dimLabel = label.calculateDimension(stringBounder);
				final Dimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				return getMargin().addDimension(Dimension2DDouble.mergeTB(dimName, dimStereo, dimLabel));
			}
		};
	}

	@Override
	public TextBlock asBig(final TextBlock title, HorizontalAlignment labelAlignment, final TextBlock stereotype,
			final double width, final double height, final SymbolContext symbolContext, final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final StringBounder stringBounder = ug.getStringBounder();
				final Dimension2D dim = calculateDimension(stringBounder);
				ug = symbolContext.apply(ug);
				final Dimension2D dimTitle = title.calculateDimension(stringBounder);
				drawFolder(ug, dim.getWidth(), dim.getHeight(), dimTitle, symbolContext.isShadowing(),
						symbolContext.getRoundCorner());
				title.drawU(ug.apply(new UTranslate(4, 2)));
				final Dimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final double posStereo = (width - dimStereo.getWidth()) / 2;

				stereotype.drawU(ug.apply(new UTranslate(4 + posStereo, 2 + getHTitle(dimTitle))));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(width, height);
			}

		};
	}

	@Override
	public boolean manageHorizontalLine() {
		return true;
	}

}