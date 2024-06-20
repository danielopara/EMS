package com.danielopara.EMS.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profile_photo")
@Data
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    @Lob
    @Column(name = "imagedata",length = 1000, columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @OneToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;
}
