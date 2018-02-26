import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

/**
 * Class which we will use to test XML parsing.
 * @author thaggus
 *
 */
public class XMLTest {
	
	static String xmlIn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<BookCatalogue>\n" + 
			"<Book>\n" + 
			"    <Title>Yogasana Vijnana: the Science of Yoga</Title>\n" + 
			"    <Author>Dhirendra Brahmachari</Author>\n" + 
			"    <Date>1966</Date>\n" + 
			"    <ISBN>81-40-34319-4</ISBN>\n" + 
			"    <Publisher>Dhirendra Yoga Publications</Publisher>\n" + 
			"    <Cost currency=\"INR\">11.50</Cost>\n" + 
			"</Book>\n" + 
			"<Book>\n" + 
			"    <Title>The First and Last Freedom</Title>\n" + 
			"    <Author>J. Krishnamurti</Author>\n" + 
			"    <Date>1954</Date>\n" + 
			"    <ISBN>0-06-064831-7</ISBN>\n" + 
			"    <Publisher>Harper &amp; Row</Publisher>\n" + 
			"    <Cost currency=\"USD\">2.95</Cost>\n" + 
			"</Book>\n" + 
			"</BookCatalogue>";
	
	List<Book> bookList = new ArrayList<Book>();
	
	class myXMLFactory {
		XMLInputFactory myInputFac = XMLInputFactory.newInstance();
		
		XMLEventReader getEventReaderInstance(String xmlInput) throws XMLStreamException {
			StringReader stream = new StringReader(xmlInput);
			return myInputFac.createXMLEventReader(stream);
		}
	}
	
	class Book {
		String title, author, date, isbn, cost;
		
		public String toString() {
			return author + ", " + title + ", " + date + ". ISBN: " + isbn
					+ " Cost: " + cost;
		}
	}
	
	XMLTest() {
		myXMLFactory fac = new myXMLFactory();
		XMLEventReader r;
		try {
			r = fac.getEventReaderInstance(xmlIn);
			while (r.hasNext()) {
				XMLEvent e = r.nextEvent();
				switch (e.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement start = e.asStartElement();
					switch (start.getName().getLocalPart().toLowerCase()) {
					case "book":
						bookList.add(readBookElement(r));
						break;
					}
				}
				System.out.println(e.toString());
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		for (Book b : bookList) {
			System.out.println(b);
		}
	}
	
	private Book readBookElement(XMLEventReader r) {
		Book out = new Book();
		boolean done = false;
		while (r.hasNext() && !done) {
			try {
				XMLEvent e = r.nextEvent();
				switch (e.getEventType()) {
				case XMLStreamConstants.END_ELEMENT:
					EndElement end = e.asEndElement();
					switch (end.getName().getLocalPart().toLowerCase()) {
					case "book":
						done = true;
						break;
					default:
						//do nothing
					}
					break;
				case XMLStreamConstants.START_ELEMENT:
					StartElement start = e.asStartElement();
					switch (start.getName().getLocalPart().toLowerCase()) {
					case "title":
						out.title = r.nextEvent().asCharacters().toString();
						break;
					case "author":
						out.author = r.nextEvent().asCharacters().toString();
						break;
					case "date":
						out.date = r.nextEvent().asCharacters().toString();
						break;
					case "isbn":
						out.isbn = r.nextEvent().asCharacters().toString();
						break;
					case "cost":
						out.cost = r.nextEvent().asCharacters().toString();
						break;
					}
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		return out;
	}
	
	public static void main(String[] args) {
		new XMLTest();
	}

}
