package it.beije.oort.girardi.dataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;

import it.beije.oort.db.DBManager;
import it.beije.oort.girardi.hibernate.RubricaHDB;
import it.beije.oort.girardi.inOut.RubricaCSV;
import it.beije.oort.girardi.inOut.RubricaXML;
import it.beije.oort.rubrica.Contatto;

public class RubricaDB {

	private static final String PATH_FILES = "C:\\Users\\Padawan05\\Desktop\\file_testo\\";
	private static String file_destinazione = "RubricaFromDB";

	
// ------------ METODI ------------
	
//1) Visualizzazione contatti -----------------------------------------
//visualizza tutti i contatti nella lista:
	public static void visualizzaTutti (Connection connection) {
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;

		try {
			ps = connection.prepareStatement("SELECT * FROM rubrica");
			rs = ps.executeQuery();			
			
			//fase di stampa a terminale:
			System.out.println("ID, COGNOME, NOME, TELEFONO, EMAIL");
			while (rs.next() ) {
				System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+
					rs.getString(3)+"   "+rs.getString(4)+"   "+rs.getString(5));
					if (++count % 30 == 0) { //mostra 30 contatti alla volta
						System.out.print("digitare 1 per vedere la seconda pagina: ");
						String si = myInput.nextLine();
						if (!(si.contentEquals("1")))
							break;
					}
			}
			System.out.println("hai visualizzato " + count + " contatti\n");
			
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//ricerca e visualizza contatto per ID:
	public static Contatto visualizzaId (Connection connection,int id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Contatto c = null;
	
		try {	
			ps = connection.prepareStatement("SELECT * FROM rubrica where id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
		
			rs.next();
			System.out.println("ID, COGNOME, NOME, TELEFONO, EMAIL");	
			System.out.println(rs.getString(1)+"   "+rs.getString(2)+"   "+
					rs.getString(3)+"   "+rs.getString(4)+"   "+rs.getString(5));
			//nome cognome telefono email
			c = new Contatto(rs.getString(3),rs.getString(2), 
									rs.getString(4), rs.getString(5));
			
			rs.close();
			
			
		} catch (SQLException sqlException) {
//			sqlException.printStackTrace();
			System.out.println("Id non disponibile");
			return c;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return c;
	}

// viualizza rubrica o contatto da Database
	public static void visualizza (Connection connection) {
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		int id = 0;	
		String in = "";
		
		try {
			System.out.println("digitare 1 o 2 per le seguenti azioni: ");
			System.out.println("\t 1) visualizza tutti i contatti");
			System.out.println("\t 2) visualizza singolo contatto");
			in = myInput.nextLine();
			
			switch (in) {
			case "1": 
				RubricaDB.visualizzaTutti(connection);
				break;
			case "2": 
				System.out.print("inserire l'id del contatto che si vuole visualizzare: ");
				id = (int) Integer.parseInt(myInput.nextLine());
				
				RubricaDB.visualizzaId(connection, id);
				break;
			default:
				System.out.println("inserimento non valido");
			}
			
		} catch (NumberFormatException nfe) {
//				nfe.printStackTrace();
			System.out.println("inserimento non valido");
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
			System.out.println("riprova");
		} catch (NoSuchElementException nse) {
			nse.printStackTrace();
			System.out.println("riprova");
		}
	}
	
	
	
//2) Modifica / Cancellazione ---------------------------------
//cancella contatto o modifica contatto tramite id:
	public static void CancModMenu  (Connection connection) {
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		int id = 0;	
		String in = "";
		
		try {
			System.out.println("digitare 1 o 2 per le seguenti azioni: ");
			System.out.println("\t 1) cancella contatto");
			System.out.println("\t 2) modifica contatto");
			in = myInput.nextLine();
			
			switch (in) {
			case "1": 
				System.out.print("inserire l'id del contatto che si vuole cancellare: ");
				id = (int) Integer.parseInt(myInput.nextLine());
				RubricaDB.deleteId(connection, id);
				break;
				
			case "2": 
				System.out.print("inserire l'id del contatto che si vuole modificare: ");
				id = (int) Integer.parseInt(myInput.nextLine());
				RubricaDB.modificaContatto(connection, id);
				break;
				
			default:
				System.out.println("inserimento non valido");
			}
			System.out.println("");
			
		} catch (NumberFormatException nfe) {
		//	nfe.printStackTrace();
			System.out.println("inserimento non valido");
		}
	}

//cancella id dal database
	public static void deleteId(Connection connection,int id) {
		Contatto contatto = visualizzaId(connection, id);
//			System.out.println("contatto da eliminare: " + contatto);
		
		PreparedStatement ps = null;
		try {	
			ps = connection.prepareStatement("DELETE FROM rubrica where id = ?");
			ps.setInt(1, id);
			
			ps.execute();
		
			System.out.println("id contatto eliminiato");
			
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
// Modifica contatto dal Database
	public static void modificaContatto (Connection connection,int id) {
		Contatto contatto = visualizzaId(connection, id);
//		System.out.println("contatto da modificare: " + contatto);
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		String input = ""; 
		
		//cognome, nome, telefono, email
		System.out.println("inserire i campi che si voglioni modificare:");
		System.out.println("(se non si intende modificare un campo premere invio)");
		System.out.print("\t inserire il nome: ");
		input = myInput.nextLine();
		if (!(input.contentEquals("")))
			contatto.setNome(input);
		System.out.print("\t inserire il cognome: ");
		input = myInput.nextLine();
		if (!(input.contentEquals("")))
			contatto.setCognome(input);
		System.out.print("\t inserire il telefono: ");
		input = myInput.nextLine();
		if (!(input.contentEquals("")))
			contatto.setTelefono(input);
		System.out.print("\t inserire la email: ");
		input = myInput.nextLine();
		if (!(input.contentEquals("")))
			contatto.setEmail(input);
		
		System.out.println("contatto aggiornato: " + contatto);
		
		if (contatto.getNome().trim().equals("") && contatto.getCognome().trim().equals("") &&
			contatto.getTelefono().trim().equals("") && contatto.getEmail().trim().equals("") )
			System.out.println("ALERT: il contatto � vuoto e non verr� inserito\n");
		else {
			RubricaDB.updateContatto(connection, contatto, id);
			System.out.println("");
		}
	}

//update contatto. 
	public static void updateContatto(Connection connection,Contatto contatto, int id) {
		PreparedStatement ps = null;
		
		try {	
			ps = connection.prepareStatement("UPDATE rubrica set cognome = ?, nome = ?,"
											+ " telefono = ?, email = ? where id = " + id);
			ps.setString(1, contatto.getCognome());
			ps.setString(2, contatto.getNome());
			ps.setString(3, contatto.getTelefono());
			ps.setString(4, contatto.getEmail());
			
			ps.execute();
			
			System.out.println("contatto modificato correttamente");
			
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	
//3) inserimento contatto nel DataBase ----------------------------------
	public static void inserisciContatto (Connection connection) {
		Contatto contatto = new Contatto();
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		
		//cognome, nome, telefono, email
		System.out.println("inserire i campi relativi al contatto:");
		System.out.println("(se non si conosce il campo richiesto premere invio)");
		System.out.print("\t inserire il nome: ");
		contatto.setNome(myInput.nextLine());
		System.out.print("\t inserire il cognome: ");
		contatto.setCognome(myInput.nextLine());
		System.out.print("\t inserire il telefono: ");
		contatto.setTelefono(myInput.nextLine());
		System.out.print("\t inserire la email: ");
		contatto.setEmail(myInput.nextLine());
		
		if (contatto.getNome().trim().equals("") && contatto.getCognome().trim().equals("") &&
			contatto.getTelefono().trim().equals("") && contatto.getEmail().trim().equals("") )
			System.out.println("ALERT: il contatto � vuoto e non verr� inserito\n");
		else {
			ToDB.insertContatto(connection, contatto);
			System.out.println("");
		}
	}
	
	
	
//4) esporta rubrica
//menu
	public static void menuExport (Connection connection) 
			throws ParserConfigurationException, TransformerException, IOException {
		Scanner myInput = new Scanner(System.in);  //apre lo scanner
		int id = 0;	
		String in = "";
		
		try {
			System.out.println("digitare 1 o 2 per le seguenti azioni: ");
			System.out.println("\t 1) esporta in formato csv");
			System.out.println("\t 2) esporta in formato xml");
			in = myInput.nextLine();
			
			switch (in) {
			case "1":  // csv
				RubricaDB.esportaCSV(connection);
				break;
			case "2":  // xml
				RubricaDB.esportaXML(connection);
				break;
			default:
				System.out.println("");
			}
			
		} catch (NumberFormatException nfe) {
//				nfe.printStackTrace();
			System.out.println("inserimento non valido");
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
			System.out.println("riprova");
		} catch (NoSuchElementException nse) {
			nse.printStackTrace();
			System.out.println("riprova");
		}
	}
	
//esporta in un file xml
	public static void esportaXML (Connection connection) 
		throws ParserConfigurationException, TransformerException, IOException {
		List<Contatto> listContatti = new ArrayList<>();
		
		//prendo la lista dei Contatti dal database:
		listContatti = FromDB.selectContatti(connection);
		
		//scrittura di un nuovo file xml con la List di Contatti:
		RubricaXML.writeContatti(listContatti, PATH_FILES + file_destinazione + ".xml");
		
		System.out.println("percorso file rubrica esportata: " + PATH_FILES 
												+ file_destinazione + ".xml");
		System.out.println("");
	}

//esporta in un file csv
	public static void esportaCSV (Connection connection) throws IOException {
		List<Contatto> listContatti = new ArrayList<>();
		
		//prendo la lista dei Contatti dal database:
		listContatti = FromDB.selectContatti(connection);
		
		//scrittura di un nuovo file csv con la List di Contatti:
		RubricaCSV.writeContatti(listContatti, PATH_FILES + file_destinazione + ".csv");
		
		System.out.println("percorso file rubrica esportata: " + PATH_FILES 
												+ file_destinazione + ".csv");
		System.out.println("");
	}
	
	
	
// -------------- MAIN -----------------
//Permette di gestire la rubrica di mySQL inserendo comandi da tastiera:
	public static void main(String[] args) 
			throws ParserConfigurationException, TransformerException, IOException {
		//A) collegamento al DataBase (db):
		Connection connection = null;
		
		try {
			connection = DBManager.getMySqlConnection(DBManager.DB_URL, 
													  DBManager.DB_USER, 
													  DBManager.DB_PASSWORD);
			System.out.println("connection is open? " + !connection.isClosed());
	
			
			//B) interfaccia utente: men� di selezione:
			System.out.print("Benvenuto!");
			
//			CORPO: while (true) {
				System.out.println("\nEcco le azioni eseguibili sulla tua rubrica:");
				final String[] menu = {"1) Visualizzazione contatti", "2) Modifica / Cancellazione", 
						    "3) Inserimento contatto", "4) Esporta rubrica", "5) Termina programma"};
				System.out.println("\t" + menu[0] + "\n\t" + menu[1] + "\n\t" + menu[2] 
									+ "\n\t" + menu[3] + "\n\t" + menu[4] );
			CORPO: while (true) {
				System.out.print("Digitare il numero legato all'azione che si vuole eseguire: ");
				
				
				//C) prende parole da tastiera tramite scanner:
				Scanner myInput = new Scanner(System.in); 
				String azione = "";
				try {
					azione = myInput.nextLine();
				} catch (NoSuchElementException nse) {
					nse.printStackTrace();
				}
				
				//D) Azioni eseguibili:
				switch (azione) {
				case "1" : 	//1) Visualizzazione contatti
					System.out.println("Hai selto di eseguire: " + menu[0]);
					RubricaDB.visualizza(connection);
					break;
										
				case "2" : 	//2) Modifica / Cancellazione
					System.out.println("Hai selto di eseguire: " + menu[1]);
					RubricaDB.CancModMenu (connection);
					break;
										
				case "3" :	//3) Inserimento contatto
					System.out.println("Hai selto di eseguire: " + menu[2]);
					RubricaDB.inserisciContatto (connection); 
					break;
										
				case "4" : 	//4) Esporta rubrica
					System.out.println("Hai selto di eseguire: " + menu[3]);
					RubricaDB.menuExport(connection);
					break;

				case "5" :	//5) Termina programma
					System.out.println("Hai selto di eseguire: " + menu[4]);
					break CORPO; //esce dal while
						
				default: 	//Azione incompresa
					System.out.println("La scelta non � andata a buon fine, prego riprovare.\n");
				} //switch
				System.gc();	//garbage collector
			} //while
			
			
		//E) chiusura connessione con il database:
		} catch (ClassNotFoundException cnfEx) {
			cnfEx.printStackTrace();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("BYE!!");
	} //main

} //class
