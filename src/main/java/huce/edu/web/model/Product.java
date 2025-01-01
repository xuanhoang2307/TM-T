package huce.edu.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "productName")
	private String productName;
	@Column(name = "price")
	private Double price;
	@Column(name = "image")
	private String image;
	@Column(name = "color")
	private String color ;
	
	@Column(name = "content", columnDefinition = "TEXT")
	@Lob
	private String content;
	
	@Column(name = "description", columnDefinition = "TEXT")
	@Lob
	private String description;
	
    @ManyToMany
    @JoinTable(
        name = "product_sizes",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private Set<Size> sizes;
	
	@OneToMany(mappedBy= "product",cascade= CascadeType.ALL,orphanRemoval = true)
	private List<Review> reviews= new ArrayList<>();
	
	private double averageRating;
	
	@ManyToOne()
	@JoinColumn(name = "category_id",referencedColumnName = "id")
	private Category category;

	private Boolean isActive = true;
	
}
