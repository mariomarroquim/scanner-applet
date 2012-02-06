import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public abstract class Componente extends JApplet {

	private static final long serialVersionUID = -8780956045046353190L;
	
	protected String alinhamento = null;

	protected URLConnection gerarRequisicao(String boundary, URL url) throws IOException {
		MultiPartFormOutputStream.createConnection(url);
		URLConnection requisicao = MultiPartFormOutputStream.createConnection(url);
		requisicao.setRequestProperty("Accept", "*/*");
		requisicao.setRequestProperty("Content-Type", MultiPartFormOutputStream.getContentType(boundary));
		requisicao.setRequestProperty("Connection", "Keep-Alive");
		requisicao.setRequestProperty("Cache-Control", "no-cache");
		return requisicao;
	}
	
	protected void anexarArquivo(File arquivo, MultiPartFormOutputStream saida) throws IOException {
		saida.writeFile("upload", "application/octet-stream", arquivo);
	}
	
	protected void carregarResposta(URLConnection requisicao) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(requisicao.getInputStream()));
		String line = "";
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
		in.close();
		in = null;
	}
	
	protected void configurarLayout() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		getContentPane().setFont(new Font("Arial, Helvetica, Sans-serif", Font.PLAIN, 12));
		getContentPane().setBackground(Color.WHITE);
	}
	
	protected void configurarAlinhamento() {
		if(getParameter("alinhamento").equals("esquerda")) {
			alinhamento = BorderLayout.WEST;
			getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
		}
		else {
			alinhamento = BorderLayout.CENTER;
			getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
		}
	}
	
	protected String getAlinhamento() {
		return alinhamento;
	}	
	
	protected String getLabelBotaoEnviar(int quantidade) {
		if(quantidade > 0){
			return "Enviar (" + quantidade + ")";
		}
		else {
			return "Enviar";
		}
	}
	
	protected void tratarEExibirErro(Exception e, String arquivoAtual) {
		habilitarCampos();
		String mensagem = "Não foi possível realizar a operação!\n";
		mensagem += "Ocorreu um erro ao enviar o arquivo " + arquivoAtual + ".\n";
		mensagem += "ERRO: "+ e.getMessage() + ".";
		JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
	
	protected void habilitarCampos() {
		trocarCampos(true);
		getContentPane().setCursor(Cursor.getDefaultCursor());
	}

	protected void desabilitarCampos() {
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		trocarCampos(false);
	}
	
	protected abstract void trocarCampos(boolean habilitar);
	
}