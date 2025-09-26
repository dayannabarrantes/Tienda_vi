package com.tienda_vi.repository;

import com.tienda_vi.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{

    public List<Categoria> findByActivoTrue();
}
