package ec.gov.informatica.firmadigital;

public class IdentificacionPdf {
	private String idCola;
	private String idPdf;
	private String nombrePdf;
	public String getIdCola() {
		return idCola;
	}
	public void setIdCola(String idCola) {
		this.idCola = idCola;
	}
	public String getIdPdf() {
		return idPdf;
	}
	public void setIdPdf(String idPdf) {
		this.idPdf = idPdf;
	}
	
	public String getNombrePdf() {
		return nombrePdf;
	}
	public void setNombrePdf(String nombrePdf) {
		this.nombrePdf = nombrePdf;
	}
	public IdentificacionPdf(String idCola, String idPdf, String nombrePdf) {
		super();
		this.idCola = idCola;
		this.idPdf = idPdf;
		this.nombrePdf = nombrePdf;
	}
	
}
