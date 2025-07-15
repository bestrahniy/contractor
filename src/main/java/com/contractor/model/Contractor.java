package com.contractor.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "contractor")
@Builder
public class Contractor {

    @Id
    @Column("id")
    private String id;

    @Column("parent_id")
    private String parentId;

    @Column("name")
    private String name;

    @Column("name_full")
    private String nameFull;

    @Column("inn")
    private String inn;

    @Column("ogrn")
    private String ogrn;

    @Column("country")
    private String country;

    @Column("industry")
    private Integer industry;

    @Column("org_form")
    private Integer orgForm;

    @CreatedDate
    @Column("create_date")
    private Instant createDate;

    @LastModifiedDate
    @Column("modify_date")
    private Instant modifyDate;

    @CreatedBy
    @Column("create_user_id")
    private String createUserId;

    @LastModifiedBy
    @Column("modify_user_id")
    private String modifyUserId;

    @Column("is_active")
    private boolean isActive = true;

}
