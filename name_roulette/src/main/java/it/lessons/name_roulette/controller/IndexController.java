package it.lessons.name_roulette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import it.lessons.name_roulette.model.Role;
import it.lessons.name_roulette.model.User;
import it.lessons.name_roulette.repository.RoleRepository;
import it.lessons.name_roulette.repository.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String login() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {

            return "login";
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("GENITORE"))) {
            return "redirect:/genitore/home";
        } if (auth.getAuthorities().contains(new SimpleGrantedAuthority("PARENTE"))) {
            return "redirect:/parente/home";
        } else {
            return "login";
        }
    }

    @PostMapping("/login")
    public String postLogin() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
    
            return "login";
        }
    
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("GENITORE"))) {
            return "redirect:/genitore/home";
        } if (auth.getAuthorities().contains(new SimpleGrantedAuthority("PARENTE"))) {
            return "redirect:/parente/home";
        } else {
            return "login";
        }
    }
    

    @GetMapping("/registrati")
    public String showRegistrazione(Model model) {

        model.addAttribute("user", new User());
        model.addAttribute("role", roleRepository.findAll());

        return "registrazione";
    }

    @PostMapping("/registrati")
    public String postRegistrazione(@ModelAttribute User user, Model model) {

        // Controlla se email esiste
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email già registrata");
            model.addAttribute("role", roleRepository.findAll());
            return "registrazione";
        }

        //Controlla se l'username esiste
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username già registrato");
            model.addAttribute("role", roleRepository.findAll());
            return "registrazione";
        }
        
        //Ricerco il ruolo da salvare e lo setto nello user
        Integer roleId = user.getRole().getId();
        Role fullRole = roleRepository.findById(roleId).get();
        user.setRole(fullRole);
        
        userRepository.save(user);
        
        return "redirect:/";
    }
    
    
}
