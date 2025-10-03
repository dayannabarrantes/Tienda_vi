package com.tienda_vi.service;

import com.tienda_vi.domain.Categoria;
import com.tienda_vi.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    @Transactional
    public void delete(Integer idCategoria) {
        // Se verifica que el idCategoria exista
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException(
                "La Categoria " + idCategoria + " no existe..."
            );
        }

        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                "No se puede eliminar la categoria, tiene datos asociados: " + e
            );
        }
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    /**
     * Este método tiene doble función:
     * - Si idCategoria está vacío, se inserta el registro.
     * - Si idCategoria tiene información, se actualiza ese registro.
     */
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoria = categoriaRepository.save(categoria);

        if (!imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                    imagenFile,
                    "categoria",
                    categoria.getIdCategoria()
                );
                categoria.setRutaImagen(rutaImagen);
                categoriaRepository.save(categoria);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
