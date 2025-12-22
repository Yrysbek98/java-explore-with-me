package service;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface StatsService {

    ResponseEntity<Object> addNewData();

    ResponseEntity<Object> getData();
}
