package com.example.demo.controllers;

import com.example.demo.models.Category;
import com.example.demo.models.Country;
import com.example.demo.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://wemovies-backend-b74e2422331f.herokuapp.com"})
@RequestMapping("/api/countries")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping()
    public ResponseEntity<List<Country>> countries() {
        try {
            List<Country> countries = countryService.getAllCountries();
            if (countries.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(countries, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Country> addCategory(@RequestBody Country country) {
        Country savedCountry = countryService.saveCountry(country);
        return ResponseEntity.ok(savedCountry);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Country> editCountry(@PathVariable("id") Long id, @RequestBody Country country) {
        if (id != null && country.getId() != null && id.equals(country.getId())) {
            Country updatedCountry = countryService.saveCountry(country);
            return ResponseEntity.ok(updatedCountry);
        } else {
            throw new IllegalArgumentException("Category ID is missing or does not match the path variable.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("id") Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping()
//    public String listMovieType(Model model) {
//        List<Country> countries = countryService.getAllCountries();
//        Map<Long, Integer> movieCounts = new HashMap<>();
//
//        for(Country country : countries) {
//            int countMoviesByCountryId = countryService.countMoviesByCountryId(country.getId());
//            movieCounts.put(country.getId(), countMoviesByCountryId);
//        }
//
//        model.addAttribute("movieCounts", movieCounts);
//
//        model.addAttribute("countCountries", countryService.countCountries());
//        model.addAttribute("countries", countries);
//        model.addAttribute("country", new Country());
//        return "admin/countries/list";
//    }
//    @PostMapping("/add")
//    public String addMovieType(@ModelAttribute("country") Country country) {
//        countryService.saveCountry(country);
//        return "redirect:/admin/countries";
//    }
//    @GetMapping("/edit/{id}")
//    public String showEditMovieTypeForm(@PathVariable("id") Long id, Model model) {
//        Country country = countryService.getCountryById(id);
//        if (country != null) {
//            model.addAttribute("country", country);
//            return "admin/countries/edit";
//        }
//        return "redirect:/admin/countries";
//    }
//    @PostMapping("/edit")
//    public String editMovieType(@ModelAttribute Country country) {
//        if (country.getId() != null) {
//            // Update existing category
//            countryService.saveCountry(country);
//        } else {
//            // Handle error if category ID is missing
//            throw new IllegalArgumentException("movieType ID is missing.");
//        }
//        return "redirect:/admin/countries";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteMovieType(@PathVariable("id") Long id) {
//        countryService.deleteCountry(id);
//        return "redirect:/admin/countries";
//    }
}
