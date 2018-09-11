/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 ${owner}
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.clover.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class StaxParser {

    private final XMLInputFactory xmlFactory;

    /**
     * Stax parser for a given stream handler and iso control chars set awarness to on.
     * The iso control chars in the xml file will be replaced by simple spaces, usefull for
     * potentially bogus XML files to parse, this has a small perfs overhead so use it only when necessary
     */
    public StaxParser() {
        xmlFactory = getXmlInputFactory();
    }

    private XMLInputFactory getXmlInputFactory() {
        WstxInputFactory inputFactory = new WstxInputFactory();
        inputFactory.configureForLowMemUsage();
        inputFactory.getConfig().setUndeclaredEntityResolver(new UndeclaredEntitiesXMLResolver());

        inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        return inputFactory;
    }

    public void parse(File xmlFile, XmlStreamHandler streamHandler) throws XMLStreamException {
        try (FileInputStream input = new FileInputStream(xmlFile)) {
            parse(input, streamHandler);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void parse(InputStream xmlInput, XmlStreamHandler streamHandler) throws XMLStreamException {
        SMInputFactory inf = new SMInputFactory(xmlFactory);
        parse(inf.rootElementCursor(xmlInput), streamHandler);
    }

    public void parse(Reader xmlReader, XmlStreamHandler streamHandler) throws XMLStreamException {
        SMInputFactory inf = new SMInputFactory(xmlFactory);
        parse(inf.rootElementCursor(xmlReader), streamHandler);
    }

    public void parse(URL xmlUrl, XmlStreamHandler streamHandler) throws XMLStreamException {
        try {
            parse(xmlUrl.openStream(), streamHandler);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void parse(SMHierarchicCursor rootCursor, XmlStreamHandler streamHandler) throws XMLStreamException {
        try {
            streamHandler.stream(rootCursor);
        } finally {
            rootCursor.getStreamReader().closeCompletely();
        }
    }

    private static class UndeclaredEntitiesXMLResolver implements XMLResolver {
        @Override
        public Object resolveEntity(String arg0, String arg1, String fileName, String undeclaredEntity) throws XMLStreamException {
            // avoid problems with XML docs containing undeclared entities.. return the entity under its raw form if not an unicode expression
            if (StringUtils.startsWithIgnoreCase(undeclaredEntity, "u") && undeclaredEntity.length() == 5) {
                int unicodeCharHexValue = Integer.parseInt(undeclaredEntity.substring(1), 16);
                if (Character.isDefined(unicodeCharHexValue)) {
                    undeclaredEntity = new String(new char[]{(char) unicodeCharHexValue});
                }
            }
            return undeclaredEntity;
        }
    }

    /**
     * Simple interface for handling XML stream to parse
     */
    public interface XmlStreamHandler {
        void stream(SMHierarchicCursor rootCursor) throws XMLStreamException;
    }

}
