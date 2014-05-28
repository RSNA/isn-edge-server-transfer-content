/* Copyright (c) <2014>, <Radiological Society of North America>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the <RSNA> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.rsna.isn.transfercontent.dcm;


import java.io.File;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;

import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.util.Environment;
import org.rsna.isn.util.FormatText;

/**
 *
 * @author Clifton Li
 * @version 3.2.0
 * @since 3.2.0
 */
public class ReportToDicom {
       
    private static final Logger logger = Logger.getLogger(ReportToDicom.class);
    
    public static void generate(Exam exam, DicomStudy study,File reportSeriesDir, String reportSeriesUID,int seriesNumber)
    {

            FopFactory fopFactory = FopFactory.newInstance();

            
            DicomRenderer dicomRenderer = new DicomRenderer(exam, study,reportSeriesDir,reportSeriesUID,seriesNumber);
            
            try 
            {
                  FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
                  dicomRenderer.setUserAgent(foUserAgent);

                  Fop fop = fopFactory.newFop(MimeConstants.MIME_PNG,foUserAgent);

                  // Setup JAXP using identity transformer
                  TransformerFactory factory = TransformerFactory.newInstance();

                  File confDir = Environment.getConfDir();
                  Source src = new StreamSource(new File(confDir,"report-stylesheet.xslt"));

                  Transformer transformer = factory.newTransformer(src);


                  Source xml = new StreamSource(new StringReader(FormatText.addReportTag(exam.getReport())));
                  
                  // Resulting SAX events (the generated FO) must be piped through to FOP
                  Result result = new SAXResult(fop.getDefaultHandler());

                  // Start XSLT transformation and FOP processing
                  transformer.transform(xml, result);
            }
            catch (Exception ex)
            {
                    logger.error("Error creating dicom report", ex);
            } 
    }
}
