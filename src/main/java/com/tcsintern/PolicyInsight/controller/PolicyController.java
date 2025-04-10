package com.tcsintern.PolicyInsight.controller;

import com.tcsintern.PolicyInsight.model.PolicyResponse;
import com.tcsintern.PolicyInsight.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzePolicyDocument(@RequestParam("pdfFile") MultipartFile pdfFile, Model model) {
        if (pdfFile.isEmpty()) {
            model.addAttribute("error", "Please select a PDF file to upload");
            return "index";
        }

        // Process the file
        PolicyResponse response = policyService.processPolicyDocument(pdfFile);

        if (response.isSuccess()) {
            model.addAttribute("policyTable", response.getHtmlTable());
            model.addAttribute("fileName", pdfFile.getOriginalFilename());
            return "result";
        } else {
            model.addAttribute("error", response.getErrorMessage());
            return "index";
        }
    }
}