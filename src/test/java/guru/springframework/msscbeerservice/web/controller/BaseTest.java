package guru.springframework.msscbeerservice.web.controller;

import java.math.BigDecimal;

import guru.springframework.msscbeerservice.bootstrap.BeerLoader;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;

public abstract class BaseTest {
    BeerDto getValidBeerDto() {
        return BeerDto.builder()
                      .beerName("My Beer")
                      .beerStyle(BeerStyleEnum.ALE)
                      .price(new BigDecimal("2.99"))
                      .upc(BeerLoader.BEER_1_UPC)
                      .build();
    }

}
