package com.contractor.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.DTO.SaveIndustryDto;
import com.contractor.model.Industry;
import com.contractor.services.IndustryServices;
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
 * that is controller for industry
 * it define the endpoints for industry class
 */
@Tag(
    name = "industry api",
    description = "manage industry controller"
)
@RestController
@AllArgsConstructor
@RequestMapping("/industry")
public class IndustryController {

    private final IndustryServices industryServices;

    /**
     * get all industry
     * @return http status
     */
    @Operation(
        summary = "get all industry",
        responses = {
            @ApiResponse(responseCode = "200", description = "industry showed to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "industry not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SaveIndustryDto>> getAllIndustry() {
        return ResponseEntity.ok(industryServices.getAllIndustry());
    }

    /**
     * get industry by id
     * @param id of industry
     * @return http status
     */
    @Operation(
        summary = "get industry by id",
        responses = {
            @ApiResponse(responseCode = "200", description = "showed industry to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "industry not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Industry> getIndustryById(
        @Parameter(
            description = """
                    unique id of industry
                    """,
            example = "1"
        )
        @PathVariable Integer id) {
        return ResponseEntity.ok(industryServices.getIndustryById(id));
    }

    /**
     * connect to saveIndustry method from indestry service
     * for save new country
     * @param saveIndustryDto dto for save industry
     * @return http status
     */
    @Operation(
        summary = "save a new industry",
        description = """
                save a new industry use a SaveIdustryDto
                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "created a new industry"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "409", description = "idustry already exists"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PutMapping("/save")
    public ResponseEntity<?> saveIndustry(@RequestBody SaveIndustryDto saveIndustryDto) {
        return ResponseEntity.ok(industryServices.saveIndustry(saveIndustryDto));
    }

    /**
     * variable is_active become false
     * @param id of industry
     * @return http status
     */
    @Operation(
        summary = "delete industry by id",
        description = """
            logical delete industry by id,
            variable is_active table industry becomes false
            and all related castomer becomes false
                """,
        responses = {
            @ApiResponse(responseCode = "204", description = "logical industry is successful"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "industry not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteIndustry(
        @Parameter(
            description = "unique id of industry",
            example = "1"
        )
        @PathVariable Integer id) {
        industryServices.deleteIndustry(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
