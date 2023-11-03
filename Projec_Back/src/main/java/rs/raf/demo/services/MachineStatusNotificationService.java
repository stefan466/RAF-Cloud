package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.enums.Status;

@Service
public class MachineStatusNotificationService {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyMachineStatusChange(Long machineId, Status newStatus) {
        String topic = "/topic/machine-status/" + machineId;
        messagingTemplate.convertAndSend(topic, newStatus.toString()); // Šaljete promenjeno stanje mašine na određenu temu
    }



}
