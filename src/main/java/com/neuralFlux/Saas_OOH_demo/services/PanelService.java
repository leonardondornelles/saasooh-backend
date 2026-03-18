package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.PanelRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.details.CampaignSnippetDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.details.FaceDetailDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.details.PanelDetailsDTO;
import com.neuralFlux.Saas_OOH_demo.enums.FaceStatus;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Face;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.FaceRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.PanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PanelService {

    private final PanelRepository panelRepository;
    private final CompanyRepository companyRepository;
    private final FaceRepository faceRepository;
    private final CampaignRepository campaignRepository;

    public Panel createPanel(Long companyId, PanelRequestDTO dto){

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Panel panel = new Panel();
        panel.setCompany(company);
        panel.setAddress(dto.address());
        panel.setCity(dto.city());
        panel.setLatitude(dto.latitude());
        panel.setLongitude(dto.longitude());
        panel.setType(dto.panelType());
        panel.setIdentificationCode(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Panel savedPanel = panelRepository.save(panel);

        int quantityFaces = savedPanel.getType().getMaxFaces();
        char faceChar = 'A';

        for(int i = 0; i < quantityFaces; i++){
            Face face = new Face();
            face.setPanel(savedPanel);
            face.setName("Face " + (char) (faceChar + i));
            face.setFormat("Padrão");
            face.setActive(true);

            faceRepository.save(face);
        }
        return savedPanel;
    }

    public List<Panel> getPanelByCompanyId(Long companyId){
        return panelRepository.findByCompanyId(companyId);
    }

    public void deletePanel(Long panelId, Long companyId) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new RuntimeException("Painel não encontrado"));

        if(!panel.getCompany().getId().equals(companyId)){
            throw new RuntimeException("Acesso Negado! Você não pode excluir o painel de outra empresa");
        }

        panelRepository.delete(panel);
    }

    public PanelDetailsDTO getPanelDetails(Long panelId, Long companyId) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new RuntimeException("Painel não encontrado"));

        if (!panel.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Acesso negado");
        }

        List<Face> faces = faceRepository.findByPanel_Id(panelId);

        DateTimeFormatter formatterBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        List<FaceDetailDTO> facesDTO = faces.stream().map(face -> {

            List<Campaign> campaigns = campaignRepository.findAllByFaceIdAndStatusIn(
                    face.getId(),
                    List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED)
            );

            List<CampaignSnippetDTO> snippets = campaigns.stream().map(c -> {
                long diasRestantes = ChronoUnit.DAYS.between(today, c.getEndDate());
                long totalDias = ChronoUnit.DAYS.between(c.getStartDate(), c.getEndDate());

                return new CampaignSnippetDTO(
                        c.getCustomer().getFantasyName(),
                        c.getStartDate().format(formatterBR),
                        c.getEndDate().format(formatterBR),
                        Math.max(0, (int) diasRestantes),
                        (int) totalDias,
                        "R$ " + c.getMonthlyValue()
                );
            }).toList();

            FaceStatus activeStatus = FaceStatus.LIVRE;
            boolean hasFutureCampaign = false;

            for (Campaign c : campaigns) {
                if (!today.isBefore(c.getStartDate()) && !today.isAfter(c.getEndDate())) {
                    activeStatus = FaceStatus.OCUPADO;
                    break;
                }
                else if(c.getStartDate().isAfter(today)){
                    hasFutureCampaign = true;
                }
            }

            if (activeStatus == FaceStatus.LIVRE && !campaigns.isEmpty()) {
                activeStatus = FaceStatus.RESERVADO;
            }

            return new FaceDetailDTO(
                    face.getId(),
                    face.getName(),
                    face.getFormat(),
                    activeStatus,
                    true,
                    snippets
            );
        }).toList();

        return new PanelDetailsDTO(
                "OUT-" + panel.getId(),
                panel.getType().name(),
                panel.getAddress(),
                panel.getCity(),
                panel.getLatitude(),
                panel.getLongitude(),
                facesDTO
        );
    }
}
