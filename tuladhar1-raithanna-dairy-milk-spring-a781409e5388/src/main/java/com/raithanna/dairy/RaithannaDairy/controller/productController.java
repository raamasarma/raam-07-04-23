package com.raithanna.dairy.RaithannaDairy.controller;

import com.raithanna.dairy.RaithannaDairy.models.productMaster;
import com.raithanna.dairy.RaithannaDairy.repositories.ProductMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.model.IModel;

import java.time.LocalDate;

@Controller
public class productController {
    @Autowired
    ProductMasterRepository productMasterRepository;
    //@GetMapping("/products")
   // public String listProducts(Model  model){
       // model.addAttribute("products",productMasterRepository.findAll());
        //return "products";
    //}

    @GetMapping ("/products/new")
    public String createProductForm(Model model){
        productMaster pm=new productMaster();
        model.addAttribute("productmaster",pm);
        model.addAttribute("dateTime", LocalDate.now());
        model.addAttribute("dateTime", LocalDate.now());
        return "productmaster";
    }
    @PostMapping("/products")
    public String saveProduct(Model model,  productMaster pm){
        System.out.println(pm);
        productMasterRepository.save(pm);
        return "redirect:/";
    }


}
