package com.yz.code.tools;

import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBasePrintLine;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

public class PrintReport {

	public static void main(String[] args) {

		try {
			String sourceFileName = "C:\\Users\\system\\test.jrxml";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("barcode", "1231231231");
			JasperDesign jasperDesign = JRXmlLoader.load(sourceFileName);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
			List<JRPrintPage> pages = jasperPrint.getPages();
			System.out.println(pages.size());
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			// jasperPrintList.add(getJasperPrint());
			jasperPrintList.add(jasperPrint);
			doPrint(jasperPrintList);
			getDefaultPrintService();

		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	public static PrintService getDefaultPrintService()
	{
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		for (PrintService printService : printServices) {
			System.out.println(printService);
		}
		return printServices[2];
	}

	private static JasperPrint getJasperPrint() throws JRException
	{
		// JasperPrint
		JasperPrint jasperPrint = new JasperPrint();
		jasperPrint.setName("NoReport");
		jasperPrint.setPageWidth(595);
		jasperPrint.setPageHeight(842);

		// Fonts
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Sans_Normal");
		normalStyle.setDefault(true);
		normalStyle.setFontName("宋体");
		normalStyle.setFontSize(8f);
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("Cp1252");
		normalStyle.setPdfEmbedded(false);
		jasperPrint.addStyle(normalStyle);

		JRDesignStyle boldStyle = new JRDesignStyle();
		boldStyle.setName("Sans_Bold");
		boldStyle.setFontName("黑体");
		boldStyle.setFontSize(8f);
		boldStyle.setBold(true);
		boldStyle.setPdfFontName("Helvetica-Bold");
		boldStyle.setPdfEncoding("Cp1252");
		boldStyle.setPdfEmbedded(false);
		jasperPrint.addStyle(boldStyle);

		JRDesignStyle italicStyle = new JRDesignStyle();
		italicStyle.setName("Sans_Italic");
		italicStyle.setFontName("宋体");
		italicStyle.setFontSize(8f);
		italicStyle.setItalic(true);
		italicStyle.setPdfFontName("Helvetica-Oblique");
		italicStyle.setPdfEncoding("Cp1252");
		italicStyle.setPdfEmbedded(false);
		jasperPrint.addStyle(italicStyle);

		JRPrintPage page = new JRBasePrintPage();

		JRPrintLine line = new JRBasePrintLine(jasperPrint.getDefaultStyleProvider());
		line.setX(40);
		line.setY(50);
		line.setWidth(515);
		line.setHeight(0);
		page.addElement(line);

		// JRPrintImage image = new
		// JRBasePrintImage(jasperPrint.getDefaultStyleProvider());
		// image.setX(45);
		// image.setY(55);
		// image.setWidth(165);
		// image.setHeight(40);
		// image.setScaleImage(ScaleImageEnum.CLIP);
		// image.setRenderable(
		// JRImageRenderer.getInstance(
		// JRLoader.loadBytesFromResource("jasperreports.png")
		// )
		// );
		// page.addElement(image);

		JRPrintText text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(210);
		text.setY(55);
		text.setWidth(345);
		text.setHeight(30);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		text.setLineSpacingFactor(1.3133681f);
		text.setLeadingOffset(-4.955078f);
		text.setStyle(boldStyle);
		text.setFontSize(18f);
		text.setText("我们都有一个家");
		page.addElement(text);

		text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(210);
		text.setY(85);
		text.setWidth(325);
		text.setHeight(15);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setStyle(italicStyle);
		text.setFontSize(12f);
		text.setText((new SimpleDateFormat("EEE, MMM d, yyyy")).format(new Date()));
		page.addElement(text);

		text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(40);
		text.setY(150);
		text.setWidth(515);
		text.setHeight(200);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.JUSTIFIED);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setStyle(normalStyle);
		text.setFontSize(14f);
		text.setText(
				"快乐就好"
						+ "JasperReports is a powerful report-generating tool that has the ability to deliver rich content onto the screen, to the printer or into PDF, HTML, XLS, CSV or XML files.\n\n"
						+
						"It is entirely written in Java and can be used in a variety of Java enabled applications, including J2EE or Web applications, to generate dynamic content.\n\n"
						+
						"Its main purpose is to help creating page oriented, ready to print documents in a simple and flexible manner."
				);
		page.addElement(text);

		jasperPrint.addPage(page);

		return jasperPrint;
	}

	private static void doPrint(List<JasperPrint> jasperPrintList) throws JRException {

		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(MediaSizeName.ISO_A4);
		// printRequestAttributeSet.add(PrintServiceAttribute);
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
		configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
		configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
		configuration.setDisplayPageDialog(false);
		configuration.setDisplayPrintDialog(false);
		configuration.setPrintService(getDefaultPrintService());

		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		// exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE,
		// value);
		exporter.setConfiguration(configuration);
		SimpleExporterInput exporterInput = SimpleExporterInput.getInstance(jasperPrintList);
		exporter.setExporterInput(exporterInput);

		// SimplePrintServiceReportConfiguration conifConfiguration = new
		// SimplePrintServiceReportConfiguration();
		// conifConfiguration.setStartPageIndex(1);
		// conifConfiguration.setEndPageIndex(1);
		// exporter.setConfiguration(conifConfiguration);
		exporter.exportReport();
	}
}
