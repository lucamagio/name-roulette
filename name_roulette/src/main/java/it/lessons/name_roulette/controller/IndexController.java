package it.lessons.name_roulette.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping()
    public String index() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("GENITORE"))) {
            return "redirect:/genitore/home";
        } if (auth.getAuthorities().contains(new SimpleGrantedAuthority("PARENTE"))) {
            return "redirect:/parente/home";
        } else {
            return "redirect:/login";
        }
    }
}
