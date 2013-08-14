package ec.gov.informatica.firmadigital.applet;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.common.base.CharMatcher;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import ec.gov.informatica.firmadigital.DatosUsuario;
import ec.gov.informatica.firmadigital.FirmaDigital;
import ec.gov.informatica.firmadigital.signature.BouncyCastleSignatureProcessor;
import ec.gov.informatica.firmadigital.signature.CMSSignatureProcessor;
import ec.gov.informatica.firmadigital.signature.SignatureVerificationException;

public class PdfViewer extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static enum Navigation {
		GO_FIRST_PAGE, FORWARD, BACKWARD, GO_LAST_PAGE, GO_N_PAGE
	}

	private static final CharMatcher POSITIVE_DIGITAL = CharMatcher
			.anyOf("0123456789");
	private static final String GO_PAGE_TEMPLATE = "%s of %s";
	private static final int FIRST_PAGE = 1;
	private int currentPage = FIRST_PAGE;
	private JButton btnFirstPage;
	private JButton btnPreviousPage;
	private JTextField txtGoPage;
	private JButton btnNextPage;
	private JButton btnLastPage;
	private PagePanel pagePanel;
	private PDFFile pdfFile;
	private String direccionPDF;
	private javax.swing.JLabel jLabel2;

	public PdfViewer(String path) {
		initial(path);
	}

	private void initial(String path) {
		setLayout(new BorderLayout(0, 0));
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(topPanel, BorderLayout.NORTH);
		btnFirstPage = createButton("|<<");
		topPanel.add(btnFirstPage);
		btnPreviousPage = createButton("<<");
		topPanel.add(btnPreviousPage);
		txtGoPage = new JTextField(10);
		txtGoPage.setHorizontalAlignment(JTextField.CENTER);
		topPanel.add(txtGoPage);
		btnNextPage = createButton(">>");
		topPanel.add(btnNextPage);
		btnLastPage = createButton(">>|");
		topPanel.add(btnLastPage);
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		JPanel viewPanel = new JPanel(new BorderLayout(0, 0));
		scrollPane.setViewportView(viewPanel);

		pagePanel = new PagePanel();
		Dimension screenSize = new Dimension(740, 500);
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pagePanel.setPreferredSize(screenSize);
		// Forma el panel de firmantes
		JPanel firmantesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		firmantesPanel.setBackground(new java.awt.Color(255, 255, 255));
		firmantesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				javax.swing.BorderFactory.createLineBorder(new java.awt.Color(
						153, 153, 153)), "Firmantes",
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font(
						"Arial", 1, 12))); // NOI18N
		jLabel2 = new javax.swing.JLabel();

		javax.swing.GroupLayout firmantesPanelLayout = new javax.swing.GroupLayout(
				firmantesPanel);
		FirmaDigital firmaDigital = new FirmaDigital();
		String firmantes = "<html><ul><li>pruebaaa de esto es es unas dasq</li>";
		try {
			for (String firmante : firmaDigital.verificar(path)) {
				firmantes += "<li>" + firmante+"</li>";
			}
		} catch (SignatureVerificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		firmantes += "</ul></html>";
		jLabel2.setText(firmantes);
		firmantesPanel.setLayout(firmantesPanelLayout);
		firmantesPanelLayout.setHorizontalGroup(firmantesPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						firmantesPanelLayout.createSequentialGroup()
								.addContainerGap().addComponent(jLabel2)
								.addGap(52, 52, 52)));
		firmantesPanelLayout.setVerticalGroup(firmantesPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						firmantesPanelLayout.createSequentialGroup()
								.addGap(10, 10, 10).addComponent(jLabel2)
								.addContainerGap(242, Short.MAX_VALUE)));

		// Agraga los paneles paralelos para el pdf y los firmantes
		javax.swing.GroupLayout viewPanelLayout = new javax.swing.GroupLayout(
				viewPanel);
		viewPanel.setLayout(viewPanelLayout);
		viewPanelLayout.setHorizontalGroup(viewPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				viewPanelLayout
						.createSequentialGroup()
						.addComponent(firmantesPanel,
								javax.swing.GroupLayout.PREFERRED_SIZE, 200,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(pagePanel,
								javax.swing.GroupLayout.PREFERRED_SIZE, 540,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(122, Short.MAX_VALUE)));
		viewPanelLayout
				.setVerticalGroup(viewPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								viewPanelLayout
										.createSequentialGroup()
										.addGroup(
												viewPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																firmantesPanel,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																pagePanel,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(0, 0, Short.MAX_VALUE)));

		// viewPanel.add(pagePanel, BorderLayout.CENTER);

		disableAllNavigationButton();

		btnFirstPage.addActionListener(new PageNavigationListener(
				Navigation.GO_FIRST_PAGE));
		btnPreviousPage.addActionListener(new PageNavigationListener(
				Navigation.BACKWARD));
		btnNextPage.addActionListener(new PageNavigationListener(
				Navigation.FORWARD));
		btnLastPage.addActionListener(new PageNavigationListener(
				Navigation.GO_LAST_PAGE));
		txtGoPage.addActionListener(new PageNavigationListener(
				Navigation.GO_N_PAGE));
	}

	private JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(55, 20));

		return button;
	}

	private void disableAllNavigationButton() {
		btnFirstPage.setEnabled(false);
		btnPreviousPage.setEnabled(false);
		btnNextPage.setEnabled(false);
		btnLastPage.setEnabled(false);
	}

	private boolean isMoreThanOnePage(PDFFile pdfFile) {
		return pdfFile.getNumPages() > 1;
	}

	private class PageNavigationListener implements ActionListener {
		private final Navigation navigation;

		private PageNavigationListener(Navigation navigation) {
			this.navigation = navigation;
		}

		public void actionPerformed(ActionEvent e) {
			if (pdfFile == null) {
				return;
			}

			int numPages = pdfFile.getNumPages();
			if (numPages <= 1) {
				disableAllNavigationButton();
			} else {
				if (navigation == Navigation.FORWARD && hasNextPage(numPages)) {
					goPage(currentPage, numPages);
				}

				if (navigation == Navigation.GO_LAST_PAGE) {
					goPage(numPages, numPages);
				}

				if (navigation == Navigation.BACKWARD && hasPreviousPage()) {
					goPage(currentPage, numPages);
				}

				if (navigation == Navigation.GO_FIRST_PAGE) {
					goPage(FIRST_PAGE, numPages);
				}

				if (navigation == Navigation.GO_N_PAGE) {
					String text = txtGoPage.getText();
					boolean isValid = false;
					if (!isNullOrEmpty(text)) {
						boolean isNumber = POSITIVE_DIGITAL.matchesAllOf(text);
						if (isNumber) {
							int pageNumber = Integer.valueOf(text);
							if (pageNumber >= 1 && pageNumber <= numPages) {
								goPage(Integer.valueOf(text), numPages);
								isValid = true;
							}
						}
					}

					if (!isValid) {
						JOptionPane
								.showMessageDialog(
										PdfViewer.this,
										String.format(
												"Invalid page number '%s' in this document",
												text));
						txtGoPage.setText(String.format(GO_PAGE_TEMPLATE,
								currentPage, numPages));
					}
				}
			}
		}

		private void goPage(int pageNumber, int numPages) {
			currentPage = pageNumber;
			PDFPage page = pdfFile.getPage(currentPage);
			pagePanel.showPage(page);
			boolean notFirstPage = isNotFirstPage();
			btnFirstPage.setEnabled(notFirstPage);
			btnPreviousPage.setEnabled(notFirstPage);
			txtGoPage.setText(String.format(GO_PAGE_TEMPLATE, currentPage,
					numPages));
			boolean notLastPage = isNotLastPage(numPages);
			btnNextPage.setEnabled(notLastPage);
			btnLastPage.setEnabled(notLastPage);
		}

		private boolean hasNextPage(int numPages) {
			return (++currentPage) <= numPages;
		}

		private boolean hasPreviousPage() {
			return (--currentPage) >= FIRST_PAGE;
		}

		private boolean isNotLastPage(int numPages) {
			return currentPage != numPages;
		}

		private boolean isNotFirstPage() {
			return currentPage != FIRST_PAGE;
		}
	}

	public PagePanel getPagePanel() {
		return pagePanel;
	}

	public void setPDFFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
		currentPage = FIRST_PAGE;
		disableAllNavigationButton();
		txtGoPage.setText(String.format(GO_PAGE_TEMPLATE, FIRST_PAGE,
				pdfFile.getNumPages()));
		boolean moreThanOnePage = isMoreThanOnePage(pdfFile);
		btnNextPage.setEnabled(moreThanOnePage);
		btnLastPage.setEnabled(moreThanOnePage);
	}

	public String getDireccionPDF() {
		return direccionPDF;
	}

	public void setDireccionPDF(String direccionPDF) {
		this.direccionPDF = direccionPDF;
	}

}