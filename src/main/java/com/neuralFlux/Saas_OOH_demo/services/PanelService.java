package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.PanelRequestDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.PanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PanelService {

    private final PanelRepository panelRepository;
    private final CompanyRepository companyRepository;

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
        String codigoGerado = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        panel.setIdentificationCode(codigoGerado);

        return panelRepository.save(panel);
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
}
