package com.contractor.services.IndustryTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.contractor.DTO.SaveIndustryDto;
import com.contractor.mapper.SaveIndustryDtoMapper;
import com.contractor.model.Industry;
import com.contractor.repository.IndustryRepositiry;
import com.contractor.services.IndustryServices;


@RunWith(MockitoJUnitRunner.class)
public class IndustryServiceMockTest {
    
    @Mock
    IndustryRepositiry industryRepositiry;

    @InjectMocks
    IndustryServices industryServices;

    @Mock
    SaveIndustryDtoMapper saveIndustryDtoMapper;

    @Mock
    JdbcTemplate jdbcTemplate;

    SaveIndustryDto saveIndustryDto;
    
    Industry testIndustry;

    @Before
    public void init(){
        testIndustry = new Industry();
        testIndustry.setId(51);
        testIndustry.setName("Инжененрия");
        testIndustry.setActive(true);

        saveIndustryDto = new SaveIndustryDto();
        saveIndustryDto.setId(51);
        saveIndustryDto.setName("Ниженерия");
        saveIndustryDto.setActive(true);
    }

    @Test
    public void getAllIndustryTest(){
        List<Industry> mockList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Industry ind = new Industry();
            ind.setId(i);
            ind.setName("Name" + i);
            mockList.add(ind);
        }
        when(industryRepositiry.findAll()).thenReturn(mockList);
        List<Industry> result = industryRepositiry.findAll();
        assertEquals(50, result.size());
    }

    @Test
    public void deleteIndustryTest(){
        industryRepositiry.save(testIndustry);
        industryServices.deleteIndustry(51);

        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class)))
            .thenReturn(false);

        Boolean type = jdbcTemplate.queryForObject("SELECT is_active FROM industry", Boolean.class);

        assertEquals(false, type);
        
        verify(jdbcTemplate).update("UPDATE industry SET is_active = false WHERE id = ?", testIndustry.getId());
        verify(jdbcTemplate).update("UPDATE contractor SET is_active = false WHERE industry = ?", testIndustry.getId());
    }

    @Test
    public void getIndustryByIdTest(){
        when(industryRepositiry.findById(1))
            .thenReturn(Optional.of(new Industry(1, "Авиастроение", true))
        );
        Industry industry = industryServices.getIndustryById(1);
        assertEquals("Авиастроение", industry.getName());
    }

    @Test
    public void saveIndustryTest(){
        when(industryRepositiry.save(testIndustry))
            .thenReturn(testIndustry);

        when(saveIndustryDtoMapper.saveIndustry(saveIndustryDto))
            .thenReturn(testIndustry);

        Industry industry = industryServices.saveIndustry(saveIndustryDto);
        assertEquals(testIndustry, industry);
    }
}
