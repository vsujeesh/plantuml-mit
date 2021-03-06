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
package net.sourceforge.plantuml.xmi;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.LongCode;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.utils.UniqueSequence;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract class XmiClassDiagramAbstract implements IXmiClassDiagram {

	// https://www.ibm.com/developerworks/library/x-wxxm24/
	// http://pierre.ree7.fr/blog/?p=5

	protected final ClassDiagram classDiagram;
	protected final Document document;
	protected Element ownedElement;

	protected final Set<IEntity> done = new HashSet<IEntity>();

	public XmiClassDiagramAbstract(ClassDiagram classDiagram) throws ParserConfigurationException {
		this.classDiagram = classDiagram;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder builder = factory.newDocumentBuilder();
		this.document = builder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		final Element xmi = document.createElement("XMI");
		xmi.setAttribute("xmi.version", "1.1");
		xmi.setAttribute("xmlns:UML", "href://org.omg/UML/1.3");
		document.appendChild(xmi);

		final Element header = document.createElement("XMI.header");
		xmi.appendChild(header);

		final Element metamodel = document.createElement("XMI.metamodel");
		metamodel.setAttribute("xmi.name", "UML");
		metamodel.setAttribute("xmi.version", "1.3");
		header.appendChild(metamodel);

		final Element content = document.createElement("XMI.content");
		xmi.appendChild(content);

		// <UML:Model xmi.id="UMLModel.4" name="Design Model"
		// visibility="public" isSpecification="false" isRoot="false"
		// isLeaf="false" isAbstract="false">
		final Element model = document.createElement("UML:Model");
		model.setAttribute("xmi.id", CucaDiagramXmiMaker.getModel(classDiagram));
		model.setAttribute("name", "PlantUML");
		content.appendChild(model);

		// <UML:Namespace.ownedElement>
		this.ownedElement = document.createElement("UML:Namespace.ownedElement");
		model.appendChild(ownedElement);

	}

	final protected String forXMI(String s) {
		return s.replace(':', ' ');
	}

	final protected String forXMI(Display s) {
		if (Display.isNull(s)) {
			return "";
		}
		return s.get(0).toString().replace(':', ' ');
	}

	final public void transformerXml(OutputStream os) throws TransformerException, ParserConfigurationException {
		final Source source = new DOMSource(document);

		final Result resultat = new StreamResult(os);

		final TransformerFactory fabrique = TransformerFactory.newInstance();
		final Transformer transformer = fabrique.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		// tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, resultat);
	}

	final protected Element createEntityNode(IEntity entity) {
		// <UML:Class xmi.id="UMLClass.5" name="Class1" visibility="public"
		// isSpecification="false"
		// namespace="UMLModel.4" isRoot="false" isLeaf="false"
		// isAbstract="false" participant="UMLAssociationEnd.11"
		// isActive="false">
		final Element cla = document.createElement("UML:Class");
		if (entity.getLeafType() == LeafType.NOTE) {
			return null;
		}

		cla.setAttribute("xmi.id", entity.getUid());
		cla.setAttribute("name", entity.getDisplay().get(0).toString());
		final LongCode parentCode = entity.getParentContainer().getLongCode();
		if (parentCode == null) {
			cla.setAttribute("namespace", CucaDiagramXmiMaker.getModel(classDiagram));
		} else {
			cla.setAttribute("namespace", parentCode.getFullName());
		}

		final Stereotype stereotype = entity.getStereotype();
		if (stereotype != null) {
			final Element stereo = document.createElement("UML:ModelElement.stereotype");
			for (String s : stereotype.getMultipleLabels()) {
				final Element name = document.createElement("UML:Stereotype");
				name.setAttribute("name", s);
				stereo.appendChild(name);
			}
			cla.appendChild(stereo);
		}

		final LeafType type = entity.getLeafType();
		if (type == LeafType.ABSTRACT_CLASS) {
			cla.setAttribute("isAbstract", "true");
		} else if (type == LeafType.INTERFACE) {
			cla.setAttribute("isInterface", "true");
		}

		final Element feature = document.createElement("UML:Classifier.feature");
		cla.appendChild(feature);

		for (Member m : entity.getBodier().getFieldsToDisplay()) {
			// <UML:Attribute xmi.id="UMLAttribute.6" name="Attribute1"
			// visibility="public" isSpecification="false"
			// ownerScope="instance" changeability="changeable"
			// targetScope="instance" type="" owner="UMLClass.5"/>
			final Element attribute = document.createElement("UML:Attribute");
			attribute.setAttribute("xmi.id", "att" + UniqueSequence.getValue());
			attribute.setAttribute("name", m.getDisplay(false));
			final VisibilityModifier visibility = m.getVisibilityModifier();
			if (visibility != null) {
				attribute.setAttribute("visibility", visibility.getXmiVisibility());
			}
			feature.appendChild(attribute);
		}

		for (Member m : entity.getBodier().getMethodsToDisplay()) {
			// <UML:Operation xmi.id="UMLOperation.7" name="Operation1"
			// visibility="public" isSpecification="false"
			// ownerScope="instance" isQuery="false" concurrency="sequential"
			// isRoot="false" isLeaf="false"
			// isAbstract="false" specification="" owner="UMLClass.5"/>
			final Element operation = document.createElement("UML:Operation");
			operation.setAttribute("xmi.id", "att" + UniqueSequence.getValue());
			operation.setAttribute("name", m.getDisplay(false));
			final VisibilityModifier visibility = m.getVisibilityModifier();
			if (visibility != null) {
				operation.setAttribute("visibility", visibility.getXmiVisibility());
			}
			feature.appendChild(operation);
		}
		return cla;
	}

}
