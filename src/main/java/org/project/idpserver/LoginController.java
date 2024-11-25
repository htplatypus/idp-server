package org.project.idpserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/api")
public class LoginController {

    @GetMapping("/login")
    @CrossOrigin("http://localhost:8081")
    public String login(Model model, @RequestParam(value = "redirect_uri") String redirectUri) {

        model.addAttribute("redirectUri", redirectUri);
        return "login";
    }
}
