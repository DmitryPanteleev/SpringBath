package ru.homework.dpanteleev.springbath.service;

import org.springframework.stereotype.Service;
import ru.homework.dpanteleev.springbath.model.Beer;

@Service
public class AddBeerService {

    public Beer addBeer(Beer beer) {
        beer.setVolume(beer.getVolume() * 2);
        return beer;
    }
}
