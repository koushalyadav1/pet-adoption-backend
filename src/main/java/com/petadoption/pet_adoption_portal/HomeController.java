
package com.petadoption.pet_adoption_portal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Pet Adoption Portal");
        model.addAttribute("message", "Find your new best friend!");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
