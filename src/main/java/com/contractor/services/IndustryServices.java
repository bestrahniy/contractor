package com.contractor.services;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.contractor.model.Industry;
import com.contractor.repository.IndustryRepositiry;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class IndustryServices {
    
    private final IndustryRepositiry industryRepositiry;

    private final JdbcTemplate jdbcTemplate;

    public List<Industry> getAllIndictry(){
        return industryRepositiry.findAll();
    }

    public Industry getIndustryById(Integer id){
        return industryRepositiry.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Industry not found"));
    }

    public Industry saveIndustry(Industry industry){
        return industryRepositiry.save(industry);
    }

    public void deleteIndustry(Integer id){

        jdbcTemplate.update("UPDATE industry SET is_active = false WHERE id = ?", id);

        jdbcTemplate.update("UPDATE contractor SET is_active = false WHERE country = ?", id);
        
    }

}
