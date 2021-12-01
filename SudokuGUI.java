package Bertolino.sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
class GiocoGUI extends JFrame implements ActionListener{
	
	private JPanel principale = new JPanel(new GridLayout(9,9));
	protected static Sudoku gioco = new Sudoku();
	private JButton sol = new JButton("Visualizza le soluzioni");
	private JButton nuova = new JButton("Inizia una nuova partita");
	private JButton salva = new JButton("Salva la partita");
	private JButton carica = new JButton("Carica una partita");
	private PuntoDiScelta ps;
	private JTextField input = new JTextField(10);
	private JButton crea = new JButton("Crea");
	private JFrame inputName = new FinestraInput();
	protected JFrame soluzioni;
	protected JFrame nuovaPartita;
	private Component[] comp;
	
	Font f = new Font("Helvetica", Font.ROMAN_BASELINE, 25);
	
	public GiocoGUI() {
		gioco = new Sudoku();
		setTitle("GaiaSudoku");
		setSize(700,600);
		setLocation(400,100);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { 
			if (uscita()) System.exit(0); } } );

		for (int i=0; i<81; i++) {
			JTextField campo = new JTextField(3);
			campo.setHorizontalAlignment(NORMAL);
			campo.setFont(f); campo.addActionListener(this);
			principale.add(campo);
		}
		JPanel comandi = new JPanel(); principale.setBackground(Color.red);
		sol.addActionListener(this); nuova.addActionListener(this); salva.addActionListener(this); carica.addActionListener(this);
		comandi.add(sol); comandi.add(nuova); comandi.add(salva); comandi.add(carica); comandi.setBackground(Color.red);
		add(principale); add(comandi, BorderLayout.SOUTH); sol.setEnabled(false);
		salva.setEnabled(false);
		nuova.setEnabled(false);
		comp = principale.getComponents();
	}
	
	private boolean uscita(){
		   int scelta=JOptionPane.showConfirmDialog( null, "Uscire ?", "Perderai la partita non salvata!", JOptionPane.YES_NO_OPTION);
		   return scelta==JOptionPane.YES_OPTION;
	}//uscita
	
	private void valuta(JTextField t, int i) {
		try {
			String testo = t.getText();
			int rig = i/9;
			int col = i%9;
			ps = new PuntoDiScelta(rig, col);
			int s = Integer.parseInt(testo);
			if (gioco.assegnabile(ps, s)) {
				t.setEnabled(false);
				gioco.assegna(ps,s);
			}
			else {
				JFrame errore = new FinestraErrore("Il numero non rispetta le regole del gioco");
				errore.setVisible(true);
			}
		}
		catch (NumberFormatException e) {
			JFrame errore = new FinestraErrore("Inserisci solo numeri");
			errore.setVisible(true);
		}
		catch (IllegalArgumentException e) {
			JFrame errore = new FinestraErrore("Inserisci solo valori fra 1 e 9");
			errore.setVisible(true);
		}
	}//valuta

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sol) {
			soluzioni = new FinestraSoluzioni();
			setVisible(false);
			soluzioni.setVisible(true);
		}
		if (e.getSource() == salva)
			nuovo();
		if (e.getSource() == nuova)
			nuovaPartita();
		if (e.getSource() == carica) {
			try {
				caricaPartita();
			} catch (IOException e1) {
				JFrame errore = new FinestraErrore("Problemi con il file. Riprova");
				errore.setVisible(true);
			}
		}
		if (e.getSource() == crea) {
			try {
				inputName.setVisible(false);
				creaFile();
			} catch (IOException e1) {
				JFrame f = new FinestraErrore("Problemi con la creazione del file. Riprova");
				f.setVisible(true);
			}
		}
		for (int i=0; i<principale.getComponentCount(); i++ ) {
			if (e.getSource() == comp[i]) {
				sol.setEnabled(true);
				salva.setEnabled(true);
				nuova.setEnabled(true);
				valuta((JTextField) comp[i], i);
			}
		}
	}//actionPerformed
	
	private void creaFile() throws IOException {
		String path = input.getText() + ".txt"; 
		File nuovo = new File(path);
		if (!nuovo.exists())
			nuovo.createNewFile();
		salvataggio();
	}//creaFile()
	
	private void caricaPartita() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new TxtFileFilter());
		int n = fileChooser.showOpenDialog(this);
		File file;
		try {
			if (n == JFileChooser.APPROVE_OPTION) {
	          file = fileChooser.getSelectedFile();
	          BufferedReader read = new BufferedReader(new FileReader(file));
	          String line = "";
	          String linea = "";
	          while(line != null) {
	            line = read.readLine();
	            if (line == null) break;
	            linea+= line;
	          }
	          read.close();
	          StringTokenizer s = new StringTokenizer(linea," ");
	          for (int i=0; i<81; i++) {
	        	  String t = s.nextToken();
	        	  gioco.assegna(new PuntoDiScelta(i/9, i%9), Integer.parseInt(t));
	        	  if (!t.equals("0")) {
		        	  ((JTextField) principale.getComponent(i)).setText(t);
		        	  ((JTextField) principale.getComponent(i)).setEnabled(false);
	        	  }
	        	  else {
	        		  ((JTextField) principale.getComponent(i)).setText("");
	        		  ((JTextField) principale.getComponent(i)).setEnabled(true);
	        	  }
	          }
			}
			nuova.setEnabled(true);
			sol.setEnabled(true);
		}
		catch (FileNotFoundException e) {
			JFrame error = new FinestraErrore("Il file non esiste");
			error.setVisible(true);
		} 
		catch (IllegalArgumentException e) {
			JFrame errore = new FinestraErrore("Nel file non ci sono numeri. Riprova");
			errore.setVisible(true);
		}
	}
	
	void nuovaPartita() {
		GiocoGUI.gioco.soluzioni = new ArrayList<>();
		Sudoku.soluzione = 0;
		sol.setEnabled(false);
		for (int i=0; i<81; i++) {
			((JTextField) principale.getComponent(i)).setText("");
			((JTextField) principale.getComponent(i)).setEnabled(true);
			gioco.assegna(new PuntoDiScelta(i/9, i%9), 0);
        }
		salva.setEnabled(false);
		nuova.setEnabled(false);
		setVisible(true);
	}//nuovaPartita
	
	private void nuovo(){
		   int scelta=JOptionPane.showConfirmDialog( null, "Vuoi creare un nuovo file?", "File", JOptionPane.YES_NO_OPTION);
		   if (scelta==JOptionPane.YES_OPTION) {
			   input.addActionListener(this);
			   crea.addActionListener(this);
			   inputName = new FinestraInput();
			   inputName.setVisible(true);
			} 
		   else {
				try {
					salvataggio();
				} catch (IOException e) {
					JFrame f = new FinestraErrore("Problemi col file. Riprova");
					f.setVisible(true);
				}
		   }
		}//nuovo()
	
	private void salvataggio() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new TxtFileFilter());
		int n = fileChooser.showSaveDialog(this);
		try {
			if (n == JFileChooser.APPROVE_OPTION) {
				File toSave = fileChooser.getSelectedFile();
				if (!toSave.exists() || toSave==null) throw new FileNotFoundException();
				BufferedWriter write = new BufferedWriter(new FileWriter(toSave));
				int[][] base = GiocoGUI.gioco.getBase();
				for (int i=0; i<base.length; i++) {
					for (int j=0; j<base.length; j++) {
						write.append(base[i][j] + " ");
					}
					write.newLine();
					write.flush();
				}
				write.close();
	      }
		}
		catch (FileNotFoundException e) {
			JFrame error = new FinestraErrore("Il file non esiste");
			error.setVisible(true);
		}
	}//salvataggio
	
	final class TxtFileFilter extends FileFilter {
		  public boolean accept(File file) {
		    if (file.isDirectory()) return true;
		    String fname = file.getName().toLowerCase();
		    return fname.endsWith("txt");
		  }
		  public String getDescription() {
		    return "File di testo";
		  }
	}//TxtFileFilter()
	
	private class FinestraErrore extends JFrame{
		public FinestraErrore(String errore) {
			setTitle("Errore!");
			setSize(400,100);
			setLocation(550,350);
			Font f = new Font("Helvetica", Font.PLAIN, 20);
			JLabel messaggio = new JLabel(errore); messaggio.setFont(f); messaggio.setForeground(Color.red); messaggio.setHorizontalAlignment(SwingConstants.CENTER);
			add(messaggio, BorderLayout.CENTER);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { setVisible(false);} } );
		}
	}//FinestraErrore
	
	private class FinestraInput extends JFrame {
		public FinestraInput() {
			setTitle("File");
			setSize(400,100);
			setLocation(550,350);
			Font f = new Font("Helvetica", Font.PLAIN, 20);
			JLabel messaggio = new JLabel("Inserisci il percorso del file>> "); messaggio.setFont(f); messaggio.setForeground(Color.black); messaggio.setHorizontalAlignment(SwingConstants.CENTER);
			add(messaggio, BorderLayout.CENTER); add(input, BorderLayout.EAST); add(crea, BorderLayout.SOUTH);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { setVisible(false);} } );
		}
	}//FinestraInput2
	
}//FinestraGUI


