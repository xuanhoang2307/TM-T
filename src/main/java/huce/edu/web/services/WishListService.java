package huce.edu.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import huce.edu.web.model.Product;
import huce.edu.web.model.User;
import huce.edu.web.model.WishList;

public interface WishListService {
	boolean isProductInWishlist(User user, Product product);
	void addToWishlist(User user, Product product);
	Page<WishList> getWishlistByUser(User user, Pageable pageable);
	void removeProductFromWishlist(Long wishlistId);
	void removeFromWishlist(User user, Product product);
}
