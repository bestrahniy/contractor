package com.contractor.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.GetPaginationDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.dto.SearchContractorRequestDto;
import com.contractor.services.ContractorServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * that is controller for contractors
 * this class defines endpoint and connect with ContractorsRepository
 */
@Tag(name = "contractor api",
    description = "manage contractor")
@RestController()
@RequestMapping("/contractor")
@AllArgsConstructor
public class ContractorContoller {

    private final ContractorServices contractorServices;

    /**
     * connect to saveContractor method from srvice to
     * save new contructor
     * @param saveContractorDto dto for save new contractor
     * @return http status
     */
    @Operation(
        summary = "save a new contractor",
        responses = {
            @ApiResponse(responseCode = "201", description = "created a new contractor"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "409", description = "contractor already exists"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PutMapping("/save")
    public ResponseEntity<?> saveContructor(
        @RequestBody SaveContractorDto saveContractorDto) {
        return  ResponseEntity.ok(contractorServices.saveContractor(saveContractorDto));
    }

    /**
     * connect to saveContractor method getContractorById
     * to get contuctor form db by id
     * @param id of contructor
     * @return http satatus
     */
    @Operation(
        summary = "get one contractor by id",
        description = "return all contractor data and related entity",
        responses = {
            @ApiResponse(responseCode = "200", description = "contractor was found"),
            @ApiResponse(responseCode = "404", description = "contractor not found"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetContactorByIdDto> getContructorById(
        @Parameter(
            description = """
                    unique id of costomer
                    """,
            example = "id1"
        )
        @PathVariable String id) {
        GetContactorByIdDto getContractor = contractorServices.getContractorById(id);
        return ResponseEntity.ok(getContractor);
    }

    /**
     * connect to deleteContractorById method to
     * do logical deletion contructor by id
     * @param id of contactor
     * @return http status
     */
    @Operation(
        summary = "logical deletion contractor by id",
        description = """
                        update variable is_active from contractor table to
                        false for logical deletion
                                """,
        responses = {
            @ApiResponse(responseCode = "204", description = "logical delete is successful"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "contractor not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContractor(
        @Parameter(
            description = "unique id of contractor",
            example = "id1"
        )
        @PathVariable String id) {
        contractorServices.deleteContractorById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * connect to getAllContractorPagination method to
     * get all contractor from db
     * @param page number
     * @param size number
     * @return http status
     */
    @Operation(
        summary = "give all contractor",
        description = "schow all contractor using pagination",
        responses = {
            @ApiResponse(responseCode = "200", description = "contractor showed to screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "contractor not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @GetMapping("/all/{page}/{size}")
    public ResponseEntity<List<GetPaginationDto>> getAllContractor(
        @Parameter(
            description = """
                    count of page, min value = 0
                    """,
            example = "0")
        @PathVariable Integer page,

        @Parameter(
            description = """
                    count of size, min value = 1
                    """,
            example = "1")
        @PathVariable Integer size) {
        return ResponseEntity.ok(contractorServices.getAllContractorPagination(page, size));
    }

    /**
     * post method to connect searchContractor method from
     * contractorService to get all contracotr by filter
     * @param searchContravtorRequestDto dto for search
     * @return https status
     */
    @Operation(
        summary = "get all contractor by filter",
        responses = {
            @ApiResponse(responseCode = "200", description = "contractor showed on screen"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "404", description = "contractor nor found"),
            @ApiResponse(responseCode = "500", description = "serever if badly")
        }
    )
    @PostMapping("/search")
    public ResponseEntity<List<GetPaginationDto>> postMethodName(
        @RequestBody SearchContractorRequestDto searchContravtorRequestDto) {
        return ResponseEntity.ok(contractorServices.searchContractors(searchContravtorRequestDto));
    }

}
