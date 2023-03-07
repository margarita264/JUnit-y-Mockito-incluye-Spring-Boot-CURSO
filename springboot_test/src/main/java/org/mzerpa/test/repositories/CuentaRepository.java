package org.mzerpa.test.repositories;

import org.mzerpa.test.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta,Long> {
    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);//para evitar el nullPointExeception
    //List<Cuenta> findAll();
    //Cuenta findById(Long id);
    //void update(Cuenta cuenta);
}
