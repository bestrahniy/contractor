package com.contractor.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.GetPaginationDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.dto.SearchContractorRequestDto;
import com.contractor.dto.UserRolesAddDto;
import com.contractor.services.ContractorServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/ui")
public class UiController {

    private final ContractorServices contractorServices;

    @Operation(
        summary = "method for conntected with auth service",
        description = """
                this is where he addresses auth service, getting
                jwt token
                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("""
            hasRole('USER') or hasRole('CONTRACTOR_RUS') or hasRole('CONTRACTOR_SUPERUSER')
            or hasRole('ADMIN') or hasRole('SUPERUSER')
            """)
    @GetMapping("/contractor/deals")
    public ResponseEntity<?> getAllDeals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(contractorServices.getAllDeals(authentication.getAuthorities()));
    }

    @Operation(
        summary = "save a new user roles",
        description = """
                connect to auth service, send it to
                list with new roles and admin token
                and service return new access token
                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/contractor/user-roles/save")
    public ResponseEntity<?> saveNewRole(
        @RequestBody UserRolesAddDto userRolesAddDto,
        @RequestHeader("Authorization") String currentAdminToken
    ) {
        return ResponseEntity.ok(contractorServices.saveNewRoleUser(userRolesAddDto, currentAdminToken));
    }

    @Operation(
        summary = "check all roles that access to user",
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("""
            hasRole('USER') or hasRole('CONTRACTOR_RUS') or hasRole('CONTRACTOR_SUPERUSER')
            or hasRole('ADMIN') or hasRole('SUPERUSER')
            """)
    @GetMapping("/contractor/user-roles/{login}")
    public ResponseEntity<?> getMethodName(@PathVariable String login) {
        return ResponseEntity.ok(contractorServices.checkRolesUser(login));
    }

    @Operation(
        summary = "save a new contractor",
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("hasRole('CONTRACTOR_SUPERUSER') or hasRole('SUPERUSER')")
    @PutMapping("/save")
    public ResponseEntity<?> saveContructor(
        @RequestBody SaveContractorDto saveContractorDto) {
        return  ResponseEntity.ok(contractorServices.saveContractor(saveContractorDto));
    }

    @Operation(
        summary = "get one contractor by id",
        description = "return all contractor data and related entity",
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("""
            hasRole('USER') or hasRole('CONTRACTOR_RUS')
            or hasRole('CONTRACTOR_SUPERUSER') or hasRole('SUPERUSER')
            """)
    @GetMapping("/{id}")
    public ResponseEntity<GetContactorByIdDto> getContructorById(@PathVariable String id) {
        GetContactorByIdDto getContractor = contractorServices.getContractorById(id);
        return ResponseEntity.ok(getContractor);
    }

    @Operation(
        summary = "logical deletion contractor by id",
        description = """
                        update variable is_active from contractor table to
                        false for logical deletion
                                """,
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("hasRole('CONTRACTOR_SUPERUSER') or hasRole('SUPERUSER')")
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

    @Operation(
        summary = "give all contractor",
        description = "schow all contractor using pagination",
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("""
            hasRole('USER') or hasRole('CONTRACTOR_RUS')
            or hasRole('CONTRACTOR_SUPERUSER') or hasRole('SUPERUSER')
            """)
    @GetMapping("/all/{page}/{size}")
    public ResponseEntity<List<GetPaginationDto>> getAllContractor(
        @PathVariable Integer page,
        @PathVariable Integer size) {
        return ResponseEntity.ok(contractorServices.getAllContractorPagination(page, size));
    }

    @Operation(
        summary = "get all contractor by filter",
        responses = {
            @ApiResponse(responseCode = "201", description = "request is correctly"),
            @ApiResponse(responseCode = "400", description = "incorrect data"),
            @ApiResponse(responseCode = "401", description = "not authorized user"),
            @ApiResponse(responseCode = "404", description = "url not found"),
            @ApiResponse(responseCode = "500", description = "server is badly")
        }
    )
    @PreAuthorize("""
            hasRole('USER') or hasRole('CONTRACTOR_RUS')
            or hasRole('CONTRACTOR_SUPERUSER') or hasRole('SUPERUSER')
            """)
    @PostMapping("/search")
    public ResponseEntity<List<GetPaginationDto>> postMethodName(
        @RequestBody SearchContractorRequestDto searchContravtorRequestDto) {
        return ResponseEntity.ok(contractorServices.searchContractors(searchContravtorRequestDto));
    }

}
