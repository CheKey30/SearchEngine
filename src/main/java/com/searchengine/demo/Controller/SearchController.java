package com.searchengine.demo.Controller;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchengine.demo.Model.Movie;
import com.searchengine.demo.Model.Search;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {


    @GetMapping("/search")
    public ModelAndView newsearchwebForm(Model model) {
        ModelAndView modelAndView = new ModelAndView("index");
        model.addAttribute("search", new Search());
        //ArrayList<Movie> movielist= new ArrayList<Movie>();
        model.addAttribute("movielist", new ArrayList<Movie>());


        return modelAndView;

    }
    @PostMapping ("/search")
    public ModelAndView newresultpage(@ModelAttribute Search search,@ModelAttribute ArrayList<Movie> movielist) {
        ModelAndView modelAndView = new ModelAndView("result");
        search.setContent(search.getContent()+"+ztldhr(after_edit)");//这里对content进行修改或执行搜索
        modelAndView.addObject("search", search);


        ObjectMapper mapper= new ObjectMapper();//这里对返回的JSON格式数据进行处理
        String movieInfo="{\"title\":\"ZTL\",\"plot\":\"GOOD MAN , GREAT GUY!\",\"type\":\"Movie\",\"celebrities\":\"Tianlin Zhao\",\"year\":2019}";//查询结果返回的JSON格式数据
        try{
            Movie moviejson=mapper.readValue(movieInfo, Movie.class);
            movielist.add(moviejson);
        }catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Movie moviesample= new Movie(); //第二种方式
        moviesample.setTitle("Tantic");
        moviesample.setPlot("Somebody died,Somebody didn't.");
        moviesample.setCelebrities("Jack,Rose,Leonardo");
        moviesample.setType("Movie");
        moviesample.setYear(1999);
        movielist.add(moviesample);

        modelAndView.addObject("movielist", movielist);
        return modelAndView;

    }
}


