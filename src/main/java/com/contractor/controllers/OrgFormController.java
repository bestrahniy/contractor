package com.contractor.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.SaveOrgFormDto;
import com.contractor.model.OrgForm;
import com.contractor.services.OrgFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * that is controller for org_form table
 * it provides endpoint for orgForm
 */
@Tag(
    name = "org_form api",
    description = "manage org_form contraller"
)
@AllArgsConstructor
@RequestMapping("/orgform")
@RestController
public class OrgFormController {

    private final OrgFormService orgFormService;

    /**
     * get all org form
     * @return http status
     */
    @Operation(
        summary = "get all org_form",
        responses = {
            @ApiResponse(responseCode = "200", description = "org form showed to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "org form not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/all")
    public ResponseEntity<List<OrgForm>> getAllOrgForm() {
        return ResponseEntity.ok(orgFormService.giveAllOrgForm());
    }

    /**
     * get org form by id
     * @param id of org form
     * @return http status
     */
    @Operation(
        summary = "get org form by id",
        description = "get org form by id",
        responses = {
            @ApiResponse(responseCode = "200", description = "showed org_form to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "org_form not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }

    )
    @GetMapping("/{id}")
    public ResponseEntity<OrgForm> getOrgFormById(
        @Parameter(
            description = "uniwue id of org form",
            example = "1"
        )
        @PathVariable Integer id) {
        return ResponseEntity.ok(orgFormService.giveOrgFormById(id));
    }

    /**
     * variable is_active becomes false
     * @param id of org form
     * @return http status
     */
    @Operation(
        summary = "logical delete by id",
        description = """
            logical delete org form by id,
            variable is_active table org_form becomes false
            and all related castomer becomes false
                """,
        responses = {
            @ApiResponse(responseCode = "204", description = "logical org_from is successful"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "org_form not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrgForm(
        @Parameter(
            description = "unique if og org_form",
            example = "1"
        )
        @PathVariable Integer id) {
        orgFormService.deleteOrgForm(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * enpoint for connect saveOrgForm method form org form service
     * it provide ready object to save method
     * @param saveOrgFormDto dto for save new org form
     * @return http status
     */
    @Operation(
        summary = "save a new org form",
        description = """
                save a new org form,
                this method use a SaveOrgFormDto
                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "created a new org_form"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "409", description = "idustry already exists"),
            @ApiResponse(responseCode = "500", description = "org_form is badly")
        }
    )
    @PutMapping("save")
    public ResponseEntity<OrgForm> saveOrgForm(@RequestBody SaveOrgFormDto saveOrgFormDto) {
        OrgForm orgForm = new OrgForm();
        orgForm.setId(saveOrgFormDto.getId());
        orgForm.setName(saveOrgFormDto.getName());
        orgForm.setActive(true);
        OrgForm saved = orgFormService.saveOrgForm(orgForm);
        return ResponseEntity.ok(saved);
    }

}
