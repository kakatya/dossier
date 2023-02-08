package ru.kakatya.dossier.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ru.kakatya.dossier.dtos.ApplicationDto;

import java.util.List;

@FeignClient(name = "${service.name}", url = "${service.url}")
public interface DealServiceFeignClient {
    @GetMapping("/{applicationId}")
    ResponseEntity<ApplicationDto> getApplication(@PathVariable Long applicationId);

    @GetMapping
    ResponseEntity<List<ApplicationDto>> getAllApplications();

    @PutMapping("/{applicationId}/status")
    ResponseEntity<Void> changeApplicationStatus(@PathVariable Long applicationId);
}
