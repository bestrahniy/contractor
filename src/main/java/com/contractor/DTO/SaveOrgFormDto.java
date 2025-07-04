package com.contractor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for save org form
 */
@Schema(name = "dto for save a new org_form")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveOrgFormDto {

    @Schema(description = "unique id org_form",
        example = "1")
    private Integer id;

    @Schema(description = "name of org_form",
        example = "-")
    private String name;

    @Schema(description = "status org_form",
        example = "true")
    private boolean isActive;

}