@SuppressWarnings("serial")
class FinestraSoluzioni extends JFrame implements ActionListener {
	
	private JPanel principale = new JPanel(new GridLayout(9,9));
	private int soluzione = 0;
	private JButton next = new JButton("NEXT");
	private JButton previous = new JButton("PREVIOUS");
	private JButton nuova = new JButton("NUOVA PARTITA");
	
	public FinestraSoluzioni() {
		setTitle("GaiaSudoku");
		setSize(700,600);
		setLocation(400,100);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { 
			if (uscita()) System.exit(0); } } );
		
		JPanel comandi = new JPanel(); comandi.setBackground(Color.red); principale.setBackground(Color.red); 
		next.addActionListener(this); previous.addActionListener(this); nuova.addActionListener(this);
		comandi.add(previous); comandi.add(next); comandi.add(nuova);
		add(principale); add (comandi, BorderLayout.SOUTH);
		
		GiocoGUI.gioco.risolvi();
		visibili();
		soluzione(soluzione);
	}//FinestraSoluzioni
	
	private void visibili() {
		if (soluzione == 0) {
			next.setEnabled(true);
			previous.setEnabled(false);
		}
		else if (soluzione == 9) {
			next.setEnabled(false);
			previous.setEnabled(true);
		}
		else {
			next.setEnabled(true);
			previous.setEnabled(true);
		}
	}//visibili
	
	private void soluzione(int n) {
		Font f = new Font("Helvetica", Font.ROMAN_BASELINE, 25);
		int[][] base = GiocoGUI.gioco.soluzioni.get(n);
		principale = new JPanel(new GridLayout(9,9));
		for (int i=0; i<81; i++) {
			int rig = i/9;
			int col = i%9;
			String s = ""+base[rig][col];
			JTextField campo = new JTextField(3); campo.setText(s); campo.setEditable(false);
			campo.setHorizontalAlignment(NORMAL);
			campo.setFont(f); this.remove(principale); 
			principale.add(campo); add(principale);
			revalidate(); repaint();
		}
	}//soluzione
	
	private boolean uscita(){
		   int scelta=JOptionPane.showConfirmDialog( null, "Uscire?", "Torna presto a giocare!", JOptionPane.YES_NO_OPTION);
		   return scelta==JOptionPane.YES_OPTION;
	}//uscita
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == next) {
			soluzione++;
			visibili();
			soluzione(soluzione);
		}
		if (e.getSource() == previous) {
			soluzione--;
			visibili();
			soluzione(soluzione);
		}
		if (e.getSource() == nuova) {
			setVisible(false);
			((GiocoGUI) FinestraIniziale.finestraGioco).nuovaPartita();
		}
	}//actionPerformed
	
}//FinestraSoluzioni


