package it.lessons.name_roulette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.lessons.name_roulette.model.User;
import it.lessons.name_roulette.repository.UserRepository;
import it.lessons.name_roulette.security.DatabaseUserDetails;
import it.lessons.name_roulette.service.UserService;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




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
            model.addAttribute("listaParenti", user.getListaParenti());
            if (user.getChose() == null) {
                model.addAttribute("nameChose", "");
            }else{
                model.addAttribute("nameChose", user.getChose());
            }

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
    
    @GetMapping("/profiloGenitore/{id}")
    public String profiloGenitore(@PathVariable ("id") Integer id, Model model) {

        User user = userService.utenteAutenticato();

        model.addAttribute("user", user);

        return "genitore/profiloGenitore";
    }

    @GetMapping("/editProfiloGenitore/{id}")
    public String editProfiloGenitore(@PathVariable ("id") Integer id, Model model) {

        User user = userService.utenteAutenticato();

        model.addAttribute("user", user);

        return "genitore/editProfiloGenitore";
    }
    
    @PostMapping("/editProfiloGenitore/{id}")
    public String editProfiloGenitore(@Valid @ModelAttribute("user") User formUser, BindingResult bindingResult, Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", formUser);
            return "genitore/editProfiloGenitore";
        }

        //Risettaggio del ruolo nello user
        User existingUser = userRepository.findById(formUser.getId()).get();
        formUser.setRole(existingUser.getRole());

        userRepository.save(formUser);

        //Aggiornamento delle autorizzazioni dell'utente dopo la modifica del profilo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getName().equals(formUser.getUsername())) {

            UserDetails updatedUserDetails = new DatabaseUserDetails(formUser);

            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, 
                updatedUserDetails.getPassword(), 
                updatedUserDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        userRepository.save(formUser);

        return "genitore/profiloGenitore";
    }
    
       
}
