package com.venesa.gateway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mobio_event_err")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobioEventErrEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mobio_event_id")
    private Long id;

    @Column
    private String method;

    @Column(name = "type")
    private String type;

    private String body;

    @Column(name = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    private final Date createTime = new Date();

    @Column(name="is_active")
    private boolean isActive = true;

    @Column(name="is_system_crm")
    private boolean isSystemCrm = false;
}

