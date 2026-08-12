package com.epr.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private Integer orden;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ElementCollection
    @CollectionTable(name = "plan_categoria_notas", joinColumns = @JoinColumn(name = "categoria_id"))
    @OrderColumn(name = "orden_nota")
    @Column(name = "nota", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> notas = new ArrayList<>();

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    @Builder.Default
    private List<Plan> planes = new ArrayList<>();

    public void addPlan(Plan plan) {
        planes.add(plan);
        plan.setCategoria(this);
    }
}