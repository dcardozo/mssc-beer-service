package guru.springframework.msscbeerservice.services;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.controller.NotFoundException;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import guru.springframework.msscbeerservice.web.model.BeerDto;
import guru.springframework.msscbeerservice.web.model.BeerPagedList;
import guru.springframework.msscbeerservice.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false")
    @Override
    public BeerDto getById(UUID beerId, boolean showInventoryOnHand) {
        System.out.println(">>> I was called!");
        return showInventoryOnHand
                ? beerMapper.beerToBeerDtoWithInventory(beerRepository.findById(beerId).orElseThrow(NotFoundException::new))
                : beerMapper.beerToBeerDtoWithoutInventory(beerRepository.findById(beerId).orElseThrow(NotFoundException::new));
    }

    @Cacheable(cacheNames = "beerCache", key = "#upc", condition = "#showInventoryOnHand == false")
    @Override
    public BeerDto getByUpc(String upc, boolean showInventoryOnHand) {
        System.out.println(">>> I was called!");
        return showInventoryOnHand
                ? beerMapper.beerToBeerDtoWithInventory(beerRepository.findByUpc(upc).orElseThrow(NotFoundException::new))
                : beerMapper.beerToBeerDtoWithoutInventory(beerRepository.findByUpc(upc).orElseThrow(NotFoundException::new));
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDtoWithInventory(
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
        return beerMapper.beerToBeerDtoWithInventory(
                beerRepository.save(mapped)
        );
    }

    @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, boolean showInventoryOnHand, PageRequest pageRequest) {

        System.out.println(">>> I was called!");

        BeerPagedList beerPagedList;
        Page<Beer> beerPage;

        if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //search both
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
            //search beer_service name
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
        } else if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //search beer_service style
            beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        beerPagedList = new BeerPagedList(beerPage
                .getContent()
                .stream()
                .map(usingBeerMapper(showInventoryOnHand))
                .collect(Collectors.toList()),
                PageRequest
                        .of(beerPage.getPageable().getPageNumber(),
                                beerPage.getPageable().getPageSize()),
                beerPage.getTotalElements());

        return beerPagedList;
    }

    private Function<Beer, BeerDto> usingBeerMapper(boolean showInventoryOnHand) {
        return showInventoryOnHand
                ? beerMapper::beerToBeerDtoWithInventory
                : beerMapper::beerToBeerDtoWithoutInventory;
    }
}
