package org.mzerpa.test.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
@Entity
@Table(name="bancos")
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(name="total_tranferencias")
    private int totalTransferencia;

    public Banco() {
    }

    public Banco(Long id, String nombre, int totalTransferencia) {
        this.id = id;
        this.nombre = nombre;
        this.totalTransferencia = totalTransferencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalTransferencias() {
        return totalTransferencia;
    }

    public void setTotalTransferencias(int totalTransferencia) {
        this.totalTransferencia = totalTransferencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Banco banco)) return false;
        return getTotalTransferencias() == banco.getTotalTransferencias() && Objects.equals(getId(), banco.getId()) && Objects.equals(getNombre(), banco.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombre(), getTotalTransferencias());
    }
}
