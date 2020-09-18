package it.beije.oort.file.sala;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RubricaToolset {
	
	public static List<Contatto> readCsvToList(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String[] intestazione = br.readLine().split(";", -1);
		int a=5, b=5, c=5, d=5;
		for(int i=0; i<intestazione.length; i++) {
			switch(intestazione[i].toUpperCase())
			{
				case "NOME":
					a=i;
					break;
				case "COGNOME":
					b=i;
					break;
				case "TELEFONO":
					c=i;
					break;
				case "EMAIL":
					d=i;
					break;
			}
		}
		
		List<Contatto> contatti = new ArrayList<Contatto>();
		while(br.ready()) {
			String[] temp = br.readLine().split(";", -1);
			contatti.add(new Contatto(temp[a], temp[b], temp[c], temp[d]));
		}
		br.close();
		return contatti;
	
	}

	public static List<Contatto> readXmlToList(String filePath) 
			throws IOException, ParserConfigurationException, SAXException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(filePath);
	    Element element = document.getDocumentElement();
	    
	    List<Contatto> rubrica = new ArrayList<Contatto>();
	    NodeList contatti = element.getChildNodes();
	    for (int i = 0; i < contatti.getLength(); i++) {
	    	Node node = contatti.item(i);
	    	if (node instanceof Element) {
	        	Element contatto = (Element) node;
	        	Contatto beanContatto = new Contatto();
	        	NodeList valori = contatto.getChildNodes();
	            for (int j = 0; j < valori.getLength(); j++) {
	            	Node n = valori.item(j);
	            	if (n instanceof Element) {
	            		Element valore = (Element) n;
	            		switch (valore.getTagName()) {
						case "nome":
							beanContatto.setNome(valore.getTextContent());
							break;
						case "cognome":
							beanContatto.setCognome(valore.getTextContent());
							break;
						case "telefono":
							beanContatto.setTelefono(valore.getTextContent());
							break;
						case "email":
							beanContatto.setEmail(valore.getTextContent());
							break;
	
						default:
							System.out.println("elemento in contatto non riconosciuto");
							break;
						}
	            	}
	            }
	            rubrica.add(beanContatto);
	    	}
	    }
	    return rubrica;
	}

	public static void contattoToXml (List<Contatto> list, String xmlPath) 
			throws IOException, ParserConfigurationException, SAXException, TransformerException {
		File f = new File(xmlPath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document;
	    if(f.exists()) {  
	        document = builder.parse(f);
	        Element docElement = document.getDocumentElement(); 
	        
	        for (Contatto c : list) {
	        	Element contatto = document.createElement("contatto");
	        	
	        	Element nome = document.createElement("nome");
	        	Element cognome = document.createElement("cognome");
	        	Element telefono = document.createElement("telefono");
	        	Element email = document.createElement("email");
	        	
	        	nome.setTextContent(c.getNome());
	        	cognome.setTextContent(c.getCognome());
	        	telefono.setTextContent(c.getTelefono());
	        	email.setTextContent(c.getEmail());
	        	
	        	contatto.appendChild(nome);
	        	contatto.appendChild(cognome);
	        	contatto.appendChild(telefono);
	        	contatto.appendChild(email);
	
	        	docElement.appendChild(contatto);
	        }
	    } else {
	    	document = builder.newDocument();
	        Element docElement = document.createElement("contatti");
	        document.appendChild(docElement);
	        
	        for (Contatto c : list) {
	        	Element contatto = document.createElement("contatto");
	        	
	        	Element nome = document.createElement("nome");
	        	Element cognome = document.createElement("cognome");
	        	Element telefono = document.createElement("telefono");
	        	Element email = document.createElement("email");
	        	
	        	nome.setTextContent(c.getNome());
	        	cognome.setTextContent(c.getCognome());
	        	telefono.setTextContent(c.getTelefono());
	        	email.setTextContent(c.getEmail());
	        	
	        	contatto.appendChild(nome);
	        	contatto.appendChild(cognome);
	        	contatto.appendChild(telefono);
	        	contatto.appendChild(email);
	
	        	docElement.appendChild(contatto);
	        }
	    }
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		
		StreamResult result = new StreamResult(f);

		transformer.transform(source, result);
	    System.out.println("Scritto file XML con successo.");
	}
	
	public static void contattoToXml (Contatto contatto, String xmlPath) 
			throws IOException, ParserConfigurationException, SAXException, TransformerException {
		List<Contatto> temp = new ArrayList<Contatto>();
		temp.add(contatto);
		contattoToXml(temp, xmlPath);
	}

	public static void contattoToCsv (List<Contatto> list, String csvPath) throws IOException {
		Random r = new Random();
		File f = new File(csvPath);
		boolean ex = f.exists();
		BufferedWriter bf = Files.newBufferedWriter(f.toPath(),
				StandardCharsets.UTF_8,
				StandardOpenOption.APPEND,
				StandardOpenOption.CREATE);
		if(!ex) bf.write("NOME;COGNOME;TELEFONO;EMAIL\n");
		for(Contatto c : list) {
			if(r.nextInt(5)==1) bf.write(c.toCsvSimpleNoNome());		//rimuovere queste due per
			else if(r.nextInt(3)==1) bf.write(c.toCsvSimpleNoCognome());//scrivere sempre tutti i campi
			else bf.write(c.toCsvSimple());
		}
		System.out.println("File CSV scritto con succeso!");
		bf.close();
	}
	
	public static void contattoToCsv (Contatto contatto, String csvPath) throws IOException {
		List<Contatto> temp = new ArrayList<Contatto>();
		temp.add(contatto);
		RubricaToolset.contattoToCsv(temp, csvPath);
	}
}
