package com.example.demo.services.Impls;

import com.example.demo.models.Country;
import com.example.demo.repositories.CountryRepositories;
import com.example.demo.services.CountryService;
import com.example.demo.services.SlugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private CountryRepositories countryRepositories;

    @Autowired
    private SlugService slugService;
    @Override
    public List<Country> getAllCountries() {
        return countryRepositories.findAll();
    }

    @Override
    public Country getCountryById(UUID id) {
        return countryRepositories.findById(id).orElse(null);
    }
    
    @Override
    public Country getCountryBySlug(String slug) {
        return countryRepositories.findBySlug(slug);
    }

    @Override
    public Country saveCountry(Country country) {
        Country existing = null;
        if (country.getId() != null) {
            existing = countryRepositories.findById(country.getId()).orElse(null);
        }

        boolean isNew = existing == null;
        boolean nameChanged = existing != null && existing.getName() != null
                && country.getName() != null
                && !existing.getName().equals(country.getName());

        if (isNew || nameChanged || country.getSlug() == null || country.getSlug().trim().isEmpty()) {
            slugService.generateCountrySlug(country);
        }

        return countryRepositories.save(country);
    }

    @Override
    public void deleteCountry(UUID id) {
        Country country = countryRepositories.findById(id).orElse(null);
        countryRepositories.delete(country);
    }

    @Override
    public int countMoviesByCountryId(UUID countryId) {
        return countryRepositories.countMoviesByCountryId(countryId);
    }

    @Override
    public int countCountries() {
        return (int) countryRepositories.count();
    }
}
