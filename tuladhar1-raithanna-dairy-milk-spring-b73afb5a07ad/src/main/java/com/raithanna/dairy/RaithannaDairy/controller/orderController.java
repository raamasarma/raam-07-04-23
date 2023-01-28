package com.raithanna.dairy.RaithannaDairy.controller;

import com.raithanna.dairy.RaithannaDairy.models.*;
import com.raithanna.dairy.RaithannaDairy.repositories.CustomerRepository;
import com.raithanna.dairy.RaithannaDairy.repositories.DailySalesRepository;
import com.raithanna.dairy.RaithannaDairy.repositories.ProductMasterRepository;
import com.raithanna.dairy.RaithannaDairy.repositories.SaleOrderRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class orderController {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private DailySalesRepository dailySalesRepository;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @RequestMapping("/createOrder")
    public String createOrder_html(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("yes")) {
            Iterable<customer> CustomersIterable = customerRepository.findAll();
            Iterable<dailySales> dailysales = dailySalesRepository.findAll();
            List<Integer> orderNos = new ArrayList<>();
            List<customer> Customers = new ArrayList<>();
            for (dailySales i : dailysales) {
                orderNos.add(i.getOrderNo());
            }
            System.out.println(dailysales);
            int orderNo = 1;
            if (!orderNos.isEmpty()) {
                orderNo = Integer.parseInt(String.valueOf(orderNos.get(orderNos.size() - 1))) + 1;
            }

            for (customer Customer : CustomersIterable) {
                Customers.add(Customer);
            }

            Iterable<productMaster> Products = productMasterRepository.findBySplCustCode(Customers.get(0).getCode().toString());
            int size = 0;
            for (productMaster value : Products) {
                size++;
            }
            if (size == 0) {
                Products = productMasterRepository.findBySplCustCode("");
            }
            model.addAttribute("customers", Customers);
            model.addAttribute("products", Products);
            model.addAttribute("orderNo", orderNo);
            model.addAttribute("dateTime", LocalDate.now());
            return "createOrder";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/login";
    }

    @PostMapping("/getproductValues")
    public ResponseEntity<?> getproductRatesByCustcode(@RequestParam Map<String, String> body, Model model) {
        System.out.println(body);
        productMaster product = productMasterRepository.findBySplCustCodeAndPCode(body.get("code"), body.get("pcode"));
        productMaster commonproduct = productMasterRepository.findBySplCustCodeAndPCode("", body.get("pcode"));
        if (product == null) {
            product = commonproduct;
        }

        System.out.println(product);
        model.addAttribute("products", product);
        Map<String, String> respBody = new HashMap<>();
        respBody.putIfAbsent("specialValue", String.valueOf(product.getUnitRate() - Integer.parseInt(body.get("disc"))));
        respBody.putIfAbsent("commonValue", String.valueOf(commonproduct.getUnitRate()));
        respBody.putIfAbsent("amount", String.valueOf((product.getUnitRate() * Integer.parseInt(body.get("qty")))));
        respBody.putIfAbsent("ttlValue", String.valueOf((product.getUnitRate() - Integer.parseInt(body.get("disc"))) * Integer.parseInt(body.get("qty"))));
        return ResponseEntity.ok(respBody);
    }

    @PostMapping("/createOrder")
    public String createOrder(Model model, @ModelAttribute dailySales order, @RequestParam Map<String, String> orderDetails, HttpServletRequest request, HttpSession session) throws InterruptedException {
        List<String> messages = new ArrayList<>();
        System.out.println(orderDetails);
        order.setOrderNo(Integer.parseInt(orderDetails.get("orderDetails[orderNo]")));
        order.setCustCode(orderDetails.get("orderDetails[custCode]"));
        order.setDisc(Double.parseDouble(orderDetails.get("orderDetails[disc]")));
        order.setNetAmount(Double.parseDouble(orderDetails.get("orderDetails[netAmount]")));
        order.setAmount(Double.parseDouble(orderDetails.get("orderDetails[amount]")));
        order.setProdCode(orderDetails.get("orderDetails[prodCode]"));
        order.setQuantity(Double.parseDouble(orderDetails.get("orderDetails[quantity]")));
        order.setUnitRate(Double.parseDouble(orderDetails.get("orderDetails[unitRate]")));
        System.out.println(order);
        System.out.println("@107");
        try {
            dailySalesRepository.save(order);
        } catch (Exception handlerException) {
            messages.add("Error Creating the order pls retry");
            model.addAttribute("messages", messages);
            return "/createOrder";
        }
        model.addAttribute("messages", messages);
        return "redirect:/";
    }

    @PostMapping("/createSaleOrder")
    public String createSaleOrder(@RequestParam String orderNo) {
        List<dailySales> orderProducts = dailySalesRepository.findByOrderNo(Integer.parseInt(orderNo));

        while (true) {
            if (orderProducts.isEmpty()) {
                orderProducts = dailySalesRepository.findByOrderNo(Integer.parseInt(orderNo));
            } else {
                break;
            }
        }
        System.out.println(orderProducts.size());
        double amt = 0;
        double netamt = 0;
        double discamt = 0;
        String custCode = "";

        for (dailySales product : orderProducts) {
            amt = amt + product.getAmount();
            netamt = netamt + product.getNetAmount();
            discamt = discamt + product.getDisc();
            custCode = product.getCustCode();
            System.out.println("asdf");
        }

        saleOrder saleorder = new saleOrder();
        saleorder.setOrderNo(Integer.parseInt(orderNo));
        saleorder.setAmount(amt);
        saleorder.setNetAmount(netamt);
        saleorder.setDisc(amt - netamt);
        saleorder.setCustCode(custCode);
        System.out.println(saleorder);
        saleOrderRepository.save(saleorder);
        return "redirect:/";
    }

    @GetMapping("/getOrder")
    public String getOrder(@RequestParam(name = "orderNo", defaultValue = "1") Integer orderNo, Model model) {
        List<dailySales> orderProducts = dailySalesRepository.findByOrderNo(orderNo);
        saleOrder sale_order = saleOrderRepository.findByOrderNo(orderNo);

        model.addAttribute("orderProducts", orderProducts);
        model.addAttribute("sale_order", sale_order);
        return "orderDisplay";
    }

    @GetMapping("/order/view/{orderNo}")
    public String viewOrderForm(Model model, @PathVariable(name = "orderNo") Integer orderNo, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("yes")) {
            // List<dailySales> orders = dailySalesRepository.findByOrderNo(orderNo);
            model.addAttribute("orders",dailySalesRepository.findByOrderNo(orderNo));
            model.addAttribute("sales",saleOrderRepository.findByOrderNo(orderNo));
            return "view_order";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/login";
    }

    @GetMapping("/orders/edit/{orderNo}")
    public String editOrderForm(Model model, @PathVariable Integer orderNo,HttpSession session) {
        if (session.getAttribute("loggedIn").equals("yes")) {
            Iterable<customer> CustomersIterable = customerRepository.findAll();
            List<customer> Customers = new ArrayList<>();
            for (customer Customer : CustomersIterable) {
                Customers.add(Customer);
            }
            Iterable<productMaster> Products = productMasterRepository.findBySplCustCode(Customers.get(0).getCode().toString());
            int size = 0;
            for (productMaster value : Products) {
                size++;
            }
            if (size == 0) {
                Products = productMasterRepository.findBySplCustCode("");
            }
            model.addAttribute("orders", dailySalesRepository.findByOrderNo(orderNo));
            model.addAttribute("sales", saleOrderRepository.findByOrderNo(orderNo));
            model.addAttribute("customers", Customers);
            model.addAttribute("products", Products);
            return "edit_order";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/login";
    }

   @PostMapping("orders/orderNo")
    public String updateOrder(Model model, @PathVariable Integer orderNo, @ModelAttribute dailySales order, @RequestParam Map<String, String> orderDetails) {
        System.out.println(orderDetails);
        order.setOrderNo(Integer.parseInt(orderDetails.get("orderDetails[orderNo]")));
        order.setCustCode(orderDetails.get("orderDetails[custCode]"));
        order.setDisc(Double.parseDouble(orderDetails.get("orderDetails[disc]")));
        order.setNetAmount(Double.parseDouble(orderDetails.get("orderDetails[netAmount]")));
        order.setAmount(Double.parseDouble(orderDetails.get("orderDetails[amount]")));
        order.setProdCode(orderDetails.get("orderDetails[prodCode]"));
        order.setQuantity(Double.parseDouble(orderDetails.get("orderDetails[quantity]")));
        order.setUnitRate(Double.parseDouble(orderDetails.get("orderDetails[unitRate]")));
        System.out.println(order);
        dailySalesRepository.save(order);
        return "redirect:/";
    }




    /*@PostMapping("orders/{orderNo}")
    public String updateOrder(@PathVariable Integer orderNo,@ModelAttribute dailySales order,@RequestParam Map<String, String> orderDetails,Model model){
        List<dailySales> existingorder=dailySalesRepository.findByOrderNo(orderNo);

         
        existingorder.setCustCode(order.getCustCode());
        existingorder.setDisc(order.getDisc());
           existingorder.setNetAmount(order.getNetAmount());
        existingorder.setAmount(order.getAmount());
          existingorder.setProdCode(order.getProdCode());
           existingorder.setQuantity(order.getQuantity());
         existingorder.setUnitRate(order.getUnitRate());

dailySalesRepository.save(existingorder);

return "redirect:/";
        System.out.println(order);
        dailySalesRepository.save(order);
        return "redirect:/";
    }*/

}






