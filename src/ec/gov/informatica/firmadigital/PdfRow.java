package ec.gov.informatica.firmadigital;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import sun.misc.BASE64Decoder;

public class PdfRow {
	private String nomProceso;
	private String nomDemandado;
	private String nomPdf;
	private String paso;
	private String file;
	private int idPdf;
	private transient byte[] pdfBytes;

	public  void generarBytes() throws IOException {
			BASE64Decoder decoder = new BASE64Decoder();
			String respuesta = getFile();
//			respuesta = respuesta.substring(1, respuesta.length() - 1);
			respuesta = respuesta.replace("\\", "");
			setPdfBytes(decoder.decodeBuffer(respuesta));
			FileOutputStream fos = new FileOutputStream(nomProceso+"-"+paso+"-"+nomDemandado+"-"+idPdf+"-"+nomPdf);
			fos.write(getPdfBytes());
			fos.close();
	}

	public String getNomProceso() {
		return nomProceso;
	}

	public void setNomProceso(String nomProceso) {
		this.nomProceso = nomProceso;
	}

	public String getNomDemandado() {
		return nomDemandado;
	}

	public void setNomDemandado(String nomDemandado) {
		this.nomDemandado = nomDemandado;
	}

	public String getNomPdf() {
		return nomPdf;
	}

	public void setNomPdf(String nomPdf) {
		this.nomPdf = nomPdf;
	}

	public String getPaso() {
		return paso;
	}

	public void setPaso(String paso) {
		this.paso = paso;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public byte[] getPdfBytes() {
		return pdfBytes;
	}

	public void setPdfBytes(byte[] pdfBytes) {
		this.pdfBytes = pdfBytes;
	}

	public int getIdPdf() {
		return idPdf;
	}

	public void setIdPdf(int idPdf) {
		this.idPdf = idPdf;
	}
	

}
