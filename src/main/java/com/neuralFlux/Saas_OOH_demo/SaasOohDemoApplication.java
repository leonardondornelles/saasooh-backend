package com.neuralFlux.Saas_OOH_demo;

import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class SaasOohDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasOohDemoApplication.class, args);
	}

	/**
	 * Semeia o banco de dados inicial caso esteja vazio.
	 * Cria a primeira Empresa e o Super Admin.
	 */
	@Bean
	CommandLineRunner run(CompanyRepository companyRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Verifica se já existe algum utilizador. Se não existir, cria o Admin.
			if (userRepository.count() == 0) {
				System.out.println("⚠️ Banco de dados vazio detectado. Criando Super Admin...");

				// 1. Cria a Empresa
				Company myCompany = new Company();
				myCompany.setCorporateName("Neural Flux OOH");
				myCompany.setFantasyName("Neural Flux Media");
				myCompany.setCnpj("12.345.678/0001-99");
				myCompany.setSaasPlan(SaasPlan.ENTERPRISE); // Dá o plano máximo ao teu utilizador de teste!
				myCompany.setActive(true);
				Company savedCompany = companyRepository.save(myCompany);

				// 2. Cria o Super Admin
				User admin = new User();
				admin.setName("Leonardo Admin");
				admin.setEmail("admin@neuralflux.com"); // <-- USA ESTE EMAIL PARA LOGAR
				admin.setPassword(passwordEncoder.encode("123456")); // <-- E ESTA SENHA
				admin.setRole(RoleUser.ADMIN);
				admin.setCompany(savedCompany);
				admin.setActive(true);
				userRepository.save(admin);

				System.out.println("✅ Super Admin criado com sucesso! Use admin@neuralflux.com / 123456");
			}
		};
	}
}