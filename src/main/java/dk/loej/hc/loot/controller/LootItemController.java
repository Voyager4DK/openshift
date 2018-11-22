package dk.loej.hc.loot.controller;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import dk.loej.hc.loot.entity.LootItemProperties;
import dk.loej.hc.loot.util.LootDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.repository.LootItemRepository;

@Controller
@RequestMapping(value = "api/loot_items")
public class LootItemController {

    private final LootItemRepository repository;

    @Autowired
    public LootItemController(LootItemRepository repository) {
        this.repository = repository;
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
    	Date lastLootDate = Date.valueOf(LootDateCalculator.getLastLootDate());
        return StreamSupport
                .stream(repository.findByLootDateOrderByPrioritySequenceAsc(lastLootDate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem post(@RequestBody(required = false) LootItem lootItem) {
        verifyCorrectPayload(lootItem);

        lootItem.setLootDate(Date.valueOf(LootDateCalculator.getLastLootDate()));

        return repository.save(lootItem);
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem get(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        return repository.findOne(id);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem put(@PathVariable("id") Integer id, @RequestBody(required = false) LootItem lootItem) {
        verifyLootItemExists(id);
        verifyCorrectPayload(lootItem);

        lootItem.setLootDate(Date.valueOf(LootDateCalculator.getLastLootDate()));

        lootItem.setId(id);
        return repository.save(lootItem);
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}/change_sequence", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem changeSequence(@PathVariable("id") Integer id, @RequestBody(required = false) Integer change) {
        System.out.println("changeSequence, id=" + id + ", change=" + change);
    	verifyLootItemExists(id);
        LootItem lootItem = repository.findOne(id);
        
        lootItem.setPrioritySequence(lootItem.getPrioritySequence() + change);
        
        //TODO also update sequence of the lootItem that has the sequence now
        
        //TODO also check max value
        if (lootItem.getPrioritySequence() > 0) {
        	System.out.println("About to save sequence=" + lootItem.getPrioritySequence());
        	return repository.save(lootItem);
        } else {
        	return repository.findOne(id);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        repository.delete(id);
    }

    @ResponseBody
    @GetMapping(value = "/loot_item_properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItemProperties getLootItemProperties() {
        return LootDateCalculator.getLootItemProperties();
    }

    private void verifyLootItemExists(Integer id) {
        if (!repository.exists(id)) {
            throw new RuntimeException(String.format("LootItem with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(LootItem lootItem) {
        if (Objects.isNull(lootItem)) {
            throw new RuntimeException("LootItem cannot be null");
        }

        if (!Objects.isNull(lootItem.getId())) {
            throw new RuntimeException("Id field must be generated");
        }
    }
}