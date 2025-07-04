package com.contractor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for save industry
 */
@Schema(name = "dto for save a new industry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveIndustryDto {

    @Schema(description = "unique industry id",
        example = "1")
    private Integer id;

    @Schema(description = "name of industry",
        example = "Авиастроение")
    private String name;

    @Schema(description = "status idustry",
        example = "true")
    private boolean isActive;

}
