package guru.springframework.msscbeerservice.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import guru.springframework.msscbeerservice.bootstrap.BeerLoader;
import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "david0.apiclient.com", uriPort = 80)
@WebMvcTest(BeerUpcController.class)
class BeerUpcControllerTest extends BaseTest{

    private static final String BEERUPC_API_PATH = "/api/v1/beerUpc/";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BeerService beerService;

    @MockBean
    BeerInventoryService beerInventoryService;

    @Test
    void getBeerByUpc() throws Exception {
        given(beerService.getByUpc(any(), anyBoolean())).willReturn(getValidBeerDto());
        mockMvc.perform(get(BEERUPC_API_PATH + "{upc}", BeerLoader.BEER_1_UPC)
                .param("showInventoryOnHand", "true")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(document("v1/beer-get-by-upc",
                       pathParameters(
                               parameterWithName("upc").description("UPC of desired beer to get.")
                       ),
                       requestParameters(
                               parameterWithName("showInventoryOnHand").description("Should show the available inventory?")
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
}