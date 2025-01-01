package huce.edu.web.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import huce.edu.web.model.OrderItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.Review;
import huce.edu.web.model.User;
import huce.edu.web.repository.OrderItemRepository;
import huce.edu.web.repository.OrderRepository;
import huce.edu.web.repository.ProductRepository;
import huce.edu.web.repository.ReviewRepository;

@Service
public class ReviewServiceImplement implements ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
    private OrderRepository orderRepository;
	
	@Autowired
    private ProductRepository productRepository;
	
	@Autowired
    private OrderItemRepository orderItemRepository;
	
	@Override
	public boolean canUserReviewProduct(User user, Product product) {
		boolean alreadyReviewed = reviewRepository.existsByUserAndProduct(user, product);

        boolean productDelivered = orderRepository.existsByUserAndProductAndStatus(user, product, "Đã nhận");

        return !alreadyReviewed && productDelivered;
	}

	@Override
	public Review addReview(User user, Product product, int rating, String comment, LocalDateTime creatAt) {
		if (!canUserReviewProduct(user, product)) {
            throw new IllegalStateException("You cannot review this product.");
        }
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(creatAt);
        reviewRepository.save(review);
        
        product.setAverageRating(calculateAverageRating(product));
        productRepository.save(product);
        List<OrderItem> orderItems = orderItemRepository.findByProductAndOrder_UserAndReviewedFalse(product, user);
        for (OrderItem orderItem : orderItems) {
            orderItem.setReviewed(true);
            orderItemRepository.save(orderItem);  // Cập nhật trạng thái reviewed thành true
        }
        return review;
    }
	
	@Override
	public List<Review> findReviewsByProduct(Product product) {
	    return reviewRepository.findByProduct(product);
	}
	@Override
    public double calculateAverageRating(Product product) {
        List<Review> reviews = reviewRepository.findByProduct(product);
        if (reviews.isEmpty()) {
            return 0.0; // Nếu không có đánh giá, trả về 0.
        }
        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }
}
