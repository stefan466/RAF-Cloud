package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.User;
import rs.raf.demo.model.enums.Status;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.MachineRepository;
import rs.raf.demo.repositories.UserRepository;

import javax.persistence.LockModeType;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MachineService {

    private MachineRepository machineRepository;
    private UserRepository userRepository;
    private ErrorMessageRepository errorMessageRepository;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);


    @Autowired
    public MachineService(MachineRepository machineRepository, UserRepository userRepository, ErrorMessageRepository errorMessageRepository) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.errorMessageRepository = errorMessageRepository;

    }


    @Transactional
    public Optional<Machine> findById(Long id) {
        System.err.println("finding");
        return machineRepository.findById(id);
    }


    @Transactional
    public Collection<Machine> getMachinesByUser(String userMail) {
        return machineRepository.findAllByCreatedBy(userRepository.findByMail(userMail));
    }


    @Transactional
    public Collection<Machine> searchMachines(String name, List<String> statuses, LocalDate dateFrom, LocalDate dateTo, String userMail) {
        List<Machine> allMachinesByUser = new ArrayList<>(getMachinesByUser(userMail));
        List<Machine> filteredMachines = new ArrayList<>();

        for (Machine machine : allMachinesByUser) {
            if ((name == null || machine.getName().toLowerCase().contains(name.toLowerCase()))
                    && (statuses == null || statuses.contains(machine.getStatus().toString()))
                    && (dateFrom == null || dateTo == null || (machine.getCreationDate().isAfter(dateFrom) && machine.getCreationDate().isBefore(dateTo)))) {
                filteredMachines.add(machine);
            }
        }

        return filteredMachines;
    }


    public Machine createMachine(String name, String userMail) {
        System.err.println("creating machine");
        return machineRepository.save(new Machine(0L, Status.STOPPED, userRepository.findByMail(userMail), true, name, LocalDate.now()/*, 0*/));
    }

    @Transactional
    public Collection<ErrorMessage> findAllErrorsForMachine(Long id){
        return errorMessageRepository.findAllByMachineId(id);
    }


    @Transactional
    public void destroyMachine(Long id) {
        System.err.println("destroying machine");
        Optional<Machine> optionalMachine = this.findById(id);
        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();
            if (machine.getStatus() != Status.STOPPED) return;
            machine.setActive(false);
            machineRepository.save(machine);
        }
    }


    @Transactional
    public void startMachine(Long id, boolean scheduled) {
        executorService.schedule(() -> {
            Optional<Machine> optionalMachine = machineRepository.findById(id);
            if (optionalMachine.isPresent()) {
                Machine machine = optionalMachine.get();
                if (machine.isActive()) {
                    if (machine.getStatus() == Status.STOPPED) {
                        System.err.println("Starting machine");
                        machine.setStatus(Status.RUNNING);
                        machineRepository.save(machine);
                        System.err.println("Machine started");
                    } else if (scheduled) {
                        errorMessageRepository.save(new ErrorMessage(0L, "The machine's status is not 'STOPPED'.", "START", LocalDate.now(), machine));
                    }
                } else if (scheduled) {
                    errorMessageRepository.save(new ErrorMessage(0L, "The machine is deactivated.", "START", LocalDate.now(), machine));
                }
            }
        }, (long) (Math.random() * (15000 - 10000) + 10000), TimeUnit.MILLISECONDS);
    }



    @Transactional
    public void stopMachine(Long id, boolean scheduled) {
        executorService.schedule(() -> {
            Optional<Machine> optionalMachine = machineRepository.findById(id);
            if (optionalMachine.isPresent()) {
                Machine machine = optionalMachine.get();
                if (machine.isActive()) {
                    if (machine.getStatus() == Status.RUNNING) {
                        System.err.println("Stopping machine");
                        machine.setStatus(Status.STOPPED);
                        machineRepository.save(machine);
                        System.err.println("Machine stopped");
                    } else if (scheduled) {
                        errorMessageRepository.save(new ErrorMessage(0L, "The machine's status is not 'RUNNING'.", "STOP", LocalDate.now(), machine));
                    }
                } else if (scheduled) {
                    errorMessageRepository.save(new ErrorMessage(0L, "The machine is deactivated.", "STOP", LocalDate.now(), machine));
                }
            }
        }, (long) (Math.random() * (15000 - 10000) + 10000), TimeUnit.MILLISECONDS);
    }





    @Transactional
    public void restartMachine(Long id, boolean scheduled) {
        executorService.schedule(() -> {
            Optional<Machine> optionalMachine = machineRepository.findById(id);
            if (optionalMachine.isPresent()) {
                Machine machine = optionalMachine.get();
                if (machine.isActive()) {
                    if (machine.getStatus() == Status.RUNNING) {
                        System.err.println("Restarting machine");
                        machine.setStatus(Status.STOPPED);
                        machineRepository.save(machine);

                        try {
                            Thread.sleep((long) (Math.random() * (10000 - 5000) + 5000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        machine.setStatus(Status.RUNNING);
                        machineRepository.save(machine);

                        System.err.println("Machine restarted");
                    } else if (scheduled) {
                        errorMessageRepository.save(new ErrorMessage(0L, "The machine's status is not 'RUNNING'.", "RESTART", LocalDate.now(), machine));
                    }
                } else if (scheduled) {
                    errorMessageRepository.save(new ErrorMessage(0L, "The machine is deactivated.", "RESTART", LocalDate.now(), machine));
                }
            }
        }, (long) (Math.random() * (15000 - 10000) + 10000), TimeUnit.MILLISECONDS);
    }


    private void logError(Long machineId, String action, String errorMessage) {
        ErrorMessage error = new ErrorMessage();
        error.setMachine(machineRepository.findById(machineId).orElse(null));
        error.setAction(action);
        error.setMessage(errorMessage);
        errorMessageRepository.save(error);
    }

    @Transactional
    public void scheduleMachine(Long id, String date, String time, String action) throws ParseException {
        Date scheduledTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " " + time);
        System.err.println("Machine scheduled for " + scheduledTime);
        Machine machine = machineRepository.findById(id).orElse(null);
        System.out.println("MASINA: " + machine.getId() + " STATUS: " + machine.getStatus() + "ACTION: " + action);

        long delay = scheduledTime.getTime() - System.currentTimeMillis();
        System.out.println("DELAY: " + delay);
        if (delay > 0) {
            executorService.schedule(() -> {
                try {
                    System.out.println("da li je usao u ovaj try");

                    System.out.println("da li je usao u ovaj switch");

                    switch (action) {
                        case "Start":
                            if (machine.getStatus() == Status.STOPPED) {
                                startMachine(id, true);
                            } else {
                                System.out.println("ACTION:  " + action);
                                logError(id, action, "Machine is already running.");
                            }
                            break;
                        case "Stop":
                            if (machine.getStatus() == Status.RUNNING) {
                                stopMachine(id, true);
                            } else {
                                System.out.println("ACTION:  " + action);
                                logError(id, action, "Machine is not running.");
                            }
                            break;
                        case "Restart":
                            if (machine.getStatus() == Status.RUNNING) {
                                restartMachine(id, true);
                            } else {
                                logError(id, action, "Machine is not running.");
                            }
                            break;
                        default:
                            System.err.println("Invalid action specified.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    String errorMsg = "Error occurred during scheduled operation: " + e.getMessage();


                }
            }, delay, TimeUnit.MILLISECONDS);
        }
    }


    public Status getMachineStatus(Long machineId) {
        Machine machine = machineRepository.findById(machineId).orElse(null);

        if (machine != null) {
            return machine.getStatus();
        } else {
            return null;
        }
    }
}
