package com.tienda_vi.controller;

import com.tienda_vi.domain.Categoria;
import com.tienda_vi.service.CategoriaService;
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
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Guarda o actualiza una categoría.
     * Si no tiene id, se inserta. Si tiene id, se actualiza.
     */
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoriaService.save(categoria, imagenFile); // ✅ Llamada corregida al Service
    }

    /**
     * Controlador para guardar una categoría desde el formulario.
     */
    @PostMapping("/guardar")
    public String guardar(Categoria categoria, MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        categoriaService.save(categoria, imagenFile);
        redirectAttributes.addFlashAttribute(
            "todook",
            messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );
        return "redirect:/categoria/listado";
    }

    /**
     * Controlador para eliminar una categoría.
     */
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria, RedirectAttributes redirectAttributes) {
        String titulo = "todook";
        String detalle = "mensaje.eliminado";
        
        try {
            categoriaService.delete(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "categoria.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "categoria.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "categoria.error03";
        }

        redirectAttributes.addFlashAttribute(
            titulo,
            messageSource.getMessage(detalle, null, Locale.getDefault())
        );
        
        return "redirect:/categoria/listado";
    }

    /**
     * Controlador para modificar una categoría existente.
     */
    @GetMapping("/modificar/{idCategoria}")
    public String modificar(
        @PathVariable("idCategoria") Integer idCategoria,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);
        
        if (categoriaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error",
                messageSource.getMessage("categoria.error01", null, Locale.getDefault())
            );
            return "redirect:/categoria/listado";
        }
        
        model.addAttribute("categoria", categoriaOpt.get());
        return "/categoria/modifica";
    }
}
