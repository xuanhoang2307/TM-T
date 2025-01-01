package huce.edu.web.services;

import huce.edu.web.model.Cart;
import huce.edu.web.model.CartItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.Size;
import huce.edu.web.model.User;

public interface CartService {
	Cart findCartByUser(User user);
	Cart findCartByUserId(Long cartId);
	CartItem findCartItem(Cart Cart, Long productId, Long sizeId);
    Cart addItemToCart(User user, Product product, int quantity, Size size);
    void removeItemFromCart(Long cartItemId);
    void updateCartItemSize(Long cartItemId, Long sizeId);
    void updateCartItemQuantity(Long cartItemId, Integer quantity);
    double calculateTotalPrice(Cart cart);
    double calculateTotalDiscountedPrice(Cart cart);
    void clearCart(User user);
}
