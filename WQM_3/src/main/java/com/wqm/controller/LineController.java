package com.wqm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.wqm.entity.Line;
import com.wqm.repository.LineRepository;

@RestController
@RequestMapping("/api/lines")
public class LineController {

    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public List<Line> getAll() {
        return lineRepository.findAll();
    }

    @GetMapping("/{id}")
    public Line getById(@PathVariable Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Line create(@RequestBody Line line) {
        line.setId(null);
        return lineRepository.save(line);
    }

    @PutMapping("/{id}")
    public Line update(@PathVariable Long id, @RequestBody Line request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found"));
        line.setName(request.getName());
        line.setLocation(request.getLocation());
        line.setActive(request.isActive());
        return lineRepository.save(line);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!lineRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found");
        }
        lineRepository.deleteById(id);
    }
}
