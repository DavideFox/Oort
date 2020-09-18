package it.beije.oort.file.rubrica;

import java.util.ArrayList;
import java.util.Random;

public class Contatto {
	private String nome;
	private String cognome;
	private String cell;
	private String email;
	
	public Contatto() {
	}
	
	public Contatto(ArrayList<String> nomi, ArrayList<String> cognomi) {
		Random r = new Random();
		this.nome = 	cognomi.get(r.nextInt(cognomi.size()));
		this.cognome = 	nomi.get(r.nextInt(nomi.size()));
		this.cell = 	GeneraNumero.generaNumero();
		this.email =	GeneraMail.generaMail(nome, cognome);
	}
	
	public String getCell() {
		return cell;
	}
	public String getCognome() {
		return cognome;
	}
	public String getEmail() {
		return email;
	}
	public String getNome() {
		return nome;
	}
	
	public void setCell(String cell) {
		this.cell = cell;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the formatted string following the CSV convention with quotes.
	 */
	public String toFormattedString() {
		return new StringBuilder("\"").append(cognome).append("\";\"").append(nome).append("\";\"").append(email)
				.append("\";\"").append(cell).append("\"").toString();
	}

	public String toString() {
		return new StringBuilder("Nome: " + this.getNome() + ". Cognome: " + this.getCognome() + ". Email: "
				+ this.getEmail() + ". Telefono: " + this.getCell()).toString();
	}
	
	public static String getContattoFormattatoToString(ArrayList<Contatto> rubrica, int i, Random r) {
		StringBuilder s = new StringBuilder();
		Contatto c = rubrica.get(i);
		if ((r.nextInt(3) + 1) != 1) {
			s.append(c.getCognome());
		}
		s.append(";");
		if ((r.nextInt(5) + 1) != 1) {
			s.append(c.getNome());
		}
		s.append(";").append(c.getCell()).append(";").append(c.getEmail());
		return s.toString();
	}
}
