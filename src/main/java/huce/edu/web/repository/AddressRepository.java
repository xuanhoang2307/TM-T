package huce.edu.web.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import huce.edu.web.model.Address;
import huce.edu.web.model.User;

public interface AddressRepository extends JpaRepository<Address, Long>{
	List<Address> findByUser(User user);
	List<Address> findByUserAndIsDeletedFalse(User user);
	List<Address> findByUserId(Long userId);
	Address findByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}
