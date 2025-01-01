package huce.edu.web.services;

import java.util.List;

import huce.edu.web.model.Address;
import huce.edu.web.model.User;

public interface AddressService {
	List<Address> findByUser(User user);
	void save(Address address);
	Address addAddress(Address address);
	List<Address> getAddressesByUser(Long userId);
	Address findById(Long id);
	Address updateAddress(Long id, Address address);
	void deleteAddress(Long id, Long userId);
	Address setDefaultAddress(Long id, Long userId);
}
