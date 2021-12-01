package Bertolino.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Sudoku extends Backtracking<PuntoDiScelta,Integer> {
	
	private int[][] base;
	private List<PuntoDiScelta> puntiDiScelta;
	protected List<int[][]> soluzioni = new ArrayList<>();
	protected static int soluzione = 0;
	
	public Sudoku() {
		base = new int[9][9];
		for (int i=0; i<base.length; i++) {
			for (int j=0; j<base[0].length; j++) {
				base[i][j] = 0;
			}
		}
		puntiDiScelta = puntiDiScelta();
	}//Sudoku()
	
	public int[][] getBase() {
		int[][] ris = new int[9][9];
		for (int i=0; i<base.length; i++) {
			for (int j=0; j<base[0].length; j++) {
				ris[i][j] = base[i][j];
			}
		}
		return ris;
	}//getBase()

	private boolean verificaGriglia(int i, int j, int v) {
		int r = 0;
		int c = 0;
		
		if (i<=2 && j<=2);
		else if (i<=2 && j<=5)
			c = 3;
		else if (i<=2 && j<=8) {
			c = 6;
		}
		else if (i<=5 && j<=2)
			r = 3;
		else if (i<=5 && j<=5) {
			r = 3;
			c = 3;
		}
		else if (i<=5 && j<=8) {
			r = 3;
			c = 6;
		}
		else if (i<=8 && j<=2)
			r = 6;
		else if (i<=8 && j<=5) {
			r = 6;
			c = 3;
		}
		else {
			r = 6;
			c = 6;
		}
		
		for (int rig=r+0; rig<r+3; rig++) {
			for (int col=c+0; col<c+3; col++) {
				if(base[rig][col] == v)
					return false;
			}
		}
		return true;
	}//verificaGriglia
	
	private void imposta(int i, int j, int v) {
		base[i][j] = v;
	}//imposta
	
	@Override
	protected boolean assegnabile( PuntoDiScelta ps, Integer s ) {
		if (s<1 || s>9) throw new IllegalArgumentException();
		int i = ps.getI();
		int j = ps.getJ();
		if (base[i][j] !=0) {
			if (base[i][j] == s) {
				if( esisteSoluzione(ps) ) scriviSoluzione(ps);
				else tentativo( puntiDiScelta, prossimoPuntoDiScelta(puntiDiScelta,ps) );}
			return false;
		}
		for (int rig=0; rig<base.length; rig ++) {
			if (base[rig][j] == s) return false;
		}
		for (int col=0; col<base.length; col++) {
			if (base[i][col] == s) return false;
		}
		if (!verificaGriglia(i,j,s))
			return false;
		return true;
	}//assegnabile
	
	@Override
	protected void assegna( PuntoDiScelta ps, Integer s ) {
		imposta(ps.getI(),ps.getJ(),s);
	}//assegna
	
	@Override
	protected List<PuntoDiScelta> puntiDiScelta() {
		List<PuntoDiScelta> puntiDiScelta = new ArrayList<>(81);
		for (int i=0; i<base.length; i++) {
			for (int j=0; j<base.length; j++) {
				puntiDiScelta.add(new PuntoDiScelta(i,j));
			}
		}
		return puntiDiScelta;
	}//puntiDiscelta
	
	@Override
	protected void deassegna( PuntoDiScelta ps, Integer s ) {
		base[ps.getI()][ps.getJ()] = 0;
	}//deassegna
	
	@Override
	protected void scriviSoluzione( PuntoDiScelta p) {
		int[][] sol = new int[9][9];
		for (int i=0; i<base.length; i++) {
			for (int j=0; j<base.length; j++) {
				sol[i][j] = base[i][j];
			}
		}
		soluzioni.add(sol);
		soluzione++;
	}//scriviSoluzione

	@Override
	protected Collection<Integer> scelte(PuntoDiScelta p) {
		Integer[] pos = {1,2,3,4,5,6,7,8,9};
		List<Integer> possibilità = Arrays.asList(pos);
		return possibilità;
	}//scelte
	
	@Override 
	protected boolean ultimaSoluzione(PuntoDiScelta p) {
		if (soluzione == 11)
			return true;
		return false;
	}//ultimaSoluzione
	
	@Override
	protected boolean esisteSoluzione(PuntoDiScelta p) {
		if (p.getI() == base.length-1 && p.getJ() == base.length-1)
			return true;
		return false;
	}//esisteSoluzione
	
	@Override
	protected void risolvi() {
		tentativo(puntiDiScelta, new PuntoDiScelta(0,0));
	}//risolvi

}//Sudoku
