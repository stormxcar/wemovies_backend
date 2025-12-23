package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.Country;
import com.example.demo.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Country>>> countries() {
        try {
            List<Country> countries = countryService.getAllCountries();
            if (countries.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "No countries found", null), HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(new ApiResponse<>(true, "Countries retrieved successfully", countries), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while retrieving countries", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Country>> addCountry(@RequestBody Country country) {
        try {
            Country savedCountry = countryService.saveCountry(country);
            return ResponseEntity.ok(new ApiResponse<>(true, "Country added successfully", savedCountry));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while adding the country", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Country>> editCountry(@PathVariable("id") Long id, @RequestBody Country country) {
        try {
            if (id != null && country.getId() != null && id.equals(country.getId())) {
                Country updatedCountry = countryService.saveCountry(country);
                return ResponseEntity.ok(new ApiResponse<>(true, "Country updated successfully", updatedCountry));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Country ID is missing or does not match the path variable", null), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while updating the country", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable("id") Long id) {
        try {
            countryService.deleteCountry(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Country deleted successfully", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the country", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}