package com.venesa.gateway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "log")
@Getter
@Setter
@NoArgsConstructor
public class LogEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id")
	private Long id;

	@Column
	private String username;

	@Column(name="request_url")
	private String url;

	@Column
	private String message;
	
	@Column
	private String method;
	
	@Column(name = "remote_addr")
	private String remoteAddr;
	
	@Column(name = "type_err")
	private String typeErr;
	
	@Column(name = "request_body")
	private String body;
	
	@Column(name = "user_agent")
	private String userAgent;
	
	@Column(name = "response_body")
	private String responseBody;

	@Column(name= "request_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "UTC")
	@Temporal(TemporalType.TIMESTAMP)
	private Date requestTime;

	@Column(name= "response_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "UTC")
	@Temporal(TemporalType.TIMESTAMP)
	private Date responseTime;

	@Column(name = "type")
	private Integer type = 1;

}
