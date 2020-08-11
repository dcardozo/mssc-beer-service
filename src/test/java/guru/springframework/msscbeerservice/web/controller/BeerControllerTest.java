package guru.springframework.msscbeerservice.web.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbeerservice.bootstrap.BeerLoader;
import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "david0.apiclient.com", uriPort = 80)
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "guru.springframework.msscbeerservice.web.mappers")
class BeerControllerTest {

    private static final String BEER_API_PATH = "/api/v1/beer/";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @Test
    void getBeer() throws Exception {
        given(beerService.getById(any())).willReturn(getValidBeerDto());
        mockMvc.perform(get(BEER_API_PATH + "{beerId}", UUID.randomUUID().toString())
                .param("iscold", "yes") // not used; just for RESTDocs demo purpose
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(document("v1/beer-get",
                       pathParameters(
                               parameterWithName("beerId").description("UUID of desired beer to get.")
                       ),
                       requestParameters(
                               parameterWithName("iscold").description("Is Beer Cold Query param")
                       ),
                       responseFields(
                               fieldWithPath("id").description("Id of Beer"),
                               fieldWithPath("version").description("Version number"),
                               fieldWithPath("createdDate").description("Date Created"),
                               fieldWithPath("lastModifiedDate").description("Date Updated"),
                               fieldWithPath("beerName").description("Beer Name"),
                               fieldWithPath("beerStyle").description("Beer Style"),
                               fieldWithPath("upc").description("UPC of Beer"),
                               fieldWithPath("price").description("Price"),
                               fieldWithPath("quantityOnHand").description("Quantity On Hand")
                       )
               ));
    }

    @Test
    void saveNewBeer() throws Exception {
        given(beerService.saveNewBeer(any())).willReturn(getValidBeerDto());
        BeerDto dto = getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(dto);

        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
        mockMvc.perform(post(BEER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
               .andExpect(status().isCreated())
               .andDo(document("v1/beer-new",
                       requestFields(
                               fields.withPath("id").ignored(),
                               fields.withPath("version").ignored(),
                               fields.withPath("createdDate").ignored(),
                               fields.withPath("lastModifiedDate").ignored(),
                               fields.withPath("beerName").description("Beer Name"),
                               fields.withPath("beerStyle").description("Beer Style"),
                               fields.withPath("upc").description("UPC of Beer"),
                               fields.withPath("price").description("Price"),
                               fields.withPath("quantityOnHand").ignored()
                       )));
    }

    @Test
    void updateBeerById() throws Exception {
        given(beerService.updateBeer(any(), any())).willReturn(getValidBeerDto());
        BeerDto dto = getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(BEER_API_PATH + UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
               .andExpect(status().isNoContent());
    }

    BeerDto getValidBeerDto() {
        return BeerDto.builder()
                      .beerName("My Beer")
                      .beerStyle(BeerStyleEnum.ALE)
                      .price(new BigDecimal("2.99"))
                      .upc(BeerLoader.BEER_1_UPC)
                      .build();
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}