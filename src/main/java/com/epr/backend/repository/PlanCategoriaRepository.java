package com.epr.backend.repository;

import com.epr.backend.entity.PlanCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanCategoriaRepository extends JpaRepository<PlanCategoria, Long> {
    List<PlanCategoria> findByActivoTrueOrderByOrdenAsc();

    List<PlanCategoria> findAllByOrderByOrdenAsc();
}
