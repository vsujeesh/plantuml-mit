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
package net.sourceforge.plantuml.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Stereotype;

public class StyleSignature {

	private final Set<String> names = new LinkedHashSet<String>();

	public StyleSignature(String s) {
		if (s.contains("*") || s.contains("&") || s.contains("-")) {
			throw new IllegalArgumentException();
		}
		this.names.add(s.toLowerCase());
	}

	public static StyleSignature empty() {
		return new StyleSignature();
	}

	private StyleSignature() {
	}

	private StyleSignature(Collection<String> copy) {
		this.names.addAll(copy);
	}

	public StyleSignature addClickable(Url url) {
		if (url == null) {
			return this;
		}
		final Set<String> result = new HashSet<String>(names);
		result.add(SName.clickable.name());
		return new StyleSignature(result);

	}

	public StyleSignature add(String s) {
		if (s == null) {
			return this;
		}
		if (s.contains("*") || s.contains("&") || s.contains("-")) {
			throw new IllegalArgumentException();
		}
		final Set<String> result = new HashSet<String>(names);
		result.add(s.toLowerCase());
		return new StyleSignature(result);
	}

	public StyleSignature addStar() {
		final Set<String> result = new HashSet<String>(names);
		result.add("*");
		return new StyleSignature(result);
	}

	public boolean isStarred() {
		return names.contains("*");
	}

	@Override
	public boolean equals(Object arg) {
		final StyleSignature other = (StyleSignature) arg;
		return this.names.equals(other.names);
	}

	@Override
	public int hashCode() {
		return names.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (String n : names) {
			if (result.length() > 0) {
				result.append('.');
			}
			result.append(n);
		}
		return result.toString();
	}

	public boolean matchAll(StyleSignature other) {
		if (other.isStarred() && names.contains("*") == false) {
			return false;
		}
		for (String token : names) {
			if (token.equals("*")) {
				continue;
			}
			if (other.names.contains(token) == false) {
				return false;
			}
		}
		return true;
	}

	public final Set<String> getNames() {
		return Collections.unmodifiableSet(names);
	}

	public static StyleSignature of(SName... names) {
		final List<String> result = new ArrayList<String>();
		for (SName name : names) {
			result.add(name.name().toLowerCase());
		}
		return new StyleSignature(result);
	}

	public StyleSignature withStereotype(Stereotype stereotype) {
		final List<String> result = new ArrayList<String>(names);
		if (stereotype != null) {
			for (String name : stereotype.getStyleNames()) {
				result.add(name.toLowerCase());
			}
		}
		result.add(SName.stereotype.name().toLowerCase());
		return new StyleSignature(result);
	}

	public StyleSignature with(Stereotype stereotype) {
		final List<String> result = new ArrayList<String>(names);
		if (stereotype != null) {
			for (String name : stereotype.getStyleNames()) {
				result.add(name.toLowerCase());
			}
		}
		return new StyleSignature(result);
	}

	public StyleSignature mergeWith(List<Style> others) {
		final List<String> copy = new ArrayList<String>(names);
		for (Style other : others) {
			for (String s : other.getSignature().getNames()) {
				copy.add(s);
			}
		}
		return new StyleSignature(copy);
	}

	public StyleSignature mergeWith(StyleSignature other) {
		final List<String> copy = new ArrayList<String>(names);
		copy.addAll(other.names);
		return new StyleSignature(copy);
	}

	public Style getMergedStyle(StyleBuilder styleBuilder) {
		if (styleBuilder == null) {
			return null;
		}
		return styleBuilder.getMergedStyle(this);
	}

	public boolean match(Stereotype stereotype) {
		for (String s : stereotype.getMultipleLabels()) {
			if (names.contains(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}