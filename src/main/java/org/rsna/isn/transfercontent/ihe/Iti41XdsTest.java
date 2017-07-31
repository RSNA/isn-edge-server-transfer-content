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
 * 
 */

package org.rsna.isn.transfercontent.ihe;

import org.dcm4che2.data.UID;
import org.dcm4che2.util.UIDUtils;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.document.XDSDocumentFromByteArray;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;
import org.rsna.isn.domain.DicomObject;
import org.rsna.isn.domain.DicomSeries;
import org.rsna.isn.domain.DicomStudy;

/**
 * This class extends ITI-41 class with 
 * modifications to support the XdsTest class. 
 * This class supports the ability to send DICOM 
 * objects in memory instead of a file.
 *
 * @author Clifton Li
 * @version 5.0.0
 * @since 3.2.0
 */
public class Iti41XdsTest extends Iti41
{
        private final String singleUsePatientId;

        public Iti41XdsTest(DicomStudy study,String singleUsePatientId) 
        {
                super(study);               
                
                this.singleUsePatientId = singleUsePatientId;
        }
        
	/**
	 * Perform the actual submission to the document repository.
	 *
	 */
	public void submitTestDocuments(byte[] dcm) throws Exception
	{
		SubmitTransactionData tx = new SubmitTransactionData();
                
		// Add entries for images

                XDSDocument dcmDoc = new XDSDocumentFromByteArray(DocumentDescriptor.DICOM, dcm);
                String dcmUuid = tx.addDocument(dcmDoc);
                DocumentEntryType dcmEntry = tx.getDocumentEntry(dcmUuid);
                initDocEntry(dcmEntry);

                DicomObject obj = new DicomObject();
                obj.setSopClassUid(UID.SecondaryCaptureImageStorage);
                obj.setSopInstanceUid(UIDUtils.createUID());  
                
                DicomSeries series = new DicomSeries();
                series.getObjects().put(obj.getSopInstanceUid(), obj);
                
                docEntryImages(dcmEntry,series);

                submitTransaction(tx,null,"image");
        }
}
