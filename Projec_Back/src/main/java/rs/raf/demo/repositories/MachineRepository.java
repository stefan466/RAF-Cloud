package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.User;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Collection<Machine> findAllByCreatedBy(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public Optional<Machine> findById(Long id);

}
