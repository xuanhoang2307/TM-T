package huce.edu.web.model;


import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	 @JsonBackReference
	private User user;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> cartItems ;
	
	@Column(name = "total_price")
	private double totalPrice;
	
	@Column(name = "total_item")
	private int totalItem; 
	
	private double totalDiscountedPrice;

	public void recalculateCart() {
		double calculatedTotalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();
		this.totalPrice = Math.round(calculatedTotalPrice * 100.0) / 100.0; 
        this.totalItem = cartItems.size();     
        
    }
	
	@Override
	public int hashCode() {
	    return Objects.hash(id, user, totalPrice, totalItem, totalDiscountedPrice); // Bỏ cartItems
	}
	
}
