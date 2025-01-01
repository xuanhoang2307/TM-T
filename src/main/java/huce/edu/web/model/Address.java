package huce.edu.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "city")
	private String city;
	@Column(name = "district")  //quận-huyện
	private String district;
	@Column(name = "ward")	//xa-phuong
	private String ward;	
	@Column(name = "street_address")
	private String streetAddress;
	private String recipientName;
    private String phoneNumber;
    private boolean isDefault;
    private boolean isDeleted = false;
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;	
	
	public boolean isDefault() {
        return isDefault;
    }
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    public boolean isDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
}
