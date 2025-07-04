package com.contractor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for show contractor on display
 * and implementation pagination
 */
@Schema(description = "get all contractor using custom pagination")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPaginationDto {

    @Schema(
        description = "unique id contractor",
        example = "id1"
    )
    private String id;

    @Schema(
        description = "fk on parent contractor table (id)",
        example = "null"
    )
    private String parentId;

    @Schema(
        description = "name of contractor",
        example = "ilya"
    )
    private String name;

    @Schema(
        description = "full name of contractor",
        example = "ilya bobkov"
    )
    private String nameFull;

    @Schema(
        description = "inn of contractor",
        example = "12345"
    )
    private String inn;

    @Schema(
        description = "ogrn of contractor",
        example = "123"
    )
    private String ogrn;

    @Schema(
        description = "fk on country table (contains id country)",
        example = "ABH"
    )
    private String country;

    @Schema(
        description = "fk on industry table (contains id indusrty)",
        example = "1"
    )
    private Integer industry;

    @Schema(
        description = "fk on org_form table (contains id org_form)",
        example = "1"
    )
    private Integer orgForm;

}
