package com.epr.backend.config;

import com.epr.backend.entity.Plan;
import com.epr.backend.entity.PlanCategoria;
import com.epr.backend.repository.PlanCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanSeeder implements CommandLineRunner {

    private final PlanCategoriaRepository planCategoriaRepository;

    @Override
    public void run(String... args) {
        if (planCategoriaRepository.count() > 0) {
            return;
        }

        PlanCategoria individual = PlanCategoria.builder()
                .titulo("Individual")
                .orden(1)
                .activo(true)
                .build();
        individual.addPlan(Plan.builder()
                .titulo("Classic E.P.R")
                .orden(1)
                .activo(true)
                .items(List.of(
                        "8 Clases mensuales",
                        "Clases PERSONALIZADAS",
                        "Evaluación funcional incluida",
                        "Hasta 6 alumnos por turno"
                ))
                .build());
        individual.addPlan(Plan.builder()
                .titulo("Full E.P.R")
                .orden(2)
                .activo(true)
                .items(List.of(
                        "20 Clases mensuales",
                        "8 Presenciales",
                        "12 Con seguimiento offline",
                        "Evaluación funcional incluida",
                        "Hasta 6 alumnos por turno"
                ))
                .build());

        PlanCategoria equipo = PlanCategoria.builder()
                .titulo("Equipo")
                .orden(2)
                .activo(true)
                .build();
        equipo.addPlan(Plan.builder()
                .titulo("Classic Equipo")
                .orden(1)
                .activo(true)
                .items(List.of(
                        "8 Clases mensuales",
                        "Clases PERSONALIZADAS",
                        "Evaluación funcional incluida",
                        "Inscripción junto a compañeros de equipo",
                        "Hasta 6 alumnos por turno"
                ))
                .build());
        equipo.addPlan(Plan.builder()
                .titulo("Full Equipo")
                .orden(2)
                .activo(true)
                .items(List.of(
                        "20 Clases mensuales",
                        "8 Presenciales + 12 Offline",
                        "Evaluación funcional incluida",
                        "Inscripción junto a compañeros de equipo",
                        "Hasta 6 alumnos por turno"
                ))
                .build());

        PlanCategoria evaluacion = PlanCategoria.builder()
                .titulo("Evaluación Funcional")
                .orden(3)
                .activo(true)
                .notas(List.of(
                        "La evaluación inicial incluye un mesociclo de seguimiento de entrenamiento personalizado.",
                        "Se recomienda una nueva evaluación a los 3 meses, para monitorear la evolución y actualizar la planificación."
                ))
                .build();
        evaluacion.addPlan(Plan.builder()
                .titulo("Classic Evaluación")
                .orden(1)
                .activo(true)
                .items(List.of(
                        "Evaluación asimetría MMII",
                        "Evaluación de fuerzas MMII",
                        "Diagnóstico de las evaluaciones",
                        "Rutina de un mes socio"
                ))
                .build());

        planCategoriaRepository.saveAll(List.of(individual, equipo, evaluacion));
    }
}
