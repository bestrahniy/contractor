package com.contractor.services;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.model.Country;
import com.contractor.repository.CountryRepository;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class CountryServices {
    
    private final CountryRepository countryRepository;

    private final JdbcTemplate jdbcTemplate;

    public Country saveCountry(Country country){
        return countryRepository.save(country);
    }

    public List<Country> getAllCountry(){
        return countryRepository.findAll();
    }

    public Country getCountryById(String id){
        return countryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("country not found"));
    }

    @Transactional
    public void deleteCountryById(String id){

        jdbcTemplate.update("UPDATE country SET is_active = false WHERE id = ?", id);

        jdbcTemplate.update("UPDATE contractor SET is_active = false WHERE country = ?", id);
        
    }

}
