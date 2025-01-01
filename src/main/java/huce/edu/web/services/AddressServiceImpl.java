package huce.edu.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import huce.edu.web.model.Address;
import huce.edu.web.model.User;
import huce.edu.web.repository.AddressRepository;
import jakarta.transaction.Transactional;

@Service
public class AddressServiceImpl implements AddressService{
	
	@Autowired
    private AddressRepository addressRepository;

	@Override
	public List<Address> findByUser(User user) {
		// TODO Auto-generated method stub
		return addressRepository.findByUserAndIsDeletedFalse(user);
	}
	
	@Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);  
    }
	@Override
	public void save(Address address) {		
		User user = address.getUser();
        // Xóa địa chỉ cũ nếu tồn tại
		List<Address> existingAddress = findByUser(user);
		if (existingAddress != null && !existingAddress.isEmpty()) {
	        // Xóa địa chỉ trùng với địa chỉ mới
	        for (Address oldAddress : existingAddress) {
	            if (oldAddress.equals(address)) {
	                addressRepository.delete(oldAddress);
	            }
	        }
	    }
		addressRepository.save(address);
	}
	
	@Override
	public List<Address> getAddressesByUser(Long userId) {
		// TODO Auto-generated method stub
		return addressRepository.findByUserId(userId);
	}

	@Override
	public Address updateAddress(Long id, Address address) {
		// TODO Auto-generated method stub
		Address existingAddress = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
        existingAddress.setRecipientName(address.getRecipientName());
        existingAddress.setPhoneNumber(address.getPhoneNumber());
        existingAddress.setStreetAddress(address.getStreetAddress());
        existingAddress.setCity(address.getCity());
        existingAddress.setDistrict(address.getDistrict());
        existingAddress.setIsDefault(address.isDefault());
        return addressRepository.save(existingAddress);
	}

	@Override
	@Transactional
	public void deleteAddress(Long id, Long userId) {
		// TODO Auto-generated method stub
		Address address = addressRepository.findByIdAndUserId(id, userId);
        
        if (address != null) {
        	address.setIsDeleted(true);
        	address.setIsDefault(false);
            addressRepository.save(address);

        } else {
            throw new RuntimeException("Address not found for user with ID: " + userId);
        }
	}

	@Override
	public Address setDefaultAddress(Long id, Long userId) {
		// TODO Auto-generated method stub
		 List<Address> addresses = addressRepository.findByUserId(userId);
	        addresses.forEach(address -> address.setIsDefault(false));
	        addressRepository.saveAll(addresses);
	        Address address = addressRepository.findByIdAndUserId(id, userId);
	        address.setIsDefault(true);
	        return addressRepository.save(address);
	}

	@Override
	public Address findById(Long id) {
		// TODO Auto-generated method stub
		return addressRepository.findById(id).get();
	}

}
