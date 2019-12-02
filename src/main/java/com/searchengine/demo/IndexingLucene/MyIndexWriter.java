package com.searchengine.demo.IndexingLucene;

import com.searchengine.demo.Model.Movie;
import com.searchengine.demo.Model.Path;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class MyIndexWriter {
    private Directory directory;
    private IndexWriter ixwriter;
    private FieldType type;
    public MyIndexWriter() throws IOException {
        directory = FSDirectory.open(Paths.get(Path.indexingDir));
        IndexWriterConfig indexConfig=new IndexWriterConfig(new WhitespaceAnalyzer());
        indexConfig.setMaxBufferedDocs(10000);
        ixwriter = new IndexWriter( directory, indexConfig);
        type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setStored(false);
        type.setStoreTermVectors(true);
    }

    public void index(Movie movie) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("imdbID", movie.getImdbID().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("plot", movie.getPlot().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("actors",movie.getActors().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("title",movie.getTitle().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("director",movie.getDirector().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("year",movie.getYear(), Field.Store.YES));
        doc.add(new TextField("genre",movie.getGenre().toLowerCase(), Field.Store.YES));
        doc.add(new TextField("score",movie.getImdbRated(), Field.Store.YES));
        doc.add(new StoredField("poster",movie.getPoster()));
        ixwriter.addDocument(doc);
        ixwriter.commit();
    }

    public void close() throws IOException {
        ixwriter.close();
        directory.close();
    }
}
