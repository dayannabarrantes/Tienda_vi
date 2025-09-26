package com.tienda_vi.service;

import com.tienda_vi.domain.Categoria;
import com.tienda_vi.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        } 
            return categoriaRepository.findAll();
        }
    }
