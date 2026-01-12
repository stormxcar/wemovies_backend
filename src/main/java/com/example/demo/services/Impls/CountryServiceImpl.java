package com.example.demo.services.Impls;

import com.example.demo.models.Country;
import com.example.demo.repositories.CountryRepositories;
import com.example.demo.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private CountryRepositories countryRepositories;
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
