package com.contractor.mapper;

import org.springframework.stereotype.Component;
import com.contractor.DTO.SaveCountryDto;
import com.contractor.model.Country;

@Component
public class SaveCountryDtoMapper {

    public Country saveNewCountry(SaveCountryDto saveCountryDto) {
        Country country = new Country();
        country.setId(saveCountryDto.getId());
        country.setName(saveCountryDto.getName());
        country.setActive(saveCountryDto.isActive());
        return country;
    }

}

