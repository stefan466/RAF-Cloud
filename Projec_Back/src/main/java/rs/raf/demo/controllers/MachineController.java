package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.enums.Status;
import rs.raf.demo.repositories.MachineRepository;
import rs.raf.demo.requests.CreateRequest;
import rs.raf.demo.requests.ScheduleRequest;
import rs.raf.demo.services.MachineService;
import rs.raf.demo.services.UserService;
import rs.raf.demo.utils.JwtUtil;

import javax.websocket.server.PathParam;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/machines")
public class MachineController {
    private final MachineService machineService;
    private final UserService userService;
    private final MachineRepository machineRepository;
    private final JwtUtil jwtUtil;

    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    public MachineController(MachineService machineService, UserService userService, MachineRepository machineRepository, JwtUtil jwtUtil, SimpMessagingTemplate simpMessagingTemplate) {
        this.machineService = machineService;
        this.userService = userService;
        this.machineRepository = machineRepository;
        this.jwtUtil = jwtUtil;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    private void sendMachineStatusUpdate(Long machineId) {
        Status newStatus = machineService.getMachineStatus(machineId);
        simpMessagingTemplate.convertAndSend("/topic/machine-status", newStatus);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Machine>> getMachinesFiltered(
            @PathParam("mail") String mail,
            @PathParam("name") String name,
            @PathParam("status") String status,
            @PathParam("dateFrom") String dateFrom,
            @PathParam("dateTo") String dateTo) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedFrom = null;
        LocalDate parsedTo = null;
        List<String> machineFilterStatuses = null;

        if (dateFrom != null) {
            parsedFrom = LocalDate.parse(dateFrom, formatter);
        }
        if (dateTo != null) {
            parsedTo = LocalDate.parse(dateTo, formatter);
        }
        if (status != null) {
            machineFilterStatuses = new ArrayList<>(Arrays.asList(status.split(",")));
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_search_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().body(machineService.searchMachines(name, machineFilterStatuses, parsedFrom, parsedTo, mail));

    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Machine>> getMachinesByUser(@PathParam("mail") String mail){
        return ResponseEntity.ok().body(machineService.getMachinesByUser(mail));
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMachine(@RequestBody CreateRequest createRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_create_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Machine createdMachine = machineService.createMachine(createRequest.getName(), createRequest.getMail());
        return ResponseEntity.ok(createdMachine);
    }

    @DeleteMapping(value = "/destroy/{id}")
    private ResponseEntity<?> destroyMachine(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_destroy_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        machineService.destroyMachine(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/start/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startMachine(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_start_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Korisnik nije autentifikovan
        }

        Optional<Machine> optionalMachine = machineService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.STOPPED) {
            machineService.startMachine(id, false);
            sendMachineStatusUpdate(id);


            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/stop/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopMachine(@PathVariable Long id) throws InterruptedException {
        // Provera dozvola korisnika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_stop_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Korisnik nije autentifikovan
        }

        Optional<Machine> optionalMachine = machineService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.RUNNING) {
            machineService.stopMachine(id, false);

            sendMachineStatusUpdate(id);
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/restart/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restartMachine(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            boolean hasPermission = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("can_restart_machines"));

            if (!hasPermission) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Machine> optionalMachine = machineService.findById(id);

        if(optionalMachine.isPresent() && optionalMachine.get().getStatus() == Status.RUNNING) {
            machineService.restartMachine(id, false);
            sendMachineStatusUpdate(id);
            return ResponseEntity.ok(optionalMachine.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/schedule")
    public ResponseEntity<?> scheduleMachine(@RequestBody ScheduleRequest scheduleRequest) throws ParseException {
        machineService.scheduleMachine(scheduleRequest.getId(),scheduleRequest.getDate(),scheduleRequest.getTime(),scheduleRequest.getAction());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/errors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getErrorHistory(@PathParam("id") Long id){
        return ResponseEntity.ok(machineService.findAllErrorsForMachine(id));
    }

}
