package com.contractor.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for getting contractor by id
 * and all related object
 */
@Schema(description = "dto dor getting contractor by id and all related object")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetContactorByIdDto {

    @Schema(description = "id contractor",
        example = "id1")
    private String id;

    @Schema(description = "id parent contractor table (fk)",
        example = "null")
    private String parentId;

    @Schema(description = "name of contractor",
        example = "ilya")
    private String name;

    @Schema(description = "full name contractor",
        example = "ilya bobkov")
    private String nameFull;

    @Schema(description = "inn contractor",
        example = "1234")
    private String inn;

    @Schema(description = "ogrn contractor",
        example = "123")
    private String ogrn;

    @Schema(description = "fk on industry table (contains id industry)",
        example = "ABH")
    private GetIndustryDto industry;

    @Schema(description = "fk on country table (contains id country table)",
        example = "1")
    private GetCountryDto country;

    @Schema(description = "fk on org_form table (contains id org_form table)",
        example = "1")
    private GetOrgFormDto orgForm;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetIndustryDto {

        @Schema(description = "id industry", example = "1")
        private Integer id;

        @Schema(description = "name industry", example = "Авиастроение")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCountryDto {

        @Schema(description = "id country", example = "ABH")
        private String id;

        @Schema(description = "name country", example = "Абхазия")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOrgFormDto  {

        @Schema(description = "id org_form",
            example = "1")
        private Integer id;

        @Schema(description = "-")
        private String name;
    }

}
