package ec.gov.informatica.firmadigital;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import sun.misc.BASE64Decoder;

public class PdfRow {
	private String nombreProceso;
	private String apellidosDemandado;
	private String nombresDemandado;
	private String nombrePdf;
	private String nombrePaso;
	private String file;
	private int idAdjunto;
	private int idCola;
	
//	private transient byte[] pdfBytes;

//	public void generarBytes() throws IOException {
//		BASE64Decoder decoder = new BASE64Decoder();
//		String respuesta = getFile();
//
//		respuesta = respuesta.replace("\\", "");
//		setPdfBytes(decoder.decodeBuffer(respuesta));
//		FileOutputStream fos = new FileOutputStream(nombreProceso + "-"
//				+ nombrePaso + "-" + apellidosDemandado + " "
//				+ nombresDemandado + "-" + idPdf + "-" + nombrePdf);
//		fos.write(getPdfBytes());
//		fos.close();
//	}

	public String getNombreProceso() {
		return nombreProceso;
	}

	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}

	public String getApellidosDemandado() {
		return apellidosDemandado;
	}

	public void setApellidosDemandado(String apellidosDemandado) {
		this.apellidosDemandado = apellidosDemandado;
	}

	public String getNombresDemandado() {
		return nombresDemandado;
	}

	public void setNombresDemandado(String nombresDemandado) {
		this.nombresDemandado = nombresDemandado;
	}

	public String getNombrePdf() {
		return nombrePdf;
	}

	public void setNombrePdf(String nombrePdf) {
		this.nombrePdf = nombrePdf;
	}

	public String getNombrePaso() {
		return nombrePaso;
	}

	public void setNombrePaso(String nombrePaso) {
		this.nombrePaso = nombrePaso;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getIdAdjunto() {
		return idAdjunto;
	}

	public void setIdAdjunto(int idAdjunto) {
		this.idAdjunto = idAdjunto;
	}

	public int getIdCola() {
		return idCola;
	}

	public void setIdCola(int idCola) {
		this.idCola = idCola;
	}

//	public byte[] getPdfBytes() {
//		return pdfBytes;
//	}
//
//	public void setPdfBytes(byte[] pdfBytes) {
//		this.pdfBytes = pdfBytes;
//	}


}
