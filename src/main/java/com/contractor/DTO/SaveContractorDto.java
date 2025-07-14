package com.contractor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for provide contractor object to save
 */
@Schema(description = "dto for save new contractor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveContractorDto {

    @Schema(description = "unique id contractor",
        example = "id1")
    private String id;

    @Schema(description = "fk for parent table contractor",
        example = "null")
    private String parentId;

    @Schema(description = "name of contractor",
        example = "ilya")
    private String name;

    @Schema(description = "full name of contractor",
        example = "ilya bobkov")
    private String nameFull;

    @Schema(description = "inn of contractor",
        example = "1234")
    private String inn;

    @Schema(description = "ogrn of contractor",
        example = "123")
    private String ogrn;

    @Schema(description = "fk on country table (contains id country table)",
        example = "ABH")
    private String country;

    @Schema(description = "fk on industry table (contains id industry table)",
        example = "1")
    private Integer industry;

    @Schema(description = "fk on org_form table (contains id org_form table)",
        example = "1")
    private Integer orgForm;

}
