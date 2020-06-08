package br.com.itau.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
	    return new Docket(DocumentationType.SWAGGER_2)
	        .select()
	        .apis(RequestHandlerSelectors.basePackage("br.com.itau.controle"))
	        .paths(PathSelectors.any())
	        .build()
	        .useDefaultResponseMessages(false)
	        .apiInfo(apiInfo());
	}
	
	private ApiInfo apiInfo() {
	    return new ApiInfoBuilder()
	            .title("Case Itaú - Api de transferência")
	            .description("O objetivo desta tarefa é demonstrar as suas habilidades de desenvolvimento e\r\n" + 
	            		"verificar os conceitos que você possui a respeito de engenharia de software.\r\n" + 
	            		"Não existe apenas um único meio de resolver o problema, mas busque sempre ser\r\n" + 
	            		"claro e prático em suas soluções.\r\n" + 
	            		"Lembre-se do conceito KISS – Keep it Simple Stupid.")
	            .version("v1 - 1.0")
	            .contact(new Contact("Bruno Barbosa da Silva", "https://www.linkedin.com/in/bruno-barbosa-6a550a29/", "brunobsgt@gmail.com"))
	            .build();
	}
}

