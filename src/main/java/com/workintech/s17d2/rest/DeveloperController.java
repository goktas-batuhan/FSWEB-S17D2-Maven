package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        this.developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAll() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer developer) {
        Developer createdDeveloper;
        if (developer.getExperience() == Experience.JUNIOR) {
            double rate = (taxable != null && taxable.getSimpleTaxRate() != null) ? taxable.getSimpleTaxRate() : 15d;
            double calculatedSalary = developer.getSalary() - (developer.getSalary() * rate / 100);
            createdDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), calculatedSalary);
        } else if (developer.getExperience() == Experience.MID) {
            double rate = (taxable != null && taxable.getMiddleTaxRate() != null) ? taxable.getMiddleTaxRate() : 25d;
            double calculatedSalary = developer.getSalary() - (developer.getSalary() * rate / 100);
            createdDeveloper = new MidDeveloper(developer.getId(), developer.getName(), calculatedSalary);
        } else if (developer.getExperience() == Experience.SENIOR) {
            double rate = (taxable != null && taxable.getUpperTaxRate() != null) ? taxable.getUpperTaxRate() : 35d;
            double calculatedSalary = developer.getSalary() - (developer.getSalary() * rate / 100);
            createdDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), calculatedSalary);
        } else {
            createdDeveloper = developer;
        }
        developers.put(developer.getId(), createdDeveloper);
        return createdDeveloper;
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer developer) {
        developer.setId(id);
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }
}
