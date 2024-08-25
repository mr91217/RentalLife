package com.example.rentallife.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

@Setter
@Getter
@NoArgsConstructor
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private String requestType; // 维修类型或替换类型

    private String description;

    private LocalDateTime requestDate;

    //private String status; // 状态（例如：Pending, In Progress, Completed）
    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 使用枚举类型来表示状态
    // Getters and Setters
    public LocalDateTime getSubmittedAt() {
        return requestDate;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.requestDate = submittedAt;
    }





}