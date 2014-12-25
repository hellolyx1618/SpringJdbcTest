package com.lyx.model;

import com.lyx.base.anno.PrimaryKey;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * user role 一对多关系
 */
@Table(name = "tb_role")
public class Role {

    @PrimaryKey
    private int id;
    @Column(name = "role_name")
    private String roleName;
    private String description;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + this.id + ", roleName='" + this.roleName
                + '\'' + ", description='" + this.description + '\'' + '}';
    }
}
