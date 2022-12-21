package com.raithanna.dairy.RaithannaDairy.controller;

import com.raithanna.dairy.RaithannaDairy.models.customer;
import com.raithanna.dairy.RaithannaDairy.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Controller
public class customerController {
    @Autowired
    private CustomerRepository customerRepository;


    @GetMapping("/editcustom")
    public String editCustomByCode(@RequestParam(name="code",defaultValue = "1")String code, Model model){

        model.addAttribute("custom",customerRepository.findByCode(code));
        return "editcustom";
    }

    @PostMapping("/editcustom")
    public String update(Model model,@ModelAttribute customer Customer){
        List<String> messages = new ArrayList<>();
        customerRepository.save(Customer);
        model.addAttribute("messages",messages);
        return "redirect:/";
    }




}
