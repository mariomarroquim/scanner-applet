import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public abstract class GedScanner extends Componente implements ScannerListener {

	protected static final long serialVersionUID = -6485821906378910164L;

	protected FileFilter EXTENCOES_SUPORTADAS = new ExtensionFilter("Imagens", new String[] {".jpg", ".jpeg"});
	
	protected final String [] PREFIXOS_INTERFACES = new String[] {"WIA", "TWAIN", "SANE"};
	
	protected Scanner scanner = null;
	protected JComboBox scanners = null; 
	protected JCheckBox modoColorido = null;
	protected JButton botaoDigitalizar = null;
	protected boolean scannersDisponiveis = false;
	protected List<String> nomesArquivos = new ArrayList<String>();
	
	protected JButton botaoSelecionarArquivos = null;
	protected JFileChooser chooserSelecionarVariosArquivos = null;
	protected File[] arquivosSelecionados = null;
	
	protected JButton botaoEnviarTudo = null;

	protected abstract void inicializar();

	public void init() {
		try {
			configurarLayout();
			inicializar();

			if(getParameter("id_documento") == null || getParameter("id_usuario") == null || getParameter("url_upload_pagina") == null){
				throw new RuntimeException("Todos os parâmetros devem ser preenchidos.");
			}
			
			scannersDisponiveis = false;
			
			if(isScannerValido()){
				scanner.addListener(this);

				modoColorido = new JCheckBox("Cor?", true);

				botaoDigitalizar = new JButton("Digitalizar");
				botaoDigitalizar.setMnemonic('D');
				
				botaoDigitalizar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						digitalizar();
					}
				});

				List<String> opcoes = new ArrayList<String>();
				String [] listaScanners = scanner.getDeviceNames();

				for(String nomeScanner : listaScanners){
					boolean podeListar = true;
					for(String prefixoInterface : PREFIXOS_INTERFACES){
						if(nomeScanner.toLowerCase().contains(prefixoInterface.toLowerCase())){
							podeListar = false;
							break;
						}
					}

					if(podeListar || listaScanners.length < 2){
						opcoes.add(nomeScanner);
					}					
				}
				
				if(opcoes.size() < 1 && nomesScanners.length > 0){
					for(String nomeScanner : nomesScanners){
						opcoes.add(nomeScanner);
					}
				}
				
				scanners = new JComboBox(opcoes.toArray());
				
				if(opcoes.size() > 0){
					scanners.setSelectedIndex(0);
				}
				
				scannersDisponiveis = true;
			}
				
			chooserSelecionarVariosArquivos = new JFileChooser("Selecione os arquivos para serem enviados");
			chooserSelecionarVariosArquivos.setMultiSelectionEnabled(true);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_SUPORTADAS);
			
			botaoSelecionarArquivos = new JButton("Selecionar...");
			botaoSelecionarArquivos.setMnemonic('S');
			
			botaoSelecionarArquivos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean chooserFechado = (chooserSelecionarVariosArquivos.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION);
					File[] arquivosSelecionadosAgora = chooserSelecionarVariosArquivos.getSelectedFiles();
					int tamanhoArquivosSelecionadosAgora = arquivosSelecionadosAgora.length; 
					
					if (chooserFechado == true && arquivosSelecionadosAgora != null && tamanhoArquivosSelecionadosAgora > 0) {
						arquivosSelecionados = arquivosSelecionadosAgora;
						botaoEnviarTudo.setEnabled(true);
						botaoEnviarTudo.setText(getLabelBotaoEnviar(nomesArquivos.size() + arquivosSelecionados.length));
					}
					
					arquivosSelecionadosAgora = null;
				}
			});
			
			botaoEnviarTudo = new JButton(getLabelBotaoEnviar(0));
			botaoEnviarTudo.setMnemonic('O');
			botaoEnviarTudo.setEnabled(false);
			
			botaoEnviarTudo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					enviarTudo();
				}
			});
			
			configurarAlinhamento();
			
			if(scannersDisponiveis == true){
				getContentPane().add(scanners, getAlinhamento());
				getContentPane().add(modoColorido, getAlinhamento());
				getContentPane().add(botaoDigitalizar, getAlinhamento());
			}
			
			getContentPane().add(botaoSelecionarArquivos, getAlinhamento());
			getContentPane().add(botaoEnviarTudo, getAlinhamento());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void update(Type type, ScannerIOMetadata metadata) {
		try {
			if (type.equals(ScannerIOMetadata.ACQUIRED)) {
				BufferedImage imagemOriginal = metadata.getImage();
				
				if(!modoColorido.isSelected()) {
					BufferedImage imagemCinza = new BufferedImage(imagemOriginal.getWidth(), imagemOriginal.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
					Graphics graphics = imagemCinza.getGraphics();  
					graphics.drawImage(imagemOriginal, 0, 0, null);  
					graphics.dispose();
					graphics = null;
					
					ImageIO.write(imagemCinza, "jpeg", new File(getNomeArquivo()));
					imagemCinza = null;
				}
				else {
					ImageIO.write(imagemOriginal, "jpeg", new File(getNomeArquivo()));
				}
				
				imagemOriginal = null;
			}
			else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
				ScannerDevice device = metadata.getDevice();
				device.setResolution(200);
			    device.setShowUserInterface(false);
			} 
			else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
				System.err.println(metadata.getStateStr());
				
				if (metadata.isFinished()) {
					nomesArquivos.add(getNomeArquivo());
					habilitarCampos();
					
					if(arquivosSelecionados != null){
						botaoEnviarTudo.setText(getLabelBotaoEnviar(nomesArquivos.size() + arquivosSelecionados.length));
					}
					else {
						botaoEnviarTudo.setText(getLabelBotaoEnviar(nomesArquivos.size()));
					}
				}
			} 
			else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
				metadata.getException().printStackTrace();
				throw new RuntimeException(metadata.getException());
		    }
		}
		catch(Exception e){
			try {
				scanner.setCancel(true);
			} catch (ScannerIOException e1) {
				e1.printStackTrace();
			}
			
			habilitarCampos();
			String mensagem = "Não foi possível realizar a operação!\nERRO: " + e.getMessage();
			JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	protected void digitalizar() {
		try {
			desabilitarCampos();
			scanner.select(scanners.getSelectedItem().toString());
			scanner.acquire();
		}
		catch(Exception e){
			try {
				scanner.setCancel(true);
			} catch (ScannerIOException e1) {
				e1.printStackTrace();
			}

			habilitarCampos();
			String mensagem = "Não foi possível realizar a operação!\nERRO:" + e.getMessage();
			JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	protected void enviarTudo(){
		enviarArquivosDigitalizados();
		enviarArquivosUpload();

		try {
			String urlBase = getParameter("url_upload_pagina");
			if(urlBase.contains("?")){
				getAppletContext().showDocument(new URL(urlBase + "&id_documento=" + getParameter("id_documento") + "&ok=true"));	
			}
			else {
				getAppletContext().showDocument(new URL(urlBase + "?id_documento=" + getParameter("id_documento") + "&ok=true"));	
			}
		}
		catch(Exception e1){
			habilitarCampos();
			String mensagem = "Não foi possível realizar a operação!\n";
			mensagem += "Ocorreu um erro ao enviar os dados.\n";
			mensagem += "ERRO: "+ e1.getMessage() + ".";
			JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
	
	protected void enviarArquivosDigitalizados() {
		if(nomesArquivos == null || nomesArquivos.size() == 0){
			return; 
		}
		
		int contador = 1;
		
		try {
			desabilitarCampos();
			URL url = new URL(getParameter("url_upload_pagina"));
			
			for(String nomeArquivo : nomesArquivos){
				File arquivo = new File(nomeArquivo);
				arquivo.deleteOnExit();
				
				String boundary = MultiPartFormOutputStream.createBoundary();
				URLConnection requisicao = gerarRequisicao(boundary, url);
	
				MultiPartFormOutputStream saida = new MultiPartFormOutputStream(requisicao.getOutputStream(), boundary);
				saida.writeField("id_documento", getParameter("id_documento"));
				saida.writeField("id_usuario", getParameter("id_usuario"));
				anexarArquivo(arquivo, saida);
				
				saida.close();
				saida = null;
				
				carregarResposta(requisicao);
				
				contador += 1;
				arquivo = null;
				nomeArquivo = null;
			}
		}
		catch(Exception e){
			habilitarCampos();
			String mensagem = "Não foi possível realizar a operação!\n";
			mensagem += "Ocorreu um erro ao enviar a página #" + contador + ".\n";
			mensagem += "ERRO: "+ e.getMessage() + ".";
			JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
		
	protected void enviarArquivosUpload() {
		if(arquivosSelecionados == null || arquivosSelecionados.length == 0){
			return; 
		}
				
		String arquivoAtual = null;
		
		try {
			desabilitarCampos();
			URL url = new URL(getParameter("url_upload_pagina"));

			for(File arquivo : arquivosSelecionados){
				arquivoAtual = arquivo.getName();
				
				String boundary = MultiPartFormOutputStream.createBoundary();
				URLConnection requisicao = gerarRequisicao(boundary, url);
	
				MultiPartFormOutputStream saida = new MultiPartFormOutputStream(requisicao.getOutputStream(), boundary);
				saida.writeField("id_documento", getParameter("id_documento"));
				saida.writeField("id_usuario", getParameter("id_usuario"));
				anexarArquivo(arquivo, saida);
				
				saida.close();
				saida = null;
				
				carregarResposta(requisicao);
				
				arquivo = null;
				arquivoAtual = null;
			}
		}
		catch(Exception e){
			tratarEExibirErro(e, arquivoAtual);
		}
	}	
		
	protected String getNomeArquivo(){
		return System.getProperty("user.home") + File.separator + getParameter("id_documento") + "_" + nomesArquivos.size() + ".jpg";
	}

	protected boolean isScannerValido() throws Exception {
		String erroPadrao = "Instale os aplicativos necessários para a digitalização.";
		try {
			if(!scanner.isAPIInstalled()){
				throw new RuntimeException(erroPadrao);
			}
			
			if(scanner.getDeviceNames().length == 0){
				throw new RuntimeException("Verifique se o scanner está instalado e conectado corretamente.");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override	
	protected void trocarCampos(boolean habilitar) {
		if(scannersDisponiveis == true){
			scanners.setEnabled(habilitar);
			modoColorido.setEnabled(habilitar);
			botaoDigitalizar.setEnabled(habilitar);
		}
		
		botaoSelecionarArquivos.setEnabled(habilitar);
				
		if(habilitar == true){
			boolean existemImagensDigitalizados = (nomesArquivos != null && nomesArquivos.size() > 0);
			boolean existemArquivosSelecionados = (arquivosSelecionados != null && arquivosSelecionados.length > 0);
			
			if(existemImagensDigitalizados || existemArquivosSelecionados){
				botaoEnviarTudo.setEnabled(true);
			}
		}
		else {
			botaoEnviarTudo.setEnabled(false);
		}
	}

}
