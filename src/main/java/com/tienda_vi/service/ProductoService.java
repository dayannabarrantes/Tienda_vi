package com.tienda_vi.service;

import com.tienda_vi.domain.Producto;
import com.tienda_vi.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void delete(Integer idProducto) {
        // Se verifica que el idProducto exista
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException(
                "La Producto " + idProducto + " no existe..."
            );
        }

        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                "No se puede eliminar la producto, tiene datos asociados: " + e
            );
        }
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    /**
     * Este método tiene doble función:
     * - Si idProducto está vacío, se inserta el registro.
     * - Si idProducto tiene información, se actualiza ese registro.
     */
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);

        if (!imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                    imagenFile,
                    "producto",
                    producto.getIdProducto()
                );
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
