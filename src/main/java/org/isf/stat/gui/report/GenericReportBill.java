/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.stat.gui.report;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.TxtPrinter;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.menu.manager.Context;
import org.isf.patient.model.Patient;
import org.isf.serviceprinting.manager.PrintReceipt;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.utils.db.DbSingleJpaConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Created on 15/Jun/08
 */
public class GenericReportBill {

    private final Logger logger = LoggerFactory.getLogger(GenericReportBill.class);
	private JasperReportsManager jasperReportsManager = Context.getApplicationContext().getBean(JasperReportsManager.class);

	public GenericReportBill(Integer billID, String jasperFileName) {
		new GenericReportBill(billID, jasperFileName, true, true);
	}

	public GenericReportBill(Integer billID, String jasperFileName, boolean show, boolean askForPrint) {
		
		TxtPrinter.getTxtPrinter();
		
		try {
            JasperReportResultDto jasperReportPDFResultDto = jasperReportsManager.getGenericReportBillPdf(billID, jasperFileName, show, askForPrint);

			if (show) {
                if (GeneralData.INTERNALVIEWER) {
                    JasperViewer.viewReport(jasperReportPDFResultDto.getJasperPrint(), false, new Locale(GeneralData.LANGUAGE));
                } else {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(GeneralData.VIEWER + " " + jasperReportPDFResultDto.getFilename());
                }
			}
			
			if (GeneralData.RECEIPTPRINTER) {
				JasperReportResultDto jasperReportTxtResultDto = jasperReportsManager.getGenericReportBillTxt(billID, jasperFileName, show, askForPrint);
				int print = JOptionPane.OK_OPTION;
				if (askForPrint) {
					print = JOptionPane.showConfirmDialog(null, MessageBundle.getMessage("angal.genericreportbill.doyouwanttoprintreceipt"));
				}
				if (print != JOptionPane.OK_OPTION) return; //STOP
				
				if (TxtPrinter.MODE.equals("PDF")) new PrintReceipt(jasperReportPDFResultDto.getJasperPrint(), jasperReportPDFResultDto.getFilename());
				else if (TxtPrinter.MODE.equals("TXT") ||
						TxtPrinter.MODE.equals("ZPL"))
					new PrintReceipt(jasperReportTxtResultDto.getJasperPrint(), jasperReportTxtResultDto.getFilename());
			}
		} catch (Exception e) {
            logger.error("", e);
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.stat.reporterror"), MessageBundle.getMessage("angal.hospital"), JOptionPane.ERROR_MESSAGE);
        }
	}
	
	//FIXME: refactor using jasperReportsManager as above.
	public GenericReportBill(Integer billID, String jasperFileName, Patient patient, ArrayList<Integer> billListId, String dataFrom, String dateTo, boolean show, boolean askForPrint) {
		try {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = Context.getApplicationContext().getBean(HospitalBrowsingManager.class);
			Hospital hosp = hospManager.getHospital();

			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("collectionbillsId", billListId); // real param
			//parameters.put("fromDate", dataFrom);
			//parameters.put("toDate", dateTo);
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			// System.out.println("Jasper Report Name:"+sbFilename.toString());

			File jasperFile = new File(sbFilename.toString());

			//Connection conn = DbSingleConn.getConnection();
			Connection conn = DbSingleJpaConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);

			if (show) {
				if (GeneralData.INTERNALVIEWER) {	
					JasperViewer.viewReport(jasperPrint, false, new Locale(GeneralData.LANGUAGE));
				} else {
					try {
						Runtime rt = Runtime.getRuntime();
						rt.exec(GeneralData.VIEWER + " " + PDFfile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if (GeneralData.RECEIPTPRINTER) {				
				sbFilename = new StringBuilder();
				sbFilename.append("rpt");
				sbFilename.append(File.separator);
				sbFilename.append(jasperFileName);
				sbFilename.append("Txt");
				sbFilename.append(".jasper");
				
				//System.out.println("Jasper Report Name:"+sbFilename.toString());

				jasperFile = new File(sbFilename.toString());
				
				jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
				
				String TXTfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".txt";
				
				int print = JOptionPane.OK_OPTION;
				if (askForPrint) {
					print = JOptionPane.showConfirmDialog(null, MessageBundle.getMessage("angal.genericreportbill.doyouwanttoprintreceipt"));
				}
				if (print == JOptionPane.OK_OPTION) {
					new PrintReceipt(jasperPrint, TXTfile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
