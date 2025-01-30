package com.example.checkdulu.controller;

import com.example.checkdulu.service.InfoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.websocket.server.PathParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String getInformation(@PathParam("barcode") String barcode, @PathParam("amount") Optional<Double> amount, Model model){
        return infoService.getInfo(barcode).map(productInfo -> {
            model.addAttribute("productName", productInfo.name());
            model.addAttribute("sugarPer100g", productInfo.sugarPerXg(amount.orElse(100d)));
            return "product";
        }).orElse("none");
    }

}
