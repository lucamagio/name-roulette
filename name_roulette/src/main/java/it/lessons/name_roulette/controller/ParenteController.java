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
@RequestMapping("/parente")
public class ParenteController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChoseRepository choseRepository;

    @Autowired
    private ChoseService choseService;

    @GetMapping("/home")
    public String indexParente(Model model, @RequestParam (name = "keyword", required = false) String keyword) {
        
        User parente = userService.utenteAutenticato();
        List<User> listaGenitori = userRepository.findByRoleName("GENITORE");
        boolean parenteAssociato = false;
        User genitoreAssociato = null;

        for (User genitore : listaGenitori) {

            List<User> listaParenti = genitore.getListaParenti();
            if (listaParenti != null) {
                for (User user : listaParenti) {
                    if (user.getId().equals(parente.getId())) {
                        parenteAssociato = true;
                        genitoreAssociato = genitore;
                        break;
                    }
                }
            }
            if (parenteAssociato) {
                break;
            }
        }

        model.addAttribute("parente", parente);
        model.addAttribute("parenteAssociato", parenteAssociato);
        model.addAttribute("genitore", genitoreAssociato);
        model.addAttribute("nameChose", parente.getChose());


        return "parente/indexParente";
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
 
        return "redirect:/parente/home";
    }

    //Profilo
    @GetMapping("/profiloParente/{id}")
    public String profiloParente(@PathVariable("id") Integer id, Model model) {

        User user = userService.utenteAutenticato();

        model.addAttribute("user", user);

        return "parente/profiloParente";
    }

    //Edit Profilo
    @GetMapping("/editProfiloParente/{id}")
    public String editProfiloParente(@PathVariable("id") Integer id, Model model) {

        User user = userService.utenteAutenticato();

        model.addAttribute("user", user);

        return "parente/editProfiloParente";
    }
    
    @PostMapping("/editProfiloParente/{id}")
    public String editProfiloParente(@Valid @ModelAttribute("user") User formUser, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", formUser);

            return "parente/editProfiloParente";
        }

        //Risettaggio ruolo dello user
        User exiUser = userRepository.findById(formUser.getId()).get();
        formUser.setRole(exiUser.getRole());

        try {        

            //Settaggio della nuova chose per l'utente
            Chose updatChose = choseService.aggiornamentoChose(exiUser.getChose(), formUser.getChose());
    
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

        return "parente/profiloParente";
    }
    
}
