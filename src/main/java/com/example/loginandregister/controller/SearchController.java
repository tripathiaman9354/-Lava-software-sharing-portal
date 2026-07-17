package com.example.loginandregister.controller;

import com.example.loginandregister.model.Chipset;
import com.example.loginandregister.model.Odm;
import com.example.loginandregister.services.ChipsetService;
import com.example.loginandregister.services.OdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private OdmService odmService;

    @Autowired
    private ChipsetService chipsetService;

    @GetMapping("/dashboard/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(required = false, defaultValue = "") String query) {
        List<Odm> matchedOdms = odmService.searchByName(query);
        List<Chipset> matchedChipsets = chipsetService.searchByName(query);

        Map<String, Object> response = new HashMap<>();
        response.put("odms", matchedOdms);
        response.put("chipsets", matchedChipsets);
        response.put("odmCount", matchedOdms.size());
        response.put("chipsetCount", matchedChipsets.size());
        response.put("totalOdmCount", odmService.getAllOdms().size());
        response.put("totalChipsetCount", chipsetService.getAllChipsets().size());

        return response;
    }
}