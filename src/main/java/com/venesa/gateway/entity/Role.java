package com.venesa.gateway.entity;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role {

	public Role() {
	}

	public Role(Integer id, String role) {
		this.id = id;
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	private String role;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
