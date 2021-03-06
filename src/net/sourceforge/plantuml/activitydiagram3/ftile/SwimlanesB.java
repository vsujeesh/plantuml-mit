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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class SwimlanesB extends SwimlanesA {

	public SwimlanesB(ISkinParam skinParam, Pragma pragma) {
		super(skinParam, pragma);
	}

	@Override
	protected void drawWhenSwimlanes(UGraphic ug, TextBlock full) {
		super.drawWhenSwimlanes(ug, full);
		double x2 = 0;

		final StringBounder stringBounder = ug.getStringBounder();

		HtmlColor color = skinParam.getHtmlColor(ColorParam.swimlaneTitleBackground, null, false);
		if (SkinParam.USE_STYLES()) {
			color = getStyle().value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());
		}
		if (color != null) {
			final double titleHeight = getTitlesHeight(stringBounder);
			final URectangle back = new URectangle(getTitlesWidth(stringBounder), titleHeight);
			back.setIgnoreForCompression(true);
			ug.apply(new UChangeBackColor(color)).apply(new UChangeColor(color)).draw(back);
		}
		for (Swimlane swimlane : swimlanes) {
			final TextBlock swTitle = getTitle(swimlane);
			final double titleWidth = swTitle.calculateDimension(stringBounder).getWidth();
			final double posTitle = x2 + (swimlane.getActualWidth() - titleWidth) / 2;
			swTitle.drawU(ug.apply(new UTranslate(posTitle, 0)));
			x2 += swimlane.getActualWidth();
		}
	}

	private double getTitlesWidth(StringBounder stringBounder) {
		double x2 = 0;
		for (Swimlane swimlane : swimlanes) {
			x2 += swimlane.getActualWidth();
		}
		return x2;
	}

	private TextBlock getTitle(Swimlane swimlane) {
		final HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
		FontConfiguration fontConfiguration = new FontConfiguration(skinParam, FontParam.SWIMLANE_TITLE, null);
		if (SkinParam.USE_STYLES()) {
			fontConfiguration = getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
		}

		LineBreakStrategy wrap = getWrap();
		if (wrap.isAuto()) {
			wrap = new LineBreakStrategy("" + ((int) swimlane.getActualWidth()));
		}

		return swimlane.getDisplay().create(fontConfiguration, horizontalAlignment, skinParam, wrap);
	}

	private LineBreakStrategy getWrap() {
		LineBreakStrategy wrap = skinParam.swimlaneWrapTitleWidth();
		if (wrap == LineBreakStrategy.NONE) {
			wrap = skinParam.wrapWidth();
		}
		return wrap;
	}

	@Override
	protected double swimlaneActualWidth(StringBounder stringBounder, double swimlaneWidth, Swimlane swimlane) {
		final double m1 = super.swimlaneActualWidth(stringBounder, swimlaneWidth, swimlane);
		if (getWrap().isAuto()) {
			return m1;
		}

		final double titleWidth = getTitle(swimlane).calculateDimension(stringBounder).getWidth();
		return MathUtils.max(m1, titleWidth + 2 * separationMargin);

	}

	@Override
	protected UTranslate getTitleHeightTranslate(final StringBounder stringBounder) {
		double titlesHeight = getTitlesHeight(stringBounder);
		return new UTranslate(0, titlesHeight > 0 ? titlesHeight + 5 : 0);
	}

	private double getTitlesHeight(StringBounder stringBounder) {
		double titlesHeight = 0;
		for (Swimlane swimlane : swimlanes) {
			final TextBlock swTitle = getTitle(swimlane);
			titlesHeight = Math.max(titlesHeight, swTitle.calculateDimension(stringBounder).getHeight());
		}
		return titlesHeight;
	}

}
