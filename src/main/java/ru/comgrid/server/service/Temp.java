package ru.comgrid.server.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Temp{
    @GetMapping("/example_messaging")
    public String exampleMessaging(){
        return "example_messaging";
    }
    @GetMapping("/example_post")
    public String examplePost(){
        return "example_post";
    }
}
