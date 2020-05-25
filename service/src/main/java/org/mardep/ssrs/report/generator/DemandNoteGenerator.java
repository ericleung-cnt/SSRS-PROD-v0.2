package org.mardep.ssrs.report.generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.poi.util.IOUtils;
import org.mardep.dns.common.DemandNote;
import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PushbuttonField;

@Service("demandNoteGenerator11")
@Transactional
public class DemandNoteGenerator extends AbstractReportGenerator implements IDemandNoteGenerator{

	private static final String dnsDateFormat = "yyyy-MM-dd";

	protected final static String PRINT_COPY_CAPTION = "printCopyCaption";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	IFeeCodeDao feeCodeDao;

	@Override
	public String getReportFileName() {
		return "Seafarer_Registration_Report.jrxml"; // TODO DemandNote PDF template ??
	}
	public byte[] generate(String demandNoteNo) throws Exception {
		return generate(demandNoteNo, false);
	}
	@Override
	public byte[] generate(String demandNoteNo, boolean autopay) throws Exception {
		Map<String, Object> inputParam = new HashMap<String, Object>();
		inputParam.put("demandNoteNo", demandNoteNo);
		inputParam.put("autopay", autopay);
		return generate(inputParam);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		String demandNoteNo = (String)inputParam.get("demandNoteNo");

		final String classpath = "server/report/template/";
		InputStream source = new ClassPathResource(classpath+"MDDNS - New DN temp for SSRS- v0.2.pdf").getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		DemandNoteHeader dnHeader = demandNoteHeaderDao.findById(demandNoteNo);
		List<DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
		if(dnHeader==null) return null;

		List<String> feeCodeList = new ArrayList<String>();
		List<String> feeAmountList = new ArrayList<String>();

		demandNoteItemList.forEach(x->{
			String fcFeeCode = x.getFcFeeCode();
			if (fcFeeCode == null) {
				if (x.getFeeCode() != null) {
					fcFeeCode = x.getFeeCode().getId();
				}
			}
			FeeCode f = feeCodeDao.findById(fcFeeCode);
			x.setFeeCode(f);
			feeCodeList.add(f.getEngDesc());
			feeCodeList.add(f.getChiDesc());
			feeAmountList.add(x.getAmount().toString());
			feeAmountList.add("");

		});

		PdfReader pdfReader = new PdfReader(source);
	    PdfStamper pdfStamper = new PdfStamper(pdfReader, os);
	    AcroFields fields = pdfStamper.getAcroFields();

	    BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
	    fields.addSubstitutionFont(bf);

	    fields.setField("DemandNoteNumber", demandNoteNo);
		SimpleDateFormat df = new SimpleDateFormat(dnsDateFormat, Locale.ENGLISH);

	    fields.setField("IssueDate", df.format(dnHeader.getGenerationTime()));
	    fields.setField("DueDate", df.format(dnHeader.getDueDate()));
	    fields.setField("AmountPayable", dnHeader.getAmount().toString());
	    fields.setField("AmountTotal", dnHeader.getAmount().toString());
	    fields.setField("BillType", "05");
//	    RevenueCode// TODO

	    fields.setField("Name", dnHeader.getBillName());
	    fields.setField("Address", StringUtils.join(new String[]{dnHeader.getAddress1(), dnHeader.getAddress2(), dnHeader.getAddress3()}, SystemUtils.LINE_SEPARATOR));

	    String feeCode = StringUtils.join(feeCodeList, SystemUtils.LINE_SEPARATOR);
	    // if autopay, add remark "PAYMENT WILL BE SETTLED BY EBS AUTOPAY"
	    if (Boolean.TRUE.equals(inputParam.get("autopay"))) {
	    	feeCode += "\n** PAYMENT WILL BE SETTLED BY EBS AUTOPAY **";
	    }
		fields.setField("FeeCode", feeCode);
	    fields.setField("FeeAmount", StringUtils.join(feeAmountList, SystemUtils.LINE_SEPARATOR));

	    PushbuttonField qpCode = fields.getNewPushbuttonFromField("QRCode");
	    qpCode.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
	    qpCode.setProportionalIcon(true);
	    qpCode.setImage(Image.getInstance(createQRCode(demandNoteNo, dnHeader.getAmount(), demandNoteItemList)));
	    fields.replacePushbuttonField("QRCode", qpCode.getField());

	    Map<String, Item> map = fields.getFields();
        for (String name : map.keySet()) {
        	fields.setFieldProperty(name, "setfflags", PdfFormField.FF_READ_ONLY, null);
        }

        if(inputParam.containsKey(PRINT_COPY_CAPTION) && (Boolean)inputParam.get(PRINT_COPY_CAPTION)){
        	addWatermark(pdfReader, pdfStamper);
        }
	    pdfStamper.close();
	    pdfReader.close();
	    return os.toByteArray();
	}

	protected byte[] createQRCode(String dnNo, BigDecimal totalAmount, List<DemandNoteItem> demandNoteItemList) throws Exception{
		Map<String, Double> revenueItem = new HashMap<String, Double>();
		revenueItem.put(demandNoteItemList.get(0).getFeeCode().getFormCode(), totalAmount.doubleValue());
		SimpleDateFormat df = new SimpleDateFormat(dnsDateFormat, Locale.ENGLISH);
		InputStream qrCode = DemandNote.getDemandNoteQR(dnNo, Cons.DNS_BILL_CODE, dnNo.substring(2, 4), df.format(new Date()), totalAmount.doubleValue(), revenueItem, 100);
		return IOUtils.toByteArray(qrCode);
	}

	protected void addWatermark(PdfReader pdfReader, PdfStamper pdfStamper){
		try{
			final String classpath = "server/report/";
			URL source = new ClassPathResource(classpath+"copy.jpg").getURL();
			Image img = Image.getInstance(source);
	        float w = img.getScaledWidth();
	        float h = img.getScaledHeight();

	        Font FONT = new Font(Font.FontFamily.HELVETICA, 60, Font.BOLD, new GrayColor(0.5f));
	        Phrase p = new Phrase("Copy", FONT);

	        // properties
	        PdfContentByte over;
	        Rectangle pagesize;
	        float x, y;

	        // loop over every page
	        int n = pdfReader.getNumberOfPages();
	        for (int i = 1; i <= n; i++) {

	            // get page size and position
	            pagesize = pdfReader.getPageSizeWithRotation(i);
	            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
	            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
	            over = pdfStamper.getOverContent(i);
	            over.saveState();

	            // set transparency
	            PdfGState state = new PdfGState();
	            state.setFillOpacity(0.2f);
	            over.setGState(state);

	            // add watermark text and image
	            if (i % 2 == 1) {
	                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
	            } else {
//	            	ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
	                over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
	            }

	            over.restoreState();
	        }
		}catch(Exception ex){
			logger.error("Fail to add watermark!", ex);
		}
	}
	@Override
	public byte[] generate(String demandNoteNo, boolean autopay, Boolean firstReminder) throws Exception {
		return generate(demandNoteNo, autopay);
	}
}
