package com.searchengine.demo.Service;

import com.searchengine.demo.IndexingLucene.MyIndexWriter;
import com.searchengine.demo.Model.Movie;
import com.searchengine.demo.Model.Path;
import org.json.JSONException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

import net.sf.json.JSONObject;

/*
get data from omdb api and save it as index
 */
public class GetDataService {


    public List<Movie> getAllMoviesByTitle(){
        List<Movie> movies = new ArrayList<>();
        String url = "http://www.omdbapi.com/?i={id}&plot={type}&apikey=4108095f";
        try{
            File file = new File(Path.movieTitles);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line=br.readLine())!=null){
                Movie res = client(url,line,"full");
                if(res!=null){
                    movies.add(res);
                }

            }
            br.close();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return movies;
    }
    public Movie client(String url,String id,String type) throws JSONException {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("type",type);
        String movieInfo = template.getForObject(url,String.class,map);//{"msg":"调用成功！","code":1}
        //System.out.println(oneMovie);
        Movie oneMoive = new Movie();
        JSONObject dataofJson = JSONObject.fromObject(movieInfo);
        if(dataofJson.has("Title")&&dataofJson.has("Year")&&dataofJson.has("Rated")&&dataofJson.has("Runtime")&&dataofJson.has("Genre")
        &&dataofJson.has("Director")&&dataofJson.has("Writer")&&dataofJson.has("Actors")&&dataofJson.has("Plot")&&dataofJson.has("Language"
        )&&dataofJson.has("Country")&&dataofJson.has("Poster")&& dataofJson.has("imdbID") && dataofJson.getString("imdbID")!=null && dataofJson.has("imdbRating")){
            oneMoive.setTitle(dataofJson.getString("Title"));
            oneMoive.setYear(dataofJson.getString("Year"));
            oneMoive.setRated(dataofJson.getString("imdbRating"));
            oneMoive.setRuntime(dataofJson.getString("Runtime"));
            oneMoive.setGenre(dataofJson.getString("Genre"));
            oneMoive.setDirector(dataofJson.getString("Director"));
            oneMoive.setWriter(dataofJson.getString("Writer"));
            oneMoive.setActors(dataofJson.getString("Actors"));
            oneMoive.setPlot(dataofJson.getString("Plot"));
            oneMoive.setLanguage(dataofJson.getString("Language"));
            oneMoive.setCountry(dataofJson.getString("Country"));
            oneMoive.setPoster(dataofJson.getString("Poster"));
            oneMoive.setImdbID(dataofJson.getString("imdbID"));
            return oneMoive;
        }else {
            return null;
        }

    }

    public void writeIndex(List<Movie> movieList) throws Exception{
        MyIndexWriter output  = new MyIndexWriter();
        for(Movie m:movieList){
//            System.out.println(m.getYear());
            output.index(m);
        }
        output.close();
    }


    // run it once to generate index
    public static void main(String[] args) throws Exception{
        GetDataService g = new GetDataService();
        List<Movie> movieList = g.getAllMoviesByTitle();
//        System.out.println(movieList.get(0).getTitle());
//        System.out.println(movieList.size());
        g.writeIndex(movieList);
    }
}
