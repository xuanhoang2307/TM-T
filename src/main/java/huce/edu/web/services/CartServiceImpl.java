package huce.edu.web.services;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import huce.edu.web.model.Cart;
import huce.edu.web.model.CartItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.Size;
import huce.edu.web.model.User;
import huce.edu.web.repository.CartRepository;
import huce.edu.web.repository.CartItemRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private SizeService sizeService;
    
    @Override
    public Cart findCartByUser(User user) {
    	if (user == null) {
            throw new IllegalArgumentException("User is not authenticated or logged in");
        }
    	if (user.getCart() == null) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            double totalPrice = calculateTotalPrice(cart);
            cart.setTotalPrice(totalPrice);
            double totalDiscountedPrice = calculateTotalDiscountedPrice(cart);
            cart.setTotalDiscountedPrice(totalDiscountedPrice);
            return cartRepository.save(cart);
        }
        return user.getCart();
        
    }
    @Override
    public Cart findCartByUserId(Long userId) {
        return cartRepository.findById(userId).get();
    }
    
    @Override
    public CartItem findCartItem(Cart cart, Long productId, Long sizeId){
    	return cart.getCartItems().stream()
    	        .filter(item -> item.getProduct().getId().equals(productId) && 
    	                        item.getSize().getId().equals(sizeId))
    	        .findFirst()
    	        .orElse(null);
    }
    @Override
    public Cart addItemToCart(User user, Product product, int quantity, Size size) {
        Cart cart = findCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
        }
        // Tìm CartItem với cùng sản phẩm và kích thước
        CartItem cartItem = findCartItem(cart, product.getId(), size.getId());

        if (cartItem == null) {
            // Tạo mới CartItem nếu không tồn tại
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity); 
            cartItem.setPrice(product.getPrice());
            cart.getCartItems().add(cartItem); // Thêm CartItem vào Cart
        } else {
            // Cập nhật số lượng nếu đã tồn tại
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cart.recalculateCart();
        double totalDiscountedPrice = calculateTotalDiscountedPrice(cart);
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        // Lưu giỏ hàng vào database
        return cartRepository.save(cart);
    }


    @Override
    public void removeItemFromCart(Long cartItemId) {
    	CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
    	Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);

        cart.recalculateCart();
        double totalDiscountedPrice = calculateTotalDiscountedPrice(cart);
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        // Save the cart and delete the item
        cartRepository.save(cart);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void updateCartItemSize(Long cartItemId, Long sizeId) {
    	CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem không tồn tại"));
        Size newSize = sizeService.findById(sizeId);
        if (newSize == null) {
            throw new IllegalArgumentException("Size không tồn tại");
        }
        cartItem.setSize(newSize);
        cartItemRepository.save(cartItem);
        // Tính lại totalPrice cho giỏ hàng
        Cart cart = cartItem.getCart(); // Lấy giỏ hàng từ CartItem
        double totalPrice = calculateTotalPrice(cart);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
    @Override
    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
    	CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem không tồn tại"));        
        // Kiểm tra số lượng hợp lệ
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }       
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        // Tính lại totalPrice cho giỏ hàng
        Cart cart = cartItem.getCart(); // Lấy giỏ hàng từ CartItem
        double totalPrice = calculateTotalPrice(cart);
        cart.setTotalPrice(totalPrice);
        double totalDiscountedPrice = calculateTotalDiscountedPrice(cart);
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cartRepository.save(cart);
    }
    @Override
    public double calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                   .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                   .sum();
    }
    @Override
    public double calculateTotalDiscountedPrice(Cart cart) {
        double totalDiscountedPrice = calculateTotalPrice(cart);
        
        if (totalDiscountedPrice > 300) {
            return totalDiscountedPrice; 
        }
        return totalDiscountedPrice + 35; 
    }
    @Override
    @Transactional
    public void clearCart(User user) {
    	Cart cart = cartRepository.findByUser(user);
        if (cart != null) {
            cart.getCartItems().clear(); 
            cart.setTotalDiscountedPrice(0);
            cartRepository.save(cart);  
        }
    }
	

}
