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
package net.sourceforge.plantuml.sequencediagram;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;

public class Reference extends AbstractEvent implements Event {

	private final List<Participant> participants;
	private final Url url;
	private final HtmlColor backColorGeneral;
	private final HtmlColor backColorElement;

	private final Display strings;

	final private Style style;
	final private Style styleHeader;

	public StyleSignature getDefaultStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.sequenceDiagram, SName.reference);
	}

	private StyleSignature getHeaderStyleDefinition() {
		return StyleSignature.of(SName.root, SName.element, SName.sequenceDiagram, SName.referenceHeader);
	}

	public Style[] getUsedStyles() {
		return new Style[] {
				style,
				styleHeader == null ? styleHeader : styleHeader.eventuallyOverride(PName.BackGroundColor,
						backColorElement) };
	}

	public Reference(List<Participant> participants, Url url, Display strings, HtmlColor backColorGeneral,
			HtmlColor backColorElement, StyleBuilder styleBuilder) {
		this.participants = participants;
		this.url = url;
		this.strings = strings;
		this.backColorGeneral = backColorGeneral;
		this.backColorElement = backColorElement;
		this.style = getDefaultStyleDefinition().getMergedStyle(styleBuilder);
		this.styleHeader = getHeaderStyleDefinition().getMergedStyle(styleBuilder);
	}

	public List<Participant> getParticipant() {
		return Collections.unmodifiableList(participants);
	}

	public Display getStrings() {
		return strings;
	}

	public boolean dealWith(Participant someone) {
		return participants.contains(someone);
	}

	public final Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return url != null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<Participant> it = participants.iterator(); it.hasNext();) {
			sb.append(it.next().getCode());
			if (it.hasNext()) {
				sb.append("-");
			}

		}
		return sb.toString();
	}

	public final HtmlColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public final HtmlColor getBackColorElement() {
		return backColorElement;
	}
}
