package com.example.demo.services;

import com.example.demo.models.Country;

import java.util.List;
import java.util.UUID;

public interface CountryService {
    List<Country> getAllCountries();
    Country getCountryById(UUID id);
    Country getCountryBySlug(String slug);
    Country saveCountry(Country country);
    void deleteCountry(UUID id);

    int countMoviesByCountryId(UUID countryId);

    int countCountries();
}
