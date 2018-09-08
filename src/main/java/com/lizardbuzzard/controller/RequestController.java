package com.lizardbuzzard.controller;

import com.lizardbuzzard.security.service.UserDTO;
import com.lizardbuzzard.security.service.UserProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/app")
public class RequestController {

    @Autowired
    UserProcessingService userProcessingService;

	@RequestMapping(method = RequestMethod.GET)
	public String rememberMeLogin() {
 		return "customLogin";
 	}

	@RequestMapping("/homepage/adminconsole")
	public String adminHomePage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("loggedUserName", authentication.getName());

        List<UserDTO> userDtoList = userProcessingService.getFormUsersList();
        model.addAttribute("listOfUsers", userDtoList);

 		return "adminConsolePage";
 	}

    @RequestMapping("/homepage/user")
    public String userHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("loggedUserName", authentication.getName());

        return "userPage";
    }

    @RequestMapping("/redirect")
    public String redirectPage() {
        return "redirect:https://www.w3.org/";
    }

    @RequestMapping("/accessDenied")
    public String authenticationErrorPage() {
        return "authenticationError";
    }
}