package it.lessons.name_roulette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.lessons.name_roulette.model.User;
import it.lessons.name_roulette.repository.UserRepository;
import it.lessons.name_roulette.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/genitore")
public class GenitoreController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    
    @GetMapping("/home")
    public String indexGenitore(Model model, @RequestParam (name = "keyword", required = false) String keyword) {
        
        User user = userService.utenteAutenticato();

        if (user.getGender() == null) {
            model.addAttribute("sceltaGenereFatta", false);
        } else{
            model.addAttribute("sceltaGenereFatta", true);
            model.addAttribute("genere", user.getGender());
        }

        return "genitore/indexGenitore";
    }

    @PostMapping("/home")
    public String sceltaGenereGenitore(@RequestParam("genere") String genere, Model model) {

        User user = userService.utenteAutenticato();
        user.setGender(genere);
        userRepository.save(user);

        model.addAttribute("genere", genere);
        model.addAttribute("sceltaGenereFatta", true);

        return "genitore/indexGenitore";
    }
    
       
}
