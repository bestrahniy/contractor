package com.contractor.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import com.contractor.model.Country;
import com.contractor.repository.CountryRepository;


@RunWith(MockitoJUnitRunner.class)
public class CountryServiceMockTest {
    
    @InjectMocks
    CountryServices countryServices;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    CountryRepository countryRepository;

    Country testCountry;
    
    @Before
    public void init(){
        testCountry = new Country();
        testCountry.setId("RU");
        testCountry.setName("Россия");
        testCountry.setActive(true);
    }

    @Test
    public void saveCountryTest(){
        when(countryRepository.save(testCountry))
            .thenReturn(testCountry);

        when(countryRepository.existsById(testCountry.getId()))
            .thenReturn(true);

        Country result = countryServices.saveCountry(testCountry);

        assertEquals(testCountry, result);

        verify(countryRepository).save(testCountry);
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

        List<Country> result = countryServices.getAllCountry();

        Country getIdresult = countryServices.getCountryById("BLR");

        assertEquals("Belarus", getIdresult.getName());

        assertEquals("USA", result.get(1).getName());
    }

    @Test
    public void deleteCountryTest(){
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class)))
            .thenReturn(false);
            
        countryServices.deleteCountryById("RU");
        Boolean type = jdbcTemplate.queryForObject("SELECT is_active FROM country WHERE id = RU", Boolean.class);
        assertEquals(false, type);

        verify(jdbcTemplate).update("UPDATE country SET is_active = false WHERE id = ?", "RU");
        verify(jdbcTemplate).update("UPDATE contractor SET is_active = false WHERE country = ?", "RU");
    }

}
