package edu.ifmg.hotelBAO.services;

import edu.ifmg.hotelBAO.dtos.StayDTO;
import edu.ifmg.hotelBAO.entities.User;
import edu.ifmg.hotelBAO.entities.Room;
import edu.ifmg.hotelBAO.entities.Stay;
import edu.ifmg.hotelBAO.repository.UserRepository;
import edu.ifmg.hotelBAO.repository.RoomRepository;
import edu.ifmg.hotelBAO.repository.StayRepository;
import edu.ifmg.hotelBAO.services.exceptions.DatabaseException;
import edu.ifmg.hotelBAO.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;


    @Transactional(readOnly = true)
    public List<StayDTO> findAll() {
        List<Stay> stays = stayRepository.findAll();
        return stays.stream().map(stay -> new StayDTO(stay)).collect(Collectors.toList());
    }

    @Transactional
    public StayDTO insert(StayDTO dto) {
        Stay stay = new Stay();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Quarto não encontrado"));

        // Verificação de disponibilidade do quarto de acordo com a data
        List<Stay> conflictingStays = stayRepository.findConflictingStays(
                room.getId(), dto.getCheckInDate(), dto.getCheckOutDate());

        if (!conflictingStays.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "O quarto já está reservado nesse período."
            );
        }

        stay.setUser(user);
        stay.setRoom(room);
        stay.setCheckInDate(dto.getCheckInDate());
        stay.setCheckOutDate(dto.getCheckOutDate());

        stay = stayRepository.save(stay);
        return new StayDTO(stay);
    }


    @Transactional
    public StayDTO update(Long id, StayDTO dto) {
        try {
            Stay entity = stayRepository.getReferenceById(id);

            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFound("Client not found with id: " + dto.getUserId()));

            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFound("Room not found with id: " + dto.getRoomId()));

            entity.setUser(user);
            entity.setRoom(room);
            entity.setCheckInDate(dto.getCheckInDate());
            entity.setCheckOutDate(dto.getCheckOutDate());

            entity = stayRepository.save(entity);
            return new StayDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Stay not found with id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!stayRepository.existsById(id)) {
            throw new ResourceNotFound("Stay not found with id: " + id);
        }
        try {
            stayRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }



}
