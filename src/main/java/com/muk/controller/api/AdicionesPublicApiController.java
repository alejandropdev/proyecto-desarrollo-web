package com.muk.controller.api;

import com.muk.service.AdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/adiciones")
@CrossOrigin(origins = "*")
public class AdicionesPublicApiController {

    private final AdicionalService adicionalService;

    @Autowired
    public AdicionesPublicApiController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    @GetMapping
    public List<ApiDtos.AdicionalDto> listarPorCategoria(@RequestParam Long categoriaId) {
        return adicionalService.findActivosByCategoriaId(categoriaId).stream()
                .map(ApiMappers::toAdicionalDto)
                .toList();
    }
}
