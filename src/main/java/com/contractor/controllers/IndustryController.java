package com.contractor.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.SaveIndustryDto;
import com.contractor.model.Industry;
import com.contractor.services.IndustryServices;
import lombok.AllArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * that is controller for industry
 * it define the endpoints for industry class
 */
@RestController
@AllArgsConstructor
@RequestMapping("/industry")
public class IndustryController {
    
    private final IndustryServices industryServices;

    /**
     * get all industry
     * @return http status
     */
    @GetMapping("/all")
    public ResponseEntity<List<Industry>> getAllindictry() {
        return ResponseEntity.ok(industryServices.getAllIndictry());
    }
    
    /**
     * get industry by id
     * @param id of industry
     * @return http status
     */
    @GetMapping("/{id}")
    public ResponseEntity<Industry> getIndustryById(@PathVariable Integer id) {
        return ResponseEntity.ok(industryServices.getIndustryById(id));
    }
    
    /**
     * connect to saveIndustry method from indestry service
     * for save new country
     * @param saveIndustryDto dto for save industry
     * @return http status
     */
    @PutMapping("/save")
    public ResponseEntity<Industry> saveIndustry(@RequestBody SaveIndustryDto saveIndustryDto){
        Industry industry = new Industry();
        industry.setId(saveIndustryDto.getId());
        industry.setName(saveIndustryDto.getName());
        industry.setActive(true);
        Industry saved = industryServices.saveIndustry(industry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * variable is_active become false
     * @param id of industry
     * @return http status
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteIndustry(@PathVariable Integer id){
        industryServices.deleteIndustry(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
