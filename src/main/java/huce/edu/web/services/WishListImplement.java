package huce.edu.web.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import huce.edu.web.model.Product;
import huce.edu.web.model.User;
import huce.edu.web.model.WishList;
import huce.edu.web.repository.WishListRepository;
@Service
public class WishListImplement implements WishListService{
	
	 @Autowired
	 private WishListRepository wishlistRepository;
	 
	 @Override
	 public boolean isProductInWishlist(User user, Product product) {
	     return wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId()) != null;
	 }

	@Override
	public void addToWishlist(User user, Product product) {
		// TODO Auto-generated method stub
		 WishList existingItem = wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId());
		 
		 if (existingItem == null) { // Nếu chưa có, thêm mới vào wishlist
		        WishList wishlist = new WishList();
		        wishlist.setUser(user); 
		        wishlist.setProduct(product); 
		        wishlistRepository.save(wishlist); 
		 } else {
		        System.out.println("Sản phẩm đã tồn tại trong wishlist"); 
		 }
	}

	@Override
	public Page<WishList> getWishlistByUser(User user, Pageable pageable) {
	    return wishlistRepository.findByUser(user, pageable);
	}
	
	@Override
    public void removeProductFromWishlist(Long wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }
	@Override
	public void removeFromWishlist(User user, Product product) {
	    WishList wishlist = wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId());
	    if (wishlist != null) {
	        wishlistRepository.delete(wishlist);
	    }
	}

}
