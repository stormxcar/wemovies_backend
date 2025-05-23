/*
 * @ (#) CountryService.java 1.0 12/24/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.services;

import com.example.demo.models.Country;

import java.util.List;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/24/2024
 * @version 1.0
 */
public interface CountryService {
    List<Country> getAllCountries();
    Country getCountryById(long id);
    Country saveCountry(Country country);
    void deleteCountry(long id);

    int countMoviesByCountryId(long countryId);

    int countCountries();
}
