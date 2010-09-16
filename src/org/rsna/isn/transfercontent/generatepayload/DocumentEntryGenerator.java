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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.generatedocument.*;

/**
 *
 * @author erackus
 */

public class DocumentEntryGenerator {
    private static Format formatter;

    public DocumentEntryGenerator() {
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
            Element root = doc.createElement("tns:DocumentEntry");
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

            Element classCode = doc.createElement("classCode");
            root.appendChild(classCode);

            Element code = doc.createElement("code");
            classCode.appendChild(code);
            text = doc.createTextNode("Radiology Exam");
            code.appendChild(text);

            Element displayName = doc.createElement("displayName");
            classCode.appendChild(displayName);

            Element localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Radiology Exam");

            Element schemeName = doc.createElement("schemeName");
            classCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon classCodes");
            schemeName.appendChild(text);

            Element confidentialityCode = doc.createElement("confidentialityCode");
            root.appendChild(classCode);

            code = doc.createElement("code");
            confidentialityCode.appendChild(code);
            text = doc.createTextNode("N");
            code.appendChild(text);

            displayName = doc.createElement("displayName");
            confidentialityCode.appendChild(displayName);

            localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Normal");

            schemeName = doc.createElement("schemeName");
            confidentialityCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon confidentialityCodes");
            schemeName.appendChild(text);

            Date date = new Date();
            formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String cDate = formatter.format(date);

            Element creationTime = doc.createElement("creationTime");
            root.appendChild(creationTime);
            text = doc.createTextNode(cDate);
            creationTime.appendChild(text);

            Element formatCode = doc.createElement("formatCode");
            root.appendChild(formatCode);

            code = doc.createElement("code");
            formatCode.appendChild(code);
            text = doc.createTextNode("CDAR2/IHE 1.0");
            code.appendChild(text);

            displayName = doc.createElement("displayName");
            formatCode.appendChild(displayName);

            localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "CDAR2/IHE 1.0");

            schemeName = doc.createElement("schemeName");
            formatCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon formatCodes");
            schemeName.appendChild(text);

            Element healthCareFacilityTypeCode = doc.createElement("healthCareFacilityTypeCode");
            root.appendChild(healthCareFacilityTypeCode);

            code = doc.createElement("code");
            healthCareFacilityTypeCode.appendChild(code);
            text = doc.createTextNode("Outpatient");
            code.appendChild(text);

            displayName = doc.createElement("displayName");
            healthCareFacilityTypeCode.appendChild(displayName);

            localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Outpatient");

            schemeName = doc.createElement("schemeName");
            healthCareFacilityTypeCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon healthcareFacilityTypeCodes");
            schemeName.appendChild(text);

            Element languageCode = doc.createElement("languageCode");
            root.appendChild(languageCode);
            text = doc.createTextNode("en-us");
            languageCode.appendChild(text);

            Element patientId = doc.createElement("patientId");
            root.appendChild(patientId);

            idNumber = doc.createElement("idNumber");
            patientId.appendChild(idNumber);
            text = doc.createTextNode("patient_id");
            idNumber.appendChild(text);

            assigningAuthorityUniversalId = doc.createElement("assigningAuthorityUniversalId");
            patientId.appendChild(assigningAuthorityUniversalId);
            text = doc.createTextNode("1.2");
            assigningAuthorityUniversalId.appendChild(text);

            assigningAuthorityUniversalIdType = doc.createElement("assigningAuthorityUniversalIdType");
            patientId.appendChild(assigningAuthorityUniversalIdType);
            text = doc.createTextNode("ISO");
            assigningAuthorityUniversalIdType.appendChild(text);

            Element practiceSettingCode = doc.createElement("practiceSettingCode");
            root.appendChild(practiceSettingCode);

            code = doc.createElement("code");
            practiceSettingCode.appendChild(code);
            text = doc.createTextNode("Radiology");
            code.appendChild(text);

            displayName = doc.createElement("displayName");
            practiceSettingCode.appendChild(displayName);

            localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Radiology");

            schemeName = doc.createElement("schemeName");
            practiceSettingCode.appendChild(schemeName);
            text = doc.createTextNode("Connect-a-thon practiceSettingCodes");
            schemeName.appendChild(text);

