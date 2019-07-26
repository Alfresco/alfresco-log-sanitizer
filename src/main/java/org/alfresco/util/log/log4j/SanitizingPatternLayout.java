/*
 * #%L
 * Alfresco Log Sanitizer
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.util.log.log4j;

import java.util.regex.Pattern;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A pattern layout that allows you to sanitize a logging event.
 */
public class SanitizingPatternLayout extends PatternLayout
{
    /**
     * The regular expression that will match the CR/LF.
     */
    private static final Pattern PATTERN_CRLF = Pattern.compile("(\\r|\\n)");

    /**
     * Returns PatternParser used to parse the conversion string.
     */
    @Override
    protected PatternParser createPatternParser(String pattern)
    {
        return new PatternParser(pattern)
        {
            @Override
            protected void finalizeConverter(char c)
            {
                switch (c)
                {
                case 'm':
                    PatternConverter pc = new PatternConverter(formattingInfo)
                    {
                        @Override
                        protected String convert(LoggingEvent event)
                        {
                            return sanitize(event.getRenderedMessage());
                        }
                    };

                    currentLiteral.setLength(0);

                    addConverter(pc);
                    break;
                default:
                    super.finalizeConverter(c);
                    break;
                }
            }
        };
    }

    private String sanitize(String message)
    {
        if (message != null)
        {
            return PATTERN_CRLF.matcher(message).replaceAll("");
        }

        return null;
    }

}
