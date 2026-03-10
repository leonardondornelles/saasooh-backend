package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.FaceResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Face;
import com.neuralFlux.Saas_OOH_demo.services.FaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panels")
@RequiredArgsConstructor
public class FaceController {

    private final FaceService faceService;

    @PostMapping("/{panelId}/faces")
    public ResponseEntity<FaceResponseDTO> addFace(@PathVariable Long panelId, @Valid @RequestBody Face face){
        Face savedFace = faceService.createFaceToPanel(panelId, face);

        return ResponseEntity.status(HttpStatus.CREATED).body(new FaceResponseDTO(savedFace));
    }

    @GetMapping("/{panelId}/faces")
    public ResponseEntity<List<FaceResponseDTO>> listFacesById(@PathVariable Long panelId){
        List<FaceResponseDTO> dtos = faceService.getFacesByPanel(panelId)
                .stream().map(FaceResponseDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
