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
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

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
        String[] tokens = prequery.toLowerCase().split(" ");
        for(String s: tokens){
            if(stopwords.contains(s.trim())){
                continue;
            }
            Query query = new FuzzyQuery(new Term(field,s),2);
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

    public void fuzzysearchbyMultiFields(String prequery, String[] fields,int n,List<Movie> res,HashSet<String> ids) throws Exception{
        if(res.size()>=n){
            return;
        }
        String[] tokens = prequery.toLowerCase().replace(",","").split(" ");
        StringBuilder fuzzy_tokens = new StringBuilder();
        for(String s: tokens) {
            fuzzy_tokens.append(s.trim()).append("~2").append(" ");
        }
        prequery = fuzzy_tokens.toString().trim();

        Map<String,Float> boosts = new HashMap<>();
        boosts.put("title",(float)30);
        boosts.put("actors",(float)30);
        boosts.put("director",(float)30);
        boosts.put("plot",(float)10);
        boosts.put("score",(float)10);
        boosts.put("year",(float)20);
        boosts.put("imdbID",(float)10);

        QueryParser parser = new MultiFieldQueryParser(fields,analyzer,boosts);
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

    public void searchByMulitFields(String prequery, int n,List<Movie> res,HashSet<String> ids) throws Exception{
        if(res.size()>n){
            return;
        }
        String[] fields = {"title","actors","director","plot","score","year","imdbID"};
        Map<String,Float> boosts = new HashMap<>();
        boosts.put("title",(float)30);
        boosts.put("actors",(float)30);
        boosts.put("director",(float)30);
        boosts.put("plot",(float)10);
        boosts.put("score",(float)10);
        boosts.put("year",(float)20);
        boosts.put("imdbID",(float)10);

        QueryParser parser = new MultiFieldQueryParser(fields,analyzer,boosts);
        parser.setDefaultOperator(QueryParser.Operator.AND); //exact search
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
            String name = names[i].trim(); //match new format of input data:  alan walker , tom allen
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
        prequery = filtration(prequery); //remove Special characters in queries that make website errors
        // search on all field first with boost (exact search)
        searchByMulitFields(prequery,n,res,ids);
        // then search by the search result if there're results
        List<Movie> firstRes= new ArrayList<>(res);
        if (firstRes.size()!=0){
            for(Movie m: firstRes){
                searchbyFieldAcc(m.getActors(),"actors",n,res,ids);
            }
            for(Movie m: firstRes){
                searchbyFieldAcc(m.getDirector(),"director",n,res,ids);
            }
        }
        // then do the fuzzy search with boost on fields without plot
        String[] fields = {"title","actors","director","score","year","imdbID"};
        fuzzysearchbyMultiFields(prequery,fields,n,res,ids);
        // then search by the search result
        firstRes= new ArrayList<>(res);
        for(Movie m: firstRes){
            searchbyFieldAcc(m.getActors(),"actors",n,res,ids);
        }
        for(Movie m: firstRes){
            searchbyFieldAcc(m.getDirector(),"director",n,res,ids);
        }
        // then do the fuzzy search with boost on plot
        String[] fields2 = {"plot"};
        fuzzysearchbyMultiFields(prequery,fields2,n,res,ids);
        return res;
    }

    public static String filtration(String str) {  // method modified from: https://blog.csdn.net/plg17/article/details/86140816
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？']";
        String regEx2 = "\\s+";
        str = Pattern.compile(regEx).matcher(str).replaceAll("").trim();
        str = Pattern.compile(regEx2).matcher(str).replaceAll(" ").trim();

        return str;
    }

    // new way to do search, add tf-idf sort
    public List<Movie> getTopN2(String prequery, int n) throws Exception{
        HashSet<String> ids = new HashSet<>();
        List<Movie> res = new ArrayList<>();
        return res;
    }
}
