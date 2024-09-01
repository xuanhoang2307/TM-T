package huce.edu.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import huce.edu.web.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

}
