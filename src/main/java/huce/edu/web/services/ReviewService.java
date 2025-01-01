package huce.edu.web.services;

import java.time.LocalDateTime;
import java.util.List;

import huce.edu.web.model.Product;
import huce.edu.web.model.Review;
import huce.edu.web.model.User;

public interface ReviewService {
	boolean canUserReviewProduct(User user, Product product);
	Review addReview(User user, Product product, int rating, String comment, LocalDateTime creatAt);
	List<Review> findReviewsByProduct(Product product);
	double calculateAverageRating(Product product);
}
