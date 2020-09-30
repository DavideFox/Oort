package it.beije.oort.db.hybernate.biblioteca;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class PrestitoUtility {

public static List<Prestito> visualizza() {

		String hql = "SELECT p FROM Prestito as p";
		Session session = HybSessionFactorybiblio.openSession();
		List<Prestito> prestiti = session.createQuery(hql).list();

		session.close();

		return prestiti;
	}

public static void inserisci() {
	Scanner sc = new  Scanner(System.in);

	Session session = HybSessionFactorybiblio.openSession();

	Transaction transaction = session.beginTransaction();
	Prestito prestito = new Prestito();

	System.out.println("Inserimento prestito:");

	int scelta = 0;
	List<Libro> libri = LibroUtility.visualizza();
	do {
		for(int i = 0; i < libri.size(); i++) {
			System.out.println(i + ") " + libri.get(i));
		}
		try {
			System.out.print("Id libro: ");
			scelta = Integer.parseInt(sc.nextLine());
		}
		catch (NumberFormatException e) {
			System.out.println("ERRORE: inserire un valore valido!");
			continue;
		}
		if(scelta >= libri.size() || scelta < 0) {
			System.out.println("ERRORE: inserire un valore valido!");
		}
	} while(scelta >= libri.size() || scelta < 0);

	prestito.setLibro(libri.get(scelta).getId());	

	scelta = 0;
	List<Utente> utenti = UtenteUtility.visualizza();
	do {
		for(int i = 0; i < utenti.size(); i++) {
			System.out.println(i + ") " + utenti.get(i));
		}
		try {
			System.out.print("Id utente: ");
			scelta = Integer.parseInt(sc.nextLine());
		}
		catch (NumberFormatException e) {
			System.out.println("ERRORE: inserire un valore valido!");
			continue;
		}
		if(scelta >= utenti.size() || scelta < 0) {
			System.out.println("ERRORE: inserire un valore valido!");
		}
	} while(scelta >= utenti.size() || scelta < 0);

	prestito.setUtente(utenti.get(scelta).getId());	

	System.out.print("Data inizio prestito: ");
	prestito.setDataInizio(sc.nextLine());
	
	System.out.print("Data fine prestito: ");
	prestito.setDataFine(sc.nextLine());

	System.out.print("Note: ");
	prestito.setNote(sc.nextLine());

	session.save(prestito);
	transaction.commit();

	session.close();		
	System.out.println("Prestito inserito correttamente!");


}

}
