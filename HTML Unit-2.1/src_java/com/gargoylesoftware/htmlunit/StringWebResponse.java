/*
 * Copyright (c) 2002-2008 Gargoyle Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment:
 *
 *       "This product includes software developed by Gargoyle Software Inc.
 *        (http://www.GargoyleSoftware.com/)."
 *
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 4. The name "Gargoyle Software" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact info@GargoyleSoftware.com.
 * 5. Products derived from this software may not be called "HtmlUnit", nor may
 *    "HtmlUnit" appear in their name, without prior written permission of
 *    Gargoyle Software Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GARGOYLE
 * SOFTWARE INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gargoylesoftware.htmlunit;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;

/**
 * A simple WebResponse created from a string. Content is assumed to be
 * of type text/html.
 *
 * @version $Revision: 2862 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Marc Guillemot
 * @author Brad Clarke
 * @author Ahmed Ashour
 */
public class StringWebResponse extends WebResponseImpl {

    private static final long serialVersionUID = 8001886227379566491L;

    /**
     * Helper method for constructors. Converts the passed string into
     * WebResponseData with other defaults specified.
     *
     * @param contentString String to be converted to WebResponseData
     * @return a simple WebResponseData with defaults specified
     */
    private static WebResponseData getWebResponseData(final String contentString, final String charset) {
        final byte[] content = TextUtil.stringToByteArray(contentString, charset);
        final List<NameValuePair> compiledHeaders = new ArrayList<NameValuePair>();
        compiledHeaders.add(new NameValuePair("Content-Type", "text/html"));
        return new WebResponseData(content, HttpStatus.SC_OK, "OK", compiledHeaders);
    }

    /**
     * Creates an instance associated with an originating URL.
     * @param content the content to return
     * @param originatingURL the URL that this should be associated with
     */
    public StringWebResponse(final String content, final URL originatingURL) {
        super(getWebResponseData(content, TextUtil.DEFAULT_CHARSET), originatingURL, SubmitMethod.GET, 0);
    }

    /**
     * Creates an instance associated with an originating URL.
     * @param content the content to return
     * @param charset the charset used to convert the content
     * @param originatingURL the URL that this should be associated with
     */
    public StringWebResponse(final String content, final String charset, final URL originatingURL) {
        super(getWebResponseData(content, charset), charset, originatingURL, SubmitMethod.GET, 0);
    }
}
