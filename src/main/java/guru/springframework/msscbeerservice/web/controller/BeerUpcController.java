package guru.springframework.msscbeerservice.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.msscbeerservice.services.BeerService;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/beerUpc")
@RestController
public class BeerUpcController {

    private final BeerService beerService;

    @GetMapping("/{upc}")
    public ResponseEntity<BeerDto> getBeerByUpc(
            @PathVariable("upc") String upc,
            @RequestParam(value = "showInventoryOnHand", required = false, defaultValue = "false") boolean showInventoryOnHand) {
        return new ResponseEntity<>(beerService.getByUpc(upc, showInventoryOnHand), HttpStatus.OK);
    }
}
