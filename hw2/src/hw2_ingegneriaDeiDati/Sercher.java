package hw2_ingegneriaDeiDati;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PhraseQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Sercher {
	public static void main(String[] args) throws IOException {
		
		Directory directory= FSDirectory.open(Paths.get("C:\\Users\\lazla\\git\\Homework2_ingegneriaDeiDati\\hw2\\index"));
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		
		/*statistiche degli indici*/
		Collection<String> indexedFields = FieldInfos.getIndexedFields(reader);
        for (String field : indexedFields) {
            System.out.println(searcher.collectionStatistics(field));
        }
        
		
		/*prendo da linea di comando la query*/
		System.out.println("cercare per titolo o cerca per contenuto?");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		
		Query query=null;

		if(input.equals("titolo")) {
			System.out.println("che titolo vuoi cercare?");
			String titolo = scanner.nextLine();
			/*costruzione query*/
			Builder phraseQuery = new PhraseQuery.Builder();
			phraseQuery.add(new Term("titolo", titolo)); //ricerca per titolo completo
	        query=phraseQuery.build();
		}
		else if(input.equals("contenuto")) {
			System.out.println("che contenuto vuoi cercare?");
			String contenuto = scanner.nextLine();
			scanner.close();
			/*costruzione query*/
			/*caso in cui si voglia cercare solo per termini*/
			if(extractTextInQuotes(contenuto)==null) {
				QueryParser parser = new QueryParser("contenuto", new StandardAnalyzer());
				try {
					query = parser.parse(contenuto);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
			/*caso in cui si voglia crecare l'intera frase*/
				Builder phraseQuery = new PhraseQuery.Builder();
				String[] parole = contenuto.replace("\"", "").split(" "); //separo parola per parola
		        for (String parola : parole) {
		            phraseQuery.add(new Term("contenuto", parola.toLowerCase()));
		        }
		        query=phraseQuery.build();
			}
		}
		
		/*stampa i primi 10 documenti più importanti*/
		TopDocs hits = searcher.search(query, 10);
		if(hits.scoreDocs.length==0) {
			System.out.println("documento non trovato ");
		}
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document doc = searcher.doc(scoreDoc.doc); 
			
			System.out.println("documento: "+ doc.get("titolo")+" punteggio: "+scoreDoc.doc);
			
		}
		
	}
	
    public static String extractTextInQuotes(String input) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}
