package com.epr.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private PlanCategoria categoria;

    @Column(nullable = false)
    private String titulo;

    private Integer orden;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    private BigDecimal precio;

    @ElementCollection
    @CollectionTable(name = "plan_items", joinColumns = @JoinColumn(name = "plan_id"))
    @OrderColumn(name = "orden_item")
    @Column(name = "item", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> items = new ArrayList<>();
}