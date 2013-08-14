package ec.gov.informatica.firmadigital.applet;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.JFrame;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import ec.gov.informatica.firmadigital.FirmaDigital;
import ec.gov.informatica.firmadigital.JerseyClient;
import ec.gov.informatica.firmadigital.PdfRow;
import ec.gov.informatica.firmadigital.signature.SignatureVerificationException;

public class PdfViewerTest {
	public static void main(String[] args) {
		try {
			long heapSize = Runtime.getRuntime().totalMemory();
			System.out.println("Heap Size = " + heapSize);

			JFrame frame = new JFrame("PDF Prueba");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// load a pdf from a byte buffer}
			String direccionPDF="C:\\Users\\hp1\\Dropbox\\Profesional\\aprendizaje\\1932394850JavaFirmado.pdf";
			File file = new File(direccionPDF);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			final PDFFile pdffile = new PDFFile(buf);
//			PdfViewer pdfViewer = new PdfViewer();
//			pdfViewer.setPDFFile(pdffile);
//			pdfViewer.setDireccionPDF(direccionPDF);
//			FirmaDigital firmaDigital = new FirmaDigital();
//			firmaDigital.verificar(pdfViewer.getDireccionPDF());
//			pdfViewer.obtenerFirmas();
			JerseyClient webServiceLink = new JerseyClient();
//			System.out.println(webServiceLink.getToken());
//			webServiceLink.getPdfRows();
//			frame.add(pdfViewer);
			frame.pack();
			frame.setVisible(true);

			PDFPage page = pdffile.getPage(0);
//			pdfViewer.getPagePanel().showPage(page);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("NO hay los archivos");
		} 
//		catch (SignatureVerificationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
