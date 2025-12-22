package controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.StatsService;


@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> addNewUser() {
        return statsService.addNewData();
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return statsService.getData();
    }

}
