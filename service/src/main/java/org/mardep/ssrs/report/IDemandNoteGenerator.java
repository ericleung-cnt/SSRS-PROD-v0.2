package org.mardep.ssrs.report;

public interface IDemandNoteGenerator extends IReportGenerator {
	byte[] generate(String demandNoteId) throws Exception;

	byte[] generate(String demandNoteNo, boolean autopay) throws Exception;

	byte[] generate(String demandNoteNo, boolean autopay, Boolean firstReminder) throws Exception;
}
