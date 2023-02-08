package org.mzerpa.test.repositories;

import org.mzerpa.test.models.Banco;

import java.util.List;

public interface BancoRepository {
    List<Banco> findAll();
    Banco finById(Long  id);
    void update(Banco banco);
}
