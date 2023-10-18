package hw2_ingegneriaDeiDati;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class indexer{

	public static void main(String[] args)throws IOException {

		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		CharArraySet stopWords = new CharArraySet(Arrays.asList("in", "dei", "di"), true);
		perFieldAnalyzers.put(" titolo", new WhitespaceAnalyzer());
		perFieldAnalyzers.put(" contenuto", new StandardAnalyzer(stopWords));
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFieldAnalyzers);
		Directory indexDirectory = FSDirectory.open(Paths.get("C:\\Users\\lazla\\git\\Homework2_ingegneriaDeiDati\\hw2\\index"));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setCodec(new SimpleTextCodec());
		IndexWriter writer = new IndexWriter(indexDirectory, config);
				
		File fileDirectory = new File("C:\\Users\\lazla\\git\\Homework2_ingegneriaDeiDati\\hw2\\documenti");
		File[] files = fileDirectory.listFiles();
		for(File f: files) {
			StringBuilder contenuto = new StringBuilder();

			try (FileInputStream fis = new FileInputStream(f);
			     InputStreamReader isr = new InputStreamReader(fis);
			     BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					contenuto.append(line).append("\n");
				     }
			
			 } catch (IOException e) {
			     e.printStackTrace();
			 }
            Document doc = new Document();
            doc.add(new TextField("titolo", f.getName(), Field.Store.YES));
            doc.add(new TextField("contenuto", contenuto.toString(), Field.Store.YES));
            writer.addDocument(doc);
    		writer.commit();
            }
		writer.close();
	}
}
