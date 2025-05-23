package com.example.demo.controllers;

import com.example.demo.models.Country;
import com.example.demo.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/countries")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping()
    public String listMovieType(Model model) {
        List<Country> countries = countryService.getAllCountries();
        Map<Long, Integer> movieCounts = new HashMap<>();

        for(Country country : countries) {
            int countMoviesByCountryId = countryService.countMoviesByCountryId(country.getCountry_id());
            movieCounts.put(country.getCountry_id(), countMoviesByCountryId);
        }

        model.addAttribute("movieCounts", movieCounts);

        model.addAttribute("countCountries", countryService.countCountries());
        model.addAttribute("countries", countries);
        model.addAttribute("country", new Country());
        return "admin/countries/list";
    }
    @PostMapping("/add")
    public String addMovieType(@ModelAttribute("country") Country country) {
        countryService.saveCountry(country);
        return "redirect:/admin/countries";
    }
    @GetMapping("/edit/{id}")
    public String showEditMovieTypeForm(@PathVariable("id") Long id, Model model) {
        Country country = countryService.getCountryById(id);
        if (country != null) {
            model.addAttribute("country", country);
            return "admin/countries/edit";
        }
        return "redirect:/admin/countries";
    }
    @PostMapping("/edit")
    public String editMovieType(@ModelAttribute Country country) {
        if (country.getCountry_id() != null) {
            // Update existing category
            countryService.saveCountry(country);
        } else {
            // Handle error if category ID is missing
            throw new IllegalArgumentException("movieType ID is missing.");
        }
        return "redirect:/admin/countries";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovieType(@PathVariable("id") Long id) {
        countryService.deleteCountry(id);
        return "redirect:/admin/countries";
    }
}
