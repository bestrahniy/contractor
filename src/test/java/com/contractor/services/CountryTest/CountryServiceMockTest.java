package com.contractor.services.CountryTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.contractor.dto.SaveCountryDto;
import com.contractor.mapper.CountrySaveDtoMapper;
import com.contractor.model.Country;
import com.contractor.repository.CountryRepository;
import com.contractor.services.CountryServices;


@RunWith(MockitoJUnitRunner.class)
public class CountryServiceMockTest {
    
    @InjectMocks
    CountryServices countryServices;

    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    CountryRepository countryRepository;

    @Mock
    JdbcAggregateTemplate jdbcAggregateTemplate;

    @Mock
    CountrySaveDtoMapper saveCountryDtoMapper;

    SaveCountryDto testCountryDto;

    Country testCountry;
    
    @Before
    public void init(){
        testCountryDto = new SaveCountryDto();
        testCountryDto.setId("RU");
        testCountryDto.setName("Россия");

        testCountry = new Country();
        testCountry.setId("RU");
        testCountry.setName("Россия");
        testCountry.setActive(true);
    }

    @Test
    public void saveCountryTest(){
        when(saveCountryDtoMapper.saveNewCountry(testCountryDto))
            .thenReturn(testCountry);

        when(jdbcAggregateTemplate.insert(testCountry))
            .thenReturn(testCountry);
            
        Country result = countryServices.saveCountry(testCountryDto);
        assertEquals(testCountry, result);
        verify(jdbcAggregateTemplate).insert(testCountry);
    }

    @Test
    public void getAllgetByIdCountryTest(){
        List<Country> countrys = List.of(
            new Country("Ru", "Russia", true),
            new Country("USA", "USA", true),
            new Country("BLR", "Belarus", true)
        );

        when(countryRepository.findById("BLR"))
            .thenReturn(Optional.of(new Country("BLR", "Belarus", true)));

        when(countryRepository.findAll()).thenReturn(countrys);

        List<SaveCountryDto> result = countryServices.getAllCountry();

        Country getIdresult = countryServices.getCountryById("BLR");

        assertEquals("Belarus", getIdresult.getName());

        assertEquals("USA", result.get(1).getName());
        }

    @Test
    public void deleteCountryTest() {
        when(jdbcTemplate.queryForObject(
            anyString(),
            any(Map.class),
            eq(Boolean.class)
        )).thenReturn(false);

        when(jdbcTemplate.update(anyString(), any(Map.class)))
            .thenReturn(1);

        countryServices.deleteCountryById("RU");

        Boolean result = jdbcTemplate.queryForObject(
            "SELECT is_active FROM country WHERE id = :id",
            Map.of("id", "RU"),
            Boolean.class
        );
        assertFalse(result);

    }

}
