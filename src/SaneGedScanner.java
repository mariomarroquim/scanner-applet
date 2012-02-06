import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import uk.co.mmscomputing.device.sane.SaneScanner;

public class SaneGedScanner extends GedScanner {

	private static final long serialVersionUID = -1198461638481286846L;

	public void inicializar() {
		scanner = new SaneScanner();
	}
	
	@Override
	public void digitalizar(){
		try {
			desabilitarCampos();
			if(!modoColorido.isSelected()) {
				executarComando("digitalizar_preto_e_branco.sh " + getNomeArquivo());
			}
			else {
				executarComando("digitalizar_colorido.sh " + getNomeArquivo());
			}

			nomesArquivos.add(getNomeArquivo());
			habilitarCampos();
			
			if(arquivosSelecionados != null){
				botaoEnviarTudo.setText(getLabelBotaoEnviar(nomesArquivos.size() + arquivosSelecionados.length));
			}
			else {
				botaoEnviarTudo.setText(getLabelBotaoEnviar(nomesArquivos.size()));
			}
		}
		catch(Exception e){
			String mensagem = "Não foi possível realizar a operação!\nERRO:" + e.getMessage();
			JOptionPane.showMessageDialog(this, mensagem, "Erro!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			habilitarCampos();
		}
	}
	
	private void executarComando(String comando) throws IOException, InterruptedException {
		Process processo = Runtime.getRuntime().exec(comando);
		BufferedReader input = new BufferedReader(new InputStreamReader(processo.getInputStream()));
		 
        String line = null;

        while((line = input.readLine()) != null) {
            System.out.println(line);
        }

        processo.waitFor();
	}

	public static void main(String [] args) throws IOException, InterruptedException{
		Process processo = Runtime.getRuntime().exec("digitalizar_colorido.sh " + "/tmp/teste.jpg");
		
		BufferedReader input = new BufferedReader(new InputStreamReader(processo.getInputStream()));
		 
        String line = null;

        while((line = input.readLine()) != null) {
            System.out.println(line);
        }

        System.out.println("Código de saída: " + processo.waitFor());
	}
	
}