package com.searchengine.demo.Controller;

import com.searchengine.demo.Model.Movie;
import com.searchengine.demo.Model.Search;

import com.searchengine.demo.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/")
    public ModelAndView newsearchwebForm(Model model) {
        ModelAndView modelAndView = new ModelAndView("index");
        model.addAttribute("search", new Search());
        //ArrayList<Movie> movielist= new ArrayList<Movie>();
        model.addAttribute("movielist", new ArrayList<Movie>());


        return modelAndView;

    }

    @GetMapping("/newsearchweb")
    public ModelAndView returnToSearch(Model model){
        ModelAndView modelAndView = new ModelAndView("index");
        model.addAttribute("search", new Search());
        return modelAndView;
    }
    @PostMapping ("/search")
    public ModelAndView newresultpage(@ModelAttribute Search search,@ModelAttribute ArrayList<Movie> movielist) {
        ModelAndView modelAndView = new ModelAndView("result");
        modelAndView.addObject("search", search);


        try{
            List<Movie> movies = searchService.getSearchRes(search.getContent());
//            ObjectMapper mapper= new ObjectMapper();//这里对返回的JSON格式数据进行处理
//            String movieInfo="{\"title\":\"ZTL\",\"plot\":\"GOOD MAN , GREAT GUY!\",\"type\":\"Movie\",\"celebrities\":\"Tianlin Zhao\",\"year\":2019}";//查询结果返回的JSON格式数据
//            Movie moviejson=mapper.readValue(movieInfo, Movie.class);
//            movielist.add(moviejson);
            modelAndView.addObject("movielist", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Movie moviesample= new Movie(); //第二种方式
//        moviesample.setTitle("Tantic");
//        moviesample.setPlot("Somebody died,Somebody didn't.");
//        moviesample.setYear("1999");
//        movielist.add(moviesample);


        return modelAndView;

    }
}


