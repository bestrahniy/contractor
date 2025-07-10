package com.contractor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for sending castoming variable to filter costumer
 */
@Schema(name = "dto for sending castoming variable to filter costumer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchContractorRequestDto {

    @Schema(description = "search all contractor by this id",
        example = "id1")
    private String id;

    @Schema(description = "search all contractor by this parentId",
        example = "null")
    private String parentId;

    @Schema(description = "search all contractor by name or part name",
        example = "il")
    private String name;

    @Schema(description = "search all contractor by nameFull or part nameFull",
        example = "bob")
    private String nameFull;

    @Schema(description = "search all contractor by inn or part inn",
        example = "12345")
    private String inn;

    @Schema(description = "search all contractor by ogrn or part ogrn",
        example = "123")
    private String ogrn;

    @Schema(description = "fk on country table (contains id country)",
        example = "ABH")
    private String country;

    @Schema(description = "fk on industry table (contains id industry)",
        example = "1")
    private String industry;

    @Schema(description = "fk on org_form table (contains id org_form)",
        example = "1")
    private String orgForm;

}
