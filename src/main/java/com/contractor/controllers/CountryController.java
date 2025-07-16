package com.contractor.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.contractor.dto.SaveCountryDto;
import com.contractor.services.CountryServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "country api",
    description = "manage country and all related entity"
)
@RestController
@RequestMapping("/country")
@AllArgsConstructor
public class CountryController {

    private final CountryServices countryServices;

    /**
     * connect to saveCountry method from country service
     * and send a ready object for saving
     * @param saveCountryDto dto for save new country
     * @return http status
     */
    @Operation(
        summary = "save a new counry",
        description = "save a new country and use SaveCountryDto for it",
        responses = {
            @ApiResponse(responseCode = "201", description = "created a new country"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "409", description = "country already exists"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PutMapping("/save")
    public ResponseEntity<?> saveCountry(
        @RequestBody SaveCountryDto saveCountryDto) {
        return ResponseEntity.ok(countryServices.saveCountry(saveCountryDto));
    }

    /**
     * connect to getAllContry method from country service
     * for show all country
     * @return http status
     */
    @Operation(
        summary = "get all country",
        responses = {
            @ApiResponse(responseCode = "200", description = "counter showed to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "country not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SaveCountryDto>> getAllCountry() {
        return ResponseEntity.ok(countryServices.getAllCountry());
    }

    /**
     * show country by id
     * @param id of country
     * @return http status
     */
    @Operation(
        summary = "get country by id",
        responses = {
            @ApiResponse(responseCode = "200", description = "showed country to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "country not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCountryById(
        @Parameter(
            description = """
                    unique id of country
                    """,
            example = "ABH"
        )
        @PathVariable String id) {
        return ResponseEntity.ok(countryServices.getCountryById(id));
    }

    /**
     * variab;e is_active of country become false
     * @param id of country
     * @return http status
     */
    @Operation(
        summary = "delete country by id",
        description = """
            logical delete country by id,
            variable is_active table country becomes false
            and all related castomer becomes false
        """,
        responses = {
            @ApiResponse(responseCode = "204", description = "logical delete is successful"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "country not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCountryById(
        @Parameter(
            description = """
                    unique id of country
                    """,
            example = "ABH"
        )
        @PathVariable String id) {
        countryServices.deleteCountryById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
