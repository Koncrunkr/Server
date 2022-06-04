package ru.comgrid.server.controller.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CommonController{
    @GetMapping("/")
    public ModelAndView redirectToFront(){
        return new ModelAndView("redirect:https://comgrid.ru/");
    }
}
