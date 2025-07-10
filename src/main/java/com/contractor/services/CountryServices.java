package com.contractor.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.contractor.DTO.SaveCountryDto;
import com.contractor.mapper.SaveCountryDtoMapper;
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

    private final SaveCountryDtoMapper saveCountryDtoMapper;

    private final JdbcTemplate jdbcTemplate;

    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * save new country
     * @param country object
     * @return save country
     */
    @Transactional
    public Country saveCountry(SaveCountryDto saveCountryDto) {
        Country country = saveCountryDtoMapper.saveNewCountry(saveCountryDto);
        try {
            return countryRepository.save(country);
        } catch (IncorrectUpdateSemanticsDataAccessException ex) {
            return jdbcAggregateTemplate.insert(country);
        }
    }

    /**
     * find all ciuntry
     * @return list of country object
     */
    @Transactional
    public List<SaveCountryDto> getAllCountry() {
        return countryRepository.findAll().stream()
            .map(country -> {
                SaveCountryDto saveCountryDto = new SaveCountryDto();
                saveCountryDto.setId(country.getId());
                saveCountryDto.setName(country.getName());
                saveCountryDto.setActive(country.isActive());
                return saveCountryDto;
            })
            .collect(Collectors.toList());
    }

    /**
     * find country by id
     * @param id of country
     * @return country object
     */
    @Transactional
    public Country getCountryById(String id) {
        Country country = countryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("country not found"));

        if (!country.isActive()) {
            throw new IllegalArgumentException("country is not active");
        }

        return country;
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
