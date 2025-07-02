package com.contractor.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.SaveCountryDto;
import com.contractor.model.Country;
import com.contractor.services.CountryServices;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * that is controller for country
 * is define endpoins of class country
 */
@RestController
@RequestMapping("/country")
@AllArgsConstructor
public class CountryControllers {

    private final CountryServices countryServices;

    /**
     * connect to saveCountry method from country service
     * and send a ready object for saving
     * @param saveCountryDto dto for save new country
     * @return http status
     */
    @PutMapping("/save")
    public ResponseEntity<Country> saveCountry(@RequestBody SaveCountryDto saveCountryDto) {
        Country country = new Country();
        country.setId(saveCountryDto.getId());
        country.setName(saveCountryDto.getName());
        country.setActive(true);
        Country saved = countryServices.saveCountry(country);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * connect to getAllContry method from country service
     * for show all country
     * @return http status
     */
    @GetMapping("/all")
    public ResponseEntity<List<Country>> getAllCountry() {
        return ResponseEntity.ok(countryServices.getAllCountry());
    }

    /**
     * show country by id
     * @param id of country
     * @return http status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCountryById(@PathVariable String id) {
        return ResponseEntity.ok(countryServices.getCountryById(id));
    }

    /**
     * variab;e is_active of country become false
     * @param id of country
     * @return http status
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCountryById(@PathVariable String id) {
        countryServices.deleteCountryById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
