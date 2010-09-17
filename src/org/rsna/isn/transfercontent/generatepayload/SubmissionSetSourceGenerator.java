/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatepayload;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.UUID;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.generatedocument.*;/**
 *
 * @author erackus
 */
public class SubmissionSetSourceGenerator {
    /**
     * Our goal is to create a DOM XML tree and then print the XML.
     */

    public SubmissionSetSourceGenerator() {
    }

    public static void CreateSubmissionSet(int jobID, String destination) {
        try {
            /////////////////////////////
            //Creating an empty XML Document
            Text text;

            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();

            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);

            ////////////////////////
            //Creating the XML tree

            //create the root element and add it to the document
            //Element root = doc.createElement("xdsb:ProvideAndRegisterDocumentSetRequest ");
            Element root = doc.createElement("tns:SubmissionSet");
            doc.appendChild(root);
            // set default namespace:
            root.setAttribute("xmlns:hl7v2", "urn:org:openhealthtools:ihe:common:hl7v2");
            root.setAttribute("xmlns:p0","htt//www.w3.org/XML/1998/namespace");
            root.setAttribute("xmlns:tns","urn:org:openhealthtools:ihe:xds:metadata");
            root.setAttribute("xmlns:xdsdemo","htt//istc.rsna.org/xdsdemo");
            root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:schemaLocation","urn:org:openhealthtools:ihe:xds:metadata metadata.xsd urn:org:openhealthtools:ihe:common:hl7v2 ../../../org.openhealthtools.ihe.common.hl7v2/resources/schema/hl7v2_wrapper.xsd htt//www.w3.org/XML/1998/namespace htt//www.w3.org/2001/xml.xsd ");

            //create a comment and put it in the root element
//            Comment comment = doc.createComment("Just a thought");
//            root.appendChild(comment);

            //create child element, add an attribute, and add to root
            Element author = doc.createElement("author");
            root.appendChild(author);

            Element authorInstitution = doc.createElement("authorInstitution");
            author.appendChild(authorInstitution);

            Element organizationName = doc.createElement("organizationName");
            authorInstitution.appendChild(organizationName);
            text = doc.createTextNode("University Hospital");
            organizationName.appendChild(text);

            Element authorPerson = doc.createElement("authorPerson");
            author.appendChild(authorPerson);

            String signer = ssQueryData.getSigner();
            String[] signerInfo = SplitDoctorName.find(signer);
            String signerIDNumber = signerInfo[1];
            String signerLastName = signerInfo[2];
            String signerFirstName = signerInfo[3];

            Element idNumber = doc.createElement("idNumber");
            authorPerson.appendChild(idNumber);
            text = doc.createTextNode(signerIDNumber);
            idNumber.appendChild(text);

            Element familyName = doc.createElement("familyName");
            authorPerson.appendChild(familyName);
            text = doc.createTextNode(signerLastName);
            familyName.appendChild(text);

            Element givenName = doc.createElement("givenName");
            authorPerson.appendChild(givenName);
            text = doc.createTextNode(signerFirstName);
            givenName.appendChild(text);

            Element assigningAuthorityUniversalId = doc.createElement("assigningAuthorityUniversalId");
            authorPerson.appendChild(assigningAuthorityUniversalId);
            text = doc.createTextNode("2.16.840.1.113883.3.933");
            assigningAuthorityUniversalId.appendChild(text);

            Element assigningAuthorityUniversalIdType = doc.createElement("assigningAuthorityUniversalIdType");
            authorPerson.appendChild(assigningAuthorityUniversalIdType);
            text = doc.createTextNode("ISO");
            assigningAuthorityUniversalIdType.appendChild(text);

            Element contentTypeCode = doc.createElement("contentTypeCode");
            root.appendChild(contentTypeCode);

            Element code = doc.createElement("code");
            contentTypeCode.appendChild(code);
            text = doc.createTextNode("Radiology Exam");
            code.appendChild(text);

            Element displayName = doc.createElement("displayName");
            contentTypeCode.appendChild(displayName);

            Element localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Radiology Exam");

            Element schemeName = doc.createElement("schemeName");
            contentTypeCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon contentTypeCodes");
            schemeName.appendChild(text);

            Element patientId = doc.createElement("patientId");
            root.appendChild(patientId);

            idNumber = doc.createElement("idNumber");
            patientId.appendChild(idNumber);
            text = doc.createTextNode(ssQueryData.getPmrn());
            idNumber.appendChild(text);

            assigningAuthorityUniversalId = doc.createElement("assigningAuthorityUniversalId");
            patientId.appendChild(assigningAuthorityUniversalId);
            text = doc.createTextNode("1.3.6.1.4.1.21367.2009.1.2.300");
            assigningAuthorityUniversalId.appendChild(text);

            assigningAuthorityUniversalIdType = doc.createElement("assigningAuthorityUniversalIdType");
            patientId.appendChild(assigningAuthorityUniversalIdType);
            text = doc.createTextNode("ISO");
            assigningAuthorityUniversalIdType.appendChild(text);

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            File outFile = new File(destination + "\\SubmissionSetSource.xml");

//            StreamResult result = new StreamResult(sw);
            StreamResult result = new StreamResult(outFile);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            result = new StreamResult(sw);
            trans.transform(source, result);
            String xmlString = sw.toString();

            //print xml
            System.out.println("Here's the xml:\n\n" + xmlString);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
