package com.searchengine.demo.Service;


import com.searchengine.demo.IndexingLucene.MyIndexReader;
import com.searchengine.demo.Model.Movie;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    // function to do search and return imdbIDs
    public List<Movie> getSearchRes(String content) throws Exception{
        MyIndexReader myIndexReader = new MyIndexReader();
        List<Movie> results = myIndexReader.getTopN(content,10);
        return results;
    }
}
