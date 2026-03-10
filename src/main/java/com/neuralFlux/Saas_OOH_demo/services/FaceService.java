package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.models.Face;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.repositories.FaceRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.PanelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceService {

    private final FaceRepository faceRepository;
    private final PanelRepository panelRepository;

    public Face createFaceToPanel(Long panelId, @Valid Face face) {

        Panel panel = panelRepository.findById((panelId))
                .orElseThrow(() -> new IllegalArgumentException("Erro: O painel especificado não existe"));

        int totalFacesAtuais = faceRepository.findByPanel_Id(panelId).size();

        if(totalFacesAtuais >= panel.getType().getMaxFaces()){
            throw new IllegalArgumentException(
                    "Este painel é do tipo " + panel.getType() +
                            " e só permite " + panel.getType().getMaxFaces() + " face(s)."
            );
        }
        face.setPanel(panel);

        return faceRepository.save(face);
    }

    public List<Face> getFacesByPanel(Long panelId){
        return faceRepository.findByPanel_Id(panelId);
    }
}
