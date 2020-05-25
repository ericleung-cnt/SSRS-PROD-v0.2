package org.mardep.ssrs.vitaldoc;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public interface IVitalDocClient {

	int INDEX_LEFT = 1;
	int INDEX_RIGHT = 2;
	int INDEX_FRONT = 3;
	int INDEX_SIDE = 4;

	String CSR_DOC_CERT = "Cert, etc.";
	String CSR_DOC_APP_FORM = "Application / Form 2";
	String CSR_DOC_CSR = "CSR";

	/**
	 * @param key
	 * @param index
	 * @param fileContent
	 * @param filename
	 * @throws IOException
	 */
	void uploadSeafarerImage(String key, int index, byte[] fileContent, String filename) throws IOException;
	byte[] download(String key, int indexLeft) throws IOException;
	byte[] download(String docType, Map<String, String> parameter) throws IOException;
	byte[] download(String docType, Map<String, String> parameter, boolean bmp) throws IOException;

	void uploadDemandNote(String dnNo, byte[] content) throws IOException;
	void uploadEbsTranscript(String imo, String shipName, String offNo, byte[] content) throws IOException;
	byte[] downloadDemandNote(String dnNo) throws IOException;
	void uploadSrSupporting(String imoNo, String offNo, String type, byte[] content) throws IOException;
	/**
	 * @param docName docName
	 * @param name Name of Ship
	 * @param imo IMO Number
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadCos(String docName, String name, String imo, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param name Ship name
	 * @param offNo Official Number
	 * @param mortgagee Name (Mortgagee Name)
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadMortgage(String docName, String name, String offNo, String mortgagee, byte[] pdf) throws IOException;
	/**
	 * @param listOrChange 0 for list, 1 for change
	 * @param formYear
	 * @param docName docName
	 * @param name Name of Ship
	 * @param offNo Official Number
	 * @param imo IMO number
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadCrewForm(int listOrChange, int formYear, String docName, String name, String offNo, String imo, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param crNo CR No.
	 * @param coName Company Name
	 * @param placeOfIncorp Place of incorporation
	 * @param addr Address
	 * @param checkDate Check Date
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadCompanySearch(String docName, String crNo, String coName, String placeOfIncorp, String addr, Date checkDate, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param name Vessel name
	 * @param offNo Official Number
	 * @param imo IMO Number
	 * @param forDate forDate
	 * @param applicant Applicant Name
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadRequestTranscript(String docName, String name, String offNo, String imo, Date forDate, String applicant, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param imo IMO Number
	 * @param name Ship Name
	 * @param pdf
	 * @return docId
	 * @throws IOException
	 */
	long uploadCsr(String docName, String imo, String name, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param name Ship Name
	 * @param imo IMO Number
	 * @param owner Name of Owner
	 * @param demise Name of Demise
	 * @param rp Name of RP
	 * @param pdf
	 * @return
	 * @throws IOException
	 */
	long uploadShipRegApp(String docName, String name, String imo, String owner, String demise, String rp, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param applicant Applicant Name
	 * @param owner Owner Name
	 * @param pdf
	 * @return
	 * @throws IOException
	 */
	long uploadShipNameReservation(String docName, String applicant, String owner, byte[] pdf) throws IOException;
	/**
	 * @param docName docName
	 * @param name Ship Name
	 * @param offNo Official Number
	 * @param owner Full Name
	 * @param pdf
	 * @return
	 * @throws IOException
	 */
	long uploadTransferDecl(String docName, String name, String offNo, String owner, byte[] pdf) throws IOException;
	void uploadCsrDoc(String imoNo, String type, byte[] content) throws IOException;
	byte[] downloadCsr(String type, String imoNo) throws IOException;

}
