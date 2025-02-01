package com.example.checkdulu.controller;

import com.example.checkdulu.service.InfoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("info")
public class InfoController {

    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @HxRequest
    @GetMapping
    public String getInformation(@RequestParam("barcode") String barcode, @RequestParam(value = "amount") Optional<Double> amount, Model model){
        return infoService.getInfo(barcode).map(productInfo -> {
            model.addAttribute("productName", productInfo.name());
            model.addAttribute("sugarPerGivenAmount", productInfo.sugarPerXg(amount.orElse(100d)));
            return "product";
        }).orElse("none");
    }
}
