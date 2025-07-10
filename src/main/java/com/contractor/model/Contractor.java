package com.contractor.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "contractor")
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

    @Column("create_date")
    private Instant createDate = Instant.now();

    @Column("modify_date")
    private Instant modifyDate;

    @Column("create_user_id")
    private String createUserId;

    @Column("modify_user_id")
    private String modifyUserId;

    @Column("is_active")
    private boolean isActive = true;

}
