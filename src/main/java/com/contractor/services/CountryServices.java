package com.contractor.services;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.model.Country;
import com.contractor.repository.CountryRepository;
import lombok.AllArgsConstructor;

/**
 * this is service to manage country db
 */
@Service
@AllArgsConstructor
public class CountryServices {

    private final CountryRepository countryRepository;

    private final JdbcTemplate jdbcTemplate;

    /**
     * save new country
     * @param country object
     * @return save country
     */
    @Transactional
    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    /**
     * find all ciuntry
     * @return list of country object
     */
    @Transactional
    public List<Country> getAllCountry() {
        return countryRepository.findAll();
    }

    /**
     * find country by id
     * @param id of country
     * @return country object
     */
    @Transactional
    public Country getCountryById(String id) {
        return countryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("country not found"));
    }

    /**
     * verify is_active countrt become false
     * and verify is_actieve related contractors become false
     * @param id of coutry
     */
    @Transactional
    public void deleteCountryById(String id) {

        jdbcTemplate.update("UPDATE country SET is_active = false WHERE id = ?", id);

        jdbcTemplate.update("UPDATE contractor SET is_active = false WHERE country = ?", id);

    }

}
