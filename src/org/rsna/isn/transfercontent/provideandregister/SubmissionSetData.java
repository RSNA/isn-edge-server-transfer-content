/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.provideandregister;

/**
 *
 * @author foyesanya
 */
public class SubmissionSetData {

    public SubmissionSetData(){

    }

private String path  ;
private String patientname ;
private String fullname ;
private String filename;
private String givenname ;
private String familyname ;
private String patientid ;
private String assigningauthority ;
private String assigningauthorityOID ;
private String authorID;
private String authorFamilyName;
private String authorGivenName;
private String authorAssigningAuthorityOID;

private String institutionname ;
private String documentid ;
private String title ;
private String date ;
private String time ;
private String street  ;
private String city ;
private String state ;
private String zip ;
private String country ;
private String sex   ;
private String birthdate ;
private String uuid ;
private String uid1 ;
private String uid2 ;
private String uid3 ;
private String uid4 ;
private String pdf ;
private String docEntrySourceFileName ;
private String docEntryFileName  ;
private String docEntrySourceToDocEntryFileName ;
private String submissionSetSourceFileName ;


    public SubmissionSetData(String path, String patientname, String fullname, String givenname, String familyname, String patientid, String assigningauthority, String assigningauthorityOID, String authorID, String authorFamilyName, String authorGivenName, String authorAssigningAuthorityOID, String institutionname, String documentid, String title, String date, String time, String street, String city, String state, String zip, String country, String sex, String birthdate, String uuid, String uid1, String uid2, String uid3, String uid4, String pdf) {
        this.path = path;
        this.patientname = patientname;
        this.fullname = fullname;
        this.givenname = givenname;
        this.familyname = familyname;
        this.patientid = patientid;
        this.assigningauthority = assigningauthority;
        this.assigningauthorityOID = assigningauthorityOID;
        this.authorID = authorID;
        this.authorFamilyName = authorFamilyName;
        this.authorGivenName = authorGivenName;
        this.institutionname = institutionname;
        this.documentid = documentid;
        this.title = title;
        this.date = date;
        this.time = time;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.sex = sex;
        this.birthdate = birthdate;
        this.uuid = uuid;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.uid3 = uid3;
        this.uid4 = uid4;
        this.pdf = pdf;
    }

    public SubmissionSetData(String kosfilename) {
        this.filename = kosfilename;
    }



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKosfilename() {
        return filename;
    }

    public void setKosfilename(String kosfilename) {
        this.filename = kosfilename;
    }

    public SubmissionSetData(String docEntrySourceFileName, String docEntryFileName, String docEntrySourceToDocEntryFileName, String submissionSetSourceFileName, String submissionSetSourceToSubmissionSetFileName, String organizationalOID, String saveMetadataToFile) {
        this.docEntrySourceFileName = docEntrySourceFileName;
        this.docEntryFileName = docEntryFileName;
        this.docEntrySourceToDocEntryFileName = docEntrySourceToDocEntryFileName;
        this.submissionSetSourceFileName = submissionSetSourceFileName;
        this.submissionSetSourceToSubmissionSetFileName = submissionSetSourceToSubmissionSetFileName;
        this.organizationalOID = organizationalOID;
        this.saveMetadataToFile = saveMetadataToFile;
    }

    public String getDocEntryFileName() {
        return docEntryFileName;
    }

    public void setDocEntryFileName(String docEntryFileName) {
        this.docEntryFileName = docEntryFileName;
    }

    public String getDocEntrySourceFileName() {
        return docEntrySourceFileName;
    }

    public void setDocEntrySourceFileName(String docEntrySourceFileName) {
        this.docEntrySourceFileName = docEntrySourceFileName;
    }

    public String getDocEntrySourceToDocEntryFileName() {
        return docEntrySourceToDocEntryFileName;
    }

    public void setDocEntrySourceToDocEntryFileName(String docEntrySourceToDocEntryFileName) {
        this.docEntrySourceToDocEntryFileName = docEntrySourceToDocEntryFileName;
    }

    public String getOrganizationalOID() {
        return organizationalOID;
    }

    public void setOrganizationalOID(String organizationalOID) {
        this.organizationalOID = organizationalOID;
    }

    public String getSaveMetadataToFile() {
        return saveMetadataToFile;
    }

    public void setSaveMetadataToFile(String saveMetadataToFile) {
        this.saveMetadataToFile = saveMetadataToFile;
    }

    public String getSubmissionSetSourceFileName() {
        return submissionSetSourceFileName;
    }

    public void setSubmissionSetSourceFileName(String submissionSetSourceFileName) {
        this.submissionSetSourceFileName = submissionSetSourceFileName;
    }

    public String getSubmissionSetSourceToSubmissionSetFileName() {
        return submissionSetSourceToSubmissionSetFileName;
    }

    public void setSubmissionSetSourceToSubmissionSetFileName(String submissionSetSourceToSubmissionSetFileName) {
        this.submissionSetSourceToSubmissionSetFileName = submissionSetSourceToSubmissionSetFileName;
    }

    public String getDocxslpath() {
        return docxslpath;
    }

    public void setDocxslpath(String docxslpath) {
        this.docxslpath = docxslpath;
    }

    public String getSubmissionSetFileName() {
        return submissionSetFileName;
    }

    public void setSubmissionSetFileName(String submissionSetFileName) {
        this.submissionSetFileName = submissionSetFileName;
    }


private String submissionSetFileName ;
private String submissionSetSourceToSubmissionSetFileName ;
private String organizationalOID ;
private String saveMetadataToFile ;
private String docxslpath  ;


    public String getAssigningauthority() {
        return assigningauthority;
    }

    public void setAssigningauthority(String assigningauthority) {
        this.assigningauthority = assigningauthority;
    }

    public String getAssigningauthorityOID() {
        return assigningauthorityOID;
    }

    public void setAssigningauthorityOID(String assigningauthorityOID) {
        this.assigningauthorityOID = assigningauthorityOID;
    }

    public String getAuthorAssigningAuthorityOID() {
        return authorAssigningAuthorityOID;
    }

    public void setAuthorAssigningAuthorityOID(String authorAssigningAuthorityOID) {
        this.authorAssigningAuthorityOID = authorAssigningAuthorityOID;
    }

    public String getAuthorFamilyName() {
        return authorFamilyName;
    }

    public void setAuthorFamilyName(String authorFamilyName) {
        this.authorFamilyName = authorFamilyName;
    }

    public String getAuthorGivenName() {
        return authorGivenName;
    }

    public void setAuthorGivenName(String authorGivenName) {
        this.authorGivenName = authorGivenName;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getInstitutionname() {
        return institutionname;
    }

    public void setInstitutionname(String institutionname) {
        this.institutionname = institutionname;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public String getUid3() {
        return uid3;
    }

    public void setUid3(String uid3) {
        this.uid3 = uid3;
    }

    public String getUid4() {
        return uid4;
    }

    public void setUid4(String uid4) {
        this.uid4 = uid4;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }




}
