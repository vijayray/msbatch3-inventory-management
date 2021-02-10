package com.sl.ms.inventorymanagement.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sl.ms.inventorymanagement.model.Inventory;
import com.sl.ms.inventorymanagement.model.Product;
import com.sl.ms.inventorymanagement.repository.InventoryRepository;
import com.sl.ms.inventorymanagement.repository.ProductRepository;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
public class InventoryController {

	@Autowired
	ProductRepository productRepo;
	@Autowired
	InventoryRepository inventoryRepo;

	//Fetch list of product inventory
	@GetMapping("/products")
	public Iterable<Product> getProducts() {
		return productRepo.findAll();
	}

	//Fetch specific details of product by passing id
	@GetMapping("/products/{id}")
	public Product getbyId(@PathVariable long id) {
		return productRepo.findById(id).get();
	}

	//insert new inventory for specific product
	@PostMapping(value = "/products/{id}", consumes = "application/json", produces = "application/json")
	public Product saveById(@PathVariable long id, @RequestBody Product product) {
		product.setId(id);
		return productRepo.save(product);
	}

	//insert more than one product at a time
	@PostMapping(value = "/products", consumes = "application/json", produces = "application/json")
	public Iterable<Product> saveAll(@RequestBody Iterable<Product> product) {
		return productRepo.saveAll(product);
	}

	//Update specific product inventory
	@PutMapping(value = "/products/{id}", consumes = "application/json", produces = "application/json")
	public Product updateById(@PathVariable long id, @RequestBody Product product) {
		product.setId(id);
		return productRepo.save(product);
	}

	//delete specific product from inventory
	@DeleteMapping(value = "/products/{id}")
	public void deleteById(@PathVariable Long id) {

		Product product = productRepo.findById(id).get();
		product.setQuantity(0);
		productRepo.save(product);
	}

	//fetch unique list of product supported by system
	@GetMapping("/supported_products")
	public HashMap<Long, String> getSupportedProducts() {
		HashMap<Long, String> uniqueMap = new HashMap<Long, String>();

		productRepo.findAll().forEach(p -> {
			uniqueMap.put(p.getId(), p.getName());
		});
		return uniqueMap;
	}

	//Post data for inventory update as file
	@RequestMapping(value = "/products/file", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") Date date,
			@RequestParam("id") Long id) {

		Inventory inv;
		Set<Product> prodList = new HashSet<Product>();
		try {
			// JSON parser object to parse read file
			@SuppressWarnings("deprecation")
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(file.getInputStream());
			JSONArray productList = (JSONArray) obj;
			System.out.println(productList);

			productList.forEach(prd -> parseProductObject((JSONObject) prd, prodList));
			System.out.println("Data" + prodList);

			inv = new Inventory(id, date, productList);

		} catch (IOException | net.minidev.json.parser.ParseException e) {
			inv = new Inventory();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inventoryRepo.save(inv);

		return "Successfully inserted";
	}

	private void parseProductObject(JSONObject prd, Set<Product> prodList) {

		Product p = new Product(((Integer) prd.get("id")).longValue(), (String) prd.get("name"),
				((Integer) prd.get("price")).doubleValue(), (Integer) prd.get("quantity"));
		prodList.add(p);
		productRepo.save(p);
	}
}
