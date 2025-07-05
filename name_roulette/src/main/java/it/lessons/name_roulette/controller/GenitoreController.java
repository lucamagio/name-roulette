package it.lessons.name_roulette.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.lessons.name_roulette.model.Chose;
import it.lessons.name_roulette.model.User;
import it.lessons.name_roulette.repository.ChoseRepository;
import it.lessons.name_roulette.repository.UserRepository;
import it.lessons.name_roulette.security.DatabaseUserDetails;
import it.lessons.name_roulette.service.ChoseService;
import it.lessons.name_roulette.service.UserService;
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

    @Autowired
    private ChoseRepository choseRepository;

    @Autowired
    private ChoseService choseService;
    
    @GetMapping("/home")
    public String indexGenitore(Model model, @RequestParam (name = "keyword", required = false) String keyword) {
        
        User genitore = userService.utenteAutenticato();

        if (genitore.getGender() == null) {
            model.addAttribute("sceltaGenereFatta", false);
        } else{
            model.addAttribute("sceltaGenereFatta", true);
            model.addAttribute("genere", genitore.getGender());
            model.addAttribute("listaParenti", genitore.getListaParenti());
            model.addAttribute("nameChose", genitore.getChose());
        }

        return "genitore/indexGenitore";
    }

    //Scelta Genere
    @PostMapping("/home")
    public String sceltaGenereGenitore(@RequestParam("genere") String genere, Model model) {

        User user = userService.utenteAutenticato();
        user.setGender(genere);
        userRepository.save(user);

        model.addAttribute("genere", genere);
        model.addAttribute("sceltaGenereFatta", true);

        return "redirect:/genitore/indexGenitore";
    }

    //Scelta nome
    @PostMapping("/home/choseName")
    public String sceltaNome(@RequestParam("nameChose") String nameChose, RedirectAttributes redirectAttributes, Model model) {

        User user = userService.utenteAutenticato();

        if (nameChose == null || nameChose.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errore", "Inserisci un nome valido.");
            return "redirect:/genitore/home";
        }

        try {        

            Chose chose = choseService.assegnazioneChose(nameChose);
            
            user.setChose(chose);
            userRepository.save(user);
    
            model.addAttribute("nameChose", chose);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/genitore/home";
    }

    //TODO
    //Svela nome
    @PostMapping("/home/unveiledName")
    public String svelaNome(Model model) {
        
        //Rivelazione nome alla lista dei parenti
        
        return "entity";
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
    public String editProfiloGenitore(@Valid @ModelAttribute("user") User formUser, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", formUser);
            return "genitore/editProfiloGenitore";
        }

        //Risettaggio del ruolo nello user
        User existingUser = userRepository.findById(formUser.getId()).get();
        formUser.setRole(existingUser.getRole());

        try {        

        //Settaggio della nuova chose per l'utente
        Chose updatChose = choseService.aggiornamentoChose(existingUser.getChose(), formUser.getChose());

        formUser.setChose(updatChose);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

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
    
    @GetMapping("/listaParenti")
    public String listaParenti(Model model) {

        User user = userService.utenteAutenticato();

        model.addAttribute("listaParenti", user.getListaParenti());
        return "genitore/listaParenti";
    }
    
    @PostMapping("/listaParenti")
    public String listaParenti(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {

        User user = userService.utenteAutenticato();

        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errore", "Inserisci un username valido.");
            return "redirect:/genitore/listaParenti";
        }

        try {
            userService.aggiungiParenteAllaLista(user.getId(), username.trim());
            redirectAttributes.addFlashAttribute("successo", "Parente aggiunto con successo.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/genitore/listaParenti";
    }
    
}
