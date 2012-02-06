import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class UploadMultiplo extends Componente {

	private static final long serialVersionUID = 2361780507139748779L;
	
	protected FileFilter EXTENCOES_DOCUMENTO_SUPORTADAS = new ExtensionFilter("Documentos", new String[] {".txt", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".odt", ".pdf"});
	protected FileFilter EXTENCOES_FIGURA_SUPORTADAS = new ExtensionFilter("Imagens", new String[] {".bmp", ".jpg", ".jpeg", ".tif", ".tiff", ".png", ".gif"});
	protected FileFilter EXTENCOES_AUDIO_SUPORTADAS = new ExtensionFilter("Imagens", new String[] {".mp3", ".wma"});
	protected FileFilter EXTENCOES_VIDEO_SUPORTADAS = new ExtensionFilter("Vídeos", new String[] {".avi", ".wmv", ".mp4", ".3gp"});
	protected FileFilter EXTENCOES_EXECUTAVEL_SUPORTADAS = new ExtensionFilter("Executáveis", new String[] {".exe", ".deb", ".rpm"});
	protected FileFilter EXTENCOES_COMPACTADO_SUPORTADAS = new ExtensionFilter("Arquivos compactados", new String[] {".zip", ".rar", ".gz"});
	
	protected JButton botaoSelecionarArquivos = null;
	protected JFileChooser chooserSelecionarVariosArquivos = null;
	protected File[] arquivosSelecionados = null;
	protected JButton botaoEnviar = null;
	
	public void init() {
		try {
			configurarLayout();
			
			if(getParameter("id_pasta") == null || getParameter("id_usuario") == null || getParameter("url_upload_arquivo") == null){
				throw new RuntimeException("Todos os parâmetros devem ser preenchidos.");
			}			

			chooserSelecionarVariosArquivos = new JFileChooser("Selecione os arquivos para serem enviados");
			chooserSelecionarVariosArquivos.setMultiSelectionEnabled(true);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_DOCUMENTO_SUPORTADAS);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_FIGURA_SUPORTADAS);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_AUDIO_SUPORTADAS);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_VIDEO_SUPORTADAS);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_EXECUTAVEL_SUPORTADAS);
			chooserSelecionarVariosArquivos.addChoosableFileFilter(EXTENCOES_COMPACTADO_SUPORTADAS);
			
			botaoSelecionarArquivos = new JButton("Selecionar...");
			botaoSelecionarArquivos.setMnemonic('S');
			
			botaoSelecionarArquivos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean chooserFechado = (chooserSelecionarVariosArquivos.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION);
					File[] arquivosSelecionadosAgora = chooserSelecionarVariosArquivos.getSelectedFiles();
					int tamanhoArquivosSelecionadosAgora = arquivosSelecionadosAgora.length;
					
					if (chooserFechado == true && arquivosSelecionadosAgora != null && tamanhoArquivosSelecionadosAgora > 0) {
						arquivosSelecionados = arquivosSelecionadosAgora;
						botaoEnviar.setEnabled(true);
						botaoEnviar.setText(getLabelBotaoEnviar(arquivosSelecionados.length));
					}
					
					arquivosSelecionadosAgora = null;
				}
			});
			
			botaoEnviar = new JButton(getLabelBotaoEnviar(0));
			botaoEnviar.setMnemonic('O');
			botaoEnviar.setEnabled(false);
			
			botaoEnviar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					enviarArquivos();
				}
			});

			configurarAlinhamento();
			getContentPane().add(botaoSelecionarArquivos, getAlinhamento());
			getContentPane().add(botaoEnviar, getAlinhamento());	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void enviarArquivos() {
		String arquivoAtual = null;
		
		try {
			desabilitarCampos();
			URL url = new URL(getParameter("url_upload_arquivo"));
			
			for(File arquivo : arquivosSelecionados){
				arquivoAtual = arquivo.getName();
				
				String boundary = MultiPartFormOutputStream.createBoundary();
				URLConnection requisicao = gerarRequisicao(boundary, url);
	
				MultiPartFormOutputStream saida = new MultiPartFormOutputStream(requisicao.getOutputStream(), boundary);
				saida.writeField("id_pasta", getParameter("id_pasta"));
				saida.writeField("id_usuario", getParameter("id_usuario"));
				anexarArquivo(arquivo, saida);
				
				saida.close();
				saida = null;
				
				carregarResposta(requisicao);
				
				arquivo = null;
				arquivoAtual = null;
			}
			
			String urlBase = getParameter("url_upload_arquivo");
			if(urlBase.contains("?")){
				getAppletContext().showDocument(new URL(urlBase + "&id_pasta=" + getParameter("id_pasta") + "&ok=true"));	
			}
			else {
				getAppletContext().showDocument(new URL(urlBase + "?id_pasta=" + getParameter("id_pasta") + "&ok=true"));	
			}
		}
		catch(Exception e){
			tratarEExibirErro(e, arquivoAtual);
		}
	}
	
    @Override	
	protected void trocarCampos(boolean habilitar) {
		botaoSelecionarArquivos.setEnabled(habilitar);
		
		if(habilitar == false){
			botaoEnviar.setEnabled(false);
		}
		else if(habilitar == true && arquivosSelecionados != null && arquivosSelecionados.length > 0){
			botaoEnviar.setEnabled(true);
		}
	}
	
}