@SuppressWarnings("serial") 
class FinestraIniziale extends JFrame implements ActionListener{
	
	protected static JFrame finestraGioco;
	private JButton inizia ;
	
	public FinestraIniziale() {
		setTitle("GaiaSudoku");
		setSize(600,330);
		setLocation(500,200);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { 
			if (uscita()) System.exit(0); } } );
		
		Font f2 = new Font("Helvetica", Font.BOLD, 15);
		JLabel benvenuto = new JLabel("Benvenuto!"); benvenuto.setFont(f2);
		
		String ins = "                                          Istruzioni: \n"
				+ "                      1) Inserisci solo i numeri fra 1 e 9\n"
				+ "  2) Una volta inserito un numero non è possibile modificarlo\n"
				+ "   3) Puoi vedere le soluzioni possibili in qualsiasi momento!\n"
				+ "4) Non puoi inserire un numero già presente sulla stessa riga,\n"
				+ "                               colonna o griglia 3x3!\n\n"
				+ "                                 Buon Divertimento!!\n\n";
		
		Font f1 = new Font("Helvetica", Font.ROMAN_BASELINE, 20);
		JTextArea istruzioni = new JTextArea(ins, 20, 20); istruzioni.setEditable(false); istruzioni.setFont(f1); 
		
		inizia = new JButton("Clicca qui per iniziare a giocare!");
		inizia.addActionListener(this);
		
		JPanel title = new JPanel(); title.add(benvenuto, BorderLayout.NORTH); title.setBackground(Color.magenta);
		JPanel pannello = new JPanel(); pannello.setBackground(Color.white);
		JPanel south = new JPanel(); south.add(inizia);
		pannello.add(title, BorderLayout.NORTH); pannello.add(istruzioni, BorderLayout.CENTER);
		add(pannello); add(south,BorderLayout.SOUTH);
	}//FinestraIniziale

	private boolean uscita(){
		   int scelta=JOptionPane.showConfirmDialog( null, "Uscire ?", "Non hai ancora giocato!", JOptionPane.YES_NO_OPTION);
		   return scelta==JOptionPane.YES_OPTION;
	}//uscita

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == inizia) {
			SudokuGUI.finestraIniziale.setVisible(false);
			finestraGioco = new GiocoGUI();
			finestraGioco.setVisible(true);
		}
	}//actionPerformed
}//FinestraIniziale

public class SudokuGUI {
	
	public static JFrame finestraIniziale;

	public static void main (String[] args) {
		finestraIniziale = new FinestraIniziale();
		finestraIniziale.setVisible(true);
	}//main
	
}//SudokuGUI
