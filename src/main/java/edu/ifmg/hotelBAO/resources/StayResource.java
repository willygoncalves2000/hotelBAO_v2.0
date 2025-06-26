package edu.ifmg.hotelBAO.resources;

import edu.ifmg.hotelBAO.dtos.StayDTO;
import edu.ifmg.hotelBAO.services.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/stay")
public class StayResource {

    @Autowired
    private StayService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<StayDTO>> findAll() {
        List<StayDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<StayDTO> insert(@RequestBody StayDTO dto) {
        StayDTO stay = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(stay.getId()).toUri();
        return ResponseEntity.created(uri).body(stay);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<StayDTO> update(@PathVariable Long id, @RequestBody StayDTO dto) {
        StayDTO stay = service.update(id, dto);
        return ResponseEntity.ok().body(stay);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<StayDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }





}
