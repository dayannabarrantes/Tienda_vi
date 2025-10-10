package com.tienda_vi.controller;

import com.tienda_vi.domain.Producto;
import com.tienda_vi.service.ProductoService;
import com.tienda_vi.service.FirebaseStorageService;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Guarda o actualiza una categoría.
     * Si no tiene id, se inserta. Si tiene id, se actualiza.
     */
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        productoService.save(producto, imagenFile); // ✅ Llamada corregida al Service
    }

    /**
     * Controlador para guardar una categoría desde el formulario.
     */
    @PostMapping("/guardar")
    public String guardar(Producto producto, MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        productoService.save(producto, imagenFile);
        redirectAttributes.addFlashAttribute(
            "todook",
            messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );
        return "redirect:/producto/listado";
    }

    /**
     * Controlador para eliminar una categoría.
     */
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        String titulo = "todook";
        String detalle = "mensaje.eliminado";
        
        try {
            productoService.delete(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "producto.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "producto.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "producto.error03";
        }

        redirectAttributes.addFlashAttribute(
            titulo,
            messageSource.getMessage(detalle, null, Locale.getDefault())
        );
        
        return "redirect:/producto/listado";
    }

    /**
     * Controlador para modificar una categoría existente.
     */
    @GetMapping("/modificar/{idProducto}")
    public String modificar(
        @PathVariable("idProducto") Integer idProducto,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Optional<Producto> productoOpt = productoService.getProducto(idProducto);
        
        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                messageSource.getMessage("producto.error01", null, Locale.getDefault())
            );
            return "redirect:/producto/listado";
        }
        
        model.addAttribute("producto", productoOpt.get());
        return "/producto/modifica";
    }
}