            Element sourcePatientId = doc.createElement("sourcePatientId");
            root.appendChild(sourcePatientId);

            idNumber = doc.createElement("idNumber");
            sourcePatientId.appendChild(idNumber);
            text = doc.createTextNode("patient_id");
            idNumber.appendChild(text);

            assigningAuthorityUniversalId = doc.createElement("assigningAuthorityUniversalId");
            sourcePatientId.appendChild(assigningAuthorityUniversalId);
            text = doc.createTextNode("1.2");
            assigningAuthorityUniversalId.appendChild(text);

            assigningAuthorityUniversalIdType = doc.createElement("assigningAuthorityUniversalIdType");
            sourcePatientId.appendChild(assigningAuthorityUniversalIdType);
            text = doc.createTextNode("ISO");
            assigningAuthorityUniversalIdType.appendChild(text);

            Element sourcePatientInfo = doc.createElement("sourcePatientInfo");
            root.appendChild(sourcePatientInfo);

            Element patientIdentifier = doc.createElement("patientIdentifier");
            sourcePatientInfo.appendChild(patientIdentifier);

            idNumber = doc.createElement("idNumber");
            patientIdentifier.appendChild(idNumber);
            text = doc.createTextNode("patient_id");
            idNumber.appendChild(text);

            assigningAuthorityUniversalId = doc.createElement("assigningAuthorityUniversalId");
            patientIdentifier.appendChild(assigningAuthorityUniversalId);
            text = doc.createTextNode("1.2");
            assigningAuthorityUniversalId.appendChild(text);

            assigningAuthorityUniversalIdType = doc.createElement("assigningAuthorityUniversalIdType");
            patientIdentifier.appendChild(assigningAuthorityUniversalIdType);
            text = doc.createTextNode("ISO");
            assigningAuthorityUniversalIdType.appendChild(text);

            Element patientName = doc.createElement("patientName");
            sourcePatientInfo.appendChild(patientName);

            String pName = ssQueryData.getPatientname();
            String[] pInfo = SplitPatientName.find(pName);
            String patientLastName = pInfo[1];
            String patientFirstName = pInfo[2];

            familyName = doc.createElement("familyName");
            patientName.appendChild(familyName);
            text = doc.createTextNode(patientLastName);
            familyName.appendChild(text);

            givenName = doc.createElement("givenName");
            patientName.appendChild(givenName);
            text = doc.createTextNode(patientFirstName);
            givenName.appendChild(text);

            Element patientDateOfBirth = doc.createElement("patientDateOfBirth");
            sourcePatientInfo.appendChild(patientDateOfBirth);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
            java.sql.Timestamp dateofBirth = ssQueryData.getDob();
            String dob = dateFormatter.format(dateofBirth);
            text = doc.createTextNode(dob);
            patientDateOfBirth.appendChild(text);

            Element patientSex = doc.createElement("patientSex");
            sourcePatientInfo.appendChild(patientSex);
            text = doc.createTextNode(ssQueryData.getSex());
            patientSex.appendChild(text);

            Element patientAddress = doc.createElement("patientAddress");
            sourcePatientInfo.appendChild(patientAddress);
            Element streetAddress = doc.createElement("streetAddress");
            patientAddress.appendChild(streetAddress);
            String fullAddress = ssQueryData.getStreet() + " " + ssQueryData.getCity() + " " + ssQueryData.getState() + " " + ssQueryData.getZip_code();
            text = doc.createTextNode(fullAddress);
            streetAddress.appendChild(text);

            Element typeCode = doc.createElement("typeCode");
            root.appendChild(practiceSettingCode);

            code = doc.createElement("code");
            typeCode.appendChild(code);
            text = doc.createTextNode("11488-4");
            code.appendChild(text);

            displayName = doc.createElement("displayName");
            typeCode.appendChild(displayName);

            localizedString = doc.createElement("LocalizedString");
            displayName.appendChild(localizedString);
            localizedString.setAttribute("value", "Consultation Note");

            schemeName = doc.createElement("schemeName");
            typeCode.appendChild(schemeName);
            text = doc.createTextNode("LOINC");
            schemeName.appendChild(text);

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            File outFile = new File(destination + "\\DocumentEntry.xml");

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
            System.out.println("Document Entry Generator: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
