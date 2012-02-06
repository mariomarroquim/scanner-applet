import uk.co.mmscomputing.device.twain.TwainScanner;

public class TwainGedScanner extends GedScanner {

	private static final long serialVersionUID = 8548685859538732977L;

	public void inicializar() {
		scanner = new TwainScanner();
	}
	
}