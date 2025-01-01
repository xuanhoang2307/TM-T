package huce.edu.web.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import huce.edu.web.model.Address;
import huce.edu.web.model.User;
import huce.edu.web.services.AddressService;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/home")
public class AddressController {
	@Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;
    
    @GetMapping("/addresses/{userId}")
    public String getUserAddresses(@PathVariable Long userId, Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
        List<Address> addresses = addressService.findByUser(user);
        if (addresses.isEmpty()) {
            // Thêm thông báo vào Model
            model.addAttribute("message", "Hiện tại bạn chưa có địa chỉ nào. Vui lòng thêm địa chỉ mới.");
        }
        model.addAttribute("addresses", addresses);
        return "layout/users/addresses";
    }

    @PostMapping("/addresses")
    @ResponseBody
    public Address addAddress(@RequestBody Address address) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        address.setUser(user);  // Gán user cho địa chỉ
        address.setPhoneNumber(address.getPhoneNumber());
        return addressService.addAddress(address);
    }

    @PutMapping("/addresses/update/{id}")
    public Address updateAddress(@PathVariable Long id, @RequestBody Address address) {
        return addressService.updateAddress(id, address);
    }

    @DeleteMapping("/addresses/deleteadd/{id}")
    @ResponseBody
    public String deleteAddress(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);      
        if (user != null) {
            Long userId = user.getId();  // Lấy userId từ đối tượng User
            // Gọi service xóa địa chỉ
            addressService.deleteAddress(id, userId);
        } else {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        }
        return "redirect:/home/addresses/" + user.getId();
    }

    @PutMapping("/addresses/setdefault/{id}")
    @ResponseBody
    public String setDefaultAddress(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);       
        if (user != null) {
            Long userId = user.getId();  
            addressService.setDefaultAddress(id, userId);
        } else {
            throw new RuntimeException("Không tìm thấy người dùng với username: " + username);
        }
        return "redirect:/home/addresses/" + user.getId();
    }
    
    @GetMapping("/dashboard/address")
	public String ViewProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
        
        List<Address> addresses = addressService.findByUser(user); 
        if (addresses == null || addresses.isEmpty()) {
            // Nếu không có địa chỉ nào, tạo mới một đối tượng Address
            addresses = new ArrayList<>();
        }
        // Nếu bạn muốn lấy địa chỉ đầu tiên, hoặc theo một điều kiện khác
        Address address = addresses.isEmpty() ? new Address() : addresses.get(0);
	    model.addAttribute("address", address );
		return "/layout/users/changeaddress";
	}
	
	
}
