package guru.springframework.msscbeerservice.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.controller.NotFoundException;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public BeerDto getById(UUID beerId) {
        return beerMapper.beerToBeerDto(
                beerRepository.findById(beerId).orElseThrow(NotFoundException::new)
        );
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDto(
                beerRepository.save(
                        beerMapper.beerDtoToBeer(beerDto)
                )
        );
    }

    @Override
    public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

//        beer.setBeerName(beerDto.getBeerName());
//        beer.setBeerStyle(beerDto.getBeerStyle().name());
//        beer.setPrice(beerDto.getPrice());
//        beer.setUpc(beerDto.getUpc());
        Beer mapped = beerMapper.beerDtoToBeer(beerDto);
        mapped.setId(beer.getId());
        return beerMapper.beerToBeerDto(
                beerRepository.save(mapped)
        );
    }
}
