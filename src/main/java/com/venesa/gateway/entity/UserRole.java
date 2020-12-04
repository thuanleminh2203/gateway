package com.venesa.gateway.entity;

import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name= "user_role")
@NoArgsConstructor
public class UserRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="role_id")
	private int roleId;
	
	
}
