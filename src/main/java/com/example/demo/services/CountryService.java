package com.example.demo.services;

import com.example.demo.models.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries();
    Country getCountryById(long id);
    Country saveCountry(Country country);
    void deleteCountry(long id);

    int countMoviesByCountryId(long countryId);

    int countCountries();
}
