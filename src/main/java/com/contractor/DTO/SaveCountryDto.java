package com.contractor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for save country
 */
@Schema(name = " dto for save a new country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCountryDto {

    @Schema(description = "unique id country",
        example = "ABH")
    private String id;

    @Schema(description = "name of country",
        example = "Абхазия")
    private String name;

    @Schema(description = "status country",
        example = "true")
    private boolean isActive;

}
