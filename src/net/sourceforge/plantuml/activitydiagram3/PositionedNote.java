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
package net.sourceforge.plantuml.activitydiagram3;

import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;

public class PositionedNote {

	private final Display display;
	private final NotePosition notePosition;
	private final NoteType type;
	private final Colors colors;
	private final Swimlane swimlaneNote;

	public PositionedNote(Display display, NotePosition position, NoteType type, Colors colors, Swimlane swimlaneNote) {
		this.display = display;
		this.notePosition = position;
		this.type = type;
		this.colors = colors;
		this.swimlaneNote = swimlaneNote;
	}

	@Override
	public String toString() {
		return "type=" + type + " notePosition=" + notePosition + " " + display;
	}

	public PositionedNote(Display note, NotePosition position, NoteType type, Swimlane swimlaneNote) {
		this(note, position, type, null, swimlaneNote);
	}

	public Display getDisplay() {
		return display;
	}

	public NotePosition getNotePosition() {
		return notePosition;
	}

	public NoteType getType() {
		return type;
	}

	public Colors getColors() {
		return colors;
	}

	public final Swimlane getSwimlaneNote() {
		return swimlaneNote;
	}

}
