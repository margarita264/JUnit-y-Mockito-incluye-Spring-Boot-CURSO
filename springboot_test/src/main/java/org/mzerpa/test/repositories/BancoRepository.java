package org.mzerpa.test.repositories;

import org.mzerpa.test.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco,Long> {
    //List<Banco> findAll();
    //Banco finById(Long  id);
    //void update(Banco banco);
}
