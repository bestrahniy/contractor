package com.contractor.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for getting contractor by id
 * and all related object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetContactorByIdDto {
    private String id;
    private String parentId;
    private String name;
    private String nameFull;
    private String inn;
    private String ogrn;
    private GetIndustryDto industry;
    private GetCountryDto country;
    private GetOrgFormDto orgForm;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetIndustryDto {
        private Integer id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCountryDto {
        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOrgFormDto  {
        private Integer id;
        private String name;
    }
}
