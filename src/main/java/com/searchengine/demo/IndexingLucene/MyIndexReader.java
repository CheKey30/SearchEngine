package com.searchengine.demo.IndexingLucene;

import com.searchengine.demo.Model.Movie;
import com.searchengine.demo.Model.Path;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.spell.DirectSpellChecker;

import org.apache.lucene.document.Document;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MyIndexReader {
    private Directory directory;
    private DirectoryReader ireader;
    private IndexSearcher isearcher;
    private Analyzer analyzer;
    private HashSet<String> stopwords;

    public MyIndexReader() throws IOException {
        directory = FSDirectory.open(Paths.get(Path.indexingDir));
        ireader = DirectoryReader.open(directory);
        isearcher = new IndexSearcher(ireader);
        analyzer = new StandardAnalyzer();
        stopwords = new HashSet<>();
        File file = new File(Path.stopWordsDir);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine())!=null){
            this.stopwords.add(line);
        }
    }


    public void searchbyField(String prequery,String field, int n,List<Movie> res,HashSet<String> ids) throws  Exception{
        if(res.size()>=n){
            return;
        }
        prequery = prequery.toLowerCase();
        QueryParser parser = new QueryParser(field,analyzer);
        Query query = parser.parse(prequery);
        TopDocs topDocs = isearcher.search(query,n);
        int i=0;
        while (res.size()<n && i<topDocs.scoreDocs.length){
            Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
            Movie movie = new Movie();
            movie.setImdbID(doc.get("imdbID"));
            movie.setTitle(doc.get("title"));
            movie.setPlot(doc.get("plot"));
            movie.setActors(doc.get("actors"));
            movie.setDirector(doc.get("director"));
            movie.setYear(doc.get("year"));
            movie.setGenre(doc.get("genre"));
            movie.setRated(doc.get("score"));
            movie.setPoster(doc.get("poster"));
            if(!ids.contains(movie.getImdbID())){
                ids.add(movie.getImdbID());
                res.add(movie);
            }
            i++;
        }
    }

    public void searchbyFieldwithCheck(String prequery,String field, int n,List<Movie> res,HashSet<String> ids) throws  Exception{
        if(res.size()>=n){
            return;
        }
        String[] tokens = prequery.split(" ");
        for(String s: tokens){
            if(stopwords.contains(s.trim())){
                continue;
            }
            prequery = prequery.toLowerCase();
            Query query = new FuzzyQuery(new Term(field,prequery),2);
            TopDocs topDocs = isearcher.search(query,n);
            int i=0;
            while (res.size()<n && i<topDocs.scoreDocs.length){
                Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
                Movie movie = new Movie();
                movie.setImdbID(doc.get("imdbID"));
                movie.setTitle(doc.get("title"));
                movie.setPlot(doc.get("plot"));
                movie.setActors(doc.get("actors"));
                movie.setDirector(doc.get("director"));
                movie.setYear(doc.get("year"));
                movie.setGenre(doc.get("genre"));
                movie.setRated(doc.get("score"));
                movie.setPoster(doc.get("poster"));
                if(!ids.contains(movie.getImdbID())){
                    ids.add(movie.getImdbID());
                    res.add(movie);
                }
                i++;
            }
        }

    }

    public void searchByMulitFields(String prequery, int n,List<Movie> res,HashSet<String> ids) throws Exception{
        if(res.size()>n){
            return;
        }
        String[] fields = {"title","actors","director","plot","score","year","imdbID"};
        QueryParser parser = new MultiFieldQueryParser(fields,analyzer);
        Query query = parser.parse(prequery);
        TopDocs topDocs = isearcher.search(query,n);
        int i=0;
        while (i<topDocs.scoreDocs.length){
            Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
            Movie movie = new Movie();
            movie.setImdbID(doc.get("imdbID"));
            movie.setTitle(doc.get("title"));
            movie.setPlot(doc.get("plot"));
            movie.setActors(doc.get("actors"));
            movie.setDirector(doc.get("director"));
            movie.setYear(doc.get("year"));
            movie.setGenre(doc.get("genre"));
            movie.setRated(doc.get("score"));
            movie.setPoster(doc.get("poster"));
            if(!ids.contains(movie.getImdbID())){
                ids.add(movie.getImdbID());
                res.add(movie);
            }
            i++;
        }
    }

    public void searchbyFieldAcc(String prequery, String field, int n,List<Movie> res,HashSet<String> ids) throws Exception{
        if(res.size()>n){
            return;
        }
        String[] names = prequery.split(",");
        int number = Math.min(names.length,3);
        for(int i=0;i<number;i++){
            String name = names[i].trim()+",";
            PhraseQuery query = new PhraseQuery(field,name.split(" "));
            TopDocs topDocs = isearcher.search(query,n);
            int j=0;
            while (res.size()<n && j<topDocs.scoreDocs.length){
                Document doc = isearcher.doc(topDocs.scoreDocs[j].doc);
                Movie movie = new Movie();
                movie.setImdbID(doc.get("imdbID"));
                movie.setTitle(doc.get("title"));
                movie.setPlot(doc.get("plot"));
                movie.setActors(doc.get("actors"));
                movie.setDirector(doc.get("director"));
                movie.setYear(doc.get("year"));
                movie.setGenre(doc.get("genre"));
                movie.setRated(doc.get("score"));
                movie.setPoster(doc.get("poster"));
                if(!ids.contains(movie.getImdbID())){
                    ids.add(movie.getImdbID());
                    res.add(movie);
                }
                j++;
            }
        }

    }

    public List<Movie> getTopN(String prequery, int n) throws Exception {
        HashSet<String> ids = new HashSet<>();
        List<Movie> res = new ArrayList<>();
        // do search by title and imdbID first
        searchbyField(prequery,"title",n,res,ids);
        searchbyField(prequery,"imdbID",n,res,ids);
        // then search by the search result
        List<Movie> firstRes= new ArrayList<>(res);
        for(Movie m: firstRes){
            searchbyFieldAcc(m.getActors(),"actors",n,res,ids);
        }
        for(Movie m: firstRes){
            searchbyFieldAcc(m.getDirector(),"director",n,res,ids);
        }
        // then search on multi field
        searchByMulitFields(prequery,n,res,ids);
        // then do the fuzzy search
        searchbyFieldwithCheck(prequery,"title",n,res,ids);
        searchbyFieldwithCheck(prequery,"actors",n,res,ids);
        searchbyFieldwithCheck(prequery,"plot",n,res,ids);
        return res;
    }

    // new way to do search, add tf-idf sort
    public List<Movie> getTopN2(String prequery, int n) throws Exception{
        HashSet<String> ids = new HashSet<>();
        List<Movie> res = new ArrayList<>();
        return res;
    }
}
