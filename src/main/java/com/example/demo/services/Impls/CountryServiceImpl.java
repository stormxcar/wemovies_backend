package com.example.demo.services.Impls;

import com.example.demo.models.Country;
import com.example.demo.repositories.CountryRepositories;
import com.example.demo.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private CountryRepositories countryRepositories;
    @Override
    public List<Country> getAllCountries() {
        return countryRepositories.findAll();
    }

    @Override
    public Country getCountryById(long id) {
        return countryRepositories.findById(id).orElse(null);
    }

    @Override
    public Country saveCountry(Country country) {
        return countryRepositories.save(country);
    }

    @Override
    public void deleteCountry(long id) {
        Country country = countryRepositories.findById(id).orElse(null);
        countryRepositories.delete(country);
    }

    @Override
    public int countMoviesByCountryId(long countryId) {
        return countryRepositories.countMoviesByCountryId(countryId);
    }

    @Override
    public int countCountries() {
        return (int) countryRepositories.count();
    }
}
