package com.example.checkdulu.controller;

import com.example.checkdulu.service.InfoService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("info")
@Slf4j
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

    @GetMapping("mobile")
    public ResponseEntity<InformationResponse> getInformationMobile(@RequestParam("barcode") String barcode, @RequestParam(value = "amount") Optional<Double> amount){
        return infoService.getInfo(barcode).map(productInfo ->
             ResponseEntity.ok(new InformationResponse(productInfo.name(), productInfo.sugarPerXg(amount.orElse(100d))))).orElseThrow(() -> {
                 log.error(STR."Product could not be found \{barcode}");
                 return new RuntimeException();
        });
    }

    private record InformationResponse(String productName, double sugarPerGivenAmount){ }
}
