package com.contractor.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.SaveOrgFormDto;
import com.contractor.model.OrgForm;
import com.contractor.services.OrgFormService;
import lombok.AllArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * that is controller for org_form table
 * it provides endpoint for orgForm
 */
@AllArgsConstructor
@RequestMapping("/orgform")
@RestController
public class OrgFormController {
    
    private final OrgFormService orgFormService;

    /**
     * get all org form
     * @return http status
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrgForm>> getAllOrgForm() {
        return ResponseEntity.ok(orgFormService.giveAllOrgForm());
    }
    
    /**
     * get org form by id
     * @param id of org form
     * @return http status
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrgForm> getOrgFormById(@PathVariable Integer id){
        return ResponseEntity.ok(orgFormService.giveOrgFormById(id));
    }

    /**
     * variable is_active becomes false 
     * @param id of org form
     * @return http status
     */
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrgForm(@PathVariable Integer id){
        orgFormService.deleteOrgForm(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * enpoint for connect saveOrgForm method form org form service
     * it provide ready object to save method
     * @param saveOrgFormDto dto for save new org form
     * @return http status
     */
    @PutMapping("save")
    public ResponseEntity<OrgForm> saveOrgForm(@RequestBody SaveOrgFormDto saveOrgFormDto){
        OrgForm orgForm = new OrgForm();
        orgForm.setId(saveOrgFormDto.getId());
        orgForm.setName(saveOrgFormDto.getName());
        orgForm.setActive(true);
        OrgForm saved = orgFormService.saveOrgForm(orgForm);
        return ResponseEntity.ok(saved);
    }
    
}
